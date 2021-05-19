package com.bubnov.controller.dto.bill;

import java.math.BigDecimal;
import java.util.Objects;

public class AmountResponseDTO {

    private BigDecimal amount;

    public AmountResponseDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public AmountResponseDTO() {
    }

    public AmountResponseDTO(BillResponseDTO billResponseDTO) {
        amount = billResponseDTO.getAmount();
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
        AmountResponseDTO that = (AmountResponseDTO) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
