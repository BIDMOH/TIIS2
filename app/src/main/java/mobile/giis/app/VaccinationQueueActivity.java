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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.GIISContract;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.fragments.FragmentVaccineNameQuantity;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.util.Constants;
import mobile.giis.app.util.ViewAppointmentRow;

/**
 * Created by Teodor on 2/8/2015.
 */
public class VaccinationQueueActivity extends BackboneActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    String child_id;
    TextView tvTodayDate;
    Button btnNameQuantity;
    Spinner spAgeDefinitions;
    BackboneApplication app;
    DatabaseHandler this_database;
    ArrayList<VQAgeDefinitions> ageDef;
    TableLayout layout;
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
    private ArrayList<ViewAppointmentRow> var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaccination_queue_activity);

        layout = (TableLayout) findViewById(R.id.view_vacc_table_layout);

        spAgeDefinitions = (Spinner)findViewById(R.id.sp_age_definitions);

        btnNameQuantity = (Button) findViewById(R.id.btn_vacc_names_quantities);
        btnNameQuantity.setOnClickListener(this);

        app = (BackboneApplication) getApplication();
        this_database = app.getDatabaseInstance();
        ageDef = getAllAgeDeffinitions();

        ArrayList<String> ageDefStr = new ArrayList<>();
        ageDefStr.add("--------");
        for(VQAgeDefinitions vqad : ageDef){
            ageDefStr.add(vqad.getName());
        }

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(this,R.layout.single_text_spinner_item_drop_down,ageDefStr);
        spAgeDefinitions.setAdapter(dataAdapter);
        spAgeDefinitions.setOnItemSelectedListener(this);

        tvTodayDate = (TextView) findViewById(R.id.tv_today_date);
        tvTodayDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()));


    }

    private void compileVaccinationQueueTable(String ageId) {

        layout.removeAllViews();

        Cursor cursor;
        var = new ArrayList<ViewAppointmentRow>();
        String result="";
        child_id="";

        //Getting child_id
        child_id = this_database.getAllChilcIdInVaccinationQueue();
        if(child_id!=null) {
            String SQLVaccinationQueue =
                    "SELECT v.APPOINTMENT_ID, v.CHILD_ID," +
                            "(SELECT GROUP_CONCAT(dose.FULLNAME)" +
                            " FROM vaccination_event INNER JOIN dose" +
                            " ON vaccination_event.DOSE_ID = dose.ID left join age_definitions on dose.TO_AGE_DEFINITON_ID = age_definitions.ID" +
                            " WHERE CHILD_ID in ("+child_id+")" +
                            " AND v.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID" +
                            " AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+"+ Constants.EligibleForVaccinationVal+" days')" +
                            " AND vaccination_event.IS_ACTIVE='true'" +
                            " AND vaccination_event.VACCINATION_STATUS='false'" +
                            " AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) AND (DAYS IS NULL or\n" +
                            "(datetime(substr(vaccination_event.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || DAYS || ' days')) ))" +
                            " AS VACCINES, " +
                            "a.NAME AS SCHEDULE, " +
                            "v.SCHEDULED_DATE  " +
                            "FROM vaccination_event v INNER JOIN dose " +
                            "ON v.DOSE_ID = dose.ID INNER JOIN age_definitions a" +
                            " ON dose.AGE_DEFINITON_ID=a.ID" +
                            " WHERE v.CHILD_ID in ("+child_id+")" +
                            " AND datetime(substr(v.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+"+ Constants.EligibleForVaccinationVal+" days')" +
                            " AND v.IS_ACTIVE='true'" +
                            " AND v.VACCINATION_STATUS='false'" +
                            " AND (v.NONVACCINATION_REASON_ID=0  OR v.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) " +
                            "AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                            " OR (datetime(substr(v.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                            ((!ageId.equals(""))?" AND a.ID = "+ ageId :"")+
                            " GROUP BY v.APPOINTMENT_ID, v.SCHEDULED_DATE, a.NAME " +
                            " ORDER BY v.SCHEDULED_DATE";
            cursor = this_database.getReadableDatabase().rawQuery(SQLVaccinationQueue, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                        row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                        row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                        row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                        row.setChild_id(cursor.getString(cursor.getColumnIndex("CHILD_ID")));
                        var.add(row);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        else{

            //Toast there are no children
        }

        //Show loading dialog
        ProgressDialog progressDialog =  new ProgressDialog(this, 0);
        progressDialog.setMessage("Loading table from database...");
        progressDialog.show();


        for(final ViewAppointmentRow a: var){



            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);
            row.setLayoutParams(lp);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAdministerVaccinesActivity(a,a.getChild_id());
                }


            });

            TextView name = new TextView(this);
            name.setTextColor(Color.parseColor("#333333"));
            name.setBackgroundColor(Color.parseColor("#33819fd1"));
            name.setGravity(Gravity.CENTER_HORIZONTAL);
            name.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f));
            name.setPadding(10,10,0,10);
            //name.setHeight(100);
            //name.setWidth(200);

            DatabaseHandler db = app.getDatabaseInstance();
            Cursor naming = null;
            naming = db.getReadableDatabase().rawQuery("SELECT FIRSTNAME1 , LASTNAME1,FIRSTNAME2 FROM child WHERE ID=?", new String[]{a.getChild_id()});
            if (naming != null) {
                if (naming.moveToFirst()) {
                    name.setText(naming.getString(naming.getColumnIndex("FIRSTNAME1"))+" " + naming.getString(naming.getColumnIndex("FIRSTNAME2")) +" "+naming.getString(naming.getColumnIndex("LASTNAME1")));
                }
            }

            TextView vac_dose = new TextView(this);
            vac_dose.setTextColor(Color.parseColor("#333333"));
            vac_dose.setBackgroundColor(Color.parseColor("#33819fd1"));
            vac_dose.setGravity(Gravity.CENTER_HORIZONTAL);
            vac_dose.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f));
