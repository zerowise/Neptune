package com.github.zerowise.neptune.zookeeper;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zerowise.neptune.Configs;

/***
 * 服务发现
 *
 */
public class ServiceDiscovery extends ZookeeperService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    public ServiceDiscovery() {
        super();
    }

    @Override
    public void start() throws Throwable {
        this.zookeeper = new ZooKeeper(registryAddress, Configs.getInt("zk.session.timeout", 5000), this);
        subcribe();
    }

    protected void subcribe() {
        try {
            List<String> children = zookeeper.getChildren(Configs.getString("zk.register.path"), this);
            List<String> urls = new ArrayList<>();
            for (String node : children) {
                urls.add(new String(
                        zookeeper.getData(Configs.getString("zk.register.path") + "/" + node, false, null)));
            }

            eventBus.notify(new ServerUpdateUrlsEvent(urls));
        } catch (KeeperException | InterruptedException e1) {
            logger.error("", e1);
        }

    }

}