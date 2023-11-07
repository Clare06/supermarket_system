package com.programmingcodez.productservice.service;

import com.programmingcodez.productservice.dto.ProductRequest;
import com.programmingcodez.productservice.dto.ProductResponse;
import com.programmingcodez.productservice.entity.Product;
import com.programmingcodez.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        // Creating instance of the product object

        Product product = Product.builder()
                .id(productRequest.getId())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .image(productRequest.getImage())
                .build();

        // save the product to the database
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    // Create object of productresponse
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .image(product.getImage())
                .build();
    }

    // delete a product by ID
    public boolean deleteProductById(String id) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Update product details
    public boolean updateProduct(String id, ProductRequest productRequest) {
        if (productRepository.existsById(id)) {
            Product existingProduct = productRepository.findById(id).orElse(null);
            if (existingProduct != null) {
                existingProduct.setName(productRequest.getName());
                existingProduct.setDescription(productRequest.getDescription());
                existingProduct.setPrice(productRequest.getPrice());
                existingProduct.setCategory(productRequest.getCategory());
                productRepository.save(existingProduct);
                log.info("Product {} is updated", id);
                return true;
            }
        }
        return false;
    }

    // Upload image for the product
    public Product getProductById(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public boolean updateProductImage(String productId, byte[] image) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setImage(image);
            productRepository.save(product);
            log.info("Image updated for Product {}", productId);
            return true;
        }
        return false; // Product not found or image update failed
    }

}
