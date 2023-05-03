package com.example.chapter124;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class BookOrder {

    private final String title;
    private final String author;
    private final String category;
    private final BigDecimal price;

    public BookOrder() {
        Faker faker = Faker.instance();
        this.title = faker.book().title();
        this.author = faker.book().author();
        this.category = faker.book().genre();
        this.price = new BigDecimal(faker.commerce().price());
    }
}
