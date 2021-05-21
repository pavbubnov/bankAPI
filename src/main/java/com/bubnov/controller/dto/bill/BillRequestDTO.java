package com.bubnov.controller.dto.bill;

import java.math.BigDecimal;
import java.util.Objects;

public class BillRequestDTO {

    private String billNumber;
    private BigDecimal amount;
    private int accountId;

    public BillRequestDTO(String billNumber, BigDecimal amount, int accountId) {
        this.billNumber = billNumber;
        this.amount = amount;
        this.accountId = accountId;
    }

    public BillRequestDTO() {
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillRequestDTO that = (BillRequestDTO) o;
        return accountId == that.accountId && Objects.equals(billNumber, that.billNumber) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billNumber, amount, accountId);
    }

    @Override
    public String toString() {
        return "BillRequestDTO{" +
                "billNumber='" + billNumber + '\'' +
                ", amount=" + amount +
                ", accountId=" + accountId +
                '}';
    }
}
