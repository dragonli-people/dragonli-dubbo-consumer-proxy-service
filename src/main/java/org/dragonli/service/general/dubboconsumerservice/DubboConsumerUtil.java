package org.dragonli.service.general.dubboconsumerservice;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;

public class DubboConsumerUtil {
    public static Map<String,Object> invoke(ConsumerInvokerManager invoker,String interfaceName,String methodName,JSONArray parameters,String group) throws Exception{

        Object[] paras = new Object[((JSONArray)parameters).size()];
        for(int i = 0 ; i< paras.length;i++)
            paras[i] = ((JSONArray)parameters).get(i);
//			long t  = System.currentTimeMillis();

        boolean f = 1 > 100;
        long now = System.currentTimeMillis();
//        String invokeServiceId = (String) para.get("invokeServiceId");
//        String httpId = (String) para.get("httpId");
//        Integer expectTime =(Integer) para.get("expectTime");
//        Integer recordTime = (Integer) para.get("recordTime");
//        Integer serviceMinExpectTime = para.containsKey("serviceMinExpectTime") ? (Integer) para.get("serviceMinExpectTime") : 50;
//        if( requestId != null && httpId != null && expectTime != null && recordTime != null )
//        {
//            if( invokeServiceId == null )
//                invokeServiceId = now + "-" + random.nextInt(1000000);
//            String remark = (String)para.get("invokeRemark");
            //2018-11-09 lruifan 说没用了
//				if(remark != null  && DubboVars.getServiceInvokeLogger()!=null)
//				{
//					Map<String,Object> more = new HashMap<>();
//					more.put("hId", httpId);
//					more.put("bId", invokeServiceId);
//					more.put("remark", remark);
//					more.put("url", para.get("url"));
//					more.put("recordTime", recordTime);
////					logger.info("==remark===:"+JSONUtil.toJSONString(more));
//					DubboVars.getServiceInvokeLogger().invokeRemarkLog(more);
//				}
//				String invokeId = invokeServiceId;//random.nextInt(1000000) + "";//模拟调用服务的ID
            //hid:httpId, consumer id,pId:parent service id(1 will be null when first),id:myself id
//				DubboVars.invokeParasOnThread.put(Thread.currentThread(), httpId+","+invokeServiceId+",,"+expectTime+","+serviceMinExpectTime);
//        }
        //将来如果再启动http应答，则此处需要提纯
//			String key = System.currentTimeMillis()+"-" + random.nextInt(1000000);//模拟comsumer生成总随机数
//			String invokeId = random.nextInt(1000000) + "";//模拟调用服务的ID
//			String requestId = random.nextInt(1000000) + "";
//			DubboVars.invokeParasOnThread.put(Thread.currentThread(), key+","+invokeId+","+requestId);//
//			String v = configsService.getServiceVersion();
//			DubboVars.invokeParasOnThread.remove(Thread.currentThread());

        Object result = invoker.invoker((String)interfaceName, (String)methodName, (Object [])paras,group);
        //2018-11-09 lruifan 说没用了
//			DubboVars.invokeParasOnThread.remove(Thread.currentThread());
//        long cost  = System.currentTimeMillis() - begin;

//			long t2 = System.currentTimeMillis();
//			logger.info("========cost====|"+interfaceName+"."+methodName+"|"+(t2-t)+"|"+t2+"|"+t);
//			InvokeStatisticsManager.times(interfaceName+"."+methodName,(Object [])paras, System.currentTimeMillis() - this.begin,1);
        String res = null;

        if( result instanceof Map)
        {
//            ((Map<String,Object>) result).put("requestId", requestId==null?0:requestId);
//            ((Map<String,Object>) result).put("cost", cost);
//            ((Map<String,Object>) result).put("invokeServiceId", invokeServiceId);

            return (Map<String,Object>)result;
        }
        else
            throw new Exception("not support result type!");
    }
}
