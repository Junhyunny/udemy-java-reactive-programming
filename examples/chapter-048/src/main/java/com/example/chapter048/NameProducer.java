package com.example.chapter048;

import com.github.javafaker.Faker;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class NameProducer implements Consumer<FluxSink<Object>> {

    private final Faker faker = Faker.instance();
    private FluxSink<Object> fluxSink;

    @Override
    public void accept(FluxSink<Object> fluxSink) {
        this.fluxSink = fluxSink;
    }

    public void produce() {
        String name = faker.name().fullName();
        fluxSink.next(String.format("%s(%s)", name, Thread.currentThread().getName()));
    }
}
