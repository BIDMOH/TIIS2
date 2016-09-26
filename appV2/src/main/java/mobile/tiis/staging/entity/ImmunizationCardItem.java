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

public class ImmunizationCardItem {


    private String vacineDose;
    private String vaccineLot;
    private String healthCenterName;
    private Date vaccinationDate;
    private String vaccinationStringFormatted;
    private boolean done;
    private String nonVaccinaitonReason;
    private String appointementId;

    public ImmunizationCardItem(String vacineDose, String vaccineLot, String healthCenterName, Date vaccinationDate, String vaccinationStringFormatted, boolean done, String nonVaccinaitonReason, String appointementId) {
        this.vacineDose = vacineDose;
        this.vaccineLot = vaccineLot;
        this.healthCenterName = healthCenterName;
        this.vaccinationDate = vaccinationDate;
        this.vaccinationStringFormatted = vaccinationStringFormatted;
        this.done = done;
        this.nonVaccinaitonReason = nonVaccinaitonReason;
        this.appointementId = appointementId;
    }

    public ImmunizationCardItem() {
    }

    public String getVacineDose() {
        return vacineDose;
    }

    public void setVacineDose(String vacineDose) {
        this.vacineDose = vacineDose;
    }

    public String getVaccineLot() {
        return vaccineLot;
    }

    public void setVaccineLot(String vaccineLot) {
        this.vaccineLot = vaccineLot;
    }

    public String getHealthCenterName() {
        return healthCenterName;
    }

    public void setHealthCenterName(String healthCenterName) {
        this.healthCenterName = healthCenterName;
    }

    public Date getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(Date vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public String getVaccinationStringFormatted() {
        return vaccinationStringFormatted;
    }

    public void setVaccinationStringFormatted(String vaccinationStringFormatted) {
        this.vaccinationStringFormatted = vaccinationStringFormatted;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getNonVaccinaitonReason() {
        return nonVaccinaitonReason;
    }

    public void setNonVaccinaitonReason(String nonVaccinaitonReason) {
        this.nonVaccinaitonReason = nonVaccinaitonReason;
    }

    public String getAppointementId() {
        return appointementId;
    }

    public void setAppointementId(String appointementId) {
        this.appointementId = appointementId;
    }
}
