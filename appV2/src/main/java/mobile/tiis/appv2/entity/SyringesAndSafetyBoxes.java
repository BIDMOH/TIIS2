package mobile.tiis.appv2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by issy on 10/25/16.
 */

public class SyringesAndSafetyBoxes implements Serializable {

    public SyringesAndSafetyBoxes(){}

    private String ItemName;
    private int OpeningBalance;
    private int Received;
    private int Used;
    private int Wastage;
    private int StockInHand;
    private int StockedOutDays;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;


    @JsonProperty("ItemName")
    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        this.ItemName = itemName;
    }

    @JsonProperty("OpeningBalance")
    public int getOpeningBalance() {
        return OpeningBalance;
    }

    public void setOpeningBalance(int openingBalance) {
        this.OpeningBalance = openingBalance;
    }

    @JsonProperty("Received")
    public int getReceived() {
        return Received;
    }


    public void setReceived(int received) {
        this.Received = received;
    }

    @JsonProperty("Used")
    public int getUsed() {
        return Used;
    }

    public void setUsed(int used) {
        this.Used = used;
    }

    @JsonProperty("Wastage")
    public int getWastage() {
        return Wastage;
    }

    public void setWastage(int wastage) {
        this.Wastage = wastage;
    }

    @JsonProperty("StockInHand")
    public int getStockInHand() {
        return StockInHand;
    }

    public void setStockInHand(int stockInHand) {
        this.StockInHand = stockInHand;
    }

    @JsonProperty("StockedOutDays")
    public int getStockedOutDays() {
        return StockedOutDays;
    }

    public void setStockedOutDays(int stockedOutDays) {
        this.StockedOutDays = stockedOutDays;
    }

    @JsonProperty("HealthFacilityId")
    public String getHealthFacilityId() {
        return HealthFacilityId;
    }

    public void setHealthFacilityId(String healthFacilityId) {
        HealthFacilityId = healthFacilityId;
    }

    @JsonProperty("ModifiedBy")
    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        ModifiedBy = modifiedBy;
    }

    @JsonProperty("ModifiedOn")
    public String getModifiedOn() {
        return ModifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        ModifiedOn = modifiedOn;
    }

    @JsonProperty("ReportedMonth")
    public int getReportedMonth() {
        return ReportedMonth;
    }

    public void setReportedMonth(int reportedMonth) {
        ReportedMonth = reportedMonth;
    }

    @JsonProperty("ReportedYear")
    public int getReportedYear() {
        return ReportedYear;
    }

    public void setReportedYear(int reportedYear) {
        ReportedYear = reportedYear;
    }
}
