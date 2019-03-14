package com.github.zerowise.neptune.kernel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

public abstract class Session {

	private static final AttributeKey<Session> SESSION_KEY = AttributeKey.newInstance("SESSION");
	
	private static final Logger logger = LoggerFactory.getLogger("Session");

	protected Channel channel;

	public static Optional<Session> getSession(Channel channel) {
		return Optional.ofNullable(channel.attr(SESSION_KEY).get());
	}

	public void bind(Channel channel) {
		this.channel = channel;
		channel.attr(SESSION_KEY).set(this);
	}

	public void unbind() {
		if (this.channel == null) {
			return;
		}
		this.channel.attr(SESSION_KEY).set(null);
		this.channel.close();
		this.channel = null;
	}

	public boolean reConnect() {
		throw new RuntimeException("该类型的session不能重连！！");
	}

	public boolean isActive() {
		return channel != null && channel.isActive();
	}

	public ChannelFuture sendMessage(Object message) {
		if (!isActive()) {
			logger.info("session not active!!!");
			return null;
		}
		return this.channel.writeAndFlush(message);
	}
}
