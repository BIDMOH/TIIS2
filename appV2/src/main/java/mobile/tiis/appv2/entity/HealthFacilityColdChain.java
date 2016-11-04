package mobile.tiis.appv2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by issy on 10/24/16.
 */

public class HealthFacilityColdChain implements Serializable {

    public HealthFacilityColdChain(){

    }

    private int AlarmHighTemp;
    private int AlarmLowTemp;
    private float TempMax;
    private float TempMin;
    private String HealthFacilityId;
    private String ModifiedBy;
    private String ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;

    @JsonProperty("AlarmHighTemp")
    public int getAlarmHighTemp() {
        return AlarmHighTemp;
    }

    public void setAlarmHighTemp(int alarmHighTemp) {
        this.AlarmHighTemp = alarmHighTemp;
    }

    @JsonProperty("AlarmLowTemp")
    public int getAlarmLowTemp() {
        return AlarmLowTemp;
    }

    public void setAlarmLowTemp(int alarmLowTemp) {
        this.AlarmLowTemp = alarmLowTemp;
    }

    @JsonProperty("TempMax")
    public float getTempMax() {
        return TempMax;
    }

    public void setTempMax(float tempMax) {
        TempMax = tempMax;
    }

    @JsonProperty("TempMin")
    public float getTempMin() {
        return TempMin;
    }

    public void setTempMin(float tempMin) {
        TempMin = tempMin;
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
        this.ModifiedBy = modifiedBy;
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
