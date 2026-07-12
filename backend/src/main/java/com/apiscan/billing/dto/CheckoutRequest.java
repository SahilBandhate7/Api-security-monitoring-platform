package com.apiscan.billing.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank(message = "Price ID is required") String priceId) {
}
