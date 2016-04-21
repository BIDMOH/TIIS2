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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.NonVaccinationReason;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by Teodor on 3/2/2015.
 */
public class AdministerVaccinesActivity extends BackboneActivity implements View.OnClickListener {

    String vaccination_status = "", nonvaccination_reason = "", dose_id = "", vaccination_event_id;
    int vac_lot_pos;
    ArrayList<RowCollector> b;
    ArrayList<RowObjects> objects;
    Date new_date;
    int counter = 0;
    CheckBox row1Cb, row2Cb;
    TextView row1Date, row2Date;
    int DateDiffDialog = 0;
    long daysDiff;
    boolean starter_set = false;
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
    Date newest_date;
    private String date_difference_new = "", dose_number_new = "";
    private int dose_number_new_parsed = 0;
    private Thread thread;
    private TextView formatTxt, barcode,tvWeightForToday;
    private TextView firstname_str, gender_str, birthdate_str,
            motherFirstname_str, firstname, gender, birthdate, motherFirstname, age;
    private Button save;
    private String appointment_id = "", barcode_st, name_st, mother_name_st, dob_st;
    private BackboneApplication app;
    private DatabaseHandler dbh;
    private String childId;
    private String origin;
    private Boolean SavedState = false;

    private CheckBox cbOutreach;
    private EditText etNotes;
    private boolean outreach = false;
    private String notes = "";

    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1;
    }

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
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
        setContentView(R.layout.administer_vaccines_activity);

        row1Date = (TextView) findViewById(R.id.txt_date_vit_a_supplement_activity);
        row2Date = (TextView) findViewById(R.id.txt_date_mebend_supplement_activity);

        Date todayD = new Date();
        SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");
        row1Date.setText(ftD.format(todayD));
        row2Date.setText(ftD.format(todayD));

        barcode = (TextView) findViewById(R.id.vaccinate_barcode);
        firstname = (TextView) findViewById(R.id.vaccinate_child_name);
        motherFirstname = (TextView) findViewById(R.id.vaccinate_mother_name);
        birthdate = (TextView) findViewById(R.id.vaccinate_dob);
        age = (TextView) findViewById(R.id.vaccinate_age);
        tvWeightForToday = (TextView)findViewById(R.id.vaccinate_weight_for_today);
        etNotes = (EditText)findViewById(R.id.et_notes);
        cbOutreach = (CheckBox)findViewById(R.id.cb_outreach);

        b = new ArrayList<RowCollector>();
        objects = new ArrayList<RowObjects>();

        Bundle extras = getIntent().getExtras();
        String value = "";
        String doselist = "";
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

            value = extras.getString("vaccines");
            if (value != null) {
                doselist = value;
            }

            value = extras.getString("age");
            if (value != null) {
                age.setText(value);
            }

            appointment_id = extras.getString("appointmentId");
        }
        newest_date = new Date();

        float weight = ((BackboneApplication) getApplication()).getDatabaseInstance().getWeightForToday(barcode_st);
        if(weight >-1){
            tvWeightForToday.setText(weight+"Kg");
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


        TableLayout layout = (TableLayout) findViewById(R.id.administer_vaccines_table_layout);
        String[] dosekeeper;
        dosekeeper = doselist.split(",");

        //Create dynamic array
//        List<String> dynamicVaccines = new ArrayList<String>();
//        for(String a : dosekeeper){
//            dynamicVaccines.add(a);
//        }
//        String noTimeConstrainDoses = "";
//
//        app = (BackboneApplication) getApplication();
//        dbh = app.getDatabaseInstance();
//        getChildId();
//        Cursor newVaccineCollectionNoTimeConstrain = dbh.getReadableDatabase().rawQuery(SQLHandler.NoTimeConstraintVaccines, new String[]{childId, childId});
//        if(newVaccineCollectionNoTimeConstrain.getCount() > 0){
//            newVaccineCollectionNoTimeConstrain.moveToFirst();
//            noTimeConstrainDoses = newVaccineCollectionNoTimeConstrain.getString(newVaccineCollectionNoTimeConstrain.getColumnIndex("VACCINES"));
//        }
//
//        String [] newdoses = noTimeConstrainDoses.split(",");
//        for(String newdose : newdoses){
//            if(!dynamicVaccines.contains(newdose)){
//                dynamicVaccines.add(newdose);
//            }
//        }





        for (String dose : dosekeeper) {

            app = (BackboneApplication) getApplication();
            dbh = app.getDatabaseInstance();
            Cursor cursor = null;
            final RowCollector rowCollector = new RowCollector();
            //final RowObjects rowObjects = new RowObjects();

            if (!starter_set) {
                rowCollector.setStarter_row(true);
                starter_set = true;
            }

            cursor = dbh.getReadableDatabase().rawQuery("SELECT vaccination_event.ID AS VID, VACCINATION_STATUS, SCHEDULED_DATE AS SCHDT, SCHEDULED_VACCINATION_ID, DOSE_ID, DOSE_NUMBER AS DN FROM vaccination_event INNER JOIN dose " +
                    "ON vaccination_event.DOSE_ID = dose.ID WHERE APPOINTMENT_ID=? AND dose.FULLNAME=?", new String[]{appointment_id, dose});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
//                vaccination_status = cursor.getString(cursor.getColumnIndex("VACCINATION_STATUS"));
                dose_id = cursor.getString(cursor.getColumnIndex("DOSE_ID"));
                vaccination_event_id = cursor.getString(cursor.getColumnIndex("VID"));

                String scheduled_date = cursor.getString(cursor.getColumnIndex("SCHDT"));
                rowCollector.setScheduled_Date_field(scheduled_date);

                String dose_number = cursor.getString(cursor.getColumnIndex("DN"));
                rowCollector.setDose_Number_field(dose_number);

                String scheduled_vaccination_id = cursor.getString(cursor.getColumnIndex("SCHEDULED_VACCINATION_ID"));
                rowCollector.setScheduled_Vaccination_Id(scheduled_vaccination_id);

                try {
                    rowCollector.setDose_Number_Parsed(Integer.parseInt(rowCollector.getDose_Number_field()) + 1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            rowCollector.setDose_id(dose_id);
            rowCollector.setVacc_ev_id(vaccination_event_id);

            //No dialog to be shown
            //DateDiffDialog = 0;
            // daysDiff stored days difference

            String scheduled_vaccination_id, item_id;
            Map<String, String> vac_lot_map = new HashMap<String, String>();
            List<String> lot_name = new ArrayList<String>();


            cursor = dbh.getReadableDatabase().rawQuery("SELECT FROM_AGE_DEFINITON_ID AS FAID, TO_AGE_DEFINITON_ID AS TAID, SCHEDULED_VACCINATION_ID FROM dose WHERE ID=?", new String[]{rowCollector.getDose_id()});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String faid = cursor.getString(cursor.getColumnIndex("FAID"));
                String taid = cursor.getString(cursor.getColumnIndex("TAID"));

                scheduled_vaccination_id = cursor.getString(cursor.getColumnIndex("SCHEDULED_VACCINATION_ID"));
                Log.d("Scheduled vacc id", scheduled_vaccination_id);

                Cursor cursor2 = dbh.getReadableDatabase().rawQuery("SELECT ITEM_ID FROM scheduled_vaccination WHERE ID=?", new String[]{scheduled_vaccination_id});
                if (cursor2.getCount() > 0) {
                    cursor2.moveToFirst();
                    item_id = cursor2.getString(cursor2.getColumnIndex("ITEM_ID"));

                    cursor = dbh.getReadableDatabase().rawQuery("SELECT '-1' AS id, '-----' AS lot_number, datetime('now') as expire_date UNION " +
                            "SELECT '-2' AS id, 'No Lot' AS lot_number, datetime('now') as expire_date UNION " +
                            "SELECT item_lot.id, item_lot.lot_number, datetime(substr(item_lot.expire_date,7,10), 'unixepoch') FROM item_lot  join health_facility_balance ON item_lot.ID = health_facility_balance.lot_id WHERE item_lot.item_id = ? " +
                            "AND balance > 0 AND datetime(substr(item_lot.expire_date,7,10), 'unixepoch') >= datetime('now') ORDER BY expire_date", new String[]{item_id});
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                Log.d("", "Adding to map" + cursor.getString(cursor.getColumnIndex("lot_number")));
                                vac_lot_map.put(cursor.getString(cursor.getColumnIndex("lot_number")), cursor.getString(cursor.getColumnIndex("id")));
                                lot_name.add(cursor.getString(cursor.getColumnIndex("lot_number")));
                            } while (cursor.moveToNext());
                            rowCollector.setVaccine_lot_map(vac_lot_map);
                            rowCollector.setVaccine_lot_list(lot_name);
                        }
                    }
                }

                try {

                    if (faid != null && Integer.parseInt(faid) > 0) {
                        String days;
                        cursor = dbh.getReadableDatabase().rawQuery("SELECT DAYS FROM age_definitions WHERE ID=?", new String[]{faid});
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            days = cursor.getString(cursor.getColumnIndex("DAYS"));
                            Log.d("faid", "condition true ? false" + daysDiff + " < " + Long.parseLong(days));
                            if (daysDiff < Long.parseLong(days)) {

                                if (DateDiffDialog == 0) {
                                    DateDiffDialog = 1;
                                }
                            }
                        }
                    } else if (taid != null && Integer.parseInt(taid) > 0) {
                        String days;
                        cursor = dbh.getReadableDatabase().rawQuery("SELECT DAYS FROM age_definitions WHERE ID=?", new String[]{taid});
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            days = cursor.getString(cursor.getColumnIndex("DAYS"));
                            if (daysDiff > Long.parseLong(days)) {
                                if (DateDiffDialog == 0) {
                                    DateDiffDialog = 2;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            //New row creation
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT);
            lp.setMargins(10, 10, 10, 10);
            row.setLayoutParams(lp);


            //Vaccination Lot column
            final Spinner vaccination_lot_col = new Spinner(this);

//            lot_name.add("----");
//            lot_name.add("No Lot");
            SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, lot_name);
            vaccination_lot_col.setAdapter(statusAdapter);
            vaccination_lot_col.setMinimumWidth(180);
            if (lot_name.size() > 2) {
                vaccination_lot_col.setSelection(2);
                rowCollector.setVaccination_lot_pos(2);
                //setting the id of vaccine lot
                rowCollector.setVaccination_lot(rowCollector.getVaccine_lot_map().get(rowCollector.getVaccine_lot_list().get(2)).toString());
                Log.d("RowCollId", rowCollector.getVaccination_lot());
            } else {
                vaccination_lot_col.setSelection(1);
                rowCollector.setVaccination_lot_pos(1);
                rowCollector.setVaccination_lot(rowCollector.getVaccine_lot_map().get(rowCollector.getVaccine_lot_list().get(1)).toString());
                Log.d("RowCollId", rowCollector.getVaccination_lot());
            }

            //rowCollector.setVaccination_lot_pos(1);
            vac_lot_pos = 1;
            vaccination_lot_col.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    vaccination_lot_col.setSelection(position);
                    vac_lot_pos = position;
                    rowCollector.setVaccination_lot_pos(position);
                    rowCollector.setVaccination_lot(rowCollector.getVaccine_lot_map().get(rowCollector.getVaccine_lot_list().get(position)).toString());
                    Log.d("RowCollId", rowCollector.getVaccination_lot());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });


            //Vaccination dose column
            final TextView vaccination_dose_col = new TextView(this);
            vaccination_dose_col.setTextColor(Color.parseColor("#333333"));
            vaccination_dose_col.setGravity(Gravity.START);
            vaccination_dose_col.setHeight(100);
            vaccination_dose_col.setWidth(140);
            vaccination_dose_col.setText(dose);
            vaccination_dose_col.setPadding(10, 0, 0, 0);


            //Vaccination Date Column TextView
            final TextView vaccination_date_col = new TextView(this);
            vaccination_date_col.setTextColor(Color.parseColor("#333333"));
            vaccination_date_col.setGravity(Gravity.START);
            vaccination_date_col.setHeight(100);
            vaccination_date_col.setWidth(200);
            vaccination_date_col.setPadding(40, 0, 0, 0);
            vaccination_date_col.setId(counter);


            vaccination_date_col.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show(vaccination_date_col, rowCollector);
                    Log.d("Time after show done", rowCollector.getTime());
                }
            });
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            vaccination_date_col.setText(ft.format(newest_date));
            rowCollector.setTime(ft.format(newest_date));
            rowCollector.setTime2(newest_date);
            //rowObjects.setInput(ft.format(newest_date));
            //rowObjects.setDate(vaccination_date_col);


            //NonVaccinationReason Column Spinner
            List<String> reasons = new ArrayList<String>();
            reasons.add("----");
            for (NonVaccinationReason nvElement : dbh.getAllNonvaccinationReasons()) {
                reasons.add(nvElement.getName());
            }


            final List<NonVaccinationReason> non_vaccination_reason_list_with_additions = dbh.getAllNonvaccinationReasons();
            NonVaccinationReason empty = new NonVaccinationReason();
            empty.setName("----");
            empty.setId("0");
            non_vaccination_reason_list_with_additions.add(empty);


            final Spinner nonvaccination_reason_col = new Spinner(this);
            final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, reasons);
            nonvaccination_reason_col.setAdapter(statusAdapterNonVaccinationReason);
            nonvaccination_reason_col.setSelection(0);
            nonvaccination_reason_col.setGravity(Gravity.START);
            rowCollector.setNon_vac_reason_pos(0);
            nonvaccination_reason_col.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    nonvaccination_reason_col.setSelection(position);
                    rowCollector.setNon_vac_reason_pos(position);
                    for(NonVaccinationReason a : non_vaccination_reason_list_with_additions)
                    {
                        if(statusAdapterNonVaccinationReason.getItem(position).toString().equalsIgnoreCase(a.getName())){
                            rowCollector.setNon_vac_reason(a.getId());
                        }
                    }

