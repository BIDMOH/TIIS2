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

package mobile.tiis.app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.helpers.Utils;

public class WeightActivity extends BackboneActivity implements View.OnClickListener {

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
    private TextView barcode;
    private EditText weight_input_dec, weight_input_comma;
    private TextView firstname, gender, birthdate, motherFirstname, date,tvpriviousDate,tvpriviousWeight;
    private TextView firstname_str, gender_str, birthdate_str, motherFirstname_str, date_str;
    private Button save, back, supplements, vaccinate, modify;
    private String today, birthday, mfgender, weight_value;
    private Child selectedChild;
    private boolean isWeightSetForChild = false;
    private String origine_activity = "ScanHandler";
    HashMap<String,String> dateAndWeight;
    LinearLayout lnPreviousDateAndWeight;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.weight_activity);

        barcode = (TextView) findViewById(R.id.weight_barcode);
        firstname = (TextView) findViewById(R.id.weight_child_name);
        birthdate = (TextView) findViewById(R.id.weight_dob);
        gender = (TextView) findViewById(R.id.weight_gender);
        weight_input_dec = (EditText) findViewById(R.id.weight_weight_dec);
        weight_input_comma = (EditText) findViewById(R.id.weight_weight_comma);
        motherFirstname = (TextView) findViewById(R.id.weight_mother_name);
        firstname_str = (TextView) findViewById(R.id.weight_firstname_str);
        birthdate_str = (TextView) findViewById(R.id.weight_dob_str);
        gender_str = (TextView) findViewById(R.id.weight_gender_str);
        motherFirstname_str = (TextView) findViewById(R.id.weight_motherFirstname_str);
        date_str = (TextView) findViewById(R.id.weight_date_str);
        date = (TextView) findViewById(R.id.weight_date);
        tvpriviousDate = (TextView)findViewById(R.id.date_weight);
        tvpriviousWeight = (TextView)findViewById(R.id.previous_weight);
        lnPreviousDateAndWeight = (LinearLayout)findViewById(R.id.linDateAndWeight);


        supplements = (Button) findViewById(R.id.weight_supplements_button);
        vaccinate = (Button) findViewById(R.id.weight_vaccinate_button);
        modify = (Button) findViewById(R.id.weight_modify_button);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        date.setText(ft.format(dNow));
        today = ft.format(dNow);

        Bundle extras = getIntent().getExtras();
        if (extras.getSerializable("currentChild") != null) {
            origine_activity = "ViewChild";
            selectedChild = (Child) extras.getSerializable("currentChild");
            barcode.setText(selectedChild.getBarcodeID());
            if (selectedChild.getFirstname1() != null && !selectedChild.getFirstname1().isEmpty()) {
                firstname.setText(selectedChild.getFirstname1() + " " + selectedChild.getFirstname2() + " " + selectedChild.getLastname1());
            } else {
                firstname.setVisibility(View.GONE);
                firstname_str.setVisibility(View.GONE);
            }
            if (selectedChild.getGender() != null) {

                if (selectedChild.getGender().equalsIgnoreCase("true")) {
                    mfgender = "M";
                    gender.setText("Male");
                } else {
                    mfgender = "F";
                    gender.setText("Female");
                }
            } else {
                gender.setVisibility(View.GONE);
                gender_str.setVisibility(View.GONE);
            }
            if (selectedChild.getBirthdate() != null && !selectedChild.getBirthdate().isEmpty()) {
                birthdate.setText(ft.format(dateParser(selectedChild.getBirthdate())));
                birthday = ft.format(dateParser(selectedChild.getBirthdate()));
            } else {
                birthdate.setVisibility(View.GONE);
                birthdate_str.setVisibility(View.GONE);
            }
            if (selectedChild.getMotherFirstname() != null && !selectedChild.getMotherFirstname().isEmpty()) {
                motherFirstname.setText(selectedChild.getMotherFirstname() + " " + selectedChild.getMotherLastname());
            } else {
                motherFirstname.setVisibility(View.GONE);
                motherFirstname_str.setVisibility(View.GONE);
            }
        } else {
            String value;
            if (extras != null) {
                value = extras.getString("barcode");
                if (!Utils.isStringBlank(value)) {
                    barcode.setText(value);
                }

                value = extras.getString("firstname");
                if (!Utils.isStringBlank(value)) {
                    firstname.setText(value);
                } else {
                    firstname.setVisibility(View.GONE);
                    firstname_str.setVisibility(View.GONE);
                }

                value = extras.getString("birthdate");
                if (!Utils.isStringBlank(value)) {
                    birthdate.setText(value);
                    birthday = value;
                } else {
                    birthdate.setVisibility(View.GONE);
                    birthdate_str.setVisibility(View.GONE);
                }
                value = extras.getString("gender");
                if (!Utils.isStringBlank(value)) {
                    gender.setText(value);
                    if (value.equalsIgnoreCase("male")) {
                        mfgender = "M";
                    } else {
                        mfgender = "F";
                    }

                } else {
                    gender.setVisibility(View.GONE);
                    gender_str.setVisibility(View.GONE);
                }
                value = extras.getString("motherFirstname");
                if (!Utils.isStringBlank(value)) {
                    motherFirstname.setText(value);
                } else {
                    motherFirstname.setVisibility(View.GONE);
                    motherFirstname_str.setVisibility(View.GONE);
                }

            }
        }
        Log.d("Weight Activity", "Creating the activity");
        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        Cursor cursor = null;
        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child_weight WHERE CHILD_BARCODE=? " +
                " AND datetime(DATE, 'unixepoch') <= datetime('now')", new String[]{barcode.getText().toString()});
        if (cursor.getCount() > 0) {
            isWeightSetForChild = true;
            Log.d(" ", "Cursor count" + cursor.getCount());
            cursor.moveToFirst();
            String weight = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildWeightColumns.WEIGHT));
            Log.d(" ", "Weight" + weight);
            weight_input_dec.setText(weight.split("\\.")[0]);
            weight_input_comma.setText(weight.split("\\.")[1]);
        } else {
            if (app.getAdministerVaccineHidden()) {
                modify.setVisibility(View.GONE);
                supplements.setVisibility(View.GONE);
            }

        }

