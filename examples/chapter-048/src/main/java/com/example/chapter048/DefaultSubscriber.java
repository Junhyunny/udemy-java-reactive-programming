package com.example.chapter048;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DefaultSubscriber implements Subscriber<Object> {

    private final String name;

    public DefaultSubscriber() {
        this.name = "default";
    }

    public DefaultSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        System.out.printf("%s received(%s): %s\n", name, Thread.currentThread().getName(), o);
    }

    @Override
    public void onError(Throwable t) {
        System.out.printf("%s error: %s\n", name, t.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.printf("%s completed\n", name);
    }
}
