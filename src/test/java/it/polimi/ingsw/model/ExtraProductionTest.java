package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtraProductionTest {

    @Test
    void activate() {
        //stub
        Game game = new Game(); //Stub
        Player player = new Player(false, "abc", game, 1, 1);
        Resource resource1 = new Resource(1,2,3,4);
        Resource resource2 = new Resource(4,3,2,1);
        LeaderAbility ability = new ExtraProduction(new ProductionPower(resource1, resource2));
        ResLeaderCard res = new ResLeaderCard(1, ability ,resource1);

        assertNull(player.getProdStrategy());
        res.getSpecialAbility().activate(player);
        assertNotNull(player.getProdStrategy());
        assertEquals(player.getProdStrategy().getProduction()[0].getProduction().getInput().getAllRes(), resource1.getAllRes());
        assertEquals(player.getProdStrategy().getProduction()[0].getProduction().getOutput().getAllRes(), resource2.getAllRes());

        new ResLeaderCard(2, new ExtraProduction(new ProductionPower(resource2, resource1)), resource1).getSpecialAbility().activate(player);
        //correct add to player
        assertEquals(player.getProdStrategy().getProduction()[0].getProduction().getInput().getAllRes(), resource1.getAllRes());
        assertEquals(player.getProdStrategy().getProduction()[0].getProduction().getOutput().getAllRes(), resource2.getAllRes());
        assertEquals(player.getProdStrategy().getProduction()[1].getProduction().getInput().getAllRes(), resource2.getAllRes());
        assertEquals(player.getProdStrategy().getProduction()[1].getProduction().getOutput().getAllRes(), resource1.getAllRes());
    }
}