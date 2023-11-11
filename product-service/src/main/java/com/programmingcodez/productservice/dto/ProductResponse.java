package com.programmingcodez.productservice.dto;

import com.programmingcodez.productservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String skucode;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageURl;
    // Product type to differentiate products units vs weight
    private Product.ProductType type;
}
