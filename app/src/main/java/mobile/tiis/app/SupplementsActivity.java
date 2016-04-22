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
import android.content.BroadcastReceiver;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.helpers.Utils;

/**
* Created by Teodor on 3/21/2015.
*/
public class SupplementsActivity extends BackboneActivity implements View.OnClickListener {

    Button back, save;
    TextView row1Date, row2Date;
    TextView barcode, name, mother_name, dob, age;
    String bday, gender;
    CheckBox row1Cb, row2Cb;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    private String childId;
    private Thread thread;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.supplements_activity);
        app = (BackboneApplication) getApplication();
        initViews();
        initDb();
        renderViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(status_receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(status_receiver);
    }

    private void initViews(){
        row1Date = (TextView)findViewById(R.id.txt_date_vit_a_supplement_activity);
        row2Date = (TextView)findViewById(R.id.txt_date_mebend_supplement_activity);
        barcode = (TextView)findViewById(R.id.txt_barcode_supplement_activity);
        name = (TextView)findViewById(R.id.child_name_supplements_activity);
        mother_name = (TextView)findViewById(R.id.txt_mother_name_supplements_activity);
        dob = (TextView)findViewById(R.id.txt_dob_supplements_activity);
        age = (TextView)findViewById(R.id.vaccinate_age_supplements_activity);
        row1Cb = (CheckBox) findViewById(R.id.chk_vit_a_supplement_activity);
        row2Cb = (CheckBox) findViewById(R.id.chk_mebend_supplement_activity);
        save = (Button) findViewById(R.id.btn_save_button_supplement_activity);
        back = (Button) findViewById(R.id.btn_back_button_supplement_activity);
    }

    private void initDb(){
        mydb = app.getDatabaseInstance();
    }

    private void renderViews(){
        Date today = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        row1Date.setText(ft.format(today));
        row2Date.setText(ft.format(today));
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            barcode.setText(extras.getString("barcode"));
            name.setText(extras.getString("name"));
            mother_name.setText(extras.getString("mother_name"));
            dob.setText(extras.getString("dob"));
            bday = extras.getString("dob");
            gender = extras.getString("gender");
        }

        try{
            Date dNow = new Date( );
            String today_str = ft.format(dNow);
            Date date1 = ft.parse(bday);
            Date date2 = ft.parse(today_str);
            int month = getMonthsDifference(date1, date2);
            if(month != 0){
                age.setText(month + " months");
            }
            else{
                long diff = date2.getTime() - date1.getTime();
                long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                Log.d("", "The diff" + difference);
                age.setText(difference + " days");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE "+ SQLHandler.ChildColumns.BARCODE_ID+"=?",
                new String[] {String.valueOf(extras.getString("barcode"))});
        if(getChildIdCursor!=null && getChildIdCursor.getCount()>0) {
            getChildIdCursor.moveToFirst();
            childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
        }else{
            toastMessage(getString(R.string.empty_child_id));
            finish();
        }
        
        if(mydb.isChildSupplementedVitAToday(childId)){
            row1Cb.setChecked(true);
            row1Cb.setEnabled(false);
        }
        if(mydb.isChildSupplementedMebendezolrToday(childId)){
            row2Cb.setChecked(true);
            row2Cb.setEnabled(false);
        }
    }

    private void initListeners(){
        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v.getId()== R.id.btn_save_button_supplement_activity) {
            saveChildSupplements();
        }

        if(v.getId()==R.id.btn_back_button_supplement_activity){
            Intent weight = new Intent(getApplicationContext(), WeightActivity.class);
            weight.putExtra("barcode", barcode.getText().toString());
            weight.putExtra("firstname", name.getText().toString());
            weight.putExtra("birthdate", dob.getText().toString());
            weight.putExtra("gender", gender);
            weight.putExtra("motherFirstname", mother_name.getText().toString());
            startActivity(weight);
        }
    }

    private void saveChildSupplements(){
        boolean vitA = false, mebendezolr=false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert_empty_fields))
                .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog)dialog).dismiss();
                    }
                });

        vitA = row1Cb.isEnabled() && row1Cb.isChecked();
        mebendezolr = row2Cb.isEnabled() && row2Cb.isChecked();
        if(vitA || mebendezolr){
            final long insertedTodaySupplementRowId = mydb.inserTodaySupplements(childId, vitA, mebendezolr, app.getLOGGED_IN_USER_ID());
            if(insertedTodaySupplementRowId>0){
                alertDialogBuilder.setMessage(getString(R.string.supplement_data_saved));
                alertDialogBuilder.show();

                // tentojme te bejme save te dhenat ne server
                thread = new Thread() {
                    @Override
                    public void run() {
                        String url = prepareUrl().toString();
                        long newInserterdTodaySupplementsId= app.insertChildSupplementidChild(url);
                        if (newInserterdTodaySupplementsId>0) {
                            mydb.updateChildSupplementsNewid(insertedTodaySupplementRowId, newInserterdTodaySupplementsId);
                        }else{
                            mydb.addPost(url,-1);
                            Log.d("Save Edited Child", "Error while saving edited child "+childId);
                        }
                    }
                };
                thread.start();
            }
        }else{
            alertDialogBuilder.setMessage(getString(R.string.select_one_supplement));
            alertDialogBuilder.show();
        }
    }

    private StringBuilder prepareUrl(){
        boolean vitA, mebendezolr;
        vitA = row1Cb.isEnabled() && row1Cb.isChecked();
        mebendezolr = row2Cb.isEnabled() && row2Cb.isChecked();

        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.CHILD_SUPPLEMENTS_SVC).append(BackboneApplication.CHILD_SUPPLEMENTS_INSERT);
        webServiceUrl.append("?barcode=" + barcode.getText().toString());
        if(vitA)
            webServiceUrl.append("&vita=true");
        else
            webServiceUrl.append("&vita=false");
        if(mebendezolr)
            webServiceUrl.append("&mebendezol=true");
        else
            webServiceUrl.append("&mebendezol=false");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        webServiceUrl.append("&date="+format.format(date));
        webServiceUrl.append("&modifiedBy="+app.getLOGGED_IN_USER_ID());
        return webServiceUrl;
    }

    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.home_wifi_icon);
            if(Utils.isOnline(context)){
                wifi_logo.setBackgroundColor(0xff00ff00);
                app.setOnlineStatus(true);
            }
            else{
                wifi_logo.setBackgroundColor(0xffff0000);
                app.setOnlineStatus(false);
            }
        }
    };

    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1;
    }

}
