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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.util.ViewAppointmentRow;

/**
 * Created by Teodor on 3/8/2015.
 */

public class ViewAppointmentActivity extends BackboneActivity implements View.OnClickListener {

    //private TextView formatTxt, barcode;
    //private TextView firstname_str, gender_str, birthdate_str, motherFirstname_str, firstname, gender, birthdate, motherFirstname;
    private Button back;
    private Bundle extras;
    private String barcode, child_id, thedob="";
    private TextView name, mother_name, dob, age, barcodef;
    private ArrayList<ViewAppointmentRow> var;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.view_appointment_activity);

        barcodef=(TextView)findViewById(R.id.vaccinate_barcode);
        name=(TextView) findViewById(R.id.vaccinate_child_name);
        mother_name=(TextView)findViewById(R.id.vaccinate_mother_name);
        dob=(TextView)findViewById(R.id.vaccinate_dob);
        age=(TextView)findViewById(R.id.vaccinate_age);

        extras = getIntent().getExtras();
        if(extras!=null){
            barcode = extras.getString("barcode");
            barcodef.setText(barcode);
            name.setText(extras.getString("name"));
            mother_name.setText(extras.getString("mother_name"));
            dob.setText(extras.getString("dob"));
            thedob = extras.getString("dob");
        }
        try{
            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            String today = ft.format(dNow);
            Date date1 = ft.parse(thedob);
            Date date2 = ft.parse(today);
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


        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler this_database = app.getDatabaseInstance();
        Cursor cursor;
        SQLHandler handler = new SQLHandler();
        var = new ArrayList<ViewAppointmentRow>();
        String result="";
        child_id="";

        //Getting child_id
        cursor = this_database.getReadableDatabase().rawQuery("SELECT ID FROM child WHERE BARCODE_ID=?", new String[] {barcode});
        if(cursor!=null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            child_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            Log.d("ViewAppointment:", "Child_Id: " + child_id);

            cursor=this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[] {child_id, child_id});
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                        Log.d("ViewAppointment: id ", row.getAppointment_id());
                        row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                        Log.d("ViewAppointment: vaccines ", row.getVaccine_dose());
                        row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                        Log.d("ViewAppointment: schedule ", row.getSchedule());
                        row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                        Log.d("ViewAppointment: scheduled date ", row.getScheduled_date());
                        var.add(row);
                    } while (cursor.moveToNext());
                }
            }
        }

        //Show loading dialog
        ProgressDialog progressDialog =  new ProgressDialog(this, 0);
        progressDialog.setMessage("Loading table from database...");
        progressDialog.show();

        TableLayout layout = (TableLayout) findViewById(R.id.view_appointment_table_layout);

        for(final ViewAppointmentRow a: var){

            final Intent vaccinate = new Intent(this, AdministerVaccines2.class);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.setMargins(10, 10, 10, 10);
            row.setLayoutParams(lp);
            row.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           vaccinate.putExtra("barcode", barcode);
                                           vaccinate.putExtra("vaccines", a.getVaccine_dose());
                                           vaccinate.putExtra("name", name.getText().toString());
                                           vaccinate.putExtra("mother_name", mother_name.getText().toString());
                                           vaccinate.putExtra("dob", thedob);
                                           vaccinate.putExtra("age", age.getText());
                                           vaccinate.putExtra("childId", child_id);
                                           vaccinate.putExtra("origin", "view_appointment");
                                           vaccinate.putExtra("appointmentId", a.getAppointment_id());
                                           startActivity(vaccinate);
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

        back = (Button) findViewById(R.id.view_appointment_back_button);
        back.setOnClickListener(this);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_appointment_back_button:
                Intent av = new Intent(getApplicationContext(), ScanResultActivity.class);
                av.putExtra("result", barcode);
                av.putExtra("back", 1);
                startActivity(av);
                break;
            default:
                Log.d("Click", "Row pressed");
                break;
        }
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

    public ArrayList<ViewAppointmentRow> cleanObjects(ArrayList<ViewAppointmentRow> a){
        String check = "xxximpossible";
        ArrayList<ViewAppointmentRow> b = new ArrayList<ViewAppointmentRow>();
        for(ViewAppointmentRow object : a){
            if (object.getAppointment_id().equalsIgnoreCase(check)){
                ViewAppointmentRow mydemo = a.get(a.size() - 1);
                mydemo.concatDose_id(object.getDose_id());
                check=object.getAppointment_id();
                //a.remove(object);
            }
            else{
                ViewAppointmentRow demo = new ViewAppointmentRow();
                demo.setAppointment_id(object.getAppointment_id());
                demo.setScheduled_date(object.getScheduled_date());
                demo.setDose_id(object.getDose_id());
                b.add(demo);
                check=object.getAppointment_id();
                //a.remove(object);
            }
        }
        return b;
    }

    String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public ArrayList<ViewAppointmentRow> cleanDoseId(ArrayList<ViewAppointmentRow> a){
        for(ViewAppointmentRow b : a){
            HashSet hs = new HashSet();
            hs.addAll(b.getDose_id());
            b.getDose_id().clear();
            b.getDose_id().addAll(hs);
        }
        return a;
    }

    public void rowClicked(){

    }

    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1;
    }
}
