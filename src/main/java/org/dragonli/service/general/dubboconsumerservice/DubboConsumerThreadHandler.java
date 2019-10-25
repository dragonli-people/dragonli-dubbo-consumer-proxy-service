package org.dragonli.service.general.dubboconsumerservice;

import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.dragonli.tools.general.DataCachePool;
import org.dragonli.tools.general.IDataCachePool;

public class DubboConsumerThreadHandler implements Runnable, IDataCachePool {
    protected final static Logger logger = Logger.getLogger(DubboConsumerThreadHandler.class);
    private Map<String, Object> para;
    private ConsumerInvokerManager invoker;
    private Channel one;
    private long begin;

    public void init(Map<String, Object> para, ConsumerInvokerManager invoker, Channel one, long begin) {
        this.para = para;
        this.invoker = invoker;
        this.one = one;
        this.begin = begin;//System.currentTimeMillis();
    }

    private static final Random random = new Random();

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            Object interfaceName = para.get("interface");
            Object methodName = para.get("method");
            Object parameters = para.get("parameters");
            String group = (String) para.get("group");
            Integer requestId = (Integer) para.get("requestId");

            if (interfaceName == null || !(interfaceName instanceof String) || methodName == null ||
                    !(methodName instanceof String) || parameters == null || !(parameters instanceof JSONArray)) {
                DataCachePool.back(this);
                return;
            }

            String res = JSON.toJSONString(
                    DubboConsumerUtil.invoke(invoker, interfaceName.toString(), methodName.toString(),
                            (JSONArray) parameters, group));


            one.writeAndFlush(new TextWebSocketFrame(res));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e);
        }

        DataCachePool.back(this);
    }

    public void clear() {
        this.para = null;
        this.invoker = null;
        this.one = null;
    }
}
