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

    public void createProduct(ProductRequest productRequest){
//        Creating instance of the product object

        Product product = Product.builder()
                .id(productRequest.getId())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

//        save the product to the database
        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    // Create object of productresponse
    private ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
    //delete a product by ID
    public boolean deleteProductById(String id) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
