package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FullCardDeckException;
import it.polimi.ingsw.model.AbilityChoice;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.network.InsertMarbleRequest;
import it.polimi.ingsw.network.Request;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestControllerTest {

    @Test
    public void processRequestTest() throws FullCardDeckException {
        Request request = new InsertMarbleRequest(2, AbilityChoice.STANDARD, 0,0);
        Game game = new Game(true);
        game.giveInkwell();
        MasterController masterController = new MasterController(game);
        //RequestController requestController = masterController.getRequestController();
        masterController.processRequest(request);

        Resource output = masterController.getResourceController().getToHandle();
        assertEquals(new Resource(0,1,1,0), output);
        assertEquals(0, game.getActivePlayer().getPlayerFaith());
    }
}