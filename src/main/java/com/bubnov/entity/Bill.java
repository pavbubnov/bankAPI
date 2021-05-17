package com.bubnov.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Bill {

    private int id;
    private int account_id;
    private BigDecimal amount;
    private String billNumber;

    public Bill() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
        Bill bill = (Bill) o;
        return id == bill.id && account_id == bill.account_id && amount.equals(bill.amount) && billNumber.equals(bill.billNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account_id, amount, billNumber);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", amount=" + amount +
                ", billNumber='" + billNumber + '\'' +
                '}';
    }
}

