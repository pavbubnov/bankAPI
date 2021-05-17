package com.bubnov.service;

import com.bubnov.controller.dto.bill.BillResponseDTO;
import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.controller.dto.deposit.DepositResponseDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.DepositRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.SQLException;

public class DepositService {

    private final DepositRepository depositRepository;
    private final BillRepository billRepository;


    public DepositService(DepositRepository depositRepository, BillRepository billRepository) {
        this.depositRepository = depositRepository;
        this.billRepository = billRepository;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    public String postDeposit (DepositRequestDTO requestDTO) throws DatabaseException, JsonProcessingException, RequestException {
        DepositResponseDTO response = new DepositResponseDTO();
        response.setBillNumber(requestDTO.getBillNumber());
        response.setAmountDeposit(requestDTO.getAmount());
        try {
            BillResponseDTO billByNumber = billRepository.getBillByNumber(requestDTO.getBillNumber());
            BigDecimal amountAfter = requestDTO.getAmount().add(billByNumber.getAmount());
            response.setAmountAfter(amountAfter);
            billRepository.changeAmount(requestDTO.getBillNumber(), amountAfter);
        } catch (SQLException throwables) {
            throw new RequestException("Не удалось найти счет: " + requestDTO.getBillNumber());
        }
        return objectMapper.writeValueAsString(response);
    }
}