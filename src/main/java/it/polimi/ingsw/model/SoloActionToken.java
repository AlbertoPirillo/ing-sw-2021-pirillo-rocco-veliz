package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.NegativeResAmountException;

import java.io.Serializable;

/**
 * Generic implementation of an action token, used in single player only
 */
public abstract class SoloActionToken implements Serializable, Cloneable {

    private transient final SoloGame game;

    /**
     * Constructs a new ActionToken, linking it to the current game
     * @param game the corresponding the game
     */
    public SoloActionToken(SoloGame game) {
        this.game = game;
    }

    /**
     * Gets the game the action token is associated with
     * @return the game
     */
    public SoloGame getGame() {
        return game;
    }
    /**
     * Activates the effects of this action token
     */
    public abstract void reveal() throws NegativeResAmountException;
    /**
     * Gets the ID of the token, used to be displayed in the GUI
     * @return a String representing the ID of the token
     */
    public abstract String getID();

    /**
     * Clones the current object
     * @return a new Object with the same data
     */
    @Override
    public SoloActionToken clone() {
        SoloActionToken clone = null;
        try {
            clone = (SoloActionToken) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}