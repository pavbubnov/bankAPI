package com.bubnov.controller.dto.counterparty;

import java.util.Objects;

public class CounterpartyDTO {

    private int employer_id;
    private int counterparty_id;

    public CounterpartyDTO(int employer_id, int counterparty_id) {
        this.employer_id = employer_id;
        this.counterparty_id = counterparty_id;
    }

    public CounterpartyDTO() {
    }

    public int getEmployer_id() {
        return employer_id;
    }

    public void setEmployer_id(int employer_id) {
        this.employer_id = employer_id;
    }

    public int getCounterparty_id() {
        return counterparty_id;
    }

    public void setCounterparty_id(int counterparty_id) {
        this.counterparty_id = counterparty_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CounterpartyDTO that = (CounterpartyDTO) o;
        return employer_id == that.employer_id && counterparty_id == that.counterparty_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employer_id, counterparty_id);
    }

    @Override
    public String toString() {
        return "CounterPartyDTO{" +
                "employer_id=" + employer_id +
                ", counterparty_id=" + counterparty_id +
                '}';
    }
}
