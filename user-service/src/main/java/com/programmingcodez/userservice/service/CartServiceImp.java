package com.programmingcodez.userservice.service;

import com.programmingcodez.userservice.dto.CartInfoDto;
import com.programmingcodez.userservice.dto.InventoryRequest;
import com.programmingcodez.userservice.dto.InventoryResponse;
import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.entity.CartItem;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.repository.CartRepository;
import com.programmingcodez.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    private final WebClient.Builder webClientBuilder;

    @Override
    public ResponseEntity<String> addItemToCart(CartInfoDto cartInfo) {
        if (userRepository.existsById(cartInfo.getUserName())){
            User user = userRepository.findById(cartInfo.getUserName()).orElse(null);

            InventoryRequest inventoryRequest = new InventoryRequest(cartInfo.getSkuCode(), cartInfo.getQyt());

             //check if the stock is available
            List<InventoryResponse> invenResponse = webClientBuilder.build()
                    .post()
                    .uri("http://inventory-service/api/inventory/stock")
                    .bodyValue(Collections.singletonList(inventoryRequest)) // Set the list as the request body
                    .retrieve()
                    .bodyToFlux(InventoryResponse.class)
                    .collectList()
                    .block();

            if (invenResponse.get(0).isInStock()){
                Cart cart = this.cartRepository.findById(user).orElse(new Cart(user, new ArrayList<>()));


                List<CartItem> cartItems = cart.getCartItems();
                cartItems.add(new CartItem(cartInfo.getSkuCode(), cartInfo.getQyt()));
                cart.setCartItems(cartItems);
                this.cartRepository.save(cart);

                return new ResponseEntity<>("Cart Updated", HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("No stocks available", HttpStatus.NOT_FOUND);
            }

        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<Cart> getUserCart(String userName) {
        if (this.userRepository.existsById(userName)){
            User user = this.userRepository.findById(userName).orElse(null);
            Cart cart = this.cartRepository.findById(user).orElse(new Cart(user, new ArrayList<>()));
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Void> clearCart(String userName) {
        if (this.userRepository.existsById(userName)){
            this.cartRepository.deleteById(this.userRepository.findById(userName).orElse(new User()));
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Cart> deleteItem(String userName, String skuCode) {
        if (this.userRepository.existsById(userName)){
            Cart cart = this.cartRepository.findById(this.userRepository.findById(userName).orElse(new User())).orElse(new Cart());
            List<CartItem> cartItems= cart.getCartItems();
            cartItems.removeIf(cartItem -> cartItem.getSkuCode().equals(skuCode));
            cart.setCartItems(cartItems);

            this.cartRepository.save(cart);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Cart> updateItem(CartInfoDto cartInfo) {
        if (this.userRepository.existsById(cartInfo.getUserName())){
            Cart cart = this.cartRepository.findById(this.userRepository.findById(cartInfo.getUserName()).orElse(new User())).orElse(new Cart());
            List<CartItem> cartItems= cart.getCartItems();

            for (CartItem cartItem : cartItems){
                if (cartItem.getSkuCode().equals(cartInfo.getSkuCode())){
                    cartItem.setQyt(cartInfo.getQyt());
                }
            }
            cart.setCartItems(cartItems);
            this.cartRepository.save(cart);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        }
        else {
            return ResponseEntity.notFound().build();
        }
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
