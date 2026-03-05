package com.example.tp1;

import com.example.tp1.model.*;
import com.example.tp1.repository.ProductRepository;
import com.example.tp1.service.OrderService;
import com.example.tp1.status.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

// Pour les assertions avec tolerance sur BigDecimal
import org.assertj.core.data.Offset;

/**
 * Tests unitaires pour OrderService.
 * Utilise StepVerifier pour tester les Mono réactifs.
 */
@DisplayName("OrderService - Tests du pipeline réactif")
class OrderServiceTest {

    private OrderService orderService;

    // ==================== SETUP ====================

    @BeforeEach
    void setUp() {
        // Utilise le ProductRepository réel avec les données de test
        ProductRepository productRepository = new ProductRepository();
        orderService = new OrderService(productRepository);
    }

    // ==================== TEST 1: CAS NOMINAL ====================

    @Test
    @DisplayName("Test 1: processOrder - Cas nominal avec IDs valides existants")
    void test_processOrderSuccess() {
        // Arrange
        OrderRequest request = new OrderRequest(
                Arrays.asList("P1", "P2"),
                "CUST001"
        );

        // Act & Assert
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // Vérifier que l'orderId est généré
                    assertThat(order.getOrderId()).isNotNull().isNotBlank();

                    // Vérifier que les produits sont présents
                    assertThat(order.getProducts()).hasSize(2);

                    // Vérifier que les IDs de produits correspondent
                    assertThat(order.getProductIds())
                            .containsExactlyInAnyOrder("P1", "P2");

                    // Vérifier que le totalPrice est calculé correctement
                    // P1 = 249.99 * 0.95 = 237.4905
                    // P2 = 59.90 * 0.95 = 56.905
                    // Total ~= 294.3955
                    assertThat(order.getTotalPrice())
                            .isGreaterThan(BigDecimal.ZERO)
                            .isLessThan(new BigDecimal("300"));

                    // Vérifier que la remise est appliquée
                    assertThat(order.getDiscountApplied()).isTrue();

                    // Vérifier que le statut est COMPLETED
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                    System.out.println("✓ Test succès: Order créée avec " + order.getProducts().size() + " produits");
                })
                .verifyComplete();
    }

    // ==================== TEST 2: IDS INVALIDES MÉLANGÉS ====================

    @Test
    @DisplayName("Test 2: processOrder - IDs invalides mélangés (valides + invalides)")
    void test_processOrderWithInvalidIds() {
        // Arrange
        // P1 existe, INVALID1 n'existe pas, P2 existe, INVALID2 n'existe pas
        OrderRequest request = new OrderRequest(
                Arrays.asList("P1", "INVALID1", "P2", "INVALID2"),
                "CUST002"
        );

        // Act & Assert
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // Vérifier que seuls les IDs valides sont traités
                    // INVALID1 et INVALID2 ne doivent pas être présents
                    assertThat(order.getProductIds())
                            .doesNotContain("INVALID1", "INVALID2")
                            .containsExactlyInAnyOrder("P1", "P2");

                    // Vérifier que le nombre de produits est inférieur à la taille originale
                    assertThat(order.getProducts().size()).isLessThan(4).isEqualTo(2);

                    // Vérifier que l'Order est toujours créée
                    assertThat(order.getOrderId()).isNotNull();
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                    System.out.println("✓ Test IDs invalides: " + order.getProducts().size() + " produits valides récupérés");
                })
                .verifyComplete();
    }

    // ==================== TEST 3: PRODUITS HORS STOCK ====================

    @Test
    @DisplayName("Test 3: processOrder - Produits avec stock = 0 sont ignorés")
    void test_processOrderWithoutStock() {
        // Arrange
        // P5 a un stock de 0, P1 en a 12
        OrderRequest request = new OrderRequest(
                Arrays.asList("P1", "P5"),
                "CUST003"
        );

        // Act & Assert
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // P5 ne doit pas être inclus (stock = 0)
                    assertThat(order.getProductIds())
                            .containsExactly("P1")
                            .doesNotContain("P5");

                    // Vérifier que seul P1 est dans les produits
                    assertThat(order.getProducts()).hasSize(1);
                    assertThat(order.getProducts().get(0).getProduct().getId()).isEqualTo("P1");

                    // Vérifier que le prix total correspond à P1 avec remise
                    BigDecimal p1DiscountedPrice = new BigDecimal("249.99").multiply(new BigDecimal("0.95"));
                    assertThat(order.getTotalPrice())
                            .isCloseTo(p1DiscountedPrice, Offset.offset(new BigDecimal("0.01")));

                    System.out.println("✓ Test stock: Produits hors stock ignorés correctement");
                })
                .verifyComplete();
    }

    // ==================== TEST 4: VÉRIFICATION DES REMISES ====================

    @Test
    @DisplayName("Test 4: processOrder - Remises correctes (électro 10%, autres 5%)")
    void test_processOrderWithDiscounts() {
        // Arrange
        ProductRepository customRepo = new ProductRepository() {
            @Override
            public Mono<Product> findById(String id) {
                return Mono.defer(() -> {
                    Product product = null;
                    if ("ELEC1".equals(id)) {
                        product = Product.builder()
                                .id("ELEC1")
                                .name("Laptop")
                                .price(new BigDecimal("1000.00"))
                                .stock(5)
                                .category("Électronique")  // => 10% discount
                                .build();
                    } else if ("OTHER1".equals(id)) {
                        product = Product.builder()
                                .id("OTHER1")
                                .name("Accessoire")
                                .price(new BigDecimal("100.00"))
                                .stock(10)
                                .category("Autre")  // => 5% discount
                                .build();
                    }
                    return Mono.justOrEmpty(product)
                            .delayElement(Duration.ofMillis(10));
                });
            }
        };

        OrderService customService = new OrderService(customRepo);
        OrderRequest request = new OrderRequest(
                Arrays.asList("ELEC1", "OTHER1"),
                "CUST004"
        );

        // Act & Assert
        StepVerifier.create(customService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getProducts()).hasSize(2);

                    // Vérifier la remise électronique (10%)
                    ProductWithPrice electronics = order.getProducts().stream()
                            .filter(p -> "ELEC1".equals(p.getProduct().getId()))
                            .findFirst()
                            .orElseThrow();
                    assertThat(electronics.getDiscountPercentage()).isEqualTo(10);
                    assertThat(electronics.getFinalPrice()).isCloseTo(
                            new BigDecimal("900.00"),
                            Offset.offset(new BigDecimal("0.01"))
                    );

                    // Vérifier la remise autres (5%)
                    ProductWithPrice other = order.getProducts().stream()
                            .filter(p -> "OTHER1".equals(p.getProduct().getId()))
                            .findFirst()
                            .orElseThrow();
                    assertThat(other.getDiscountPercentage()).isEqualTo(5);
                    assertThat(other.getFinalPrice()).isCloseTo(
                            new BigDecimal("95.00"),
                            Offset.offset(new BigDecimal("0.01"))
                    );

                    // Vérifier que totalPrice = sum(finalPrice)
                    BigDecimal expectedTotal = electronics.getFinalPrice()
                            .add(other.getFinalPrice());
                    assertThat(order.getTotalPrice()).isCloseTo(
                            expectedTotal,
                            Offset.offset(new BigDecimal("0.01"))
                    );

                    System.out.println("✓ Test remises: Électro 10% = " + electronics.getFinalPrice()
                            + ", Autre 5% = " + other.getFinalPrice());
                })
                .verifyComplete();
    }

    // ==================== TEST 5: TIMEOUT ====================

    @Test
    @DisplayName("Test 5: processOrder - Timeout après 5s")
    void test_processOrderTimeout() {
        // Arrange: Repository avec délai très long (>5s)
        ProductRepository slowRepo = new ProductRepository() {
            @Override
            public Mono<Product> findById(String id) {
                return Mono.defer(() -> {
                    Product product = Product.builder()
                            .id("SLOW1")
                            .name("SlowProduct")
                            .price(new BigDecimal("100.00"))
                            .stock(1)
                            .category("Test")
                            .build();
                    return Mono.just(product)
                            .delayElement(Duration.ofSeconds(6));  // > 5 secondes
                });
            }
        };

        OrderService slowService = new OrderService(slowRepo);
        OrderRequest request = new OrderRequest(
                Arrays.asList("SLOW1"),
                "CUST005"
        );

        // Act & Assert
        StepVerifier.create(slowService.processOrder(request))
                .assertNext(order -> {
                    // Dû au timeout, une Order avec status FAILED doit être créée
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                    assertThat(order.getProductIds()).isEmpty();

                    System.out.println("✓ Test timeout: Order créée avec status FAILED après 5s");
                })
                .verifyComplete();
    }

    // ==================== TEST 6: GESTION D'ERREURS ====================

    @Test
    @DisplayName("Test 6: processOrder - Gestion d'erreurs avec onErrorResume")
    void test_processOrderWithErrors() {
        // Arrange: Repository qui alterne entre succès et erreurs
        ProductRepository errorRepo = new ProductRepository() {
            private final AtomicInteger callCount = new AtomicInteger(0);

            @Override
            public Mono<Product> findById(String id) {
                return Mono.defer(() -> {
                    int count = callCount.incrementAndGet();
                    // 50% d'erreurs: les appels pairs échouent, les impairs réussissent
                    if (count % 2 == 0) {
                        return Mono.error(new RuntimeException("DB error on call #" + count));
                    }

                    Product product = Product.builder()
                            .id("P" + count)
                            .name("Product " + count)
                            .price(new BigDecimal("100.00"))
                            .stock(10)
                            .category("Test")
                            .build();
                    return Mono.just(product)
                            .delayElement(Duration.ofMillis(10));
                });
            }
        };

        OrderService errorService = new OrderService(errorRepo);
        OrderRequest request = new OrderRequest(
                Arrays.asList("ID1", "ID2", "ID3", "ID4"),
                "CUST006"
        );

        // Act & Assert
        StepVerifier.create(errorService.processOrder(request))
                .assertNext(order -> {
                    // Même avec des erreurs (50%), l'Order est créée
                    assertThat(order.getOrderId()).isNotNull();

                    // Les produits qui ont réussi sont inclus
                    // Les appels 1, 3 réussissent => 2 produits
                    assertThat(order.getProducts().size()).isGreaterThan(0);

                    // Vérifier que le statut est COMPLETED (car au moins un produit a réussi)
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                    System.out.println("✓ Test erreurs: " + order.getProducts().size() + " produits malgré les erreurs");
                })
                .verifyComplete();
    }

    // ==================== TEST BONUS: VALIDATION ENTRÉES ====================

    @Test
    @DisplayName("Test Bonus: processOrder - Rejet des requêtes invalides (null productIds)")
    void test_processOrderWithNullProductIds() {
        // Arrange
        OrderRequest request = new OrderRequest(null, "CUST007");

        // Act & Assert
        // La validation échoue, mais le onErrorResume global crée une Order avec status FAILED
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                })
                .verifyComplete();

        System.out.println("✓ Test validation: Order créée avec status FAILED pour productIds null");
    }

    @Test
    @DisplayName("Test Bonus: processOrder - Rejet des requêtes invalides (empty productIds)")
    void test_processOrderWithEmptyProductIds() {
        // Arrange
        OrderRequest request = new OrderRequest(new ArrayList<>(), "CUST008");

        // Act & Assert
        // La validation échoue, mais le onErrorResume global crée une Order avec status FAILED
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                })
                .verifyComplete();

        System.out.println("✓ Test validation: Order créée avec status FAILED pour productIds vides");
    }

    @Test
    @DisplayName("Test Bonus: processOrder - Rejet des requêtes invalides (null customerId)")
    void test_processOrderWithNullCustomerId() {
        // Arrange
        OrderRequest request = new OrderRequest(Arrays.asList("P1"), null);

        // Act & Assert
        // La validation échoue, mais le onErrorResume global crée une Order avec status FAILED
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                })
                .verifyComplete();

        System.out.println("✓ Test validation: Order créée avec status FAILED pour customerId null");
    }

    // ==================== TEST BONUS: LIMIT TAKE(100) ====================

    @Test
    @DisplayName("Test Bonus: processOrder - Limite à 100 produits avec take(100)")
    void test_processOrderWithMoreThan100Products() {
        // Arrange
        ProductRepository limitRepo = new ProductRepository() {
            @Override
            public Mono<Product> findById(String id) {
                return Mono.defer(() -> {
                    Product product = Product.builder()
                            .id(id)
                            .name("Product " + id)
                            .price(new BigDecimal("10.00"))
                            .stock(1)
                            .category("Test")
                            .build();
                    return Mono.just(product).delayElement(Duration.ofMillis(1));
                });
            }
        };

        OrderService limitService = new OrderService(limitRepo);

        // Créer une liste avec 150 IDs
        List<String> manyIds = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            manyIds.add("P" + i);
        }

        OrderRequest request = new OrderRequest(manyIds, "CUST009");

        // Act & Assert
        StepVerifier.create(limitService.processOrder(request))
                .assertNext(order -> {
                    // Vérifier que le nombre de produits est limité à 100
                    assertThat(order.getProducts().size()).isLessThanOrEqualTo(100);

                    System.out.println("✓ Test take(100): Limité à " + order.getProducts().size() + " produits sur 150");
                })
                .verifyComplete();
    }

    // ==================== HELPER ====================

    // Aucune méthode helper nécessaire - Offset.offset() est suffisant
}
