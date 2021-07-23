package com.example.KafkaSpringBoot.service;

import com.example.KafkaSpringBoot.model.*;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByID(String userId);
    User createUser(User user);
    User updateUser(User user);
//    public void deleteUser(User user);
}
