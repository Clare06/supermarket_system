package com.programmingcodez.userservice.service;

import com.programmingcodez.userservice.dto.CartInfoDto;
import com.programmingcodez.userservice.entity.User;
import com.programmingcodez.userservice.entity.Cart;
import org.springframework.http.ResponseEntity;

public interface CartService {

    public ResponseEntity <String> addItemToCart(CartInfoDto cartInfo);
    public ResponseEntity <Cart> getUserCart(String userName);
    public ResponseEntity <Void> clearCart(String userName);
    public ResponseEntity <Cart> deleteItem(String userName, String skuCode);
    public ResponseEntity <Cart> updateItem(CartInfoDto cartInfo);

}
