package com.programmingcodez.userservice.repository;

import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
//    CartItem findByCartAndProduct(Cart cart, Product product);
}
