package com.example.chapter165;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

class Chapter165Tests {

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

        getWelcomeMessage()
                .contextWrite(Context.of("user", "Jake"))
                .contextWrite(Context.of("user", "Sam"))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example02() {

        getWelcomeMessage()
                .contextWrite(context -> context.put("user", "Mike"))
                .subscribe(
                        value -> System.out.printf("received: %s\n", value),
                        error -> System.out.printf("error: %s\n", error.getMessage()),
                        () -> System.out.println("completed")
                );
    }

    @Test
    void example03() {

        BookService.getBook()
                .contextWrite(UserService.userCategoryContext())
                .contextWrite(Context.of("user", "Sam"))
                .repeat(1)
                .subscribe(
                        value -> System.out.printf("received-1: %s\n", value),
                        error -> System.out.printf("error-1: %s\n", error.getMessage()),
                        () -> System.out.println("completed-1")
                );


        BookService.getBook()
                .contextWrite(UserService.userCategoryContext())
                .contextWrite(Context.of("user", "Mike"))
                .repeat(2)
                .subscribe(
                        value -> System.out.printf("received-2: %s\n", value),
                        error -> System.out.printf("error-2: %s\n", error.getMessage()),
                        () -> System.out.println("completed-2")
                );
    }
}
