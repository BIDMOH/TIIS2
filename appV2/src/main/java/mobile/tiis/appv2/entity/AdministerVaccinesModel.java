/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.tiis.appv2.entity;

import android.util.Log;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by utente1 on 5/13/2015.
 */
public class AdministerVaccinesModel {

    //id of the dose name
    private String doseName;
    private String dose_id;
    //status of vaccination done
    private String status = "true";
    //VaccinationEvent id for the row
    private String vacc_ev_id = "";
    //Vaccination Date
    private String time;
    private Date time2;
    //Non vaccination reason
    private String non_vac_reason = "0";
    //Vaccine lot picked
    private String vaccination_lot_id = "-2";
    private String updateURL = "";
    private String updateURLAppointment = "";
    private int vaccination_lot_pos;
    private int non_vac_reason_pos = 0;
    private boolean keep_child_due;
    private Map vaccine_lot_map;
    private List<String> vaccine_lot_list;

    private String Scheduled_Date_field = "";
    private String Dose_Number_field = "";
    private int Dose_Number_Parsed = 0;
    private String Scheduled_Vaccination_Id = "";

    private TextView automation_date;
    private String automation_date_string;

    private String new_date_difference;

    private Boolean starter_row = false;

    //lot balance in health facility balance
    private List<String> balance;


    public AdministerVaccinesModel() {

    }

    public TextView getAutomation_date() {
        return automation_date;
    }

    public void setAutomation_date(TextView automation_date) {
        this.automation_date = automation_date;
    }

    public String getAutomation_date_string() {
        return automation_date_string;
    }

    public void setAutomation_date_string(String automation_date_string) {
        this.automation_date_string = automation_date_string;
    }

    public String getNew_date_difference() {
        return new_date_difference;
    }
    public String getDoseName() {
        return doseName;
    }
    public void setDoseName(String doseName) {
        this.doseName = doseName;
    }


    public void setNew_date_difference(String new_date_difference) {
        this.new_date_difference = new_date_difference;
    }

    public Boolean getStarter_row() {
        return starter_row;
    }

    public void setStarter_row(Boolean starter_row) {
        this.starter_row = starter_row;
    }

    public String getDose_Number_field() {
        return Dose_Number_field;
    }

    public void setDose_Number_field(String dose_Number_field) {
        Dose_Number_field = dose_Number_field;
    }

    public int getDose_Number_Parsed() {
        return Dose_Number_Parsed;
    }

    public void setDose_Number_Parsed(int dose_Number_Parsed) {
        Dose_Number_Parsed = dose_Number_Parsed;
    }

    public String getScheduled_Date_field() {
        return Scheduled_Date_field;
    }

    public void setScheduled_Date_field(String scheduled_Date_field) {
        Scheduled_Date_field = scheduled_Date_field;
    }

    public String getScheduled_Vaccination_Id() {
        return Scheduled_Vaccination_Id;
    }

    public void setScheduled_Vaccination_Id(String scheduled_Vaccination_Id) {
        Scheduled_Vaccination_Id = scheduled_Vaccination_Id;
    }

    public Date getTime2() {
        return time2;
    }

    public void setTime2(Date time2) {
        this.time2 = time2;
    }

    public List<String> getVaccine_lot_list() {
        return vaccine_lot_list;
    }

    public void setVaccine_lot_list(List<String> vaccine_lot_list) {
        this.vaccine_lot_list = vaccine_lot_list;
    }

    public Map getVaccine_lot_map() {
        return vaccine_lot_map;
    }

    public void setVaccine_lot_map(Map vaccine_lot_map) {
        this.vaccine_lot_map = vaccine_lot_map;
    }

    public String getDose_id() {
        return dose_id;
    }

    public void setDose_id(String dose_id) {
        this.dose_id = dose_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        Log.d("Setting status", status);
        this.status = status;
    }

    public int getNon_vac_reason_pos() {
        return non_vac_reason_pos;
    }

    public void setNon_vac_reason_pos(int non_vac_reason_pos) {
        this.non_vac_reason_pos = non_vac_reason_pos;
    }

    public int getVaccination_lot_pos() {
        return vaccination_lot_pos;
    }

    public void setVaccination_lot_pos(int vaccination_lot_pos) {
        this.vaccination_lot_pos = vaccination_lot_pos;
    }

    public String getVacc_ev_id() {
        return vacc_ev_id;
    }

    public void setVacc_ev_id(String vacc_ev_id) {
        this.vacc_ev_id = vacc_ev_id;
    }

    public String getVaccination_lot() {
        return vaccination_lot_id;
    }

    public void setVaccination_lot(String vaccination_lot) {
        this.vaccination_lot_id = vaccination_lot;
    }

    public String getNon_vac_reason() {
        return non_vac_reason;
    }

    public void setNon_vac_reason(String non_vac_reason) {
        this.non_vac_reason = non_vac_reason;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateURL() {
        return updateURL;
    }
    public void setUpdateURL(String updateURL){

        this.updateURL =updateURL;
    }
    public String getAppointmentUpdateURL() {
        return updateURLAppointment;
    }

    public void setUpdateURLAppointment(String updateURLAppointment){
        this.updateURLAppointment = updateURLAppointment;

    }

    public boolean isKeep_child_due() {
        return keep_child_due;
    }

    public void setKeep_child_due(boolean keep_child_due) {
        this.keep_child_due = keep_child_due;
    }

    public List<String> getBalance() {
        return balance;
    }

    public void setBalance(List<String> balance) {
        this.balance = balance;
    }
}
