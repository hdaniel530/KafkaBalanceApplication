package com.example.KafkaSpringBoot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

//@Data imports all of the getters and setters
@Data
@Document(collection = "test-2")
public class User {
    @Id
    @NotEmpty(message = "ID cannot be null")
    public String id;
    @NotEmpty(message = "Name cannot be null")
    public String name;
    public int balance;
    public String time;

    public User(){}

    public User(@NotEmpty(message = "ID cannot be null") String id,@NotEmpty(message = "Name cannot be null")
            String name, int balance, String time) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.time = time;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getBalance() {
//        return balance;
//    }
//
//    public void setBalance(int balance) {
//        this.balance = balance;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", time='" + time + '\'' +
                '}';
    }
}