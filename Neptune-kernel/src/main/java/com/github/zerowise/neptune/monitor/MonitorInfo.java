package com.github.zerowise.neptune.monitor;

/**
 * @Author: hanyuanliang@hulai.com
 * @Date: 2019-03-24 19:56
 **/
public class MonitorInfo {

    private String host;
    private long node;
    private String service;
    private String method;
    private long timeInMills;//所花事件
    private long requestId;

    public MonitorInfo() {
    }

    public MonitorInfo(String host, long node, String service, String method, long timeInMills, long requestId) {
        this.host = host;
        this.node = node;
        this.service = service;
        this.method = method;
        this.timeInMills = timeInMills;
        this.requestId = requestId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getNode() {
        return node;
    }

    public void setNode(long node) {
        this.node = node;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getTimeInMills() {
        return timeInMills;
    }

    public void setTimeInMills(long timeInMills) {
        this.timeInMills = timeInMills;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
