package com.github.zerowise.neptune.example.consumer;

import com.github.zerowise.neptune.consumer.NeptuneConsumer;
import com.github.zerowise.neptune.example.api.HelloWorldService;
import com.github.zerowise.neptune.kernel.Session4Cluster;
import com.github.zerowise.neptune.proxy.JdkProxy;
import com.github.zerowise.neptune.proxy.SnowFlake;

/**
 * Hello world!
 *
 */
public class ExampleConsumer {
	public static void main(String[] args) throws Exception {

		SnowFlake snowFlake = new SnowFlake(2, 3);
		Session4Cluster session = new Session4Cluster();

		JdkProxy proxy = new JdkProxy(session, snowFlake);

		NeptuneConsumer neptuneConsumer = new NeptuneConsumer();

		session.registerSession(neptuneConsumer.start(proxy, "127.0.0.1", 8899, snowFlake.currId()));

		HelloWorldService helloWorldService = proxy.proxy(HelloWorldService.class);
		helloWorldService.helloworld();

		neptuneConsumer.shutdown();
	}
}
