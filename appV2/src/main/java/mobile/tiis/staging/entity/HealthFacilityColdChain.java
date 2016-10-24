package mobile.tiis.staging.entity;

import java.util.Date;

/**
 * Created by issy on 10/24/16.
 */

public class HealthFacilityColdChain {

    public HealthFacilityColdChain(){

    }

    private float alarmHighTemp;
    private float alarmLowTemp;
    private float TempMax;
    private float TempMin;
    private String HealthFacilityId;
    private String modifiedBy;
    private Date ModifiedOn;
    private int ReportedMonth;
    private int ReportedYear;

    public float getAlarmHighTemp() {
        return alarmHighTemp;
    }

    public void setAlarmHighTemp(float alarmHighTemp) {
        this.alarmHighTemp = alarmHighTemp;
    }

    public float getAlarmLowTemp() {
        return alarmLowTemp;
    }

    public void setAlarmLowTemp(float alarmLowTemp) {
        this.alarmLowTemp = alarmLowTemp;
    }

    public float getTempMax() {
        return TempMax;
    }

    public void setTempMax(float tempMax) {
        TempMax = tempMax;
    }

    public float getTempMin() {
        return TempMin;
    }

    public void setTempMin(float tempMin) {
        TempMin = tempMin;
    }

    public String getHealthFacilityId() {
        return HealthFacilityId;
    }

    public void setHealthFacilityId(String healthFacilityId) {
        HealthFacilityId = healthFacilityId;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return ModifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        ModifiedOn = modifiedOn;
    }

    public int getReportedMonth() {
        return ReportedMonth;
    }

    public void setReportedMonth(int reportedMonth) {
        ReportedMonth = reportedMonth;
    }

    public int getReportedYear() {
        return ReportedYear;
    }

    public void setReportedYear(int reportedYear) {
        ReportedYear = reportedYear;
    }
}
