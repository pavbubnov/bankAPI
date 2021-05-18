//package com.bubnov.repository;
//
//import com.bubnov.exception.DatabaseException;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class H2DatasourceTest {
//
//    @Test
//    public void getH2Connection() throws SQLException, DatabaseException {
//        H2Datasource h2Datasource = new H2Datasource();
//        try(Connection connection = h2Datasource.setH2Connection()) {
//            assertTrue(connection.isValid(1));
//            assertFalse(connection.isClosed());
//        }
//    }
//}