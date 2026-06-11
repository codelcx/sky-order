package com.sky.config;

import com.alibaba.fastjson.TypeReference;
import com.sky.entity.DishFlavor;
import com.sky.type.JsonTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MyBatisConfig {

    @Bean
    TypeHandler<List<DishFlavor>> listDishFlavorTypeHandler() {
        return new JsonTypeHandler<>(new TypeReference<List<DishFlavor>>() {}.getType());
    }
}
