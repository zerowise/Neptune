package com.github.zerowise.neptune.zookeeper;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.github.zerowise.neptune.Configs;
import com.github.zerowise.neptune.Service;
import com.github.zerowise.neptune.event.EventBus;

public abstract class ZookeeperService implements Watcher, Service {

	private CountDownLatch latch = new CountDownLatch(1);

	private String registryAddress;
	protected ZooKeeper zookeeper;
	
	protected EventBus eventBus;

	public ZookeeperService() {
		this.registryAddress = Configs.getString("zk.cluster.addrs");
		try {
			this.zookeeper = new ZooKeeper(registryAddress, Configs.getInt("zk.session.timeout", 5000), this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
			switch (event.getType()) {
			case None:
				if (event.getPath() == null) {
					latch.countDown();
				}
				break;
			case NodeChildrenChanged:
				subcribe();
				break;
			default:
				break;
			}
		}
	}


	@Override
	public void regist(EventBus eventBus) {
		this.eventBus = Objects.requireNonNull(eventBus);
	}

	protected abstract void subcribe();

	@Override
	public void stop() throws Throwable {
		if (zookeeper != null) {
			zookeeper.close();
		}

	}

}
