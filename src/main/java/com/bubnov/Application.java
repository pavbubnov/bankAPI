package com.bubnov;

import com.bubnov.controller.AccountController;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.Query;
import com.bubnov.repository.Repository;
import com.bubnov.service.AccountService;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        Repository repository = Repository.getInstance();
        try {
            repository.getH2Connection();
            repository.createStart(Query.startQueryList());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        AccountService accountService = new AccountService(repository);
        AccountController accountController = new AccountController(accountService);
        accountController.startController();
    }

}
