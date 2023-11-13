package com.programmingcodez.userservice.controller;

import com.programmingcodez.userservice.dto.CartInfoDto;
import com.programmingcodez.userservice.entity.Cart;
import com.programmingcodez.userservice.service.CartServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/user")
@CrossOrigin(allowedHeaders = "*",origins = "*")
public class CartController {

    @Autowired
    private CartServiceImp cartService;

    @GetMapping("/cart/{userName}")
    public ResponseEntity<Cart> getUserCart(@PathVariable String userName){
        return this.cartService.getUserCart(userName);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<String> addItemsToCart(@RequestBody CartInfoDto cartInfo){
        return this.cartService.addItemToCart(cartInfo);
    }

    @PutMapping("/cart/update")
    public ResponseEntity<Cart> updateItem (@RequestBody CartInfoDto cartInfo){
        return this.cartService.updateItem(cartInfo);
    }

    @PutMapping("/cart/{userName}/remove/{skuCode}")
    public ResponseEntity<Cart> deleteItem(@PathVariable String userName, @PathVariable String skuCode){
        return this.cartService.deleteItem(userName, skuCode);
    }

    @DeleteMapping("/cart/{userName}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userName){
        return this.cartService.clearCart(userName);
    }
}
