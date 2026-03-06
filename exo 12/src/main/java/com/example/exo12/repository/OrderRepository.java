package com.example.exo12.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.exo12.model.Order;
import com.example.exo12.states.OrderStatus;

import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findByStatus(OrderStatus status);

    @Query("SELECT * FROM orders LIMIT :size OFFSET :offset")
    Flux<Order> findPaged(int size, long offset);
}