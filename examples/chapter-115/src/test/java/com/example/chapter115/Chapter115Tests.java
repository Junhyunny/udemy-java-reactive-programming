package com.example.chapter115;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class Chapter115Tests {

    Faker faker = Faker.instance();

    List<String> cache = new ArrayList<>();

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Flux<String> generateNames() {
        return Flux.generate(synchronousSink -> {
                    System.out.println("generate fresh");
                    sleep(1000);
                    String name = faker.name().fullName();
                    synchronousSink.next(name);
                    cache.add(name);
                })
                .cast(String.class)
                .startWith(getFromCache());
    }

    Flux<String> getFromCache() {
        return Flux.fromIterable(cache);
    }

    @Test
    void example01() {

        generateNames()
                .take(3)
                .subscribe(
                        value -> System.out.printf("received-1: %s\n", value),
                        error -> System.out.printf("error-1: %s\n", error.getMessage()),
                        () -> System.out.println("completed-1")
                );

        generateNames()
                .take(4)
                .subscribe(
                        value -> System.out.printf("received-2: %s\n", value),
                        error -> System.out.printf("error-2: %s\n", error.getMessage()),
                        () -> System.out.println("completed-2")
                );

        generateNames()
                .filter(name -> name.startsWith("A"))
                .take(1)
                .subscribe(
                        value -> System.out.printf("received-3: %s\n", value),
                        error -> System.out.printf("error-3: %s\n", error.getMessage()),
                        () -> System.out.println("completed-3")
                );
    }

    @Test
    void example02() {

        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d", "e");
        Flux<String> flux3 = Flux.error(new RuntimeException("oops"));

        // Flux<String> flux = flux1.concatWith(flux2);
        Flux<String> flux = Flux.concat(flux1, flux3, flux2);
        // Flux<String> flux = Flux.concatDelayError(flux1, flux3, flux2);

        flux.subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage()),
                () -> System.out.println("completed")
        );
    }

    @Test
    void example03() {

        Flux<String> mergeFlux = Flux.merge(
                Qatar.getFlights(),
                Emirates.getFlights(),
                AmericaAirlines.getFlights()
        );

        mergeFlux.subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage()),
                () -> System.out.println("completed")
        );

        sleep(15000);
    }

    Flux<String> getBody() {
        return Flux.range(1, 5)
                .map(number -> "body");
    }

    Flux<String> getEngine() {
        return Flux.range(1, 2)
                .map(number -> "engine");
    }

    Flux<String> getTires() {
        return Flux.range(1, 6)
                .map(number -> "tire");
    }

    @Test
    void example04() {

        // 각 퍼블리셔에서 발생시키는 아이템을 하나씩 모아서 리스트를 만든다.
        // 특정 퍼블리셔의 아이템이 모자르다면 subscribe 되지 않는다.
        Flux.zip(getBody(),
                getEngine(),
                getTires()
        ).subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage()),
                () -> System.out.println("completed")
        );
    }

    Flux<String> getString() {
        return Flux.just("A", "B", "C", "D")
                .delayElements(Duration.ofSeconds(1));
    }

    Flux<Integer> getNumber() {
        return Flux.just(1, 2)
                .delayElements(Duration.ofMillis(2500));
    }

    @Test
    void example05() {

        Flux.combineLatest(
                getString(),
                getNumber(),
                (a, b) -> a + b
        ).subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage()),
                () -> System.out.println("completed")
        );

        sleep(7500);
    }

    Flux<Long> monthStream() {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(1));
    }

    Flux<BigDecimal> demandStream() {
        return Flux.interval(Duration.ofSeconds(3))
                .map(number -> BigDecimal.valueOf(faker.random().nextInt(80, 120) / 100d))
                .startWith(BigDecimal.valueOf(1d));
    }

    @Test
    void example06() {

        final int carPrice = 10000;
        Flux.combineLatest(
                monthStream(),
                demandStream(),
                (month, demand) -> demand.multiply(BigDecimal.valueOf(carPrice - (month * 100)))

        ).subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage()),
                () -> System.out.println("completed")
        );

        sleep(10000);
    }
}
