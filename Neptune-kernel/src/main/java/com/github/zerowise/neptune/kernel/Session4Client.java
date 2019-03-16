package com.github.zerowise.neptune.kernel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;

public class Session4Client extends Session4Server {

	private static final Logger logger = LoggerFactory.getLogger("Session4Client");

	private Bootstrap bootstrap;
	private String host;
	private int inetPort;

	public Session4Client(Bootstrap bootstrap, String host, int inetPort) {
		super();
		this.bootstrap = bootstrap;
		this.host = host;
		this.inetPort = inetPort;

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
			channel = bootstrap.connect(host, inetPort).sync().channel();
			logger.info("NeptuneConsumer connect {}:{} success", host, inetPort);
		} catch (InterruptedException e) {
			logger.error("NeptuneConsumer connect {}:{} failed", host, inetPort, e);
		}
		return isActive();
	}

}
