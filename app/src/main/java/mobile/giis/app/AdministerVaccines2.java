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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobile.giis.app.adapters.AdapterAdministerVaccines;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.AdministerVaccinesModel;

/**
 * Created by utente1 on 5/13/2015.
 */
public class AdministerVaccines2 extends BackboneActivity implements View.OnClickListener {

    private CheckBox cbOutreach;
    private EditText etNotes;
    ListView listView;
    CheckBox row1Cb, row2Cb;
    TextView row1Date, row2Date,tvSup;
    private TextView  barcode,tvWeightForToday;
    private TextView firstname_str, gender_str, birthdate_str,
            motherFirstname_str, firstname, gender, birthdate, motherFirstname, age;
    private Button save,back;


    String origin;
    ArrayList<AdministerVaccinesModel> arrayListAdminVacc;
    Date new_date;
    int counter = 0,DateDiffDialog = 0;
    long daysDiff;
    boolean starter_set = false;
    Date newest_date;
    private String date_difference_new = "", dose_number_new = "", appointment_id = "", barcode_st, name_st, mother_name_st, dob_st,childId,originnotes = "";
    private int dose_number_new_parsed = 0;
    private Thread thread;
    private BackboneApplication app;
    private DatabaseHandler dbh;
    private Boolean SavedState = false;
    private boolean outreach = false;
    ArrayList<String> dosekeeper;


    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView) findViewById(R.id.home_wifi_icon);
            if (mobile.giis.app.helpers.Utils.isOnline(context)) {
                wifi_logo.setBackgroundColor(0xff00ff00);
                app.setOnlineStatus(true);
            } else {
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

    public static final long getDaysDifference(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.HOUR,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.HOUR,0);
        c2.set(Calendar.MINUTE,0);
        c2.set(Calendar.SECOND,0);
        c2.set(Calendar.MILLISECOND,0);

        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return Math.abs(difference);
    }


    public static Date dateParser(String date_str) {
        Date date = null;
        Pattern pattern = Pattern.compile("\\((.*?)-");
        Pattern pattern_plus = Pattern.compile("\\((.*?)\\+");
        Matcher matcher = pattern.matcher(date_str);
        Matcher matcher_plus = pattern_plus.matcher(date_str);
        if (matcher.find()) {
            date = new Date(Long.parseLong(matcher.group(1)));
        } else if (matcher_plus.find()) {
            date = new Date(Long.parseLong(matcher_plus.group(1)));
        } else {
            date = new Date();
        }
        return date;
    }

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.administer_vacc2);

        tvSup = (TextView)findViewById(R.id.tv_sup);
        app = (BackboneApplication) getApplication();
        dbh = app.getDatabaseInstance();
        initViews();
        Date todayD = new Date();

        SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");
        row1Date.setText(ftD.format(todayD));
        row2Date.setText(ftD.format(todayD));

        arrayListAdminVacc = new ArrayList<AdministerVaccinesModel>();
        getBundleExtras();
        newest_date = new Date();


        for (String dose : dosekeeper) {
            final AdministerVaccinesModel adminVacc = dbh.getPartOneAdminVaccModel(starter_set, appointment_id, dose);
            starter_set = true;

            dbh.getPartTwoAdminVacc(adminVacc, daysDiff, DateDiffDialog);


            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            adminVacc.setTime(ft.format(newest_date));
            adminVacc.setTime2(newest_date);
            //rowObjects.setInput(ft.format(newest_date));
            //rowObjects.setDate(vaccination_date_col);

            arrayListAdminVacc.add(adminVacc);
        }


        AdapterAdministerVaccines adapterList = new AdapterAdministerVaccines(this,R.layout.item_listview_admin_vacc,arrayListAdminVacc,dob_st,1);

        listView.setAdapter(adapterList);


        DateDiffDialog();

        getChildId();

        if (dbh.isChildSupplementedVitAToday(childId)) {
            row1Cb.setChecked(true);
            row1Cb.setEnabled(false);
        }
        if (dbh.isChildSupplementedMebendezolrToday(childId)) {
            row2Cb.setChecked(true);
            row2Cb.setEnabled(false);
        }

    }

    public void DateDiffDialog(){
        switch (DateDiffDialog) {
            case 1:
                final AlertDialog ad22first = new AlertDialog.Builder(AdministerVaccines2.this).create();
                ad22first.setTitle(getString(R.string.warning));
                ad22first.setMessage(getString(R.string.too_early_vaccination));
                ad22first.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22first.dismiss();
                    }
                });
                ad22first.show();
                break;
            case 2:
                final AlertDialog ad22second = new AlertDialog.Builder(AdministerVaccines2.this).create();
                ad22second.setTitle(getString(R.string.warning));
                ad22second.setMessage(getString(R.string.too_late_vaccination));
                ad22second.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22second.dismiss();
                    }
                });
                ad22second.show();
                break;
            default:
                break;
        }

    }

    private void initViews() {

        listView = (ListView) findViewById(R.id.administer_vaccines_list_view);
        row1Date = (TextView) findViewById(R.id.txt_date_vit_a_supplement_activity);
        row2Date = (TextView) findViewById(R.id.txt_date_mebend_supplement_activity);
        barcode = (TextView) findViewById(R.id.vaccinate_barcode);
        firstname = (TextView) findViewById(R.id.vaccinate_child_name);
        motherFirstname = (TextView) findViewById(R.id.vaccinate_mother_name);
        birthdate = (TextView) findViewById(R.id.vaccinate_dob);
        age = (TextView) findViewById(R.id.vaccinate_age);
        tvWeightForToday = (TextView)findViewById(R.id.vaccinate_weight_for_today);
        etNotes = (EditText)findViewById(R.id.et_notes);
        cbOutreach = (CheckBox)findViewById(R.id.cb_outreach);
        row1Cb = (CheckBox) findViewById(R.id.chk_vit_a_vacc_adminis_activity);
        row2Cb = (CheckBox) findViewById(R.id.chk_mebend_vacc_adminis__activity);
        save = (Button) findViewById(R.id.vaccinate_save_button);
        back = (Button) findViewById(R.id.vaccinate_back_button);
        back.setOnClickListener(this);
        save.setOnClickListener(this);


    }

    public void getBundleExtras(){

        Bundle extras = getIntent().getExtras();
        String value = "";
        if (extras != null) {
            value = extras.getString("barcode");
            if (value != null) {
                barcode.setText(value);
                barcode_st = value;
            }

            value = extras.getString("name");
            if (value != null) {
                firstname.setText(value);
                name_st = value;
            }

            value = extras.getString("origin");
            if (value != null) {
                origin = value;
            }

            value = extras.getString("mother_name");
            if (value != null) {
                motherFirstname.setText(value);
                mother_name_st = value;
            }

            value = extras.getString("dob");
            if (value != null) {
                birthdate.setText(value);
                dob_st = value;
            }

            value = extras.getString("age");
            if (value != null) {
                age.setText(value);
            }

            appointment_id = extras.getString("appointmentId");
        }


        float weight = ((BackboneApplication) getApplication()).getDatabaseInstance().getWeightForToday(barcode_st);
        if(weight >-1){
            tvWeightForToday.setText(weight+" kg");
        }


        try {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            String today = ft.format(dNow);
            Date date1 = ft.parse(birthdate.getText().toString());
            Date date2 = ft.parse(today);
            int month = getMonthsDifference(date1, date2);
            daysDiff = getDaysDifference(date1, date2);
            if (month != 0) {
                age.setText(month + " months");
            } else {
                long diff = date2.getTime() - date1.getTime();
                long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                Log.d("", "The diff" + difference);
                age.setText(difference + " days");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        dosekeeper = dbh.getDosesForAppointmentID(appointment_id);

        if(age.getText().equals("9 months") || age.getText().equals("6 months") || age.getText().equals("18 months")){
            tvSup.setVisibility(View.VISIBLE);
        }
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

    private void getChildId() {
        Cursor getChildIdCursor = dbh.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                new String[]{String.valueOf(getIntent().getExtras().getString("barcode"))});
        if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
            getChildIdCursor.moveToFirst();
            childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            getChildIdCursor.close();
        } else {
            toastMessage(getString(R.string.empty_child_id));
            getChildIdCursor.close();
            finish();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vaccinate_save_button:
                saveButton();
                break;
            case R.id.vaccinate_back_button:
                backButton();
                break;

        }
    }

    private void saveChildSupplements() {
        boolean vitA = false, mebendezolr = false;
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.alert_empty_fields))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ((AlertDialog) dialog).dismiss();
//                    }
//                });

        vitA = row1Cb.isEnabled() && row1Cb.isChecked();
        mebendezolr = row2Cb.isEnabled() && row2Cb.isChecked();
        if (vitA || mebendezolr) {
            final long insertedChildSupplementsRowId = dbh.inserTodaySupplements(childId, vitA, mebendezolr, app.getLOGGED_IN_USER_ID());
            if (insertedChildSupplementsRowId > 0) {
//                alertDialogBuilder.setMessage(getString(R.string.supplement_data_saved));
//                alertDialogBuilder.show();

                // tentojme te bejme save te dhenat ne server
                thread = new Thread() {
                    @Override
                    public void run() {
                        String url = prepareUrlChildSupplements().toString();
                        long newInserterdTodaySupplementsId = app.insertChildSupplementidChild(url);
                        if (newInserterdTodaySupplementsId > 0) {
                            dbh.updateChildSupplementsNewid(insertedChildSupplementsRowId, newInserterdTodaySupplementsId);
                        } else {
                            dbh.addPost(url, -1);
                            Log.d("Save Edited Child", "Error while saving edited child " + childId);
                        }
                    }
                };
                thread.start();
            }
        } else {
//            alertDialogBuilder.setMessage(getString(R.string.select_one_supplement));
//            alertDialogBuilder.show();
        }
    }

    private StringBuilder prepareUrlChildSupplements() {
        boolean vitA, mebendezolr;
        vitA = row1Cb.isEnabled() && row1Cb.isChecked();
        mebendezolr = row2Cb.isEnabled() && row2Cb.isChecked();

        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.CHILD_SUPPLEMENTS_SVC).append(BackboneApplication.CHILD_SUPPLEMENTS_INSERT);
        webServiceUrl.append("?barcode=" + barcode.getText().toString());
        if (vitA)
            webServiceUrl.append("&vita=true");
        else
            webServiceUrl.append("&vita=false");
        if (mebendezolr)
            webServiceUrl.append("&mebendezol=true");
        else
            webServiceUrl.append("&mebendezol=false");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        webServiceUrl.append("&date=" + format.format(date));
        webServiceUrl.append("&modifiedBy=" + app.getLOGGED_IN_USER_ID());
        return webServiceUrl;
    }

    private class updateAdministerVaccine extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            int status = application.updateVaccinationEventOnServer(params[0]);
            Log.d("The status", status + "");
            String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
            try {
                dateTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Register Audit
            application.registerAudit(BackboneApplication.CHILD_AUDIT, params[1], dateTodayTimestamp,
                    application.getLOGGED_IN_USER_ID(), 7);

            //a.syncVaccines();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class updateBalance extends AsyncTask<AdministerVaccinesModel, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(AdministerVaccinesModel... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            for (AdministerVaccinesModel item : params) {
                Log.d("Updating balance", "");
                if (item.getStatus().equalsIgnoreCase("true")) {
                    Log.d("Starting update protocol", "");
                    Cursor cursor = db.getReadableDatabase().rawQuery("SELECT balance FROM health_facility_balance WHERE lot_id=?", new String[]{item.getVaccination_lot()});
                    //Cursor cursor = db.getReadableDatabase().rawQuery("UPDATE health_facility_balance SET balance = balance - 1 WHERE lot_id=?", new String[]{a.getVaccination_lot()});
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int bal = cursor.getInt(cursor.getColumnIndex("balance"));
                        Log.d("Balance found on database: ", bal + "");
                        bal = bal - 1;
                        Log.d("Balance being set: ", bal + "");
                        ContentValues cv = new ContentValues();
                        cv.put(SQLHandler.HealthFacilityBalanceColumns.BALANCE, bal);
                        db.updateStockBalance(cv, item.getVaccination_lot());
                        //cursor = db.getReadableDatabase().rawQuery("UPDATE health_facility_balance SET balance=? WHERE lot_id=?", new String[]{String.valueOf(bal), a.getVaccination_lot()});
                    }
                    //database.updateStockBalance(a.getDose_id());
                }
            }
            //a.syncVaccines();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class updateAppointmentOutreach extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            if(cbOutreach.isChecked() && arrayListAdminVacc!=null && arrayListAdminVacc.size()>0) {
                application.updateVaccinationAppOutreach(barcode_st, arrayListAdminVacc.get(0).getDose_id());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    public String calculateDateDiff(int daysDiff,AdministerVaccinesModel a) {
        return "/Date(" + (a.getTime2().getTime() + ((long)daysDiff*(long)86400000)) + "-0500)/";
    }

    public void backButton(){


        BackboneApplication app2 = (BackboneApplication) getApplication();
        DatabaseHandler database = app2.getDatabaseInstance();

        if (SavedState) {
            for (AdministerVaccinesModel a : arrayListAdminVacc) {

                updateAdministerVaccine task = new updateAdministerVaccine();
                task.execute(a.getUpdateURL(), barcode.getText().toString());

                updateAppointmentOutreach task2 = new updateAppointmentOutreach();
                task2.execute();

                updateBalance balance = new updateBalance();
                balance.execute(a);
            }
        }


        if (origin.equalsIgnoreCase("vaccination_queue")) {
            Intent av = new Intent(getApplicationContext(), VaccinationQueueActivity.class);
            av.putExtra("barcode", barcode_st);
            av.putExtra("name", name_st);
            av.putExtra("mother_name", mother_name_st);
            av.putExtra("dob", dob_st);
            startActivity(av);
        } else if (origin.equalsIgnoreCase("view_appointment")) {
            Intent av = new Intent(getApplicationContext(), ViewAppointmentActivity.class);
            av.putExtra("barcode", barcode_st);
            av.putExtra("name", name_st);
            av.putExtra("mother_name", mother_name_st);
            av.putExtra("dob", dob_st);
            startActivity(av);
        } else if (origin.equalsIgnoreCase("view_child")) {
            Intent av = new Intent(getApplicationContext(), ViewChildActivity.class);
            av.putExtra("barcode", barcode_st);
            av.putExtra("name", name_st);
            av.putExtra("mother_name", mother_name_st);
            av.putExtra("dob", dob_st);
            startActivity(av);
        } else if (origin.equalsIgnoreCase("monthly_plan")) {
            Intent av = new Intent(getApplicationContext(), MonthlyPlanActivity.class);
            av.putExtra("barcode", barcode_st);
            av.putExtra("name", name_st);
            av.putExtra("mother_name", mother_name_st);
            av.putExtra("dob", dob_st);
            startActivity(av);
        }


    }

    public void saveButton() {

        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        for (AdministerVaccinesModel a : arrayListAdminVacc) {

            if (a.getStatus().equalsIgnoreCase("true") && a.getVaccine_lot_list().get(a.getVaccination_lot_pos()).equalsIgnoreCase("-----")) {
                final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccines2.this).create();
                ad22.setTitle(getString(R.string.not_saved));
                ad22.setMessage("Please select vaccine lot");
                ad22.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22.dismiss();
                    }
                });
                ad22.show();
                return;
            }

            if (a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() == 0) {
                final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccines2.this).create();
                ad22.setTitle("Not Saved");
                ad22.setMessage("Please select a reason for not vaccinating child!");
                ad22.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22.dismiss();
                    }
                });
                ad22.show();
                return;
            }

            Log.d("VacLotID", a.getVaccine_lot_map().get(a.getVaccine_lot_list().get(a.getVaccination_lot_pos())).toString());
            //Only the rows where done is checked or a reason is selected should be saved and sent to server
            if (!(a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() == 0)) {
                ContentValues updateRow = new ContentValues();
                updateRow.put(SQLHandler.SyncColumns.UPDATED, 1);
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_STATUS, a.getStatus());
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINE_LOT_ID, a.getVaccine_lot_map().get(a.getVaccine_lot_list().get(a.getVaccination_lot_pos())).toString());
                updateRow.put(SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID, app.getLOGGED_IN_USER_HF_ID());
                if (!(etNotes.getText().toString().equalsIgnoreCase(""))) {
                    updateRow.put(SQLHandler.VaccinationEventColumns.NOTES, etNotes.getText().toString());
                }
                Log.d("Time sent to update database", "/Date(" + a.getTime2().getTime() + "-0500)/");
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_DATE, "/Date(" + a.getTime2().getTime() + "-0500)/");
                if (!(a.getNon_vac_reason().equalsIgnoreCase(""))) {
                    updateRow.put(SQLHandler.VaccinationEventColumns.NONVACCINATION_REASON_ID, a.getNon_vac_reason());
                }

                app.setUpdateURL(a, etNotes.getText().toString(), barcode.getText().toString());
                app.setAppointmentUpdateURL(a, appointment_id, cbOutreach);
                SavedState = true;
                Log.d("Saving appointment id" + appointment_id + " status", "dose id is: " + a.getDose_id());
                mydb.updateAdministerVaccineDoneStatus(updateRow, appointment_id, a.getDose_id());

                if (a.getDose_Number_Parsed() > 1 && (!a.getStatus().equalsIgnoreCase("false") ||
                        (a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() != 0 && !a.isKeep_child_due()))) {
                    Cursor crsCurrentAge = mydb.getReadableDatabase()
                            .rawQuery("Select DAYS from AGE_DEFINITIONS where ID in (select AGE_DEFINITON_ID from DOSE where ID=? )"
                                    , new String[]{ a.getDose_id()});
                    int currAgeDef = 0;
                    if(crsCurrentAge.moveToFirst()){
                        currAgeDef = Integer.parseInt(crsCurrentAge.getString(0));
                    }
                    crsCurrentAge.close();

                    Cursor crs = null;
                    crs = mydb.getReadableDatabase()
                            .rawQuery("SELECT vaccination_event.ID AS VACID, vaccination_event.DOSE_ID as DOSE_ID " +
                                    " FROM vaccination_event JOIN dose ON vaccination_event.DOSE_ID = dose.ID " +
                                    " WHERE dose.SCHEDULED_VACCINATION_ID = ? AND DOSE_NUMBER=? AND CHILD_ID=?"
                                    , new String[]{a.getScheduled_Vaccination_Id(), String.valueOf(a.getDose_Number_Parsed()), childId});

                    if (crs.moveToFirst()) {
                        do {
                            Log.d("Query", " is Working");
                            ContentValues cv = new ContentValues();
                            cv.put("IS_ACTIVE", "true");
                            Cursor crsNextAge = mydb.getReadableDatabase()
                                    .rawQuery("Select DAYS from age_definitions where ID in (select AGE_DEFINITON_ID from dose where ID=? )"
                                            , new String[]{ crs.getString(crs.getColumnIndex("DOSE_ID"))});
                            int nextAgeDef = 0;
                            if(crsNextAge.moveToFirst()){
                                nextAgeDef = Integer.parseInt(crsNextAge.getString(0));
                            }
                            crsNextAge.close();
                            int dayDiff = nextAgeDef - currAgeDef;
                            cv.put("SCHEDULED_DATE", calculateDateDiff(dayDiff, a));
                            //cv.put("SCHEDULED_DATE", a.calculateDateDiff());
                            String vaccination_event_id = crs.getString(crs.getColumnIndex("VACID"));
                            mydb.updateAdministerVaccineSchedule(cv, vaccination_event_id);
                        } while (crs.moveToNext());
                    }

                    crs.close();
                }
            }
        }


        saveChildSupplements();

        final AlertDialog ad2 = new AlertDialog.Builder(AdministerVaccines2.this).create();
        ad2.setTitle("Saved");
        ad2.setMessage(getString(R.string.changes_saved));
        ad2.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad2.dismiss();
            }
        });
        ad2.show();
    }
}
