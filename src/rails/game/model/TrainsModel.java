package rails.game.model;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;

import rails.game.TrainCertificateType;
import rails.game.Train;
import rails.game.state.Model;
import rails.game.state.Owner;
import rails.game.state.Portfolio;
import rails.game.state.PortfolioSet;

public class TrainsModel extends Model {

    public static final String ID = "TrainsModel";
    
    private final PortfolioSet<Train> trains;
    
    private boolean abbrList = false;

    private TrainsModel(Owner parent, String id) {
        super(parent, id);
        trains = PortfolioSet.create(parent, "trains", Train.class);
    }
    
    /** 
     * @return fully initialized TrainsModel
     */
    public static TrainsModel create(Owner parent) {
        return new TrainsModel(parent, ID);
    }
    
    @Override
    public Owner getParent() {
        return (Owner)super.getParent();
    }
    
    public Portfolio<Train> getPortfolio() {
        return trains;
    }
    
    public void setAbbrList(boolean abbrList) {
        this.abbrList = abbrList;
    }
    
    public ImmutableSet<Train> getTrains() {
        return trains.items();
    }
    
    public Train getTrainOfType(TrainCertificateType type) {
        for (Train train:trains) {
            if (train.getCertType() == type) return train;
        }
        return null;
    }
    
    /**
     * Make a full list of trains, like "2 2 3 3", to show in any field
     * describing train possessions, except the IPO.
     */
    private String makeListOfTrains() {
        if (trains.isEmpty()) return "";

        StringBuilder b = new StringBuilder();
        for (Train train:trains) {
            if (b.length() > 0) b.append(" ");
            if (train.isObsolete()) b.append("[");
            b.append(train.toText());
            if (train.isObsolete()) b.append("]");
        }

        return b.toString();
    }

    /**
     * Make an abbreviated list of trains, like "2(6) 3(5)" etc, to show in the
     * IPO.
     */
    public String makeListOfTrainCertificates() {
        if (trains.isEmpty()) return "";

        // create a bag with train types
        Multiset<TrainCertificateType> trainCertTypes = HashMultiset.create();
        for (Train train:trains) {
            trainCertTypes.add(train.getCertType()); 
        }
        
        StringBuilder b = new StringBuilder();
        
        for (TrainCertificateType certType:ImmutableSortedSet.copyOf(trainCertTypes.elementSet())) {
            if (b.length() > 0) b.append(" ");
            b.append(certType.toText()).append("(");
            if (certType.hasInfiniteQuantity()) {
                b.append("+");
            } else {
                b.append(trainCertTypes.count(certType));
            }
            b.append(")");
        }

        return b.toString();
    }
    
    @Override
    public String toText() {
        if (!abbrList) {
            return makeListOfTrains();
        } else {
            return makeListOfTrainCertificates();
        }
    }
}