package com.example.KafkaSpringBoot.config;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.Schema;
import org.apache.kafka.clients.admin.NewTopic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Properties;

@Configuration
public class Configs {

    @Value("${spring.kafka.streams.application-id}")
    String applicationID;
    @Value("${spring.kafka.streams.bootstrap-servers}")
    String bootstrapServers;
    @Value("${spring.kafka.properties.schema.registry.url}")
    String schemaURL;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    String offsetReset;
    @Value("${spring.kafka.template.default-topic}")
    String topic;

    //String format for Avro schema
    private String avroSchema = "{\"type\":\"record\"," +
            "\"name\":\"user\"," +
            "\"fields\":[{\"name\":\"id\",\"type\":\"string\"}," +
            "{\"name\":\"name\",\"type\":\"string\"}," +
            "{\"name\":\"balance\",\"type\":\"int\"}," +
            "{\"name\":\"time\",\"type\":\"string\"}" +
            "]}";



    @Bean
    public NewTopic testTopic(){
        return TopicBuilder.name(topic).build();
    }

    @Bean
    public Properties streamProperties(){
        Properties props = new Properties();

        //Streams configurations
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        //Avro serializer and deserializer configs
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class.getName());
        props.put("schema.registry.url", schemaURL);

        //Disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        //Exactly once processing
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        return props;
    }

    @Bean
    public Schema avroSchema(){
        Schema.Parser parser = new Schema.Parser();
        return parser.parse(avroSchema);
    }

}
