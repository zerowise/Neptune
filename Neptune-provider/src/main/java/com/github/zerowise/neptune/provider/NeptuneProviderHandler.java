package com.github.zerowise.neptune.provider;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.Session;
import com.github.zerowise.neptune.kernel.Session4Server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NeptuneProviderHandler extends SimpleChannelInboundHandler<RequestMessage> {
	
	private static final Logger logger = LoggerFactory.getLogger("NeptuneProviderHandler");
	private final BiConsumer<Session, RequestMessage> consumer;

	public NeptuneProviderHandler(BiConsumer<Session, RequestMessage> consumer) {
		super();
		this.consumer = Objects.requireNonNull(consumer);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		new Session4Server().bind(ctx.channel());
		logger.info("NeptuneProvider channelInactive: {}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SocketAddress addr = ctx.channel().remoteAddress();
		Session.getSession(ctx.channel()).ifPresent(session -> session.unbind());
		logger.info("NeptuneProvider channelInactive: {}", addr);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
		consumer.accept(Session.getSession(ctx.channel()).get(), msg);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				ctx.close();
			}
		}
	}

}
