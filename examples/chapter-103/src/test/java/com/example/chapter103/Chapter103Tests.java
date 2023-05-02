package com.example.chapter103;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

class Chapter103Tests {

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void example01() {

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 501; index++) {
                        fluxSink.next(index);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(50))
                .subscribe(value -> System.out.printf("received: %s\n", value));

        sleep(45000);
    }

    @Test
    void example02() {

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 501; index++) {
                        fluxSink.next(index);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                .onBackpressureDrop()
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(50))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(45000);
    }

    @Test
    void example03() {

        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 501; index++) {
                        fluxSink.next(index);
                        sleep(30);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                .onBackpressureDrop()
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(50))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(45000);
    }

    @Test
    void example04() {

        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        List<Object> list = new ArrayList<>();

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 501; index++) {
                        fluxSink.next(index);
                        sleep(5);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                .onBackpressureDrop(list::add)
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(10))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(15000);
        System.out.println(list);
    }

    @Test
    void example05() {

        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 201; index++) {
                        fluxSink.next(index);
                        sleep(1);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                // 기능 다시 확인 필요
                .onBackpressureLatest()
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(10))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(15000);
    }

    @Test
    void example06() {


        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 201 && !fluxSink.isCancelled(); index++) {
                        fluxSink.next(index);
                        sleep(1);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                // 에러가 발생하면 파이프라인 cancel 처리
                .onBackpressureError()
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(10))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(15000);
    }

    @Test
    void example07() {

        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 201 && !fluxSink.isCancelled(); index++) {
                        fluxSink.next(index);
                        sleep(1);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                })
                .onBackpressureBuffer(10, (obj) -> System.out.printf("dropped %s\n", obj))
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(10))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(5000);
    }

    @Test
    void example08() {

        // 큐 버퍼 사이즈가 작더라도 publish / subscribe 속도가 비슷하면 소화 가능
        System.setProperty("reactor.bufferSize.small", "16");

        Flux.create(fluxSink -> {
                    for (int index = 0; index < 201 && !fluxSink.isCancelled(); index++) {
                        fluxSink.next(index);
                        sleep(1);
                        System.out.printf("pushed: %s\n", index);
                    }
                    fluxSink.complete();
                }, FluxSink.OverflowStrategy.DROP)
                // other thread from here
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(number -> sleep(10))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(15000);
    }
}
