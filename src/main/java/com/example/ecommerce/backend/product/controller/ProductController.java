package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductSearchRequest;
import com.example.ecommerce.backend.product.dto.request.ProductUpdateRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.service.ProductService;
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
 * REST controller for managing product catalog items.
 *
 * <p>Exposes versioned product endpoints under
 * {@link ApiEndpoints.Product#BASE_PRODUCT} and wraps successful responses with
 * the common {@link ApiResponse} structure used by the API.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Product.BASE_PRODUCT)
@RequiredArgsConstructor
@Tag(
        name = "Product",
        description = "Operations for managing product catalog items"
)
public class ProductController {
    private final ProductService productService;

    /**
     * Creates a new product catalog item.
     *
     * @param request validated product creation payload
     * @return response containing the created product
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException when the SKU already exists
     * @throws jakarta.persistence.EntityNotFoundException when the category does not exist
     */
    @Operation(
            summary = "Create a new product",
            description = "Creates a product using a unique SKU and an existing category identifier.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Product creation payload containing SKU, catalog details, price, and category identifier.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "Create product",
                                    value = """
                                            {
                                              "sku": "PHONE-001",
                                              "name": "Smartphone",
                                              "description": "Android smartphone with 128GB storage.",
                                              "price": 499.99,
                                              "isActive": true,
                                              "categoryId": 1,
                                              "imageUrl": "https://example.com/products/phone-001.jpg"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Product created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid product creation payload",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "A product with the requested SKU already exists",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(request)));
    }

    /**
     * Retrieves a product by its identifier.
     *
     * @param id product identifier
     * @return response containing the matching product
     * @throws jakarta.persistence.EntityNotFoundException when no product exists for the identifier
     */
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a product catalog item by its unique database identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Product retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No product exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Unique identifier of the product.", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    /**
     * Retrieves products using page-based pagination.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @return response containing paginated product data
     */
    @Operation(
            summary = "List products",
            description = "Retrieves product catalog items using zero-based pagination.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Products listed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> listProducts(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of products to return per page.", example = "10")
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(productService.getAll(pageable))));
    }

    /**
     * Searches active products using optional filters.
     *
     * @param request product search filters and pagination information
     * @return response containing matching active products
     */
    @Operation(
            summary = "Search active products",
            description = "Assignment scaffold: students must search products by optional name, SKU, category, and price range filters and return only active products.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Product search payload containing optional filters and pagination values.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductSearchRequest.class),
                            examples = @ExampleObject(
                                    name = "Search products",
                                    value = """
                                            {
                                              "name": "Smartphone",
                                              "sku": "PHONE",
                                              "categoryId": 1,
                                              "minPrice": 100.00,
                                              "maxPrice": 700.00,
                                              "page": 0,
                                              "size": 10
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> searchProducts(
            @Valid @RequestBody ProductSearchRequest request) {
        var page = productService.search(request);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(page)));
    }

    /**
     * Updates existing product details.
     *
     * @param id product identifier
     * @param request validated product update payload
     * @return response containing the updated product
     * @throws jakarta.persistence.EntityNotFoundException when no product or category exists for the identifier
     */
    @Operation(
            summary = "Update product details",
            description = "Updates editable product details while keeping the SKU immutable.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Product update payload containing replacement product details and category identifier.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "Update product",
                                    value = """
                                            {
                                              "name": "Smartphone Pro",
                                              "description": "Android smartphone with 256GB storage.",
                                              "price": 649.99,
                                              "isActive": true,
                                              "categoryId": 1,
                                              "imageUrl": "https://example.com/products/phone-001-pro.jpg"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid product update payload",
                            content = @Content
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No product or category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Unique identifier of the product to update.", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.update(id, request)));
    }

    /**
     * Soft deletes a product.
     *
     * @param id product identifier
     * @return empty response when deletion succeeds
     * @throws jakarta.persistence.EntityNotFoundException when no product exists for the identifier
     */
    @Operation(
            summary = "Soft delete product",
            description = "Assignment scaffold: students must change this endpoint from hard delete to soft delete by setting isActive to false.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "204",
                            description = "Product deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No product exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Unique identifier of the product to delete.", example = "1", required = true)
            @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
