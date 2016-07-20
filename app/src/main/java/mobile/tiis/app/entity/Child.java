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

import java.io.Serializable;


public class Child implements Serializable{

    private String address;
    private String barcodeID;
    private String birthdate;
    private String birthplace;
    private String birthplaceId;
    private String caretakerFirstname;
    private String caretakerId;
    private String caretakerLastname;
    private String community;
    private String communityId;
    private String domicile;
    private String domicileId;
    private String email;
    private String fatherFirstname;
    private String fatherId;
    private String fatherLastname;
    private String firstname1;
    private String firstname2;
    private String gender;
    private String healthcenter;
    private String healthcenterId;
    private String id;
    private String identificationNo1;
    private String identificationNo2;
    private String identificationNo3;
    private String isActive;
    private String lastname1;
    private String lastname2;
    private String mobile;
    private String modifiedBy;
    private String modifiedOn;
    private String motherFirstname;
    private String motherId;
    private String motherLastname;
    private String MotherHivStatus;
    private String MotherTT2Status;
    private String notes;
    private String phone;
    private String status;
    private String statusId;
    private String systemId;
    private String tempId;
    private String ChildCumulativeSn;
    private String ChildRegistryYear;


    public Child() {

    }

    @JsonProperty("Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("BarcodeId")
    public String getBarcodeID() {
        return barcodeID;
    }

    public void setBarcodeID(String barcodeID) {
        this.barcodeID = barcodeID;
    }

    @JsonProperty("Birthdate")
    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    @JsonProperty("Birthplace")
    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    @JsonProperty("BirthplaceId")
    public String getBirthplaceId() {
        return birthplaceId;
    }

    public void setBirthplaceId(String birthplaceId) {
        this.birthplaceId = birthplaceId;
    }

    @JsonProperty("CaretakerFirstname")
    public String getCaretakerFirstname() {
        return caretakerFirstname;
    }

    public void setCaretakerFirstname(String caretakerFirstname) {
        this.caretakerFirstname = caretakerFirstname;
    }

    @JsonProperty("CaretakerId")
    public String getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(String caretakerId) {
        this.caretakerId = caretakerId;
    }

    @JsonProperty("CaretakerLastname")
    public String getCaretakerLastname() {
        return caretakerLastname;
    }

    public void setCaretakerLastname(String caretakerLastname) {
        this.caretakerLastname = caretakerLastname;
    }

    @JsonProperty("Community")
    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @JsonProperty("CommunityId")
    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    @JsonProperty("Domicile")
    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    @JsonProperty("DomicileId")
    public String getDomicileId() {
        return domicileId;
    }

    public void setDomicileId(String domicileId) {
        this.domicileId = domicileId;
    }

    @JsonProperty("Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("FatherFirstname")
    public String getFatherFirstname() {
        return fatherFirstname;
    }

    public void setFatherFirstname(String fatherFirstname) {
        this.fatherFirstname = fatherFirstname;
    }

    @JsonProperty("FatherId")
    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    @JsonProperty("FatherLastname")
    public String getFatherLastname() {
        return fatherLastname;
    }

    public void setFatherLastname(String fatherLastname) {
        this.fatherLastname = fatherLastname;
    }

    @JsonProperty("Firstname1")
    public String getFirstname1() {
        return firstname1;
    }

    public void setFirstname1(String firstname1) {
        this.firstname1 = firstname1;
    }

    @JsonProperty("Firstname2")
    public String getFirstname2() {
        return firstname2;
    }

    public void setFirstname2(String firstname2) {
        this.firstname2 = firstname2;
    }

    @JsonProperty("Gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("Healthcenter")
    public String getHealthcenter() {
        return healthcenter;
    }

    public void setHealthcenter(String healthcenter) {
        this.healthcenter = healthcenter;
    }

    @JsonProperty("HealthcenterId")
    public String getHealthcenterId() {
        return healthcenterId;
    }

    public void setHealthcenterId(String healthcenterId) {
        this.healthcenterId = healthcenterId;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("IdentificationNo1")
    public String getIdentificationNo1() {
        return identificationNo1;
    }

    public void setIdentificationNo1(String identificationNo1) {
        this.identificationNo1 = identificationNo1;
    }

    @JsonProperty("IdentificationNo2")
    public String getIdentificationNo2() {
        return identificationNo2;
    }

    public void setIdentificationNo2(String identificationNo2) {
        this.identificationNo2 = identificationNo2;
    }

    @JsonProperty("IdentificationNo3")
    public String getIdentificationNo3() {
        return identificationNo3;
    }

    public void setIdentificationNo3(String identificationNo3) {
        this.identificationNo3 = identificationNo3;
    }

    @JsonProperty("IsActive")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("Lastname1")
    public String getLastname1() {
        return lastname1;
    }

    public void setLastname1(String lastname1) {
        this.lastname1 = lastname1;
    }

    @JsonProperty("Lastname2")
    public String getLastname2() {
        return lastname2;
    }

    public void setLastname2(String lastname2) {
        this.lastname2 = lastname2;
    }

    @JsonProperty("Mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    @JsonProperty("MotherFirstname")
    public String getMotherFirstname() {
        return motherFirstname;
    }

    public void setMotherFirstname(String motherFirstname) {
        this.motherFirstname = motherFirstname;
    }

    @JsonProperty("MotherId")
    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    @JsonProperty("MotherLastname")
    public String getMotherLastname() {
        return motherLastname;
    }

    public void setMotherLastname(String motherLastname) {
        this.motherLastname = motherLastname;
    }

    @JsonProperty("Notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("Phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("StatusId")
    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    @JsonProperty("SystemId")
    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @JsonProperty("TempId")
    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    @JsonProperty("MotherHivStatus")
    public String getMotherHivStatus() {
        return MotherHivStatus;
    }

    public void setMotherHivStatus(String motherHivStatus) {
        this.MotherHivStatus = motherHivStatus;
    }

    @JsonProperty("MotherTT2Status")
    public String getMotherTT2Status() {
        return MotherTT2Status;
    }

    public void setMotherTT2Status(String motherTT2Status) {
        this.MotherTT2Status = motherTT2Status;
    }

    @JsonProperty("ChildCumulativeSn")
    public String getChildCumulativeSn() {
        return ChildCumulativeSn;
    }

    public void setChildCumulativeSn(String childCumulativeSn) {
        this.ChildCumulativeSn = childCumulativeSn;
    }

    @JsonProperty("ChildRegistryYear")
    public String getChildRegistryYear() {
        return ChildRegistryYear;
    }

    public void setChildRegistryYear(String childRegistryYear) {
        this.ChildRegistryYear = childRegistryYear;
    }

    //
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//
//
//    public String getSystemID() {
//        return systemID;
//    }
//
//    public void setSystemID(String systemID) {
//        this.systemID = systemID;
//    }
//
//    public String getBarcodeID() {
//        return barcodeID;
//    }
//
//    public void setBarcodeID(String barcodeID) {
//        this.barcodeID = barcodeID;
//    }
//
//    public String getTempID() {
//        return tempID;
//    }
//
//    public void setTempID(String tempID) {
//        this.tempID = tempID;
//    }
//
//    public String getFirstname() {
//        return firstname1;
//    }
//
//    public void setFirstname(String firstname1) {
//        this.firstname1 = firstname1;
//    }
//
//    public String getLastname() {
//        return lastname1;
//    }
//
//    public void setLastname(String lastname1) {
//        this.lastname1 = lastname1;
//    }
//
//    public String getBirthdate() {
//        return birthdate;
//    }
//
//    public void setBirthdate(String birthdate) {
//        this.birthdate = birthdate;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getBirthplaceId() {
//        return birthplaceId;
//    }
//
//    public void setBirthplaceId(String birthplaceId) {
//        this.birthplaceId = birthplaceId;
//    }
//
//    public String getCommunityId() {
//        return communityId;
//    }
//
//    public void setCommunityId(String communityId) {
//        this.communityId = communityId;
//    }
//
//    public String getDomicileId() {
//        return domicileId;
//    }
//
//    public void setDomicileId(String domicileId) {
//        this.domicileId = domicileId;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//
//    public String getMotherFirstname() {
//        return motherFirstname;
//    }
//
//    public void setMotherFirstname(String motherFirstname) {
//        this.motherFirstname = motherFirstname;
//    }
//
//    public String getMotherLastname() {
//        return motherLastname;
//    }
//
//    public void setMotherLastname(String motherLastname) {
//        this.motherLastname = motherLastname;
//    }
//
//    public String getMotherId() {
//        return motherId;
//    }
//
//    public void setMotherId(String motherId) {
//        this.motherId = motherId;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    public String getModifiedOn() {
//        return modifiedOn;
//    }
//
//    public void setModifiedOn(String modifiedOn) {
//        this.modifiedOn = modifiedOn;
//    }
//
//    public String getModifiedBy() {
//        return modifiedBy;
//    }
//
//    public void setModifiedBy(String modifiedBy) {
//        this.modifiedBy = modifiedBy;
//    }
//
//    public String getStatusId() {
//        return statusId;
//    }
//
//    public void setStatusId(String statusId) {
//        this.statusId = statusId;
//    }
//
//    public String getHealthFacilityId() {
//        return healthFacilityId;
//    }
//
//    public void setHealthFacilityId(String healthFacilityId) {
//        this.healthFacilityId = healthFacilityId;
//    }
//
//    public String getLocalDB_childID() {
//        return localDB_childID;
//    }
//
//    public void setLocalDB_childID(String localDB_childID) {
//        this.localDB_childID = localDB_childID;
//    }


    public static final String TAG_CHILD = "Child";
    public static final String TAG_ID = "ID";
    public static final String TAG_SYSTEM_ID = "SYSTEM_ID";
    public static final String TAG_BARCODE_ID = "BARCODE_ID";
    public static final String TAG_TEMP_ID = "TEMP_ID";
    public static final String TAG_FIRSTNAME1 = "FIRSTNAME1";
    public static final String TAG_LASTNAME1 = "LASTNAME1";
    public static final String TAG_BIRTHDATE = "BIRTHDATE";
    public static final String TAG_GENDER = "GENDER";
    public static final String TAG_BIRTHPLACE_ID = "BIRTHPLACE_ID";
    public static final String TAG_COMMUNITY_ID = "COMMUNITY_ID";
    public static final String TAG_DOMICILE_ID = "DOMICILE_ID";
    public static final String TAG_ADDRESS = "ADDRESS";
    public static final String TAG_PHONE = "PHONE";
    public static final String TAG_MOBILE = "MOBILE";
    public static final String TAG_MOTHER_FIRSTNAME = "MOTHER_FIRSTNAME";
    public static final String TAG_MOTHER_LASTNAME = "MOTHER_LASTNAME";
    public static final String TAG_MOTHER_ID = "MOTHER_ID";
    public static final String TAG_NOTES = "NOTES";
    public static final String TAG_MODIFIED_ON = "MODIFIED_ON";
    public static final String TAG_MODIFIED_BY = "MODIFIED_BY";
    public static final String TAG_STATUS_ID = "STATUS_ID";
    public static final String TAG_HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    public static final String TAG_CUMULATIVE_SERIAL_NUMBER = "CUMULATIVE_SERIAL_NUMBER";
    public static final String TAG_REGISTRY_YEAR = "REGISTRY_YEAR";
    public static final String TAG_MOTHER_VVU_STATUS = "MOTHER_VVU_STATUS";
    public static final String TAG_MOTHER_TT2_STATUS = "MOTHER_TT2_STATUS";

//    /**
//     * @return a <code>Map</code> containing all tags
//     */
//    public Map<String, String> toMap() {
//        Map<String, String> map = new HashMap<String, String>();
//
//        map.put(TAG_ID, id);
//        map.put(TAG_SYSTEM_ID, systemID);
//        map.put(TAG_BARCODE_ID, barcodeID);
//        map.put(TAG_TEMP_ID, tempID);
//        map.put(TAG_FIRSTNAME1, firstname1);
//        map.put(TAG_LASTNAME1, lastname1);
//        map.put(TAG_BIRTHDATE, birthdate);
//        map.put(TAG_GENDER, gender);
//        map.put(TAG_BIRTHPLACE_ID, birthplaceId);
//        map.put(TAG_COMMUNITY_ID, communityId);
//        map.put(TAG_DOMICILE_ID, domicileId);
//        map.put(TAG_STATUS_ID, statusId);
//        map.put(TAG_ADDRESS, address);
//        map.put(TAG_PHONE, phone);
//        map.put(TAG_MOBILE, mobile);
//        map.put(TAG_MOTHER_FIRSTNAME, motherFirstname);
//        map.put(TAG_MOTHER_LASTNAME, motherLastname);
//        map.put(TAG_MOTHER_ID, motherId);
//        map.put(TAG_NOTES, notes);
//        map.put(TAG_MODIFIED_ON, modifiedOn);
//        map.put(TAG_MODIFIED_BY, modifiedBy);
//
//        map.put(BaseColumns._ID, localDB_childID);
//
//        return map;
//    }
}
