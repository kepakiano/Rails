package rails.game;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rails.algorithms.RevenueManager;
import rails.common.Config;
import rails.common.DisplayBuffer;
import rails.common.GameData;
import rails.common.GameOption;
import rails.common.LocalText;
import rails.common.ReportBuffer;
import rails.common.ReportManager;
import rails.common.parser.ComponentManager;
import rails.common.parser.Configurable;
import rails.common.parser.ConfigurationException;
import rails.common.parser.Tag;
import rails.common.parser.XMLTags;
import rails.game.state.Root;

import com.google.common.base.Preconditions;

public class RailsRoot extends Root implements RailsItem {

    private static final Logger log =
            LoggerFactory.getLogger(RailsRoot.class);
    
    // currently we assume that there is only one instance running
    // thus it is possible to retrieve that version
    // TODO: Replace this by a full support of concurrent usage
    private static RailsRoot instance;
    
    public static RailsRoot getInstance() {
        return instance;
    }

    // Base XML file
    private static final String GAME_XML_FILE = "Game.xml";
    
    // Instance fields
    
    // Game data fields
    private final GameData gameData;

    // Component Managers
    private GameManager gameManager;
    private CompanyManager companyManager;
    private PlayerManager playerManager;
    private PhaseManager phaseManager;
    private TrainManager trainManager;
    private StockMarket stockMarket;
    private MapManager mapManager;
    private TileManager tileManager;
    private RevenueManager revenueManager;
    private Bank bank;
    
    // Other Managers
    private ReportManager reportManager;

    // TODO (Rails2.0): Move currency to Bank 
    private Currency currency;

    private RailsRoot(GameData gameData) {
        super();
        
        // TODO (Rails2.0): Is this the correct place?
        gameData.getGameOptions().put(GameOption.NUMBER_OF_PLAYERS,
                String.valueOf(gameData.getPlayers().size()));

        for (String playerName : gameData.getPlayers()) {
            log.debug("Player: " + playerName);
        }
        for (String optionName : gameData.getGameOptions().keySet()) {
            log.debug("Option: " + optionName + "="
                    + gameData.getGameOptions().get(optionName));
        }

        this.gameData = gameData;
    }
    
    public static RailsRoot create(GameData gameData) throws ConfigurationException {
        Preconditions.checkState(instance == null, 
                "Currently only a single instance of RailsRoot is allowed");
        instance = new RailsRoot(gameData);
        log.debug("RailsRoot: instance created");
        instance.init();
        log.debug("RailsRoot: instance initialized");
        instance.initGameFromXML();
        log.debug("RailsRoot: game configuration initialized");
        instance.finishConfiguration();
        log.debug("RailsRoot: game configuration finished");
        
        return instance;
    }

    
    // feedback from ComponentManager
    public void setComponent(Configurable component) {
        if (component instanceof PlayerManager) {
            playerManager = (PlayerManager) component;
        } else if (component instanceof Bank) {
            bank = (Bank) component;
        } else if (component instanceof CompanyManager) {
            companyManager = (CompanyManager) component;
        } else if (component instanceof StockMarket) {
            stockMarket = (StockMarket) component;
        } else if (component instanceof GameManager) {
            gameManager = (GameManager) component;
        } else if (component instanceof PhaseManager) {
            phaseManager = (PhaseManager) component;
        } else if (component instanceof TrainManager) {
            trainManager = (TrainManager) component;
        } else if (component instanceof MapManager) {
            mapManager = (MapManager) component;
        } else if (component instanceof TileManager) {
            tileManager = (TileManager) component;
        } else if (component instanceof RevenueManager) {
            revenueManager = (RevenueManager) component;
        }
    }
    
    public void initGameFromXML() throws ConfigurationException {
        String directory = "data/" + gameData.getGameName();
        
        Tag componentManagerTag = Tag.findTopTagInFile(
                GAME_XML_FILE, directory, XMLTags.COMPONENT_MANAGER_ELEMENT_ID, gameData.getGameOptions());
        
        ComponentManager componentManager = new ComponentManager();
        componentManager.start(this,  componentManagerTag);
        // The componentManager automatically returns results

        // creation of Report facilities
        reportManager = ReportManager.create(this, "reportManager");
    }
    
    public boolean finishConfiguration() {
        /*
         * Initializations that involve relations between components can
         * only be done after all XML has been processed.
         */
        log.info("========== Start of rails.game " + gameData.getGameName() + " ==========");
        log.info("Rails version "+ Config.getVersion());
        ReportBuffer.add(this,LocalText.getText("GameIs", gameData.getGameName()));

        playerManager.setPlayers(gameData.getPlayers(), bank);
        gameManager.init();
        // TODO: Can this be merged above?
        playerManager.init();

        try {
            playerManager.finishConfiguration(this);
            companyManager.finishConfiguration(this);
            trainManager.finishConfiguration(this);
            phaseManager.finishConfiguration(this);
            tileManager.finishConfiguration(this);
            mapManager.finishConfiguration(this);
            bank.finishConfiguration(this);
            stockMarket.finishConfiguration(this);

            if (revenueManager != null)
                revenueManager.finishConfiguration(this);
        } catch (ConfigurationException e) {
            log.error(e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
            DisplayBuffer.add(this, e.getMessage());
            return false;
        }
        return true;
    }
    
    public String start() {
        // FIXME (Rails2.0): Should this not be part of the initial Setup configuration?
        int nbPlayers = gameData.getPlayers().size();
        if (nbPlayers < playerManager.getMinPlayers()
                || nbPlayers > playerManager.getMaxPlayers()) {
            return gameData.getGameName() +" is not configured to be played with "+ nbPlayers +" players\n"
                    + "Please enter a valid number of players, or add a <Players> entry to data/"+ gameData.getGameName() +"/Game.xml";
        }
        
        gameManager.startGame();
        return null;
    }

    // Setters
    // TODO (Rails2.0): Replace Currency here, move to Bank
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }
    
    /*----- Getters -----*/

    public GameManager getGameManager() {
        return gameManager;
    }
    
    public CompanyManager getCompanyManager() {
        return companyManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PhaseManager getPhaseManager() {
        return phaseManager;
    }

    public TrainManager getTrainManager() {
        return trainManager;
    }

    public StockMarket getStockMarket() {
        return stockMarket;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public TileManager getTileManager() {
        return tileManager;
    }

    public RevenueManager getRevenueManager() {
        return revenueManager;
    }

    public Bank getBank() {
        return bank;
    }
    
    public ReportManager getReportManager() {
        return reportManager;
    }
    
    
    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameData.getGameName();
    }
    
    /**
     * @return the gameOptions
     */
    public Map<String, String> getGameOptions() {
        return gameData.getGameOptions();
    }
    
    /**
     * @return the gameData
     */
    public GameData getGameData() {
        return gameData;
    }
    
    @Override
    public RailsRoot getParent() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public RailsRoot getRoot() {
        return this;
    }
    
    public static void clearInstance() {
        instance = null;
    }
}