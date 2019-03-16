package com.github.zerowise.neptune.kernel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelFuture;

public class Session4Cluster extends Session {

	private Map<Long, Session> sessions = new ConcurrentHashMap<>();

	public void registerSession(Session session) {
		sessions.put(session.id, session);
	}

	@Override
	public ChannelFuture sendMessage(Object message) {

		return sessions.values()
				.stream()
				.filter(Session::isActive)
				.findAny()
				.map(sess -> sess.sendMessage(message))
				.get();
	}

}
