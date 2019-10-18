package org.dragonli.service.general.dubboconsumerservice;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.dragonli.service.nettyservice.NettyServiceConfig;
import org.dragonli.service.nettyservice.websockethandle.INettySocketHandler;
import org.dragonli.service.nettyservice.websockethandle.NettyWebSocketCentral;

public class DubboConsumerNettyWebSocketCentral extends NettyWebSocketCentral {

	private DubboConsumerSocketHandler handler;
	public DubboConsumerNettyWebSocketCentral(INettySocketHandler handler) throws Exception {
		super(handler);
		this.handler = (DubboConsumerSocketHandler) handler;
		// TODO Auto-generated constructor stub
	}
	
	public DubboConsumerNettyWebSocketCentral(INettySocketHandler handler, NettyServiceConfig nettyServiceConfig) throws Exception
	{
		this(handler);
		this.nettyServiceConfig = nettyServiceConfig;
	}
	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		if (msg instanceof WebSocketFrame ) {
//			logger.info("socket channelRead :  msg:"+msg.toString());
			handleDubboConsumerWebSocketRequest(ctx, (WebSocketFrame) msg);
			return;
		}
		super.channelRead(ctx, msg);
	}

	private void handleDubboConsumerWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame req) {
		// TODO Auto-generated method stub
//		logger.info("socket handleWebSocket.......");
		if (req instanceof CloseWebSocketFrame) {
//			logger.info("socket handleWebSocket : Frame Type-->CloseWebSocketFrame");
			ctx.channel().close();
//			handshaker.close(ctx.channel(), (CloseWebSocketFrame) req);
			return;
		}
		if (req instanceof PongWebSocketFrame) {
//			logger.info("socket handleWebSocket : Frame Type-->PongWebSocketFrame");
			ctx.channel().write(new PongWebSocketFrame(req.content()));
			ctx.channel().closeFuture();
			return;
		}
		
		if (req instanceof PingWebSocketFrame) {
//			logger.info("socket handleWebSocket : Frame Type-->PingWebSocketFrame");
		   ctx.channel().writeAndFlush(new PongWebSocketFrame(req.content()));  
			return ;
		}

		if(req instanceof TextWebSocketFrame){
			long begin = System.currentTimeMillis();
//			logger.info("socket handleWebSocket : Frame Type-->TextWebSocketFrame:" + ((TextWebSocketFrame) req).text());
			Map<String,Object> paras = JSON.parseObject(((TextWebSocketFrame) req).text());
			this.handler.handleInvoker(paras,ctx.channel(),begin);
//			ctx.channel().writeAndFlush(new TextWebSocketFrame(((TextWebSocketFrame) req).text()));
			return;
			
		}
	}
}
