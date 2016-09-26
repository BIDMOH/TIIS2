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

package mobile.tiis.staging.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Rubin on 6/1/2015.
 */
public class AdjustmentReasons {
    private String id,name,pozitive,isActive,modBy,modOn,notes;

    public AdjustmentReasons(String id, String notes, String modOn, String modBy, String isActive, String pozitive, String name) {
        this.id = id;
        this.notes = notes;
        this.modOn = modOn;
        this.modBy = modBy;
        this.isActive = isActive;
        this.pozitive = pozitive;
        this.name = name;
    }

    public AdjustmentReasons() {
    }
    @JsonProperty("ModifiedBy")
    public String getModBy() {
        return modBy;
    }

    public void setModBy(String modBy) {
        this.modBy = modBy;
    }
    @JsonProperty("ModifiedOn")
    public String getModOn() {
        return modOn;
    }

    public void setModOn(String modOn) {
        this.modOn = modOn;
    }
    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("IsActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("Positive")
    public String getPozitive() {
        return pozitive;
    }

    public void setPozitive(String pozitive) {
        this.pozitive = pozitive;
    }
}
