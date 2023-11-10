package com.programmingcodez.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto {

    private String skucode;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