//                    if (position == 0) {
//                        rowCollector.setNon_vac_reason("0");
//                    }
//                    if (position == 1) {
//                        rowCollector.setNon_vac_reason("29");
//                    }
//                    if (position == 2) {
//                        rowCollector.setNon_vac_reason("30");
//                    }
//                    if (position == 3) {
//                        rowCollector.setNon_vac_reason("31");
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });
            nonvaccination_reason_col.setVisibility(View.GONE);
            nonvaccination_reason_col.setMinimumWidth(220);


            //Done Status Column CheckBox
            final CheckBox vaccination_status_col = new CheckBox(this);
            vaccination_status_col.setChecked(true);
            vaccination_status_col.setGravity(Gravity.START);
            vaccination_status_col.setWidth(50);
            vaccination_status_col.setButtonDrawable(R.drawable.checkbox);
            vaccination_status_col.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    rowCollector.setStatus(String.valueOf(b));
                    //Toast.makeText(AdministerVaccinesActivity.this, "Value changed to " + b, Toast.LENGTH_SHORT).show();
                    if (!b) {
                        nonvaccination_reason_col.setVisibility(View.VISIBLE);
                        vaccination_lot_col.setSelection(0);
                        rowCollector.setNon_vac_reason_pos(0);

                    }
                    if (b) {
                        nonvaccination_reason_col.setVisibility(View.GONE);
                        rowCollector.setNon_vac_reason("-1");
                    }
                }
            });




            //Adding dose name column
            row.addView(vaccination_dose_col);
            //Adding vaccine lot column
            row.addView(vaccination_lot_col);
            //Adding vaccination date column
            row.addView(vaccination_date_col);
            //Adding vaccination done status column
            row.addView(vaccination_status_col);
            //Adding nonvaccination reason column
            row.addView(nonvaccination_reason_col);
            //Displaying row
            layout.addView(row);

            //Add row to Row Collector for later calling Save/Remove
            //rowCollector.setUpdateURL();
            //objects.add(rowObjects);
            b.add(rowCollector);
            counter++;
        }

        switch (DateDiffDialog) {
            case 1:
                final AlertDialog ad22first = new AlertDialog.Builder(AdministerVaccinesActivity.this).create();
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
                final AlertDialog ad22second = new AlertDialog.Builder(AdministerVaccinesActivity.this).create();
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
        row1Cb = (CheckBox) findViewById(R.id.chk_vit_a_vacc_adminis_activity);
        row2Cb = (CheckBox) findViewById(R.id.chk_mebend_vacc_adminis__activity);

        getChildId();
        if (dbh.isChildSupplementedVitAToday(childId)) {
            row1Cb.setChecked(true);
            row1Cb.setEnabled(false);
        }
        if (dbh.isChildSupplementedMebendezolrToday(childId)) {
            row2Cb.setChecked(true);
            row2Cb.setEnabled(false);
        }

        save = (Button) findViewById(R.id.vaccinate_save_button);
        Button back = (Button) findViewById(R.id.vaccinate_back_button);
        back.setOnClickListener(this);
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

                BackboneApplication app = (BackboneApplication) getApplication();
                DatabaseHandler mydb = app.getDatabaseInstance();
                for (RowCollector a : b) {

                    if (a.getStatus().equalsIgnoreCase("true") && a.getVaccine_lot_list().get(a.getVaccination_lot_pos()).equalsIgnoreCase("-----")) {
                        final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccinesActivity.this).create();
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
                        final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccinesActivity.this).create();
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
                        if(!(etNotes.getText().toString().equalsIgnoreCase(""))){
                            updateRow.put(SQLHandler.VaccinationEventColumns.NOTES, etNotes.getText().toString());
                        }
                        Log.d("Time sent to update database", "/Date(" + a.getTime2().getTime() + "-0500)/");
                        updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_DATE, "/Date(" + a.getTime2().getTime() + "-0500)/");
                        if (!(a.getNon_vac_reason().equalsIgnoreCase(""))) {
                            updateRow.put(SQLHandler.VaccinationEventColumns.NONVACCINATION_REASON_ID, a.getNon_vac_reason());
                        }

                        a.setUpdateURL();
                        a.setAppointmentUpdateURL();
                        SavedState = true;
                        Log.d("Saving appointment id" + appointment_id + " status", "dose id is: " + a.getDose_id());
                        mydb.updateAdministerVaccineDoneStatus(updateRow, appointment_id, a.getDose_id());

                        if (a.getDose_Number_Parsed() > 1 && !(a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() == 0)) {
                            Cursor crs = null;
                            crs = mydb.getReadableDatabase().rawQuery("SELECT vaccination_event.ID AS VACID, vaccination_event.SCHEDULED_DATE as SCHDATE FROM vaccination_event JOIN dose ON vaccination_event.DOSE_ID = dose.ID WHERE dose.SCHEDULED_VACCINATION_ID = ? AND DOSE_NUMBER=? AND CHILD_ID=?", new String[]{a.getScheduled_Vaccination_Id(), String.valueOf(a.getDose_Number_Parsed()), childId});
                            Log.d("Checking query AdminsterVaccine", "SELECT vaccination_event.ID FROM vaccination_event JOIN dose ON vaccination_event.DOSE_ID = dose.ID WHERE dose.SCHEDULED_VACCINATION_ID = ? AND DOSE_NUMBER=? AND CHILD_ID=?" + a.getScheduled_Vaccination_Id() + " " + String.valueOf(a.getDose_Number_Parsed()) + " " + childId);

                            if (crs.getCount() > 0) {
                                if (crs.moveToFirst()) {
                                    do {
                                        Log.d("Query", " is Working");
                                        ContentValues cv = new ContentValues();
                                        cv.put("IS_ACTIVE", "true");
                                        cv.put("SCHEDULED_DATE", a.calculateDateDiff(crs.getString(crs.getColumnIndex("SCHDATE"))));
                                        //cv.put("SCHEDULED_DATE", a.calculateDateDiff());
                                        String vaccination_event_id = crs.getString(crs.getColumnIndex("VACID"));
                                        Log.d("Updating vaccination event with id", vaccination_event_id);
                                        mydb.updateAdministerVaccineSchedule(cv, vaccination_event_id);
                                    } while (crs.moveToNext());
                                }
                            }
                            crs.close();
                        }
                    }
                }

