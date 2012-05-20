package rails.game.state;

public abstract class OwnableItem<T extends OwnableItem<T>> extends AbstractItem implements Ownable<T> {
    
    private Portfolio<T> portfolio;
    
    public Portfolio<T> getPortfolio() {
        return portfolio;
    }
    
    public Owner getOwner() {
        return portfolio.getOwner();
    }
    
    public void setPortfolio(Portfolio<T> p) {
        portfolio = p;
    }
    
}
