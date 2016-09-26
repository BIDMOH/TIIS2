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

package mobile.tiis.appV2.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


public class ScheduledVaccination {

    private String code;
    private String deseases;
    private String entryDate;
    private String exitDate;
    private String id;
    private String isActive;
    private String item;
    private String itemId;
    private String modifiedBy;
    private String modifiedOn;
    private String name;
    private String notes;
    private String status;


    public ScheduledVaccination(){}

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Deseases")
    public String getDeseases() {
        return deseases;
    }

    public void setDeseases(String deseases) {
        this.deseases = deseases;
    }

    @JsonProperty("EntryDate")
    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    @JsonProperty("ExitDate")
    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
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

    @JsonProperty("Item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @JsonProperty("ItemId")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //Check usage

    public static final String TAG_SCHEDULED_VACCINATION = "ScheduledVaccination";
    public static final String TAG_ID = "ID";
    public static final String TAG_NAME = "NAME";
    public static final String TAG_CODE = "CODE";
    public static final String TAG_ITEM_ID= "ITEM_ID";
    public static final String TAG_ENTRY_DATE = "ENTRY_DATE";
    public static final String TAG_EXIT_DATE = "EXIT_DATE";

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_NAME, name);
        map.put(TAG_CODE, code);
        map.put(TAG_ITEM_ID, itemId);
        map.put(TAG_ENTRY_DATE, entryDate);
        map.put(TAG_EXIT_DATE, exitDate);

        return map;
    }

    public String toString() {
        return name;
    }


}
