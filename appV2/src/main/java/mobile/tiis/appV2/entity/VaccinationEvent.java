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

import android.provider.BaseColumns;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


public class VaccinationEvent {

    public static final String TAG_VACCINATION_EVENT = "VaccinationEvent";
    public static final String TAG_ID = "ID";
    public static final String TAG_CHILD_ID = "CHILD_ID";
    public static final String TAG_APPOINTMENT_ID = "APPOINTMENT_ID";
    public static final String TAG_DOSE_ID = "DOSE_ID";
    public static final String TAG_VACCINE_LOT_ID = "VACCINE_LOT_ID";
    public static final String TAG_HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    public static final String TAG_SCHEDULED_DATE = "SCHEDULED_DATE";
    public static final String TAG_VACCINATION_DATE = "VACCINATION_DATE";
    public static final String TAG_VACCINATION_STATUS = "VACCINATION_STATUS";
    public static final String TAG_NONVACCINATION_REASON_ID = "NONVACCINATION_REASON_ID";
    public static final String TAG_IS_ACTIVE = "IS_ACTIVE";
    public static final String TAG_MODIFIED_ON = "MODIFIED_ON";
    public static final String TAG_MODIFIED_BY = "MODIFIED_BY";
    public static final String TAG_NOTES = "NOTES";
    private String appointment;
    private String appointmentId;
    private String child;
    private String childId;
    private String doseId;
    private String healthFacility;
    private String healthFacilityId;
    private String id;
    private String isActive;
    private String modifiedOn;
    private String modifiedBy;
    private String nonvaccinationReasonId;
    private String notes;
    private String reason;
    private String scheduledDate;
    private String vaccinationDate;
    private String vaccinationStatus;
    private String vaccineDose;
    private String vaccineDoseList;
    private String vaccineLotId;
    private String vaccineLotText;
    private String localDB_vaccinationEventID;

    public VaccinationEvent() {
    }

    @JsonProperty("Appointment")
    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    @JsonProperty("AppointmentId")
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    @JsonProperty("Child")
    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    @JsonProperty("ChildId")
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @JsonProperty("DoseId")
    public String getDoseId() {
        return doseId;
    }

    public void setDoseId(String doseId) {
        this.doseId = doseId;
    }

    @JsonProperty("HealthFacility")
    public String getHealthFacility() {
        return healthFacility;
    }

    public void setHealthFacility(String healthFacility) {
        this.healthFacility = healthFacility;
    }

    @JsonProperty("HealthFacilityId")
    public String getHealthFacilityId() {
        return healthFacilityId;
    }

    public void setHealthFacilityId(String healthFacilityId) {
        this.healthFacilityId = healthFacilityId;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("IsActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("ModifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("ModifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("NonvaccinationReasonId")
    public String getNonvaccinationReasonId() {
        return nonvaccinationReasonId;
    }

    public void setNonvaccinationReasonId(String nonvaccinationReasonId) {
        this.nonvaccinationReasonId = nonvaccinationReasonId;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("ScheduledDate")
    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    @JsonProperty("VaccinationDate")
    public String getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(String vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    @JsonProperty("VaccinationStatus")
    public String getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(String vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    @JsonProperty("VaccineDose")
    public String getVaccineDose() {
        return vaccineDose;
    }

    public void setVaccineDose(String vaccineDose) {
        this.vaccineDose = vaccineDose;
    }

    @JsonProperty("VaccineDoseList")
    public String getVaccineDoseList() {
        return vaccineDoseList;
    }

    public void setVaccineDoseList(String vaccineDoseList) {
        this.vaccineDoseList = vaccineDoseList;
    }

    @JsonProperty("VaccineLotId")
    public String getVaccineLotId() {
        return vaccineLotId;
    }

    public void setVaccineLotId(String vaccineLotId) {
        this.vaccineLotId = vaccineLotId;
    }

    @JsonProperty("VaccineLotText")
    public String getVaccineLotText() {
        return vaccineLotText;
    }

    public void setVaccineLotText(String vaccineLotText) {
        this.vaccineLotText = vaccineLotText;
    }

    public String getLocalDB_vaccinationEventID() {
        return localDB_vaccinationEventID;
    }

    public void setLocalDB_vaccinationEventID(String localDB_vaccinationEventID) {
        this.localDB_vaccinationEventID = localDB_vaccinationEventID;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_CHILD_ID, childId);
        map.put(TAG_APPOINTMENT_ID, appointmentId);
        map.put(TAG_DOSE_ID, doseId);
        map.put(TAG_VACCINE_LOT_ID, vaccineLotId);
        map.put(TAG_HEALTH_FACILITY_ID, healthFacilityId);
        map.put(TAG_SCHEDULED_DATE, scheduledDate);
        map.put(TAG_VACCINATION_DATE, vaccinationDate);
        map.put(TAG_VACCINATION_STATUS, vaccinationStatus);
        map.put(TAG_NONVACCINATION_REASON_ID, nonvaccinationReasonId);
        map.put(TAG_IS_ACTIVE, isActive);
        map.put(TAG_MODIFIED_ON, modifiedOn);
        map.put(TAG_MODIFIED_BY, modifiedBy);
        map.put(TAG_NOTES, notes);
        map.put(BaseColumns._ID, localDB_vaccinationEventID);

        return map;
    }

    public String toString() {
        return scheduledDate;
    }

}
