package rails.game.round;

import rails.game.RailsAbstractItem;
import rails.game.RailsItem;
import rails.game.action.PossibleAction;

public abstract class Activity extends RailsAbstractItem {
   
    protected Activity(RailsItem parent, String id) {
        super(parent, id);
    }

    /**
     * @return true if activity is active
     */
    public abstract boolean isActive();

    /**
     * @return available actions thus checks the preconditions and creates the allowed actions 
     */
    public abstract Iterable<PossibleAction> getActions();
    
    /**
     * @return checks if the conditions of the actions are fullfilled
     */
    public abstract boolean isActionAllowed(PossibleAction action);
    
    /**
     * executes the action
     */
    public abstract void executeAction(PossibleAction action);
    
}
    


