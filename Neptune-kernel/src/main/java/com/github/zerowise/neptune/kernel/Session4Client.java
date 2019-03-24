package com.github.zerowise.neptune.kernel;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;

public class Session4Client extends Session4Server {

	private static final Logger logger = LoggerFactory.getLogger("Session4Client");

	private Bootstrap bootstrap;
	private SocketAddress addr;

	public Session4Client(Bootstrap bootstrap, SocketAddress addr) {
		super();
		this.bootstrap = bootstrap;
		this.addr = addr;
		reConnect();
	}

	@Override
	public boolean reConnect() {
		if (bootstrap.config().group().isShutdown()) {
			return false;
		}

		if (isActive()) {
			return true;
		}

		try {
			bind(channel = bootstrap.connect(addr).sync().channel());
			logger.info("NeptuneConsumer connect {} success", addr);
		} catch (InterruptedException e) {
			logger.error("NeptuneConsumer connect {} failed", addr, e);
		}
		return isActive();
	}

	@Override
	public void stop() {
		if (bootstrap.config().group().isShutdown()) {
			return;
		}

		bootstrap.config().group().shutdownGracefully();

	}
}
