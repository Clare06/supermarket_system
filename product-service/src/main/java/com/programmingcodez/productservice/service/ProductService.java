package com.programmingcodez.productservice.service;

import com.programmingcodez.productservice.dto.ProductRequest;
import com.programmingcodez.productservice.dto.ProductResponse;
import com.programmingcodez.productservice.entity.Product;
import com.programmingcodez.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        // Creating instance of the product object

        Product product = Product.builder()
                .skucode(productRequest.getSkucode())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .category(productRequest.getCategory())
                .imageURl(productRequest.getImageURl())
                .build();

        // save the product to the database
        productRepository.save(product);
        log.info("Product {} is saved", product.getSkucode());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    // Create object of productresponse
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .skucode(product.getSkucode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageURl(product.getImageURl())
                .build();
    }

    // delete a product by ID
    public boolean deleteProductById(String skucode) {

        if (productRepository.existsById(skucode)) {
            productRepository.deleteById(skucode);
            return true;
        } else {
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
                productRepository.save(existingProduct);
                log.info("Product {} is updated", skucode);
                return true;
            }
        }
        return false;
    }

    public Product getProductBySkuCode(String skuCode) {
        return productRepository.findById(skuCode).orElse(null);
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
