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

package mobile.tiis.appv2.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Melisa on 02/02/2015.
 */
public class GIISContract {

    public static final long UPDATED_NEVER = -2;
    public static final long UPDATED_UNKNOWN = -1;
    public static final long UPDATED_MODIFIED = -3;
    public static final long UPDATED_SYNCED = -4;

    public interface SyncColumns {
        String UPDATED = "updated";
        String OWNERS_USERNAME="owners_username";
        String MODIFIED_AT ="modfied_at";
    }

    interface ImmunizationSessionColumns{
        String FIXED_CONDUCTED = "FIXED_CONDUCTED";
        String OUTREACH_PLANNED = "OUTREACH_PLANNED";
        String OUTREACH_CONDUCTED = "OUTREACH_CONDUCTED";
        String OUTREACH_CANCELLED = "OUTREACH_CANCELLED";
        String MODIFIED_ON = "MODIFIED_ON";
        String REPORTING_MONTH = "REPORTING_MONTH";
        String OTHERACTIVITIES = "OTHERACTIVITIES";
    }

    public interface VaccinationsBcgOpvTtColumns{
        String DOSE_ID = "DOSE_ID";
        String MALE_SERVICE_AREA = "MALE_SERVICE_AREA";
        String FEMALE_SERVICE_AREA = "FEMALE_SERVICE_AREA";
        String TOTAL_SERVICE_AREA = "TOTAL_SERVICE_AREA";
        String MALE_CATCHMENT_AREA = "MALE_CATCHMENT_AREA";
        String FEMALE_CATCHMENT_AREA = "FEMALE_CATCHMENT_AREA";
        String TOTAL_CATCHMENT_AREA = "TOTAL_CATCHMENT_AREA";
        String COVERAGE_CATCHMENT_AREA = "COVERAGE_CATCHMENT_AREA";
        String COVERAGE_SERVICE_AREA = "COVERAGE_SERVICE_AREA";
        String COVERAGE_CATCHMENT_AND_SERVICE = "COVERAGE_CATCHMENT_AND_SERVICE";
        String TOTAL_CATCHMENT_AND_SERVICE_AREA = "TOTAL_CATCHMENT_AND_SERVICE_AREA";
        String REPORTING_MONTH = "REPORTING_MONTH";
        String MODIFIED_ON = "MODIFIED_ON";
    }

    public interface OtherMajorImmunizationActivitiesColumns{
        String OTHER_ACTIVITIES = "OTHER_ACTIVITIES";
        String REPORTING_MONTH = "REPORTING_MONTH";
        String MODIFIED_ON = "MODIFIED_ON";
    }

    public interface SyringesAndSafetyBoxesColumns{
        String ITEM_NAME = "ITEM_NAME";
        String OPENING_BALANCE = "OPENING_BALANCE";
        String RECEIVED = "RECEIVED";
        String USED = "USED";
        String WASTAGE = "WASTAGE";
        String STOCK_AT_HAND = "STOCK_AT_HAND";
        String STOCKED_OUT_DAYS = "STOCKED_OUT_DAYS";
        String REPORTING_MONTH = "REPORTING_MONTH";
        String MODIFIED_ON = "MODIFIED_ON";
    }

    public interface HfVitaminAColumns{
        String VITAMIN_NAME = "VITAMIN_NAME";
        String OPENING_BALANCE = "OPENING_BALANCE";
        String RECEIVED = "RECEIVED";
        String TOTAL_ADMINISTERED = "TOTAL_ADMINISTERED";
        String WASTAGE = "WASTAGE";
        String STOCK_ON_HAND = "STOCK_ON_HAND";
        String REPORTING_MONTH = "REPORTING_MONTH";
        String MODIFIED_ON = "MODIFIED_ON";
    }
    public interface HfLoginSessions{
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
        String USER_ID = "USER_ID";
        String LOGING_TIME = "LOGING_TIME";
        String SESSION_LENGTH = "SESSION_LENGTH";
        String STATUS = "STATUS";
    }

    public interface HealthFacilityColumns{
        String ID = "ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String TYPE_ID = "TYPE_ID";
        String PARENT_ID = "PARENT_ID";
        String MODIFIED_ON = "MODIFIED_ON";
    }

