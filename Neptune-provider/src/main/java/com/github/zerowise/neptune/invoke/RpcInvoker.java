package com.github.zerowise.neptune.invoke;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.github.zerowise.neptune.kernel.RequestMessage;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session;

import io.netty.util.concurrent.DefaultThreadFactory;

public class RpcInvoker implements BiConsumer<Session, RequestMessage> {
	private ExecutorService executorService;

	private Map<MethodInvokerId, MethodInvoker> methodInvokers;

	public RpcInvoker() {
		this(Executors.newCachedThreadPool(new DefaultThreadFactory("RpcServiceInvoker")));
	}

	public RpcInvoker(ExecutorService executorService) {
		this.executorService = executorService;
		this.methodInvokers = new HashMap<>();
	}

	public void register(MethodInvoker methodInvoker) {
		this.methodInvokers.putIfAbsent(methodInvoker.getKey(), methodInvoker);
	}

	@Override
	public void accept(Session session, RequestMessage request) {
		executorService.execute(() -> {
			if (request.getId() < 0) {
				// 心跳
				return;
			}
			MethodInvoker methodInvoker = methodInvokers.get(new MethodInvokerId(request.getServiceName(),
					request.getMethodName(), request.getParameterTypes()));
			ResponseMessage response = new ResponseMessage();
			response.setId(request.getId());
			if (methodInvoker == null) {
				response.setErrormsg(String.format("not exist this rpc method!!%s.%s", request.getServiceName(),
						request.getMethodName()));
			} else {
				Object result = methodInvoker.invoke(request.getArgments());
				response.setResult(result);
			}
			session.sendMessage(response);
		});
	}

	public void shutdown() {
		executorService.shutdown();
	}

}
