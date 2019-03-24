package com.github.zerowise.neptune.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.github.zerowise.neptune.consumer.RpcConsumer;
import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;

public class JdkProxy implements Consumer<ResponseMessage> {

    private Map<Long, RpcResult> rpcResults = new ConcurrentHashMap<>();

    public <T> T proxy(Class<T> infClazz, Consumer<RequestMessage> consumer, SnowFlake snowFlake) {
        return (T) Proxy.newProxyInstance(infClazz.getClassLoader(), new Class[]{infClazz},
                (proxy, method, args) -> {
                    RequestMessage requestMessage = new RequestMessage(snowFlake.nextId(), method, args);
                    RpcResult result = new RpcResult();
                    rpcResults.put(requestMessage.getId(), result);
                    consumer.accept(requestMessage);
                    return result.getResult();
                });
    }

    @Override
    public void accept(ResponseMessage t) {
        RpcResult rpcResult = rpcResults.remove(t.getId());
        if (rpcResult != null) {
            rpcResult.onResult(t.getResult(), t.getErrormsg());
        }
    }
}
