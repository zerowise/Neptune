package com.github.zerowise.neptune.kernel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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

	@Override
	public void stop() {
		sessions.values().forEach(Session::stop);
		sessions.clear();
	}

	public void update(Map<Long, InetSocketAddress> addresses) {
		List<Long> removes = new ArrayList<>();

		sessions.forEach((key,val)->{
			InetSocketAddress remove = addresses.remove(key);
			if(remove == null){
				removes.add(key);
			}
		});

		removes.forEach(id-> sessions.remove(id).stop());
	}
}
