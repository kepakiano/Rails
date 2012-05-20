/* $Header: /Users/blentz/rails_rcs/cvs/18xx/rails/game/special/ExchangeForShare.java,v 1.19 2010/05/05 21:37:18 evos Exp $ */
package rails.game.special;

import rails.common.LocalText;
import rails.common.parser.ConfigurationException;
import rails.common.parser.Tag;
import rails.game.*;
import rails.util.*;

public class ExchangeForShare extends SpecialProperty {

    /** The public company of which a share can be obtained. */
    String publicCompanyName;

    /** The share size */
    int share;

    @Override
    public void configureFromXML(Tag tag) throws ConfigurationException {

        super.configureFromXML(tag);

        Tag swapTag = tag.getChild("ExchangeForShare");
        if (swapTag == null) {
            throw new ConfigurationException("<ExchangeForShare> tag missing");
        }

        publicCompanyName = swapTag.getAttributeAsString("company");
        if (!Util.hasValue(publicCompanyName))
            throw new ConfigurationException(
                    "ExchangeForShare: company name missing");
        share = swapTag.getAttributeAsInteger("share", 10);
    }

    public boolean isExecutionable() {
        // FIXME: Check if this works correctly
        // IT is better to rewrite this check
        return ((PrivateCompany)originalCompany).getPortfolio().getOwner() instanceof Player;
    }

    /**
     * @return Returns the publicCompanyName.
     */
    public String getPublicCompanyName() {
        return publicCompanyName;
    }

    /**
     * @return Returns the share.
     */
    public int getShare() {
        return share;
    }

    public String getId() {
        return toString();
    }

    @Override
    public String toString() {
        return "Swap " + originalCompany.getId() + " for " + share
               + "% share of " + publicCompanyName;
    }

    @Override
    public String toMenu() {
        return LocalText.getText("SwapPrivateForCertificate",
                originalCompany.getId(),
                share,
                publicCompanyName );
    }
    
    public String getInfo() {
        return toMenu();
    }
}
