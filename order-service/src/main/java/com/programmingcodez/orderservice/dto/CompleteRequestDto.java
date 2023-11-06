package com.programmingcodez.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteRequestDto {
    private ChargeRequest chargeRequest;
    private Long orderId;
}
