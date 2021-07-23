package com.example.KafkaSpringBoot.service.userserviceimpl;

import com.example.KafkaSpringBoot.model.*;
import com.example.KafkaSpringBoot.repository.UserRepository;
import com.example.KafkaSpringBoot.service.UserService;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> getUserByID(String userId){
        return userRepository.findById(userId);
    }

    @Override
    public User createUser(User user){
        if(!userRepository.findById(user.getId()).isPresent()){
            return userRepository.save(user);
        }
        else{
            throw new MongoException("Error in creating new user with id: " + user.getId());
        }
    }

    @Override
    public User updateUser(User user){
        User update = userRepository.findById(user.getId()).get();
        //checking if names are also equivalent
        if (user.getName().equalsIgnoreCase(update.getName())) {
            return userRepository.save(user);
        }
        else {
            throw new MongoException("Error in updating the user with id: " + user.getId()
                                    + " and name" + user.getName());
        }
    }

//    @Override
//    public void deleteUser(User user){
//        if(userRepository.findById(user.getId()).isPresent()){
//            userRepository.delete(user);
//        }
//        else{
//            throw new MongoException("Cannot delete user since user " +
//                                    "does not exists with id: " + user.getId());
//        }
//    }
}
