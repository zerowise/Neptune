package com.github.zerowise.neptune.provider;

import com.github.zerowise.neptune.kernel.RequestMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NeptuneProviderHandler extends SimpleChannelInboundHandler<RequestMessage> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
		
	}

}
