package com.example.ecommerce.backend.product.service;

import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductSearchRequest;
import com.example.ecommerce.backend.product.dto.request.ProductUpdateRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for product catalog management.
 *
 * <p>Provides CRUD operations for products while exposing DTO responses to API
 * consumers.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface ProductService {
    /**
     * Creates a new product.
     *
     * @param request product creation details
     * @return the newly created product response
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException if SKU already exists
     * @throws jakarta.persistence.EntityNotFoundException if category not found
     */
    ProductResponse create(ProductCreateRequest request);

    /**
     * Retrieves a product by its ID.
     *
     * @param id product identifier
     * @return matching product response
     * @throws jakarta.persistence.EntityNotFoundException if product not found
     */
    ProductResponse getById(Long id);

    /**
     * Retrieves all products with pagination.
     *
     * @param pageable pagination parameters
     * @return a page of product responses
     */
    Page<ProductResponse> getAll(Pageable pageable);

    /**
     * Updates editable details for an existing product.
     *
     * @param id product identifier
     * @param request product update details
     * @return updated product response
     * @throws jakarta.persistence.EntityNotFoundException if product or category not found
     */
    ProductResponse update(Long id, ProductUpdateRequest request);

    /**
     * Deletes a product by ID.
     *
     * @param id product identifier
     * @throws jakarta.persistence.EntityNotFoundException if product not found
     */
    void delete(Long id);

    /**
     * Searches active products using optional filters and pagination.
     *
     * @param request the search filters and pagination info
     * @return a page of matching active product responses
     */
    Page<ProductResponse> search(ProductSearchRequest request);
}
