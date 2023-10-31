package com.programmingcodez.orderservice.controller;

import com.programmingcodez.orderservice.dto.ChargeRequest;
import com.programmingcodez.orderservice.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")  // Define a base path for your REST endpoints
public class ChargeController {

    @Autowired
    private StripeService paymentsService;

    @PostMapping("/charge")
    public Map<String, Object> charge(@RequestBody ChargeRequest chargeRequest)
            throws StripeException {
        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(ChargeRequest.Currency.EUR);
        Charge charge = paymentsService.charge(chargeRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("id", charge.getId());
        response.put("status", charge.getStatus());
        response.put("chargeId", charge.getId());
        response.put("balance_transaction", charge.getBalanceTransaction());

        return response;
    }

    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleError(StripeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}
