package com.github.zerowise.neptune.provider;

import java.util.Objects;
import java.util.function.BiConsumer;

import com.github.zerowise.neptune.Service;
import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.event.EventListener;
import com.github.zerowise.neptune.zookeeper.ZkRegisterNodeEvent;
import com.github.zerowise.neptune.zookeeper.ZkRegisterStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.Configs;
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

public class NeptuneProvider implements Service {
    private static final Logger logger = LoggerFactory.getLogger("NeptuneProvider");
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private EventBus eventBus;

    private BiConsumer<Session, RequestMessage> consumer;

    public NeptuneProvider(BiConsumer<Session, RequestMessage> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
        if (boss == null) {
            boss = new NioEventLoopGroup(1, new DefaultThreadFactory("PROVIDER-BOSS"));
        }
        if (worker == null) {
            worker = new NioEventLoopGroup(0, new DefaultThreadFactory("PROVIDER-WORK"));
        }
    }

    @Override
    public void regist(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.regist(this, new EventListener<ZkRegisterStartedEvent>() {
            @Override
            public void onEvent(ZkRegisterStartedEvent event) {
                try {
                    start();
                } catch (Throwable throwable) {
                    logger.info("NeptuneProvider failed listen {}",
                            Configs.getString("rpc.provider.node"));
                    try {
                        stop();
                    } catch (Throwable throwable1) {

                    }
                }
            }
        });
    }

    public void start() throws Throwable {

        String node = Configs.getString("rpc.provider.node");
        String[] arr = node.split(":");

        int inetPort = Integer.parseInt(arr[2]);
        new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        CodecFactory.build(RequestMessage.class).andThen(channel -> {
                            channel.pipeline().addLast(new IdleStateHandler(6, 0, 0),
                                    new NeptuneProviderHandler(consumer));
                        }).accept(ch);

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

        eventBus.notify(new ZkRegisterNodeEvent(node));
        logger.info("NeptuneProvider start listen {}", inetPort);
    }

    public void stop() throws Throwable {
        worker.shutdownGracefully();
        boss.shutdownGracefully();

        eventBus.unregist(this);
    }
}
