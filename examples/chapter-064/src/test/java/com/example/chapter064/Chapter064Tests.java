package com.example.chapter064;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

class Chapter064Tests {

    Faker faker = Faker.instance();

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void example01() {
        Flux.range(1, 20)
                .handle((number, synchronousSink) -> {
                    if (number % 2 == 0) {
                        synchronousSink.next(number);
                    } else {
                        synchronousSink.next(number + "a");
                    }
                    if (number == 7) {
                        synchronousSink.complete();
                    }
                })
                .subscribe(System.out::println);
    }

    @Test
    void example02() {

        Flux.generate(synchronousSink -> synchronousSink.next(faker.country().name()))
                .map(Object::toString)
                .handle((country, synchronousSink) -> {
                    synchronousSink.next(country);
                    if (country.equalsIgnoreCase("canada")) {
                        synchronousSink.complete();
                    }
                })
                .subscribe(System.out::println);
    }

    @Test
    void example03() {

        Flux.create(fluxSink -> {
                    System.out.println("inside create");
                    for (int index = 0; index < 5; index++) {
                        fluxSink.next(index);
                    }
                    fluxSink.complete();
                    System.out.println("inside create complete");
                })
                // lifecycle for callback
                .doOnComplete(() -> System.out.println("doOnComplete"))
                // doFirst reverse order
                .doFirst(() -> System.out.println("doFirst-1"))
                .doFirst(() -> System.out.println("doFirst-2"))
                .doFirst(() -> System.out.println("doFirst-3"))
                // doOnSubscribe order
                .doOnSubscribe((subscription) -> System.out.printf("doOnSubscribe-1: %s\n", subscription))
                .doOnSubscribe((subscription) -> System.out.printf("doOnSubscribe-2: %s\n", subscription))
                .doOnRequest((number) -> System.out.printf("doOnRequest: %s\n", number))
                //
                .doOnNext((number) -> System.out.printf("doOnNext: %s\n", number))
                .doOnError((error) -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .doOnDiscard(Object.class, (object) -> System.out.printf("doOnDiscard: %s\n", object))
                .doOnCancel(() -> System.out.println("doOnCancel"))
                //
                .doOnTerminate(() -> System.out.println("doOnTerminate"))
                .doFinally((signalType) -> System.out.printf("doFinally-1: %s\n", signalType.name()))
                //
                .take(2)
                //
                .doFinally((signalType) -> System.out.printf("doFinally-2: %s\n", signalType.name()))
                .subscribe(
                        System.out::println,
                        (error) -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example04() {

        Flux.range(1, 1000)
                .log()
                .limitRate(100)
                .subscribe(
                        System.out::println,
                        (error) -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example05() {

        Flux.range(1, 1000)
                .log()
                .limitRate(100, 80)
                .subscribe(
                        System.out::println,
                        (error) -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example06() {

        System.setProperty("reactor.bufferSize.x", "10");

        Flux.range(1, 60)
                .log()
                .delayElements(Duration.ofSeconds(1))
                .subscribe(
                        System.out::println,
                        (error) -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(60000);
    }

    @Test
    void example07() {

        Flux.range(1, 10)
                .log()
                // exception 발생하면 cancel request 발생
                .map(number -> 10 / (5 - number))
                .doOnError((error) -> System.out.printf("doOnError: %s\n", error.getMessage()))
                // .onErrorReturn(-1)
                // .onErrorResume(error -> fallback())
                // logging and continue pipeline
                .onErrorContinue((error, object) -> System.out.printf("onErrorContinue: (%s, %s)\n", error.getMessage(), object))
                .subscribe(
                        System.out::println,
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example08() {

        getOrderNumbers()
                .timeout(Duration.ofSeconds(2))
                .doOnError((error) -> System.out.printf("doOnError: %s\n", error.getMessage()))
                .onErrorResume(error -> fallbackFlux())
                .subscribe(
                        System.out::println,
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(15000);
    }

    @Test
    void example09() {

        Flux.range(1, 10)
                .filter(number -> number > 10)
                .defaultIfEmpty(-100)
                .subscribe(
                        System.out::println,
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example10() {

        Flux.range(1, 10)
                .filter(number -> number > 10)
                // fallback
                .switchIfEmpty(Flux.range(20, 5))
                .subscribe(
                        System.out::println,
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example11() {

        Flux.range(1, 10)
                .map(number -> new Person())
                .transform(
                        personFlux -> personFlux
                                .filter(p -> p.getAge() > 10)
                                .doOnNext(p -> p.setName(p.getName().toUpperCase()))
                                .doOnDiscard(Person.class, person -> System.out.printf("not allowing: %s\n", person))
                )
                .subscribe(
                        (person -> System.out.printf("received: %s\n", person)),
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example12() {

        Flux.range(1, 10)
                .map(number -> new Person())
                // run when first item
                .switchOnFirst(
                        (signal, personFlux) -> {
                            System.out.println("switchOnFirst");
                            if (signal.isOnNext() && Objects.requireNonNull(signal.get()).getAge() > 10) {
                                return personFlux;
                            }
                            return personFlux
                                    .filter(p -> p.getAge() > 10)
                                    .doOnNext(p -> p.setName(p.getName().toUpperCase()))
                                    .doOnDiscard(Person.class, person -> System.out.printf("not allowing: %s\n", person));
                        }
                )
                .subscribe(
                        (person -> System.out.printf("received: %s\n", person)),
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example13() {

        UserService.getUsers()
                .flatMap(user -> OrderService.getOrders(user.getId()))
                .subscribe(
                        (person -> System.out.printf("received: %s\n", person)),
                        (error) -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );

        sleep(10000);
    }

    Mono<Integer> fallback() {
        return Mono.fromSupplier(() -> faker.random().nextInt(100, 200));
    }

    Flux<Integer> fallbackFlux() {
        return Flux.range(100, 10)
                .delayElements(Duration.ofMillis(200));
    }

    Flux<Integer> getOrderNumbers() {
        return Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(5));
    }
}
