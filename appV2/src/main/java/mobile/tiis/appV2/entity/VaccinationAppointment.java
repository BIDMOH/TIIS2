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


public class VaccinationAppointment {

    private String aefi;
    private String aefiDate;
    private String child;
    private String childId;
    private String id;
    private String isActive;
    private String modifiedBy;
    private String modifiedOn;
    private String notes;
    private String outreach;
    private String scheduledDate;
    private String scheduledFacility;
    private String scheduledFacilityId;


    public VaccinationAppointment(){}

    @JsonProperty("Aefi")
    public String getAefi() {
        return aefi;
    }

    public void setAefi(String aefi) {
        this.aefi = aefi;
    }

    @JsonProperty("AefiDate")
    public String getAefiDate() {
        return aefiDate;
    }

    public void setAefiDate(String aefiDate) {
        this.aefiDate = aefiDate;
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

    @JsonProperty("ModifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("ModifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Outreach")
    public String getOutreach() {
        return outreach;
    }

    public void setOutreach(String outreach) {
        this.outreach = outreach;
    }

    @JsonProperty("ScheduledDate")
    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    @JsonProperty("ScheduledFacility")
    public String getScheduledFacility() {
        return scheduledFacility;
    }

    public void setScheduledFacility(String scheduledFacility) {
        this.scheduledFacility = scheduledFacility;
    }

    @JsonProperty("ScheduledFacilityId")
    public String getScheduledFacilityId() {
        return scheduledFacilityId;
    }

    public void setScheduledFacilityId(String scheduledFacilityId) {
        this.scheduledFacilityId = scheduledFacilityId;
    }




    private String localDB_vaccinationAppointmentID;

    public String getLocalDB_vaccinationAppointmentID() {
        return localDB_vaccinationAppointmentID;
    }

    public void setLocalDB_vaccinationAppointmentID(String localDB_vaccinationAppointmentID) {
        this.localDB_vaccinationAppointmentID = localDB_vaccinationAppointmentID;
    }


    public static final String TAG_VACCINATION_APPOINTMENT = "VaccinationAppointment";
    public static final String TAG_ID = "ID";
    public static final String TAG_CHILD_ID = "CHILD_ID";
    public static final String TAG_SCHEDULED_FACILITY_ID = "SCHEDULED_FACILITY_ID";
    public static final String TAG_SCHEDULED_DATE= "SCHEDULED_DATE";
    public static final String TAG_IS_ACTIVE = "IS_ACTIVE";
    public static final String TAG_NOTES = "NOTES";
    public static final String TAG_MODIFIED_ON = "MODIFIED_ON";
    public static final String TAG_MODIFIED_BY = "MODIFIED_BY";

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_CHILD_ID, childId);
        map.put(TAG_SCHEDULED_FACILITY_ID, scheduledFacilityId);
        map.put(TAG_SCHEDULED_DATE, scheduledDate);
        map.put(TAG_IS_ACTIVE, isActive);
        map.put(TAG_NOTES, notes);
        map.put(TAG_MODIFIED_ON, modifiedOn);
        map.put(TAG_MODIFIED_BY, modifiedBy);

        map.put(BaseColumns._ID, localDB_vaccinationAppointmentID);

        return map;
    }

    public String toString() {
        return scheduledDate;
    }

}
