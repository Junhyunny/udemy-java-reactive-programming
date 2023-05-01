package com.example.chapter048;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;

class Chapter048Test {

    Faker faker = Faker.instance();

    @Test
    void example01() {

        Flux.create(fluxSink -> {

            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.complete();

            // onNExtDropped
            fluxSink.next(3);

        }).subscribe(new DefaultSubscriber());
    }

    @Test
    void example02() {

        Flux.create(fluxSink -> {
            String country;
            do {
                country = faker.country().name();
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada"));
            fluxSink.complete();
        }).subscribe(new DefaultSubscriber());
    }

    @Test
    void example03() {

        NameProducer nameProducer = new NameProducer();

        Flux.create(nameProducer).subscribe(new DefaultSubscriber());

        nameProducer.produce();
    }

    @Test
    void example04() {

        NameProducer nameProducer = new NameProducer();

        Flux.create(nameProducer).subscribe(new DefaultSubscriber());

        Runnable runnable = nameProducer::produce;
        for (int index = 0; index < 10; index++) {
            new Thread(runnable).start();
        }

        sleep(2000);
    }

    @Test
    void example05() {

        Flux.range(1, 10)
                .log()
//                .take(3) // cancel subscription
                .filter(number -> number <= 3) // not cancel just flow
                .log()
                .subscribe(System.out::println);
    }

    @Test
    void example06() {

        Flux.create(fluxSink -> {
                    String country;
                    do {
                        country = faker.country().name();
                        System.out.printf("emitting %s\n", country);
                        fluxSink.next(country);
                    } while (!country.equalsIgnoreCase("canada") && !fluxSink.isCancelled());
                    fluxSink.complete();
                })
                .take(3)
                .log()
                .subscribe(new DefaultSubscriber());
    }

    @Test
    void example07() {

        Flux.generate(synchronousSink -> {
                    String country = faker.country().name();
                    System.out.printf("emitting %s\n", country);
                    synchronousSink.next(country);

                    // error - synchronousSink can emmit only one item
                    // synchronousSink.next(faker.country().name());

                    // finish pipeline
                    // synchronousSink.complete();

                    // throw error
                    // synchronousSink.error(new RuntimeException("oops"));
                })
                .take(2)
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example08() {

        Flux.generate(synchronousSink -> {
                    String country = faker.country().name();
                    System.out.printf("emitting %s\n", country);
                    synchronousSink.next(country);
                    if (country.equalsIgnoreCase("canada")) {
                        synchronousSink.complete();
                    }
                })
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example09() {

        // how to count some number in loop?
        Flux.generate(
                        () -> 1,
                        (index, synchronousSink) -> {
                            String country = faker.country().name();
                            synchronousSink.next(country);
                            if (country.equalsIgnoreCase("canada") || index >= 10) {
                                synchronousSink.complete();
                            }
                            return index + 1;
                        }
                )
                // .take(4)
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example10() {

        NameProducer nameProducer = new NameProducer();

        Flux.push(nameProducer).subscribe(new DefaultSubscriber());

        Runnable runnable = nameProducer::produce;
        for (int index = 0; index < 10; index++) {
            new Thread(runnable).start();
        }

        sleep(2000);
    }

    @Test
    void example11() {

        FileReaderService readerService = new FileReaderService();

        Path path = Paths.get("src/main/resources/file01.txt");
        readerService.read(path)
                .map(str -> {
                    int number = faker.random().nextInt(0, 20);
                    if (number > 19) {
                        throw new RuntimeException("over 19");
                    }
                    return str;
                })
                .take(20)
                .subscribe(
                        System.out::println,
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