//                ContentValues appointmentOutreachContentVal = new ContentValues();
//                appointmentOutreachContentVal.put(SQLHandler.VaccinationAppointmentColumns.OUTREACH, String.valueOf(cbOutreach.isChecked()));
//                getChildId();
//                try{
//                    mydb.updateVaccinationAppointementOutreach(appointmentOutreachContentVal, childId, appointment_id);
//                }catch (NullPointerException e){
//                    e.printStackTrace();
//                }



                saveChildSupplements();

                final AlertDialog ad2 = new AlertDialog.Builder(AdministerVaccinesActivity.this).create();
                ad2.setTitle("Saved");
                ad2.setMessage(getString(R.string.changes_saved));
                ad2.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad2.dismiss();
                    }
                });
                ad2.show();
                break;

            case R.id.vaccinate_back_button:
                BackboneApplication app2 = (BackboneApplication) getApplication();
                DatabaseHandler database = app2.getDatabaseInstance();

                if (SavedState) {
                    for (RowCollector a : b) {

                        updateAdministerVaccine task = new updateAdministerVaccine();
                        task.execute(a.getUpdateURL());
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
                break;

//            case R.id.vaccinate_remove_button:
//
//                break;
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

    public Date show(final TextView a, final RowCollector coll) {
        final Dialog d = new Dialog(AdministerVaccinesActivity.this);
        d.setTitle("Date Picker");
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
        dp.setMaxDate(new Date().getTime());
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date date = ft.parse(dob_st);
            dp.setMinDate(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    //setText(Integer.toString(dp.getDayOfMonth()) + "/" + Integer.toString(dp.getMonth() + 1) + "/" + Integer.toString(dp.getYear()));
                    calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                    new_date = calendar.getTime();
                    //Date dNow = new Date();
                    if (getDaysDifference(new_date, newest_date) > 0) {
                        coll.setTime(ft.format(new_date));
                        a.setText(ft.format(new_date));
                        coll.setTime2(new_date);
                        int cc = 0;
                        if (coll.getStarter_row()) {
                            for (RowCollector others : b) {
                                others.setTime(ft.format(new_date));
                                others.setTime2(new_date);
                                TextView t = (TextView) findViewById(cc);
                                t.setText(ft.format(new_date));
                                cc++;
                            }
                        }
                    } else {
                        coll.setTime(ft.format(newest_date));
                        a.setText(ft.format(newest_date));
                        coll.setTime2(newest_date);
                        int cc = 0;
                        if (coll.getStarter_row()) {
                            for (RowCollector others : b) {
                                others.setTime(ft.format(newest_date));
                                others.setTime2(newest_date);
                                TextView t = (TextView) findViewById(cc);
                                t.setText(ft.format(newest_date));
                                cc++;
                            }
                        }
                    }
                }catch(Exception e){
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
        return new_date;
    }

    public class RowCollector {

        //id of the dose name
        private String dose_id;
        //status of vaccination done
        private String status = "true";
        //VaccinationEvent id for the row
        private String vacc_ev_id = "";
        //Vaccination Date
        private String time;
        private Date time2;
        //Non vaccination reason
        private String non_vac_reason = "0";
        //Vaccine lot picked
        private String vaccination_lot_id = "-2";
        private String updateURL = "";
        private String updateURLAppointment = "";
        private int vaccination_lot_pos;
        private int non_vac_reason_pos;
        private Map vaccine_lot_map;
        private List<String> vaccine_lot_list;

        private String Scheduled_Date_field = "";
        private String Dose_Number_field = "";
        private int Dose_Number_Parsed = 0;
        private String Scheduled_Vaccination_Id = "";

        private TextView automation_date;
        private String automation_date_string;

        private String new_date_difference;

        private Boolean starter_row = false;


        public RowCollector() {

        }

        public TextView getAutomation_date() {
            return automation_date;
        }

        public void setAutomation_date(TextView automation_date) {
            this.automation_date = automation_date;
        }

        public String getAutomation_date_string() {
            return automation_date_string;
        }

        public void setAutomation_date_string(String automation_date_string) {
            this.automation_date_string = automation_date_string;
        }

        public String getNew_date_difference() {
            return new_date_difference;
        }

        public void setNew_date_difference(String new_date_difference) {
            this.new_date_difference = new_date_difference;
        }

        public Boolean getStarter_row() {
            return starter_row;
        }

        public void setStarter_row(Boolean starter_row) {
            this.starter_row = starter_row;
        }

        public String getDose_Number_field() {
            return Dose_Number_field;
        }

        public void setDose_Number_field(String dose_Number_field) {
            Dose_Number_field = dose_Number_field;
        }

        public int getDose_Number_Parsed() {
            return Dose_Number_Parsed;
        }

        public void setDose_Number_Parsed(int dose_Number_Parsed) {
            Dose_Number_Parsed = dose_Number_Parsed;
        }

        public String getScheduled_Date_field() {
            return Scheduled_Date_field;
        }

        public void setScheduled_Date_field(String scheduled_Date_field) {
            Scheduled_Date_field = scheduled_Date_field;
        }

        public String getScheduled_Vaccination_Id() {
            return Scheduled_Vaccination_Id;
        }

        public void setScheduled_Vaccination_Id(String scheduled_Vaccination_Id) {
            Scheduled_Vaccination_Id = scheduled_Vaccination_Id;
        }

        public Date getTime2() {
            return time2;
        }

        public void setTime2(Date time2) {
            this.time2 = time2;
        }

        public List<String> getVaccine_lot_list() {
            return vaccine_lot_list;
        }

        public void setVaccine_lot_list(List<String> vaccine_lot_list) {
            this.vaccine_lot_list = vaccine_lot_list;
        }

        public Map getVaccine_lot_map() {
            return vaccine_lot_map;
        }

        public void setVaccine_lot_map(Map vaccine_lot_map) {
            this.vaccine_lot_map = vaccine_lot_map;
        }

        public String getDose_id() {
            return dose_id;
        }

        public void setDose_id(String dose_id) {
            this.dose_id = dose_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            Log.d("Setting status", status);
            this.status = status;
        }

        public int getNon_vac_reason_pos() {
            return non_vac_reason_pos;
        }

        public void setNon_vac_reason_pos(int non_vac_reason_pos) {
            this.non_vac_reason_pos = non_vac_reason_pos;
        }

        public int getVaccination_lot_pos() {
            return vaccination_lot_pos;
        }

        public void setVaccination_lot_pos(int vaccination_lot_pos) {
            this.vaccination_lot_pos = vaccination_lot_pos;
        }

        public String getVacc_ev_id() {
            return vacc_ev_id;
        }

        public void setVacc_ev_id(String vacc_ev_id) {
            this.vacc_ev_id = vacc_ev_id;
        }

        public String getVaccination_lot() {
            return vaccination_lot_id;
        }

        public void setVaccination_lot(String vaccination_lot) {
            this.vaccination_lot_id = vaccination_lot;
        }

        public String getNon_vac_reason() {
            return non_vac_reason;
        }

        public void setNon_vac_reason(String non_vac_reason) {
            this.non_vac_reason = non_vac_reason;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUpdateURL() {
            return updateURL;
        }

        public String getAppointmentUpdateURL() {
            return updateURLAppointment;
        }

        public void setUpdateURL() {
            BackboneApplication app = (BackboneApplication) getApplication();
            SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
            String vnotes = "";
            if (!(etNotes.getText().toString().equalsIgnoreCase(""))) {
                vnotes = etNotes.getText().toString();
            }
            final StringBuilder VaccinationEventUpdateURL = new StringBuilder(WCF_URL + "VaccinationEvent.svc/UpdateVaccinationEventBarcodeAndDoseId?")
                    .append("barcode=").append(barcode.getText().toString())
                    .append("&doseId=").append(dose_id)
                    .append("&vaccineLotId=").append(vaccination_lot_id).append("&healthFacilityId=").append(app.getLOGGED_IN_USER_HF_ID())
                    .append("&vaccinationDate=").append(URLEncoder.encode(formatted.format(time2)))
                    .append("&notes=").append(vnotes)
                    .append("&vaccinationStatus=").append(status)
                    .append("&nonvaccinationReasonId=").append(non_vac_reason)
                    .append("&userId=").append(app.getLOGGED_IN_USER_ID());
            //.append("&vaccinationEventId=").append(vacc_ev_id);

            this.updateURL = VaccinationEventUpdateURL.toString();
        }

        public void setAppointmentUpdateURL() {
            BackboneApplication app = (BackboneApplication) getApplication();
            SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

            final StringBuilder VaccinationAppointmentUpdateURL = new StringBuilder(WCF_URL + "VaccinationAppointmentManagement.svc/UpdateVaccinationApp?")
                    .append("outreach=").append(String.valueOf(cbOutreach.isChecked()))
                    .append("&userId=").append(app.getLOGGED_IN_USER_ID())
                    .append("&vaccinationAppointmentId=").append(appointment_id);

            this.updateURLAppointment = VaccinationAppointmentUpdateURL.toString();
            Log.d("Appointment update url on Outreach", this.updateURLAppointment);
        }

        public String calculateDateDiff(String schdate) {
            Date scheduled = dateParser(Scheduled_Date_field);
            long diff = time2.getTime() - scheduled.getTime();
            Date newscheduled = dateParser(schdate);
            return "/Date(" + (newscheduled.getTime() + diff) + "-0500)/";
        }
    }

    public class RowObjects {

        private TextView date;

        public RowObjects() {

        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }

        public String getInput() {
            return date.getText().toString();
        }

        public void setInput(String a) {
            this.date.setText(a);
        }
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
            String threadTodayTimestamp = null;
            for (String a : params) {
                int status = application.updateVaccinationEventOnServer(a);
                Log.d("The status", status + "");
                String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                try {
                    threadTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //Register Audit
                application.registerAudit(BackboneApplication.CHILD_AUDIT, barcode.getText().toString(), threadTodayTimestamp,
                        application.getLOGGED_IN_USER_ID(), 7);
            }
            //a.syncVaccines();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class updateBalance extends AsyncTask<RowCollector, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(RowCollector... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            for (RowCollector a : params) {
                Log.d("Updating balance", "");
                if (a.getStatus().equalsIgnoreCase("true")) {
                    Log.d("Starting update protocol", "");
                    Cursor cursor = db.getReadableDatabase().rawQuery("SELECT balance FROM health_facility_balance WHERE lot_id=?", new String[]{a.getVaccination_lot()});
                    //Cursor cursor = db.getReadableDatabase().rawQuery("UPDATE health_facility_balance SET balance = balance - 1 WHERE lot_id=?", new String[]{a.getVaccination_lot()});
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int bal = cursor.getInt(cursor.getColumnIndex("balance"));
                        Log.d("Balance found on database: ", bal + "");
                        bal = bal - 1;
                        Log.d("Balance being set: ", bal + "");
                        ContentValues cv = new ContentValues();
                        cv.put(SQLHandler.HealthFacilityBalanceColumns.BALANCE, bal);
                        db.updateStockBalance(cv, a.getVaccination_lot());
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
            for (String a : params) {
                if(!a.equalsIgnoreCase("")){
                    //int status = application.updateVaccinationAppOutreach(a);
                    //Log.d("The status", status + "");
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
}
