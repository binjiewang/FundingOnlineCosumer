package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class CrowdMainZuulConsumerClass {
    public static void main(String[] args) {
        SpringApplication.run(CrowdMainZuulConsumerClass.class,args);
    }
}