//        if(!Utils.isStringBlank(weight_input_dec.getText().toString())){
//            LinearLayout buttonSet2 = (LinearLayout) findViewById(R.id.weight_button_set_2);
//            buttonSet2.setVisibility(View.VISIBLE);
//        }


        if (!Utils.isStringBlank(weight_input_dec.getText().toString())) {
            LinearLayout buttonSet2 = (LinearLayout) findViewById(R.id.weight_button_set_2);
            buttonSet2.setVisibility(View.VISIBLE);
            if (app.getAdministerVaccineHidden()) {
                modify.setVisibility(View.GONE);
                vaccinate.setVisibility(View.GONE);
//                LinearLayout modify = (LinearLayout)findViewById(R.id.modify_layout);
//                modify.setVisibility(View.GONE);
//                LinearLayout vaccinate = (LinearLayout)findViewById(R.id.vaccinate_layout);
//                vaccinate.setVisibility(View.GONE);
            }

        }

//        if(!Utils.isOnline(this) && Utils.isStringBlank(barcode.getText().toString())){
//            modify.setVisibility(View.GONE);
//            supplements.setVisibility(View.GONE);
//        }


        save = (Button) findViewById(R.id.weight_save_button);
        back = (Button) findViewById(R.id.weight_back_button);
        dateAndWeight = mydb.getPreviousDateAndWeight(barcode.getText().toString());
        if(dateAndWeight!=null)
        {
            SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = new Date(Long.parseLong(dateAndWeight.get("Date"))*1000);
            String previousDate = myFormat.format(date);
            tvpriviousDate.setText(previousDate);
            tvpriviousWeight.setText(dateAndWeight.get("Weight"));
        }
        else{

            lnPreviousDateAndWeight.setVisibility(View.GONE);
        }
        save.setOnClickListener(this);
        back.setOnClickListener(this);
        supplements.setOnClickListener(this);
        vaccinate.setOnClickListener(this);
        modify.setOnClickListener(this);

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

    public void onClick(View v) {
        if (v.getId() == R.id.weight_save_button) {
            if (Utils.isStringBlank((weight_input_dec.getText().toString())) || weight_input_dec.getText().toString().substring(0, 1).equals("0")) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);
                ad.setTitle(getString(R.string.warning));
                ad.setMessage(getString(R.string.weight_not_correct));
                ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // this will solve your error
                AlertDialog alert = ad.create();
                alert.show();
                alert.getWindow().getAttributes();

                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                textView.setTextSize(30);
            } else {
                if (!isWeightSetForChild) {
                    isWeightSetForChild = true;
                    updateWeight((weight_input_dec.getText().toString()) + "." + (weight_input_comma.getText().toString().trim().equals("") ? "00" : weight_input_comma.getText().toString()));
                    LinearLayout buttonSet1 = (LinearLayout) findViewById(R.id.weight_button_set_1);
                    LinearLayout buttonSet2 = (LinearLayout) findViewById(R.id.weight_button_set_2);

                    //buttonSet1.setVisibility(View.GONE);
                    buttonSet2.setVisibility(View.VISIBLE);
//                weight_input_comma.setEnabled(false);
//                weight_input_dec.setEnabled(false);

                    if (!Utils.isStringBlank(birthday) && !Utils.isStringBlank((weight_input_dec.getText().toString()))) {
                        long difference;
                        String sd3neg, sd2neg, sd3, sd2;
                        try {
                            SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            Date date1 = myFormat.parse(birthday);
                            Date date2 = myFormat.parse(today);
                            long diff = date2.getTime() - date1.getTime();
                            difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                            Log.d("", "The diff" + difference);

                            BackboneApplication app = (BackboneApplication) getApplication();
                            DatabaseHandler mydb = app.getDatabaseInstance();
                            Cursor cursor = null;

                            cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM weight WHERE DAY=? AND GENDER=?", new String[]{String.valueOf(difference), String.valueOf(mfgender)});
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                sd3 = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD3));
                                sd2 = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD2));
                                sd3neg = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD3NEG));
                                sd2neg = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD2NEG));

                                String str = "0";
                                String message = "";

                                weight_value = weight_input_dec.getText().toString() + "." + weight_input_comma.getText().toString();
                                if (Double.parseDouble(weight_value) <= Double.parseDouble(sd3neg)) {
                                    str = "-3";
                                } else if (Double.parseDouble(weight_value) <= Double.parseDouble(sd2neg)) {
                                    str = "-2";
                                } else if (Double.parseDouble(weight_value) >= Double.parseDouble(sd3)) {
                                    str = "3";
                                } else if (Double.parseDouble(weight_value) >= Double.parseDouble(sd2)) {
                                    str = "2";
                                } else if (Double.parseDouble(sd2neg) < Double.parseDouble(weight_value) && Double.parseDouble(weight_value) < Double.parseDouble(sd2)) {
                                    str = "OK";
                                }

                                switch (str) {
                                    case "-3":
                                        message = (getString(R.string.child_significantly_underweight));
                                        break;
                                    case "-2":
                                        message = (getString(R.string.child_weight_too_low));
                                        break;
                                    case "3":
                                        message = (getString(R.string.child_weight_too_hight));
                                        break;
                                    case "2":
                                        message = (getString(R.string.child_weight_too_hight));
                                        break;
                                    case "OK":
                                        message = (getString(R.string.normal_child_weight));
                                        break;
                                    default:
                                        message = (getString(R.string.error_ocurred));
                                        break;
                                }

                                final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);

                                ad.setTitle(getString(R.string.weight_analysis));
                                ad.setMessage(message);
                                ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                // this will solve your error
                                AlertDialog alert = ad.create();
                                alert.show();
                                alert.getWindow().getAttributes();

                                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                                textView.setTextSize(30);

                            } else {
                                final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);
                                ad.setTitle(getString(R.string.weight_analysis));
                                ad.setMessage(getString(R.string.weight_analyzer_not_reached));

                                ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                // this will solve your error
                                AlertDialog alert = ad.create();
                                alert.show();
                                alert.getWindow().getAttributes();

                                TextView textView = (TextView) alert.findViewById(android.R.id.message);
                                textView.setTextSize(30);
                            }


                        } catch (ParseException e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Weight Analyser stopped!", Toast.LENGTH_SHORT);
                            toast.show();

                            e.printStackTrace();
                        }
                    }


                    //@Arinela Server Update

                    String dateToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());

                    BackboneApplication app = (BackboneApplication) getApplication();
                    new Thread() {
                        String threadDateToday
                                ,
                                threadDateModON
                                ,
                                threadbarcode
                                ,
                                threadWeight
                                , threadModBy;

                        public Thread setData(String threadbarcode, String threadDateToday, String threadDateModON, String threadWeight, String threadModBy) {
                            try {
                                this.threadDateToday = URLEncoder.encode(threadDateToday, "utf-8");
                                this.threadDateModON = URLEncoder.encode(threadDateModON, "utf-8");
                                this.threadbarcode = threadbarcode;
                                this.threadModBy = threadModBy;
                                this.threadWeight = threadWeight;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            return this;
                        }

                        @Override
                        public void run() {


                            synchronized (this) {


                                BackboneApplication backbone = (BackboneApplication) getApplication();
                                backbone.saveWeight(threadbarcode,
                                        threadDateToday, threadDateModON, threadWeight,
                                        threadModBy);
                                //Register Audit
                                backbone.registerAudit(BackboneApplication.CHILD_AUDIT, threadbarcode, threadDateModON,
                                        backbone.getLOGGED_IN_USER_ID(), 6);

                            }

                        }
                    }.setData(barcode.getText().toString()
                            , dateToday
                            , dateTodayTimestamp
                            , weight_input_dec.getText().toString() + "." + (weight_input_comma.getText().toString().trim().equals("") ? "00" : weight_input_comma.getText().toString())
                            , app.getLOGGED_IN_USER_ID()).start();

                    final DatabaseHandler mydb = app.getDatabaseInstance();
                    String childID = mydb.getChildIdByBarcode(barcode.getText().toString());

                    if (childID != null) {
                        if (mydb.isChildToBeAddedInVaccinationQueue(childID)) {


                            ContentValues cv = new ContentValues();
                            cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childID);
                            cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateTodayTimestamp);

                            if (mydb.addChildToVaccinationQueue(cv) > -1) {

                                // hfid should never be null since a child allways has to have a hfid
                                String hfid = mydb.getChildHFIDByChildId(childID + "");


                                new Thread() {
                                    String thredHfid
                                            ,
                                            threadTodayTimestamp;

                                    public Thread setDataToServer(String thredHfid, String threadTodayTimestamp) {
                                        try {
                                            this.threadTodayTimestamp = URLEncoder.encode(threadTodayTimestamp, "utf-8");
                                            this.thredHfid = thredHfid;
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        return this;
                                    }
                                    @Override
                                    public void run() {
                                        synchronized (this) {
                                            BackboneApplication app = (BackboneApplication) getApplication();
                                            app.updateVaccinationQueue(barcode.getText().toString(), app.getLOGGED_IN_USER_HF_ID(), threadTodayTimestamp, app.getLOGGED_IN_USER_ID());
                                        }

                                    }
                                }.setDataToServer(hfid, dateTodayTimestamp).start();


                            }
                        }


                    }


                } else {
                    showAlertThatChildHasWeightInDB();
                }
            }
        }
        if (v.getId() == R.id.weight_back_button) {
            Bundle extras = getIntent().getExtras();
            Intent back;
            if (origine_activity.equalsIgnoreCase("ScanHandler")) {
                back = new Intent(this, ScanHandlerActivity.class);
                startActivity(back);
            } else if (origine_activity.equalsIgnoreCase("ViewChild"))
            {
                back = new Intent(this, ViewChildActivity.class);
                back.putExtra("barcode", selectedChild.getBarcodeID());
                startActivity(back);
            }

            //back.putExtra("result", extras.getString("barcode"));

        }

        if (v.getId() == R.id.weight_supplements_button) {
            Intent supplements = new Intent(this, SupplementsActivity.class);
            supplements.putExtra("barcode", barcode.getText().toString());
            supplements.putExtra("name", firstname.getText().toString());
            supplements.putExtra("mother_name", motherFirstname.getText().toString());
            supplements.putExtra("dob", birthdate.getText().toString());
            supplements.putExtra("gender", gender.getText().toString());
            startActivity(supplements);
        }
        if (v.getId() == R.id.weight_vaccinate_button) {
            Bundle extras = getIntent().getExtras();
            BackboneApplication app = (BackboneApplication) getApplication();
            if (!Utils.isOnline(this) && Utils.isStringBlank(barcode.getText().toString())) {
                Intent vaccinate = new Intent(this, AdministerVaccinesOfflineActivity.class);
                vaccinate.putExtra("barcode", barcode.getText().toString());
                startActivity(vaccinate);
            } else if (app.getAdministerVaccineHidden()) {
                Intent vaccinate = new Intent(this, AdministerVaccinesOfflineActivity.class);
                vaccinate.putExtra("barcode", barcode.getText().toString());
                startActivity(vaccinate);
            } else {
                Intent vaccinate = new Intent(this, ViewAppointmentActivity.class);
                vaccinate.putExtra("barcode", barcode.getText().toString());
                vaccinate.putExtra("name", firstname.getText().toString());
                vaccinate.putExtra("mother_name", motherFirstname.getText().toString());
                vaccinate.putExtra("dob", birthdate.getText().toString());
                startActivity(vaccinate);
            }

        }

        if (v.getId() == R.id.weight_modify_button) {
            Intent vaccinate = new Intent(this, ViewChildActivity.class);
            vaccinate.putExtra("barcode", barcode.getText().toString());
            startActivity(vaccinate);
        }
    }

    public void show() {

        final Dialog d = new Dialog(WeightActivity.this);
        d.setTitle("Weight Picker");
        d.setContentView(R.layout.weight_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np.setMaxValue(100); // max value 100
        np.setMinValue(0);   // min value 0
        np2.setMaxValue(100); // max value 100
        np2.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np2.setWrapSelectorWheel(false);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //weight_input.setText(String.valueOf(np.getValue()) + "," + String.valueOf(np2.getValue())); //set the value to textview
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

    public void updateWeight(String weight) {
        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        ContentValues child = new ContentValues();
        child.put(SQLHandler.SyncColumns.UPDATED, 1);
        child.put(SQLHandler.ChildWeightColumns.WEIGHT, weight);
        child.put(SQLHandler.ChildWeightColumns.DATE,Calendar.getInstance().getTimeInMillis()/1000);
        child.put(SQLHandler.ChildWeightColumns.CHILD_BARCODE, barcode.getText().toString());
        Cursor cursor = null;

        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child_weight WHERE CHILD_BARCODE=? ", new String[]{String.valueOf(barcode.getText())});
        if (cursor.getCount() > 0) {
            mydb.updateWeight(child, barcode.getText().toString());
            final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);
            ad.setMessage(getString(R.string.weight_updated));
            ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // this will solve your error
            AlertDialog alert = ad.create();
            alert.show();
            alert.getWindow().getAttributes();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            textView.setTextSize(30);
        } else {
            mydb.addChildWeight(child);
            final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);
            ad.setMessage(getString(R.string.weight_registered));
            ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // this will solve your error
            AlertDialog alert = ad.create();
            alert.show();
            alert.getWindow().getAttributes();

            TextView textView = (TextView) alert.findViewById(android.R.id.message);
            textView.setTextSize(30);
        }

    }

    private void showAlertThatChildHasWeightInDB() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(WeightActivity.this);

        ad.setTitle(getString(R.string.weight_analysis));
        ad.setMessage(getString(R.string.child_weight_already_entered));
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isWeightSetForChild = false;
                // as a workaround i just call the on click programmatically since the code that manages te actions is in the on click of the save button
                save.callOnClick();
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
}
