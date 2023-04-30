package com.example.chapter032;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

class Chapter032Test {

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void example01() {

        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);


        flux.subscribe(
                System.out::println,
                (error) -> System.out.println(error.getMessage()),
                () -> System.out.println("completed")
        );
    }

    @Test
    void example02() {

        Flux<Integer> flux = Flux.empty();


        flux.subscribe(
                System.out::println,
                (error) -> System.out.println(error.getMessage()),
                () -> System.out.println("completed")
        );
    }

    @Test
    void example03() {

        Flux<Object> flux = Flux.just(1, 2, 3, "a", "hello world");


        flux.subscribe(
                System.out::println,
                (error) -> System.out.println(error.getMessage()),
                () -> System.out.println("completed")
        );
    }

    @Test
    void example04() {

        Flux<Integer> flux = Flux.just(1, 2, 3, 4);
        Flux<Integer> evenFlux = flux.filter(number -> number % 2 == 0);


        flux.subscribe((number) -> System.out.printf("Sub1: %s\n", number));
        flux.subscribe((number) -> System.out.printf("Sub2: %s\n", number));
        evenFlux.subscribe((number) -> System.out.printf("Even Sub: %s\n", number));
    }

    @Test
    void example05() {

        List<String> list = Arrays.asList("a", "b", "c");
        Flux.fromIterable(list).subscribe(System.out::println);


        Integer[] array = {1, 2, 3, 4, 5};
        Flux.fromArray(array).subscribe(System.out::println);
    }

    @Test
    void example06() {

        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Stream<Integer> stream = list.stream();


        // stream.forEach(System.out::println);

        // error occur cause stream used once
        // stream.forEach(System.out::println);

        Flux.fromStream(stream)
                .subscribe(
                        System.out::println,
                        (error) -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        // error occur cause stream used once
//        Flux.fromStream(stream)
//                .subscribe(
//                        System.out::println,
//                        (error) -> System.out.println(error.getMessage()),
//                        () -> System.out.println("completed")
//                );
    }

    @Test
    void example07() {

        Flux.range(1, 10)
                .log()
                .map(number -> number * 2)
                .log()
                .subscribe(System.out::println);
    }

    @Test
    void example08() {

        AtomicReference<Subscription> atomicReference = new AtomicReference<>();

        Flux.range(1, 20)
                .log()
                .subscribeWith(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        System.out.printf("Received: %s\n", s);
                        atomicReference.set(s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.printf("onNext: %s\n", integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.printf("onError: %s\n", t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });

        sleep(1000);
        atomicReference
                .get()
                .request(5);
        sleep(1000);
        atomicReference
                .get()
                .request(5);
        sleep(1000);
        System.out.println("going to cancel");
        atomicReference
                .get()
                .cancel();
        sleep(1000);
        // nothing happened here because request is canceled
        atomicReference
                .get()
                .request(5);
        sleep(1000);
    }
}
