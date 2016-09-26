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

package mobile.tiis.appV2.util;

import mobile.tiis.appV2.base.BackboneApplication;

import java.util.ArrayList;

/**
 * Created by Teodor on 3/8/2015.
 */
public class ViewAppointmentRow extends BackboneApplication{

    private String vaccine_dose = "";
    private String schedule;
    private String scheduled_date;
    private String appointment_id;
    private String age_definition_id;
    private ArrayList<String> dose_id = new ArrayList<String>();
    private String child_id="";
    private String child_name="";

    public ViewAppointmentRow() {

    }

    public String getVaccine_dose() {
        return vaccine_dose;
    }

    public void setVaccine_dose(String vaccine_dose) {
        this.vaccine_dose = this.vaccine_dose + vaccine_dose;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getScheduled_date() {
        return scheduled_date;
    }

    public void setScheduled_date(String scheduled_date) {
        this.scheduled_date = scheduled_date;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public ArrayList<String> getDose_id() {
        return dose_id;
    }

    public void addDose_id(String id){
        this.dose_id.add(id);
    }

    public void concatDose_id(ArrayList<String> dose_id){
        this.dose_id.addAll(dose_id);
    }

    public void setDose_id(ArrayList<String> dose_id) {
        this.dose_id = dose_id;
    }

    public String getAge_definition_id() {
        return age_definition_id;
    }

    public void setAge_definition_id(String age_definition_id) {
        this.age_definition_id = age_definition_id;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        if(child_id!=null){
            this.child_id = child_id;
        }

    }

}