//            vac_dose.setHeight(100);
//            vac_dose.setWidth(250);
            vac_dose.setPadding(10,10,0,10);
            vac_dose.setText(a.getVaccine_dose());

            TextView schedule = new TextView(this);
            schedule.setTextColor(Color.parseColor("#333333"));
            schedule.setBackgroundColor(Color.parseColor("#33819fd1"));
            schedule.setGravity(Gravity.CENTER_HORIZONTAL);
            schedule.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f));
            //schedule.setHeight(100);
            //schedule.setWidth(150);
            schedule.setPadding(10,10,0,10);
            schedule.setText(a.getSchedule());

            TextView sch_date = new TextView(this);
            sch_date.setTextColor(Color.parseColor("#333333"));
            sch_date.setBackgroundColor(Color.parseColor("#33819fd1"));
            sch_date.setGravity(Gravity.CENTER_HORIZONTAL);
            sch_date.setLayoutParams(new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,1.0f));
            //sch_date.setHeight(100);
            //sch_date.setWidth(200);
            sch_date.setPadding(10,10,0,10);

            Date scheduled_date = dateParser(a.getScheduled_date());
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            sch_date.setText(ft.format(scheduled_date));

            row.addView(name);
            row.addView(vac_dose);
            row.addView(schedule);
            row.addView(sch_date);
            layout.addView(row);
        }
        progressDialog.dismiss();
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

    private ArrayList<VQAgeDefinitions> getAllAgeDeffinitions(){

        ArrayList<VQAgeDefinitions> list = new ArrayList<>();
        String SQLAgeDef = "SELECT * from "+ SQLHandler.Tables.AGE_DEFINITIONS;
        Cursor cursor = this_database.getReadableDatabase().rawQuery(SQLAgeDef, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    VQAgeDefinitions row = new VQAgeDefinitions();
                    row.setId(cursor.getString(cursor.getColumnIndex(GIISContract.AgeDefinitionsTable.ID)));
                    row.setName(cursor.getString(cursor.getColumnIndex(GIISContract.AgeDefinitionsTable.NAME)));
                    list.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0) {
            compileVaccinationQueueTable(ageDef.get(position - 1).getId());
        }else{
            compileVaccinationQueueTable("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void startAdministerVaccinesActivity(ViewAppointmentRow a, String child_id) {

        DatabaseHandler db = app.getDatabaseInstance();
        Intent vaccinate = new Intent(this, AdministerVaccines2.class);
        Cursor c = db.getReadableDatabase().rawQuery("SELECT * , substr("+GIISContract.ChildTable.BIRTHDATE+" ,7,13) as dob FROM child WHERE ID IN ('" + child_id + "')",null);
        if (c != null) {
            if (c.moveToFirst()) {
                vaccinate.putExtra("barcode", c.getString(c.getColumnIndex(GIISContract.ChildTable.BARCODE_ID)));
                vaccinate.putExtra("name", c.getString(c.getColumnIndex(GIISContract.ChildTable.FIRSTNAME1))+" " +c.getString(c.getColumnIndex(GIISContract.ChildTable.FIRSTNAME2))+ " "+c.getString(c.getColumnIndex(GIISContract.ChildTable.LASTNAME1)));
                vaccinate.putExtra("mother_name", c.getString(c.getColumnIndex(GIISContract.ChildTable.MOTHER_FIRSTNAME))+" "+c.getString(c.getColumnIndex(GIISContract.ChildTable.MOTHER_LASTNAME)));
                String d = c.getString(c.getColumnIndex("dob"));

                String dob = new SimpleDateFormat("dd-MMM-yyyy").format(new Date(Long.parseLong(c.getString(c.getColumnIndex("dob")))));
                vaccinate.putExtra("dob",dob);
            }
            c.close();
        }

        vaccinate.putExtra("vaccines", a.getVaccine_dose());
        vaccinate.putExtra("childId", child_id);
        //setting origine for Back button
        vaccinate.putExtra("origin", "vaccination_queue");
        vaccinate.putExtra("appointmentId", a.getAppointment_id());
        startActivity(vaccinate);
    }

    @Override
    public void onClick(View v) {

        // this method fills the list that should after this be shown in a view(listview , gridview or anything)
        ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> list = this_database.getQuantityOfVaccinesNeededVaccinationQueue();
        if(list != null) {
            // Create and show the dialog.
            FragmentVaccineNameQuantity newFragment = new FragmentVaccineNameQuantity();
            newFragment.setListVacineNameQuantity(list);
            newFragment.show(getSupportFragmentManager(), "dialogNameQuantity");
        }
    }

    private class VQAgeDefinitions{
        private String name , id;

        private VQAgeDefinitions() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
