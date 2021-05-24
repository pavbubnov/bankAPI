package com.bubnov.controller.dto.transfer;

import java.math.BigDecimal;
import java.util.Objects;

public class TransferDTO {

    private String senderBillNumber;
    private String recipientBillNumber;
    private BigDecimal amount;

    public TransferDTO(String senderBillNumber, String recipientBillNumber, BigDecimal amount) {
        this.senderBillNumber = senderBillNumber;
        this.recipientBillNumber = recipientBillNumber;
        this.amount = amount;
    }

    public TransferDTO() {
    }

    public String getSenderBillNumber() {
        return senderBillNumber;
    }

    public void setSenderBillNumber(String senderBillNumber) {
        this.senderBillNumber = senderBillNumber;
    }

    public String getRecipientBillNumber() {
        return recipientBillNumber;
    }

    public void setRecipientBillNumber(String recipientBillNumber) {
        this.recipientBillNumber = recipientBillNumber;
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
        TransferDTO that = (TransferDTO) o;
        return Objects.equals(senderBillNumber, that.senderBillNumber) &&
                Objects.equals(recipientBillNumber, that.recipientBillNumber) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(senderBillNumber, recipientBillNumber, amount);
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
                "senderBillNumber='" + senderBillNumber + '\'' +
                ", recipientBillNumber='" + recipientBillNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
