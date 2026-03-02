package com.example.tp1;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class Exo2 {

    public static void main(String[] args) {

        AtomicInteger attempt = new AtomicInteger(0);

        Mono<String> unreliableApi = Mono.fromCallable(() -> {
            int currentAttempt = attempt.incrementAndGet();

            if (currentAttempt < 3) {
                System.out.println("Tentative " + currentAttempt + ": Erreur réseau");
                throw new RuntimeException("Erreur réseau");
            }

            System.out.println("Tentative " + currentAttempt + ": Succès!");
            return "Succès!";
        });

        unreliableApi
                .retry(2)
                .subscribe(
                        result -> System.out.println("Résultat final : " + result),
                        error -> System.err.println("Échec définitif : " + error.getMessage())
                );
    }
}
