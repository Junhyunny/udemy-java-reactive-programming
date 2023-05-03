package com.example.chapter140;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Chapter140Tests {

    Faker faker = Faker.instance();

    AtomicInteger atomicInteger = new AtomicInteger(1);

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Flux<Integer> getIntegers() {
        return Flux.range(1, 3)
                .map(number -> atomicInteger.getAndIncrement())
                .doOnSubscribe(value -> System.out.printf("doOnSubscribe: %s\n", value))
                .doOnComplete(() -> System.out.println("doOnComplete"));
    }

    @Test
    void example01() {

        getIntegers()
                // .repeat(2)
                .repeat(() -> atomicInteger.get() < 14)
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example02() {

        getIntegers()
                .map(number -> number / new Random().nextInt(2))
                .doOnError(error -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .retry(10)
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example03() {

        getIntegers()
                .map(number -> number / new Random().nextInt(2))
                .doOnError(error -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3)))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(5000);
    }

    void processPayment(String ccNumber) {
        int random = faker.random().nextInt(1, 10);
        if (random < 8) {
            throw new RuntimeException("500");
        } else if (random < 10) {
            throw new RuntimeException("404");
        }
    }

    Mono<String> orderService(String ccNumber) {
        return Mono.fromSupplier(() -> {
            processPayment(ccNumber);
            return faker.idNumber().valid();
        });
    }

    @Test
    void example05() {

        orderService(faker.business().creditCardNumber())
                .doOnError(error -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .retry(5)
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example06() {

        orderService(faker.business().creditCardNumber())
                .doOnError(error -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .retryWhen(
                        Retry.from(flux -> flux.doOnNext(retrySignal -> {
                                            System.out.printf("totalRetries: %s\n", retrySignal.totalRetries());
                                            System.out.printf("failure: %s\n", retrySignal.failure());
                                        })
                                        .handle((retrySignal, synchronousSink) -> {
                                            if (retrySignal.failure().getMessage().equals("500")) {
                                                synchronousSink.next(1);
                                            } else {
                                                synchronousSink.error(retrySignal.failure());
                                            }
                                        })
                                        .delayElements(Duration.ofMillis(1))
                        )
                )
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(25000);
    }
}
