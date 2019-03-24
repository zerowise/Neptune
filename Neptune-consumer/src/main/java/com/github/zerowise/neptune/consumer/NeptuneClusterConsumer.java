package com.github.zerowise.neptune.consumer;

import com.github.zerowise.neptune.event.EventBus;
import com.github.zerowise.neptune.event.EventListener;
import com.github.zerowise.neptune.kernel.ResponseMessage;
import com.github.zerowise.neptune.kernel.Session4Cluster;
import com.github.zerowise.neptune.zookeeper.ServerUpdateUrlsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NeptuneClusterConsumer extends RpcConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NeptuneConsumer.class);

    public NeptuneClusterConsumer(Consumer<ResponseMessage> consumer) {
        this.session = new Session4Cluster();
        this.consumer = Objects.requireNonNull(consumer);
    }

    @Override
    public void start() throws Throwable {
    }

    @Override
    public void stop() throws Throwable {
        session.stop();
        session = null;

        eventBus.unregist(this);
    }

    @Override
    public void regist(EventBus eventBus) {
        super.regist(eventBus);
        eventBus.regist(this, new EventListener<ServerUpdateUrlsEvent>() {
            @Override
            public void onEvent(ServerUpdateUrlsEvent event) {
                if (session == null) {
                    return;
                }
                Map<Long, InetSocketAddress> addresses = event.urls.stream().collect(Collectors.toMap(
                        s -> Long.parseLong(s.split(":")[0]),
                        s -> {
                            String[] s1 = s.split(":");
                            return new InetSocketAddress(s1[1], Integer.parseInt(s1[2]));
                        }

                ));

                ((Session4Cluster) session).update(addresses);

                if (addresses.isEmpty()) {
                    return;
                }

                addresses.forEach((id, addr) -> {
                    NeptuneConsumer neptuneConsumer = new NeptuneConsumer(consumer, id, addr);
                    try {
                        neptuneConsumer.start();
                        ((Session4Cluster) session).registerSession(neptuneConsumer.session);
                    } catch (Throwable throwable) {
                        logger.error("id{}, addr:{} start listen failed", id, addr);
                        return;
                    }
                });
            }
        });
    }
}
