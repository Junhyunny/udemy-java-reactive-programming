package com.example.chapter081;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class PurchaseOrder {

    private String item;
    private BigDecimal price;
    private String category;
    private int quantity;

    public PurchaseOrder() {
        Faker faker = Faker.instance();
        this.item = faker.commerce().productName();
        this.price = new BigDecimal(faker.commerce().price());
        this.category = faker.commerce().department();
        this.quantity = faker.random().nextInt(1, 10);
    }
}
