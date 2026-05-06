package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.dto.request.CategorySearchRequest;
import com.example.ecommerce.backend.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing product categories.
 *
 * <p>Exposes versioned category endpoints under
 * {@link ApiEndpoints.Category#BASE_CATEGORY} and wraps successful responses
 * with the common {@link ApiResponse} structure used by the API.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Category.BASE_CATEGORY)
@RequiredArgsConstructor
@Tag(
        name = "Category",
        description = "Operations for managing product categories"
)
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new product category.
     *
     * @param request validated category creation payload
     * @return response containing the created category
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException when the category code already exists
     */
    @Operation(
            summary = "Create a new category",
            description = "Creates a product category using a unique category code and optional description.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Category creation payload containing the category name, unique code, and optional description.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Create category",
                                    value = """
                                            {
                                              "name": "Electronics",
                                              "code": "ELEC",
                                              "description": "Devices, gadgets, and consumer electronics."
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid category creation payload",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "A category with the requested code already exists",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(request)));
    }

    /**
     * Retrieves a category by its identifier.
     *
     * @param id category identifier
     * @return response containing the matching category
     * @throws jakarta.persistence.EntityNotFoundException when no category exists for the identifier
     */
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a product category by its unique database identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(
            @Parameter(description = "Unique identifier of the category.", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    /**
     * Retrieves categories using page-based pagination.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @return response containing paginated category data
     */
    @Operation(
            summary = "List categories",
            description = "Retrieves product categories using zero-based pagination.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Categories listed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Category>>> listCategories(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of categories to return per page.", example = "10")
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(categoryService.getAll(pageable))));
    }

    /**
     * Searches active categories using optional filters.
     *
     * @param request category search filters and pagination information
     * @return response containing matching active categories
     */
    @Operation(
            summary = "Search active categories",
            description = "Assignment scaffold: students must search categories by optional name and code filters and return only active categories.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Category search payload containing optional filters and pagination values.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategorySearchRequest.class),
                            examples = @ExampleObject(
                                    name = "Search categories",
                                    value = """
                                            {
                                              "name": "Electronics",
                                              "code": "ELEC",
                                              "page": 0,
                                              "size": 10
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<Category>>> searchCategories(
            @Valid @RequestBody CategorySearchRequest request) {
                var page = categoryService.search(request);
                return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(page)));
    }

    /**
     * Updates existing category details.
     *
     * @param id      category identifier
     * @param request validated category update payload
     * @return response containing the updated category
     * @throws jakarta.persistence.EntityNotFoundException when no category exists for the identifier
     */
    @Operation(
            summary = "Update category details",
            description = "Updates the name and description of an existing product category.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Category update payload containing the replacement category name and description.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "Update category",
                                    value = """
                                            {
                                              "name": "Consumer Electronics",
                                              "description": "Smart devices, gadgets, and everyday electronics."
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid category update payload",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @Parameter(description = "Unique identifier of the category to update.", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, request)));
    }

    /**
     * Updates the active status of a category.
     *
     * @param id       category identifier
     * @param isActive desired active status
     * @return response containing the category after the status change
     * @throws jakarta.persistence.EntityNotFoundException when no category exists for the identifier
     */
    @Operation(
            summary = "Activate or deactivate category",
            description = "Sets the active status of a product category.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category status updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Category>> toggleCategoryStatus(
            @Parameter(description = "Unique identifier of the category to update.", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "New active status for the category.", example = "true", required = true)
            @RequestParam Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.toggleStatus(id, isActive)));
    }

    /**
     * Soft deletes a category.
     *
     * @param id category identifier
     * @return empty response when deletion succeeds
     * @throws jakarta.persistence.EntityNotFoundException when no category exists for the identifier
     */
    @Operation(
            summary = "Soft delete category",
            description = "Assignment scaffold: students must change this endpoint from hard delete to soft delete by setting isActive to false.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "Category deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Unique identifier of the category to delete.", example = "1", required = true)
            @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
