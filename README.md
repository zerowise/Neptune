# Neptune

采用DUBBO类似的架构思想

![dubbo架构](https://images2017.cnblogs.com/blog/784166/201708/784166-20170821143129824-377503972.png)

节点角色说明：

Provider: 暴露服务的服务提供方。（已实现)

Consumer: 调用远程服务的服务消费方。（已实现)

Registry: 服务注册与发现的注册中心。（使用zookeeper）

Monitor: 统计服务的调用次调和调用时间的监控中心。（未实现)

Container: 服务运行容器。（未实现)


### 技术
1. jdk
2. netty
3. protostuff序列化
4. zookeeper
5. typesafe 配置文件
6. 观察者模式