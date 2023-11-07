package com.programmingcodez.productservice.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String skucode;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageURl;
}
