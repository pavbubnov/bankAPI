package com.bubnov.service;

import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.ConfirmationRepository;

import java.sql.SQLException;
import java.util.List;

public class ConfirmationService {

    private final ConfirmationRepository confirmationRepository;

    public ConfirmationService(ConfirmationRepository confirmationRepository) {
        this.confirmationRepository = confirmationRepository;
    }

    public ConfirmationResponseDTO postConfirmation(ConfirmationRequestDTO confirmationRequestDTO)
            throws DatabaseException, SQLException {
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

}
