package com.bubnov.controller.dto.confirmation;

import java.util.Objects;

public class ConfirmationRequestDTO {

    private String entityName;
    private String operation;
    private String info;
    private String confirmationStatus;

    public ConfirmationRequestDTO(String entityName, String operation, String info, String confirmationStatus) {
        this.entityName = entityName;
        this.operation = operation;
        this.info = info;
        this.confirmationStatus = confirmationStatus;
    }

    public ConfirmationRequestDTO() {
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(String confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationRequestDTO that = (ConfirmationRequestDTO) o;
        return Objects.equals(entityName, that.entityName) && Objects.equals(operation, that.operation) && Objects.equals(info, that.info) && Objects.equals(confirmationStatus, that.confirmationStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, operation, info, confirmationStatus);
    }

    @Override
    public String toString() {
        return "ConfirmationRequestDTO{" +
                "entityName='" + entityName + '\'' +
                ", operation='" + operation + '\'' +
                ", info='" + info + '\'' +
                ", confirmationStatus='" + confirmationStatus + '\'' +
                '}';
    }
}





