package com.github.zerowise.neptune.consumer;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

import com.github.zerowise.neptune.kernel.HeartBeatService;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;

import com.github.zerowise.neptune.kernel.Session4Client;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class NeptuneConsumerHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    private HeartBeatService heartBeat;
    private final Consumer<ResponseMessage> consumer;

    public NeptuneConsumerHandler(Consumer<ResponseMessage> consumer) {
        super();
        this.consumer = consumer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        heartBeat = createService(HeartBeatService.class, ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        consumer.accept(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && heartBeat != null) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            heartBeat.heartBeat();
        }
    }

    protected <T> T createService(Class<T> infClazz, Channel channel) {
        return (T) Proxy.newProxyInstance(infClazz.getClassLoader(), new Class[]{infClazz},
                (proxy, method, args) -> {
                    channel.writeAndFlush(new RequestMessage(-1, method, args));
                    return null;
                });
    }
}