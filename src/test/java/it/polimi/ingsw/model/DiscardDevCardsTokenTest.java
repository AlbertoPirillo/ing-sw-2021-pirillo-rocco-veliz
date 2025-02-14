package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.FullCardDeckException;
import it.polimi.ingsw.exceptions.NegativeResAmountException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscardDevCardsTokenTest {

    @Test
    void revealTest() throws FullCardDeckException, NegativeResAmountException {
        SoloGame game = new SoloGame(new Player("a"));
        SoloActionToken token = new DiscardDevCardsToken(game, CardColor.GREEN);
        Market market = game.getMarket();
        assertFalse(market.isDeckEmpty(1, CardColor.GREEN));
        token.reveal();
        assertFalse(market.isDeckEmpty(1, CardColor.GREEN));
        token.reveal();
        assertTrue(market.isDeckEmpty(1, CardColor.GREEN));
        assertFalse(market.isDeckEmpty(2, CardColor.GREEN));
        token.reveal();
        assertFalse(market.isDeckEmpty(2, CardColor.GREEN));
        token.reveal();
        assertTrue(market.isDeckEmpty(2, CardColor.GREEN));
        assertFalse(market.isDeckEmpty(3, CardColor.GREEN));
    }

    @Test
    public void countTest() throws FullCardDeckException, NegativeResAmountException {
        SoloGame game = new SoloGame(new Player("a"));
        SoloActionToken token = new DiscardDevCardsToken(game, CardColor.BLUE);
        Market market = game.getMarket();
        assertEquals(12, market.getAvailableCards().size());
        token.reveal();
        assertEquals(12, market.getAvailableCards().size());
        token.reveal();
        assertTrue(market.isDeckEmpty(1, CardColor.BLUE));
    }

    @Test
    public void sideEffectsTest() throws FullCardDeckException, NegativeResAmountException {
        SoloGame game = new SoloGame(new Player("a"));
        SoloActionToken token = new DiscardDevCardsToken(game, CardColor.YELLOW);
        Market market = game.getMarket();
        token.reveal();
        token.reveal();
        assertTrue(market.isDeckEmpty(1, CardColor.YELLOW));
        assertFalse(market.isDeckEmpty(1, CardColor.BLUE));
        assertFalse(market.isDeckEmpty(1, CardColor.GREEN));
        assertFalse(market.isDeckEmpty(1, CardColor.PURPLE));
    }

    @Test
    public void discardButLoseTest() throws FullCardDeckException, NegativeResAmountException {
        SoloGame game = new SoloGame(new Player("a"));
        SoloActionToken token1 = new DiscardDevCardsToken(game, CardColor.PURPLE);
        SoloActionToken token2 = new DiscardDevCardsToken(game, CardColor.PURPLE);

        //Discard level 1 cards
        token1.reveal();
        token2.reveal();
        //Discard level 2 cards
        token2.reveal();
        token1.reveal();
        //Discard two level 3 cards
        token1.reveal();
        //Discard last two level3, game is over
        token2.reveal();
    }
}