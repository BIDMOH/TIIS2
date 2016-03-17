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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Teodor on 1/31/2015.
 */
public class ScanResultActivity extends BackboneActivity implements View.OnClickListener {

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
    private TextView formatTxt, barcode;
    private TextView firstname_str, gender_str, birthdate_str, motherFirstname_str, firstname, gender, birthdate, motherFirstname;
    private Button weight, modify, vaccinate;
    private String childId;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.scan_result_activity);

        barcode = (TextView) findViewById(R.id.scan_result_barcode);
        firstname = (TextView) findViewById(R.id.scan_result_child_name);
        birthdate = (TextView) findViewById(R.id.scan_result_dob);
        gender = (TextView) findViewById(R.id.scan_result_gender);
        motherFirstname = (TextView) findViewById(R.id.scan_result_mother_name);
        firstname_str = (TextView) findViewById(R.id.scan_result_firstname_str);
        birthdate_str = (TextView) findViewById(R.id.scan_result_dob_str);
        gender_str = (TextView) findViewById(R.id.scan_result_gender_str);
        motherFirstname_str = (TextView) findViewById(R.id.scan_result_motherFirstname_str);

        weight = (Button) findViewById(R.id.scan_result_weight_button);
        modify = (Button) findViewById(R.id.scan_result_modify_button);
        vaccinate = (Button) findViewById(R.id.scan_result_vaccinate_button);


        Bundle extras = getIntent().getExtras();
        String value = "";
        int onBack = 0;
        if (extras != null) {
            value = extras.getString("result");
            barcode.setText(value);
            onBack = extras.getInt("back");
        } else {
            barcode.setText("NULL");
        }

        BackboneApplication app = (BackboneApplication) getApplication();


        DatabaseHandler mydb = app.getDatabaseInstance();

        String dateNow = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        if (mydb.isChildInChildWeightToday(value, dateNow)) {

            weight.setVisibility(View.GONE);

        }

        Cursor cursor = null;
        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{value});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            app.setAdministerVaccineHidden(false);
            childId = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            firstname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1))
                    + " " + cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2)) + " "
                    + cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1)));

            Date bdate = dateParser(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHDATE)));
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            birthdate.setText(ft.format(bdate));
            if (Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.GENDER)))) {
                gender.setText("Male");
            } else {
                gender.setText("Female");
            }

            motherFirstname.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME))
                    + " "
                    + cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME)));
        }
        //Child not found in local database
        else if (!mydb.isChildInDB(barcode.getText().toString())) {

            firstname.setVisibility(View.GONE);
            birthdate.setVisibility(View.GONE);
            gender.setVisibility(View.GONE);
            motherFirstname.setVisibility(View.GONE);
            firstname_str.setVisibility(View.GONE);
            birthdate_str.setVisibility(View.GONE);
            gender_str.setVisibility(View.GONE);
            motherFirstname_str.setVisibility(View.GONE);
            modify.setVisibility(View.GONE);
            if (Utils.isOnline(this)) {
                vaccinate.setVisibility(View.GONE);
            }

            app.setAdministerVaccineHidden(true);

            if (onBack == 0) {
                final AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setTitle(getString(R.string.not_found));
                ad.setMessage(getString(R.string.barcode_not_found_local));
                ad.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad.show();
            }


        }
        cursor.close();

        weight.setOnClickListener(this);
        modify.setOnClickListener(this);
        vaccinate.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.scan_result_weight_button:
                Intent weight = new Intent(getApplicationContext(), WeightActivity.class);
                weight.putExtra("barcode", barcode.getText().toString());
                weight.putExtra("firstname", firstname.getText().toString());
                weight.putExtra("birthdate", birthdate.getText().toString());
                weight.putExtra("gender", gender.getText().toString());
                weight.putExtra("motherFirstname", motherFirstname.getText().toString());
                startActivity(weight);
                break;
            case R.id.scan_result_modify_button:
                Intent modify = new Intent(getApplicationContext(), ViewChildActivity.class);
                modify.putExtra("barcode", barcode.getText().toString());
                modify.putExtra("firstname", firstname.getText().toString());
                modify.putExtra("birthdate", birthdate.getText().toString());
                modify.putExtra("motherFirstname", motherFirstname.getText().toString());
                modify.putExtra("dob", birthdate.getText().toString());
                modify.putExtra("gender", gender.getText().toString());
                modify.putExtra(((BackboneApplication) getApplication()).CHILD_ID, childId);
                startActivity(modify);
                break;
            case R.id.scan_result_vaccinate_button:
                Bundle extras = getIntent().getExtras();
                BackboneApplication app = (BackboneApplication) getApplication();
                DatabaseHandler mydb = app.getDatabaseInstance();
                if (!Utils.isOnline(this) && !mydb.isChildInDB(barcode.getText().toString())) {
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

                break;
        }
    }


}
