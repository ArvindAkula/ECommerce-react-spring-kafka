package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    /**
     * Find a product by ID with a pessimistic write lock to prevent concurrent updates
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findById(String id);
    
    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find products with stock less than the specified quantity
     */
    List<Product> findByStockQuantityLessThan(Integer quantity);
    
    /**
     * Search products by name or description containing the given keyword
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(String keyword);
}