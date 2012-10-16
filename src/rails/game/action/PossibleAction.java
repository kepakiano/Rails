/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/game/action/PossibleAction.java,v 1.16 2010/01/31 22:22:29 macfreek Exp $
 *
 * Created on 14-Sep-2006
 * Change Log:
 */
package rails.game.action;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rails.game.*;
import rails.game.state.ChangeAction;

/**
 * PossibleAction is the superclass of all classes that describe an allowed user
 * action (such as laying a tile or dropping a token on a specific hex, buying a
 * train etc.).
 *
 * @author Erik Vos
 */
// TODO (Rails2.0): Replace this with a new XML version
// Remove the link getInstance variables and methods

/* Or should this be an interface? We will see. */
public abstract class PossibleAction implements ChangeAction, Serializable {

    protected String playerName;
    protected int playerIndex;

    protected boolean acted = false;

    public static final long serialVersionUID = 3L;

    protected static Logger log =
            LoggerFactory.getLogger(PossibleAction.class);

    /**
     *
     */
    public PossibleAction() {
        Player player = getRoot().getPlayerManager().getCurrentPlayer();
        if (player != null) {
            playerName = player.getId();
            playerIndex = player.getIndex();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Set the name of the player who <b>executed</b> the action (as opposed to
     * the player who was <b>allowed</b> to do the action, which is the one set
     * in the constructor).
     *
     * @param playerName
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean hasActed() {
        return acted;
    }

    public void setActed() {
        this.acted = true;
    }

    /** 
     * Compare the choice options of two action objects, without regard to whatever choice has been made, if any.
     * In other words: only the server-set (prior) attributes must be compared.
     * <p>This method is used by the server (engine) to validate 
     * the incoming action that has actually been chosen in the client (GUI),
     * but only for the purpose to check if the chosen option was really on offer,
     * not to check if the chosen action is actually valid. 
     * These perspectives could give different results in cases where 
     * the PossibleAction does not fully restrict choices to valid values only
     * (such as the blanket LayTile that does no restrict the hex to lay a tile on,
     * or the SetDividend that will accept any revenue value).
     * @param pa Another PossibleAction to compare with.
     * @return True if the compared PossibleAction object has equal choice options.
     */
    public abstract boolean equalsAsOption (PossibleAction pa);

    /** 
     * Compare the chosen actions of two action objects.
     * In other words: the client-set (posterior) attributes must be compared,
     * in addition to those server-set (prior) attributes that sufficiently identify the action.
     * <p>This method is used by the server (engine) to check if two action 
     * objects represent the same actual action, as is done when reloading a saved file
     * (i.e. loading a later stage of the same game).
     * @param pa Another PossibleAction to compare with.
     * @return True if the compared PossibleAction object has equal selected action values.
     */
    public abstract boolean equalsAsAction (PossibleAction pa);

    protected RailsRoot getRoot() {
        return RailsRoot.getInstance();
    }
    
    protected GameManager getGameManager() {
        return GameManager.getInstance();
    }

    protected CompanyManager getCompanyManager () {
        return RailsRoot.getInstance().getCompanyManager();
    }

    /** Default version of an Menu item text. To be overridden where useful. */
    public String toMenu() {
        return toString();
    }

}