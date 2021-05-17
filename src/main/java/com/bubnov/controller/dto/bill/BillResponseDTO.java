package com.bubnov.controller.dto.bill;

import java.math.BigDecimal;

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
}
