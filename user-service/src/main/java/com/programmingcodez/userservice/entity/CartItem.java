package com.programmingcodez.userservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    private String skuCode;
    private  int qyt;

//    @ManyToOne
//    private Product product;

//    @ManyToOne
//    private Cart cart;
//    private int quantity;
}
