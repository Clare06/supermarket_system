package com.programmingcodez.orderservice.dto;


import lombok.Data;

@Data
public class ChargeRequest {

    public enum Currency {
        EUR, USD, LKR;
    }
    private String description;
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}
