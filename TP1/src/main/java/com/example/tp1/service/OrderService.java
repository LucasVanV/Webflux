package com.example.tp1.service;

import com.example.tp1.exception.InvalidOrderException;
import com.example.tp1.model.*;
import com.example.tp1.repository.ProductRepository;
import com.example.tp1.status.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;

    public Mono<Order> processOrder(OrderRequest request) {
        
        log.info("┌─── Début du traitement de commande ───");
        log.info("  Requête reçue : customerId={}, # produits={}", 
                 request.getCustomerId(), 
                 request.getProductIds() != null ? request.getProductIds().size() : 0);

        return Mono.defer(() -> {
            if (request == null || request.getProductIds() == null || request.getProductIds().isEmpty()) {
                log.error("❌ Requête invalide : productIds vides ou null");
                return Mono.error(new InvalidOrderException("La requête ne doit pas être vide, productIds requis"));
            }
            if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
                log.error("❌ Requête invalide : customerId null ou vide");
                return Mono.error(new InvalidOrderException("customerId ne doit pas être null ou vide"));
            }
            log.info("✓ Validation réussie");
            return Mono.just(request);
        })

        .flatMapMany(req -> {
            log.info("│ Conversion de la liste en flux (filtering + take(100))");
            return Flux.fromIterable(req.getProductIds())
                    .doOnNext(id -> log.debug("  → Produit ID: {}", id))
                    .filter(id -> id != null && !id.isBlank())
                    .take(100)
                    .doOnNext(id -> log.info("  ✓ Produit {} accepté (après filter)", id))
                    .doOnComplete(() -> log.info("│ Fin de la conversion du flux"));
        })

        .flatMap(productId -> {
            log.info("│ Recherche du produit ID: {}", productId);
            return productRepository.findById(productId)
                    .doOnNext(product -> log.info("  ✓ Produit trouvé: {} ({})", product.getName(), product.getId()))
                    .doOnError(error -> log.warn("  ⚠ Erreur lors de la recherche: {}", error.getMessage()))
                    .onErrorResume(error -> {
                        log.warn("  ⚠ Produit {} non trouvé, ignoré (onErrorResume)", productId);
                        return Mono.empty();
                    });
        })

        .filter(product -> {
            boolean hasStock = product.getStock() > 0;
            if (hasStock) {
                log.info("│ ✓ Produit {} - Stock OK ({} unités)", product.getName(), product.getStock());
            } else {
                log.warn("│ ✗ Produit {} - Stock ZERO, ignoré", product.getName());
            }
            return hasStock;
        })

        .map(product -> {
            log.info("│ Calcul de remise pour {}", product.getName());
            
            int discountPercentage;
            if (product.getCategory() != null && "Électronique".equalsIgnoreCase(product.getCategory())) {
                discountPercentage = 10;
                log.info("  → Catégorie: {} → 10% discount", product.getCategory());
            } else {
                discountPercentage = 5;
                log.info("  → Catégorie: {} → 5% discount", product.getCategory());
            }
            
            BigDecimal discount = product.getPrice()
                    .multiply(new BigDecimal(discountPercentage))
                    .divide(new BigDecimal(100));
            BigDecimal finalPrice = product.getPrice().subtract(discount);
            
            log.info("  ✓ Prix original: {}, discount: {}, prix final: {}", 
                     product.getPrice(), discount, finalPrice);
            
            return ProductWithPrice.builder()
                    .product(product)
                    .originalPrice(product.getPrice())
                    .discountPercentage(discountPercentage)
                    .finalPrice(finalPrice)
                    .build();
        })

        .collectList()
        .map(productsWithPrice -> {
            log.info("│ Création de l'Order finale");
            log.info("  → {} produits avec remise appliquée", productsWithPrice.size());
            
            BigDecimal totalPrice = productsWithPrice.stream()
                    .map(ProductWithPrice::getFinalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.info("  → Total: {}", totalPrice);
            
            return Order.builder()
                    .orderId(UUID.randomUUID().toString())
                    .productIds(productsWithPrice.stream()
                            .map(pwp -> pwp.getProduct().getId())
                            .toList())
                    .products(productsWithPrice)
                    .totalPrice(totalPrice)
                    .discountApplied(!productsWithPrice.isEmpty())
                    .createdAt(LocalDateTime.now())
                    .status(OrderStatus.COMPLETED)
                    .build();
        })

        .timeout(java.time.Duration.ofSeconds(5))
        .doOnNext(order -> log.info("✓ Order créée avec succès: {}", order.getOrderId()))

        .onErrorResume(error -> {
            log.error("⚠ ERREUR durante le traitement: {}", error.getMessage(), error);
            
            Order failedOrder = Order.builder()
                    .orderId(UUID.randomUUID().toString())
                    .productIds(java.util.List.of())
                    .products(java.util.List.of())
                    .totalPrice(BigDecimal.ZERO)
                    .discountApplied(false)
                    .createdAt(LocalDateTime.now())
                    .status(OrderStatus.FAILED)
                    .build();
            
            log.error("└─── Order créée avec status FAILED: {}", failedOrder.getOrderId());
            return Mono.just(failedOrder);
        })

        .doFinally(signalType -> {
            log.info("└─── Fin du traitement de commande (signalType: {}) ───", signalType);
        });
    }
}
