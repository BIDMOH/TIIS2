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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Dose {

    private String ageDefinition;
    private String ageDefinitionId;
    private String doseNumber;
    private String fromAgeDef;
    private String fromAgeDefId;
    private String fullname;
    private String id;
    private String isActive;
    private String modifiedBy;
    private String modifiedOn;
    private String notes;
    private String scheduledVaccination;
    private String scheduledVaccinationId;
    private String toAgeDef;
    private String toAgeDefId;
    private String vaccinationEventId;

    public Dose(){}

    @JsonProperty("AgeDefinition")
    public String getAgeDefinition() {
        return ageDefinition;
    }

    public void setAgeDefinition(String ageDefinition) {
        this.ageDefinition = ageDefinition;
    }

    @JsonProperty("AgeDefinitionId")
    public String getAgeDefinitionId() {
        return ageDefinitionId;
    }

    public void setAgeDefinitionId(String ageDefinitionId) {
        this.ageDefinitionId = ageDefinitionId;
    }

    @JsonProperty("DoseNumber")
    public String getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(String doseNumber) {
        this.doseNumber = doseNumber;
    }

    @JsonProperty("FromAgeDefinition")
    public String getFromAgeDef() {
        return fromAgeDef;
    }

    public void setFromAgeDef(String fromAgeDef) {
        this.fromAgeDef = fromAgeDef;
    }

    @JsonProperty("FromAgeDefinitionId")
    public String getFromAgeDefId() {
        return fromAgeDefId;
    }

    public void setFromAgeDefId(String fromAgeDefId) {
        this.fromAgeDefId = fromAgeDefId;
    }

    @JsonProperty("Fullname")
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    @JsonProperty("ScheduledVaccination")
    public String getScheduledVaccination() {
        return scheduledVaccination;
    }

    public void setScheduledVaccination(String scheduledVaccination) {
        this.scheduledVaccination = scheduledVaccination;
    }

    @JsonProperty("ScheduledVaccinationId")
    public String getScheduledVaccinationId() {
        return scheduledVaccinationId;
    }

    public void setScheduledVaccinationId(String scheduledVaccinationId) {
        this.scheduledVaccinationId = scheduledVaccinationId;
    }

    @JsonProperty("ToAgeDefinition")
    public String getToAgeDef() {
        return toAgeDef;
    }

    public void setToAgeDef(String toAgeDef) {
        this.toAgeDef = toAgeDef;
    }

    @JsonProperty("ToAgeDefinitionId")
    public String getToAgeDefId() {
        return toAgeDefId;
    }

    public void setToAgeDefId(String toAgeDefId) {
        this.toAgeDefId = toAgeDefId;
    }

    @JsonProperty("VaccinationEventId")
    public String getVaccinationEventId() {
        return vaccinationEventId;
    }

    public void setVaccinationEventId(String vaccinationEventId) {
        this.vaccinationEventId = vaccinationEventId;
    }


    //To be checked for usage

    public static final String TAG_DOSE = "Dose";
    public static final String TAG_ID = "ID";
    public static final String TAG_SCHEDULED_VACCINATION_ID = "SCHEDULED_VACCINATION_ID";
    public static final String TAG_DOSE_NUMBER = "DOSE_NUMBER";
    public static final String TAG_FULLNAME= "FULLNAME";
    public static final String TAG_AGE_DEFINITION_ID = "AGE_DEFINITON_ID";
    public static final String TAG_FROM_AGE_DEFINITION_ID = "FROM_AGE_DEFINITON_ID";
    public static final String TAG_TO_AGE_DEFINITION_ID= "TO_AGE_DEFINITON_ID";

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_SCHEDULED_VACCINATION_ID, scheduledVaccinationId);
        map.put(TAG_DOSE_NUMBER, doseNumber);
        map.put(TAG_FULLNAME, fullname);
        map.put(TAG_AGE_DEFINITION_ID, ageDefinitionId);
        map.put(TAG_FROM_AGE_DEFINITION_ID, fromAgeDefId);
        map.put(TAG_TO_AGE_DEFINITION_ID, toAgeDefId);

        return map;
    }

    public String toString() {
        return doseNumber;
    }


}
