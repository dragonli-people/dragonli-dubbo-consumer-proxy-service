# dragonli-dubbo-consumer-proxy-service
* provide service for nodejs to visit dubbo service easily and quickly by websocket protocol

* 本项目使用反射生成dubbo消费者的实例
* 本项目只包含运行所需的代码，并不包含接口类
* 因此，欲令本项目实际运行，则打包时需通过mvn参数将所需要的接口的包引入进来。参数不同可打包出不同的基于本项目的"网关"
* 配置参数格式，详见dependencies.config.readme.txt


* 您可以修改service.micro-service.dubbo-consumer-proxy-service来修改服务所需的配置
* 与所有服务一样，您可以设置 --MICRO_SERVICE_PORT、--MICRO_SERVICE_HTTP_PORT分别用来覆盖微服务端口号、http端口号，（尤其是在端口号冲突之时）
* 您可以通过 --MICRO_SERVICE_NETTY_PORT 覆盖给nodejs访问的端口
* 您可以通过 --MICRO_SERVICE_TELNET_PORT 覆盖telnet命令端口
