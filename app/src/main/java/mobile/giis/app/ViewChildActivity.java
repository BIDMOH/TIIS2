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

package mobile.giis.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.Birthplace;
import mobile.giis.app.entity.Child;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.entity.Place;
import mobile.giis.app.entity.Status;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.util.ViewAppointmentRow;

/**
 * Created by Teodor on 2/25/2015.
 */
public class ViewChildActivity extends BackboneActivity implements View.OnClickListener {

    TableLayout layout;
    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView) findViewById(R.id.home_wifi_icon);
            if (Utils.isOnline(context)) {
                wifi_logo.setBackgroundColor(0xff00ff00);
                app.setOnlineStatus(true);
            } else {
                wifi_logo.setBackgroundColor(0xffff0000);
                app.setOnlineStatus(false);
            }
        }
    };
    private EditText temp_id,
            firstname, lastname,
            motherFirstname, motherLastname,
            phone, notes, barcode,etFirstname2;
    private RadioGroup gender;
    private RadioButton male, female;
    private ImageButton btnHome;
    private Spinner birthplace, village,
            healthFacility, status;
    private Button scan, save, birthdate, weight, aefi, immunization_card;
    private LinearLayout linTempId;
    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;
    private TextView firstname_str, gender_str, birthdate_str, motherFirstname_str, tvTemp;
    private ArrayList<ViewAppointmentRow> var;
    private Date bdate;
    private Child currentChild;
    private DatabaseHandler mydb;
    private List<Place> placeList;
    private List<Birthplace> birthplaceList;
    private List<HealthFacility> healthFacilityList;
    private List<Status> statusList;
    private BackboneApplication app;
    private Thread thread;
    private long birthDatesDiff = 0; // variable we need to find the diff of time between the birth dates we change
    private String localBarcode = "";
    //Strings befor changes
    private String tempIdOrig, firstnameOrig, lastnameOrig, birthdateOrig, motherFirOrig, motherLastOrig, phoneOrig, notesOrig, barcodeOrig,firstname2Orig;
    private int birthplaceOrig, villageOrig, healthFacOrig, statusOrig, genderOrig;
    private int notApplicablePos = -1;


    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.view_child_activity);

        app = (BackboneApplication) getApplication();

        decideShowBackBtnOrNot();

        barcode = (EditText) findViewById(R.id.view_child_barcode_id);
        temp_id = (EditText) findViewById(R.id.view_child_temp_id);

        weight = (Button) findViewById(R.id.view_child_weight_btn);
        aefi = (Button) findViewById(R.id.view_child_aefi_btn);
        immunization_card = (Button) findViewById(R.id.view_child_immunization_card_btn);

        btnHome = (ImageButton) findViewById(R.id.img_btn_home_view_child);
        btnHome.setOnClickListener(this);

        firstname = (EditText) findViewById(R.id.view_child_child_firstname);
        lastname = (EditText) findViewById(R.id.view_child_child_lastname);

        etFirstname2 = (EditText)findViewById(R.id.firstname2);

        motherFirstname = (EditText) findViewById(R.id.view_child_mother_firstname);
        motherLastname = (EditText) findViewById(R.id.view_child_mother_lastname);

        birthdate = (Button) findViewById(R.id.view_child_dob);
        gender = (RadioGroup) findViewById(R.id.view_child_gender);
        male = (RadioButton) findViewById(R.id.male_radio);
        female = (RadioButton) findViewById(R.id.female_radio);
        birthplace = (Spinner) findViewById(R.id.view_child_birthplace);
        village = (Spinner) findViewById(R.id.view_child_village);

        healthFacility = (Spinner) findViewById(R.id.view_child_health_facility);
        status = (Spinner) findViewById(R.id.view_child_status);

        phone = (EditText) findViewById(R.id.view_child_phone);
        notes = (EditText) findViewById(R.id.view_child_notes);

        scan = (Button) findViewById(R.id.home_btn_scan);
        save = (Button) findViewById(R.id.view_child_save_button);

        linTempId = (LinearLayout) findViewById(R.id.lin_tempId);
        tvTemp = (TextView) findViewById(R.id.textTemp);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mydb = app.getDatabaseInstance();

        Bundle extras = getIntent().getExtras();
        String value = "";

        if (extras != null) {
            value = extras.getString(BackboneApplication.CHILD_ID);
            // kur nuk na vjen child id
            if (value == null || value.equalsIgnoreCase("")) {
                if (extras.getString("barcode") != null) {
                    value = extras.getString("barcode");
                    Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                            new String[]{String.valueOf(value)});
                    if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                        getChildIdCursor.moveToFirst();
                        value = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
                    } else {
                        toastMessage(getString(R.string.empty_child_id));
                        finish();
                    }
                } else {
                    toastMessage(getString(R.string.empty_barcode));
                    finish();
                }
            }
        }

        BackboneApplication app = (BackboneApplication) getApplication();

        Cursor cursor = null;
        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                new String[]{String.valueOf(value)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            //parsing the data in a value object from the cursor
            currentChild = getChildFromCursror(cursor);

            if (currentChild.getBarcodeID() == null || currentChild.getBarcodeID().isEmpty()) {
                toastMessage(getString(R.string.empty_barcode));
            }

            localBarcode = currentChild.getBarcodeID();
            barcode.setText(currentChild.getBarcodeID());
            barcodeOrig = currentChild.getBarcodeID();

            if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().equalsIgnoreCase("")) {
                linTempId.setVisibility(View.GONE);
                tvTemp.setVisibility(View.GONE);
            } else {
                barcode.setEnabled(true);
                barcode.setText("");
                tvTemp.setVisibility(View.VISIBLE);
            }

            scan.setOnClickListener(this);

            temp_id.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.TEMP_ID)));
            tempIdOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.TEMP_ID));
            firstname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1)));
            etFirstname2.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2)));
            firstnameOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1));
            firstname2Orig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2));
            lastname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1)));
            lastnameOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1));

            bdate = dateParser(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHDATE)));
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
            birthdate.setText(ft.format(bdate));
            birthdateOrig = ft.format(bdate);
            ft = new SimpleDateFormat("dd-MMM-yyyy");
            birthdate_val = ft.format(bdate);

            motherFirstname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME)));
            motherFirOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME));
            motherLastname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME)));
            motherLastOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME));

            phone.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.PHONE)));
            phoneOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.PHONE));
            notes.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.NOTES)));
            notesOrig = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.NOTES));

            child_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            hf_id = (cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
            if (Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.GENDER)))) {
                male.setChecked(true);
                gender_val = "male";
                genderOrig = gender.getCheckedRadioButtonId();
            } else {
                female.setChecked(true);
                gender_val = "female";
                genderOrig = gender.getCheckedRadioButtonId();
            }
        }

        placeList = mydb.getAllPlaces();
        for(int i = 0 ; i<placeList.size();i++){
            if(placeList.get(i).getId().equals("-100")){
                notApplicablePos = i;
                break;
            }
        }
        List<String> place_names = new ArrayList<String>();
        for (Place element : placeList) {
            place_names.add(element.getName());
        }
        place_names.add("--------");

        birthplaceList = mydb.getAllBirthplaces();
        List<String> birthplaceNames = new ArrayList<String>();
        for (Birthplace element : birthplaceList) {
            birthplaceNames.add(element.getName());
        }
        birthplaceNames.add("--------");

        SingleTextViewAdapter birthplaceAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, birthplaceNames);
        birthplace.setAdapter(birthplaceAdapter);
        birthplace.setEnabled(false);
        int pos = birthplaceAdapter.getPosition(currentChild.getBirthplace());
        if (pos != -1) {
            birthplace.setSelection(pos);
            birthplaceOrig = pos;
        } else {
            birthplace.setSelection(birthplaceAdapter.getCount() - 1);
            birthplaceOrig = birthplaceAdapter.getCount() - 1;
        }

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, place_names);
        //@Teodor -> Modification -> E njejta liste si per Place of Birth dhe per Village
        village.setAdapter(dataAdapter);
        village.setEnabled(false);
        pos = dataAdapter.getPosition(currentChild.getDomicile());
        if (pos != -1) {
            village.setSelection(pos);
            villageOrig = pos;
        } else {
            village.setSelection(dataAdapter.getCount() - 1);
            villageOrig = dataAdapter.getCount() - 1;
        }


        healthFacilityList = mydb.getAllHealthFacility();
        List<String> facility_name = new ArrayList<String>();
        for (HealthFacility element : healthFacilityList) {
            facility_name.add(element.getName());
        }
        facility_name.add("------");

        SingleTextViewAdapter healthAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, facility_name);
        healthFacility.setAdapter(healthAdapter);
        healthFacility.setEnabled(false);
        pos = healthAdapter.getPosition(currentChild.getHealthcenter());
        if (pos != -1) {
            healthFacility.setSelection(pos);
            healthFacOrig = pos;

        } else {
            healthFacility.setSelection(healthAdapter.getCount() - 1);
            healthFacOrig = healthAdapter.getCount() - 1;
        }


        statusList = mydb.getStatus();
        List<String> status_name = new ArrayList<String>();

        for (Status element : statusList) {
            Log.d("Added status", element.getName());
            status_name.add(element.getName());
        }
        status_name.add("");
        SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, status_name);
        status.setAdapter(statusAdapter);
        status.setEnabled(false);
        pos = statusAdapter.getPosition(currentChild.getStatus());
        if (pos != -1) {
            status.setSelection(pos);
            statusOrig = pos;
        } else {
            status.setSelection(statusAdapter.getCount() - 1);
            statusOrig = statusAdapter.getCount() - 1;
        }

        loadViewAppointementsTable(false);
        initListeners();

    }

    private void initListeners() {
        birthplace.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                birthplace.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        birthplace.setEnabled(true);

        village.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                village.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        village.setEnabled(true);

        healthFacility.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                healthFacility.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        healthFacility.setEnabled(true);

        status.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                status.setSelection(position);
                // check if status is not active, if so than block everything else for being editable
                if (status.getSelectedItemPosition() != 2) {
                    setEditableFalse();
                } else {
                    setEditableTrue();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        status.setEnabled(true);

        //if the child have done vacinations in the past we can not anymore change birthday
        Cursor vacinationCursor = mydb.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + SQLHandler.Tables.VACCINATION_EVENT +
                        " where " + SQLHandler.VaccinationEventColumns.CHILD_ID + "=? and " +
                        SQLHandler.VaccinationEventColumns.VACCINATION_STATUS + "= 'true'",
                new String[]{currentChild.getId()});
        vacinationCursor.moveToFirst();
        if (vacinationCursor.getInt(0) <= 0) {
            birthdate.setOnClickListener(this);
        }
        weight.setOnClickListener(this);
        aefi.setOnClickListener(this);
        immunization_card.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(status_receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(status_receiver);
    }

    private void loadViewAppointementsTable(boolean doReload) {
        layout = (TableLayout) findViewById(R.id.view_appointment_table_layout);
        if (doReload) {
            layout.removeAllViews();
        }
        DatabaseHandler this_database = app.getDatabaseInstance();
        SQLHandler handler = new SQLHandler();
        var = new ArrayList<ViewAppointmentRow>();
        String result = "";

        if (currentChild.getId() != null && !currentChild.getId().isEmpty()) {
            child_id = currentChild.getId();
            Log.d("ViewAppointment:", "Child_Id: " + child_id);
            Cursor cursor = null;
            cursor = this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[]{child_id, child_id});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                        row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                        row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                        row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                        var.add(row);
                    } while (cursor.moveToNext());
                }
            }
        }

        //Show loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this, 0);
        progressDialog.setMessage("Loading table from database...");
        progressDialog.show();


        for (final ViewAppointmentRow a : var) {

            final Intent vaccinate = new Intent(this, AdministerVaccines2.class);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.setMargins(10, 10, 10, 10);
            row.setLayoutParams(lp);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().isEmpty()) {
                        vaccinate.putExtra("barcode", barcode.getText().toString());
                        vaccinate.putExtra("name", firstname.getText().toString() + " " + etFirstname2.getText().toString() +" " + lastname.getText().toString());
                        vaccinate.putExtra("mother_name", motherFirstname.getText().toString() + " " + motherLastname.getText().toString());
                        vaccinate.putExtra("dob", birthdate_val);
                        vaccinate.putExtra("gender", gender_val);
                        vaccinate.putExtra("childId", child_id);
                        vaccinate.putExtra("appointmentId", a.getAppointment_id());
                        vaccinate.putExtra("vaccines", a.getVaccine_dose());
                        vaccinate.putExtra("origin", "view_child");
                        startActivity(vaccinate);
                    } else {
                        toastMessage(getString(R.string.assign_barcode_first));
                    }
                }
            });

            TextView vac_dose = new TextView(this);
            vac_dose.setTextColor(Color.parseColor("#333333"));
            vac_dose.setBackgroundColor(Color.parseColor("#33819fd1"));
            vac_dose.setGravity(Gravity.CENTER);
            vac_dose.setHeight(100);
            vac_dose.setWidth(200);
            vac_dose.setText(a.getVaccine_dose());

            TextView schedule = new TextView(this);
            schedule.setTextColor(Color.parseColor("#333333"));
            schedule.setBackgroundColor(Color.parseColor("#33819fd1"));
            schedule.setGravity(Gravity.CENTER);
            schedule.setHeight(100);
            schedule.setWidth(200);
            schedule.setText(a.getSchedule());

            TextView sch_date = new TextView(this);
            sch_date.setTextColor(Color.parseColor("#333333"));
            sch_date.setBackgroundColor(Color.parseColor("#33819fd1"));
            sch_date.setGravity(Gravity.CENTER);
            sch_date.setHeight(100);
            sch_date.setWidth(200);
            Date scheduled_date = dateParser(a.getScheduled_date());
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            sch_date.setText(ft.format(scheduled_date));

            row.addView(vac_dose);
            row.addView(schedule);
            row.addView(sch_date);
            layout.addView(row);
        }
        progressDialog.dismiss();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.view_child_dob) {
            show();
        }
        else if (v.getId() == R.id.view_child_weight_btn) {
            if (!checkIfThereAreChangesData("weight")) {
                if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().isEmpty()) {
                    Intent weightIntent = new Intent(this, WeightActivity.class);
                    weightIntent.putExtra("currentChild", currentChild);
                    startActivity(weightIntent);
                } else {
                    toastMessage(getString(R.string.assign_barcode_first));
                }
            }
        }
        else if (v.getId() == R.id.view_child_immunization_card_btn) {
            if (!checkIfThereAreChangesData("Immunization")) {
                Intent immunizationCardIntent = new Intent(this, ImmunizationCardActivity.class);
                immunizationCardIntent.putExtra("barcode", barcode.getText().toString());
                immunizationCardIntent.putExtra("name", firstname.getText().toString() + " " + etFirstname2.getText().toString() + " " + lastname.getText().toString());
                immunizationCardIntent.putExtra("mother_name", motherFirstname.getText().toString() + " " + motherLastname.getText().toString());
                immunizationCardIntent.putExtra("dob", birthdate_val);
                immunizationCardIntent.putExtra("gender", currentChild.getGender());
                immunizationCardIntent.putExtra("childId", child_id);
                startActivity(immunizationCardIntent);
            }
        }
        else if (v.getId() == R.id.view_child_aefi_btn) {
            if (!checkIfThereAreChangesData("Aefi")) {
                Intent aefiIntent = new Intent(this, AefiActivity.class);
                aefiIntent.putExtra("barcode", barcode.getText().toString());
                aefiIntent.putExtra("name", firstname.getText().toString());
                aefiIntent.putExtra("firstname2", etFirstname2.getText().toString());
                aefiIntent.putExtra("mother_name", motherFirstname.getText().toString());
                aefiIntent.putExtra("dob", birthdate.getText().toString());
                aefiIntent.putExtra("gender", currentChild.getGender());
                startActivity(aefiIntent);
            }
        }
        else if (v.getId() == R.id.view_child_save_button) {
            if (checkDataIntegrityBeforeSave()) {
                if (!localBarcode.equals(barcode.getText().toString()) && !localBarcode.equals("")) {
                    showAlertThatChildHadABarcode();
                } else {
                    saveChangedData();
                }
            }
        }
        else if (v.getId() == R.id.home_btn_scan) {
            if (!checkIfThereAreChangesData("scan")) {
                if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().isEmpty()) {
                    showDialogGoingScanWhenHasBarcode();
                } else {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(ViewChildActivity.this);
                    //TODO modified by coze
//                    scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    scanIntegrator.initiateScan();
                }
            }

        }
        else if(v.getId() == R.id.img_btn_home_view_child){
            if(!checkIfThereAreChangesData("home")){
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                barcode.setText(scanContent);
                initListeners();
            }
        }
    }

    private void showDialogGoingScanWhenHasBarcode() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.change_barcode_scan_when_exists_dialog))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((AlertDialog) dialogInterface).dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                        IntentIntegrator scanIntegrator = new IntentIntegrator(ViewChildActivity.this);
                        //TODO modified by coze
