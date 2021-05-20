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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositResponseDTO that = (DepositResponseDTO) o;
        return Objects.equals(billNumber, that.billNumber) && Objects.equals(amountDeposit, that.amountDeposit)
                && Objects.equals(amountAfter, that.amountAfter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billNumber, amountDeposit, amountAfter);
    }

    @Override
    public String toString() {
        return "DepositResponseDTO{" +
                "billNumber='" + billNumber + '\'' +
                ", amountDeposit=" + amountDeposit +
                ", amountAfter=" + amountAfter +
                '}';
    }
}
