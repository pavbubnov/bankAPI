package com.bubnov.controller.dto.bill;

import java.math.BigDecimal;
import java.util.Objects;

public class BillResponseDTO {

    private BigDecimal amount;
    private int accountId;

    public BillResponseDTO(BigDecimal amount, int accountId) {
        this.amount = amount;
        this.accountId = accountId;
    }

    public BillResponseDTO() {
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
        BillResponseDTO that = (BillResponseDTO) o;
        return accountId == that.accountId && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, accountId);
    }

    @Override
    public String toString() {
        return "BillResponseDTO{" +
                "amount=" + amount +
                ", accountId=" + accountId +
                '}';
    }
}
