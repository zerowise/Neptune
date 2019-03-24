package com.github.zerowise.neptune.zookeeper;

import com.github.zerowise.neptune.event.EventBus;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.Configs;
import com.github.zerowise.neptune.event.EventListener;

/**
 * 服务注册
 */
public class ServiceRegistry extends ZookeeperService {


    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private volatile boolean hasCreateNode;


    public ServiceRegistry() {
        super();
    }

    private void register(String data) {
        if (data != null) {
            addRootNode();
            createNode(data);
        }
    }

    private void addRootNode() {
        if(hasCreateNode){
            return;
        }
        try {
            Stat s = zookeeper.exists(Configs.getString("zk.register.path"), false);
            if (s == null) {
                zookeeper.create(Configs.getString("zk.register.path"), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
                hasCreateNode = true;
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
    public void regist(EventBus eventBus) {
        super.regist(eventBus);
        eventBus.regist(this, new EventListener<ZkRegisterNodeEvent>() {

            @Override
            public void onEvent(ZkRegisterNodeEvent event) {
                register(event.node);
            }
        });
    }

    @Override
    public void start() throws Throwable {
        this.zookeeper = new ZooKeeper(registryAddress, Configs.getInt("zk.session.timeout", 5000), this);

        eventBus.notify(new ZkRegisterStartedEvent());
    }

    @Override
    protected void subcribe() {
    }
}