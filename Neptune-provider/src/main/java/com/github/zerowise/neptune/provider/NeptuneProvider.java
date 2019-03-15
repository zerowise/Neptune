package com.github.zerowise.neptune.provider;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.Session;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NeptuneProvider {
	private static final Logger logger = LoggerFactory.getLogger("NeptuneProvider");
	private EventLoopGroup boss;
	private EventLoopGroup worker;

	public void start(BiConsumer<Session, RequestMessage> consumer, int inetPort) {
		start(new CodecFactory(RequestMessage.class), consumer, inetPort);
	}

	public void start(CodecFactory codecFactory, BiConsumer<Session, RequestMessage> consumer, int inetPort) {
		if (boss == null) {
			boss = new NioEventLoopGroup(1, new DefaultThreadFactory("PROVIDER-BOSS"));
		}

		if (worker == null) {
			worker = new NioEventLoopGroup(0, new DefaultThreadFactory("PROVIDER-WORK"));
		}

		try {
			new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							codecFactory.build(ch);
							ch.pipeline().addLast(new IdleStateHandler(6, 0, 0), new NeptuneProviderHandler(consumer));
						}
					}).childOption(ChannelOption.SO_KEEPALIVE, true)// 开启时系统会在连接空闲一定时间后向客户端发送请求确认连接是否有效
					.childOption(ChannelOption.TCP_NODELAY, true)// 关闭Nagle算法
					.childOption(ChannelOption.SO_SNDBUF, 4096)// 系统sockets发送数据buff的大小
					.childOption(ChannelOption.SO_RCVBUF, 2048)// ---接收
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// 使用bytebuf池, 默认不使用
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)// 使用bytebuf池,//
																										// 默认不使用
					.option(ChannelOption.SO_REUSEADDR, true)// 端口重用,如果开启则在上一个进程未关闭情况下也能正常启动
					.option(ChannelOption.SO_BACKLOG, 64)// 最大等待连接的connection数量
					.bind(inetPort).sync();
			logger.info("NeptuneProvider start listen {}",  inetPort);
		} catch (InterruptedException e) {
			shutdown();
			logger.info("NeptuneProvider failed listen {}",inetPort);
		}
	}

	public void shutdown() {
		worker.shutdownGracefully();
		boss.shutdownGracefully();
	}
}
