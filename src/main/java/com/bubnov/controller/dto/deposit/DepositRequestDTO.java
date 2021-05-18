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

}
