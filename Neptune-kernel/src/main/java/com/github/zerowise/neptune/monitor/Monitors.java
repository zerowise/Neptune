package com.github.zerowise.neptune.monitor;

import com.alibaba.fastjson.JSON;
import com.github.zerowise.neptune.Configs;
import com.github.zerowise.neptune.HttpUtil;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: hanyuanliang@hulai.com
 * @Date: 2019-03-24 20:04
 **/
public class Monitors {

    private static Queue<MonitorInfo> monitorInfoQueue;

    private static ScheduledExecutorService executor;

    private static boolean hasWorked;

    public static void start() {
        if (!Configs.hasPath("monitors.url")) {
            return;
        }
        hasWorked = true;

        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("SEND_MONITOR"));
        }

        monitorInfoQueue = new ConcurrentLinkedQueue<>();

        executor.schedule(() -> monitor(), 10, TimeUnit.SECONDS);
    }

    private static void monitor() {
        List<MonitorInfo> monitorInfos = peekAll();
        if (monitorInfos.isEmpty()) {
            return;
        }

        try {
            HttpUtil.sendPost(Configs.getString("monitors.url"), JSON.toJSONBytes(monitorInfos));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void add(MonitorInfo monitorInfo) {
        if (!hasWorked) {
            return;
        }
        monitorInfoQueue.add(monitorInfo);
    }


    private static List<MonitorInfo> peekAll() {
        if (monitorInfoQueue.isEmpty()) {
            return Collections.emptyList();
        }
        List<MonitorInfo> arr = new ArrayList<>(monitorInfoQueue.size());
        MonitorInfo head;
        while ((head = monitorInfoQueue.peek()) != null) {
            arr.add(head);
        }

        return arr;
    }

    public static void shutdown() {
        if (executor != null)
            executor.shutdown();
    }
}
