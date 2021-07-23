package com.example.KafkaSpringBoot.service;

import com.example.KafkaSpringBoot.model.User;
import com.example.KafkaSpringBoot.service.userserviceimpl.UserServiceImpl;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service
public class Streams {
    //private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Streams.class);

    @Value("${spring.kafka.properties.schema.registry.url}")
    String schemaURL;
    @Value("${spring.kafka.template.default-topic}")
    String streamToTopic;
    String streamFromTopic = "third_topic";

    @Autowired
    Schema avroSchema;

    @Autowired
    Properties streamProperties;

    @Autowired
    UserServiceImpl userService;

    @Bean
    public void StreamsService(){
        ///Override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", schemaURL);
        //Avro value Serde
        final Serde<GenericRecord> valueGenericAvroSerde = new GenericAvroSerde();
        valueGenericAvroSerde.configure(serdeConfig, false); // `false` for record values

        //builds Stream and Kafka Stream objects
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, GenericRecord> bankTransactions = builder.stream(streamFromTopic,
                Consumed.with(Serdes.String(), valueGenericAvroSerde));


        //Create the initial avro object for balances
        GenericRecord initialBalance = new GenericData.Record(avroSchema);

        initialBalance.put("id", "");
        initialBalance.put("name", "");
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

        //Creates KTable to perform aggregation functions on records from KStream
        KTable<String, GenericRecord> bankBalance = bankTransactions
                .groupByKey()
                .aggregate(() -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        Materialized.<String, GenericRecord, KeyValueStore<Bytes, byte[]>>as("bank-agg")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(valueGenericAvroSerde));

        //Converts KTable to KStream then writes records to new Topic
        bankBalance.toStream().to(streamToTopic, Produced.with(Serdes.String(), valueGenericAvroSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), streamProperties);
        streams.cleanUp();
        streams.start();

        //Print the topology
        //streams.localThreadsMetadata().forEach(data -> System.out.println(data));

        //Shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    //Creates new Avro record with new balance based on aggregated transactions
    private GenericRecord newBalance(GenericRecord transaction, GenericRecord balance) {
        GenericRecord newBalance = new GenericData.Record(avroSchema);

        newBalance.put("id", transaction.get("id").toString());
        newBalance.put("name", transaction.get("name").toString());

        newBalance.put("balance", Integer.parseInt(balance.get("balance").toString())
                + Integer.parseInt(transaction.get("amount").toString()));

        //Store the most recent transaction time
        Long balanceEpoch = Instant.parse(balance.get("time").toString()).toEpochMilli();
        Long transactionEpoch = Instant.parse(transaction.get("time").toString()).toEpochMilli();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        newBalance.put("time", newBalanceInstant.toString());

        //Create new user object to potentially add or update to database
        User user = new User(newBalance.get("id").toString(),newBalance.get("name").toString(),
                            Integer.parseInt(newBalance.get("balance").toString()),
                            newBalance.get("time").toString());

        log.info("Attempting to update database");
        updateDatabase(user);
        return newBalance;
    }
    //Creates a User object if non-existent, updates existing object with most recent time and balance
    private void updateDatabase(User user){
        //System.out.println("Value of get ID "+ userService.getUserByID(user.getId()));
        //checks if user with given id exists in the database, if not create a new user document else update existing if same user
        if(userService.getUserByID(user.getId()).isPresent()){
            try{
                log.info("Attempting to update existing user in database");
                userService.updateUser(user);
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }
        }
        else{
            try{
                log.info("Attempting to create new user in database");
                userService.createUser(user);
            }
            catch (Exception ex){
                log.error(ex.getMessage());
            }
        }
    }
}
