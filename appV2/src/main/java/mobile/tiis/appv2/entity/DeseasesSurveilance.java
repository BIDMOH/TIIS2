package mobile.tiis.appv2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

import mobile.tiis.appv2.database.GIISContract;
import mobile.tiis.appv2.database.SQLHandler;

/**
 * Created by issy on 10/24/16.
 */

public class DeseasesSurveilance implements Serializable{

    public DeseasesSurveilance(){

    }


    private int FeverMonthlyDeaths;
    private int FeverMonthlyCases;
    private int AFPMonthlyCases;
    private int AFPDeaths;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int NeonatalTTCases;
    private int NeonatalTTDeaths;
    private int ReportedMonth;
    private int ReportedYear;

    @JsonProperty("FeverMonthlyDeaths")
    public int getFeverMonthlyDeaths() {
        return FeverMonthlyDeaths;
    }

    public void setFeverMonthlyDeaths(int feverMonthlyDeaths) {
        FeverMonthlyDeaths = feverMonthlyDeaths;
    }

    public void setFeverMonthlyCases(int feverMonthlyCases) {
        FeverMonthlyCases = feverMonthlyCases;
    }

    @JsonProperty("AFPMonthlyCases")
    public int getAFPMonthlyCases() {
        return AFPMonthlyCases;
    }

    public void setAFPMonthlyCases(int AFPMonthlyCases) {
        this.AFPMonthlyCases = AFPMonthlyCases;
    }

    @JsonProperty("AFPDeaths")
    public int getAFPDeaths() {
        return AFPDeaths;
    }

    public void setAFPDeaths(int AFPDeaths) {
        this.AFPDeaths = AFPDeaths;
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

    public void setNeonatalTTCases(int neonatalTTCases) {
        NeonatalTTCases = neonatalTTCases;
    }

    public void setNeonatalTTDeaths(int neonatalTTDeaths) {
        NeonatalTTDeaths = neonatalTTDeaths;
    }

    @JsonProperty("FeverMonthlyCases")
    public int getFeverMonthlyCases() {
        return FeverMonthlyCases;
    }

    @JsonProperty("NeonatalTTCases")
    public int getNeonatalTTCases() {
        return NeonatalTTCases;
    }

    @JsonProperty("NeonatalTTDeaths")
    public int getNeonatalTTDeaths() {
        return NeonatalTTDeaths;
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
