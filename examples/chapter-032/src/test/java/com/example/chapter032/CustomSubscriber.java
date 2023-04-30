package com.example.chapter032;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicReference;

public class CustomSubscriber implements Subscriber<Integer> {

    private final AtomicReference<Subscription> atomicReference;

    public CustomSubscriber(AtomicReference<Subscription> atomicReference) {
        this.atomicReference = atomicReference;
    }

    @Override
    public void onSubscribe(Subscription s) {
        System.out.printf("Received: %s\n", s);
        atomicReference.set(s);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.printf("onNext: %s\n", integer);
    }

    @Override
    public void onError(Throwable t) {
        System.out.printf("onError: %s\n", t.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
