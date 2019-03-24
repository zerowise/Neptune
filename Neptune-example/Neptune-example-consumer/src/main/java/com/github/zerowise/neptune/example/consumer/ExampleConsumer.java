package com.github.zerowise.neptune.example.consumer;

import com.github.zerowise.neptune.consumer.NeptuneClusterConsumer;
import com.github.zerowise.neptune.consumer.NeptuneConsumer;
import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.example.api.HelloWorldService;
import com.github.zerowise.neptune.kernel.Session4Cluster;
import com.github.zerowise.neptune.proxy.JdkProxy;
import com.github.zerowise.neptune.proxy.SnowFlake;
import com.github.zerowise.neptune.zookeeper.ServiceDiscovery;

/**
 * Hello world!
 *
 */
public class ExampleConsumer {
	public static void main(String[] args) throws Throwable {


		EventBus eventBus = new EventBus();

		//加入zookeeper 发现
		ServiceDiscovery discovery = new ServiceDiscovery();
		discovery.regist(eventBus);

		JdkProxy proxy = new JdkProxy();

		NeptuneClusterConsumer clusterConsumer = new NeptuneClusterConsumer(proxy);

		clusterConsumer.regist(eventBus);



		SnowFlake snowFlake = new SnowFlake(2, 3);

		HelloWorldService helloWorldService = proxy.proxy(HelloWorldService.class, clusterConsumer,snowFlake);



		discovery.start();



//		helloWorldService.helloworld();
//
//
//
//		clusterConsumer.stop();
//		discovery.stop();
	}
}
