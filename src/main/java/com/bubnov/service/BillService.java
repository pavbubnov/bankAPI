package com.bubnov.service;

import com.bubnov.controller.dto.account.AccountDTO;
import com.bubnov.controller.dto.bill.AmountResponseDTO;
import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;

public class BillService {

    private final BillRepository billRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public AmountResponseDTO getAmount(String request) throws DatabaseException, JsonProcessingException,
            RequestException {
        try {
            AmountResponseDTO amountResponse = new AmountResponseDTO(billRepository.getBillByNumber(request));
            if (amountResponse.getAmount() == null) {
                throw new RequestException("Счет : " + request + " не найден");
            }
            return amountResponse;
        } catch (SQLException throwables) {
            throw new DatabaseException("Ошибка базы данных");
        }
    }

    public BillRequestDTO postBill(ConfirmationResponseDTO responseDTO)
            throws DatabaseException, SQLException, IOException {
        BillRequestDTO requestDTO = objectMapper.readValue(responseDTO.getInfo(), BillRequestDTO.class);
        return billRepository.createBill(requestDTO);
    }
}
