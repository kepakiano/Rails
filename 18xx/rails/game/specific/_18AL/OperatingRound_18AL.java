package rails.game.specific._18AL;

import java.util.ArrayList;
import java.util.List;

import rails.game.OperatingRound;
import rails.game.ReportBuffer;
import rails.game.TrainI;
import rails.game.action.PossibleAction;
import rails.game.move.MoveSet;
import rails.game.specific._18AL.AssignNamedTrains;
import rails.util.LocalText;

public class OperatingRound_18AL extends OperatingRound {

	@Override	
    protected void setGameSpecificPossibleActions () {
        
        for (NameTrains stl : getSpecialProperties (NameTrains.class))
        {
            List<TrainI> trains = operatingCompany.getPortfolio().getTrainList();
            if (trains != null && !trains.isEmpty()) {
                possibleActions.add(new AssignNamedTrains(stl, trains));
            }
            
        }
    }
    
    @Override 
    public boolean processGameSpecificAction (PossibleAction action) {

        if (action instanceof AssignNamedTrains) {
            
            AssignNamedTrains namingAction = (AssignNamedTrains) action;
            List<NameableTrain> trains = namingAction.getNameableTrains();
            List<NameableTrain> newTrainsPerToken = namingAction.getPostTrainPerToken();
            List<NamedTrainToken> tokens = namingAction.getTokens();
            
            List<NamedTrainToken> newTokenPerTrain = new ArrayList<NamedTrainToken>(trains.size());
            
            NameableTrain newTrain;
            NamedTrainToken oldToken, newToken;
            
            for (int i=0; i<trains.size(); i++) {
                newTokenPerTrain.add(null);
            }
            for (int i=0; i<tokens.size(); i++) {
                newTrain = newTrainsPerToken.get(i);
                if (newTrain != null) newTokenPerTrain.set(trains.indexOf(newTrain), tokens.get(i));
            }
            
            MoveSet.start(true);
            
            for (int i=0; i<trains.size(); i++) {
                oldToken = trains.get(i).getNameToken();
                newToken = newTokenPerTrain.get(i);
                if (oldToken != newToken) {
                    trains.get(i).setNameToken(newToken);
                    if (newToken != null) {
                        ReportBuffer.add(LocalText.getText("NamesTrain", new String[] {
                                operatingCompany.getName(),
                                trains.get(i).getName(),
                                newToken.getLongName()
                        }));
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    
    }
    
   

}
