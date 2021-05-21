package com.bubnov.repository;

import com.bubnov.controller.dto.confirmation.ConfirmationRequestDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationResponseDTO;
import com.bubnov.controller.dto.confirmation.ConfirmationUpdateDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfirmationRepository {

    private static ConfirmationRepository INSTANCE;
    private static final String INSERT_CONFIRM = "INSERT INTO CONFIRMATION(ENTITY_NAME, OPERATION, INFO, " +
            "CONFIRMATION_STATUS) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE CONFIRMATION SET CONFIRMATION_STATUS = ? WHERE ID = ?";
    private static final String SELECT_UNCONFIRM = "SELECT * FROM CONFIRMATION WHERE CONFIRMATION_STATUS = 'NOT_CONFIRMED'";
    private static final String SELECT_CONFIRM = "SELECT * FROM CONFIRMATION WHERE ID = ?";

    private H2Datasource h2Datasource;

    private ConfirmationRepository() {
    }

    public void setH2Datasource(H2Datasource h2Datasource) {
        this.h2Datasource = h2Datasource;
    }

    public static ConfirmationRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfirmationRepository();
        }
        return INSTANCE;
    }

    public List<ConfirmationResponseDTO> getALLNotConfirm() throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_UNCONFIRM);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ConfirmationResponseDTO> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(new ConfirmationResponseDTO(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
            }
            return list;
        }
    }

    public ConfirmationResponseDTO getConfirmById(int id) throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_CONFIRM);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ConfirmationResponseDTO> list = new ArrayList<>();
            resultSet.next();
            return new ConfirmationResponseDTO(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
        }
    }

    public ConfirmationResponseDTO createConfirmation(ConfirmationRequestDTO confirmationRequestDTO)
            throws SQLException, DatabaseException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_CONFIRM, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, confirmationRequestDTO.getEntityName());
            preparedStatement.setString(2, confirmationRequestDTO.getOperation());
            preparedStatement.setString(3, confirmationRequestDTO.getInfo());
            preparedStatement.setString(4, confirmationRequestDTO.getConfirmationStatus());
            int id = preparedStatement.executeUpdate();
            return new ConfirmationResponseDTO(id, confirmationRequestDTO);
        }
    }

    public ConfirmationUpdateDTO updateStatus(ConfirmationUpdateDTO updateDTO)
            throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement paymentStatement = db.prepareStatement(UPDATE_STATUS);
        ) {
            paymentStatement.setString(1, updateDTO.getStatus());
            paymentStatement.setInt(2, updateDTO.getId());
            paymentStatement.executeUpdate();
            return updateDTO;
        }
    }
}
