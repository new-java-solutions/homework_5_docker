package com.jankasper.user.dto;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        BigInteger balance,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