    public interface  PlaceColumns{
        String ID = "ID";
        String NAME = "NAME";
        String PARENT_ID = "PARENT_ID";
        String CODE = "CODE";
        String MODIFIED_ON = "MODIFIED_ON";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }

    public interface  CummulativeSerialNumberColumns{
        String ID = "ID";
        String YEAR = "YEAR";
        String MONTH = "MONTH";
        String CHILD_ID = "CHILD_ID";
        String CUMMULATIVE_SERIAL_NUMBER = "CUMMULATIVE_SERIAL_NUMBER";
        String MODIFIED_ON = "MODIFIED_ON";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }

    public interface AdjustmentColumns {
        String ID = "ID";
        String NAME = "NAME";
        String POSITIVE = "POSITIVE";
        String IS_ACTIVE = "IS_ACTIVE";
    }

    public interface  UserColumns{
        String ID = "ID";
        String USERNAME = "USERNAME";
        String PASSWORD = "PASSWORD";
        String FIRSTNAME = "FIRSTNAME";
        String LASTNAME = "LASTNAME";
        String IS_ACTIVE = "IS_ACTIVE";
        String DELETED = "DELETED";
        String NOTES = "NOTES";
        String EMAIL = "EMAIL";
        String ISLOGGEDIN = "ISLOGGEDIN";
        String LASTLOGIN = "LASTLOGIN";
        String USER_ROLE_ID = "USER_ROLE_ID";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }

    public  interface ChildColumns {
        String UPDATED = "updated";

        String ID = "ID";
        String SYSTEM_ID = "SYSTEM_ID";
        String BARCODE_ID = "BARCODE_ID";
        String TEMP_ID = "TEMP_ID";
        String FIRSTNAME1 = "FIRSTNAME1";
        String FIRSTNAME2 = "FIRSTNAME2";
        String LASTNAME1 = "LASTNAME1";
        String BIRTHDATE = "BIRTHDATE";
        String GENDER = "GENDER";
        String BIRTHPLACE_ID = "BIRTHPLACE_ID";
        String BIRTHPLACE = "BIRTHPLACE";
        String COMMUNITY_ID = "COMMUNITY_ID";
        String DOMICILE_ID = "DOMICILE_ID";
        String DOMICILE = "DOMICILE";
        String ADDRESS = "ADDRESS";
        String PHONE = "PHONE";
        String MOBILE = "MOBILE";
        String MOTHER_FIRSTNAME = "MOTHER_FIRSTNAME";
        String MOTHER_LASTNAME = "MOTHER_LASTNAME";
        String MOTHER_ID = "MOTHER_ID";
        String NOTES = "NOTES";
        String MODIFIED_ON = "MODIFIED_ON";
        String MODIFIED_BY = "MODIFIED_BY";
        String STATUS_ID = "STATUS_ID";
        String STATUS = "STATUS";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
        String HEALTH_FACILITY = "HEALTH_FACILITY";
        String MOTHER_VVU_STS  = "MOTHER_VVU_STS";
        String MOTHER_TT2_STS = "MOTHER_TT2_STS";
        String CUMULATIVE_SERIAL_NUMBER = "CUMULATIVE_SERIAL_NUMBER";
        String CHILD_REGISTRY_YEAR = "CHILD_REGISTRY_YEAR";
    }

    public interface  StatusColumns{
        String ID = "ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String PARENT_ID = "PARENT_ID";

      }

    interface  CommunityColumns{
        String ID = "ID";
        String NAME = "NAME";
    }

    interface  ChildWeightColumns{
        String ID = "ID";
        String CHILD_ID = "CHILD_ID";
        String CHILD_BARCODE = "CHILD_BARCODE";
        String WEIGHT = "WEIGHT";
        String DATE = "DATE";
        String NOTES = "NOTES";
        String MODIFIED_ON = "MODIFIED_ON";
        String MODIFIED_BY = "MODIFIED_BY";
    }

    interface  WeightColumns{
        String ID = "ID";
        String DAY = "DAY";
        String GENDER = "GENDER";
        String SD0 = "SD0";
        String SD1 = "SD1";
        String SD1NEG = "SD1NEG";
        String SD2 = "SD2";
        String SD2NEG = "SD2NEG";
        String SD3 = "SD3";
        String SD3NEG = "SD3NEG";
        String SD4 = "SD4";
        String SD4NEG = "SD4NEG";
    }

