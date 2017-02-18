package mobile.tiis.appv2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by issy on 10/25/16.
 */

public class BcgOpvTtSurveillance implements Serializable {

    public BcgOpvTtSurveillance(){}

    private int DoseId;
    private int MaleServiceArea;
    private int FemaleServiceArea;
    private int CoverageServiceArea;
    private int MaleCatchmentArea;
    private int FemaleCatchmentArea;
    private int CoverageCatchmentArea;
    private int CoverageCatchmentAndServiceArea;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;


    @JsonProperty("DoseId")
    public int getDoseId() {
        return DoseId;
    }

    public void setDoseId(int doseId) {
        this.DoseId = doseId;
    }

    @JsonProperty("MaleServiceArea")
    public int getMaleServiceArea() {
        return MaleServiceArea;
    }

    public void setMaleServiceArea(int maleServiceArea) {
        this.MaleServiceArea = maleServiceArea;
    }

    @JsonProperty("FemaleServiceArea")
    public int getFemaleServiceArea() {
        return FemaleServiceArea;
    }

    public void setFemaleServiceArea(int femaleServiceArea) {
        this.FemaleServiceArea = femaleServiceArea;
    }


    @JsonProperty("CoverageServiceArea")
    public int getCoverageServiceArea() {
        return CoverageServiceArea;
    }

    public void setCoverageServiceArea(int coverageServiceArea) {
        this.CoverageServiceArea = coverageServiceArea;
    }

    @JsonProperty("MaleCatchmentArea")
    public int getMaleCatchmentArea() {
        return MaleCatchmentArea;
    }

    public void setMaleCatchmentArea(int maleCatchmentArea) {
        this.MaleCatchmentArea = maleCatchmentArea;
    }

    @JsonProperty("FemaleCatchmentArea")
    public int getFemaleCatchmentArea() {
        return FemaleCatchmentArea;
    }

    public void setFemaleCatchmentArea(int femaleCatchmentArea) {
        this.FemaleCatchmentArea = femaleCatchmentArea;
    }

    @JsonProperty("CoverageCatchmentArea")
    public int getCoverageCatchmentArea() {
        return CoverageCatchmentArea;
    }

    public void setCoverageCatchmentArea(int coverageCatchmentArea) {
        this.CoverageCatchmentArea = coverageCatchmentArea;
    }

    @JsonProperty("CoverageCatchmentAndServiceArea")
    public int getCoverageCatchmentAndServiceArea() {
        return CoverageCatchmentAndServiceArea;
    }

    public void setCoverageCatchmentAndServiceArea(int coverageCatchmentAndServiceArea) {
        this.CoverageCatchmentAndServiceArea = coverageCatchmentAndServiceArea;
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
