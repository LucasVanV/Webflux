package com.example.tp1.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithPrice {

    @NotNull
    private Product product;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal originalPrice;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal finalPrice;
}
