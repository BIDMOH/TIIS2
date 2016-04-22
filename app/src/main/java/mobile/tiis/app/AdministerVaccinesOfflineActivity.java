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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.entity.NonVaccinationReason;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by Teodor on 3/24/2015.
 */
public class AdministerVaccinesOfflineActivity extends BackboneActivity implements View.OnClickListener {


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
    Date new_date;
    private TextView barcode;
    private TableLayout tableLayout;
    private BackboneApplication application;
    private DatabaseHandler database;
    private ArrayList<RowCollector> rowCollectorContainer;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.administer_vaccines_offline_activity);

        barcode = (TextView) findViewById(R.id.administer_vaccine_offline_barcode);
        tableLayout = (TableLayout) findViewById(R.id.administer_vaccines_offline_table_layout);

        //set the barcode text view
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barcode.setText(extras.getString("barcode"));
        }

        //Collector of information for every row
        rowCollectorContainer = new ArrayList<RowCollector>();

        //Getting all vaccinations from Scheduled Vaccination Table dhe their Id's
        application = (BackboneApplication) getApplication();
        database = application.getDatabaseInstance();
        Cursor cursor = null;
        cursor = database.getReadableDatabase().rawQuery("SELECT * FROM scheduled_vaccination", null);

        if (cursor.getCount() > 0)
        {
            if (cursor.moveToFirst()) {
                do {
                    RowCollector rowCollector = new RowCollector();
                    Log.d("Adding Scheduled Vaccination Id", cursor.getString(cursor.getColumnIndex("ID")));
                    rowCollector.setScheduled_vaccination_id(cursor.getString(cursor.getColumnIndex("ID")));

                    Log.d("Adding Scheduled Vaccination Name", cursor.getString(cursor.getColumnIndex("NAME")));
                    rowCollector.setScheduled_vaccination_name(cursor.getString(cursor.getColumnIndex("NAME")));

                    Log.d("Adding Scheduled Vaccination Item Id", cursor.getString(cursor.getColumnIndex("ITEM_ID")));
                    rowCollector.setScheduled_vaccination_item_id(cursor.getString(cursor.getColumnIndex("ITEM_ID")));

                    rowCollectorContainer.add(rowCollector);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        //Going over all vaccines from row collector container
        for (final RowCollector rowCollector : rowCollectorContainer) {

            //Create a new row to hold the row data
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT);
            lp.setMargins(10, 10, 10, 10);
            row.setLayoutParams(lp);


            //Create VACCINE Column ========================================================
            final TextView VaccineColumn = new TextView(this);
            VaccineColumn.setTextColor(Color.parseColor("#333333"));
            VaccineColumn.setGravity(Gravity.START);
            VaccineColumn.setHeight(100);
            VaccineColumn.setWidth(170);
            VaccineColumn.setText(rowCollector.getScheduled_vaccination_name());
            VaccineColumn.setPadding(10, 0, 0, 0);


            //Create VACCINE LOT Column ====================================================
            final Spinner VaccineLotColumn = new Spinner(this);

            Map<String, String> vaccine_lot_map = new HashMap<String, String>();
            List<String> vaccine_lot_names_list = new ArrayList<String>();
            cursor = database.getReadableDatabase().rawQuery("SELECT '-1' AS id, '-----' AS lot_number, datetime('now') as expire_date UNION " +
                    "SELECT '-2' AS id, 'No Lot' AS lot_number, datetime('now') as expire_date UNION " +
                    "SELECT item_lot.id, item_lot.lot_number, datetime(substr(item_lot.expire_date,7,10), 'unixepoch') FROM item_lot  join health_facility_balance ON item_lot.ID = health_facility_balance.lot_id WHERE item_lot.item_id = ? AND health_facility_balance.LotIsActive = 'true'" +
                    " AND datetime(substr(item_lot.expire_date,7,10), 'unixepoch') >= datetime('now') ORDER BY expire_date", new String[]{rowCollector.getScheduled_vaccination_item_id()});
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("", "Adding to map" + cursor.getString(cursor.getColumnIndex("lot_number")));
                        //Getting a map of vacine lot numbers and their id's
                        vaccine_lot_map.put(cursor.getString(cursor.getColumnIndex("lot_number")), cursor.getString(cursor.getColumnIndex("id")));
                        //Getting a list of the vaccine lot numbers for the spinner
                        vaccine_lot_names_list.add(cursor.getString(cursor.getColumnIndex("lot_number")));
                    } while (cursor.moveToNext());
                    rowCollector.setVaccine_lot_id_name_map(vaccine_lot_map);
                    rowCollector.setVaccine_lot_names_list(vaccine_lot_names_list);
                }
            }

            SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, rowCollector.getVaccine_lot_names_list());
            VaccineLotColumn.setAdapter(statusAdapter);
            VaccineLotColumn.setMinimumWidth(200);
            // -Condition to set Lot on start (first good one / No lot)
            if (rowCollector.getVaccine_lot_names_list().size() > 2) {
                VaccineLotColumn.setSelection(2);
                rowCollector.setVaccine_lot_current_position(2);
            } else {
                VaccineLotColumn.setSelection(1);
                rowCollector.setVaccine_lot_current_position(1);
            }
            VaccineLotColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    VaccineLotColumn.setSelection(position);
                    rowCollector.setVaccine_lot_current_position(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });


            //Create VACCINATION DATE Column ====================================================
            final TextView VaccinationDateColumn = new TextView(this);
            VaccinationDateColumn.setTextColor(Color.parseColor("#333333"));
            VaccinationDateColumn.setGravity(Gravity.START);
            VaccinationDateColumn.setHeight(100);
            VaccinationDateColumn.setWidth(200);
            VaccinationDateColumn.setPadding(40, 0, 0, 0);

            VaccinationDateColumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show(VaccinationDateColumn, rowCollector);
                }
            });
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            VaccinationDateColumn.setText(ft.format(dNow));
            rowCollector.setVaccination_date(dNow);


            //NonVaccinationReason Column Spinner
            List<String> reasons = new ArrayList<String>();
            reasons.add("----");
            for (NonVaccinationReason nvElement : database.getAllNonvaccinationReasons()) {
                reasons.add(nvElement.getName());
            }


            final List<NonVaccinationReason> non_vaccination_reason_list_with_additions = database.getAllNonvaccinationReasons();
            NonVaccinationReason empty = new NonVaccinationReason();
            empty.setName("----");
            empty.setId("0");
            non_vaccination_reason_list_with_additions.add(empty);



            final Spinner NonvaccinationReasonColumn = new Spinner(this);
            final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, reasons);
            NonvaccinationReasonColumn.setAdapter(statusAdapterNonVaccinationReason);
            NonvaccinationReasonColumn.setSelection(0);
            NonvaccinationReasonColumn.setGravity(Gravity.START);
            rowCollector.setNonvaccination_reason_position(0);
            rowCollector.setNon_vac_reason("0");
            NonvaccinationReasonColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    NonvaccinationReasonColumn.setSelection(position);
                    rowCollector.setNonvaccination_reason_position(position);

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
            //NonvaccinationReasonColumn.setVisibility(View.GONE);
            NonvaccinationReasonColumn.setMinimumWidth(200);


            //Create VACCINATION STATUS Column ====================================================
            final CheckBox VaccinationStatusColumn = new CheckBox(this);
            VaccinationStatusColumn.setChecked(false); //needs to be unchecked when we enter screen
            VaccinationStatusColumn.setGravity(Gravity.START);
            VaccinationStatusColumn.setWidth(50);
            rowCollector.setVaccination_done_status("false");
            VaccinationStatusColumn.setButtonDrawable(R.drawable.checkbox);
            VaccinationStatusColumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    rowCollector.setVaccination_done_status(String.valueOf(b));
                    if (!b) {
                        NonvaccinationReasonColumn.setVisibility(View.VISIBLE);
                        //VaccineLotColumn.setSelection(0);
                        rowCollector.setNonvaccination_reason_position(0);

                    }
                    if (b) {
                        NonvaccinationReasonColumn.setVisibility(View.GONE);
                        rowCollector.setNon_vac_reason("-1");
                    }
                }
            });


            //Adding dose name column
            row.addView(VaccineColumn);
            //Adding vaccine lot column
            row.addView(VaccineLotColumn);
            //Adding vaccination date column
            row.addView(VaccinationDateColumn);
            //Adding vaccination done status column
            row.addView(VaccinationStatusColumn);
            //Adding nonvaccination reason column
            row.addView(NonvaccinationReasonColumn);
            //Displaying row
            tableLayout.addView(row);

        }


        Button save = (Button) findViewById(R.id.vaccinate_offline_save_button);
        Button back = (Button) findViewById(R.id.vaccinate_offline_back_button);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vaccinate_offline_save_button:
                int done = 0;

                //Check if there is any Done column, and if tha
                for (RowCollector a : rowCollectorContainer) {
                    if (a.getVaccination_done_status().equalsIgnoreCase("false")) {
                        if (a.getNonvaccination_reason_position() != 0) {
                            done += 1;
                        }
                    } else if (a.getVaccination_done_status().equalsIgnoreCase("true")) {
                        done += 1;
                    }

                }


                if (done == 0) {
                    final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccinesOfflineActivity.this).create();
                    ad22.setTitle(getString(R.string.warning));
                    ad22.setMessage(getString(R.string.select_non_vacc_reason));
                    ad22.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad22.dismiss();
                        }
                    });
                    ad22.show();
                    return;
                }


                for (RowCollector a : rowCollectorContainer) {

                    if (a.getVaccination_done_status().equalsIgnoreCase("true") && a.getVaccine_lot_names_list().get(a.getVaccine_lot_current_position()).equalsIgnoreCase("-----")) {
                        final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccinesOfflineActivity.this).create();
                        ad22.setTitle("Not Saved");
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

                    if (a.getVaccination_done_status().equalsIgnoreCase("false") && a.getNonvaccination_reason_position() == 0) {
                        Log.d("Skip", "Skipping vaccine");
                    } else {
                        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
                        updateOfflineAdministerVaccine task = new updateOfflineAdministerVaccine();
                        StringBuilder updateUrl = new StringBuilder(WCF_URL + "VaccinationEvent.svc/UpdateVaccinationEventByBarcodeVaccine?")
                                .append("barcodeId=").append(barcode.getText().toString())
                                .append("&vaccineId=").append(a.getScheduled_vaccination_id())
                                .append("&vaccinelot=").append(a.getVaccine_lot_id_name_map().get(a.getVaccine_lot_names_list().get(a.getVaccine_lot_current_position())))
                                .append("&healthFacilityId=").append(application.getLOGGED_IN_USER_HF_ID())
                                .append("&vaccinationDate=").append(URLEncoder.encode(formatted.format(a.getVaccination_date())))
                                .append("&notes=").append("")
                                .append("&vaccinationStatus=").append(a.getVaccination_done_status())
                                .append("&nonvaccinationReasonId=").append(a.getNon_vac_reason())
                                .append("&userId=").append(application.getLOGGED_IN_USER_ID());
                        Log.d("Created URL", updateUrl.toString());
                        task.execute(updateUrl.toString());
                    }


//                    if(a.getVaccination_done_status().equalsIgnoreCase("false") && a.getNonvaccination_reason_position() == 0){
//                        final AlertDialog ad22 = new AlertDialog.Builder(AdministerVaccinesOfflineActivity.this).create();
//                        ad22.setTitle("Not Saved");
//                        ad22.setMessage("Please select nonvaccination reason");
//                        ad22.setButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ad22.dismiss();
//                            }
//                        });
//                        ad22.show();
//                        return;
//                    }

                    //Toast.makeText(this, "ServerSaving", Toast.LENGTH_SHORT).show();
                }

                final AlertDialog ad2 = new AlertDialog.Builder(AdministerVaccinesOfflineActivity.this).create();
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

            case R.id.vaccinate_offline_back_button:
                Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
                Intent av = new Intent(getApplicationContext(), ScanResultActivity.class);
                av.putExtra("result", barcode.getText().toString());
                startActivity(av);
                break;
        }
    }

    public Date show(final TextView a, final RowCollector coll) {
        final Dialog d = new Dialog(AdministerVaccinesOfflineActivity.this);
        d.setTitle("Date Picker");
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
        dp.setMaxDate(new Date().getTime());
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                //setText(Integer.toString(dp.getDayOfMonth()) + "/" + Integer.toString(dp.getMonth() + 1) + "/" + Integer.toString(dp.getYear()));
                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                new_date = calendar.getTime();
                Date dNow = new Date();
                if (getDaysDifference(new_date, dNow) > 0) {
                    coll.setVaccination_date(new_date);
                    a.setText(ft.format(new_date));
                } else {
                    coll.setVaccination_date(dNow);
                    a.setText(ft.format(dNow));
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
        //Holding values from Scheduled Vaccination Table
        private String scheduled_vaccination_name;
        private String scheduled_vaccination_id;
        private String scheduled_vaccination_item_id;

        //Holding Values from Item Lot Table
        private Map vaccine_lot_id_name_map;
        private List<String> vaccine_lot_names_list;
        private int vaccine_lot_current_position;

        private Date vaccination_date;

        //Holds status Done / NotDone
        private String vaccination_done_status = "false";

        private int nonvaccination_reason_position;
        private String non_vac_reason = "";


        public RowCollector() {

        }

        public String getScheduled_vaccination_name() {
            return scheduled_vaccination_name;
        }

        public void setScheduled_vaccination_name(String scheduled_vaccination_name) {
            this.scheduled_vaccination_name = scheduled_vaccination_name;
        }

        public String getScheduled_vaccination_item_id() {
            return scheduled_vaccination_item_id;
        }

        public void setScheduled_vaccination_item_id(String scheduled_vaccination_item_id) {
            this.scheduled_vaccination_item_id = scheduled_vaccination_item_id;
        }

        public String getScheduled_vaccination_id() {
            return scheduled_vaccination_id;
        }

        public void setScheduled_vaccination_id(String scheduled_vaccination_id) {
            this.scheduled_vaccination_id = scheduled_vaccination_id;
        }


        public Map getVaccine_lot_id_name_map() {
            return vaccine_lot_id_name_map;
        }

        public void setVaccine_lot_id_name_map(Map vaccine_lot_id_name_map) {
            this.vaccine_lot_id_name_map = vaccine_lot_id_name_map;
        }

        public List<String> getVaccine_lot_names_list() {
            return vaccine_lot_names_list;
        }

        public void setVaccine_lot_names_list(List<String> vaccine_lot_names_list) {
            this.vaccine_lot_names_list = vaccine_lot_names_list;
        }

        public int getVaccine_lot_current_position() {
            return vaccine_lot_current_position;
        }

        public void setVaccine_lot_current_position(int vaccine_lot_current_position) {
            this.vaccine_lot_current_position = vaccine_lot_current_position;
        }


        public Date getVaccination_date() {
            return vaccination_date;
        }

        public void setVaccination_date(Date vaccination_date) {
            this.vaccination_date = vaccination_date;
        }


        public String getVaccination_done_status() {
            return vaccination_done_status;
        }

        public void setVaccination_done_status(String vaccination_done_status) {
            this.vaccination_done_status = vaccination_done_status;
        }

        public int getNonvaccination_reason_position() {
            return nonvaccination_reason_position;
        }

        public void setNonvaccination_reason_position(int nonvaccination_reason_position) {
            this.nonvaccination_reason_position = nonvaccination_reason_position;
        }


        public String getNon_vac_reason() {
            return non_vac_reason;
        }

        public void setNon_vac_reason(String non_vac_reason) {
            this.non_vac_reason = non_vac_reason;
        }

    }

    private class updateOfflineAdministerVaccine extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            for (String a : params) {
                int status = application.updateVaccinationEventOnServer(a);
                Log.d("Saving offline status", status + "");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}
