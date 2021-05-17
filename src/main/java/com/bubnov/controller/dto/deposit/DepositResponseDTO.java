package com.bubnov.controller.dto.deposit;

import java.math.BigDecimal;
import java.util.Objects;

public class DepositResponseDTO {

    private String billNumber;
    private BigDecimal amountDeposit;
    private BigDecimal amountAfter;

    public DepositResponseDTO(String billNumber, BigDecimal amount, BigDecimal amountAfter) {
        this.billNumber = billNumber;
        amountDeposit = amount;
        this.amountAfter = amountAfter;
    }

    public DepositResponseDTO(DepositRequestDTO requestDTO, BigDecimal amountAfter) {
        billNumber = requestDTO.getBillNumber();
        amountDeposit = requestDTO.getAmount();
        this.amountAfter = amountAfter;
    }

    public DepositResponseDTO() {
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public BigDecimal getAmountDeposit() {
        return amountDeposit;
    }

    public void setAmountDeposit(BigDecimal amountDeposit) {
        this.amountDeposit = amountDeposit;
    }

    public BigDecimal getAmountAfter() {
        return amountAfter;
    }

    public void setAmountAfter(BigDecimal amountAfter) {
        this.amountAfter = amountAfter;
    }

}
