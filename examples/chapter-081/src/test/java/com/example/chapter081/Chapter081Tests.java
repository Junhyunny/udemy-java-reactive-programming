package com.example.chapter081;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

class Chapter081Tests {

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Stream<String> getMovie() {
        System.out.println("got the movie stream request");
        return Stream.of(
                "scene 1",
                "scene 2",
                "scene 3",
                "scene 4",
                "scene 5",
                "scene 6",
                "scene 7",
                "scene 8",
                "scene 9",
                "scene 10"
        );
    }

    @Test
    void example01() {

        // cold publisher
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1));

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(3000);

        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example02() {

        // hot publisher
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .share();

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(3000);

        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example03() {

        // share = publish().refCount(1)
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .refCount(1);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(3000);

        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example04() {

        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .refCount(2);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(3000);

        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example05() {

        // 한번 소요되고 있는 파이프라인 중간에 청취하는 경우 같은 데이터를 전달받는다.
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .refCount(1);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(11000);

        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example06() {

        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .autoConnect(1);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(11000);

        System.out.println("Mike is about to join");

        // do not subscribe
        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example07() {

        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .publish()
                .autoConnect(0);

        sleep(3000);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(11000);

        System.out.println("Mike is about to join");

        // do not subscribe
        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example08() {

        // cache = replay().autoConnect(1)
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .cache();

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(11000);

        System.out.println("Mike is about to join");

        // not delayed
        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example09() {

        // cache != publish().replay()
        // cache != replay().autoConnect(1)
        Flux<String> movieStream = Flux.fromStream(this::getMovie)
                .delayElements(Duration.ofSeconds(1))
                .replay()
                .autoConnect(1);

        movieStream.subscribe(
                movie -> System.out.printf("Sam: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Sam completed")
        );

        sleep(11000);

        System.out.println("Mike is about to join");

        // not delayed
        movieStream.subscribe(
                movie -> System.out.printf("Mike: %s\n", movie),
                error -> System.out.println(error.getMessage()),
                () -> System.out.println("Mike completed")
        );

        sleep(11000);
    }

    @Test
    void example10() {

        OrderService orderService = new OrderService();
        RevenueService revenueService = new RevenueService();
        InventoryService inventoryService = new InventoryService();

        // revenue and inventory - observe the order stream
        orderService.orderStream().subscribe(
                revenueService.subscribeOrderStream()
        );
        orderService.orderStream().subscribe(
                inventoryService.subscribeOrderStream()
        );


        revenueService.revenueStream()
                .subscribe(
                        revenue -> System.out.printf("Revenue: %s\n", revenue),
                        error -> System.out.printf("Revenue error: %s\n", error.getMessage()),
                        () -> System.out.println("Revenue completed")
                );
        inventoryService.inventoryStream()
                .subscribe(
                        inventory -> System.out.printf("Inventory: %s\n", inventory),
                        error -> System.out.printf("Inventory error: %s\n", error.getMessage()),
                        () -> System.out.println("Inventory completed")
                );

        sleep(60000);
    }
}
