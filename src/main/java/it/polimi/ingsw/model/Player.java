package it.polimi.ingsw.model;

import java.util.*;

public abstract class Player {

    public Player() {
    }

    private boolean hasInkwell;

    private String nickname;

    private boolean isHisTurn;

    private LeaderCard[] leaderCards;






    public void takeResources(Resource[] resources) {
        // TODO implement here
    }

    public void buyDevCard(DevelopmentCard devCard) {
        // TODO implement here
    }

    public void activateProduction(DevelopmentCard[] cards) {
        // TODO implement here
    }

    public void useLeader(LeaderCard card, LeaderAction choice) {
        // TODO implement here
    }

    public LeaderCard[] chooseLeaderCard(LeaderCard[] leader) {
        // TODO implement here
        return null;
    }

}