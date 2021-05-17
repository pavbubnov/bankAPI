package com.bubnov.entity;

import java.util.Objects;

public class Bill {

    private int id;
    private int account_id;
    private Long billNumber;

    public Bill(int id, int account_id, Long billNumber) {
        this.id = id;
        this.account_id = account_id;
        this.billNumber = billNumber;
    }

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

    public Long getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(Long billNumber) {
        this.billNumber = billNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return id == bill.id && account_id == bill.account_id && billNumber.equals(bill.billNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account_id, billNumber);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", account_id=" + account_id +
                ", billNumber='" + billNumber + '\'' +
                '}';
    }
}

