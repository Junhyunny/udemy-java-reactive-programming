package com.example.chapter009;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.example.chapter009.TestUtil.*;

class Chapter09Test {

    Function<Integer, Integer> function = number -> {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return number * 2;
    };

    @Test
    void example01() {

        Stream<Integer> stream = Stream.of(1).map(function);

        stream.forEach(System.out::println);
    }

    @Test
    void example02() {

        // publisher
        Mono<Integer> mono = Mono.just(1);

        // happen after subscribe
        mono.subscribe(number -> System.out.printf("Received: %s\n", number));
    }

    @Test
    void example03() {

        Mono<String> mono = Mono.just("ball");

        // subscribe method in reactor
        // onNext - Consumer<T>
        // onError - Consumer<Throwable>
        // onComplete - Runnable
        mono.subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    @Test
    void example04() {

        Mono<Integer> mono = Mono.just("ball").map(String::length).map(length -> length / 0);

        // subscribe method in reactor
        // onNext - Consumer<T>
        // onError - Consumer<Throwable>
        // onComplete - Runnable
        mono.subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    @Test
    void example05() {

        useRepository(1).subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    @Test
    void example06() {

        useRepository(2).subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    @Test
    void example07() {

        useRepository(3).subscribe(Util.onNext(), Util.onError(), Util.onComplete());
    }

    @Test
    void example08() {

        Supplier<String> supplier = TestUtil::getName;

        Mono.fromSupplier(supplier).subscribe(Util.onNext());
    }

    @Test
    void example09() {

        Callable<String> callable = TestUtil::getName;

        Mono.fromCallable(callable).subscribe(Util.onNext());
    }

    @Test
    void example10() {

        // thread is not blocked
        getNameMono();
        // some pipeline is subscribed then main thread is blocked
        getNameMono().subscribe(Util.onNext());
        getNameMono();
    }

    @Test
    void example11() {

        getNameMono();
        getNameMono()
                // make subscribe asynchronously
                .subscribeOn(Schedulers.boundedElastic()).subscribe(Util.onNext());
        getNameMono();


        Util.sleep(4000);
    }

    @Test
    void example12() {

        getNameMono();
        System.out.println(getNameMono()
                // make subscribe asynchronously
                .subscribeOn(Schedulers.boundedElastic())
                // make main thread block
                // subscribe internally and get return value
                .block());
        getNameMono();
    }

    @Test
    void example13() {

        Mono.fromFuture(getNameCompletableFuture()).subscribe(Util.onNext());


        Util.sleep(1000);
    }

    @Test
    void example14() {

        Mono.fromRunnable(timeConsumingProcess())
                .subscribe(
                        Util.onNext(),
                        Util.onError(),
                        Util.onComplete()
                );
    }
}
