package com.github.zerowise.neptune.provider;

import java.util.Objects;
import java.util.function.BiConsumer;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.Session;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NeptuneProviderHandler extends SimpleChannelInboundHandler<RequestMessage> {

	private final BiConsumer<Session, RequestMessage> consumer;

	public NeptuneProviderHandler(BiConsumer<Session, RequestMessage> consumer) {
		super();
		Objects.requireNonNull(consumer);
		this.consumer = consumer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
		consumer.accept(Session.getSession(ctx.channel()).get(), msg);
	}

}
