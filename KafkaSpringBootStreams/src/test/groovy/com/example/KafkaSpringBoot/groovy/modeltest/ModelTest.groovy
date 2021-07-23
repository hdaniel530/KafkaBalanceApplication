package com.example.KafkaSpringBoot.groovy.modeltest

import com.example.KafkaSpringBoot.model.User

import spock.lang.Specification

class ModelTest extends Specification{
    def "userModel test setID() and getID() if id set correctly after new user created"(){
        given:
        String id = "125"
        User user = new User()

        when:
        user.setId(id)

        then:
        user.getId().equalsIgnoreCase(id) == true

    }
}
