package com.example.exo13.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.exo13.model.Product;

import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findByNameContainingIgnoreCase(String name);
}