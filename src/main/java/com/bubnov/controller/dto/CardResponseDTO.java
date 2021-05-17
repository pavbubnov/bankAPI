package com.bubnov.controller.dto;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardResponseDTO that = (CardResponseDTO) o;
        return cardNumber.equals(that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return "CardRequestDTO{" +
                "cardNumber=" + cardNumber +
                '}';
    }
}
