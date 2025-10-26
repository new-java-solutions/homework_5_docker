package com.jankasper.exchange;

import com.jankasper.exchange.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateResponse> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(exchangeService.getExchangeRate(from, to));
    }
}
