package com.github.zerowise.neptune.consumer;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.HeartBeatService;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;
import com.github.zerowise.neptune.kernel.Session4Client;
import com.github.zerowise.neptune.proxy.RpcResult;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NeptuneConsumer {

	private static final Logger logger = LoggerFactory.getLogger("NeptuneConsumer");

	private EventLoopGroup worker;
	private Session session;
	private HeartBeatService heartBeat;

	public Session start(Consumer<ResponseMessage> consumer, String host, int inetPort, long id) {
		return start(new CodecFactory(ResponseMessage.class), consumer, host, inetPort, id);
	}

	public Session start(CodecFactory codecFactory, Consumer<ResponseMessage> consumer, String host, int inetPort,
			long id) {

		heartBeat = createService();

		if (worker == null) {
			worker = new NioEventLoopGroup(1, new DefaultThreadFactory("CONSUMER"));
		}
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				codecFactory.build().andThen(channel -> ch.pipeline().addLast(new IdleStateHandler(0, 5, 0),
						new NeptuneConsumerHandler(newRunnable(), consumer))).accept(ch);
			}
		}).option(ChannelOption.TCP_NODELAY, true);
		session = new Session4Client(bootstrap, host, inetPort).bind(id);
		return session;
	}

	protected Runnable newRunnable() {
		return () -> {
			if (heartBeat != null) {
				heartBeat.heartBeat();
			}
		};
	}

	public boolean isActive() {
		return session != null && session.isActive();
	}

	public ChannelFuture send(Object object) {
		return session.sendMessage(object);
	}

	protected HeartBeatService createService() {
		Class<?> infClazz = HeartBeatService.class;
		return (HeartBeatService) Proxy.newProxyInstance(infClazz.getClassLoader(), new Class[] { infClazz },
				(proxy, method, args) -> {
					session.sendMessage(new RequestMessage(-1, method, args));
					return null;
				});
	}

	public void shutdown() {
		SocketAddress addr = null;
		if (isActive()) {
			addr = session.remoteAddress();
			if(heartBeat != null) {
				heartBeat.logout();
			}
			session.unbind();
		}

		worker.shutdownGracefully();
		logger.info("NeptuneConsumer disconnect {} success shutdown!", addr);
	}
}
