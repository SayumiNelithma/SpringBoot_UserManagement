package com.project.UsersManagement.repository;

import com.project.UsersManagement.entity.OurUsers;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsersRepo extends MongoRepository<OurUsers, String> {

    Optional<OurUsers> findByEmail(String email);
}
