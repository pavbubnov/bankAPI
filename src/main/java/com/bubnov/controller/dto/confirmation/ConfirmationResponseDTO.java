package com.bubnov.controller.dto.confirmation;

import java.util.Objects;

public class ConfirmationResponseDTO {

    private int id;
    private String entityName;
    private String operation;
    private String info;
    private String confirmationStatus;

    public ConfirmationResponseDTO(int id, String entityName, String operation, String info, String confirmationStatus) {
        this.id = id;
        this.entityName = entityName;
        this.operation = operation;
        this.info = info;
        this.confirmationStatus = confirmationStatus;
    }

    public ConfirmationResponseDTO(int id, ConfirmationRequestDTO requestDTO) {
        this.id = id;
        this.entityName = requestDTO.getEntityName();
        this.operation = requestDTO.getOperation();
        this.info = requestDTO.getInfo();
        this.confirmationStatus = requestDTO.getConfirmationStatus();
    }

    public ConfirmationResponseDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        ConfirmationResponseDTO that = (ConfirmationResponseDTO) o;
        return id == that.id && Objects.equals(info, that.info) && Objects.equals(entityName, that.entityName) &&
                Objects.equals(operation, that.operation) && Objects.equals(confirmationStatus, that.confirmationStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityName, operation, info, confirmationStatus);
    }

    @Override
    public String toString() {
        return "ConfirmationResponseDTO{" +
                "id=" + id +
                ", entityName='" + entityName + '\'' +
                ", operation='" + operation + '\'' +
                ", accountId=" + info +
                ", confirmationStatus='" + confirmationStatus + '\'' +
                '}';
    }
}
