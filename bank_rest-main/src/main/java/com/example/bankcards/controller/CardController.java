package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.bankcards.exception.ResourceNotFoundException;


import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{userId}")
    public ResponseEntity<CardResponse> createCard(@PathVariable Long userId, @Valid @RequestBody CardCreateRequest request) {
        CardResponse response = cardService.createCard(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{cardId}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long cardId, @Valid @RequestBody CardUpdateRequest request) {
        CardResponse response = cardService.updateCard(cardId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{cardId}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId) {
        CardResponse response = cardService.blockCard(cardId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{cardId}/activate")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId) {
        CardResponse response = cardService.activateCard(cardId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Page<CardResponse>> getAllCards(Pageable pageable) {
        Page<CardResponse> cards = cardService.findAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{cardId}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long cardId) {
        CardResponse card = cardService.getCardById(cardId);
        return ResponseEntity.ok(card);
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")

    @GetMapping("/my")
    public ResponseEntity<Page<CardResponse>> getMyCards(Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<CardResponse> cards = cardService.getUserCards(userId, pageable);
        return ResponseEntity.ok(cards);
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")

    @GetMapping("/my/{cardId}")
    public ResponseEntity<CardResponse> getMyCardById(@PathVariable Long cardId) {
        Long userId = getCurrentUserId();
        CardResponse card = cardService.getUserCardById(cardId, userId);
        return ResponseEntity.ok(card);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@Valid @RequestBody TransferRequest request) {
        Long userId = getCurrentUserId();
        cardService.transferFunds(userId, request);
        return ResponseEntity.ok("Средства успешно переведены");
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/my/{cardId}/request-block")
    public ResponseEntity<CardResponse> requestBlockCard(@PathVariable Long cardId) {
        Long userId = getCurrentUserId();
        CardResponse response = cardService.requestBlockCard(cardId, userId);
        return ResponseEntity.ok(response);
    }


    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Юзер не зарегался");
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
         return Optional.ofNullable(userService.findByUsername(username))
                .map(com.example.bankcards.entity.User::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Авторизированный юзер не найден в бд"));
    }
}