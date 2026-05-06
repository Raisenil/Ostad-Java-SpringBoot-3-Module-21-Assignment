package com.example.ecommerce.backend.product.repository;

import com.example.ecommerce.backend.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for product catalog persistence operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    /**
     * Checks whether a product already exists for the supplied SKU.
     *
     * @param sku product stock keeping unit
     * @return {@code true} when the SKU is already used
     */
    boolean existsBySku(String sku);
}
