package it.polimi.ingsw.network.updates;

import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.model.ClientError;

public class ErrorUpdate extends ServerUpdate {

    private final ClientError clientError;

    public ErrorUpdate(String activePlayer, ClientError clientError) {
        super(activePlayer);
        this.clientError = clientError;
    }

    public ClientError getClientError() {
        return clientError;
    }

    @Override
    public void update(UserInterface userInterface) {
        userInterface.displayError(this);
    }
}
