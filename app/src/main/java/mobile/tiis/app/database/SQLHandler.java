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

package mobile.tiis.app.database;

import android.provider.BaseColumns;


public class SQLHandler {


    public static final String SQLScheduledVaccinationTable =
            "CREATE TABLE " + Tables.SCHEDULED_VACCINATION + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.ScheduledVaccinationColumns.ID + " TEXT,"
                    + GIISContract.ScheduledVaccinationColumns.NAME + " TEXT,"
                    + GIISContract.ScheduledVaccinationColumns.CODE + " TEXT,"
                    + GIISContract.ScheduledVaccinationColumns.ITEM_ID + " TEXT,"
                    + GIISContract.ScheduledVaccinationColumns.ENTRY_DATE + " DATE,"
                    + GIISContract.ScheduledVaccinationColumns.EXIT_DATE + " DATE);";
    public static final String SQLDoseTable =
            "CREATE TABLE " + Tables.DOSE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.DoseColumns.ID + " TEXT,"
                    + GIISContract.DoseColumns.SCHEDULED_VACCINATION_ID + " TEXT,"
                    + GIISContract.DoseColumns.DOSE_NUMBER + " TEXT,"
                    + GIISContract.DoseColumns.FULLNAME + " TEXT,"
                    + DoseColumns.ENTRY_DATE + " DATE,"
                    + GIISContract.DoseColumns.AGE_DEFINITON_ID + " TEXT,"
                    + GIISContract.DoseColumns.FROM_AGE_DEFINITON_ID + " TEXT,"
                    + GIISContract.DoseColumns.TO_AGE_DEFINITON_ID + " TEXT);";

    public static final String SQLVaccinationAppointmentTable =
            "CREATE TABLE " + Tables.VACCINATION_APPOINTMENT + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.VaccinationAppointmentColumns.ID + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.CHILD_ID + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.SCHEDULED_FACILITY_ID + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.SCHEDULED_DATE + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.IS_ACTIVE + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.NOTES + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.AEFI + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.AEFI_DATE + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.MODIFIED_ON + " DATETIME,"
                    + GIISContract.VaccinationAppointmentColumns.MODIFIED_BY + " TEXT,"
                    + GIISContract.VaccinationAppointmentColumns.OUTREACH + " TEXT);";

