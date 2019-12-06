package org.dragonli.service.general.dubboconsumerservice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.fastjson.JSON;
import org.dragonli.service.dubbosupport.DubboBeanManager;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author dev
 */
public class ConsumerInvoker {
    private final AtomicBoolean hadInit = new AtomicBoolean(false);
    private volatile boolean initFlag = false;
    private final DubboBeanManager beanManager = new DubboBeanManager();
    private final Map<String, List<Method>> methodDic = new HashMap<>();
    private String host_port;
    @Value("${DEBUG_LOG:false}")
    private boolean debugLog;
    protected final static Logger logger = Logger.getLogger(ConsumerInvoker.class);

    public boolean init(String name, ApplicationConfig application, RegistryConfig registry, String group) {
//		if(hadInit.get())
//			return initFlag;
//		if()
        String[] array = name.split("\\|");
        name = array[0];
        host_port = array.length > 1 ? array[1] : null;
        Class<?> cls = null;
        synchronized (hadInit) {
            if (hadInit.get()) {
                logger.info("step -1");
                return initFlag;
            }

            try {
                cls = Class.forName(name);
//				logger.info("step -2.0 cls"+cls+"|tostring:"+cls.toString());
                for (Method m : cls.getMethods()) {
//					logger.info("step -2.1");
                    List<Method> list = !methodDic.containsKey(m.getName()) ? new ArrayList<Method>() : methodDic.get(
                            m.getName());
//					logger.info("step -2.1.2");
                    list.add(m);
//					logger.info("step -2.1.3"+methodDic.containsKey(m.getName() ));
                    if (!methodDic.containsKey(m.getName())) methodDic.put(m.getName(), list);
                }
            } catch (Exception ee) {
                hadInit.set(true);
                initFlag = false;
                logger.info("class not found:" + name);
                logger.error("step-2", ee);
                return false;
            }
        }

        boolean flag = beanManager.init(cls, name, application, registry, host_port, group);
        if (!flag) {
            hadInit.set(true);
            initFlag = false;
            logger.info("step -3");
            return false;
        }

        initFlag = true;
        hadInit.set(true);

        return initFlag;
    }

    public Map<String, Object> invoke(String mName, Object[] paras) {
        //must had init success
        logger.info("debug log flag is :" + debugLog);
        List<Method> mList = methodDic.get(mName);
        if (mList == null || mList.size() == 0) {
            //return
            return ConsumerInvokerManager.createErrMsg(ConsumerInvokerManager.ERR_NO_SUCH_METHOD);
        }

        Method mmm = null;
        for (Method mm : mList) {
            boolean f = false;
            if (debugLog)logger.info("paras==null:"+(paras==null)+";mm==null:"+(mm==null)+";mm.getParameterTypes()==null:"+(mm.getParameterTypes()==null));
            if (debugLog) logger.info("paras::" + "||paras.length:" + paras.length + "mm.getParameterTypes().length::" +
                    mm.getParameterTypes().length + "||mm.getName()::" + mm.getName() + "||mName::" + mName);
            if (paras.length == mm.getParameterTypes().length && mm.getName().equals(mName)) {
                f = true;
                for (int j = 0; j < paras.length; j++) {
                    boolean valicode = valicodeClassType(mm.getParameterTypes()[j], paras[j]);
                    if (debugLog) logger.info(
                            "valicodeClassType(mm.getParameterTypes()[" + j + "],paras[" + j + "])::" + valicode +
                                    "|||mm.getParameterTypes()[" + j + "]::" +
                                    JSON.toJSONString(mm.getParameterTypes()[j]) + "||paras[" + j + "]::" + paras[j]);
                    if (!valicode) {
                        f = false;
                        break;
                    }
                }
            }
            if (f) {
                mmm = mm;
                break;
            }
        }
        if (debugLog) logger.info(mmm.getName() + "{} find method:{}" + (mmm != null));
        if (mmm == null) {
            //return
            return ConsumerInvokerManager.createErrMsg(ConsumerInvokerManager.ERR_PARAMETERS_TYPE_WRONG);
        }

        Object result = null;
        try {
            if (debugLog) logger.info("step -1 paras.length" + paras.length);
            for (int i = 0; i < paras.length; i++) {
                if (debugLog) logger.info("i:" + i + "|paras[i]:" + paras[i].toString());
                paras[i] = this.castValueForInvoke(mmm.getParameterTypes()[i], paras[i]);
            }
            if (debugLog) {
                logger.info("step -2 paras.length" + paras.length);
                logger.info("exec invoke:beanManager.getBean():" + beanManager.getBean().getClass());
            }
            result = mmm.invoke(beanManager.getBean(), paras);
        } catch (Exception e) {

            String ps = mName + " ";

            ps = "  paras : ";
            for (int i = 0; i < paras.length; i++) {
                ps += "index-" + i + ":" + JSON.toJSONString(paras[i]);
            }

            logger.error(ps, e);
            if (e instanceof InvocationTargetException) {
                return ConsumerInvokerManager.createErrMsg(
                        ((InvocationTargetException) e).getTargetException().getMessage());
            }
            return ConsumerInvokerManager.createErrMsg(e.getMessage());
        }

        return ConsumerInvokerManager.createResultMsg(result);
    }

    private boolean ifNumberClass(Class<?> cls) {
        return Number.class.isAssignableFrom(cls) || cls == int.class || cls == long.class || cls == short.class ||
                cls == byte.class || cls == float.class || cls == double.class;
    }

    private boolean valicodeClassType(Class<?> cls, Object value) {
//		Field f = cls.getDeclaredField("TYPE");
        if (value == null) return cls.isInstance(value);

        if (ifNumberClass(cls) && value instanceof Number) return true;//all is number

        return cls.isInstance(value);
    }

    private Object castValueForInvoke(Class<?> cls, Object value) {
        if (value == null || !(value instanceof Number) || !ifNumberClass(cls)) return value;

        if (cls == int.class || cls == AtomicInteger.class || cls == Integer.class) return ((Number) value).intValue();
        if (cls == long.class || cls == AtomicLong.class || cls == Long.class) return ((Number) value).longValue();
        if (cls == float.class || cls == Float.class) return ((Number) value).floatValue();
        if (cls == double.class || cls == Double.class) return ((Number) value).doubleValue();
        if (cls == Byte.class || cls == byte.class) return ((Number) value).byteValue();
        if (cls == Short.class || cls == short.class) return ((Number) value).shortValue();
        if (cls == BigInteger.class) return new BigInteger(value.toString());
        if (cls == BigDecimal.class) return new BigDecimal(value.toString());

        return value;
    }
}
