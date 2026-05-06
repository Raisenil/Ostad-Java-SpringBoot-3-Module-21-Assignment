package com.example.ecommerce.backend.product.service.impl;

import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductUpdateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductSearchRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.entity.Product;
import com.example.ecommerce.backend.product.mapper.ProductMapper;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.repository.ProductRepository;
import com.example.ecommerce.backend.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProductService} for managing product catalog items.
 *
 * <p>Handles SKU uniqueness checks, category lookup, persistence, and response
 * mapping for product CRUD workflows.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new ResourceConflictException("Product with SKU '" + request.sku() + "' already exists.");
        }

        Product product = productMapper.toEntity(request);
        product.setIsActive(request.isActive() != null ? request.isActive() : Boolean.TRUE);
        product.setCategory(getCategoryById(request.categoryId()));
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(getProductById(id));
    }

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProductById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setIsActive(request.isActive());
        product.setImageUrl(request.imageUrl());
        product.setCategory(getCategoryById(request.categoryId()));
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = getProductById(id);
        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> search(ProductSearchRequest request) {
        Specification<Product> spec = Specification.where((root, query, cb) -> cb.isTrue(root.get("isActive")));
        if (request.name() != null && !request.name().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + request.name().toLowerCase() + "%"));
        }
        if (request.sku() != null && !request.sku().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("sku")), "%" + request.sku().toLowerCase() + "%"));
        }
        if (request.categoryId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), request.categoryId()));
        }
        if (request.minPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("price"), request.minPrice()));
        }
        if (request.maxPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("price"), request.maxPrice()));
        }
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 10;
        return productRepository.findAll(spec, PageRequest.of(page, size))
                .map(productMapper::toResponse);
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
    }
}
