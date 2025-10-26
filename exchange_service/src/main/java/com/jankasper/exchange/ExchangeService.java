package com.jankasper.exchange;

import com.jankasper.exchange.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    // Mock exchange rates (base currency: USD)
    private static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
            "USD", BigDecimal.ONE,
            "EUR", new BigDecimal("0.92"),
            "GBP", new BigDecimal("0.79"),
            "JPY", new BigDecimal("149.50"),
            "CHF", new BigDecimal("0.88"),
            "CAD", new BigDecimal("1.36"),
            "AUD", new BigDecimal("1.52"),
            "CNY", new BigDecimal("7.24")
    );

    public ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency) {
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();

        if (!EXCHANGE_RATES.containsKey(from)) {
            throw new RuntimeException("Currency not supported: " + from);
        }

        if (!EXCHANGE_RATES.containsKey(to)) {
            throw new RuntimeException("Currency not supported: " + to);
        }

        // Calculate exchange rate: (1 / fromRate) * toRate
        BigDecimal fromRate = EXCHANGE_RATES.get(from);
        BigDecimal toRate = EXCHANGE_RATES.get(to);
        BigDecimal exchangeRate = toRate.divide(fromRate, 6, RoundingMode.HALF_UP);

        return ExchangeRateResponse.builder()
                .fromCurrency(from)
                .toCurrency(to)
                .rate(exchangeRate)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
