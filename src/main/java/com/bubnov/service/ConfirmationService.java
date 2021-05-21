package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.AccountRepository;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.ConfirmationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ConfirmationService {

    private final ConfirmationRepository confirmationRepository;

    public ConfirmationService(ConfirmationRepository confirmationRepository) {
        this.confirmationRepository = confirmationRepository;
    }

    public ConfirmationResponseDTO postConfirmation(ConfirmationRequestDTO confirmationRequestDTO)
            throws DatabaseException, SQLException, IOException, RequestException {
        String entityName = confirmationRequestDTO.getEntityName();
        switch (entityName) {
            case "Bill": {
                checkAccount(confirmationRequestDTO);
                checkBillNotExist(confirmationRequestDTO);
                break;
            }
        }
        return confirmationRepository.createConfirmation(confirmationRequestDTO);
    }

    public List<ConfirmationResponseDTO> getUnconfirm() throws DatabaseException, SQLException {
        return confirmationRepository.getALLNotConfirm();
    }

    public ConfirmationUpdateDTO updateStatus(ConfirmationUpdateDTO updateDTO) throws DatabaseException, SQLException {
        return confirmationRepository.updateStatus(updateDTO);
    }

    public ConfirmationResponseDTO getById(int id) throws DatabaseException, SQLException {
        return confirmationRepository.getConfirmById(id);
    }

    private void checkAccount(ConfirmationRequestDTO confirmationRequestDTO)
            throws IOException, RequestException {
        AccountRepository accountRepository = AccountRepository.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        int accountId = objectMapper.readValue(confirmationRequestDTO.getInfo(),
                BillRequestDTO.class).getAccountId();
        if (accountId <= 0) {
            throw new RequestException("Номер аккаунта должен быть положитльным");
        }
        try {
            accountRepository.getAccountById(accountId).getNameOfPerson();
        } catch (Exception e) {
            throw new RequestException("Аккаунта с id: " + accountId + " не существует");
        }
    }

    private void checkBillNotExist(ConfirmationRequestDTO confirmationRequestDTO)
            throws IOException, RequestException, DatabaseException, SQLException {
        BillRepository billRepository = BillRepository.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        String billNumber = objectMapper.readValue(confirmationRequestDTO.getInfo(),
                BillRequestDTO.class).getBillNumber();
        if (!billNumber.matches("\\d+")) {
            throw new RequestException("Номер счета задан некорректно");
        }
        if (billRepository.checkBillExists(billNumber)) {
            throw new RequestException("Счет: " + billNumber + " уже существует");
        }
    }
}
