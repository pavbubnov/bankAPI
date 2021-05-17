package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillRequestDTO;
import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

public class BillService {

    private final BillRepository billRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public String getAmount (BillRequestDTO requestDTO) throws DatabaseException, JsonProcessingException, RequestException {
        try {
            BillResponseDTO billByNumber = billRepository.getBillByNumber(requestDTO.getBillNumber());
            if (billByNumber.getAmount() == null) {
                throw new RequestException("Не удалось найти баланс счета: " + requestDTO.getBillNumber());
            }
            return objectMapper.writeValueAsString(billByNumber.getAmount());
        } catch (SQLException throwables) {
            throw new DatabaseException("Ошибка базы данных");
        }
    }

}
