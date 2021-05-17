package com.bubnov;

import com.bubnov.controller.CardsController;
import com.bubnov.controller.ControllerHandler;
import com.bubnov.exception.DatabaseException;
import com.bubnov.repository.Query;
import com.bubnov.repository.CardRepository;
import com.bubnov.service.CardService;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        CardRepository cardRepository = CardRepository.getInstance();
        try {
            cardRepository.getH2Connection();
            cardRepository.createStart(Query.startQueryList());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        CardService cardService = new CardService(cardRepository);
        CardsController cardsController = new CardsController(cardService);
        ControllerHandler controllerHandler = new ControllerHandler(cardsController);
        controllerHandler.startController();
    }

}
