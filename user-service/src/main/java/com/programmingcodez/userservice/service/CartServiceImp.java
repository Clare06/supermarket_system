package com.programmingcodez.userservice.service;

import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.entity.CartItem;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.repository.CartItemRepository;
import com.programmingcodez.userservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImp {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

//    @Override
//    public String addItemToCart(User user, Product product, int quantity){
//        Cart cart = user.getCart();
//        if (cart == null){
//            cart = new Cart();
//            cart.setUser(user);
//        }
//
//        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product);
//
//        if (existingItem != null){
//            existingItem.setQuantity(existingItem.getQuantity() + quantity);
//        } else {
//            CartItem cartItem = new CartItem();
////            cartItem.setProduct(product);
//            cartItem.setQuantity(quantity);
//            cartItem.setCart(cart);
//            cart.getCartItems().add(cartItem);
//        }
//        cartRepository.save(cart);
//    }

}
