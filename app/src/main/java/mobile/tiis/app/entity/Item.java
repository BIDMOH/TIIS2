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

package mobile.tiis.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Item {

    private String code;
    private String entryDate;
    private String exitDate;
    private String hl7Vaccine;
    private String hl7VaccineId;
    private String id;
    private String isActive;
    private String itemCategory;
    private String itemCategoryId;
    private String modifiedBy;
    private String modifiedOn;
    private String name;
    private String notes;


    public Item(){}

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @JsonProperty("Hl7Vaccine")
    public String getHl7Vaccine() {
        return hl7Vaccine;
    }

    public void setHl7Vaccine(String hl7Vaccine) {
        this.hl7Vaccine = hl7Vaccine;
    }

    @JsonProperty("Hl7VaccineId")
    public String getHl7VaccineId() {
        return hl7VaccineId;
    }

    public void setHl7VaccineId(String hl7VaccineId) {
        this.hl7VaccineId = hl7VaccineId;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("ItemCategory")
    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    @JsonProperty("ItemCategoryId")
    public String getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(String itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
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

    @JsonProperty("IsActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }



    //Check for usage

    public static final String TAG_ITEM = "Item";
    public static final String TAG_ID = "ID";
    public static final String TAG_ITEM_CATEGORY_ID = "ITEM_CATEGORY_ID";
    public static final String TAG_NAME = "NAME";
    public static final String TAG_CODE = "CODE";
    public static final String TAG_ENTRY_DATE = "ENTRY_DATE";
    public static final String TAG_EXIT_DATE = "EXIT_DATE";

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(TAG_ID, id);
        map.put(TAG_ITEM_CATEGORY_ID, itemCategoryId);
        map.put(TAG_NAME, name);
        map.put(TAG_CODE, code);
        map.put(TAG_ENTRY_DATE, entryDate);
        map.put(TAG_EXIT_DATE, exitDate);

        return map;
    }

    public String toString() {
        return name;
    }

}
