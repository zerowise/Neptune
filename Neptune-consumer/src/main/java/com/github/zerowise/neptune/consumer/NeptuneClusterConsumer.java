package com.github.zerowise.neptune.consumer;

import java.util.function.Consumer;

import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;
import com.github.zerowise.neptune.kernel.Session4Cluster;

public class NeptuneClusterConsumer implements RpcConsumer {

	private Session4Cluster session;

	private RpcConsumer rpcConsumer;

	public NeptuneClusterConsumer(RpcConsumer rpcConsumer) {
		session = new Session4Cluster();
		this.rpcConsumer = rpcConsumer;
	}

	@Override
	public Session start(Consumer<ResponseMessage> consumer, String host, int inetPort) {
		session.registerSession(rpcConsumer.start(consumer, host, inetPort));
		return session;
	}

	@Override
	public Session start(CodecFactory codecFactory, Consumer<ResponseMessage> consumer, String host, int inetPort) {
		session.registerSession(rpcConsumer.start(codecFactory, consumer, host, inetPort));
		return session;
	}

	@Override
	public void stop() throws Throwable {
		rpcConsumer.stop();
	}

	@Override
	public void regist(EventBus eventBus) {
		rpcConsumer.regist(eventBus);
	}

}
