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

package mobile.giis.app.entity;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;


/**
 * Created by Teodor on 2/11/2015.
 * This class will be used to easily call tables in Vaccination Queue
 * and Monthly Plan activities from the database.
 */

public class VaccinationQueueObject {

    private String _child_fullname;
    private String _village;
    private String _vaccines;
    private String _schedule;
    private String _scheduled_date;


    public VaccinationQueueObject(){

    }

    public void setChildFullname(String _child_fullname){
        this._child_fullname = _child_fullname;
    }

    public String getChildFullname(){
        return _child_fullname;
    }

    public void setVillage(String _village){
        this._village = _village;
    }

    public String getVillage(){
        return _village;
    }

    public void setVaccines(String _vaccines){
        this._vaccines = _vaccines;
    }

    public String getVaccines(){
        return _vaccines;
    }

    public void setSchedule(String _schedule){
        this._schedule = _schedule;
    }

    public String getSchedule(){
        return _schedule;
    }

    public void setScheduledDate(String _scheduled_date){
        this._scheduled_date = _scheduled_date;
    }

    public String getScheduledDate(){
        return _scheduled_date;
    }

}
