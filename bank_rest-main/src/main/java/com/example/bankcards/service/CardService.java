package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberUtil cardNumberUtil;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserRepository userRepository, CardNumberUtil cardNumberUtil, UserService userService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardNumberUtil = cardNumberUtil;
        this.userService = userService;
    }


    @Transactional
    public CardResponse createCard(Long userId, CardCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден по ID: " + userId));

        String encryptedCardNumber = cardNumberUtil.encryptCardNumber(request.getCardNumber());
        if (cardRepository.findByCardNumber(encryptedCardNumber).isPresent()) {
            throw new BadRequestException("Карта с таким номером уже существует.");
        }

        Card card = new Card();
        card.setCardNumber(encryptedCardNumber);
        card.setCardholderName(request.getCardholderName());
        card.setExpiryDate(request.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);
        return mapCardToCardResponse(savedCard);
    }

    @Transactional
    public CardResponse updateCard(Long cardId, CardUpdateRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена по ID: " + cardId));

        Optional.ofNullable(request.getCardholderName()).ifPresent(card::setCardholderName);
        Optional.ofNullable(request.getExpiryDate()).ifPresent(card::setExpiryDate);
        Optional.ofNullable(request.getStatus()).ifPresent(card::setStatus);

        Card updatedCard = cardRepository.save(card);
        return mapCardToCardResponse(updatedCard);
    }

    @Transactional
    public CardResponse blockCard(Long cardId) {
        return changeCardStatus(cardId, CardStatus.BLOCKED);
    }

    @Transactional
    public CardResponse activateCard(Long cardId) {
        return changeCardStatus(cardId, CardStatus.ACTIVE);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Карта не найдена по ID: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }

    public Page<CardResponse> findAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::mapCardToCardResponse);
    }

    public CardResponse getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена по ID: " + cardId));
        return mapCardToCardResponse(card);
    }


    public Page<CardResponse> getUserCards(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable)
                .map(this::mapCardToCardResponse);
    }

    public CardResponse getUserCardById(Long cardId, Long userId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена по ID: " + cardId + " для пользователя: " + userId));
        return mapCardToCardResponse(card);
    }

    @Transactional
    public CardResponse requestBlockCard(Long cardId, Long userId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена по ID: " + cardId + " для пользователя: " + userId));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Карта уже заблокирована.");
        }
        card.setStatus(CardStatus.BLOCKED);
        return mapCardToCardResponse(cardRepository.save(card));
    }

    @Transactional
    public void transferFunds(Long userId, TransferRequest request) {
        String encryptedFromCardNumber = cardNumberUtil.encryptCardNumber(request.getFromCardNumber());
        String encryptedToCardNumber = cardNumberUtil.encryptCardNumber(request.getToCardNumber());

        Card fromCard = cardRepository.findByCardNumber(encryptedFromCardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Карта отправителя не найдена."));
        Card toCard = cardRepository.findByCardNumber(encryptedToCardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Карта получателя не найдена."));

        if (!fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) {
            throw new BadRequestException("Обе карты должны принадлежать текущему пользователю для перевода.");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Карта отправителя неактивна.");
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Карта получателя неактивна.");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Недостаточно средств на карте отправителя.");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    private CardResponse changeCardStatus(Long cardId, CardStatus newStatus) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Карта не найдена по ID: " + cardId));
        card.setStatus(newStatus);
        Card updatedCard = cardRepository.save(card);
        return mapCardToCardResponse(updatedCard);
    }

    private CardResponse mapCardToCardResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setMaskedCardNumber(cardNumberUtil.maskCardNumber(cardNumberUtil.decryptCardNumber(card.getCardNumber())));
        response.setCardholderName(card.getCardholderName());
        response.setExpiryDate(card.getExpiryDate());
        response.setStatus(card.getStatus());
        response.setBalance(card.getBalance());
        response.setUserId(card.getUser().getId());
        return response;
    }
}
