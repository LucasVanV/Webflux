package com.example.tp1.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.tp1.status.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @NotNull
    @Builder.Default
    private String orderId = UUID.randomUUID().toString();

    @Builder.Default
    private List<String> productIds = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<ProductWithPrice> products = new ArrayList<>();

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @NotNull
    @Builder.Default
    private Boolean discountApplied = Boolean.FALSE;

    @NotNull
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;
}