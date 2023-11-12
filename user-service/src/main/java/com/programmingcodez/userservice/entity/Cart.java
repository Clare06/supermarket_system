package com.programmingcodez.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private String userName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CartItem> cartItems;

}
