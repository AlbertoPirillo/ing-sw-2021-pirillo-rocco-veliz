package it.polimi.ingsw.network;

import it.polimi.ingsw.client.PlayerInterface;
import it.polimi.ingsw.model.LeaderCard;

import java.io.Serializable;
import java.util.List;

public class LeaderCardsUpdate extends ServerUpdate implements Serializable {
    private final List<LeaderCard> cards;

    public LeaderCardsUpdate(String activePlayer, boolean lastUpdate, List<LeaderCard> cards) {
        super(activePlayer, lastUpdate);
        this.cards = cards;
    }

    public List<LeaderCard> getCards(){
        return cards;
    }

    @Override
    public void update(PlayerInterface playerInterface) {
        if(super.getActivePlayer().equals(playerInterface.getNickname())) {
            playerInterface.viewInitialsLeadersCards(cards);
        }
    }

}
