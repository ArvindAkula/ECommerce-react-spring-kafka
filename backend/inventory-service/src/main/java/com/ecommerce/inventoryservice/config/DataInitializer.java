package com.ecommerce.inventoryservice.config;

import com.ecommerce.inventoryservice.model.Product;
import com.ecommerce.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final ProductRepository productRepository;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting database initialization...");
            
            if (productRepository.count() == 0) {
                log.info("No products found. Adding sample products.");
                
                List<Product> sampleProducts = List.of(
                    Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Smartphone X")
                        .description("Latest smartphone with advanced camera and long battery life")
                        .price(new BigDecimal("899.99"))
                        .stockQuantity(50)
                        .category("Electronics")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                        
                    Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Laptop Pro")
                        .description("High-performance laptop for professional use")
                        .price(new BigDecimal("1299.99"))
                        .stockQuantity(25)
                        .category("Electronics")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                        
                    Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Wireless Headphones")
                        .description("Premium noise-canceling wireless headphones")
                        .price(new BigDecimal("199.99"))
                        .stockQuantity(100)
                        .category("Audio")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                        
                    Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Smart Watch")
                        .description("Fitness tracker and smartwatch with heart rate monitor")
                        .price(new BigDecimal("249.99"))
                        .stockQuantity(40)
                        .category("Wearables")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                        
                    Product.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Bluetooth Speaker")
                        .description("Portable waterproof Bluetooth speaker")
                        .price(new BigDecimal("79.99"))
                        .stockQuantity(75)
                        .category("Audio")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
                );
                
                productRepository.saveAll(sampleProducts);
                log.info("Added {} sample products", sampleProducts.size());
            } else {
                log.info("Products already exist. Skipping initialization.");
            }
        };
    }
}
