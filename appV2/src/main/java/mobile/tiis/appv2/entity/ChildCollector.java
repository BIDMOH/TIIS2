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

import java.util.List;

/**
 * Created by Teodor on 2/16/2015.
 */
public class ChildCollector {

    private Child childEntity;
    private List<VaccinationAppointment> vaList;
    private List<VaccinationEvent> veList;

    @JsonProperty("childEntity")
    public Child getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(Child childEntity) {
        this.childEntity = childEntity;
    }

    @JsonProperty("veList")
    public List<VaccinationEvent> getVeList() {
        return veList;
    }

    public void setVeList(List<VaccinationEvent> veList) {
        this.veList = veList;
    }

    @JsonProperty("vaList")
    public List<VaccinationAppointment> getVaList() {
        return vaList;
    }

    public void setVaList(List<VaccinationAppointment> vaList) {
        this.vaList = vaList;
    }
}
