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

package mobile.tiis.appv2.DatabaseModals;

/**
 * Created by Rubin on 3/18/2015.
 */
public class SessionsModel extends Modal {

    private int _id,STATUS ;
    private String USER_ID,HEALTH_FACILITY_ID,url;
    private long LOGING_TIME,SESSION_LENGTH;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setStatus(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUser_id(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getHEALTH_FACILITY_ID() {
        return HEALTH_FACILITY_ID;
    }

    public void setHealth_facility_id(String HEALTH_FACILITY_ID) {
        this.HEALTH_FACILITY_ID = HEALTH_FACILITY_ID;
    }

    public long getLOGING_TIME() {
        return LOGING_TIME;
    }

    public void setLoging_time(long LOGING_TIME) {
        this.LOGING_TIME = LOGING_TIME;
    }

    public long getSESSION_LENGTH() {
        return SESSION_LENGTH;
    }

    public void setSession_length(long SESSION_LENGTH) {
        this.SESSION_LENGTH = SESSION_LENGTH;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
