/**
 * 
 */
package org.dragonli.service.general.dubboconsumerservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.dragonli.service.nettyservice.websockethandle.INettySocketCentral;
import org.dragonli.service.nettyservice.websockethandle.INettySocketHandler;
import org.dragonli.tools.general.DataCachePool;
import org.dragonli.tools.general.ITelnetCommandHandler;
import org.dragonli.tools.general.JSONUtil;
import org.dragonli.tools.general.TelnetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author freeangel
 *
 */
@Component("socketHandler")
public class DubboConsumerSocketHandler implements INettySocketHandler {
	
	private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	public DubboConsumerSocketHandler(
			@Autowired ConsumerInvokerManager invoker,
			@Value("${service.micro-service.dubbo-consumer-proxy-service.telnet-port}") Integer telnetPort
	) {
		// TODO Auto-generated constructor stub
		this.invoker = invoker;
		if(telnetPort!=null)
			this.setTelnetPort(telnetPort);
	}
	
	@Autowired
	private ConsumerInvokerManager invoker;
	public ConsumerInvokerManager getInvoker() {
		return invoker;
	}

	public void setInvoker(ConsumerInvokerManager invoker) {
		this.invoker = invoker;
	}

	public static Logger logger = Logger.getLogger(DubboConsumerSocketHandler.class);
	private static final AtomicInteger connectCount = new AtomicInteger(0);
	

	private INettySocketCentral central;
	private final Map<Channel,Boolean> channels = new ConcurrentHashMap<>() ;
	
	private Integer telnetPort;
	public Integer getTelnetPort() {
		return telnetPort;
	}

	public void setTelnetPort(Integer telnetPort) {
		this.telnetPort = telnetPort;
		startTelnetCommand();
	}

	protected static boolean telnetStart = false;
	protected static boolean shutdown = false;
	protected static final Map<String, ITelnetCommandHandler> serviceTelnetCommandDic = new HashMap<>() ;

	
	public void startTelnetCommand()
	{
		if(telnetPort == null)
			return;
		
		if(telnetStart)
			return;
		
		telnetStart = true;
		
		serviceTelnetCommandDic.put("shutdown", (String[] args)-> {
			shutdown = true;
			for(Channel c:channels.keySet())
				sendAvailableInfo(c,false);
			return "shutdowning suceessed!please stop the server after a few seconds!!";
		});
		
		serviceTelnetCommandDic.put("sp", (String[] args)-> {
			ConsumerVars.setPausing(true);
			return "pause OK";
		});
		
		serviceTelnetCommandDic.put("sr",  (String[] args)-> {
			ConsumerVars.setPausing(false);
			return "resume OK";
		});
		
		serviceTelnetCommandDic.put("test", (String[] args)->{

			try
			{
				if( args.length < 1 )
					return "paras length err";
				String interfaceName = args[0];
				String group = args.length >= 2 ? args[1] : "";
//					JSONArray list = (JSONArray) JSON.parse(args[2]);
//				List<Object> list = JSONUtil.parseObject(args[2]);
				logger.info("cmd debug,interface name is :"+interfaceName+"||invoker is null?:"+(invoker==null));
				boolean flag = invoker.testInterface(interfaceName, group);
				return "test result:"+flag;
			}catch(Exception e){logger.error(e);}
			return "err!plz to see the log";
		});
		
		TelnetUtil.startTelnet(serviceTelnetCommandDic, telnetPort);
	}
	
	protected void sendAvailableInfo(Channel one,boolean f)
	{
		Map<String,Object> map = new HashMap<>();
		map.put("available", f);
		String res = JSONUtil.toJSONString(map);
		one.writeAndFlush(new TextWebSocketFrame(res));
	}
	
	@Override
	public void setCentral(INettySocketCentral central) {
		// TODO Auto-generated method stub
		this.central = central;
	}

	@Override
	public void notifyConnected(HttpRequest req,Channel one) throws Exception {
		// TODO Auto-generated method stub
		final boolean _shutdown = shutdown;
		cachedThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendAvailableInfo(one,!_shutdown);
			}
		});
		
		channels.put(one, true);
		connectCount.incrementAndGet();
	}


	@Override
	public void notifyClosed(Channel one) throws Exception {
		// TODO Auto-generated method stub
		connectCount.decrementAndGet();
		channels.remove(one);
	}
	
	@SuppressWarnings("serial")
	public void handleInvoker(Map<String,Object> paras,Channel one,long begin)
	{
		DubboConsumerThreadHandler dth = null;
		if(paras.get("heart")!=null)
		{
			one.writeAndFlush(new TextWebSocketFrame( JSONUtil.toJSONString(new HashMap<String,Object>(){{put("heart",true);}}) ));
			return ;
		}
		try {
			dth = DataCachePool.get(DubboConsumerThreadHandler.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DataCachePool.back(dth);
			logger.error(e);
			return;
		}
		try {
			dth.init(paras, invoker, one,begin);
			cachedThreadPool.execute(dth);//单独启动线程，执行调用
		}catch(Exception e) {
			logger.error(e);
			return;
		}
	}

	@Override
	public boolean send(Object msg, int moduleId, Map<String, Object> para) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean send(String uuid, String msg, int moduleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int sendToAll(String msg, int moduleId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close(int moduleId, Map<String, Object> para) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean valicodeSocket(HttpRequest req) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getConnectCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String receive(Channel channel, String msg) {
		// TODO Auto-generated method stub
		return null;
	}

}
