package com.bubnov.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Card {

    private int id;
    private String number;
    private BigDecimal amount;
    private int bill_id;

    public Card(int id, String number, BigDecimal amount, int bill_id) {
        this.id = id;
        this.number = number;
        this.amount = amount;
        this.bill_id = bill_id;
    }

    public Card() {
    }

    public int getBill_id() {
        return bill_id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && bill_id == card.bill_id && number.equals(card.number) && amount.equals(card.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, amount, bill_id);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", amount=" + amount +
                ", bill_id=" + bill_id +
                '}';
    }
}
