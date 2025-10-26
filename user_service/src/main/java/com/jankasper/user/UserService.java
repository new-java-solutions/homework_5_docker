package com.jankasper.user;

import com.jankasper.user.client.ExchangeServiceClient;
import com.jankasper.user.dto.ExchangeRateResponse;
import com.jankasper.user.dto.UserBalanceResponse;
import com.jankasper.user.dto.UserRequest;
import com.jankasper.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExchangeServiceClient exchangeServiceClient;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toResponse(user);
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User already exists with email: " + request.email());
        }

        User user = User.builder()
            .email(request.email())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .balance(BigInteger.ZERO)
            .active(true)
            .build();

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User already exists with email: " + request.email());
        }

        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponse generateRandomUser() {
        String randomId = java.util.UUID.randomUUID().toString().substring(0, 8);
        String email = "user" + randomId + "@example.com";
        
        User user = User.builder()
                .email(email)
                .firstName("User" + randomId)
                .lastName("Generated")
                .balance(BigInteger.valueOf(100))
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    public UserBalanceResponse getUserBalanceInCurrency(Long id, String currency) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String normalizedCurrency = currency.toUpperCase();
        BigDecimal balanceInUsd = new BigDecimal(user.getBalance());

        // If requested currency is USD, return balance as is
        if ("USD".equals(normalizedCurrency)) {
            return UserBalanceResponse.builder()
                    .userId(user.getId())
                    .balance(balanceInUsd)
                    .currency("USD")
                    .build();
        }

        // Get exchange rate from exchange service using Feign
        ExchangeRateResponse exchangeRate = exchangeServiceClient.getExchangeRate("USD", normalizedCurrency);
        BigDecimal convertedBalance = balanceInUsd.multiply(exchangeRate.rate()).setScale(2, RoundingMode.HALF_UP);

        return UserBalanceResponse.builder()
                .userId(user.getId())
                .balance(convertedBalance)
                .currency(normalizedCurrency)
                .build();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .balance(user.getBalance())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
