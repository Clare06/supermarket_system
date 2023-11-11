package com.programmingcodez.userservice.service;

import com.programmingcodez.userservice.dto.CartInfoDto;
import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.repository.CartRepository;
import com.programmingcodez.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImp implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<String> addItemToCart(CartInfoDto cartInfoDto) {
        if (userRepository.existsById(cartInfoDto.getUserName())){
             User user = userRepository.findById(cartInfoDto.getUserName()).orElse(null);

        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        return null;
    }

    @Override
    public ResponseEntity<Cart> getUserCart(String userName) {
        return null;
    }

    @Override
    public ResponseEntity<Void> clearCart(String userName) {
        return null;
    }

    @Override
    public ResponseEntity<Cart> deleteItem(String userName, String skuCode) {
        return null;
    }

    @Override
    public ResponseEntity<Cart> updateItem(String userName, String skuCode) {
        return null;
    }

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
