package com.bubnov.service;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.entity.Account;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.CounterpartyRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;
    private final AccountRepository accountRepository;


    public CounterpartyService(CounterpartyRepository counterpartyRepository, AccountRepository accountRepository) {
        this.counterpartyRepository = counterpartyRepository;
        this.accountRepository = accountRepository;
    }

    public CounterpartyDTO postCounterparty(CounterpartyDTO counterpartyDTO)
            throws RequestException, DatabaseException, SQLException {
        int employer_id = counterpartyDTO.getEmployer_id();
        int counterparty_id = counterpartyDTO.getCounterparty_id();
        try {
            accountRepository.getAccountById(employer_id);
        } catch (Exception e) {
            throw new RequestException("Аккаунта с id: " + employer_id +
                    " не существует");
        }
        try {
            accountRepository.getAccountById(counterparty_id);
        } catch (Exception e) {
            throw new RequestException("Аккаунта с id: " + counterparty_id +
                    " не существует");
        }

        if (counterpartyRepository.checkThatCounterParty(counterpartyDTO.getEmployer_id(),
                counterpartyDTO.getCounterparty_id())) {
            throw new RequestException("Аккаунт с id: " + counterpartyDTO.getCounterparty_id() +
                    " уже является контрагентом для аккаунта с id: " + counterpartyDTO.getEmployer_id());
        }
        return counterpartyRepository.createCounterparty(counterpartyDTO);
    }

    public List<Account> getCounterparties(int id) throws DatabaseException, SQLException, RequestException {
        try {
            accountRepository.getAccountById(id);
        } catch (Exception e) {
            throw new RequestException("Аккаунта с id: " + id +
                    " не существует");
        }
        List<Integer> counterparties = counterpartyRepository.getCounterparties(id);
        List<Account> accounts = new ArrayList<>();
        for (
                int i : counterparties) {
            Account account = accountRepository.getAccountById(i);
            accounts.add(account);
        }
        return accounts;
    }
}
