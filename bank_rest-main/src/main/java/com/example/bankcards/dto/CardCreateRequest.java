package com.example.bankcards.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardCreateRequest {
    @NotBlank(message = "Номер карты не может быть пустым")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты должен состоять из 16 цифр.")
    private String cardNumber;

    @NotBlank(message = "Имя владельца карты не может быть пустым")
    private String cardholderName;

    @NotNull(message = "Дата истечения срока действия не может быть нулевой")
    @FutureOrPresent(message = "Дата истечения срока должна быть в настоящем или будущем.")
    private LocalDate expiryDate;

    @NotNull(message = "Начальный баланс не может быть нулевым")
    private BigDecimal initialBalance;


    public @NotBlank(message = "Имя владельца карты не может быть пустым") String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(@NotBlank(message = "Имя владельца карты не может быть пустым") String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public @NotBlank(message = "Номер карты не может быть пустым") @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты должен состоять из 16 цифр.") String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(@NotBlank(message = "Номер карты не может быть пустым") @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты должен состоять из 16 цифр.") String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public @NotNull(message = "Дата истечения срока действия не может быть нулевой") @FutureOrPresent(message = "Дата истечения срока должна быть в настоящем или будущем.") LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(@NotNull(message = "Дата истечения срока действия не может быть нулевой") @FutureOrPresent(message = "Дата истечения срока должна быть в настоящем или будущем.") LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public @NotNull(message = "Начальный баланс не может быть нулевым") BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(@NotNull(message = "Начальный баланс не может быть нулевым") BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}