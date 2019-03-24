package com.github.zerowise.neptune.consumer;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.kernel.CodecFactory;
import com.github.zerowise.neptune.kernel.ResponseMessage;
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

public class NeptuneConsumer extends RpcConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NeptuneConsumer.class);

    private EventLoopGroup worker;
    private SocketAddress addr;
    private Long id;

    public NeptuneConsumer(Consumer<ResponseMessage> consumer, Long id, SocketAddress addr) {
        this.consumer = Objects.requireNonNull(consumer);
        if (worker == null) {
            worker = new NioEventLoopGroup(1, new DefaultThreadFactory("CONSUMER"));
        }
        this.addr = Objects.requireNonNull(addr);
        this.id = id;
    }

    @Override
    public void start() throws Throwable {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                CodecFactory.build(ResponseMessage.class).andThen(channel -> ch.pipeline().addLast(new IdleStateHandler(0, 5, 0),
                        new NeptuneConsumerHandler(consumer))).accept(ch);
            }
        }).option(ChannelOption.TCP_NODELAY, true);
        session = new Session4Client(bootstrap, addr).bind(id);
    }

    @Override
    public void stop() throws Throwable {
        worker.shutdownGracefully();
        logger.info("NeptuneConsumer disconnect {} success shutdown!", addr);
    }
}
