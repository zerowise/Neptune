package com.github.zerowise.neptune.example.provider;

import java.util.stream.Stream;

import com.github.zerowise.neptune.example.api.HelloWorldService;
import com.github.zerowise.neptune.invoke.MethodInvoker;
import com.github.zerowise.neptune.invoke.RpcInvoker;
import com.github.zerowise.neptune.provider.NeptuneProvider;

/**
 * Hello world!
 *
 */
public class ExampleProvider {
	public static void main(String[] args) {
		HelloWorldService helloWorldService = new HelloWorldServiceImpl();
		RpcInvoker rpcInvoker = new RpcInvoker();
		Stream.of(HelloWorldService.class.getMethods())
				.forEach(method -> rpcInvoker.register(new MethodInvoker(method, helloWorldService)));

		
		NeptuneProvider provider = new NeptuneProvider();
		provider.start(rpcInvoker);
	}
}
