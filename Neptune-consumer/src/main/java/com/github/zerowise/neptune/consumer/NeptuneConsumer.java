package com.github.zerowise.neptune.consumer;

import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.HeartBeatService;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;

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
	private Channel channel;

	public void start(Consumer<ResponseMessage> consumer, String host, int inetPort) {
		start(new CodecFactory(ResponseMessage.class), consumer, host, inetPort);
	}

	public void start(CodecFactory codecFactory, Consumer<ResponseMessage> consumer, String host, int inetPort) {

		if (worker == null) {
			worker = new NioEventLoopGroup(1, new DefaultThreadFactory("CONSUMER"));
		}

		try {
			channel = new Bootstrap().group(worker).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							codecFactory.build(ch);
							ch.pipeline().addLast(new IdleStateHandler(0, 5, 0),
									new NeptuneConsumerHandler(newRunnable(), consumer));
						}
					}).option(ChannelOption.TCP_NODELAY, true).connect(host, inetPort).sync().channel();
			logger.info("NeptuneConsumer connect {}:{} success", host, inetPort);
		} catch (InterruptedException e) {
			logger.error("NeptuneConsumer connect {}:{} failed", host, inetPort, e);
		}
	}

	protected Runnable newRunnable() throws Exception {
		Method method = HeartBeatService.class.getMethod("heartBeat");
		RequestMessage msg = new RequestMessage(-1, method, null);
		return () -> send(msg);
	}

	public boolean isActive() {
		return channel != null && channel.isActive();
	}

	public ChannelFuture send(Object object) {
		if (!isActive()) {
			return null;
		}
		return channel.writeAndFlush(object);
	}

	public void shutdown() throws Exception {
		SocketAddress addr = null;
		if (isActive()) {
			addr = channel.remoteAddress();
			
			Method method = HeartBeatService.class.getMethod("logout");
			RequestMessage msg = new RequestMessage(-1, method, null);
			Optional.ofNullable(send(msg)).ifPresent(future -> {
				try {
					future.sync().channel().close();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}

		worker.shutdownGracefully();
		logger.info("NeptuneConsumer disconnect {} success shutdown!", addr);
	}
}
