package com.example.chapter146;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Data
public class SlackRoom {

    private String name;
    private Sinks.Many<SlackMessage> sink;
    private Flux<SlackMessage> flux;

    public SlackRoom(String name) {
        this.name = name;
        this.sink = Sinks.many().replay().all();
        this.flux = sink.asFlux();
    }

    public void joinRoom(SlackMember sender) {
        System.out.println(sender.getName() + " joined " + this.name);
        subscribe(sender);
        sender.setMessageConsumer(
                message -> postMessage(message, sender)
        );
    }

    private void subscribe(SlackMember receiver) {
        flux.filter(slackMessage -> !slackMessage.getSender().equals(receiver.getName()))
                .doOnNext(slackMessage -> slackMessage.setReceiver(receiver.getName()))
                .map(SlackMessage::toString)
                .subscribe(receiver::receives);
    }

    private void postMessage(String message, SlackMember sender) {
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setSender(sender.getName());
        slackMessage.setMessage(message);
        sink.tryEmitNext(slackMessage);
    }
}
