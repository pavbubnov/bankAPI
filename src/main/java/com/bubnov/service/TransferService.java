package com.bubnov.service;

import com.bubnov.controller.dto.transfer.TransferDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.exception.RequestException;
import com.bubnov.repository.BillRepository;
import com.bubnov.repository.CounterpartyRepository;
import com.bubnov.repository.TransferRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransferService {

    private final TransferRepository transferRepository;
    private final BillRepository billRepository;
    private final CounterpartyRepository counterpartyRepository;

    public TransferService(TransferRepository transferRepository, BillRepository billRepository,
                           CounterpartyRepository counterpartyRepository) {
        this.transferRepository = transferRepository;
        this.billRepository = billRepository;
        this.counterpartyRepository = counterpartyRepository;
    }

    public TransferDTO postTransfer(TransferDTO transferDTO) throws DatabaseException, SQLException, RequestException {
        String senderBill = transferDTO.getSenderBillNumber();
        String recipientBill = transferDTO.getRecipientBillNumber();
        BigDecimal amount = transferDTO.getAmount();
        if (amount.signum() != 1) {
            throw new RequestException("Сумма перевода должна быть положительной");
        }
        if (!billRepository.checkBillExists(senderBill)) {
            throw new RequestException("Счет " + senderBill + " не создан");
        } else if (!billRepository.checkBillExists(recipientBill)) {
            throw new RequestException("Счет " + recipientBill + " не создан");
        }
        int senderAccountId = billRepository.getBillByNumber(senderBill).getAccountId();
        int recipientAccountId = billRepository.getBillByNumber(recipientBill).getAccountId();
        if (!counterpartyRepository.checkThatCounterParty(senderAccountId, recipientAccountId)) {
            throw new RequestException("Получатель не является контрагентом отправителя, создайте запрос на " +
                    "добавление контрагента");
        }
        BigDecimal senderNewAmount = billRepository.getBillByNumber(senderBill).getAmount().
                subtract(amount);
        BigDecimal recipientNewAmount = billRepository.getBillByNumber(recipientBill).getAmount().
                add(amount);
        if (senderNewAmount.signum() == -1) {
            throw new RequestException("У отправителя недостаточно средств для совершения перевода");
        }
        try {
            transferRepository.doTransactionalTransfer(transferDTO, senderNewAmount, recipientNewAmount);
        } catch (Exception e) {
            throw new DatabaseException("Трансфер не удался");
        }
        return transferRepository.createTransfer(transferDTO);
    }
}
