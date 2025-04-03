package com.project.UsersManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories // Enables MongoDB repositories
public class UsersManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersManagementApplication.class, args);
    }
}