    public static final String SQLVaccinationEventTable =
            "CREATE TABLE " + Tables.VACCINATION_EVENT + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.VaccinationEventColumns.ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.APPOINTMENT_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.CHILD_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.DOSE_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.VACCINE_LOT_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.HEALTH_FACILITY_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.SCHEDULED_DATE + " TEXT,"
                    + GIISContract.VaccinationEventColumns.VACCINATION_DATE + " TEXT,"
                    + GIISContract.VaccinationEventColumns.VACCINATION_STATUS + " TEXT,"
                    + GIISContract.VaccinationEventColumns.NONVACCINATION_REASON_ID + " TEXT,"
                    + GIISContract.VaccinationEventColumns.IS_ACTIVE + " TEXT,"
                    + GIISContract.VaccinationEventColumns.MODIFIED_ON + " DATETIME,"
                    + GIISContract.VaccinationEventColumns.MODIFIED_BY + " TEXT,"
                    + GIISContract.VaccinationEventColumns.NOTES + " TEXT);";
    public static final String SQLWeightTable =
            "CREATE TABLE " + Tables.WEIGHT + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.WeightColumns.ID + " TEXT,"
                    + GIISContract.WeightColumns.DAY + " TEXT,"
                    + GIISContract.WeightColumns.SD4NEG + " TEXT,"
                    + GIISContract.WeightColumns.SD3NEG + " TEXT,"
                    + GIISContract.WeightColumns.SD2NEG + " TEXT,"
                    + GIISContract.WeightColumns.SD1NEG + " TEXT,"
                    + GIISContract.WeightColumns.SD0 + " TEXT,"
                    + GIISContract.WeightColumns.SD1 + " TEXT,"
                    + GIISContract.WeightColumns.SD2 + " TEXT,"
                    + GIISContract.WeightColumns.SD3 + " TEXT,"
                    + GIISContract.WeightColumns.SD4 + " TEXT,"
                    + GIISContract.WeightColumns.GENDER + " TEXT);";
    public static final String SQLChildSupplements =
            "CREATE TABLE " + Tables.CHILD_SUPPLEMENTS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.ChildSupplementsColumns.ID + " INTEGER,"
                    + GIISContract.ChildSupplementsColumns.CHILD_ID + " TEXT,"
                    + GIISContract.ChildSupplementsColumns.VitA + " TEXT,"
                    + GIISContract.ChildSupplementsColumns.MEBENDEZOLR + " TEXT,"
                    + GIISContract.ChildSupplementsColumns.DATE + " DATE default CURRENT_DATE, "
                    + GIISContract.ChildSupplementsColumns.MODIFIED_ON + " DATE, "
                    + GIISContract.VaccinationEventColumns.MODIFIED_BY + " TEXT);";
    public static final String SQLItemLot =
            "CREATE TABLE " + Tables.ITEM_LOT + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.ItemLotColumns.ID + " INTEGER,"
                    + GIISContract.ItemLotColumns.GTIN + " TEXT,"
                    + GIISContract.ItemLotColumns.NOTES + " TEXT,"
                    + GIISContract.ItemLotColumns.LOT_NUMBER + " TEXT NOT NULL,"
                    + GIISContract.ItemLotColumns.ITEM_ID + " INTEGER NOT NULL,"
                    + GIISContract.ItemLotColumns.EXPIRE_DATE + " DATE); ";
    public static final String SQLHealthFacilityBalance =
            "CREATE TABLE " + Tables.HEALTH_FACILITY_BALANCE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.HealthFacilityBalanceColumns.LOT_ID + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.GTIN + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.LOT_NUMBER + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.ITEM + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.BALANCE + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.EXPIRE_DATE + " DATE,"
                    + HealthFacilityBalanceColumns.GTIN_ISACTIVE + " TEXT,"
                    + HealthFacilityBalanceColumns.LOT_ISACTIVE + " TEXT,"
                    + GIISContract.HealthFacilityBalanceColumns.REORDER_QTY + " INTEGER); ";
    public static final String SQLUIValuesTable =
            "CREATE TABLE " + Tables.UIVALUES + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + UIValuesColumns.LANGUAGE_ID + " INTEGER NOT NULL,"
                    + UIValuesColumns.CODE + " TEXT NOT NULL,"
                    + UIValuesColumns.VALUE + " TEXT NOT NULL,"
                    + UIValuesColumns.SCREEN + " TEXT NOT NULL); ";
    public static final String SQLFillUitValuesTable =
            "INSERT INTO '" + Tables.UIVALUES + "' (" + UIValuesColumns.LANGUAGE_ID + "," + UIValuesColumns.CODE + "," + UIValuesColumns.VALUE + "," + UIValuesColumns.SCREEN + ") " +
                    "SELECT 1 AS '" + UIValuesColumns.LANGUAGE_ID + "','app_name' AS '" + UIValuesColumns.CODE
                    + "','Tanzania Immunization Information System' AS '" + UIValuesColumns.VALUE + "','App' AS '" + UIValuesColumns.SCREEN + "'" +
                    " UNION SELECT 1,'app_picker_name','Applications','App'" +
                    " UNION SELECT 1,'bookmark_picker_name','Bookmarks','App'" +
                    " UNION SELECT 1,'button_add_calendar','Add to calendar','App'" +
                    " UNION SELECT 1,'button_add_contact','Add contact','App'" +
                    " UNION SELECT 1,'button_book_search','Book Search','App'" +
                    " UNION SELECT 1,'button_cancel','Cancel','App'" +
                    " UNION SELECT 1,'button_custom_product_search','Custom search','App'" +
                    " UNION SELECT 1,'button_dial','Dial number','App'" +
                    " UNION SELECT 1,'button_email','Send email','App'" +
                    " UNION SELECT 1,'button_get_directions','Get directions','App'" +
                    " UNION SELECT 1,'button_mms','Send MMS','App'" +
                    " UNION SELECT 1,'button_ok','OK','App'" +
                    " UNION SELECT 1,'button_open_browser','Open browser','App'" +
                    " UNION SELECT 1,'button_product_search','Product search','App'" +
                    " UNION SELECT 1,'button_search_book_contents','Search contents','App'" +
                    " UNION SELECT 1,'button_share_app','Application','App'" +
                    " UNION SELECT 1,'button_share_bookmark','Bookmark','App'" +
                    " UNION SELECT 1,'button_share_by_email','Share via email','App'" +
                    " UNION SELECT 1,'button_share_by_sms','Share via SMS','App'" +
                    " UNION SELECT 1,'button_share_clipboard','Clipboard','App'" +
                    " UNION SELECT 1,'button_share_contact','Contact','App'" +
                    " UNION SELECT 1,'button_show_map','Show map','App'" +
                    " UNION SELECT 1,'button_sms','Send SMS','App'" +
                    " UNION SELECT 1,'button_web_search','Web search','App'" +
                    " UNION SELECT 1,'button_wifi','Connect to Network','App'" +
                    " UNION SELECT 1,'contents_contact','Contact info','App'" +
                    " UNION SELECT 1,'contents_email','Email address','App'" +
                    " UNION SELECT 1,'contents_location','Geographic coordinates','App'" +
                    " UNION SELECT 1,'contents_phone','Phone number','App'" +
                    " UNION SELECT 1,'contents_sms','SMS address','App'" +
                    " UNION SELECT 1,'contents_text','Plain text','App'" +
                    " UNION SELECT 1,'history_clear_text','Clear history','App'" +
                    " UNION SELECT 1,'history_clear_one_history_text','Clear','App'" +
                    " UNION SELECT 1,'history_email_title','Barcode Scanner history','App'" +
                    " UNION SELECT 1,'history_empty','Empty','App'" +
                    " UNION SELECT 1,'history_empty_detail','No barcode scans have been recorded','App'" +
                    " UNION SELECT 1,'history_send','Send history','App'" +
                    " UNION SELECT 1,'history_title','History','App'" +
                    " UNION SELECT 1,'menu_encode_mecard','Use MECARD','App'" +
                    " UNION SELECT 1,'menu_encode_vcard','Use vCard','App'" +
                    " UNION SELECT 1,'menu_help','Help','App'" +
                    " UNION SELECT 1,'menu_history','History','App'" +
                    " UNION SELECT 1,'menu_settings','Settings','App'" +
                    " UNION SELECT 1,'menu_share','Share','App'" +
                    " UNION SELECT 1,'msg_bulk_mode_scanned','Bulk mode: barcode scanned and saved','App'" +
                    " UNION SELECT 1,'msg_camera_framework_bug','Sorry the Android camera encountered a problem. You may need to restart the device.','App'" +
                    " UNION SELECT 1,'msg_default_format','Format','App'" +
                    " UNION SELECT 1,'msg_default_meta','Metadata','App'" +
                    " UNION SELECT 1,'msg_default_mms_subject','Hi','App'" +
                    " UNION SELECT 1,'msg_default_status','Place a barcode inside the viewfinder rectangle to scan it.','App'" +
                    " UNION SELECT 1,'msg_default_time','Time','App'" +
                    " UNION SELECT 1,'msg_default_type','Type','App'" +
                    " UNION SELECT 1,'msg_encode_contents_failed','Could not encode a barcode from the data provided.','App'" +
                    " UNION SELECT 1,'msg_error','Error','App'" +
                    " UNION SELECT 1,'msg_google_books','Google','App'" +
                    " UNION SELECT 1,'msg_google_product','Google','App'" +
                    " UNION SELECT 1,'msg_intent_failed','Sorry the requested application could not be launched. The barcode contents may be invalid.','App'" +
                    " UNION SELECT 1,'msg_invalid_value','Invalid value','App'" +
                    " UNION SELECT 1,'msg_redirect','Redirect','App'" +
                    " UNION SELECT 1,'msg_sbc_book_not_searchable','Sorry this book is not searchable.','App'" +
                    " UNION SELECT 1,'msg_sbc_failed','Sorry the search encountered a problem.','App'" +
                    " UNION SELECT 1,'msg_sbc_no_page_returned','No page returned','App'" +
                    " UNION SELECT 1,'msg_sbc_page','Page','App'" +
                    " UNION SELECT 1,'msg_sbc_results','Results','App'" +
                    " UNION SELECT 1,'msg_sbc_searching_book','Searching book\u2026','App'" +
                    " UNION SELECT 1,'msg_sbc_snippet_unavailable','Snippet not available','App'" +
                    " UNION SELECT 1,'msg_share_explanation','You can share data by displaying a barcode on your screen and scanning it with another phone.','App'" +
                    " UNION SELECT 1,'msg_share_text','Or type some text and press Enter','App'" +
                    " UNION SELECT 1,'msg_sure','Are you sure?','App'" +
                    " UNION SELECT 1,'msg_unmount_usb','Sorry the SD card is not accessible.','App'" +
                    " UNION SELECT 1,'preferences_actions_title','When a barcode is found\u2026','App'" +
                    " UNION SELECT 1,'preferences_auto_focus_title','Use auto focus','App'" +
                    " UNION SELECT 1,'preferences_auto_open_web_title','Open web pages automatically','App'" +
                    " UNION SELECT 1,'preferences_bulk_mode_summary','Scan and save many barcodes continuously','App'" +
                    " UNION SELECT 1,'preferences_bulk_mode_title','Bulk scan mode','App'" +
                    " UNION SELECT 1,'preferences_copy_to_clipboard_title','Copy to clipboard','App'" +
                    " UNION SELECT 1,'preferences_custom_product_search_summary','Substitutions: %s = contents, %f = format, %t = type','App'" +
                    " UNION SELECT 1,'preferences_custom_product_search_title','Custom search URL','App'" +
                    " UNION SELECT 1,'preferences_decode_1D_industrial_title','1D Industrial','App'" +
                    " UNION SELECT 1,'preferences_decode_1D_product_title','1D Product','App'" +
                    " UNION SELECT 1,'preferences_decode_Aztec_title','Aztec','App'" +
                    " UNION SELECT 1,'preferences_decode_Data_Matrix_title','Data Matrix','App'" +
                    " UNION SELECT 1,'preferences_decode_PDF417_title','PDF417 (β)','App'" +
                    " UNION SELECT 1,'preferences_decode_QR_title','QR Codes','App'" +
                    " UNION SELECT 1,'preferences_device_bug_workarounds_title','Device Bug Workarounds','App'" +
                    " UNION SELECT 1,'preferences_disable_barcode_scene_mode_title','No barcode scene mode','App'" +
                    " UNION SELECT 1,'preferences_disable_continuous_focus_summary','Use only standard focus mode','App'" +
                    " UNION SELECT 1,'preferences_disable_continuous_focus_title','No continuous focus','App'" +
                    " UNION SELECT 1,'preferences_disable_exposure_title','No exposure','App'" +
                    " UNION SELECT 1,'preferences_disable_metering_title','No metering','App'" +
                    " UNION SELECT 1,'preferences_front_light_summary','Improves scanning in low light on some phones but may cause glare. Does not work on all phones.','App'" +
                    " UNION SELECT 1,'preferences_front_light_title','Use front light','App'" +
                    " UNION SELECT 1,'preferences_front_light_auto','Automatic','App'" +
                    " UNION SELECT 1,'preferences_front_light_off','Off','App'" +
                    " UNION SELECT 1,'preferences_front_light_on','On','App'" +
                    " UNION SELECT 1,'preferences_general_title','General settings','App'" +
                    " UNION SELECT 1,'preferences_history_summary','Store your scans in History','App'" +
                    " UNION SELECT 1,'preferences_history_title','Add to History','App'" +
                    " UNION SELECT 1,'preferences_invert_scan_title','Invert scan','App'" +
                    " UNION SELECT 1,'preferences_invert_scan_summary','Scan for white barcodes on black background. Not available on some devices.','App'" +
                    " UNION SELECT 1,'preferences_name','Settings','App'" +
                    " UNION SELECT 1,'preferences_orientation_title','No automatic rotation','App'" +
                    " UNION SELECT 1,'preferences_play_beep_title','Beep','App'" +
                    " UNION SELECT 1,'preferences_remember_duplicates_summary','Store multiple scans of the same barcode in History','App'" +
                    " UNION SELECT 1,'preferences_remember_duplicates_title','Remember duplicates','App'" +
                    " UNION SELECT 1,'preferences_result_title','Result settings','App'" +
                    " UNION SELECT 1,'preferences_scanning_title','When scanning for barcodes decode\u2026','App'" +
                    " UNION SELECT 1,'preferences_search_country','Search country','App'" +
                    " UNION SELECT 1,'preferences_supplemental_summary','Try to retrieve more information about the barcode contents','App'" +
                    " UNION SELECT 1,'preferences_supplemental_title','Retrieve more info','App'" +
                    " UNION SELECT 1,'preferences_vibrate_title','Vibrate','App'" +
                    " UNION SELECT 1,'result_address_book','Found contact info','App'" +
                    " UNION SELECT 1,'result_calendar','Found calendar event','App'" +
                    " UNION SELECT 1,'result_email_address','Found email address','App'" +
                    " UNION SELECT 1,'result_geo','Found geographic coordinates','App'" +
                    " UNION SELECT 1,'result_isbn','Found book','App'" +
                    " UNION SELECT 1,'result_product','Found product','App'" +
                    " UNION SELECT 1,'result_sms','Found SMS address','App'" +
                    " UNION SELECT 1,'result_tel','Found phone number','App'" +
                    " UNION SELECT 1,'result_text','Found plain text','App'" +
                    " UNION SELECT 1,'result_uri','Found URL','App'" +
                    " UNION SELECT 1,'result_wifi','Found WLAN Configuration','App'" +
                    " UNION SELECT 1,'sbc_name','Google Book Search','App'" +
                    " UNION SELECT 1,'wifi_changing_network','Requesting connection to network\u2026','App'" +
                    " UNION SELECT 1,'login','Sign in','App'" +
                    " UNION SELECT 1,'username','Username','App'" +
                    " UNION SELECT 1,'password','Password','App'" +
                    " UNION SELECT 1,'enter','Sign in','App'" +
                    " UNION SELECT 1,'online_status','Offline','App'" +
                    " UNION SELECT 1,'button_search','Search','App'" +
                    " UNION SELECT 1,'title_scan','Scan','App'" +
                    " UNION SELECT 1,'title_check_in','Check in','App'" +
                    " UNION SELECT 1,'title_search_child','Search Child','App'" +
                    " UNION SELECT 1,'title_register_child','Register Child','App'" +
                    " UNION SELECT 1,'title_vaccination_queue','Vaccination Queue','App'" +
                    " UNION SELECT 1,'title_view_appointment','View Appointments','App'" +
                    " UNION SELECT 1,'title_monthly_plan','Monthly plan','App'" +
                    " UNION SELECT 1,'title_stock','Stock','App'" +
                    " UNION SELECT 1,'title_vaccinate','Vaccinate','App'" +
                    " UNION SELECT 1,'title_supplements','Supplements','App'" +
                    " UNION SELECT 1,'title_immunization_card','Immunization Card','App'" +
                    " UNION SELECT 1,'title_scan_child','Scan Child','App'" +
                    " UNION SELECT 1,'title_scan_result','Scan Results','App'" +
                    " UNION SELECT 1,'title_view_child','View/Edit Child','App'" +
                    " UNION SELECT 1,'title_child','Child','App'" +
                    " UNION SELECT 1,'title_administer_vaccines','Administer Vaccines','App'" +
                    " UNION SELECT 1,'description_home','Home','App'" +
                    " UNION SELECT 1,'barcode_id','Barcode','App'" +
                    " UNION SELECT 1,'barcode','Barcode','App'" +
                    " UNION SELECT 1,'temp_id','Temp ID','App'" +
                    " UNION SELECT 1,'firstname','Firstname','App'" +
                    " UNION SELECT 1,'surname','Surname','App'" +
                    " UNION SELECT 1,'mother','Mother','App'" +
                    " UNION SELECT 1,'mother_firstname','Mother Firstname','App'" +
                    " UNION SELECT 1,'mother_surname','Mother Surname','App'" +
                    " UNION SELECT 1,'name','Name','App'" +
                    " UNION SELECT 1,'birthdate','Birthdate','App'" +
                    " UNION SELECT 1,'date_of_birth','Date of Birth','App'" +
                    " UNION SELECT 1,'dob_from','DOB from','App'" +
                    " UNION SELECT 1,'dob_to','DOB to','App'" +
                    " UNION SELECT 1,'place_of_birth','Place of Birth','App'" +
                    " UNION SELECT 1,'village_domicile','Village/Domicile','App'" +
                    " UNION SELECT 1,'place_of_birth_prompt','Select Place of Birth','App'" +
                    " UNION SELECT 1,'village_domicile_prompt','Select Village/Domicile','App'" +
                    " UNION SELECT 1,'health_facility','Health Facility','App'" +
                    " UNION SELECT 1,'status','Status','App'" +
                    " UNION SELECT 1,'health_facility_prompt','Select Health Facility','App'" +
                    " UNION SELECT 1,'status_prompt','Select Status','App'" +
                    " UNION SELECT 1,'title_bar_search_child','Search Child','App'" +
                    " UNION SELECT 1,'choose_date','Select date','App'" +
                    " UNION SELECT 1,'log_in_txt','LOG IN TO YOUR ACCOUNT','App'" +
                    " UNION SELECT 1,'gender','Gender','App'" +
                    " UNION SELECT 1,'radio_male','M','App'" +
                    " UNION SELECT 1,'radio_female','F','App'" +
                    " UNION SELECT 1,'phone','Phone','App'" +
                    " UNION SELECT 1,'notes','Notes','App'" +
                    " UNION SELECT 1,'search','Search','App'" +
                    " UNION SELECT 1,'btn_scan','Scan','App'" +
                    " UNION SELECT 1,'btn_save','Save','App'" +
                    " UNION SELECT 1,'or','Or','App'" +
                    " UNION SELECT 1,'type_barcode_number','Type barcode number','App'" +
                    " UNION SELECT 1,'weight','Weight','App'" +
                    " UNION SELECT 1,'supplements','Supplements','App'" +
                    " UNION SELECT 1,'input','Input','App'" +
                    " UNION SELECT 1,'modify_record','Modify Record','App'" +
                    " UNION SELECT 1,'vaccinate','Vaccinate','App'" +
                    " UNION SELECT 1,'date','Date','App'" +
                    " UNION SELECT 1,'back','Back','App'" +
                    " UNION SELECT 1,'child','Child','App'" +
                    " UNION SELECT 1,'child_name','Name','App'" +
                    " UNION SELECT 1,'vaccine_dose','Vaccine Doses','App'" +
                    " UNION SELECT 1,'schedule','Schedule','App'" +
                    " UNION SELECT 1,'scheduled_date','Scheduled Date','App'" +
                    " UNION SELECT 1,'vaccines','Vaccines','App'" +
                    " UNION SELECT 1,'previous','Previous','App'" +
                    " UNION SELECT 1,'next','Next','App'" +
                    " UNION SELECT 1,'month','Month','App'" +
                    " UNION SELECT 1,'age','Age','App'" +
                    " UNION SELECT 1,'today_date','Today Date','App'" +
                    " UNION SELECT 1,'immunization_card','Immunization Card','App'" +
                    " UNION SELECT 1,'aefi','Aefi','App'" +
                    " UNION SELECT 1,'aefi_date','Aefi Date','App'" +
                    " UNION SELECT 1,'dose','Dose','App'" +
                    " UNION SELECT 1,'search_again','Search Again','App'" +
                    " UNION SELECT 1,'there_are_no_children','There are no children that match search criteria!','App'" +
                    " UNION SELECT 1,'btn_register','Register Child','App'" +
                    " UNION SELECT 1,'alert_empty_fields','Please check your data','App'" +
                    " UNION SELECT 1,'empty_barcode','The barcode field can not be empty','App'" +
                    " UNION SELECT 1,'change_barcode_scan_when_exists_dialog','This child already has a barcode are you sure you want to assign another one?','App'" +
                    " UNION SELECT 1,'empty_child_id','The child Id field can not be empty','App'" +
                    " UNION SELECT 1,'empty_names','The last/first name can not be empty','App'" +
                    " UNION SELECT 1,'empty_mother_names','The last/first mother name can not be empty','App'" +
                    " UNION SELECT 1,'future_birth_date','Selected birthdate is incorrect','App'" +
                    " UNION SELECT 1,'empty_birthplace','The birth place can not be empty','App'" +
                    " UNION SELECT 1,'empty_village','Please select domicile / village','App'" +
                    " UNION SELECT 1,'empty_healthfacility','Please select health facility','App'" +
                    " UNION SELECT 1,'empty_status','Please select status','App'" +
                    " UNION SELECT 1,'child_change_data_saved_success','Changes saved!','App'" +
                    " UNION SELECT 1,'child_change_data_saved_error','Changes not saved!','App'" +
                    " UNION SELECT 1,'btn_clear','Clear','App'" +
                    " UNION SELECT 1,'vaccine_lot','Vaccine Lot','App'" +
                    " UNION SELECT 1,'vaccination_date','Vaccination Date','App'" +
                    " UNION SELECT 1,'done','Done','App'" +
                    " UNION SELECT 1,'reason','Reason \nif not done','App'" +
                    " UNION SELECT 1,'modify','Modify','App'" +
                    " UNION SELECT 1,'empty_phone','The phone number can not be empty','App'" +
                    " UNION SELECT 1,'empty_notes','The notes can not be empty','App'" +
                    " UNION SELECT 1,'not_found','Not Found','App'" +
                    " UNION SELECT 1,'barcode_does_not_exist','This barcode doesnt exist locally or on server.','App'" +
                    " UNION SELECT 1,'assign_barcode_first','Please assign a barcode to this child first!','App'" +
                    " UNION SELECT 1,'error_retrieving_child_data','Error retrieving child data.','App'" +
                    " UNION SELECT 1,'txt_vitamina_a','Vit A','App'" +
                    " UNION SELECT 1,'txt_mebend_a','Mebendezol','App'" +
                    " UNION SELECT 1,'chk_yes','Yes','App'" +
                    " UNION SELECT 1,'select_one_supplement','Please select at least one supplement that was given!','App'" +
                    " UNION SELECT 1,'supplement_data_saved','The supplement data was saved!','App'" +
                    " UNION SELECT 1,'txt_vaccine_dose','Vaccine Dose','App'" +
                    " UNION SELECT 1,'txt_vaccine_lot','Vaccine Lot','App'" +
                    " UNION SELECT 1,'txt_health_center','Health Center','App'" +
                    " UNION SELECT 1,'txt_vaccination_date','Vaccination Date','App'" +
                    " UNION SELECT 1,'txt_done','Done','App'" +
                    " UNION SELECT 1,'txt_non_vaccination_reason','Non Vaccination Reason','App'" +
                    " UNION SELECT 1,'please_enter_barcode','Please enter barcode','App'" +
                    " UNION SELECT 1,'please_enter_firstname','Please enter firstname','App'" +
                    " UNION SELECT 1,'please_enter_surname','Please enter surname','App'" +
                    " UNION SELECT 1,'please_enter_birthdate','Please enter birthdate','App'" +
                    " UNION SELECT 1,'please_enter_mother_firstname','Please enter mother firstname','App'" +
                    " UNION SELECT 1,'please_enter_mother_surname','Please enter mother surname','App'" +
                    " UNION SELECT 1,'please_select_place_of_birth','Please select place of birth','App'" +
                    " UNION SELECT 1,'please_select_village_domicile','Please select Village/Domicile','App'" +
                    " UNION SELECT 1,'same_barcode','Please choose another barcode','App'" +
                    " UNION SELECT 1,'chk_child_had_aefi','Child had AEFI on this encounter','App'" +
                    " UNION SELECT 1,'vaccine_names_quantity','Vaccine Quantities','App'" +
                    " UNION SELECT 1,'back_to_queue','Back','App'" +
                    " UNION SELECT 1,'quantity','Quantity','App'" +
                    " UNION SELECT 1,'child_weight_already_entered','Weight for this child has already been entered are you sure you want to override it ','App';";
    public static final String SQLMonthlyPlanView =
            " CREATE VIEW " + Views.MONTHLY_PLAN + " AS  " +
                    " SELECT v._ID, v.APPOINTMENT_ID AS APPOINTMENT_ID,   c.ID AS CHILD_ID, c.FIRSTNAME1 || c.LASTNAME1 AS NAME, a.NAME AS SCHEDULE,   v.SCHEDULED_DATE AS SCHEDULED_DATE ," +
                    " PLACE.NAME AS DOMICILE,  v.HEALTH_FACILITY_ID AS HEALTH_FACILITY_ID , DOSE.SCHEDULED_VACCINATION_ID AS SCHEDULED_VACCINATION_ID,  c.DOMICILE_ID AS DOMICILE_ID,  a.ID AS SCHEDULE_ID, DOSE.ID as DOSE_ID" +
                    " FROM CHILD c JOIN PLACE ON c.DOMICILE_ID = PLACE.ID   JOIN VACCINATION_EVENT v ON c.ID = v.CHILD_ID " +
                    " JOIN DOSE ON v.DOSE_ID = DOSE.ID  JOIN AGE_DEFINITIONS a ON DOSE.AGE_DEFINITON_ID = a.ID" +
                    " WHERE c.STATUS_ID = 1 AND  v.IS_ACTIVE = 'true' AND v.VACCINATION_STATUS = 'false' AND  (v.NONVACCINATION_REASON_ID=0  OR v.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true'));";
    public static final String SQLVaccinationQueueTable =
            "CREATE TABLE " + Tables.VACCINATION_QUEUE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + VaccinationQueueColumns.CHILD_ID + " TEXT,"
                    + VaccinationQueueColumns.DATE + " TEXT);";
    public static final String SQLBirthplaceTable =
            "CREATE TABLE " + Tables.BIRTHPLACE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PlaceColumns.ID + " TEXT,"
                    + PlaceColumns.NAME + " TEXT);";
    public static final String SQLAdjustmentTable =
            "CREATE TABLE " + Tables.ADJUSTMENT_REASONS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + AdjustmentColumns.ID + " TEXT,"
                    + AdjustmentColumns.NAME + " TEXT,"
                    + AdjustmentColumns.POSITIVE + " TEXT,"
                    + AdjustmentColumns.IS_ACTIVE + " TEXT);";
        public static final String SQLConfigTable =
                "CREATE TABLE " + Tables.CONFIG + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + ConfigColumns.NAME + " TEXT,"
                        + ConfigColumns.VALUE + " TEXT);";
    public static final String SQLVaccinations =
            "SELECT * FROM (SELECT v.APPOINTMENT_ID, " +
                    "(SELECT GROUP_CONCAT(dose.FULLNAME)" +
                    " FROM vaccination_event INNER JOIN dose" +
                    " ON vaccination_event.DOSE_ID = dose.ID left join age_definitions on dose.TO_AGE_DEFINITON_ID = age_definitions.ID" +
                    " WHERE CHILD_ID=?" +
                    " AND v.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID" +
                    " AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+60 days')" +
                    " AND vaccination_event.IS_ACTIVE='true'" +
                    " AND vaccination_event.VACCINATION_STATUS='false'" +
                    " AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) AND (DAYS IS NULL or " +
                    "(datetime(substr(vaccination_event.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || DAYS || ' days' )) ))" +
                    " AS VACCINES, " +
                    "a.NAME AS SCHEDULE, " +
                    "v.SCHEDULED_DATE  " +
                    "FROM vaccination_event v INNER JOIN dose " +
                    "ON v.DOSE_ID = dose.ID INNER JOIN age_definitions a" +
                    " ON dose.AGE_DEFINITON_ID=a.ID" +
                    " WHERE v.CHILD_ID =?" +
                    " AND datetime(substr(v.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+60 days')" +
                    " AND v.IS_ACTIVE='true'" +
                    " AND v.VACCINATION_STATUS='false'" +
                    " AND (v.NONVACCINATION_REASON_ID=0  OR v.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) " +
                    " AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                    " OR (datetime(substr(v.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                    "GROUP BY v.APPOINTMENT_ID, v.SCHEDULED_DATE, a.NAME " +
                    "ORDER BY v.SCHEDULED_DATE)" +
                    "GROUP BY APPOINTMENT_ID " +
                    "ORDER BY SCHEDULED_DATE";
    public static final String SQLPostmanTable =
            "CREATE TABLE " + Tables.POSTMAN + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PostmanColumns.URL + " TEXT,"
                    + PostmanColumns.RESPONSE_TYPE_ID + " INTEGER,"
                    + PostmanColumns.TEMPORARY_ID + " TEXT,"
                    + " FOREIGN KEY ("+PostmanColumns.RESPONSE_TYPE_ID+") REFERENCES "+Tables.RESPONSE_TYPE+" ("+BaseColumns._ID+"));";
    public static final String SQLResponseTypeTable =
            "CREATE TABLE " + Tables.RESPONSE_TYPE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ResponseTypeColumns.RESPONSE_DESCRIPTION + " TEXT )";
    public static final String SQLFillResponseTypeTable = "INSERT INTO '" + Tables.RESPONSE_TYPE + "' " +
            " SELECT '1' AS '" + ResponseTypeColumns.RESPONSE_DESCRIPTION + "', 'Not needed to manage the return in any special way' AS '" + ResponseTypeColumns.RESPONSE_DESCRIPTION + "' " +
            " UNION SELECT '2', 'something has to be done' " +
            " UNION SELECT '3', 'Registration call that returns ID of child and that needs to replace the temporary id' ";
    public static final String ApplicationStateTable =
            "CREATE TABLE " + Tables.APP_STATE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "main_sync_needed" + " INTEGER NOT NULL);";
    public static final String SQLHealthFacilityTable =
            "CREATE TABLE " + Tables.HEALTH_FACILITY + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATETIME, "
                    + GIISContract.HealthFacilityColumns.ID + " TEXT,"
                    + GIISContract.HealthFacilityColumns.NAME + " TEXT,"
                    + GIISContract.HealthFacilityColumns.CODE + " TEXT,"
                    + GIISContract.HealthFacilityColumns.PARENT_ID + " TEXT,"
                    + GIISContract.HealthFacilityColumns.MODIFIED_ON + " DATETIME);";
    public static final String SQLPlaceTable =
            "CREATE TABLE " + Tables.PLACE + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.PlaceColumns.ID + " TEXT,"
                    + GIISContract.PlaceColumns.NAME + " TEXT,"
                    + GIISContract.PlaceColumns.PARENT_ID + " TEXT,"
                    + GIISContract.PlaceColumns.CODE + " TEXT,"
                    + GIISContract.PlaceColumns.MODIFIED_ON + " DATETIME, "
                    + GIISContract.PlaceColumns.HEALTH_FACILITY_ID + " TEXT);";

    public static final String SQLUserTable =
            "CREATE TABLE " + Tables.USER + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.UserColumns.ID + " TEXT,"
                    + GIISContract.UserColumns.USERNAME + " TEXT,"
                    + GIISContract.UserColumns.PASSWORD + " TEXT,"
                    + GIISContract.UserColumns.FIRSTNAME + " TEXT,"
                    + GIISContract.UserColumns.LASTNAME + " TEXT,"
                    + GIISContract.UserColumns.IS_ACTIVE + " TEXT,"
                    + GIISContract.UserColumns.DELETED + " TEXT,"
                    + GIISContract.UserColumns.NOTES + " TEXT,"
                    + GIISContract.UserColumns.EMAIL + " TEXT,"
                    + GIISContract.UserColumns.ISLOGGEDIN + " TEXT,"
                    + GIISContract.UserColumns.LASTLOGIN + " DATETIME, "
                    + GIISContract.UserColumns.USER_ROLE_ID + " TEXT,"
                    + GIISContract.UserColumns.HEALTH_FACILITY_ID + " TEXT);";

    public static final String SQLChildTable =
            "CREATE TABLE " + Tables.CHILD + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.ChildColumns.ID + " TEXT,"
                    + GIISContract.ChildColumns.SYSTEM_ID + " TEXT,"
                    + GIISContract.ChildColumns.BARCODE_ID + " TEXT,"
                    + GIISContract.ChildColumns.TEMP_ID + " TEXT,"
                    + GIISContract.ChildColumns.FIRSTNAME1 + " TEXT,"
                    + GIISContract.ChildColumns.FIRSTNAME2 + " TEXT,"
                    + GIISContract.ChildColumns.LASTNAME1 + " TEXT,"
                    + GIISContract.ChildColumns.BIRTHDATE + " DATETIME,"
                    + GIISContract.ChildColumns.GENDER + " TEXT,"
                    + GIISContract.ChildColumns.BIRTHPLACE_ID + " TEXT,"
                    + GIISContract.ChildColumns.BIRTHPLACE + " TEXT,"
                    + GIISContract.ChildColumns.COMMUNITY_ID + " TEXT,"
                    + GIISContract.ChildColumns.DOMICILE_ID + " TEXT,"
                    + GIISContract.ChildColumns.DOMICILE + " TEXT,"
                    + GIISContract.ChildColumns.HEALTH_FACILITY + " TEXT,"
                    + GIISContract.ChildColumns.STATUS + " TEXT,"
                    + GIISContract.ChildColumns.ADDRESS + " TEXT,"
                    + GIISContract.ChildColumns.PHONE + " TEXT,"
                    + GIISContract.ChildColumns.MOBILE + " TEXT,"
                    + GIISContract.ChildColumns.MOTHER_FIRSTNAME + " TEXT,"
                    + GIISContract.ChildColumns.MOTHER_LASTNAME + " TEXT, "
                    + GIISContract.ChildColumns.MOTHER_ID + " TEXT,"
                    + GIISContract.ChildColumns.NOTES + " TEXT,"
                    + GIISContract.ChildColumns.MODIFIED_ON + " TEXT,"
                    + GIISContract.ChildColumns.MODIFIED_BY + " TEXT,"
                    + GIISContract.ChildColumns.STATUS_ID + " TEXT,"
                    + GIISContract.ChildColumns.MOTHER_VVU_STS + " TEXT,"
                    + GIISContract.ChildColumns.MOTHER_TT2_STS + " TEXT,"
                    + GIISContract.ChildColumns.CUMULATIVE_SERIAL_NUMBER + " TEXT,"
                    + GIISContract.ChildColumns.CHILD_REGISTRY_YEAR + " TEXT,"
                    + GIISContract.ChildColumns.HEALTH_FACILITY_ID + " TEXT);";
    public static final String SQLStatusTable =
            "CREATE TABLE " + Tables.STATUS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.StatusColumns.ID + " TEXT,"
                    + GIISContract.StatusColumns.CODE + " TEXT,"
                    + GIISContract.StatusColumns.PARENT_ID + " TEXT,"
                    + GIISContract.StatusColumns.NAME + " TEXT);";
    public static final String SQLCommunityTable =
            "CREATE TABLE " + Tables.COMMUNITY + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.CommunityColumns.ID + " TEXT,"
                    + GIISContract.CommunityColumns.NAME + " TEXT);";
    public static final String SQLChildWeightTable =
            "CREATE TABLE " + Tables.CHILD_WEIGHT + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.ChildWeightColumns.ID + " TEXT,"
                    + GIISContract.ChildWeightColumns.CHILD_ID + " TEXT,"
                    + GIISContract.ChildWeightColumns.CHILD_BARCODE + " TEXT,"
                    + GIISContract.ChildWeightColumns.WEIGHT + " TEXT,"
                    + GIISContract.ChildWeightColumns.DATE + " DATE,"
                    + GIISContract.ChildWeightColumns.NOTES + " TEXT,"
                    + GIISContract.ChildWeightColumns.MODIFIED_ON + " DATETIME, "
                    + GIISContract.ChildWeightColumns.MODIFIED_BY + " TEXT);";
    public static final String SQLNonvaccinationReasonTable =
            "CREATE TABLE " + Tables.NONVACCINATION_REASON + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.NonVaccinationReasonColumns.KEEP_CHILD_DUE + " TEXT, "
                    + GIISContract.NonVaccinationReasonColumns.ID + " TEXT,"
                    + GIISContract.NonVaccinationReasonColumns.NAME + " TEXT);";
    public static final String SQLAgeDefinitionsTable =
            "CREATE TABLE " + Tables.AGE_DEFINITIONS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.AgeDefinitionsColumns.ID + " TEXT,"

                    + GIISContract.AgeDefinitionsColumns.NAME + " TEXT,"
                    + GIISContract.AgeDefinitionsColumns.DAYS + " TEXT);";
    public static final String SQLItemTable =
            "CREATE TABLE " + Tables.ITEM + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
                    + GIISContract.ItemColumns.ID + " TEXT,"
                    + GIISContract.ItemColumns.ITEM_CATEGORY_ID + " TEXT,"
                    + GIISContract.ItemColumns.NAME + " TEXT,"
                    + GIISContract.ItemColumns.CODE + " TEXT,"
                    + GIISContract.ItemColumns.ENTRY_DATE + " DATE,"
                    + GIISContract.ItemColumns.EXIT_DATE + " DATE);";

    public static final String NoTimeConstraintVaccines =
            "SELECT * FROM (" +
                    "SELECT v.APPOINTMENT_ID, " +
                    "(SELECT GROUP_CONCAT(dose.FULLNAME) " +
                    "FROM vaccination_event INNER JOIN dose " +
                    "ON vaccination_event.DOSE_ID = dose.ID " +
                    "WHERE CHILD_ID=? " +
                    "AND v.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID " +
                    "AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+60 days') " +
                    "AND vaccination_event.IS_ACTIVE='true' " +
                    "AND vaccination_event.VACCINATION_STATUS='false' " +
                    "AND vaccination_event.NONVACCINATION_REASON_ID=0 ) " +
                    "AS VACCINES, " +
                    "v.SCHEDULED_DATE " +
                    "FROM vaccination_event v INNER JOIN dose " +
                    "ON v.DOSE_ID = dose.ID " +
                    "WHERE v.CHILD_ID =? " +
                    "AND datetime(substr(v.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+60 days') " +
                    "AND v.IS_ACTIVE='true' " +
                    "AND v.VACCINATION_STATUS='false' " +
                    "AND v.NONVACCINATION_REASON_ID=0 " +
                    "GROUP BY v.APPOINTMENT_ID, v.SCHEDULED_DATE " +
                    "ORDER BY v.SCHEDULED_DATE) " +
                    "GROUP BY APPOINTMENT_ID";

    public interface SyncColumns {
        String UPDATED = "updated";
        String OWNERS_USERNAME = "owners_username";
        String MODIFIED_AT = "modfied_at";
    }

    public interface HealthFacilityColumns {
        String ID = "ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String PARENT_ID = "PARENT_ID";
        String MODIFIED_ON = "MODIFIED_ON";
    }

    public interface PlaceColumns {
        String ID = "ID";
        String NAME = "NAME";
        String PARENT_ID = "PARENT_ID";
        String CODE = "CODE";
        String MODIFIED_ON = "MODIFIED_ON";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }
        public interface AdjustmentColumns {
                String ID = "ID";
                String NAME = "NAME";
                String POSITIVE = "POSITIVE";
                String IS_ACTIVE = "IS_ACTIVE";
        }
    public interface ConfigColumns {
        String NAME = "NAME";
        String VALUE = "VALUE";
    }

    public interface CummulativeSerialNumberColumns{
        String ID = "ID";
        String YEAR = "YEAR";
        String MONTH = "MONTH";
        String CUMMULATIVE_SERIAL_NUMBER = "CUMMULATIVE_SERIAL_NUMBER";
        String CHILD_ID = "CHILD_ID";
        String MODIFIED_ON = "MODIFIED_ON";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }

    public interface UserColumns {
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

    public interface ChildColumns {
        String ID = "ID";
        String SYSTEM_ID = "SYSTEM_ID";
        String BARCODE_ID = "BARCODE_ID";
        String TEMP_ID = "TEMP_ID";
        String HEALTH_FACILITY = "HEALTH_FACILITY";
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
        String MOTHER_VVU_STS = "MOTHER_VVU_STS";
        String MOTHER_TT2_STS = "MOTHER_TT2_STS";
        String CUMULATIVE_SERIAL_NUMBER = "CUMULATIVE_SERIAL_NUMBER";
        String CHILD_REGISTRY_YEAR = "CHILD_REGISTRY_YEAR";
        String HEALTH_FACILITY_ID = "HEALTH_FACILITY_ID";
    }

    public interface StatusColumns {
        String ID = "ID";
        String NAME = "NAME";
    }

    public interface CommunityColumns {
        String ID = "ID";
        String NAME = "NAME";
    }

    public interface ChildWeightColumns {
        String ID = "ID";
        String CHILD_ID = "CHILD_ID";
        String CHILD_BARCODE = "CHILD_BARCODE";
        String WEIGHT = "WEIGHT";
        String DATE = "DATE";
        String NOTES = "NOTES";
        String MODIFIED_ON = "MODIFIED_ON";
        String MODIFIED_BY = "MODIFIED_BY";
    }

    public interface NonVaccinationReasonColumns {
        String ID = "ID";
        String NAME = "NAME";
        String KEEP_CHILD_DUE = "KEEP_CHILD_DUE";
    }

    public interface AgeDefinitionsColumns {
        String ID = "ID";
        String NAME = "NAME";
        String DAYS = "DAYS";
    }

    public interface ItemColumns {
        String ID = "ID";
        String ITEM_CATEGORY_ID = "ITEM_CATEGORY_ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String ENTRY_DATE = "ENTRY_DATE";
        String EXIT_DATE = "EXIT_DATE";
    }

    public interface ScheduledVaccinationColumns {
        String ID = "ID";
        String NAME = "NAME";
        String CODE = "CODE";
        String ITEM_ID = "ITEM_ID";
        String ENTRY_DATE = "ENTRY_DATE";
        String EXIT_DATE = "EXIT_DATE";
    }

    public interface DoseColumns {
        String ID = "ID";
        String SCHEDULED_VACCINATION_ID = "SCHEDULED_VACCINATION_ID";
        String DOSE_NUMBER = "DOSE_NUMBER";
        String FULLNAME = "FULLNAME";
        String AGE_DEFINITON_ID = "AGE_DEFINITON_ID";
        String FROM_AGE_DEFINITON_ID = "FROM_AGE_DEFINITON_ID";
        String TO_AGE_DEFINITON_ID = "TO_AGE_DEFINITON_ID";
        String ENTRY_DATE = "ENTRY_DATE";
    }

    public interface VaccinationAppointmentColumns {
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

//    public static final String SQLScheduledVaccinationTable =
//            "CREATE TABLE " + Tables.SCHEDULED_VACCINATION + " ("
//                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + GIISContract.SyncColumns.UPDATED + " INTEGER NOT NULL,"
//                    + GIISContract.SyncColumns.OWNERS_USERNAME + " TEXT,"
//                    + GIISContract.SyncColumns.MODIFIED_AT + " DATE, "
//                    + GIISContract.ScheduledVaccinationColumns.ID + " TEXT,"
//                    + GIISContract.ScheduledVaccinationColumns.NAME + " TEXT,"
//                    + GIISContract.ScheduledVaccinationColumns.CODE + " TEXT,"
//                    + GIISContract.ScheduledVaccinationColumns.ITEM_ID + " TEXT,"
//                    + GIISContract.ScheduledVaccinationColumns.ENTRY_DATE + " DATE,"
//                    + GIISContract.ScheduledVaccinationColumns.EXIT_DATE + " DATE);";

    public interface VaccinationEventColumns {
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

    public interface WeightColumns {
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

    public interface VaccinationQueueColumns {
        String CHILD_ID = "CHILD_ID";
        String DATE = "DATE";
    }

    public interface PostmanColumns {
        String URL = "url";
        String RESPONSE_TYPE_ID = "response_type_id";
        String TEMPORARY_ID = "temporary_id";
    }

    public interface ResponseTypeColumns {
        String RESPONSE_DESCRIPTION = "response_description";
    }

    public interface ChildSupplementsColumns {
        String ID = "id";
        String CHILD_ID = "child_id";
        String VitA = "VitA";
        String MEBENDEZOLR = "Mebendezol";
        String DATE = "date";
        String MODIFIED_ON = "modified_on";
        String MODIFIED_BY = "modified_by";

    }

    public interface ItemLotColumns {
        String GTIN = "gtin";
        String LOT_NUMBER = "lot_number";
        String ITEM_ID = "item_id";
        String EXPIRE_DATE = "expire_date";
        String NOTES = "notes";
        String ID = "id";
    }

    public interface HealthFacilityBalanceColumns {
        String LOT_ID = "lot_id";
        String GTIN = "gtin";
        String LOT_NUMBER = "lot_number";
        String ITEM = "item";
        String EXPIRE_DATE = "expire_date";
        String BALANCE = "balance";
        String REORDER_QTY = "ReorderQty";
        String GTIN_ISACTIVE = "GtinIsActive";
        String LOT_ISACTIVE = "LotIsActive";
    }

    public interface UIValuesColumns {
        String LANGUAGE_ID = "LANGUAGE_ID";
        String CODE = "CODE";
        String VALUE = "VALUE";
        String SCREEN = "SCREEN";
    }


    public interface Tables {
        String APP_STATE = "app_state";
        String HEALTH_FACILITY = "health_facility";
        String PLACE = "place";
        String USER = "user";
        String CHILD = "child";
        String STATUS = "status";
        String UIVALUES = "uivalues";
        String COMMUNITY = "community";
        String WEIGHT = "weight";
        String CHILD_WEIGHT = "child_weight";
        String NONVACCINATION_REASON = "nonvaccination_reason";
        String AGE_DEFINITIONS = "age_definitions";
        String ITEM = "item";
        String SCHEDULED_VACCINATION = "scheduled_vaccination";
        String DOSE = "dose";
        String VACCINATION_APPOINTMENT = "vaccination_appointment";
        String VACCINATION_EVENT = "vaccination_event";
        String VACCINATION_QUEUE = "vaccination_queue";
        String POSTMAN = "postman";
        String RESPONSE_TYPE = "response_type";
        String CHILD_SUPPLEMENTS = "child_supplements";
        String ITEM_LOT = "item_lot";
        String HEALTH_FACILITY_BALANCE = "health_facility_balance";
        String BIRTHPLACE = "birthplace";
        String CONFIG = "config";
        String ADJUSTMENT_REASONS = "Adjustment_Reasons";
    }

    public interface Views {
        String MONTHLY_PLAN = "monthly_plan";
        String CHILD_REGISTRY = "child_registry";
    }
}
