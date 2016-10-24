package mobile.tiis.staging.entity;

import java.util.Date;

import mobile.tiis.staging.database.GIISContract;
import mobile.tiis.staging.database.SQLHandler;

/**
 * Created by issy on 10/24/16.
 */

public class DeseasesSurveilance {

    public DeseasesSurveilance(){

    }

    private Date modifiedAt;
    private String feverMonthlyCases;
    private String feverDeaths;
    private String apfMonthlyCases;
    private String apfDeaths;
    private String neonatalTTCases;
    private String neonatalTTDeaths;
    private String reportedMonth;

    public String getReportedMonth() {
        return reportedMonth;
    }

    public void setReportedMonth(String reportedMonth) {
        this.reportedMonth = reportedMonth;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getFeverMonthlyCases() {
        return feverMonthlyCases;
    }

    public void setFeverMonthlyCases(String feverMonthlyCases) {
        this.feverMonthlyCases = feverMonthlyCases;
    }

    public String getFeverDeaths() {
        return feverDeaths;
    }

    public void setFeverDeaths(String feverDeaths) {
        this.feverDeaths = feverDeaths;
    }

    public String getApfMonthlyCases() {
        return apfMonthlyCases;
    }

    public void setApfMonthlyCases(String apfMonthlyCases) {
        this.apfMonthlyCases = apfMonthlyCases;
    }

    public String getApfDeaths() {
        return apfDeaths;
    }

    public void setApfDeaths(String apfDeaths) {
        this.apfDeaths = apfDeaths;
    }

    public String getNeonatalTTCases() {
        return neonatalTTCases;
    }

    public void setNeonatalTTCases(String neonatalTTCases) {
        this.neonatalTTCases = neonatalTTCases;
    }

    public String getNeonatalTTDeaths() {
        return neonatalTTDeaths;
    }

    public void setNeonatalTTDeaths(String neonatalTTDeaths) {
        this.neonatalTTDeaths = neonatalTTDeaths;
    }
}
