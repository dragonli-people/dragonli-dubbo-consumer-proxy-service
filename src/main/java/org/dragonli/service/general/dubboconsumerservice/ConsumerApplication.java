package org.dragonli.service.general.dubboconsumerservice;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.log4j.Logger;
import org.dragonli.service.dubbosupport.DubboApplicationBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
		exclude={DataSourceAutoConfiguration.class},
		scanBasePackages={"org.dragonli"})
@DubboComponentScan(basePackages = "org.dragonli.service.general.dubboconsumerservice")
public class ConsumerApplication extends DubboApplicationBase {
	
	public ConsumerApplication(
			@Value("${service.micro-service.dubbo-consumer-proxy-service.application-name}") String applicationName,
			@Value("${service.micro-service.common.registry-address}") String registryAddr,
			@Value("${service.micro-service.dubbo-consumer-proxy-service.protocol-name}") String protocolName,
			@Value("${service.micro-service.dubbo-consumer-proxy-service.protocol-port}") Integer protocolPort,
			@Value("${service.micro-service.dubbo-consumer-proxy-service.scan}") String registryId,
			@Value("${service.micro-service.dubbo-consumer-proxy-service.http-port}") int port,
            @Value("${DEBUG_LOG}") boolean debugLog
		) throws Exception
	{
		//测试动态打包的代码
//		Class clz1 = Class.forName("org.dragonli.service.general.other.ZookeeperLock");
//		System.out.println(clz1.getName());
//		Class clz2 = Class.forName("org.springframework.jdbc.core.PreparedStatementCallback");
//		System.out.println(clz2.getName());
//		Class clz3 = Class.forName("com.twilio.base.ResourceSet");
//		System.out.println(clz3.getName());
		super(applicationName, registryAddr, protocolName, protocolPort, registryId, port);
		ConsumerVars.debugLog = debugLog;
		System.out.println("===========debugLog:"+debugLog);
	}

	@SuppressWarnings(value = "unused")
	final Logger logger = Logger.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}
}
