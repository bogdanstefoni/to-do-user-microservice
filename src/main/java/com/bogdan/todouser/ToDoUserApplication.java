package com.bogdan.todouser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;

import java.io.File;

import static com.bogdan.todouser.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
@ConfigurationPropertiesScan("com.bogdan.todouser")
@EnableFeignClients
public class ToDoUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoUserApplication.class, args);
        new File(USER_FOLDER).mkdirs();
    }

    @Bean
    public RequestContextListener contextListener() {
        return new RequestContextListener();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
