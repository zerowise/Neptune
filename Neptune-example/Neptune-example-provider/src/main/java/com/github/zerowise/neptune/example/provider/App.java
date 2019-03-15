package com.github.zerowise.neptune.example.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.github.zerowise.neptune.example.api.HelloWorldService;
import com.github.zerowise.neptune.invoke.MethodInvoker;
import com.github.zerowise.neptune.invoke.MethodInvokerId;
import com.github.zerowise.neptune.invoke.RpcInvoker;
import com.github.zerowise.neptune.provider.NeptuneProvider;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Map<MethodInvokerId, MethodInvoker> m = new HashMap<>();
		HelloWorldService helloWorldService = new HelloWorldServiceImpl();

		Stream.of(HelloWorldService.class.getMethods())
				.forEach(method -> m.put(new MethodInvokerId(method.getDeclaringClass().getName(), method.getName(),
						method.getParameterTypes()), new MethodInvoker(method, helloWorldService)));

		RpcInvoker rpcInvoker = new RpcInvoker(m);
		NeptuneProvider provider = new NeptuneProvider();
		provider.start(rpcInvoker, 8899);
	}
}
