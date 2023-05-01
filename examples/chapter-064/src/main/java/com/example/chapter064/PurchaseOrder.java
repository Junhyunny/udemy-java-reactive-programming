package com.example.chapter064;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class PurchaseOrder {

    private String item;
    private BigDecimal price;
    private int userId;

    public PurchaseOrder(int userId) {
        Faker faker = Faker.instance();
        this.item = faker.commerce().productName();
        this.price = new BigDecimal(faker.commerce().price());
        this.userId = userId;
    }

}