    interface  NonVaccinationReasonColumns{
        String ID = "ID";
        String NAME = "NAME";
        String KEEP_CHILD_DUE = "KEEP_CHILD_DUE";
    }

    interface  AgeDefinitionsColumns{
        String ID = "ID";
        String NAME = "NAME";
        String DAYS = "DAYS";
    }

    interface  ItemColumns{
        String ID = "ID";
        String ITEM_CATEGORY_ID = "ITEM_CATEGORY_ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String ENTRY_DATE = "ENTRY_DATE";
        String EXIT_DATE = "EXIT_DATE";
    }

    interface SurveillanceColumns{
        String ID = "ID";
        String FEVER_MONTHLY_CASES = "FEVER_MONTHLY_CASES";
        String FEVER_DEATHS = "FEVER_DEATHS";
        String APF_MONTHLY_CASES = "AFP_MONTHLY_CASES";
        String APF_DEATHS = "AFP_DEATHS";
        String NEONATAL_TT_CASES = "NEONATAL_TT_CASES";
        String NEONATAL_TT_DEATHS = "NEONATAL_TT_DEATHS";
        String REPORTED_MONTH = "REPORTED_MONTH";
    }

    interface RefrigeratorColums{
        String ID = "ID";
        String TEMP_MIN = "TEMP_MIN";
        String TEMP_MAX = "TEMP_MAX";
        String ALARM_LOW_TEMP = "ALARM_LOW_TEMP";
        String ALARM_HIGH_TEMP = "ALARM_HIGH_TEMP";
        String REPORTED_MONTH = "REPORTED_MONTH";
    }

    interface StockStatusColumns{
        String ID = "ID";
        String ITEM_NAME = "ITEM_NAME";
        String OPPENING_BALANCE ="OPPENING_BALANCE";
        String CLOSING_BALANCE = "CLOSING_BALANCE";
        String DOSES_RECEIVED = "DOSES_RECEIVED";
        String DISCARDED_UNOPENED = "DISCARDED_UNOPENED";
        String DISCARDED_OPENED = "DISCARDED_OPENED";
        String IMMUNIZED_CHILDREN = "IMMUNIZED_CHILDREN";
        String REPORTED_MONTH = "REPORTED_MONTH";
    }

    interface  ScheduledVaccinationColumns{
        String ID = "ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String ITEM_ID = "ITEM_ID";
        String ENTRY_DATE = "ENTRY_DATE";
        String EXIT_DATE = "EXIT_DATE";
    }

    interface  DoseColumns{
        String ID = "ID";
        String SCHEDULED_VACCINATION_ID = "SCHEDULED_VACCINATION_ID";
        String DOSE_NUMBER = "DOSE_NUMBER";
        String FULLNAME = "FULLNAME";
        String AGE_DEFINITON_ID = "AGE_DEFINITON_ID";
        String FROM_AGE_DEFINITON_ID = "FROM_AGE_DEFINITON_ID";
        String TO_AGE_DEFINITON_ID = "TO_AGE_DEFINITON_ID";
    }

    interface  VaccinationAppointmentColumns{
        String ID = "ID";
        String CHILD_ID = "CHILD_ID";
        String SCHEDULED_FACILITY_ID = "SCHEDULED_FACILITY_ID";
        String SCHEDULED_DATE = "SCHEDULED_DATE";
        String IS_ACTIVE = "IS_ACTIVE";
        String NOTES = "NOTES";
        String MODIFIED_ON = "MODIFIED_ON";
        String MODIFIED_BY = "MODIFIED_BY";
        String AEFI = "AEFI";
        String AEFI_DATE = "AEFI_DATE";
        String OUTREACH = "OUTREACH";
    }

    interface  VaccinationEventColumns{
        String ID = "ID";
        String CHILD_ID = "CHILD_ID";
        String APPOINTMENT_ID = "APPOINTMENT_ID";
        String DOSE_ID = "DOSE_ID";
        String VACCINE_LOT_ID = "VACCINE_LOT_ID";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
        String SCHEDULED_DATE = "SCHEDULED_DATE";
        String VACCINATION_DATE = "VACCINATION_DATE";
        String VACCINATION_STATUS = "VACCINATION_STATUS";
        String NONVACCINATION_REASON_ID = "NONVACCINATION_REASON_ID";
        String IS_ACTIVE = "IS_ACTIVE";
        String MODIFIED_ON = "MODIFIED_ON";
        String MODIFIED_BY = "MODIFIED_BY";
        String NOTES = "NOTES";
    }

