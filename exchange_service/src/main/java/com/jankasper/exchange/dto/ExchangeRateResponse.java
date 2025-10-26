package com.jankasper.exchange.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ExchangeRateResponse(
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        LocalDateTime timestamp
) {
}
