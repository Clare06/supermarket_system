package com.programmingcodez.orderservice.controller;

import com.programmingcodez.orderservice.dto.ChargeRequest;
import com.programmingcodez.orderservice.dto.ChargeResponse;
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
    public ChargeResponse charge(@RequestBody ChargeRequest chargeRequest)
            throws StripeException {
        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(ChargeRequest.Currency.EUR);
        Charge charge = paymentsService.charge(chargeRequest);

        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setChargeId(charge.getId());
        chargeResponse.setStatus(charge.getStatus());
        chargeResponse.setChargeId(charge.getId());
        chargeResponse.setBalanceTransaction(charge.getBalanceTransaction());

        return chargeResponse;
    }

    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleError(StripeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}
