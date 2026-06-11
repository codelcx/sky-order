package com.sky.config;

import com.sky.utils.LocalStorageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class OssConfig {

    @Bean
    public LocalStorageUtil localStorageUtil() {
        return new LocalStorageUtil(Paths.get(System.getProperty("user.dir"), "uploads"));
    }
}
