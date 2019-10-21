# dragonli-dubbo-consumer-proxy-service
provide service for nodejs to visit dubbo service easily and quickly by websocket protocol

1 本项目使用反射生成dubbo消费者的实例
1 本项目只包含运行所需的代码，并不包含接口类
1 因此，欲令本项目实际运行，则打包时需通过mvn参数将所需要的接口的包引入进来。参数不同可打包出不同的基于本项目的"网关"
1 配置参数格式示例如下：
mvn -Pinterface1 \
-Dinterface.groupId1=org.dragonli.service \
-Dinterface.artifactId1=dragonli-general-service-interfaces \
-Dinterface.version1=1.0 \
clean install
首先要用-P参数激活一个配置，并为之配置groupId和artifactId
支持多个依赖：
mvn \
-Pinterface1 \
-Dinterface.groupId1=org.dragonli.service \
-Dinterface.artifactId1=dragonli-general-service-interfaces \
-Dinterface.version1=1.0 \
-Pinterface2 \
-Dinterface.groupId2=org.springframework \
-Dinterface.artifactId2=spring-jdbc \
-Dinterface.version1=5.1.2.RELEASE \
clean install
可支持10项这样的配置

1 另外还有一种便捷的增加依赖包的方式。您也可以使用类似下面linux shell脚本：

dps=" \
 \
<dependency> \
<groupId>org.dragonli.service<\/groupId> \
<artifactId>dragonli-general-service-interfaces<\/artifactId> \
<version>1.0<\/version> \
<\/dependency> \
 \
<dependency> \
<groupId>org.springframework<\/groupId> \
<artifactId>dragonli-netty-core<\/artifactId> \
<version>1.0<\/version> \
<\/dependency> \
 \
"
sed "s/<\!-- extend dependencies begin -->.*<\!-- extend dependencies end -->/<\!-- extend dependencies begin --> $dps <\!-- extend dependencies end -->/g" pom.xml > pom.xml.tmp
rm -rf pom.xml
mv pom.xml.tmp pom.xml
mvn clean install

