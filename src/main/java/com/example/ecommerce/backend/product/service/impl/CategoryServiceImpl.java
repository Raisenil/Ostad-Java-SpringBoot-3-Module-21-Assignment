package com.example.ecommerce.backend.product.service.impl;

import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.backend.product.dto.request.CategorySearchRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.mapper.CategoryMapper;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CategoryService} for managing product categories.
 *
 * <p>Provides CRUD operations and status management for categories
 * with unique code validation.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category create(CategoryCreateRequest request) {
        if (categoryRepository.existsByCode(request.code())) {
            throw new ResourceConflictException("Category with Code '" + request.code() + "' already exists.");
        }

        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    @Override
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category update(Long id, CategoryUpdateRequest request) {
        Category category = getById(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    @Override
    public Category toggleStatus(Long id, Boolean isActive) {
        Category category = getById(id);
        category.setIsActive(isActive);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getById(id);
        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    @Override
    public Page<Category> search(CategorySearchRequest request) {
        Specification<Category> spec = Specification.where((root, query, cb) -> cb.isTrue(root.get("isActive")));
        if (request.name() != null && !request.name().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + request.name().toLowerCase() + "%"));
        }
        if (request.code() != null && !request.code().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("code")), "%" + request.code().toLowerCase() + "%"));
        }
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 10;
        return categoryRepository.findAll(spec, PageRequest.of(page, size));
    }
}
