package com.bubnov.controller.dto.bill;

import java.util.Objects;

public class BillRequestDTO {

    private String billNumber;

    public BillRequestDTO(String billNumber) {
        this.billNumber = billNumber;
    }

    public BillRequestDTO() {
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillRequestDTO that = (BillRequestDTO) o;
        return Objects.equals(billNumber, that.billNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billNumber);
    }
}
