package com.github.zerowise.neptune.consumer;

import com.github.zerowise.neptune.Service;
import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;

import java.util.function.Consumer;

public abstract class RpcConsumer implements Service, Consumer<RequestMessage> {

    protected EventBus eventBus;
    protected Session session;
    protected Consumer<ResponseMessage> consumer;

    @Override
    public void regist(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void accept(RequestMessage requestMessage) {
        session.sendMessage(requestMessage);
    }
}
