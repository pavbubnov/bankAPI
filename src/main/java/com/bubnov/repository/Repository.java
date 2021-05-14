package com.bubnov.repository;

import com.bubnov.entity.Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Repository {

    private static Repository INSTANCE;
    private Connection db;

    private Repository() {

    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Repository();
        }
        return INSTANCE;

    }

    public void getH2Connection() throws SQLException {

        this.db = DriverManager.getConnection("jdbc:h2:file:/Users/a19189145/Documents" +
                "/workProjects/bankAPI/src/main/resources/database;MV_STORE=false");

    }

    public void closeConnection() {
        try {
            this.db.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void createStart() {
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(Query.CREATE_TABLE);
            preparedStatement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void postCity(Account account) {


        try {
            PreparedStatement preparedStatement = this.db.prepareStatement("INSERT INTO ACCOUNT(NAME) VALUES (?)");
            //preparedStatement.setInt(1, 1);
            preparedStatement.setString(1, account.getNameOfPerson());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
