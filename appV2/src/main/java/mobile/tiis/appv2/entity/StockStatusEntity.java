package mobile.tiis.appv2.entity;

/**
 * Created by issy on 10/18/16.
 */

public class StockStatusEntity {

    String itemName;
    String openningBalance;
    String dosesReceived;
    String closingBalance;
    String discardedUnopened;
    String immunizedChildren;
    String usageRate;
    String wastageRate;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemname) {
        this.itemName = itemname;
    }

    public String getOpenningBalance() {
        return openningBalance;
    }

    public void setOpenningBalance(String openningBalance) {
        this.openningBalance = openningBalance;
    }

    public String getDosesReceived() {
        return dosesReceived;
    }

    public void setDosesReceived(String dosesReceived) {
        this.dosesReceived = dosesReceived;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getDiscardedUnopened() {
        return discardedUnopened;
    }

    public void setDiscardedUnopened(String discardedUnopened) {
        this.discardedUnopened = discardedUnopened;
    }

    public String getImmunizedChildren() {
        return immunizedChildren;
    }

    public void setImmunizedChildren(String immunizedChildren) {
        this.immunizedChildren = immunizedChildren;
    }

    public String getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(String usageRate) {
        this.usageRate = usageRate;
    }

    public String getWastageRate() {
        return wastageRate;
    }

    public void setWastageRate(String wastageRate) {
        this.wastageRate = wastageRate;
    }
}
