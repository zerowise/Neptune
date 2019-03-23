package com.github.zerowise.neptune.zookeeper;

import com.github.zerowise.neptune.EventCodes;
import com.github.zerowise.neptune.event.Event;
import com.github.zerowise.neptune.event.EventType;

@EventType(EventCodes.ZK_REGISTER_START)
public class ZkRegisterStartedEvent extends Event{

}
