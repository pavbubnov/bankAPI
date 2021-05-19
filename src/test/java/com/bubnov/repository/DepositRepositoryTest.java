package com.bubnov.repository;

import com.bubnov.controller.dto.deposit.DepositRequestDTO;
import com.bubnov.entity.Deposit;
import com.bubnov.exception.DatabaseException;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DepositRepositoryTest {

    DepositRepository depositRepository = DepositRepository.getInstance();
    H2Datasource datasource = new H2Datasource();
    String databasePath = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    String databaseScript = "src/main/resources/tests/testCardDatabase.sql";
    String databaseScriptDel = "src/main/resources/tests/deleteTestCardDatabase.sql";
    private static final String SELECT_DEPOSITS_BY_BILL = "SELECT * FROM DEPOSITS WHERE BILL_NUMBER = ?";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_BILL_NUMBER = "BILL_NUMBER";
    private static final String COLUMN_AMOUNT = "AMOUNT";

    @BeforeEach
    void setUp() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection(databasePath);
        depositRepository.setDatabasePath(databasePath);
        RunScript.execute(db, new FileReader(databaseScript));
    }

    @AfterEach
    void tearDown() throws DatabaseException, SQLException, FileNotFoundException {
        Connection db = datasource.setH2Connection(databasePath);
        RunScript.execute(db, new FileReader(databaseScriptDel));
    }

    @Test
    void createDeposit() throws DatabaseException, SQLException {
        DepositRequestDTO depositRequestDTO = new DepositRequestDTO("11111", BigDecimal.valueOf(123.99));
        DepositRequestDTO depositRequestDTO2 = new DepositRequestDTO("11111", BigDecimal.valueOf(99));
        depositRepository.createDeposit(depositRequestDTO);
        depositRepository.createDeposit(depositRequestDTO2);
        List<Deposit> deposits = getDeposit(depositRequestDTO.getBillNumber());

        List<Integer> ids = deposits.stream().map(deposit -> deposit.getId()).collect(Collectors.toList());
        List<Double> amounts = deposits.stream().map(deposit -> deposit.getAmount().doubleValue()).collect(Collectors.
                toList());
        List<String> billNumbers = deposits.stream().map(deposit -> deposit.getBillNumber()).collect(Collectors.
                toList());
        org.assertj.core.api.Assertions.assertThat(ids).containsExactly(1, 2);
        org.assertj.core.api.Assertions.assertThat(amounts).containsExactly(123.99, 99.00);
        org.assertj.core.api.Assertions.assertThat(billNumbers).containsExactly("11111", "11111");
    }

    private List<Deposit> getDeposit(String billNumber) throws SQLException, DatabaseException {
        try (Connection db = datasource.setH2Connection(databasePath);
             PreparedStatement preparedStatement = db.prepareStatement(SELECT_DEPOSITS_BY_BILL);
        ) {
            preparedStatement.setString(1, billNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Deposit> deposits = new ArrayList<>();
            while (resultSet.next()) {
                Deposit deposit = new Deposit();
                deposit.setId(resultSet.getInt(COLUMN_ID));
                deposit.setBillNumber(resultSet.getString(COLUMN_BILL_NUMBER));
                deposit.setAmount(resultSet.getBigDecimal(COLUMN_AMOUNT));
                deposits.add(deposit);
            }
            return deposits;
        }
    }
}