package com.github.zerowise.neptune.consumer;

import java.lang.reflect.Method;

import com.github.zerowise.neptune.kernel.HeartBeatService;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class NeptuneConsumerHandler extends SimpleChannelInboundHandler<ResponseMessage> {
	
	private final Runnable heartBeat;

	public NeptuneConsumerHandler(Runnable heartBeat) {
		super();
		this.heartBeat = heartBeat;
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
		
	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			// 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
			heartBeat.run();
		}
	}
}