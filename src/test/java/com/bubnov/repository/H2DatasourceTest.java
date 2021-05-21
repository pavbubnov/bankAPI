package com.bubnov.repository;

import com.bubnov.exception.DatabaseException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class H2DatasourceTest {

    @Test
    public void getH2Connection() throws SQLException, DatabaseException {
        FileInputStream fileInputStream;
        Properties property = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/path.properties");
            property.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String databasePath = property.getProperty("database.path");
        H2Datasource h2Datasource = new H2Datasource(databasePath);
        try (Connection connection = h2Datasource.setH2Connection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }
}