package com.jankasper.user.client;

import com.jankasper.user.dto.ExchangeRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service", url = "${exchange.service.url}")
public interface ExchangeServiceClient {

    @GetMapping("/api/v1/exchange/rate")
    ExchangeRateResponse getExchangeRate(
            @RequestParam("from") String from,
            @RequestParam("to") String to
    );
}
