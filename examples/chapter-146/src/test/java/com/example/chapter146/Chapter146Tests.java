package com.example.chapter146;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class Chapter146Tests {

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void example01() {

        // make simple sink
        Sinks.One<Object> sink = Sinks.one();

        Mono<Object> mono = sink.asMono();
        // run after emitting
        mono.subscribe(
                System.out::println
        );

        sink.tryEmitValue("hi");
    }

    @Test
    void example02() {

        // make simple sink
        Sinks.One<Object> sink = Sinks.one();

        Mono<Object> mono = sink.asMono();
        // run after emitting
        mono.subscribe(
                System.out::println,
                error -> System.out.printf("error: %s\n", error.getMessage())
        );

        sink.tryEmitError(new RuntimeException("hello world exception"));
    }

    @Test
    void example03() {

        // make simple sink
        Sinks.One<Object> sink = Sinks.one();

        Mono<Object> mono = sink.asMono();
        // run after emitting
        mono.subscribe(
                System.out::println,
                error -> System.out.printf("error: %s\n", error.getMessage())
        );

        sink.emitValue("hi", (signalType, emitResult) -> {
            System.out.println("hi - " + signalType.name());
            System.out.println("hi - " + emitResult.name());
            return false;
        });

        // run failure handler
        sink.emitValue("hello", (signalType, emitResult) -> {
            System.out.println("hello - " + signalType.name());
            System.out.println("hello - " + emitResult.name());
            return false;
        });
    }

    @Test
    void example04() {

        // make simple sink
        Sinks.One<Object> sink = Sinks.one();

        Mono<Object> mono = sink.asMono();
        // run after emitting
        mono.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );
        mono.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );

        sink.tryEmitValue("hello");
    }

    @Test
    void example05() {

        // handle through which we would push items
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();
        flux.subscribe(
                value -> System.out.printf("received: %s\n", value),
                error -> System.out.printf("error: %s\n", error.getMessage())
        );


        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");
        sink.tryEmitNext("hi");
    }

    @Test
    void example06() {

        // handle through which we would push items
        Sinks.Many<Object> sink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();
        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );
        flux.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );


        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");
        sink.tryEmitNext("hi");
    }

    @Test
    void example07() {

        // handle through which we would push items
        Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();
        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );
        flux.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );


        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");
        sink.tryEmitNext("hi");
    }

    @Test
    void example08() {

        Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();

        List<Object> list = new ArrayList<>();
        flux.subscribe(list::add);

//        for (int index = 0; index < 1000; index++) {
//            final int subIndex = index;
//            CompletableFuture.runAsync(() -> {
//                sink.tryEmitNext(subIndex);
//            });
//        }

        for (int index = 0; index < 1000; index++) {
            final int subIndex = index;
            CompletableFuture.runAsync(() -> {
                sink.emitNext(subIndex, (s, e) -> true);
            });
        }

        sleep(3000);
        System.out.println(list.size());
    }

    @Test
    void example09() {

        Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Object> flux = sink.asFlux();

        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");

        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );

        flux.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );

        sink.tryEmitNext("hi");

        flux.subscribe(
                value -> System.out.printf("received-3: %s\n", value),
                error -> System.out.printf("error-3: %s\n", error.getMessage())
        );

        sink.tryEmitNext("new message");
    }

    @Test
    void example10() {

        Sinks.Many<Object> sink = Sinks.many().multicast().directAllOrNothing();
        Flux<Object> flux = sink.asFlux();

        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");

        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );

        flux.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );

        sink.tryEmitNext("hi");

        flux.subscribe(
                value -> System.out.printf("received-3: %s\n", value),
                error -> System.out.printf("error-3: %s\n", error.getMessage())
        );

        sink.tryEmitNext("new message");
    }

    @Test
    void example11() {

        System.setProperty("reactor.bufferSize.small", "16");

        Sinks.Many<Object> sink = Sinks.many().multicast().directAllOrNothing();
        Flux<Object> flux = sink.asFlux();

        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage()),
                () -> System.out.println("completed-1")
        );

        flux.delayElements(Duration.ofMillis(200))
                .subscribe(
                        value -> System.out.printf("received-2: %s\n", value),
                        error -> System.out.printf("error-2: %s\n", error.getMessage()),
                        () -> System.out.println("completed-2")
                );

        for (int index = 0; index < 100; index++) {
            sink.tryEmitNext(index);
        }

        sleep(10000);
    }

    @Test
    void example12() {

        System.setProperty("reactor.bufferSize.small", "16");

        Sinks.Many<Object> sink = Sinks.many().multicast().directBestEffort();
        Flux<Object> flux = sink.asFlux();

        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage()),
                () -> System.out.println("completed-1")
        );

        flux.delayElements(Duration.ofMillis(200))
                .subscribe(
                        value -> System.out.printf("received-2: %s\n", value),
                        error -> System.out.printf("error-2: %s\n", error.getMessage()),
                        () -> System.out.println("completed-2")
                );

        for (int index = 0; index < 100; index++) {
            sink.tryEmitNext(index);
        }

        sleep(10000);
    }

    @Test
    void example13() {

        Sinks.Many<Object> sink = Sinks.many().replay().all();
        Flux<Object> flux = sink.asFlux();

        sink.tryEmitNext("hello");
        sink.tryEmitNext("hello world");

        flux.subscribe(
                value -> System.out.printf("received-1: %s\n", value),
                error -> System.out.printf("error-1: %s\n", error.getMessage())
        );

        flux.subscribe(
                value -> System.out.printf("received-2: %s\n", value),
                error -> System.out.printf("error-2: %s\n", error.getMessage())
        );

        sink.tryEmitNext("hi");

        flux.subscribe(
                value -> System.out.printf("received-3: %s\n", value),
                error -> System.out.printf("error-3: %s\n", error.getMessage())
        );

        sink.tryEmitNext("new message");
    }

    @Test
    void example14() {

        SlackRoom slackRoom = new SlackRoom("reactor");

        SlackMember sam = new SlackMember("sam");
        SlackMember jake = new SlackMember("jake");
        SlackMember mike = new SlackMember("mike");

        slackRoom.joinRoom(sam);
        slackRoom.joinRoom(jake);

        sam.says("Hi all!");
        sleep(1000);

        jake.says("Hey!");
        sam.says("I simply wanted to say hi...");
        sleep(1000);

        slackRoom.joinRoom(mike);
        mike.says("Hello all!");
        sleep(1000);
    }
}
