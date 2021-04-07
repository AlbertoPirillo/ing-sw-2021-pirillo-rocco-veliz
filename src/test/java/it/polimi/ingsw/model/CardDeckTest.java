package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.DeckEmptyException;
import it.polimi.ingsw.exceptions.FullCardDeckException;
import it.polimi.ingsw.exceptions.InvalidKeyException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CardDeckTest {

    @Test
    public void addCard() throws FullCardDeckException {
        CardDeck deck = new CardDeck();
        assertEquals(deck.getNumbersOfCards(), 0);
        //stub
        deck.addCard(new DevelopmentCard(1, new Resource(), CardColor.BLUE, 1, new ProductionPower(new Resource(), new Resource())));
        deck.addCard(new DevelopmentCard(2, new Resource(), CardColor.GREEN, 2, new ProductionPower(new Resource(), new Resource())));
        deck.addCard(new DevelopmentCard(3, new Resource(), CardColor.YELLOW, 3, new ProductionPower(new Resource(), new Resource())));
        assertEquals(deck.getNumbersOfCards(), 3);
    }

    @RepeatedTest(10)
    public void shuffleTest() throws FullCardDeckException {
        CardDeck deck = new CardDeck();
        //stub
        Resource res1 = new Resource(1,2,3,4);
        Resource res2 = new Resource(4,3,2,1);
        Resource res3 = new Resource(2,4,1,3);
        DevelopmentCard dev1 = new DevelopmentCard(1, res1, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        DevelopmentCard dev2 = new DevelopmentCard(2, res2, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        DevelopmentCard dev3 = new DevelopmentCard(3, res3, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        deck.addCard(dev1);
        deck.addCard(dev2);
        deck.addCard(dev3);
        assertEquals(deck.getNumbersOfCards(), 3);
        DevelopmentCard devCard = deck.removeCard();
        assertEquals(deck.getNumbersOfCards(), 2);
        DevelopmentCard devCard1 = deck.removeCard();
        assertEquals(deck.getNumbersOfCards(), 1);
        boolean bool = devCard.equals(dev2) || devCard.equals(dev3) || devCard1.equals(dev3);
        assertTrue(bool);
    }

    @Test
    public void LevelTest() throws FullCardDeckException, DeckEmptyException {
        CardDeck deck = new CardDeck();
        assertThrows(DeckEmptyException.class, () -> deck.getLevel());
        DevelopmentCard devCard = new DevelopmentCard(5, new Resource(), CardColor.BLUE, 2, new ProductionPower(new Resource(), new Resource()));
        deck.addCard(devCard);
        assertEquals(deck.getLevel(), 2);
    }

    @Test
    public void getCard() throws FullCardDeckException {
        CardDeck deck = new CardDeck();
        assertNull(deck.getCard());
        Resource res = new Resource(1,2,3,4);
        Resource res1 = new Resource(1,2,3,2);
        deck.addCard(new DevelopmentCard( 10, res, CardColor.BLUE, 1, new ProductionPower(res, res1)));
        DevelopmentCard card = deck.removeCard();
        //return the card
        assertEquals(card.getType(), CardColor.BLUE);
        assertEquals(card.getLevel(), 1);
        assertEquals(card.getResource().getAllRes(), res.getAllRes());
        assertEquals(card.getProdPower().getInput().getAllRes(), res.getAllRes());
        assertEquals(card.getProdPower().getOutput().getAllRes(), res1.getAllRes());
        //remove the card
        assertEquals(deck.getNumbersOfCards(),0);
    }

    @Test
    public void emptyTest() throws FullCardDeckException {
        CardDeck deck = new CardDeck();
        assertNull(deck.getCard());//return null
        Resource res1 = new Resource(1,2,3,4);
        Resource res2 = new Resource(4,3,2,1);
        Resource res3 = new Resource(2,4,1,3);
        DevelopmentCard dev1 = new DevelopmentCard(2, res1, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        DevelopmentCard dev2 = new DevelopmentCard(3, res2, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        DevelopmentCard dev3 = new DevelopmentCard(4, res3, CardColor.BLUE, 2, new ProductionPower(res1, res2));
        deck.addCard(dev1);
        deck.addCard(dev2);
        deck.addCard(dev3);
        deck.removeCard();
        deck.removeCard();
        deck.removeCard();
        assertNull(deck.getCard());//return null
    }
}