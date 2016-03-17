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
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.Birthplace;
import mobile.giis.app.entity.Place;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Arinela on 2/8/2015.
 */
public class RegisterChildActivity  extends BackboneActivity implements View.OnClickListener, View.OnTouchListener {





    public Button btnBirthdate, btnScan, btnSave;
    public Spinner spPlaceOfBirth, spVillage;
    public RelativeLayout progBar;
    public RadioButton rBtnMale, rbtnFemale;
    protected EditText etbarcode, etFirstName, etSurname, etMotherFirstName, etMotherSurname, etPhone, etNotes,etFirstname2;
    List<Place> placeList;
    List<Birthplace> birthplaceList;
    String barcode, firstanme, surname, motherFirstname, motherLastname, gender_val, gen, genderChildWithoutApp,firstname2;
    int spPlacePos, spVillagePos , notApplicablePos = -1;
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


    private Date bdate;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_child_activity);

        progBar = (RelativeLayout)findViewById(R.id.rl_load_item_post);
        progBar.setOnClickListener(this);
        etbarcode = (EditText) findViewById(R.id.barcode_field);
        etFirstName = (EditText) findViewById(R.id.firstname);
        etSurname = (EditText) findViewById(R.id.lastname);
        etMotherFirstName = (EditText) findViewById(R.id.mother_firstname);
        etMotherSurname = (EditText) findViewById(R.id.mother_lastname);
        etPhone = (EditText) findViewById(R.id.phone);
        etNotes = (EditText) findViewById(R.id.notes);
        btnScan = (Button) findViewById(R.id.register_child_btn_scan);
        btnScan.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.save_button);
        btnSave.setOnClickListener(this);
        btnBirthdate = (Button) findViewById(R.id.birthdate_field);
        btnBirthdate.setOnClickListener(this);
        spPlaceOfBirth = (Spinner) findViewById(R.id.place_of_birth);
        spVillage = (Spinner) findViewById(R.id.village_domicile);
        rBtnMale = (RadioButton) findViewById(R.id.male_radio);
        etFirstname2 = (EditText)findViewById(R.id.firstname2);
        rbtnFemale = (RadioButton) findViewById(R.id.female_radio);
      //  rBtnMale.setChecked(true);
        //Mer vlerat nga e fushave te plotesuara nga search child

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barcode = extras.getString("barcode", "");
            firstanme = extras.getString("firstname", "");
            surname = extras.getString("surname", "");
            motherFirstname = extras.getString("motherFirstname", "");
            motherLastname = extras.getString("motherSurname", "");
            spPlacePos = extras.getInt("placeOfBirth", 0);
            spVillagePos = extras.getInt("domicile", 0);
            firstname2 = extras.getString("firstname2","");


        }

        /*
        if(true){
            etbarcode.setVisibility(View.GONE);
        }*/

        //Mbushja e edit text tek regjister

        etbarcode.setText(barcode);
        etFirstName.setText(firstanme);
        etFirstname2.setText(firstname2);
        etSurname.setText(surname);
        etMotherFirstName.setText(motherFirstname);
        etMotherSurname.setText(motherLastname);


        //mbushja e spinner
        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();

        placeList = mydb.getAllPlaces();
        for(int i = 0 ; i<placeList.size();i++){
            if(placeList.get(i).getId().equals("-100")){
                notApplicablePos = i+1;
                break;
            }
        }
        List<String> place_names = new ArrayList<String>();
        place_names.add("-Please choose-");
        for (Place element : placeList) {

            place_names.add(element.getName());
        }

        birthplaceList = mydb.getAllBirthplaces();
        List<String> birthplaceNames = new ArrayList<String>();
        birthplaceNames.add("-Please choose-");
        for (Birthplace element : birthplaceList) {

            birthplaceNames.add(element.getName());
        }

        SingleTextViewAdapter spBirthOfPlace = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, birthplaceNames);
        spPlaceOfBirth.setAdapter(spBirthOfPlace);

        if (spPlacePos != 0) {
            spPlaceOfBirth.setSelection(spPlacePos);
        } else {
            spPlaceOfBirth.setSelection(0);
        }

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, place_names);
        spVillage.setAdapter(dataAdapter);

        if (spVillagePos != 0) {
            spVillage.setSelection(spVillagePos);
        }
        spVillage.setSelection(spVillagePos);

    }

    public void show() {
        final Dialog d = new Dialog(RegisterChildActivity.this);
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
                //btnBirthdate.setText(Integer.toString(dp.getDayOfMonth()) + "/"  + Integer.toString(dp.getMonth()+1) + "/" + Integer.toString(dp.getYear()));
                Calendar calendar = Calendar.getInstance();
                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), 0, 0);
                Date new_date = calendar.getTime();
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+0300"));
                bdate = calendar.getTime();


                Date dNow = new Date();

                if (getDaysDifference(new_date, dNow) > 0) {
                    btnBirthdate.setText(ft.format(new_date));
                } else {
                    btnBirthdate.setText(ft.format(new_date));
                }

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


    public void showDialogWhenRegisterIsDone() {
        final Dialog d = new Dialog(RegisterChildActivity.this);
        d.setTitle(getString(R.string.title_register_child));
        d.setContentView(R.layout.layout_register_child);
        d.show();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (d != null) {
                                    d.dismiss();
                                    progBar.setVisibility(View.GONE);

                                }
                            }
                        });
                    }
                },
                3500
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(status_receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                etbarcode.setText(scanContent);
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
                        IntentIntegrator scanIntegrator = new IntentIntegrator(RegisterChildActivity.this);
                        //TODO modified by coze
//                        scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        scanIntegrator.initiateScan();
                    }
                });
        alertDialogBuilder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(status_receiver);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.birthdate_field) {
            show();
        }

        if (v.getId() == R.id.register_child_btn_scan) {
            if (barcode != null) {
                if (!barcode.equalsIgnoreCase("")) {
                    showDialogGoingScanWhenHasBarcode();
                } else {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(RegisterChildActivity.this);
                    //TODO modified by coze
//                    scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    scanIntegrator.initiateScan();
                }

            } else {
                IntentIntegrator scanIntegrator = new IntentIntegrator(RegisterChildActivity.this);
//                TODO modified by coze
//                scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                scanIntegrator.initiateScan();
            }
        }

        if (v.getId() == R.id.save_button) {

            BackboneApplication app = (BackboneApplication) getApplication();
            DatabaseHandler mydb = app.getDatabaseInstance();
            progBar.setVisibility(View.VISIBLE);
            if (mydb.isBarcodeInChildTable(etbarcode.getText().toString())) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.same_barcode))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((AlertDialog) dialog).dismiss();
                                progBar.setVisibility(View.GONE);
                            }
                        });
                alertDialogBuilder.show();
                return;
            }
            if (checkDataIntegrityBeforeSave()) {
                progBar.setVisibility(View.VISIBLE);
                //kontrrollojme nese kemi ne db kete child me keto te dhena,nese true nxjerim dialog,nese false bejme regjistrimin
                if (mydb.isChildinDb(etSurname.getText().toString(), bdate.getTime(), gender_val)) {

                    createDialogAlertIsInChild().show();
                } else {
                    if (rBtnMale.isChecked()) {
                        gen = "M";
                    } else {
                        gen = "F";
                    }

                    askServerIfthereIsSimilarChild(etSurname.getText().toString(), bdate, gen);
                    Log.e("CheckInSever","CheckInSever");
                }

            }

        }
    }

    public Dialog createDialogAlertIsInChild() {

        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.child_with_this_data_exist))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                registerChildInDB();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                                progBar.setVisibility(View.GONE);
                            }
                        }
                )
                .create();
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
        if (etbarcode.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_barcode));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        if (etbarcode.getText().length()!=10) {
            alertDialogBuilder.setMessage(getString(R.string.barcode_ten_numbers_constraint));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        if (etFirstName.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_firstname));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        if (etSurname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_surname));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        if (etMotherFirstName.getText().toString().isEmpty() || etMotherSurname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_mother_names));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        if (bdate == null || bdate.compareTo(new Date()) > 0) {
            alertDialogBuilder.setMessage(getString(R.string.future_birth_date));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (spPlaceOfBirth.getSelectedItemPosition() == 0) {
            alertDialogBuilder.setMessage(getString(R.string.empty_birthplace));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (spVillage.getSelectedItemPosition() == 0) {
            alertDialogBuilder.setMessage(getString(R.string.empty_village));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }
        // If not applicable is selected in the Birthplace or Domicile spinners and Notes is empty , than we need the user to fill the notes field
        if (spVillage.getSelectedItemPosition() == notApplicablePos && etNotes.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_notes));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }


        if (rBtnMale.isChecked()) {
            gender_val = "true";
        } else if(rbtnFemale.isChecked()){
            gender_val = "false";
        }else{
            alertDialogBuilder.setMessage(getString(R.string.empty_gender));
            alertDialogBuilder.show();
            progBar.setVisibility(View.GONE);
            return false;
        }

        progBar.setVisibility(View.GONE);
        return true;
    }

    public void registerChildInDB() {

        ContentValues contentValues = new ContentValues();

        if (!etbarcode.getText().toString().equalsIgnoreCase("")) {

            contentValues.put(SQLHandler.ChildColumns.BARCODE_ID, etbarcode.getText().toString());
        }


        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME1, etFirstName.getText().toString());
        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME2, etFirstname2.getText().toString());


        if (!etSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.LASTNAME1, etSurname.getText().toString());
        }
        if (bdate != null && bdate.compareTo(new Date()) < 0) {
            contentValues.put(SQLHandler.ChildColumns.BIRTHDATE, stringToDateParser(bdate));
        }
        if (!etMotherFirstName.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, etMotherFirstName.getText().toString());
        }
        if (!etMotherSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, etMotherSurname.getText().toString());
        }


        BackboneApplication backboneApplication = (BackboneApplication) getApplication();
        contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, backboneApplication.getLOGGED_IN_USER_HF_ID());
        contentValues.put(SQLHandler.ChildColumns.ADDRESS, "");
        contentValues.put(SQLHandler.ChildColumns.MODIFIED_ON, new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));


        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE, birthplaceList.get(spPlaceOfBirth.getSelectedItemPosition() - 1).getName());
        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, birthplaceList.get(spPlaceOfBirth.getSelectedItemPosition() - 1).getId());


        contentValues.put(SQLHandler.ChildColumns.DOMICILE, placeList.get(spVillage.getSelectedItemPosition() - 1).getName());
        contentValues.put(SQLHandler.ChildColumns.DOMICILE_ID, placeList.get(spVillage.getSelectedItemPosition() - 1).getId());


        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace('\'','a');
        uuid = uuid.replace('\"','a');
        contentValues.put(SQLHandler.ChildColumns.ID, uuid);

        if (rBtnMale.isChecked()) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "true");
        } else if (rbtnFemale.isChecked()) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "false");
        }
        contentValues.put("modfied_at", "/Date(" + Calendar.getInstance().getTime().getTime() + "-0500)/");
        contentValues.put(SQLHandler.ChildColumns.PHONE, etPhone.getText().toString());
        contentValues.put(SQLHandler.ChildColumns.NOTES, etNotes.getText().toString());
        contentValues.put("updated", 1);
        contentValues.put("owners_username", "");
        contentValues.put("STATUS_ID", "");
        contentValues.put("HEALTH_FACILITY", "");
        contentValues.put("COMMUNITY_ID", "");
        contentValues.put("MOBILE", "");
        contentValues.put("MOTHER_ID", "");
        contentValues.put("STATUS_ID", 1);
        contentValues.put("STATUS", "");
        contentValues.put("SYSTEM_ID", "");
        contentValues.put("TEMP_ID", "");


        if (contentValues.size() > 0) {

            BackboneApplication app = (BackboneApplication) getApplication();
            DatabaseHandler mydb = app.getDatabaseInstance();
            contentValues.put("MODIFIED_BY", app.getLOGGED_IN_USER_ID());
            if (mydb.registerChild(contentValues) > -1) {
                mydb.InsertVaccinationsForChild(uuid, app.getLOGGED_IN_USER_ID());
                showDialogWhenRegisterIsDone();
                if (rBtnMale.isChecked()) {
                    genderChildWithoutApp = "true";
                } else {
                    genderChildWithoutApp = "false";

                }

                try {
                    registerChildWithoutAppointments(etbarcode.getText().toString(),
                            etFirstName.getText().toString(),
                            etSurname.getText().toString(),
                            bdate,
                            genderChildWithoutApp,
                            app.getLOGGED_IN_USER_HF_ID(),
                            birthplaceList.get(spPlaceOfBirth.getSelectedItemPosition() - 1).getId(),
                            placeList.get(spVillage.getSelectedItemPosition() - 1).getId(),
                            "",
                            etPhone.getText().toString(),
                            etMotherFirstName.getText().toString(),
                            etMotherSurname.getText().toString(),
                            etNotes.getText().toString(),
                            app.getLOGGED_IN_USER_ID(),
                            Calendar.getInstance().getTime(),
                            uuid,etFirstname2.getText().toString());
                }catch(Exception exception){
                    exception.printStackTrace();
                }




            }else{
                progBar.setVisibility(View.GONE);
            }

        }

    }

    private synchronized void askServerIfthereIsSimilarChild(String lastname, final Date bdate, String gender) {
        new Thread() {
            String threadBDateString;
            String threadLastname
                    ,
                    threadGender;

            public Thread setData(String threadLastname, Date threadBDate, String threadGender) {

                try {
                    this.threadLastname = threadLastname;
                    this.threadBDateString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(threadBDate), "utf-8");
                    this.threadGender = threadGender;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public void run() {
                super.run();

                BackboneApplication backbone = (BackboneApplication) getApplication();

                final boolean found = backbone.checkChildInServer(threadLastname, threadBDateString, threadGender);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (found) {
                            createDialogAlertIsInChild().show();
                        } else {

                            registerChildInDB();
                        }


                    }
                });
            }
        }.setData(lastname, bdate, gender).start();
    }

    private synchronized void registerChildWithoutAppointments(String barcode, String fristname, String lastname, Date bDate, String gender, String  hfid, String birthPlaceId, String domId,
                                                               String addr, String phone, String motherFirstname, String motherLastname, String notes, String userID, Date modOn, final String tempId,String firstname2) {
        new Thread() {
            String threadBDateString;
            String threadModOn;
            String threadbarcode
                    ,
                    threadfristname
                    ,
                    threadLastname
                    ,
                    threadGender
                    ,
                    threadAddr
                    ,
                    threadPhone
                    ,
                    threadMotherFirstname
                    ,
                    threadMotherLastname
                    ,
                    threadhfid
                    ,
                    threadBirthPlaceID
                    ,
                    threadDomID
                    ,
                    threadNotes;
            String threadUserID;
            String threadTempId;
            String threadFirstname2;

            public Thread setData(String threadbarcode, String threadfristname, String threadLastname, Date threadBDate, String threadGender, String threadhfid, String threadBirthPlaceID, String threadDomID,
                                  String threadAddr, String threadPhone, String threadMotherFirstname, String threadMotherLastname, String threadNotes, String threadUserID, Date threadModOn, String tempId,String threadFirstname2) {

                try {
                    this.threadbarcode = threadbarcode;
                    this.threadfristname = threadfristname;
                    this.threadFirstname2 = threadFirstname2;
                    this.threadLastname = threadLastname;
                    this.threadGender = threadGender;
                    this.threadhfid = threadhfid;
                    this.threadBirthPlaceID = threadBirthPlaceID;
                    this.threadDomID = threadDomID;
                    this.threadAddr = threadAddr;
                    this.threadPhone = threadPhone;
                    this.threadMotherFirstname = threadMotherFirstname;
                    this.threadMotherLastname = threadMotherLastname;
                    this.threadNotes = threadNotes;
                    this.threadUserID = threadUserID;
                    this.threadTempId = tempId;
                    this.threadModOn = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(threadModOn), "utf-8");
                    this.threadBDateString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(threadBDate), "utf-8");
                    this.threadGender = threadGender;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public void run() {
                super.run();

                BackboneApplication backbone = (BackboneApplication) getApplication();

                final int childID = backbone.registerChildWithAppoitments(threadbarcode, threadfristname, threadLastname, threadBDateString, threadGender, threadhfid, threadBirthPlaceID, threadDomID, threadAddr
                        , threadPhone, threadMotherFirstname, threadMotherLastname, threadNotes, threadUserID, threadModOn, null,threadFirstname2);


                if (childID != -1) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SQLHandler.ChildColumns.ID, childID);
                    DatabaseHandler mydb = backbone.getDatabaseInstance();
                    if (mydb.updateChildTableWithChildID(contentValues, threadTempId)) {

                        mydb.updateVaccinationAppointementChildId(threadTempId, childID + "");
                        mydb.updateVaccinationEventChildId(threadTempId, childID + "");


                        Intent viewChild = new Intent(getApplicationContext(), ViewChildActivity.class);
                        Bundle bnd = new Bundle();
                        bnd.putString(BackboneApplication.CHILD_ID, childID + "");
                        bnd.putString("barcode", threadbarcode);
                        viewChild.putExtras(bnd);
                        startActivity(viewChild);

                    }

                } else {
                    Intent viewChild = new Intent(getApplicationContext(), ViewChildActivity.class);
                    Bundle bnd = new Bundle();
                    bnd.putString(BackboneApplication.CHILD_ID, threadTempId);
                    bnd.putString("barcode", threadbarcode);
                    viewChild.putExtras(bnd);
                    startActivity(viewChild);
                }
            }
        }.setData(barcode, fristname, lastname, bDate, gender, hfid, birthPlaceId, domId, addr, phone, motherFirstname, motherLastname, notes, userID, modOn, tempId,firstname2).start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
