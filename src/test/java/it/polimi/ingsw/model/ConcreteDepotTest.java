package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.network.DepotSetting;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConcreteDepotTest {

    @Test
    public void buildTest() throws InvalidLayerNumberException {
        Depot depot = new ConcreteDepot();
        assertNull(depot.getLayer(1).getResource());
        assertEquals(0, depot.getLayer(1).getAmount());
        assertNull(depot.getLayer(2).getResource());
        assertEquals(0, depot.getLayer(2).getAmount());
        assertNull(depot.getLayer(3).getResource());
        assertEquals(0, depot.getLayer(3).getAmount());
    }

    @Test
    public void getTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.COIN, 1);
        assertEquals(ResourceType.COIN, depot.getLayer(1).getResource());
        assertEquals(1, depot.getLayer(1).getAmount());
        assertThrows(InvalidLayerNumberException.class,
                () -> depot.getLayer(4));
        assertThrows(InvalidLayerNumberException.class,
                () -> depot.getLayer(-3));
    }

    @Test
    public void setTest() throws AlreadyInAnotherLayerException, CannotContainFaithException, NotEnoughSpaceException, NegativeResAmountException, LayerNotEmptyException, InvalidLayerNumberException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        assertThrows(NotEnoughSpaceException.class,
                () -> depot.modifyLayer(1, ResourceType.COIN, 5));
        assertThrows(CannotContainFaithException.class,
                () -> depot.modifyLayer(2, ResourceType.FAITH, 5));
        assertThrows(NegativeResAmountException.class,
                () -> depot.modifyLayer(3, ResourceType.COIN, -3));
        depot.modifyLayer(1, ResourceType.SHIELD, 1);
        assertThrows(AlreadyInAnotherLayerException.class, () -> depot.modifyLayer(2, ResourceType.SHIELD, 2));
    }

    @Test
    public void overrideTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.SERVANT, 1);
        assertThrows(LayerNotEmptyException.class,
                () -> depot.modifyLayer(3, ResourceType.COIN, 2));
    }

    @Test
    public void modifyTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.modifyLayer(2, ResourceType.COIN, -1);
        assertEquals(1, depot.getLayer(2).getAmount());

        assertThrows(NegativeResAmountException.class,
                () -> depot.modifyLayer(2, ResourceType.COIN, -5));

        assertThrows(NotEnoughSpaceException.class,
                () -> depot.modifyLayer(2, ResourceType.COIN, 2));

        assertThrows(LayerNotEmptyException.class,
                () -> depot.modifyLayer(2, ResourceType.SHIELD, 1));

        assertThrows(CannotContainFaithException.class,
                () -> depot.modifyLayer(2, ResourceType.FAITH, 2));
    }

    @Test
    public void getAllResTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidKeyException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.STONE, 1);
        depot.modifyLayer(2, ResourceType.SHIELD, 2);
        Resource actual = depot.queryAllRes();
        Resource expected = new Resource(1, 0, 2, 0);

        for (ResourceType key : ResourceType.values()) {
            if (key != ResourceType.FAITH && key != ResourceType.ALL) {
                assertEquals(expected.getValue(key), actual.getValue(key));
            }
        }
    }

    @Test
    public void moveToEmptyTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.moveResources(2, 3, 2);

        assertEquals(0, depot.getLayer(2).getAmount());
        assertNull(depot.getLayer(2).getResource());
        assertEquals(2, depot.getLayer(3).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(3).getResource());
    }

    @Test
    public void moveExceptionTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);

        assertThrows(NotEnoughSpaceException.class,
                () -> depot.moveResources(2, 1, 2));

        assertThrows(NegativeResAmountException.class,
                () -> depot.moveResources(2, 1, -5));
    }

    @Test
    public void moveNotEnoughTest() throws CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.SHIELD, 1);

        assertThrows(NotEnoughResException.class,
                () -> depot.moveResources(3, 2, 3));
    }

    @Test
    public void noMoveTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.moveResources(2, 1, 0);

        assertEquals(2, depot.getLayer(2).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(0, depot.getLayer(1).getAmount());
        assertNull(depot.getLayer(1).getResource());
    }

    @Test
    public void swapOkTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.COIN, 1);
        depot.modifyLayer(2, ResourceType.SHIELD, 1);
        depot.moveResources(2, 1, 1);

        assertEquals(ResourceType.SHIELD, depot.getLayer(1).getResource());
        assertEquals(1, depot.getLayer(1).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(1, depot.getLayer(2).getAmount());
    }

    @Test
    public void swapExceptionTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.COIN, 2);
        depot.modifyLayer(2, ResourceType.SHIELD, 1);

        assertThrows(NotEnoughResException.class,
                () -> depot.moveResources(3, 2, 4));

        assertThrows(NotEnoughSpaceException.class,
                () -> depot.moveResources(3, 1, 2));
    }

    @Test
    public void swapAlreadyPresTest() throws CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.COIN, 1);
        depot.modifyLayer(2, ResourceType.SHIELD, 2);
        depot.moveResources(2, 3, 2);

        assertEquals(ResourceType.SHIELD, depot.getLayer(3).getResource());
        assertEquals(2, depot.getLayer(3).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(1, depot.getLayer(2).getAmount());
    }

    @Test
    public void retrieveTest() throws AlreadyInAnotherLayerException, CannotContainFaithException, NotEnoughSpaceException, NegativeResAmountException, LayerNotEmptyException, InvalidLayerNumberException, NotEnoughResException, InvalidKeyException, InvalidResourceException {
        Resource res = new Resource(0, 1, 3, 1);
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.SERVANT, 1);
        depot.modifyLayer(2, ResourceType.COIN, 2);
        assertThrows(NotEnoughResException.class, () -> depot.retrieveRes(res));
        depot.modifyLayer(3, ResourceType.SHIELD, 3);
        depot.retrieveRes(res);
        Resource content = depot.queryAllRes();
        assertEquals(0, content.getValue(ResourceType.STONE));
        assertEquals(1, content.getValue(ResourceType.COIN));
        assertEquals(0, content.getValue(ResourceType.SHIELD));
        assertEquals(0, content.getValue(ResourceType.SERVANT));
    }

    @Test
    public void takeTest() throws NegativeResAmountException, InvalidResourceException, LayerNotEmptyException, NotEnoughSpaceException, InvalidLayerNumberException, CannotContainFaithException, AlreadyInAnotherLayerException, NotEnoughResException {
        Depot depot = new ConcreteDepot();
        ExtraSlotAbility extraSlot = new ExtraSlotAbility(ResourceType.COIN);
        depot = new ConcreteDepotDecorator(depot, extraSlot);
        depot.modifyLayer(3, ResourceType.COIN, 2);
        depot.modifyLayer(4, ResourceType.COIN, 2);
        Resource res = new Resource();
        res.addResource(ResourceType.COIN, 3);
        depot.retrieveRes(res);
        assertNull(depot.getLayer(1).getResource());
        assertNull(depot.getLayer(2).getResource());
        assertNull(depot.getLayer(3).getResource());
        assertEquals(1, depot.getLayer(4).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(4).getResource());
    }

    @Test
    public void discardTest() throws FullCardDeckException, CannotContainFaithException, NegativeResAmountException {
        Player player1 = new Player("a");
        Player player2 = new Player("b");
        Player player3 = new Player("c");

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);

        Game game = new MultiGame(3, playerList);
        player1.setGame(game);
        player2.setGame(game);
        player3.setGame(game);
        Depot depot1 = player1.getPersonalBoard().getDepot();

        Resource KO = new Resource();
        KO.addResource(ResourceType.COIN, 2);
        KO.addResource(ResourceType.FAITH, 1);
        assertThrows(CannotContainFaithException.class, () -> depot1.discardRes(player1, KO));

        Resource OK = new Resource(0, 2, 0, 2);
        depot1.discardRes(player1, OK);

        assertEquals(new Resource(0, 0, 0, 0), depot1.queryAllRes());
        assertEquals(0, player1.getPlayerFaith());
        assertEquals(4, player2.getPlayerFaith());
        assertEquals(4, player3.getPlayerFaith());
    }

    @Test
    public void toDepotSettingTest() throws InvalidResourceException, LayerNotEmptyException, NotEnoughSpaceException, InvalidLayerNumberException, CannotContainFaithException, AlreadyInAnotherLayerException, NegativeResAmountException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.SERVANT, 1);
        depot.modifyLayer(3, ResourceType.SHIELD, 2);
        List<DepotSetting> settings = depot.toDepotSetting();

        assertEquals(1, settings.get(0).getLayerNumber());
        assertEquals(2, settings.get(1).getLayerNumber());
        assertEquals(3, settings.get(2).getLayerNumber());
        assertEquals(1, settings.get(0).getAmount());
        assertEquals(0, settings.get(1).getAmount());
        assertEquals(2, settings.get(2).getAmount());
        assertEquals(ResourceType.SERVANT, settings.get(0).getResType());
        assertNull(settings.get(1).getResType());
        assertEquals(ResourceType.SHIELD, settings.get(2).getResType());

        ExtraSlotAbility extraSlot = new ExtraSlotAbility(ResourceType.COIN);
        depot = new ConcreteDepotDecorator(depot, extraSlot);
        depot.modifyLayer(4, ResourceType.COIN, 2);
        settings = depot.toDepotSetting();

        assertEquals(1, settings.get(0).getLayerNumber());
        assertEquals(2, settings.get(1).getLayerNumber());
        assertEquals(3, settings.get(2).getLayerNumber());
        assertEquals(4, settings.get(3).getLayerNumber());
        assertEquals(1, settings.get(0).getAmount());
        assertEquals(0, settings.get(1).getAmount());
        assertEquals(2, settings.get(2).getAmount());
        assertEquals(2, settings.get(3).getAmount());
        assertEquals(ResourceType.SERVANT, settings.get(0).getResType());
        assertNull(settings.get(1).getResType());
        assertEquals(ResourceType.SHIELD, settings.get(2).getResType());
        assertEquals(ResourceType.COIN, settings.get(3).getResType());
    }

    @Test
    public void setFromSettingsKOTest() throws InvalidLayerNumberException, InvalidResourceException, LayerNotEmptyException, NotEnoughSpaceException, CannotContainFaithException, AlreadyInAnotherLayerException, NegativeResAmountException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.modifyLayer(3, ResourceType.SHIELD, 2);
        assertThrows(WrongDepotInstructionsException.class,
                () -> depot.setFromDepotSetting(null));
        assertNull(depot.getLayer(1).getResource());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(ResourceType.SHIELD, depot.getLayer(3).getResource());
        assertEquals(2, depot.getLayer(2).getAmount());
        assertEquals(2, depot.getLayer(3).getAmount());
    }

    @Test
    public void setFromSettingsTest() throws InvalidLayerNumberException, InvalidResourceException, LayerNotEmptyException, NotEnoughSpaceException, CannotContainFaithException, AlreadyInAnotherLayerException, NegativeResAmountException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.modifyLayer(3, ResourceType.SHIELD, 2);
        List<DepotSetting> settings = new ArrayList<>();
        settings.add(new DepotSetting(1, ResourceType.COIN, 1));
        settings.add(new DepotSetting(2, ResourceType.COIN, 1));
        settings.add(new DepotSetting(3, ResourceType.SHIELD, 2));
        assertThrows(WrongDepotInstructionsException.class,
                () -> depot.setFromDepotSetting(settings));
        assertNull(depot.getLayer(1).getResource());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(ResourceType.SHIELD, depot.getLayer(3).getResource());
        assertEquals(2, depot.getLayer(2).getAmount());
        assertEquals(2, depot.getLayer(3).getAmount());
    }

    @Test
    public void setFromSettingsOKTest() throws InvalidLayerNumberException, InvalidResourceException, LayerNotEmptyException, NotEnoughSpaceException, CannotContainFaithException, AlreadyInAnotherLayerException, NegativeResAmountException, WrongDepotInstructionsException {
        Depot depot = new ConcreteDepot();
        ExtraSlotAbility extraSlot = new ExtraSlotAbility(ResourceType.SERVANT);
        depot = new ConcreteDepotDecorator(depot, extraSlot);
        depot.modifyLayer(2, ResourceType.COIN, 1);
        depot.modifyLayer(3, ResourceType.SHIELD, 2);
        depot.modifyLayer(4, ResourceType.SERVANT, 1);
        List<DepotSetting> settings = new ArrayList<>();
        settings.add(new DepotSetting(1, ResourceType.COIN, 1));
        settings.add(new DepotSetting(2, ResourceType.SHIELD, 2));
        settings.add(new DepotSetting(4, ResourceType.SERVANT, 1));
        depot.setFromDepotSetting(settings);
        assertEquals(ResourceType.COIN, depot.getLayer(1).getResource());
        assertEquals(ResourceType.SHIELD, depot.getLayer(2).getResource());
        assertNull(depot.getLayer(3).getResource());
        assertEquals(ResourceType.SERVANT, depot.getLayer(4).getResource());
        assertEquals(1, depot.getLayer(1).getAmount());
        assertEquals(2, depot.getLayer(2).getAmount());
        assertEquals(0, depot.getLayer(3).getAmount());
        assertEquals(1, depot.getLayer(4).getAmount());
    }
}
