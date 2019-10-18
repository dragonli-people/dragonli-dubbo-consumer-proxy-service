package org.dragonli.service.general.dubboconsumerservice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

public class InvokeStatisticsManager {
	protected final static Logger logger = Logger.getLogger(InvokeStatisticsManager.class);
	private final static ConcurrentMap<String, ConcurrentMap<String,Object>> statisticsMap0 = new ConcurrentHashMap<>();
	private final static ConcurrentMap<String, ConcurrentMap<String,Object>> statisticsMap1 = new ConcurrentHashMap<>();
	public static void times (String methodName,Object [] paras,long cost,int type){

		ConcurrentMap<String, ConcurrentMap<String,Object>> statisticsMap = type == 0 ? statisticsMap0 : statisticsMap1;
		try{
			ConcurrentMap<String,Object> statisticsData;
			if(statisticsMap.containsKey(methodName)) statisticsData = statisticsMap.get(methodName);
			else{
				statisticsData =  new ConcurrentHashMap<>();
				statisticsMap.put(methodName,statisticsData);
			}
			//第一次执行时间
			if(!statisticsData.containsKey("firstInvokeTime"))
				statisticsData.put("firstInvokeTime", System.currentTimeMillis()+"");
			
			
			//执行次数
			if(!statisticsData.containsKey("invokeTime"))
				statisticsData.put("invokeTime", 1L);
			else{
				statisticsData.put("invokeTime", (long)statisticsData.get("invokeTime")+1L);
			}
			
			//总耗时
			if(!statisticsData.containsKey("invokeCost"))
				statisticsData.put("invokeCost", cost);
			else{
				statisticsData.put("invokeCost", (long)statisticsData.get("invokeCost")+cost);
			}
			//最短耗时
			if(!statisticsData.containsKey("invokeMaxCost"))
				statisticsData.put("invokeMaxCost", cost);
			else{
				statisticsData.put("invokeMaxCost", Math.max((long)statisticsData.get("invokeMaxCost"), cost));
			}
			//最长耗时
			if(!statisticsData.containsKey("invokeMinCost"))
				statisticsData.put("invokeMinCost", cost);
			else{
				statisticsData.put("invokeMinCost", Math.min((long)statisticsData.get("invokeMinCost"), cost));
			}
			
			String ps = "";
//			if(cost >1000L){
//				ps = "  paras : ";
//				for (int i = 0; i < paras.length; i++) {
//					ps += "index-"+i+":" +JSONUtil.toJSONString(paras[i]);
//				}
//				
//			}
			logger.info("requestTyep :"+(type == 0 ? "HTTP ":"SOCKET ")+methodName+""+ " cost:"+cost+" ms"+" ms AVG:"+((long)statisticsData.get("invokeCost")/(long)statisticsData.get("invokeTime")) + " times:"+ statisticsData.get("invokeTime")+ "  max:"+statisticsData.get("invokeMaxCost")+" ms   min:"+statisticsData.get("invokeMinCost")+ps );
		}catch (Exception e) {
			logger.error(".......", e);
		}
	}
	
	
	
}
