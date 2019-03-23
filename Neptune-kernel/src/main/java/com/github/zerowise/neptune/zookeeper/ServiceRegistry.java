package com.github.zerowise.neptune.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.Configs;
import com.github.zerowise.neptune.event.EventListener;

/**
 * 服务注册
 *
 */
public class ServiceRegistry extends ZookeeperService {
	private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

	public ServiceRegistry() {
		super();
	}

	private void register(String data) {
		if (data != null) {
			createNode(data);
		}
	}

	private void addRootNode() {
		try {
			Stat s = zookeeper.exists(Configs.getString("zk.register.path"), false);
			if (s == null) {
				zookeeper.create(Configs.getString("zk.register.path"), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
		} catch (KeeperException | InterruptedException e) {
			logger.error("", e);
		}
	}

	private void createNode(String data) {
		try {
			byte[] bytes = data.getBytes();
			String path = zookeeper.create(Configs.getString("zk.register.path") + "/data", bytes,
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("create zookeeper node ({} => {})", path, data);
		} catch (KeeperException | InterruptedException e) {
			logger.error("", e);
		}
	}

	@Override
	public void start() throws Throwable {
		if (zookeeper != null) {
			addRootNode(); // Add root node if not exist
		}
		eventBus.notify(new ZkRegisterStartedEvent());
		eventBus.regist(new EventListener<ZkRegisterNodeEvent>() {

			@Override
			public void onEvent(ZkRegisterNodeEvent event) {
				register(event.node);
			}
		});
	}

	@Override
	protected void subcribe() {
	}
}