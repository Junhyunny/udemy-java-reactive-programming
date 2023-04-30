package com.example.chapter032;

import com.github.javafaker.Faker;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class StockPricePublisher {

    public static Flux<Integer> getPrice() {
        AtomicInteger atomicInteger = new AtomicInteger(100);
        return Flux.interval(Duration.ofSeconds(1))
                .map(number -> atomicInteger.getAndAccumulate(
                                Faker.instance().random().nextInt(-5, 5),
                                Integer::sum
                        )
                );
    }
}
