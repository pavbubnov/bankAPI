package com.bubnov.controller.dto.deposit;

import java.math.BigDecimal;
import java.util.Objects;

public class DepositRequestDTO {

    private String billNumber;
    private BigDecimal amount;

    public DepositRequestDTO(String billNumber, BigDecimal amount) {
        this.billNumber = billNumber;
        this.amount = amount;
    }

    public DepositRequestDTO() {
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
        DepositRequestDTO that = (DepositRequestDTO) o;
        return Objects.equals(billNumber, that.billNumber) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billNumber, amount);
    }
}
