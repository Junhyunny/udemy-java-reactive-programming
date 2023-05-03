package com.example.chapter115;

import com.github.javafaker.Faker;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class AmericaAirlines {

    static Faker faker = Faker.instance();

    public static Flux<String> getFlights() {
        return Flux.range(1, faker.random().nextInt(1, 10))
                .delayElements(Duration.ofSeconds(1))
                .map(number -> "AmericaAirlines " + faker.random().nextInt(100, 999))
                .filter(flight -> faker.random().nextBoolean());
    }
}
