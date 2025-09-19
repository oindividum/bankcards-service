package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Номер карты отправителя не может быть пустым")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты отправителя должен состоять из 16 цифр")
    private String fromCardNumber;

    @NotBlank(message = "Номер карты получателя не может быть пустым")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты получателя должен состоять из 16 цифр")
    private String toCardNumber;

    @NotNull(message = "Сумма перевода не может быть пустой")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
    private BigDecimal amount;

    public @NotNull(message = "Сумма перевода не может быть пустой")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
    BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(message = "Сумма перевода не может быть пустой")
                          @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
                          BigDecimal amount) {
        this.amount = amount;
    }

    public @NotBlank(message = "Номер карты отправителя не может быть пустым")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты отправителя должен состоять из 16 цифр")
    String getFromCardNumber() {
        return fromCardNumber;
    }

    public void setFromCardNumber(@NotBlank(message = "Номер карты отправителя не может быть пустым")
                                  @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты отправителя должен состоять из 16 цифр")
                                  String fromCardNumber) {
        this.fromCardNumber = fromCardNumber;
    }

    public @NotBlank(message = "Номер карты получателя не может быть пустым")
    @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты получателя должен состоять из 16 цифр")
    String getToCardNumber() {
        return toCardNumber;
    }

    public void setToCardNumber(@NotBlank(message = "Номер карты получателя не может быть пустым")
                                @Pattern(regexp = "^[0-9]{16}$", message = "Номер карты получателя должен состоять из 16 цифр")
                                String toCardNumber) {
        this.toCardNumber = toCardNumber;
    }
}
