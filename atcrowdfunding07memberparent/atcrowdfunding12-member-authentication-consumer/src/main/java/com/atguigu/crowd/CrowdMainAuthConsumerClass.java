package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient // 当前版本可以不写
@SpringBootApplication
@EnableFeignClients
public class CrowdMainAuthConsumerClass {
    public static void main(String[] args) {
        SpringApplication.run(CrowdMainAuthConsumerClass.class,args);
    }
}
