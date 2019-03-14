package com.github.zerowise.neptune;

import com.github.zerowise.neptune.consumer.NeptuneConsumer;
import com.github.zerowise.neptune.proxy.RpcProxy;
import com.github.zerowise.neptune.proxy.SnowFlake;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		NeptuneConsumer neptuneConsumer = new NeptuneConsumer();
		SnowFlake snowFlake = new SnowFlake(2, 3);
		RpcProxy proxy = new RpcProxy(neptuneConsumer, snowFlake);
		
		
		neptuneConsumer.start(proxy, "127.0.0.1", 8899);
		
		
		
	}
}
