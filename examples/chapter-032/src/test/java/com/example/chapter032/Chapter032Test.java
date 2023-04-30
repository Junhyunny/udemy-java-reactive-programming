package com.example.chapter032;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

class Chapter032Test {

    Faker faker = Faker.instance();

    List<String> getNames(int count) {
        List<String> list = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            list.add(faker.name().fullName());
        }
        return list;
    }

    Flux<String> getNamesFlux(int count) {
        return Flux
                .range(0, count)
                .map(number -> {
                    sleep(1000);
                    return faker.name().fullName();
                });
    }

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
                .subscribeWith(new CustomSubscriber(atomicReference));

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

    @Test
    void example09() {

        getNames(5).forEach(System.out::println);

        getNamesFlux(5)
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example10() {

        Flux.interval(Duration.ofSeconds(1)).subscribe(System.out::println);

        sleep(5000);
    }

    @Test
    void example11() {

        Mono<String> mono = Mono.just("Hello World");
        Flux<String> flux = Flux.from(mono);
        flux.subscribe(System.out::println);
    }

    @Test
    void example12() {

        // next 메소드를 선언하면 Mono 타입으로 변경된다.
        Flux.range(1, 10)
                .filter(number -> number > 3)
                .next()
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example13() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        StockPricePublisher.getPrice()
                .subscribeWith(new Subscriber<Integer>() {

                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer price) {
                        System.out.printf("%s - price : %s\n", LocalDateTime.now(), price);
                        if (price > 110 || price < 90) {
                            subscription.cancel();
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onComplete() {
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await();
    }
}
