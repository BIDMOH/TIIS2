package mobile.tiis.app.entity;

/**
 * Created by issy on 10/18/16.
 */

public class StockStatusEntity {

    String antigen;
    String openingBalance;
    String dosesReceived;
    String stockOnHand;
    String dosesDiscardedUnopened;
    String childrenImmunized;
    String dosesDiscardedOpened;
    String usageRate;
    String wastageRate;
    String reportingMonth;

    /*
    "antigen": "OPV",
    "childrenImmunized": 227,
    "dosesDiscardedOpened": 62,
    "dosesDiscardedUnopened": 0,
    "dosesReceived": 0,
    "openingBalance": 133,
    "stockOnHand": 40
     */

    public String getDosesReceived() {
        return dosesReceived;
    }

    public void setDosesReceived(String dosesReceived) {
        this.dosesReceived = dosesReceived;
    }

    public String getAntigen() {
        return antigen;
    }

    public void setAntigen(String antigen) {
        this.antigen = antigen;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(String stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public String getDosesDiscardedUnopened() {
        return dosesDiscardedUnopened;
    }

    public void setDosesDiscardedUnopened(String dosesDiscardedUnopened) {
        this.dosesDiscardedUnopened = dosesDiscardedUnopened;
    }

    public String getChildrenImmunized() {
        return childrenImmunized;
    }

    public void setChildrenImmunized(String childrenImmunized) {
        this.childrenImmunized = childrenImmunized;
    }

    public String getDosesDiscardedOpened() {
        return dosesDiscardedOpened;
    }

    public void setDosesDiscardedOpened(String dosesDiscardedOpened) {
        this.dosesDiscardedOpened = dosesDiscardedOpened;
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

    public String getReportingMonth() {
        return reportingMonth;
    }

    public void setReportingMonth(String reportingMonth) {
        this.reportingMonth = reportingMonth;
    }

}
