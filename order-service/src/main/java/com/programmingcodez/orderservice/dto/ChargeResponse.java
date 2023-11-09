package com.programmingcodez.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargeResponse {
    private String id;
    private String status;
    private String chargeId;
    private String balanceTransaction;

}
