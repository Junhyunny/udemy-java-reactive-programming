package com.example.chapter124;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

class Chapter124Tests {

    // batch - buffer, window, group

    AtomicInteger atomicInteger = new AtomicInteger();

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Flux<String> eventStream(int millis) {
        return Flux.interval(Duration.ofMillis(millis))
                .map(number -> "event-" + number);
    }

    @Test
    void example01() {

        eventStream(250)
                // not blocking just wait for items
                .buffer(5)
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    @Test
    void example02() {

        eventStream(250)
                // not blocking just wait for items
                .buffer(Duration.ofSeconds(2))
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    @Test
    void example03() {

        eventStream(10)
                // not blocking just wait for items
                .bufferTimeout(5, Duration.ofSeconds(2))
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    @Test
    void example04() {

        eventStream(1000)
                // not blocking just wait for items
                .bufferTimeout(5, Duration.ofSeconds(2))
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    @Test
    void example05() {

        eventStream(250)
                // not blocking just wait for items
                .buffer(3, 1)
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    Flux<BookOrder> bookStream() {
        return Flux.interval(Duration.ofMillis(250))
                .map(number -> new BookOrder());
    }

    RevenueReport revenueCalculator(List<BookOrder> books) {
        Map<String, BigDecimal> revenue = books
                .stream()
                .collect(Collectors.groupingBy(
                                BookOrder::getCategory,
                                Collectors.reducing(BigDecimal.ZERO, BookOrder::getPrice, BigDecimal::add)
                        )
                );
        return new RevenueReport(revenue);
    }

    @Test
    void example06() {

        Set<String> allowedCategories = Set.of(
                "Science fiction",
                "Fantasy",
                "Suspense/Thriller"
        );

        bookStream()
                .filter(book -> allowedCategories.contains(book.getCategory()))
                .buffer(Duration.ofSeconds(5))
                .map(this::revenueCalculator)
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(60000);
    }

    Mono<Integer> saveEvents(Flux<String> flux) {
        return flux
                .doOnNext(event -> System.out.printf("Saving: %s\n", event))
                .doOnComplete(() -> System.out.println("Saved this batch"))
                .then(Mono.just(
                        atomicInteger.getAndIncrement()
                ));
    }

    @Test
    void example07() {

        eventStream(500)
                // .window(5)
                .window(Duration.ofSeconds(2))
                .flatMap(this::saveEvents)
                .subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(10000);
    }

    void process(Flux<Integer> flux, int key) {
        flux.subscribe(
                value -> System.out.printf("Key: %s, Item: %s\n", key, value),
                error -> System.out.printf("Error: %s\n", error.getMessage()),
                () -> System.out.println("Completed")
        );
    }

    @Test
    void example08() {

        Flux.range(1, 30)
                .delayElements(Duration.ofMillis(500))
                .groupBy(number -> number % 3)
                .subscribe(groupFlux -> process(groupFlux, groupFlux.key()));

        sleep(15000);
    }

    @Test
    void example09() {

        Map<String, Function<Flux<PurchaseOrder>, Flux<PurchaseOrder>>> map = Map.of(
                "Kids", OrderProcessor.kidsProcessing(),
                "Automotive", OrderProcessor.automotiveProcessing()
        );

        Set<String> set = map.keySet();

        OrderService.orderStream()
                .filter(p -> set.contains(p.getCategory()))
                .groupBy(PurchaseOrder::getCategory)
                .flatMap(
                        groupedFlux -> map.get(groupedFlux.key()).apply(groupedFlux)
                ).subscribe(
                        value -> System.out.printf("Received: %s\n", value),
                        error -> System.out.printf("Error: %s\n", error.getMessage()),
                        () -> System.out.println("Completed")
                );

        sleep(15000);
    }
}
