package com.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.platform.mapper", "com.platform.module.*.mapper"})
public class PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
        System.out.println("========================================");
        System.out.println("  Platform Backend Started Successfully  ");
        System.out.println("========================================");
        System.out.println("  Module paths:");
        System.out.println("    - com.platform.module.*");
        System.out.println("========================================");
    }
}
