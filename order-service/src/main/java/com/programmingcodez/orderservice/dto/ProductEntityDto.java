package com.programmingcodez.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntityDto {

    private String skucode;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageURl;

}
