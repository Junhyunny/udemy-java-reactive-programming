package com.example.chapter064;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {

    private String name;
    private int age;

    public Person() {
        Faker faker = Faker.instance();
        this.name = faker.name().firstName();
        this.age = faker.random().nextInt(1, 20);
    }
}
