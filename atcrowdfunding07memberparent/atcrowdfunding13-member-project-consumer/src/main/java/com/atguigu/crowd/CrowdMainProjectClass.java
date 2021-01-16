package com.atguigu.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CrowdMainProjectClass {
    public static void main(String[] args) {
        SpringApplication.run(CrowdMainProjectClass.class, args);
    }
}
