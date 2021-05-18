package com.bubnov.repository;

import com.bubnov.exception.DatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class H2ConnectionUtilsTest {

    @Test
    public void getH2Connection() throws SQLException, DatabaseException {
        H2ConnectionUtils h2ConnectionUtils = new H2ConnectionUtils();
        try(Connection connection = h2ConnectionUtils.getH2Connection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }
}