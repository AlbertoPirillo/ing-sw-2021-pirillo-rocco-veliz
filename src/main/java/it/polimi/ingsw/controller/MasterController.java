package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidKeyException;
import it.polimi.ingsw.exceptions.NegativeResAmountException;
import it.polimi.ingsw.model.ClientError;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TempResource;
import it.polimi.ingsw.network.requests.Request;

public class MasterController {

    private final ClientError clientError;
    private final PlayerController playerController;
    private final ResourceController resourceController;
    private final RequestController requestController;
    private final SetupController setupController;
    private final Game game;

    public MasterController(Game game) {
        this.clientError = new ClientError();
        TempResource tempRes = new TempResource();
        this.playerController = new PlayerController(this);
        this.resourceController = new ResourceController(this, tempRes);
        this.requestController = new RequestController(this);
        this.setupController = new SetupController(this);
        this.game = game;
    }

    public ClientError getClientError() {
        return clientError;
    }

    public String getError() {
        return this.clientError.getError();
    }

    public void setException(Throwable exception) {
        this.clientError.setException(exception);
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public ResourceController getResourceController() {
        return resourceController;
    }

    public RequestController getRequestController() {
        return requestController;
    }

    public SetupController getSetupController() {
        return setupController;
    }

    public Game getGame() {
        return game;
    }

    public void processRequest(Request request) {
        request.activateRequest(this);
       // this.getGame().updateClientModel();
    }

    //Testing
    public void simulateGame(){
        try {
            game.nextTurn();
        } catch (NegativeResAmountException | InvalidKeyException e) {
            e.printStackTrace();
        }
        game.updateMarketTray();
        game.updateMarket();
        game.notifyEndOfUpdates();
    }
}