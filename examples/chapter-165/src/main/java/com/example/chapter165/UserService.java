package com.example.chapter165;

import reactor.util.context.Context;

import java.util.Map;
import java.util.function.Function;

public class UserService {

    private static final Map<String, String> MAP = Map.of(
            "Sam", "Standard",
            "Mike", "Prime"
    );

    public static Function<Context, Context> userCategoryContext() {
        return context -> {
            System.out.println("userCategoryContext");
            String user = context.get("user");
            String category = MAP.get(user);
            return context.put("category", category);
        };
    }
}
