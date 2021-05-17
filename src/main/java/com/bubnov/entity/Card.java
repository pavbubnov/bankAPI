package com.bubnov.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Card {

    private int id;
    private Long cardNumber;
    private Long billNumber;

    public Card(int id, Long cardNumber, BigDecimal amount, Long billNumber) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.billNumber = billNumber;
    }

    public Card() {
    }

    public Long getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(Long billNumber) {
        this.billNumber = billNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Long cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && billNumber == card.billNumber && cardNumber.equals(card.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, billNumber);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber=" + cardNumber +
                ", billNumber=" + billNumber +
                '}';
    }
}
