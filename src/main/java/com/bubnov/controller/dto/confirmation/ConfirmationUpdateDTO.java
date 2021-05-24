package com.bubnov.controller.dto.confirmation;

import java.util.Objects;

public class ConfirmationUpdateDTO {

    private int id;
    private String status;

    public ConfirmationUpdateDTO(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public ConfirmationUpdateDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationUpdateDTO that = (ConfirmationUpdateDTO) o;
        return id == that.id && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    @Override
    public String toString() {
        return "ConfirmationUpdateDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
