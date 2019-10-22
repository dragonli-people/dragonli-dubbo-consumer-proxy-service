# dragonli-dubbo-consumer-proxy-service
* provide service for nodejs to visit dubbo service easily and quickly by websocket protocol

* 本项目使用反射生成dubbo消费者的实例
* 本项目只包含运行所需的代码，并不包含接口类
* 因此，欲令本项目实际运行，则打包时需要作一些手脚，在打包过程时动态地将包含微服务接口类的依赖包引入进来。即根据不同的参数、引入不同的依赖包，可启动不同的本项目的实例（比如，您可以籍此形成不同的实例，并提供不同权限的网关给nodejs调用）
* 如何动态引入依赖包？详见dependencies.config.readme.txt


* 您可以修改service.micro-service.dubbo-consumer-proxy-service来修改服务所需的配置
* 与所有服务一样，您可以设置 --MICRO_SERVICE_PORT、--MICRO_SERVICE_HTTP_PORT分别用来覆盖微服务端口号、http端口号，（尤其是在端口号冲突之时）
* 您可以通过 --MICRO_SERVICE_NETTY_PORT 覆盖给nodejs访问的端口
* 您可以通过 --MICRO_SERVICE_TELNET_PORT 覆盖telnet命令端口
