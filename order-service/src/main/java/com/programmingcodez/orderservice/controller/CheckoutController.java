package com.programmingcodez.orderservice.controller;


import com.programmingcodez.orderservice.dto.ChargeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CheckoutController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @GetMapping("/checkout")
    public Map<String, Object> checkout() {
        Map<String, Object> response = new HashMap<>();
        response.put("amount", 50 * 100); // in cents
        response.put("stripePublicKey", stripePublicKey);
        response.put("currency", ChargeRequest.Currency.EUR);
        return response;
    }
}