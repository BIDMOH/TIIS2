package mobile.tiis.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by issy on 10/25/16.
 */

public class VitaminAStock implements Serializable {

    public VitaminAStock(){}

    private String VitaminName;
    private int OpeningBalance;
    private int Received;
    private int TotalAdministered;
    private int Wastage;
    private int StockInHand;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;

    @JsonProperty("VitaminName")
    public String getVitaminName() {
        return VitaminName;
    }

    public void setVitaminName(String vitaminName) {
        VitaminName = vitaminName;
    }

    @JsonProperty("OpeningBalance")
    public int getOpeningBalance() {
        return OpeningBalance;
    }

    public void setOpeningBalance(int openingBalance) {
        OpeningBalance = openingBalance;
    }

    @JsonProperty("Received")
    public int getReceived() {
        return Received;
    }

    public void setReceived(int received) {
        Received = received;
    }

    @JsonProperty("TotalAdministered")
    public int getTotalAdministered() {
        return TotalAdministered;
    }

    public void setTotalAdministered(int totalAdministered) {
        TotalAdministered = totalAdministered;
    }

    @JsonProperty("Wastage")
    public int getWastage() {
        return Wastage;
    }

    public void setWastage(int wastage) {
        Wastage = wastage;
    }

    @JsonProperty("StockInHand")
    public int getStockInHand() {
        return StockInHand;
    }

    public void setStockInHand(int stockInHand) {
        StockInHand = stockInHand;
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