    interface ChildSupplementsColumns{
        String ID="id";
        String CHILD_ID="child_id";
        String VitA = "VitA";
        String MEBENDEZOLR = "Mebendezol";
        String DATE = "date";
        String MODIFIED_ON = "modified_on";
        String MODIFIED_BY = "modified_by";

    }

    interface ItemLotColumns{
        String GTIN = "gtin";
        String LOT_NUMBER = "lot_number";
        String ITEM_ID = "item_id";
        String EXPIRE_DATE = "expire_date";
        String NOTES = "notes";
        String ID = "id";
    }

    interface HealthFacilityBalanceColumns{
        String LOT_ID = "lot_id";
        String GTIN = "gtin";
        String LOT_NUMBER = "lot_number";
        String ITEM = "item";
        String EXPIRE_DATE = "expire_date";
        String BALANCE = "balance";
        String REORDER_QTY = "ReorderQty";
    }


    public interface ActiveLotNumbersColumns{
        String LOT_ID = "lot_id";
        String LOT_NUMBER = "lot_number";
        String ITEM = "item";
        String DATE = "date";
    }

    public static final String CONTENT_AUTHORITY = "tiis.mobile";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_HEALTH_FACILITY = "health_facility";
    private static final String PATH_PLACE = "place";
    private static final String PATH_USER = "user";
    private static final String PATH_CHILD = "child";
    private static final String PATH_STATUS = "status";
    private static final String PATH_COMMUNITY = "community";
    private static final String PATH_CHILD_WEIGHT = "child_weight";
    private static final String PATH_NONVACCINATION_REASON= "nonvaccination_reason";
    private static final String PATH_AGE_DEFINITIONS = "age_definitions";
    private static final String PATH_ITEM = "item";
    private static final String PATH_SCHEDULED_VACCINATION = "scheduled_vaccination";
    private static final String PATH_DOSE = "dose";
    private static final String PATH_VACCINATION_APPOINTMENT = "vaccination_appointment";
    private static final String PATH_VACCINATION_EVENT = "vaccination_event";
    private static final String PATH_CHILD_SUPPLEMENTS = "child_supplements";
    private static final String PATH_ITEM_LOT = "item_lot";
    private static final String PATH_HEALTH_FACILITY_BALANCE = "health_facility_balance";

