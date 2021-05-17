package com.bubnov.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Deposit {

    private int id;
    private String billNumber;
    private BigDecimal amount;

    public Deposit(int id, String billNumber, BigDecimal amount) {
        this.id = id;
        this.billNumber = billNumber;
        this.amount = amount;
    }

    public Deposit() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
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
        Deposit deposit = (Deposit) o;
        return id == deposit.id && billNumber.equals(deposit.billNumber) && amount.equals(deposit.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, billNumber, amount);
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "id=" + id +
                ", billNumber='" + billNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
