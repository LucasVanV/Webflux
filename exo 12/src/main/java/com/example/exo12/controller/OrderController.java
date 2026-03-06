package com.example.exo12.controller;

import com.example.exo12.dto.OrderStatusUpdateRequest;
import com.example.exo12.model.Order;
import com.example.exo12.service.OrderService;
import com.example.exo12.states.OrderStatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request) {

        return orderService.updateOrderStatus(id, OrderStatus.valueOf(request.getStatus()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .flatMap(existingOrder ->
                        orderService.deleteOrder(id)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<Order> searchByStatus(@RequestParam String status) {
        return orderService.searchByStatus(status);
    }

    @GetMapping("/paged")
    public Flux<Order> getPagedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return orderService.getPagedOrders(page, size);
    }

    @GetMapping("/customer/{customerName}")
    public Flux<Order> getOrdersByCustomerName(@PathVariable String customerName) {
        return orderService.getOrdersByCustomerName(customerName);
    }
}