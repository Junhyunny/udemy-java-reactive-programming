package com.example.chapter170;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;

import java.time.Duration;

class Chapter170Tests {

    Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(context -> {
            if (context.hasKey("user")) {
                return Mono.just("Welcome " + context.get("user"));
            }
            return Mono.error(new RuntimeException("unauthenticated"));
        });
    }

    @Test
    void example01() {

        Flux<Integer> flux = Flux.just(1, 2, 3);


        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void example02() {

        Flux<Integer> flux = Flux.just(1, 2, 3);


        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void example03() {

        Flux<Integer> flux = Flux.just(1, 2, 3);
        Flux<Integer> errorFlux = Flux.error(new RuntimeException("oops"));
        Flux<Integer> concatFlux = Flux.concat(flux, errorFlux);


        StepVerifier.create(concatFlux)
                .expectNext(1, 2, 3)
                .verifyError();
    }

    @Test
    void example04() {

        Flux<Integer> flux = Flux.just(1, 2, 3);
        Flux<Integer> errorFlux = Flux.error(new IllegalArgumentException("oops"));
        Flux<Integer> concatFlux = Flux.concat(flux, errorFlux);


        StepVerifier.create(concatFlux)
                .expectNext(1, 2, 3)
                .verifyError(IllegalArgumentException.class);
    }

    @Test
    void example05() {

        Flux<Integer> flux = Flux.just(1, 2, 3);
        Flux<Integer> errorFlux = Flux.error(new IllegalArgumentException("oops"));
        Flux<Integer> concatFlux = Flux.concat(flux, errorFlux);


        StepVerifier.create(concatFlux)
                .expectNext(1, 2, 3)
                .verifyErrorMessage("oops");
    }

    @Test
    void example06() {

        Flux<Integer> flux = Flux.range(1, 50);

        StepVerifier.create(flux)
                // error
                // .expectNext(1, 2, 3)
                .expectNextCount(50)
                // error
                // .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void example07() {

        Flux<Integer> flux = Flux.range(1, 50)
                // .map(number -> number * 2)
                ;

        StepVerifier.create(flux)
                .thenConsumeWhile(number -> number < 100)
                .verifyComplete();
    }

    @Test
    void example08() {

        Mono<String> mono = Mono.fromSupplier(() -> "hello world")
                .delayElement(Duration.ofSeconds(3));


        StepVerifier.create(mono)
                .assertNext(Assertions::assertNotNull)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    Flux<String> timeConsumingFlux() {
        return Flux.range(1, 4)
                .delayElements(Duration.ofSeconds(5))
                .map(number -> {
                    System.out.println(number);
                    return number + "-a";
                });
    }

    @Test
    void example09() {

        StepVerifier
                .withVirtualTime(this::timeConsumingFlux)
                .thenAwait(Duration.ofSeconds(30))
                .expectNext("1-a", "2-a", "3-a", "4-a")
                .verifyComplete();
    }

    @Test
    void example10() {

        StepVerifier
                .withVirtualTime(this::timeConsumingFlux)
                .expectSubscription() // subscription is and event
                .expectNoEvent(Duration.ofSeconds(4))
                .thenAwait(Duration.ofSeconds(30))
                .expectNext("1-a", "2-a", "3-a", "4-a")
                .verifyComplete();
    }

    @Test
    void example11() {

        Flux<String> flux = Flux.just("a", "b", "c");


        // create scenario name for test
        StepVerifierOptions options = StepVerifierOptions.create().scenarioName("alphabets test");

        StepVerifier.create(flux, options)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void example12() {

        Flux<String> flux = Flux.just("a", "b", "c");


        StepVerifier.create(flux)
                .expectNext("a").as("a-test")
                .expectNext("b").as("b-test")
                .expectNext("d").as("c-test")
                .verifyComplete();
    }

    @Test
    void example13() {

        StepVerifier.create(getWelcomeMessage())
                .verifyError(RuntimeException.class);
    }

    @Test
    void example14() {

        StepVerifierOptions options = StepVerifierOptions.create().withInitialContext(
                Context.of("user", "Sam")
        );

        StepVerifier.create(getWelcomeMessage(), options)
                .expectNext("Welcome Sam")
                .verifyComplete();
    }
}
