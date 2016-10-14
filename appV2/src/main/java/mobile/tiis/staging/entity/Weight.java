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
 * Created by Melisa on 05/02/2015.
 */
public class Weight {

    private String day;
    private String gender;
    private String id;
    private String SD0;
    private String SD1;
    private String SD1neg;
    private String SD2;
    private String SD2neg;
    private String SD3;
    private String SD3neg;
    private String SD4;
    private String SD4neg;


    public Weight(){}

    @JsonProperty("Day")
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @JsonProperty("Gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("SD0")
    public String getSD0() {
        return SD0;
    }

    public void setSD0(String SD0) {
        this.SD0 = SD0;
    }

    @JsonProperty("SD1")
    public String getSD1() {
        return SD1;
    }

    public void setSD1(String SD1) {
        this.SD1 = SD1;
    }

    @JsonProperty("SD1neg")
    public String getSD1neg() {
        return SD1neg;
    }

    public void setSD1neg(String SD1neg) {
        this.SD1neg = SD1neg;
    }

    @JsonProperty("SD2")
    public String getSD2() {
        return SD2;
    }

    public void setSD2(String SD2) {
        this.SD2 = SD2;
    }

    @JsonProperty("SD2neg")
    public String getSD2neg() {
        return SD2neg;
    }

    public void setSD2neg(String SD2neg) {
        this.SD2neg = SD2neg;
    }

    @JsonProperty("SD3")
    public String getSD3() {
        return SD3;
    }

    public void setSD3(String SD3) {
        this.SD3 = SD3;
    }

    @JsonProperty("SD3neg")
    public String getSD3neg() {
        return SD3neg;
    }

    public void setSD3neg(String SD3neg) {
        this.SD3neg = SD3neg;
    }

    @JsonProperty("SD4")
    public String getSD4() {
        return SD4;
    }

    public void setSD4(String SD4) {
        this.SD4 = SD4;
    }

    @JsonProperty("SD4neg")
    public String getSD4neg() {
        return SD4neg;
    }

    public void setSD4neg(String SD4neg) {
        this.SD4neg = SD4neg;
    }


    public static final String TAG_STATUS = "Status";
    public static final String TAG_ID = "ID";
    public static final String TAG_CHILD_ID = "CHILD_ID";
    public static final String TAG_WEIGHT = "WEIGHT";
    public static final String TAG_DATE = "DATE";
    public static final String TAG_NOTES = "NOTES";
    public static final String TAG_MODIFIED_ON = "MODIFIED_ON";
    public static final String TAG_MODIFIED_BY = "MODIFIED_BY";

//    public Map<String, String> toMap() {
//        Map<String, String> map = new HashMap<String, String>();
//
//        map.put(TAG_ID, id);
//        map.put(TAG_CHILD_ID, childId);
//        map.put(TAG_WEIGHT, weight);
//        map.put(TAG_DATE, date);
//        map.put(TAG_NOTES, notes);
//        map.put(TAG_MODIFIED_ON, modifiedOn);
//        map.put(TAG_MODIFIED_BY, modifiedBy);
//
//        return map;
//    }

//    public String toString() {
//        return weight;
//    }
}
