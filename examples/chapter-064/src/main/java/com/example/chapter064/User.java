package com.example.chapter064;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {

    private int id;
    private String name;

    public User(int userId) {
        Faker faker = Faker.instance();
        this.id = userId;
        this.name = faker.name().firstName();
    }
}
