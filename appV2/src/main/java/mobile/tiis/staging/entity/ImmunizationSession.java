package mobile.tiis.staging.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by issy on 10/25/16.
 */

public class ImmunizationSession implements Serializable{

    public ImmunizationSession(){}

    private String OtherMajorImmunizationActivities;
    private int OutreachPlanned;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;

    @JsonProperty("OtherMajorImmunizationActivities")
    public String getOtherMajorImmunizationActivities() {
        return OtherMajorImmunizationActivities;
    }

    public void setOtherMajorImmunizationActivities(String otherMajorImmunizationActivities) {
        OtherMajorImmunizationActivities = otherMajorImmunizationActivities;
    }

    @JsonProperty("OutreachPlanned")
    public int getOutreachPlanned() {
        return OutreachPlanned;
    }

    public void setOutreachPlanned(int outreachPlanned) {
        OutreachPlanned = outreachPlanned;
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
