package com.bubnov.repository;

import com.bubnov.controller.dto.counterparty.CounterpartyDTO;
import com.bubnov.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CounterpartyRepository {

    private static CounterpartyRepository INSTANCE;
    private static final String INSERT_COUNTERPARTY =
            "INSERT INTO COUNTERPARTY(EMPLOYER_ID, COUNTERPARTY_ID) VALUES (?, ?)";
    private static final String CHECK_SELECT =
            "SELECT COUNT(*) FROM COUNTERPARTY WHERE EMPLOYER_ID = ? AND COUNTERPARTY_ID = ?";
    private static final String SELECT_COUNTERPARTIES =
            "SELECT COUNTERPARTY_ID FROM COUNTERPARTY WHERE EMPLOYER_ID = ?";

    private H2Datasource h2Datasource;

    private CounterpartyRepository() {
    }

    public void setH2Datasource(H2Datasource h2Datasource) {
        this.h2Datasource = h2Datasource;
    }

    public static CounterpartyRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CounterpartyRepository();
        }
        return INSTANCE;
    }

    public CounterpartyDTO createCounterparty(CounterpartyDTO counterpartyDTO) throws SQLException, DatabaseException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(INSERT_COUNTERPARTY);
        ) {
            preparedStatement.setInt(1, counterpartyDTO.getEmployer_id());
            preparedStatement.setInt(2, counterpartyDTO.getCounterparty_id());
            preparedStatement.execute();
            return counterpartyDTO;
        }
    }

    public boolean checkThatCounterParty(int employer_id, int counterparty_id) throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(CHECK_SELECT);
        ) {
            preparedStatement.setInt(1, employer_id);
            preparedStatement.setInt(2, counterparty_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    return true;
                }
            }
            return false;
        }
    }

    public List<Integer> getCounterparties(int employer_id) throws DatabaseException, SQLException {
        try (Connection db = h2Datasource.setH2Connection();
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_COUNTERPARTIES);
        ) {
            preparedStatement.setInt(1, employer_id);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> counterparties = new ArrayList<>();
            while (resultSet.next()) {
                int counterparty = resultSet.getInt(1);
                counterparties.add(counterparty);
            }
            return counterparties;
        }
    }

}
