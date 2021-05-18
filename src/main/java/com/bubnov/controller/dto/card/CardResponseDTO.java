package com.bubnov.controller.dto.card;

import java.util.Objects;

public class CardResponseDTO {

    private String cardNumber;

    public CardResponseDTO(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardResponseDTO(CardRequestDTO requestDTO) {
        cardNumber = requestDTO.getCardNumber();
    }

    public CardResponseDTO() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

}
