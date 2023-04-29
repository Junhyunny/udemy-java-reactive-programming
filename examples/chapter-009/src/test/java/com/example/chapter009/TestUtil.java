package com.example.chapter009;

import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static com.example.chapter009.Util.faker;
import static com.example.chapter009.Util.sleep;

public class TestUtil {

    public static Mono<String> useRepository(int userId) {
        if (userId == 1) {
            return Mono.just(faker().name().fullName());
        } else if (userId == 2) {
            return Mono.empty();
        }
        return Mono.error(new RuntimeException(String.format("Not found user when id: %s", userId)));
    }

    public static String getName() {
        System.out.println("Generating name...");
        return faker().name().fullName();
    }

    public static Mono<String> getNameMono() {
        System.out.println("called method");
        // just return publisher form building pipeline
        // executed lazily
        return Mono.fromSupplier(() -> {
            System.out.println("Generating name...");
            sleep(3000);
            return faker().name().fullName();
        }).map(String::toUpperCase);
    }

    public static CompletableFuture<String> getNameCompletableFuture() {
        return CompletableFuture.supplyAsync(() -> faker().name().fullName());
    }

    public static Runnable timeConsumingProcess() {
        return () -> {
            Util.sleep(3000);
            System.out.println("operation completed");
        };
    }
}
