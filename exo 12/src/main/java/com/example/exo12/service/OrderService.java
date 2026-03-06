package com.example.exo12.service;

import com.example.exo12.model.Order;
import com.example.exo12.repository.OrderRepository;
import com.example.exo12.states.OrderStatus;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> createOrder(Order order) {
        order.setId(null);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Mono<Order> updateOrderStatus(Long id, OrderStatus newStatus) {
        return orderRepository.findById(id)
                .flatMap(order -> {
                    order.setStatus(newStatus);
                    return orderRepository.save(order);
                });
    }

    public Mono<Void> deleteOrder(Long id) {
        return orderRepository.deleteById(id);
    }

    public Flux<Order> searchByStatus(String status) {
        return orderRepository.findByStatus(OrderStatus.valueOf(status));
    }

    public Flux<Order> getPagedOrders(int page, int size) {
        long offset = (long) page * size;
        return orderRepository.findPaged(size, offset);
    }

    public Flux<Order> getOrdersByCustomerName(String customerName) {
    return orderRepository.findByCustomerName(customerName);
}
}