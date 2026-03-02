package com.example.tp1.repository;

import com.example.tp1.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {

    private static final Duration DB_LATENCY = Duration.ofMillis(100);

    // 5 produits en dur (en mémoire)
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public ProductRepository() {
        seed();
    }

    private void seed() {
        // IDs simples pour les tests
        products.put("P1", Product.builder()
                .id("P1")
                .name("Casque intégral")
                .price(new BigDecimal("249.99"))
                .stock(12)
                .category("Equipement")
                .build());

        products.put("P2", Product.builder()
                .id("P2")
                .name("Gants racing")
                .price(new BigDecimal("59.90"))
                .stock(30)
                .category("Equipement")
                .build());

        products.put("P3", Product.builder()
                .id("P3")
                .name("Combinaison FIA")
                .price(new BigDecimal("399.00"))
                .stock(5)
                .category("Equipement")
                .build());

        products.put("P4", Product.builder()
                .id("P4")
                .name("Huile moteur 5W30")
                .price(new BigDecimal("39.99"))
                .stock(50)
                .category("Entretien")
                .build());

        products.put("P5", Product.builder()
                .id("P5")
                .name("Kit plaquettes frein")
                .price(new BigDecimal("129.00"))
                .stock(0)
                .category("Pièces")
                .build());
    }

    /**
     * Retourne Mono.empty() si le produit n'existe pas.
     * Ajoute une latence de 100ms pour simuler la BDD.
     */
    public Mono<Product> findById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(products.get(id)))
                .delayElement(DB_LATENCY);
    }

    /**
     * Récupère une liste de produits à partir d'une liste d'IDs.
     * Doit utiliser flatMapIterable.
     * Ajoute une latence de 100ms *par élément* via findById().
     */
    public Flux<Product> findByIds(List<String> ids) {
        if (ids == null) {
            return Flux.empty();
        }
        return Mono.just(ids)
                .flatMapIterable(list -> list)   // <-- flatMapIterable demandé
                .flatMap(this::findById);        // findById inclut déjà le delay de 100ms
    }

    public Mono<Integer> getStock(String productId) {
        return Mono.defer(() -> {
                    Product p = products.get(productId);
                    return (p == null) ? Mono.<Integer>empty() : Mono.just(p.getStock());
                })
                .delayElement(DB_LATENCY);
    }
}