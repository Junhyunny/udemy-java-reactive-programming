package com.example.chapter165;

import com.github.javafaker.Faker;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BookService {

    private static final Faker faker = Faker.instance();
    private static final Map<String, Integer> MAP = new HashMap<>();

    static {
        MAP.put("Standard", 2);
        MAP.put("Prime", 3);
    }

    public static Mono<String> getBook() {
        return Mono.deferContextual(context -> {
                    if (context.get("allow")) {
                        return Mono.just(faker.book().title());
                    }
                    return Mono.error(new RuntimeException("not allowed"));
                })
                .contextWrite(rateLimiterContext());
    }

    private static Function<Context, Context> rateLimiterContext() {
        return context -> {
            System.out.println("rateLimiterContext");
            if (context.hasKey("category")) {
                String category = context.get("category");
                int attempts = MAP.get(category);
                if (attempts > 0) {
                    MAP.put(category, attempts - 1);
                    return context.put("allow", true);
                }
            }
            return context.put("allow", false);
        };
    }
}
