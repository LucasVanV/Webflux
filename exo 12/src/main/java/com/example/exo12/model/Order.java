package com.example.exo12.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.example.exo12.states.OrderStatus;

@Table("orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {

   @Id
    private Long id;

    @Column("customer_name")
    private String customerName;

    @Column("total_amount")
    private Double totalAmount;

    @Column("status")
    private OrderStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

}