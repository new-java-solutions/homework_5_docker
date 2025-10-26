package com.jankasper.user.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserBalanceResponse(
        Long userId,
        BigDecimal balance,
        String currency
) {
}
