package com.bogdan.todouser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@ComponentScan("com.bogdan.todouser.config")
@EnableFeignClients
public class ToDoUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToDoUserApplication.class, args);
    }

}
