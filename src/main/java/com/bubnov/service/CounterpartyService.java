package com.bubnov.service;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.CounterpartyRepository;

import java.sql.SQLException;

public class CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;

    public CounterpartyService(CounterpartyRepository counterpartyRepository) {
        this.counterpartyRepository = counterpartyRepository;
    }

    public CounterpartyDTO postCounterparty(CounterpartyDTO counterpartyDTO)
            throws RequestException, DatabaseException, SQLException {
        if (counterpartyRepository.checkThatCounterParty(counterpartyDTO.getEmployer_id(),
                counterpartyDTO.getCounterparty_id())) {
            throw new RequestException("Аккаунт с id: " + counterpartyDTO.getCounterparty_id() +
                    "уже является контрагентом для аккаунта с id: " + counterpartyDTO.getEmployer_id());
        }
        return counterpartyRepository.createCounterparty(counterpartyDTO);
    }
}
