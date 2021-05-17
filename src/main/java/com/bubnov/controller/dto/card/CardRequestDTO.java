package com.bubnov.controller.dto.card;

import java.util.Objects;

public class CardRequestDTO {

    private String cardNumber;
    private String billNumber;

    public CardRequestDTO(String cardNumber, String billNumber) {
        this.cardNumber = cardNumber;
        this.billNumber = billNumber;
    }

    public CardRequestDTO() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRequestDTO that = (CardRequestDTO) o;
        return cardNumber.equals(that.cardNumber) && billNumber.equals(that.billNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, billNumber);
    }

    @Override
    public String toString() {
        return "CardRequestDTO{" +
                "cardNumber='" + cardNumber + '\'' +
                ", billNumber='" + billNumber + '\'' +
                '}';
    }
}
