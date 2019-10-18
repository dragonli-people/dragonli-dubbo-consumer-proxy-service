package org.dragonli.service.general.dubboconsumerservice;

import java.util.ArrayList;

import com.alibaba.dubbo.config.annotation.Service;
import org.dragonli.service.nettyservice.NettyService;
import org.dragonli.service.nettyservice.NettyServiceConfig;
import org.dragonli.service.nettyservice.websockethandle.INettySocketHandler;
import org.dragonli.service.nettyservice.websockethandle.NettyWebSocketCentral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;



import io.netty.channel.ChannelHandler;

@Service(interfaceClass = ConsumerService.class , register = true,timeout = 6000,retries=-1 ,delay=-1)
public class DubboConsumerService extends NettyService implements ConsumerService{

	public DubboConsumerService(@Value("${service.micro-service.dubbo-consumer-proxy-service.netty-port}") int nettyPort,@Autowired INettySocketHandler socketHandler) throws Exception {
		super();
		NettyServiceConfig config = new NettyServiceConfig(nettyPort,socketHandler );
		this.config = config;
		this.channelHandlers = new ArrayList<ChannelHandler>();
//		if( config.isUseWebSocketService() )
		channelHandlers.add(createNettyWebSocketCentral(config.getSocketHandler()));
		
		this.start();
	}
	
	

	@Override
	protected NettyWebSocketCentral createNettyWebSocketCentral(INettySocketHandler handler) throws Exception
	{
		return new DubboConsumerNettyWebSocketCentral(handler,this.config);
	}
	
}