//                        scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        scanIntegrator.initiateScan();
                    }
                });
        alertDialogBuilder.show();
    }

    /**
     * This is a method that is used to check if the user has changed any data from the data of the child
     * If yes that we save the changes, if not then we toast this.
     */
    private void saveChangedData() {
        giveValueAfterSave();
        ContentValues contentValues = new ContentValues();

        if (!barcode.getText().toString().equalsIgnoreCase(currentChild.getBarcodeID())) {
            currentChild.setBarcodeID(barcode.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.BARCODE_ID, barcode.getText().toString());
        }
        if (!firstname.getText().toString().equalsIgnoreCase(currentChild.getFirstname1())) {
            currentChild.setFirstname1(firstname.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.FIRSTNAME1, firstname.getText().toString());
        }
        if (!etFirstname2.getText().toString().equalsIgnoreCase(currentChild.getFirstname2())) {
            currentChild.setFirstname2(etFirstname2.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.FIRSTNAME2, etFirstname2.getText().toString());
        }
        if (!lastname.getText().toString().equalsIgnoreCase(currentChild.getLastname1())) {
            currentChild.setLastname1(lastname.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.LASTNAME1, lastname.getText().toString());
        }
        if (bdate.compareTo(dateParser(currentChild.getBirthdate())) != 0) {
            birthDatesDiff = bdate.getTime() - dateParser(currentChild.getBirthdate()).getTime();
            // trick qe te marrim sa dite diference kemi dhe te gjejme fiks me sa dite ndryshon datelindja ne terma timestamp
            // e bejme gjithashtu nje floor te divisionit keshtu qe marrim vetem pjesen e plote te pjestimit dhe nuk
            // ngaterrohemi me castimin ne int. Ne cdo rast duhet te kemi kujdes ne mos numrat na kastohen ne int per
            // arsye se int do te na japi nje overflow dhe si pasoje nuk do te na ktheje vleren e sakte.
            // tani nuk do te na duhet me qe te bejme trickun me kalimin e nje dite diference pasi ate e kemi pasur si problem nga
            // overflow qe na bente int.
            double daysDiff = Math.floor(birthDatesDiff / 86400000);
            birthDatesDiff = (long) daysDiff * 86400000;
            currentChild.setBirthdate(birthdate_val);
            contentValues.put(SQLHandler.ChildColumns.BIRTHDATE, stringToDateParser(bdate));
        }
        if (!motherFirstname.getText().toString().equalsIgnoreCase(currentChild.getMotherFirstname())) {
            currentChild.setMotherFirstname(motherFirstname.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, motherFirstname.getText().toString());
        }
        if (!motherLastname.getText().toString().equalsIgnoreCase(currentChild.getMotherLastname())) {
            currentChild.setMotherLastname(motherLastname.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, motherLastname.getText().toString());
        }
        if (!birthplaceList.get(birthplace.getSelectedItemPosition()).getName().equalsIgnoreCase(currentChild.getBirthplace())) {
            currentChild.setBirthplaceId(birthplaceList.get(birthplace.getSelectedItemPosition()).getId());
            currentChild.setBirthplace(birthplaceList.get(birthplace.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE, birthplaceList.get(birthplace.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, birthplaceList.get(birthplace.getSelectedItemPosition()).getId());
        }
        if (!placeList.get(village.getSelectedItemPosition()).getName().equalsIgnoreCase(currentChild.getDomicile())) {
            currentChild.setDomicileId(placeList.get(village.getSelectedItemPosition()).getId());
            currentChild.setDomicile(placeList.get(village.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.DOMICILE, placeList.get(village.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.DOMICILE_ID, placeList.get(village.getSelectedItemPosition()).getId());
        }
        if (!healthFacilityList.get(healthFacility.getSelectedItemPosition()).getName().equalsIgnoreCase(currentChild.getHealthcenter())) {
            currentChild.setHealthcenterId(healthFacilityList.get(healthFacility.getSelectedItemPosition()).getId());
            currentChild.setHealthcenter(healthFacilityList.get(healthFacility.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY, healthFacilityList.get(healthFacility.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, healthFacilityList.get(healthFacility.getSelectedItemPosition()).getId());
        }
        if (!statusList.get(status.getSelectedItemPosition()).getName().equalsIgnoreCase(currentChild.getStatus())) {
            currentChild.setStatusId(statusList.get(status.getSelectedItemPosition()).getId());
            currentChild.setStatus(statusList.get(status.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.STATUS, statusList.get(status.getSelectedItemPosition()).getName());
            contentValues.put(SQLHandler.ChildColumns.STATUS_ID, statusList.get(status.getSelectedItemPosition()).getId());
        }
        if (male.isChecked() && !gender_val.equalsIgnoreCase("male")) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "true");
        } else if (female.isChecked() && !gender_val.equalsIgnoreCase("female")) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "false");
        }
        if (!phone.getText().toString().equalsIgnoreCase(currentChild.getPhone())) {
            currentChild.setPhone(phone.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.PHONE, currentChild.getPhone());
        }
        if (!notes.getText().toString().equalsIgnoreCase(currentChild.getNotes())) {
            currentChild.setNotes(notes.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.NOTES, currentChild.getNotes());
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });

        try {
            if (contentValues.size() > 0) {

                if (mydb.updateChild(contentValues, currentChild.getId()) > 0) {
                    if (birthDatesDiff != 0) {
                        mydb.updateVaccinationAppointementForBirthDtChangeChild(currentChild.getId(), birthDatesDiff);
                        mydb.updateVaccinationEventForBirthDtChangeChild(currentChild.getId(), birthDatesDiff);
                        loadViewAppointementsTable(true);
                    }

                    // bejme update statusin e appointement nese ka ndryshuar statusi i childit
                    if (!currentChild.getStatusId().equalsIgnoreCase("1"))
                        mydb.updateVaccinationAppointementDisactive(currentChild.getId());
                    // bejme update vacination appointement nese
                    if (contentValues.get(SQLHandler.ChildColumns.HEALTH_FACILITY_ID) != null) {
                        mydb.updateVaccinationAppointementNewFacility(currentChild.getId(), currentChild.getHealthcenterId());
                        mydb.updateVaccinationEventNewFacility(currentChild.getId(), currentChild.getHealthcenterId());
                    }

                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_success);
                    thread = new Thread() {
                        @Override
                        public void run() {
                            String url = prepareUrl().toString();
                            String threadTodayTimestamp= null;
                            BackboneApplication backbone = (BackboneApplication) getApplication();
                            if (!app.updateChild(prepareUrl())) {
                                mydb.addPost(url, -1);
                                Log.d("Save Edited Child", "Error while saving edited child " + currentChild.getId());
                            } else {
                                String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
                                try {
                                     threadTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                //Register Audit
                                backbone.registerAudit(BackboneApplication.CHILD_AUDIT, barcode.getText().toString(), threadTodayTimestamp ,
                                        backbone.getLOGGED_IN_USER_ID(), 2);
                            }
                        }
                    };
                    thread.start();
                } else {
                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_error);
                    initListeners();
                }
                alertDialogBuilder.show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * This funcition is for checking if the data that we are trying to update for the child are
     * accepptable.
     */
    private boolean checkDataIntegrityBeforeSave() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_empty_fields))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });
        if (barcode.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_barcode));
            alertDialogBuilder.show();
            return false;
        }
        if (!barcode.getText().toString().equalsIgnoreCase(currentChild.getBarcodeID())) {
            if (mydb.isBarcodeInChildTable(barcode.getText().toString())) {
                alertDialogBuilder.setMessage(getString(R.string.barcode_assigned));
                alertDialogBuilder.show();
                return false;
            }
        }
        if (firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_names));
            alertDialogBuilder.show();
            return false;
        }
        if (motherFirstname.getText().toString().isEmpty() || motherLastname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_mother_names));
            alertDialogBuilder.show();
            return false;
        }
        if (motherFirstname.getText().toString().isEmpty() || motherLastname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_mother_names));
            alertDialogBuilder.show();
            return false;
        }
        if (bdate.compareTo(new Date()) > 0) {
            alertDialogBuilder.setMessage(getString(R.string.future_birth_date));
            alertDialogBuilder.show();
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (birthplace.getSelectedItemPosition() == birthplaceList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_birthplace));
            alertDialogBuilder.show();
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (village.getSelectedItemPosition() == placeList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_village));
            alertDialogBuilder.show();
            return false;
        }
        if (healthFacility.getSelectedItemPosition() == healthFacilityList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_healthfacility));
            alertDialogBuilder.show();
            return false;
        }
        if (status.getSelectedItemPosition() == statusList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_status));
            alertDialogBuilder.show();
            return false;
        }
        // If not applicable is selected in the Birthplace or Domicile spinners and Notes is empty , than we need the user to fill the notes field
        if (village.getSelectedItemPosition() == notApplicablePos && notes.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_notes));
            alertDialogBuilder.show();
            return false;
        }
        return true;
    }

    private StringBuilder prepareUrl() {
        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.CHILD_MANAGEMENT_SVC).append(BackboneApplication.CHILD_UPDATE);
        try {
            webServiceUrl.append("barcode=" + URLEncoder.encode(barcode.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&firstname1=" + URLEncoder.encode(firstname.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&lastname1=" + URLEncoder.encode(lastname.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        try {
            webServiceUrl.append("&birthdate=" + URLEncoder.encode(formatted.format(bdate), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&motherFirstname=" + URLEncoder.encode(motherFirstname.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&motherLastname=" + URLEncoder.encode(motherLastname.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&birthplaceId=" + URLEncoder.encode(birthplaceList.get(birthplace.getSelectedItemPosition()).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&domicileId=" + URLEncoder.encode(placeList.get(village.getSelectedItemPosition()).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&healthFacilityId=" + URLEncoder.encode(healthFacilityList.get(healthFacility.getSelectedItemPosition()).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&statusid=" + URLEncoder.encode(statusList.get(status.getSelectedItemPosition()).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (male.isChecked())
            webServiceUrl.append("&gender=true");
        else
            webServiceUrl.append("&gender=true");
        try {
            webServiceUrl.append("&phone=" + URLEncoder.encode(phone.getText().toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&notes=" + URLEncoder.encode(notes.getText().toString(), "UTF-8"));
            if (currentChild.getId().matches("\\d+")) {
                webServiceUrl.append("&childId=" + URLEncoder.encode(currentChild.getId(), "UTF-8"));
            } else {
                webServiceUrl.append("&childId=" + 0); // hardcoded workaround for issues related to guid
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            webServiceUrl.append("&firstname2=" + URLEncoder.encode(etFirstname2.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&modifiedOn=" + URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return webServiceUrl;
    }

    public void show() {
        final Dialog d = new Dialog(ViewChildActivity.this);
        d.setTitle(getString(R.string.birthdate_picker));
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
        dp.setMaxDate(new Date().getTime());
        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //birthdate.setText(Integer.toString(dp.getDayOfMonth()) + "/"  + Integer.toString(dp.getMonth()+1) + "/" + Integer.toString(dp.getYear()));
                Calendar calendar = Calendar.getInstance();
                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());

                Date new_date = calendar.getTime();
                Date dNow = new Date();

                if (getDaysDifference(new_date, dNow) > 0) {
                    birthdate.setText(ft.format(new_date));
                } else {
                    birthdate.setText(ft.format(new_date));
                }


                bdate = calendar.getTime();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });

        d.show();
    }

    public List<String> cleanDouble(List<String> a) {
        HashSet hs = new HashSet();
        hs.addAll(a);
        a.clear();
        a.addAll(hs);
        return a;
    }

    public Child getChildFromCursror(Cursor cursor) {
        Child parsedChild = new Child();
        parsedChild.setId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID)));
        parsedChild.setBarcodeID(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BARCODE_ID)));
        parsedChild.setTempId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.TEMP_ID)));
        parsedChild.setFirstname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1)));
        parsedChild.setFirstname2(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2)));
        parsedChild.setLastname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1)));
        parsedChild.setBirthdate(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHDATE)));
        parsedChild.setMotherFirstname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME)));
        parsedChild.setMotherLastname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME)));
        parsedChild.setPhone(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.PHONE)));
        parsedChild.setNotes(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.NOTES)));
        parsedChild.setBirthplaceId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHPLACE_ID)));
        parsedChild.setGender(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.GENDER)));
        Cursor cursor1 = mydb.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }

        parsedChild.setDomicile(villagestr);
        parsedChild.setHealthcenterId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
        try {
            Cursor cursor3 = mydb.getReadableDatabase().rawQuery("SELECT * FROM health_facility WHERE ID=?", new String[]{parsedChild.getHealthcenterId()});
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                hfstr = cursor3.getString(cursor3.getColumnIndex(SQLHandler.HealthFacilityColumns.NAME));
            }
        }catch (Exception e){
            hfstr = "";
        }
        parsedChild.setHealthcenter(hfstr);

        parsedChild.setStatusId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.STATUS_ID)));
        Cursor cursor4 = mydb.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

    private void showAlertThatChildHadABarcode() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(ViewChildActivity.this);

        ad.setTitle(getString(R.string.warning));
        ad.setMessage(getString(R.string.barcode_already_entered));
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveChangedData();
                dialog.dismiss();
            }
        });

        // this will solve your error
        AlertDialog alert = ad.create();
        alert.show();
        alert.getWindow().getAttributes();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(30);
    }


    private void decideShowBackBtnOrNot() {
        Button back = (Button) findViewById(R.id.back_btn);
        if (getIntent().hasExtra("cameFromSearch")) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkIfThereAreChangesData("back")) {
                        ViewChildActivity.super.onBackPressedInViewChildWhenCameFromSearch();
                    }
                }
            });
        } else {
            back.setVisibility(View.GONE);
        }
    }

    public boolean checkIfThereAreChangesData(final String type) {
        if (!barcode.getText().toString().equals(barcodeOrig) || !firstname.getText().toString().equals(firstnameOrig) || !etFirstname2.getText().toString().equals(firstname2Orig) ||  !motherFirstname.getText().toString().equals(motherFirOrig) || !phone.getText().toString().equals(phoneOrig) ||
                !notes.getText().toString().equals(notesOrig) || !motherLastname.getText().toString().equals(motherLastOrig)
                || !lastname.getText().toString().equals(lastnameOrig) || !birthdate.getText().toString().equals(birthdateOrig)
                || birthplace.getSelectedItemPosition() != birthplaceOrig || healthFacility.getSelectedItemPosition() != healthFacOrig || status.getSelectedItemPosition() != statusOrig || village.getSelectedItemPosition() != villageOrig ||
                gender.getCheckedRadioButtonId() != genderOrig) {
            final AlertDialog.Builder ad = new AlertDialog.Builder(ViewChildActivity.this);

            ad.setTitle("Warning");
            ad.setMessage(getString(R.string.are_you_sure));
            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (type.equals("scan")) {
                        if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().isEmpty()) {
                            showDialogGoingScanWhenHasBarcode();
                        } else {
                            IntentIntegrator scanIntegrator = new IntentIntegrator(ViewChildActivity.this);
                            //TODO modified by coze
//                            scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            scanIntegrator.initiateScan();
                        }

                    }
                    else if (type.equals("weight")) {

                        if (currentChild.getBarcodeID() != null && !currentChild.getBarcodeID().isEmpty()) {
                            Intent weightIntent = new Intent(ViewChildActivity.this, WeightActivity.class);
                            weightIntent.putExtra("currentChild", currentChild);
                            startActivity(weightIntent);
                        } else {
                            toastMessage(getString(R.string.assign_barcode_first));
                        }
                    }

                    else if (type.equals("Aefi")) {

                        Intent aefiIntent = new Intent(ViewChildActivity.this, AefiActivity.class);
                        aefiIntent.putExtra("barcode", barcode.getText().toString());
                        aefiIntent.putExtra("name", firstname.getText().toString());
                        aefiIntent.putExtra("mother_name", motherFirstname.getText().toString());
                        aefiIntent.putExtra("dob", birthdate.getText().toString());
                        aefiIntent.putExtra("gender", currentChild.getGender());
                        startActivity(aefiIntent);
                    }
                    else if (type.equals("Immunization")) {


                        Intent immunizationCardIntent = new Intent(ViewChildActivity.this, ImmunizationCardActivity.class);
                        immunizationCardIntent.putExtra("barcode", barcode.getText().toString());
                        immunizationCardIntent.putExtra("name", firstname.getText().toString() + " " + etFirstname2.getText().toString() +" " + lastname.getText().toString());
                        immunizationCardIntent.putExtra("mother_name", motherFirstname.getText().toString() + " " + motherLastname.getText().toString());
                        immunizationCardIntent.putExtra("dob", birthdate_val);
                        immunizationCardIntent.putExtra("gender", currentChild.getGender());
                        immunizationCardIntent.putExtra("childId", child_id);
                        startActivity(immunizationCardIntent);
                    }
                    else if (type.equals("back")) {
                        ViewChildActivity.super.onBackPressedInViewChildWhenCameFromSearch();
                    }
                    else if (type.equals("home")){
                        Intent i = new Intent(ViewChildActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                    dialog.dismiss();
                }
            });

            // this will solve your error
            AlertDialog alert = ad.create();
            alert.show();
            alert.getWindow().getAttributes();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            textView.setTextSize(30);

            return true;
        }
        return false;
    }

    public void setEditableFalse() {
        barcode.setEnabled(false);
        firstname.setEnabled(false);
        etFirstname2.setEnabled(false);
        motherFirstname.setEnabled(false);
        birthdate.setOnClickListener(null);
        phone.setEnabled(false);
        notes.setEnabled(false);
        male.setEnabled(false);
        female.setEnabled(false);
        motherLastname.setEnabled(false);
        lastname.setEnabled(false);
        weight.setOnClickListener(null);
        scan.setOnClickListener(null);
        aefi.setOnClickListener(null);
        birthplace.setEnabled(false);
        healthFacility.setEnabled(false);
        village.setEnabled(false);
        immunization_card.setOnClickListener(null);

    }

    public void setEditableTrue() {
        barcode.setEnabled(true);
        firstname.setEnabled(true);
        etFirstname2.setEnabled(true);
        motherFirstname.setEnabled(true);
        birthdate.setOnClickListener(this);
        phone.setEnabled(true);
        notes.setEnabled(true);
        male.setEnabled(true);
        female.setEnabled(true);
        motherLastname.setEnabled(true);
        lastname.setEnabled(true);
        weight.setOnClickListener(this);
        scan.setOnClickListener(this);
        aefi.setOnClickListener(this);
        birthplace.setEnabled(true);
        healthFacility.setEnabled(true);
        village.setEnabled(true);
        immunization_card.setOnClickListener(this);

    }

    public void giveValueAfterSave(){
        tempIdOrig = temp_id.getText().toString();
        firstnameOrig = firstname.getText().toString();
        firstname2Orig = etFirstname2.getText().toString();
        lastnameOrig = lastname.getText().toString();
        birthdateOrig = birthdate.getText().toString();
        motherFirOrig = motherFirstname.getText().toString();
        motherLastOrig = motherLastname.getText().toString();
        phoneOrig = phone.getText().toString();
        notesOrig = notes.getText().toString();
        barcodeOrig = barcode.getText().toString();
        birthplaceOrig = birthplace.getSelectedItemPosition();
        villageOrig = village.getSelectedItemPosition();
        healthFacOrig = healthFacility.getSelectedItemPosition();
        statusOrig = status.getSelectedItemPosition();
        genderOrig =   gender.getCheckedRadioButtonId();

    }
}