    /**HEALTH_FACILITY TABLE**/
    public static class HealthFacilityTable implements HealthFacilityColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HEALTH_FACILITY).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.health_facility";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.health_facility";

        public static Uri buildHealthFacilityUri(String healthFacilityId) {
            return CONTENT_URI.buildUpon().appendPath(healthFacilityId).build();
        }
        public static String getHealthFacilityId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**PLACE TABLE**/
    public static class PlaceTable implements PlaceColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.place";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.place";

        public static Uri buildPlaceUri(String placeId) {
            return CONTENT_URI.buildUpon().appendPath(placeId).build();
        }
        public static String getPlaceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**USER TABLE**/
    public static class UserTable implements UserColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.user";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.user";

        public static Uri buildUserUri(String userId) {
            return CONTENT_URI.buildUpon().appendPath(userId).build();
        }
        public static String getUserId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**CHILD TABLE**/
    public static class ChildTable implements ChildColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHILD).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.child";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.child";

        public static Uri buildChildUri(String childId) {
            return CONTENT_URI.buildUpon().appendPath(childId).build();
        }
        public static String getChildId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**STATUS TABLE**/
    public static class StatusTable implements StatusColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATUS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.status";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.status";

        public static Uri buildStatusUri(String statusId) {
            return CONTENT_URI.buildUpon().appendPath(statusId).build();
        }
        public static String getStatusId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**COMMUNITY TABLE**/
    public static class CommunityTable implements CommunityColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMUNITY).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tiis.community";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tiis.community";

        public static Uri buildCommunityUri(String communityId) {
            return CONTENT_URI.buildUpon().appendPath(communityId).build();
        }
        public static String getCommunityId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**CHILD_WEIGHT TABLE**/
    public static class ChildWeightTable implements ChildWeightColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHILD_WEIGHT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.iis.child_weight";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.child_weight";


        public static Uri buildChildWeightUri(String child_weightId) {
            return CONTENT_URI.buildUpon().appendPath(child_weightId).build();
        }

        public static String getChildWeightId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**NONVACCINATION_REASON TABLE**/
    public static class NonVaccinationReasonTable implements NonVaccinationReasonColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NONVACCINATION_REASON).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.nonvaccination_reason";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.nonvaccination_reason";


        public static Uri buildNonVaccinationReasonUri(String nonVaccinationReasonId) {
            return CONTENT_URI.buildUpon().appendPath(nonVaccinationReasonId).build();
        }

        public static String getNonVaccinationReasonId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**AGE_DEFINITIONS TABLE**/
    public static class AgeDefinitionsTable implements AgeDefinitionsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AGE_DEFINITIONS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.age_definitions";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.age_definitions";


        public static Uri buildAgeDefinitionsUri(String age_definitionsId) {
            return CONTENT_URI.buildUpon().appendPath(age_definitionsId).build();
        }

        public static String getAgeDefinitionsId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**ITEM TABLE**/
    public static class ItemTable implements ItemColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.item";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.item";


        public static Uri buildItemUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static String getItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**SCHEDULE_VACCINATION TABLE**/
    public static class ScheduledVaccinationTable implements ScheduledVaccinationColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULED_VACCINATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.scheduled_vaccination";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.scheduled_vaccination";


        public static Uri buildScheduledVaccinationUri(String scheduled_vaccinationId) {
            return CONTENT_URI.buildUpon().appendPath(scheduled_vaccinationId).build();
        }

        public static String getScheduledVaccinationdId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**DOSE TABLE**/
    public static class DoseTable implements DoseColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DOSE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.dose";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.dose";


        public static Uri buildDoseUri(String doseId) {
            return CONTENT_URI.buildUpon().appendPath(doseId).build();
        }

        public static String getDoseId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**VACCINATION_APPOINTMENT TABLE**/
    public static class VaccinationAppointmentTable implements VaccinationAppointmentColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACCINATION_APPOINTMENT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.vaccination_appointment";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.vaccination_appointment";


        public static Uri buildVaccinationAppointmentUri(String vaccinationAppointmentId) {
            return CONTENT_URI.buildUpon().appendPath(vaccinationAppointmentId).build();
        }

        public static String getVaccinationAppointmentId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**VACCINATION_EVENT TABLE**/
    public static class VaccinationEventTable implements VaccinationEventColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACCINATION_EVENT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.vaccination_event";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.vaccination_event";


        public static Uri buildVaccinationEventUri(String vaccinationAppointmentId) {
            return CONTENT_URI.buildUpon().appendPath(vaccinationAppointmentId).build();
        }

        public static String getVaccinationEventId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**CHILDSUPPLEMENTS TABLE**/
    public static class ChildSupplementsTable implements ChildSupplementsColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHILD_SUPPLEMENTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.child_supplements";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.child_supplements";


        public static Uri buildChildSupplementsUri(String childSupplementsId) {
            return CONTENT_URI.buildUpon().appendPath(childSupplementsId).build();
        }

        public static String getChildSupplementsId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**ITEM_LOT TABLE**/
    public static class ItemLotTable implements ItemLotColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM_LOT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.item_lot";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.item_lot";


        public static Uri buildItemLotUri(String itemLotId) {
            return CONTENT_URI.buildUpon().appendPath(itemLotId).build();
        }

        public static String getItemLotId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**HEALTH_FACILITY_BALANCE TABLE**/
    public static class HealthFacilityBalanceTable implements HealthFacilityBalanceColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HEALTH_FACILITY_BALANCE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.tiis.health_facility_balance";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.tiis.health_facility_balance";


        public static Uri buildHealthFacilityBalanceUri(String healthFacilityBalanceId) {
            return CONTENT_URI.buildUpon().appendPath(healthFacilityBalanceId).build();
        }

        public static String getHealthFacilityBalanceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}

