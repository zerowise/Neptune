package com.github.zerowise.neptune.zookeeper;

import com.github.zerowise.neptune.EventCodes;
import com.github.zerowise.neptune.event.Event;
import com.github.zerowise.neptune.event.EventType;

import java.util.List;

/**
 * @Author: hanyuanliang@hulai.com
 * @Date: 2019-03-24 16:45
 **/

@EventType(EventCodes.SERVER_UPDATE_URLS)
public class ServerUpdateUrlsEvent extends Event {
    public final List<String> urls;

    public ServerUpdateUrlsEvent(List<String> urls) {
        this.urls = urls;
    }
}
