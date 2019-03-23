package com.github.zerowise.neptune.consumer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;
import com.github.zerowise.neptune.kernel.Session4Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NeptuneConsumer implements RpcConsumer{

	private static final Logger logger = LoggerFactory.getLogger("NeptuneConsumer");

	private EventLoopGroup worker;
	private SocketAddress addr;
	private EventBus eventBus;

	@Override
	public Session start(Consumer<ResponseMessage> consumer, String host, int inetPort) {
		return start(new CodecFactory(ResponseMessage.class), consumer, host, inetPort);
	}

	@Override
	public Session start(CodecFactory codecFactory, Consumer<ResponseMessage> consumer, String host, int inetPort) {
		if (worker == null) {
			worker = new NioEventLoopGroup(1, new DefaultThreadFactory("CONSUMER"));
		}

		addr = new InetSocketAddress(host, inetPort);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				codecFactory.build().andThen(channel -> ch.pipeline().addLast(new IdleStateHandler(0, 5, 0),
						new NeptuneConsumerHandler(consumer))).accept(ch);
			}
		}).option(ChannelOption.TCP_NODELAY, true);
		return new Session4Client(bootstrap, addr);
	}

	public void stop() {
		worker.shutdownGracefully();
		logger.info("NeptuneConsumer disconnect {} success shutdown!", addr);
	}

	@Override
	public void regist(EventBus eventBus) {
		this.eventBus = eventBus;
	}
}
