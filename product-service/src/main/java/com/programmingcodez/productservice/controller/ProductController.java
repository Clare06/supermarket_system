package com.programmingcodez.productservice.controller;

import com.programmingcodez.productservice.dto.ProductRequest;
import com.programmingcodez.productservice.dto.ProductResponse;
import com.programmingcodez.productservice.entity.Product;
import com.programmingcodez.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{skucode}")
    @ResponseStatus(HttpStatus.OK)
    public Product getProductBySkuCode(@PathVariable String skucode) {
        return productService.getProductBySkuCode(skucode);
    }

    @DeleteMapping("/{skucode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteProduct(@PathVariable String skucode) {
        boolean deleted = productService.deleteProductById(skucode);

        if (deleted) {
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found or could not be deleted");
        }
    }

    @PutMapping("/{skucode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateProduct(@PathVariable String skucode, @RequestBody ProductRequest productRequest) {
        boolean updated = productService.updateProduct(skucode, productRequest);

        if (updated) {
            return ResponseEntity.ok("Product details updated successfully");
        } else {
            return ResponseEntity.ok("can not find the product");
        }
    }

    // Uploading image to a product

    @PostMapping("/{skuCode}/image")
    public ResponseEntity<String> uploadImage(@PathVariable String skuCode, @RequestParam("imageUrl") String imageUrl) {
        String result = productService.uploadProductImage(skuCode, imageUrl);
        return ResponseEntity.ok(result);
    }

    // Getting image of a product
    @GetMapping("/{skuCode}/image")
    public ResponseEntity<String> getImageUrl(@PathVariable String skuCode) {
        Product product = productService.getProductBySkuCode(skuCode);
        if (product != null && product.getImageURl() != null) {
            return ResponseEntity.ok(product.getImageURl());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}