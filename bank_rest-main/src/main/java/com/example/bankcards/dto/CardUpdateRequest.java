package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardUpdateRequest {
    private String cardholderName;
    private LocalDate expiryDate;
    private CardStatus status;
    //баланс не будеь обновляется напрямую через этот дто, только через переводы
}