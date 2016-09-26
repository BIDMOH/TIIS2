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

import java.util.Date;

public class AefiListItem {

    private String appointementId;
    private String vaccines, healthFacilityName, Notes;
    private boolean aefi, done;
    private Date aefiDate, vaccinationDate, modifiedOn;
    private String modifiedById;

    public AefiListItem(String appointementId, String vaccines, String healthFacilityName, String notes, boolean aefi, boolean done, Date aefiDate, Date vaccinationDate, Date modifiedOn, String modifiedById) {
        this.appointementId = appointementId;
        this.vaccines = vaccines;
        this.healthFacilityName = healthFacilityName;
        Notes = notes;
        this.aefi = aefi;
        this.done = done;
        this.aefiDate = aefiDate;
        this.vaccinationDate = vaccinationDate;
        this.modifiedOn = modifiedOn;
        this.modifiedById = modifiedById;
    }

    public AefiListItem() {
    }

    public String getAppointementId() {
        return appointementId;
    }

    public void setAppointementId(String appointementId) {
        this.appointementId = appointementId;
    }

    public String getVaccines() {
        return vaccines;
    }

    public void setVaccines(String vaccines) {
        this.vaccines = vaccines;
    }

    public String getHealthFacilityName() {
        return healthFacilityName;
    }

    public void setHealthFacilityName(String healthFacilityName) {
        this.healthFacilityName = healthFacilityName;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public boolean isAefi() {
        return aefi;
    }

    public void setAefi(boolean aefi) {
        this.aefi = aefi;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getAefiDate() {
        return aefiDate;
    }

    public void setAefiDate(Date aefiDate) {
        this.aefiDate = aefiDate;
    }

    public Date getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(Date vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedById() {
        return modifiedById;
    }

    public void setModifiedById(String modifiedById) {
        this.modifiedById = modifiedById;
    }
}
