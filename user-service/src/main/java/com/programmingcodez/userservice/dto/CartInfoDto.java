package com.programmingcodez.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartInfoDto {
    private String userName;
    private String skuCode;
    private int qty;
}
