package com.programmingcodez.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private Long id;
    private String skuCode;
    private Integer quantity;
}
