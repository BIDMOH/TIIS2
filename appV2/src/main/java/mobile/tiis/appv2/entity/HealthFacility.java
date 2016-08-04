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

/**
 * Created by Melisa on 03/02/2015.
 */
public class HealthFacility {


    private String id;
    private String address;
    private String coldStorageCapacity;
    private String contact;
    private String isActive;
    private String leaf;
    private String lowcode;
    private String modifiedBy;
    private String name;
    private String code;
    private String notes;
    private String ownership;
    private String topLevel;
    private String typeId;
    private String vaccinationPoint;
    private String vaccineStore;
    private String parentId;
    private String modifiedOn;

    public HealthFacility(){}

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("ColdStorageCapacity")
    public String getColdStorageCapacity() {
        return coldStorageCapacity;
    }

    public void setColdStorageCapacity(String coldStorageCapacity) {
        this.coldStorageCapacity = coldStorageCapacity;
    }

    @JsonProperty("Contact")
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @JsonProperty("IsActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("Leaf")
    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    @JsonProperty("Lowcode")
    public String getLowcode() {
        return lowcode;
    }

    public void setLowcode(String lowcode) {
        this.lowcode = lowcode;
    }

    @JsonProperty("ModifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Ownership")
    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    @JsonProperty("TopLevel")
    public String getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(String topLevel) {
        this.topLevel = topLevel;
    }

    @JsonProperty("TypeId")
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @JsonProperty("VaccinationPoint")
    public String getVaccinationPoint() {
        return vaccinationPoint;
    }

    public void setVaccinationPoint(String vaccinationPoint) {
        this.vaccinationPoint = vaccinationPoint;
    }

    @JsonProperty("VaccineStore")
    public String getVaccineStore() {
        return vaccineStore;
    }

    public void setVaccineStore(String vaccineStore) {
        this.vaccineStore = vaccineStore;
    }

    @JsonProperty("ParentId")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("ModifiedOn")
    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }



    public static final String TAG_HEALTH_FACILITY = "HealthFacility";
    public static final String TAG_SEARCH_HEALTH_FACILITIES = "HealthFacilities";
    public static final String TAG_ID = "ID";
    public static final String TAG_NAME = "NAME";
    public static final String TAG_CODE = "CODE";
    public static final String TAG_PARENT_ID = "PARENT_ID";
    public static final String TAG_MODIFIED_ON = "MODIFIED_ON";

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_NAME, name);
        map.put(TAG_CODE, code);
        map.put(TAG_PARENT_ID, parentId);
        map.put(TAG_MODIFIED_ON, modifiedOn);

        return map;
    }

    public String toString() {
        return name;
    }
}
