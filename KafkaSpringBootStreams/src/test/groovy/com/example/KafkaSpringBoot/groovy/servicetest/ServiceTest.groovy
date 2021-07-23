package com.example.KafkaSpringBoot.groovy.servicetest

import com.example.KafkaSpringBoot.model.User
import com.example.KafkaSpringBoot.service.userserviceimpl.UserServiceImpl

import com.mongodb.MongoException
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.TopologyTestDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import spock.lang.*

@SpringBootTest
class ServiceTest extends Specification {
    @Autowired
    UserServiceImpl userService

    @Autowired
    Properties streamsConfig

    def "maximum of two numbers"() {
        expect:
        Math.max(a, b) == c

        where:
        a | b || c
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }
    @Ignore
    def "userService check if existing id is present in Mongo"(){
        given:
        def id = "125"

        expect:
        userService.getUserByID(id).isPresent() == true
    }
    @Ignore
    def "userService check if non-existing id is present in Mongo"(){
        given:
        def id = "1259"

        expect:
        userService.getUserByID(id).isPresent() == false
    }
    @Ignore
    def "userService check update user parameters if id exists but name does not match"(){
        given:
        def id = "125"
        def name = "Bob"
        User user = new User()
        user.setId(id)
        user.setName(name)

        when:
        userService.updateUser(user)

        then:
        thrown(MongoException)
    }
    @Ignore
    def "test Kafka Streams"(){
        given:
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("third_topic").to("fourth_topic-1");
        Topology topology = builder.build();

        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology,streamsConfig)

        topologyTestDriver.createInputTopic()
        topologyTestDriver.createOutputTopic()

        expect:
        verify()
    }

}
