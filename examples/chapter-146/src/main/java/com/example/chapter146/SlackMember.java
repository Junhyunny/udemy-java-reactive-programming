package com.example.chapter146;

import lombok.Data;

import java.util.function.Consumer;

@Data
public class SlackMember {

    private String name;
    private Consumer<String> messageConsumer;

    public SlackMember(String name) {
        this.name = name;
    }

    public void receives(String message) {
        System.out.println(message);
    }

    public void says(String message) {
        messageConsumer.accept(message);
    }
}
