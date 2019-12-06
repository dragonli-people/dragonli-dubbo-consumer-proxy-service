/**
 *
 */
package org.dragonli.service.general.dubboconsumerservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dev
 *
 */
@Component
public class ConsumerInvokerManager {
    private final ConcurrentMap<String, ConcurrentMap<String, ConsumerInvoker>> invokers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AtomicInteger> errs = new ConcurrentHashMap<>();
    public static Logger logger = Logger.getLogger(ConsumerInvokerManager.class);
    @Value("${DEBUG_LOG:false}")
    private Boolean debugLog;
    private ApplicationConfig application;
    private RegistryConfig registry;
    protected static final String ERR_NO_SUCH_INTERFACE = "no such interface";
    protected static final String ERR_NO_SUCH_METHOD = "no such method";
    protected static final String ERR_PARAMETERS_TYPE_WRONG = "parameter`s lemgth is wrong,or one or more parameter`s" +
			" type is wrong";

    public ConsumerInvokerManager(ApplicationConfig application, RegistryConfig registry) {
        this.application = application;
        this.registry = registry;
    }

    public Map<String, Object> invoker(String interfaceName, String methodName, Object[] paras, String group) {

        if (debugLog) logger.info(
                "invoker-args:|interfaceName:" + interfaceName + "|methodName:" + methodName + "|paras:" +
                        paras.toString() + "|group:" + group);

        if (group == null) group = "";
        if (errs.containsKey(interfaceName) && errs.get(interfaceName).get() > 20) {
            logger.info("重试次数过多-ERR_NO_SUCH_INTERFACE");
            return createErrMsg(ERR_NO_SUCH_INTERFACE);
        }

        ConsumerInvoker invoker = findInvoker(interfaceName, group);

        if (!invoker.init(interfaceName, application, registry, group)) {
            errs.putIfAbsent(interfaceName, new AtomicInteger(0));
            logger.info("初始化-ERR_NO_SUCH_INTERFACE");
            return createErrMsg(ERR_NO_SUCH_INTERFACE);
        }
        return invoker.invoke(methodName, paras);
    }

    public ConsumerInvoker findInvoker(String interfaceName, String group) {
        ConsumerInvoker invoker = null;
        ConcurrentMap<String, ConsumerInvoker> groupInvokers = null;
        if ((groupInvokers = invokers.get(group)) == null) {
            groupInvokers = new ConcurrentHashMap<String, ConsumerInvoker>();
            invokers.putIfAbsent(group, groupInvokers);
            groupInvokers = invokers.get(group);
        }
        if ((invoker = groupInvokers.get(interfaceName)) == null) {
            invoker = new ConsumerInvoker();
            groupInvokers.putIfAbsent(interfaceName, invoker);
            invoker = groupInvokers.get(interfaceName);
        }

        return invoker;
    }

    public boolean testInterface(String interfaceName, String group) {
        ConsumerInvoker invoker = findInvoker(interfaceName, group);
        return invoker != null && invoker.init(interfaceName, application, registry, group);
    }

    public static Map<String, Object> createErrMsg(String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("status", false);
        msg.put("errMsg", text);
        return msg;
    }

    public static Map<String, Object> createResultMsg(Object o) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("status", true);
        msg.put("result", o);
        return msg;
    }
}
