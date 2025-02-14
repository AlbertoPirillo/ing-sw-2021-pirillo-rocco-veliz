package it.polimi.ingsw.network.updates;

import it.polimi.ingsw.client.UserInterface;
import it.polimi.ingsw.model.LeaderCard;

import java.util.List;
import java.util.Map;

public class LeaderUpdate extends ServerUpdate {

    private final Map<String, List<LeaderCard>> leaderMap;

    public LeaderUpdate(String activePlayer, Map<String, List<LeaderCard>> leaderMap) {
        super(activePlayer);
        this.leaderMap = leaderMap;
    }

    public Map<String, List<LeaderCard>> getLeaderMap() {
        return leaderMap;
    }

    @Override
    public void update(UserInterface userInterface) {
        userInterface.updateLeaderCards(this);
    }
}
