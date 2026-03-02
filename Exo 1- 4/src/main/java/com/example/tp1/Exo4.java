package com.example.tp1;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class Exo4 {

    public static void main(String[] args) {

        Mono.zip(getFirstName(), getLastName())
                .map(tuple -> tuple.getT1() + " " + tuple.getT2())
                .map(String::toUpperCase)
                .subscribe(System.out::println);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static Mono<String> getFirstName() {
        return Mono.delay(Duration.ofMillis(500))
                .map(t -> "Jean");
    }

    static Mono<String> getLastName() {
        return Mono.delay(Duration.ofMillis(800))
                .map(t -> "Dupont");
    }
}