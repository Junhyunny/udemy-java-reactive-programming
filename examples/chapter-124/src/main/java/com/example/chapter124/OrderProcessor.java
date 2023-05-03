package com.example.chapter124;

import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.function.Function;

public class OrderProcessor {

    public static Function<Flux<PurchaseOrder>, Flux<PurchaseOrder>> automotiveProcessing() {
        return flux -> flux
                .doOnNext(
                        purchaseOrder -> purchaseOrder.setPrice(purchaseOrder.getPrice().multiply(BigDecimal.valueOf(1.1)))
                )
                .doOnNext(
                        purchaseOrder -> purchaseOrder.setItem("{{ " + purchaseOrder.getItem() + " }}")
                );
    }

    public static Function<Flux<PurchaseOrder>, Flux<PurchaseOrder>> kidsProcessing() {
        return flux -> flux
                .doOnNext(
                        purchaseOrder -> purchaseOrder.setPrice(purchaseOrder.getPrice().multiply(BigDecimal.valueOf(0.5)))
                )
                .flatMap(purchaseOrder -> Flux.just(purchaseOrder, getFreeKidsOrder()));
    }

    private static PurchaseOrder getFreeKidsOrder() {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPrice(BigDecimal.ZERO);
        purchaseOrder.setItem("FREE-" + purchaseOrder.getItem());
        purchaseOrder.setCategory("Kids");
        return purchaseOrder;
    }
}
