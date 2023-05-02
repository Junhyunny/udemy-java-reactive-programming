package com.example.chapter091;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class Chapter091Tests {

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void printThreadName(String message) {
        System.out.printf("%s | thread name: %s\n", message, Thread.currentThread().getName());
    }

    @Test
    void example01() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                    fluxSink.complete();
                })
                .doOnNext(value -> printThreadName(String.format("next %s", value)))
                .doOnComplete(() -> printThreadName("completed"));

        flux.subscribe(value -> printThreadName(String.format("subscribe %s", value)));
    }

    @Test
    void example02() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                    fluxSink.complete();
                })
                .doOnNext(value -> printThreadName(String.format("next %s", value)))
                .doOnComplete(() -> printThreadName("completed"));

        Runnable runnable = () -> flux.subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        for (int index = 0; index < 2; index++) {
            new Thread(runnable).start();
        }

        sleep(1000);
    }

    @Test
    void example03() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(number -> printThreadName("next " + number));

        flux
                .doFirst(() -> printThreadName("first-2"))
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> printThreadName("first-1"))
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        sleep(5000);
    }

    @Test
    void example04() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .doOnNext(number -> printThreadName("next " + number));

        Runnable runnable = () -> flux
                .doFirst(() -> printThreadName("first-2"))
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> printThreadName("first-1"))
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        for (int index = 0; index < 2; index++) {
            new Thread(runnable).start();
        }

        sleep(5000);
    }

    @Test
    void example05() {

        // multiple subscribe on
        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    fluxSink.next(1);
                })
                .subscribeOn(Schedulers.newParallel("bins"))
                .doOnNext(number -> printThreadName("next " + number));

        Runnable runnable = () -> flux
                .doFirst(() -> printThreadName("first-2"))
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> printThreadName("first-1"))
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        for (int index = 0; index < 2; index++) {
            new Thread(runnable).start();
        }

        sleep(5000);
    }

    @Test
    void example06() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                        sleep(250);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next " + number));

        flux.subscribeOn(Schedulers.parallel())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        sleep(10000);
    }

    @Test
    void example07() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                        sleep(250);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next " + number));

        Runnable runnable = () -> flux.subscribeOn(Schedulers.parallel())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        for (int index = 0; index < 2; index++) {
            new Thread(runnable).start();
        }

        sleep(10000);
    }

    @Test
    void example08() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                        sleep(250);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next " + number));

        Runnable runnable = () -> flux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        for (int index = 0; index < 5; index++) {
            new Thread(runnable).start();
        }

        sleep(10000);
    }

    @Test
    void example09() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                        sleep(250);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next " + number));

        flux.subscribeOn(Schedulers.boundedElastic())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        flux.subscribeOn(Schedulers.parallel())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        sleep(10000);
    }

    @Test
    void example10() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next-1: " + number));

        flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> printThreadName("next-2: " + number))
                .subscribe(value -> printThreadName(String.format("subscribe %s", value)));

        sleep(10000);
    }

    @Test
    void example11() {

        Flux<Object> flux = Flux.create(fluxSink -> {
                    printThreadName("create"); // boundedElastic
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                    }
                    fluxSink.complete();
                })
                .doOnNext(number -> printThreadName("next-1: " + number)); // boundedElastic

        flux.publishOn(Schedulers.parallel())
                .doOnNext(number -> printThreadName("next-2: " + number)) // parallel
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(value -> printThreadName(String.format("subscribe %s", value))); // parallel

        sleep(10000);
    }

    @Test
    void example12() {

        // not thread safe
        List<Integer> list = new ArrayList<>();

        // thread safe
        // Queue<Integer> list = new ConcurrentLinkedQueue<>();

        Flux.range(1, 1000)
                // .parallel()
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(number -> printThreadName("next: " + number))
                // bring flux back to flux
                // .sequential()
                .subscribe(list::add);

        sleep(2500);

        System.out.println(list.size());
    }

    @Test
    void example13() {

        // interval used parallel internally
        Flux.interval(Duration.ofSeconds(1))
                // 내부에셔 parallel 방식의 스레드를 사용하므로 이를 아래에서 변경할 수 있다.
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(System.out::println);

        sleep(5000);
    }
}
