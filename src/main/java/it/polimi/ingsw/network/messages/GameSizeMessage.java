package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.server.Connection;
import it.polimi.ingsw.server.Server;

public class GameSizeMessage extends Message {

    private final int size;

    public GameSizeMessage(String activePlayer, int size) {
        super(activePlayer);
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }

    @Override
    public void process(Server server, Connection connection) {
        server.setGameSize(size);
    }

    @Override
    public void update(UserInterface userInterface) {

    }
}
