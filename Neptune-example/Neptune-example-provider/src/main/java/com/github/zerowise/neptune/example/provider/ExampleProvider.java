package com.github.zerowise.neptune.example.provider;

import java.util.stream.Stream;

import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.example.api.HelloWorldService;
import com.github.zerowise.neptune.invoke.MethodInvoker;
import com.github.zerowise.neptune.invoke.RpcInvoker;
import com.github.zerowise.neptune.provider.NeptuneProvider;
import com.github.zerowise.neptune.zookeeper.ServiceRegistry;

/**
 * Hello world!
 *
 */
public class ExampleProvider {
	public static void main(String[] args) throws Throwable{
		HelloWorldService helloWorldService = new HelloWorldServiceImpl();
		RpcInvoker rpcInvoker = new RpcInvoker();
		Stream.of(HelloWorldService.class.getMethods())
				.forEach(method -> rpcInvoker.register(new MethodInvoker(method, helloWorldService)));


		EventBus eventBus = new EventBus();

		ServiceRegistry serviceRegistry = new ServiceRegistry();
		serviceRegistry.regist(eventBus);


		
		NeptuneProvider provider = new NeptuneProvider(rpcInvoker);

		provider.regist(eventBus);



		serviceRegistry.start();
	}
}
