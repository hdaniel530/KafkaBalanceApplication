package com.example.KafkaSpringBoot.repository;

import com.example.KafkaSpringBoot.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {}
