package it.polimi.ingsw.network.requests;

import it.polimi.ingsw.controller.MasterController;

public class EndTurnRequest extends Request {

    public void activateRequest(MasterController masterController) {
        masterController.getPlayerController().endTurn();
    }
}