package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import org.junit.jupiter.api.Test;

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
                () -> depot.modifyLayer(1,ResourceType.COIN, 5));
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
                () ->depot.modifyLayer(2, ResourceType.COIN, -5));

        assertThrows(NotEnoughSpaceException.class,
                () ->depot.modifyLayer(2, ResourceType.COIN, 2));

        assertThrows(LayerNotEmptyException.class,
                () ->depot.modifyLayer(2, ResourceType.SHIELD, 1));

        assertThrows(CannotContainFaithException.class,
                () ->depot.modifyLayer(2, ResourceType.FAITH, 2));
    }

    @Test
    public void getAllResTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidKeyException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot =  new ConcreteDepot();
        depot.modifyLayer(1, ResourceType.STONE, 1);
        depot.modifyLayer(2, ResourceType.SHIELD, 2);
        Resource actual = depot.queryAllRes();
        Resource expected = new Resource(1, 0, 2 ,0);

        for(ResourceType key: ResourceType.values()) {
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
        depot.moveResources(2, 2, 3);

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
                () -> depot.moveResources(2, 2, 1));

        assertThrows(NegativeResAmountException.class,
                () -> depot.moveResources(-5, 2, 1));
    }

    @Test
    public void moveNotEnoughTest() throws CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.SHIELD, 1);

        assertThrows(NotEnoughResException.class,
                () -> depot.moveResources(3,3, 2));
    }

    @Test
    public void noMoveTest() throws
            CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(2, ResourceType.COIN, 2);
        depot.moveResources(0, 2, 1);

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
        depot.moveResources(1, 2, 1);

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
                () -> depot.moveResources(4, 3, 2));

        assertThrows(NotEnoughSpaceException.class,
                () -> depot.moveResources(2, 3, 1));
    }

    @Test
    public void swapAlreadyPresTest() throws CannotContainFaithException, LayerNotEmptyException, NegativeResAmountException, NotEnoughSpaceException, NotEnoughResException, InvalidLayerNumberException, AlreadyInAnotherLayerException, InvalidResourceException {
        Depot depot = new ConcreteDepot();
        depot.modifyLayer(3, ResourceType.COIN, 1);
        depot.modifyLayer(2, ResourceType.SHIELD, 2);
        depot.moveResources(2, 2, 3);

        assertEquals(ResourceType.SHIELD, depot.getLayer(3).getResource());
        assertEquals(2, depot.getLayer(3).getAmount());
        assertEquals(ResourceType.COIN, depot.getLayer(2).getResource());
        assertEquals(1, depot.getLayer(2).getAmount());
    }

     @Test
     public void retrieveTest() throws AlreadyInAnotherLayerException, CannotContainFaithException, NotEnoughSpaceException, NegativeResAmountException, LayerNotEmptyException, InvalidLayerNumberException, NotEnoughResException, InvalidKeyException, InvalidResourceException {
        Resource res = new Resource(0,1,3,1);
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
    public void discardTest() {
        //TODO: Test discard
    }
}
