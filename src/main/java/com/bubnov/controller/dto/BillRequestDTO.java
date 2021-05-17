package com.bubnov.controller.dto;

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

}
