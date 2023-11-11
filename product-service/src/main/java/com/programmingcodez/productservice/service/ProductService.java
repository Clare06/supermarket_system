package com.programmingcodez.productservice.service;

import com.programmingcodez.productservice.dto.ProductRequest;
import com.programmingcodez.productservice.dto.ProductResponse;
import com.programmingcodez.productservice.entity.Product;
import com.programmingcodez.productservice.repository.ProductRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public static final String CREATE_PRODUCT_CIRCUIT_BREAKER = "create-product-circuit-breaker";
    public static final String CREATE_PRODUCT_RETRY = "create-product-retry";
    public static final String GET_BY_ID_CIRCUIT_BREAKER = "get-all-products-circuit-breaker";
    public static final String GET_BY_ID_RETRY = "get-all-products-retry";

    @CircuitBreaker(name = CREATE_PRODUCT_CIRCUIT_BREAKER, fallbackMethod = "createProductFallback")
    @Retry(name = CREATE_PRODUCT_RETRY)
    public void createProduct(ProductRequest productRequest) {
        try {
            // Check if SKU code already exists
            if (productRepository.existsById(productRequest.getSkucode())) {
                log.error("Error saving product: SKU code already exists.");
                return;
            }
            // Creating instance of the product object
            Product product = Product.builder()
                    .skucode(productRequest.getSkucode())
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .category(productRequest.getCategory())
                    .type(productRequest.getType())
                    .imageURl(productRequest.getImageURl())
                    .build();

            // save the product to the database
            productRepository.save(product);
            log.info("Product {} is saved", product.getSkucode());
        } catch (Exception e) {
            log.error("Error saving product", e);
        }
    }

    // Fallback method to handle create-product-circuit-breaker open state
    private void createProductFallback(ProductRequest productRequest, Throwable throwable) {
        log.error("Error saving product: Service is currently unavailable. Please try again later.");
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    // Create object of product response
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .skucode(product.getSkucode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .type(product.getType())
                .imageURl(product.getImageURl())
                .build();
    }

    // delete a product by ID
    public boolean deleteProductById(String skucode) {

        try {
            if (productRepository.existsById(skucode)) {
                productRepository.deleteById(skucode);
                return true;
            } else {
                log.error("Error deleting product: SKU code does not exist.");
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return false;
        }
    }

    // Update product details
    public boolean updateProduct(String skucode, ProductRequest productRequest) {
        if (productRepository.existsById(skucode)) {
            Product existingProduct = productRepository.findById(skucode).orElse(null);
            if (existingProduct != null) {
                existingProduct.setName(productRequest.getName());
                existingProduct.setDescription(productRequest.getDescription());
                existingProduct.setPrice(productRequest.getPrice());
                existingProduct.setCategory(productRequest.getCategory());
                existingProduct.setType(productRequest.getType());
                productRepository.save(existingProduct);
                log.info("Product {} is updated", skucode);
                return true;
            }
        }
        return false;

    }

    @CircuitBreaker(name = GET_BY_ID_CIRCUIT_BREAKER, fallbackMethod = "getProductBySkuCodeFallback")
    @Retry(name = GET_BY_ID_RETRY)
    public Product getProductBySkuCode(String skuCode) {
        return productRepository.findById(skuCode).orElse(null);
    }

    // Fallback method to handle get-by-id-circuit-breaker open state
    private Product getProductBySkuCodeFallback(String skuCode, Throwable throwable) {
        log.error("Error getting product: Service is currently unavailable. Please try again later.");
        return null;
    }

    // upload product image
    public String uploadProductImage(String skuCode, String imageUrl) {
        Product product = productRepository.findById(skuCode).orElse(null);
        if (product != null) {
            product.setImageURl(imageUrl);
            productRepository.save(product);
            return "Image uploaded for product " + skuCode;
        }
        return "Product not found";
    }

}
