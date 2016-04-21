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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.AefiLastAppointementListAdapter;
import mobile.tiis.app.adapters.AefiListAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.AefiListItem;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by olsi on 25-03-15.
 */
public class AefiActivity extends BackboneActivity implements View.OnClickListener {

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
    private Button back, save, btnAefiDate;
    private CheckBox chkHadAefi;
    private EditText edtNotesAefi;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    private String childId;
    private ListView listViewAefiLastAppointement, listViewAefi;
    private ArrayList<AefiListItem> aefiItems;
    private AefiListItem lastAppointementAefi;
    private ArrayList<AefiListItem> lastAppointementAefiList;
    private AefiLastAppointementListAdapter aefiLastAppointementListAdapter;
    private AefiListAdapter aefiListAdapter;
    private SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
    private Date aefiNewDate;
    private Thread thread;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.aefi_activity);
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(status_receiver);
    }

    private void initViews() {
        back = (Button) findViewById(R.id.btn_back_button_aefi_activity);
        listViewAefiLastAppointement = (ListView) findViewById(R.id.lst1_aefi_activity);
        listViewAefi = (ListView) findViewById(R.id.lst2_aefi_activity);
        save = (Button) findViewById(R.id.btn_save_aefi_activity);
        edtNotesAefi = (EditText) findViewById(R.id.edt_notes_aefi_activity);
        chkHadAefi = (CheckBox) findViewById(R.id.chk_had_aefi_activity);
        btnAefiDate = (Button) findViewById(R.id.btn_aefi_date_aefi_activity);
    }

    private void initDb() {
        mydb = app.getDatabaseInstance();
    }

    private void renderViews() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                    new String[]{String.valueOf(extras.getString("barcode"))});
            if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                getChildIdCursor.moveToFirst();
                childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            } else {
                toastMessage(getString(R.string.empty_child_id));
                finish();
            }
        } else {
            toastMessage(getString(R.string.empty_barcode));
            finish();
        }
        aefiItems = mydb.getAefiVaccinationAppointement(childId);
        if (aefiItems != null && aefiItems.size() > 0) {
            aefiListAdapter = new AefiListAdapter(this, R.layout.item_aefi, aefiItems);
            listViewAefi.setAdapter(aefiListAdapter);
        }

        lastAppointementAefiList = mydb.getAefiLastVaccinationAppointement(childId);
        if (lastAppointementAefiList != null && lastAppointementAefiList.size() > 0) {
            lastAppointementAefi = lastAppointementAefiList.get(0);

            if (lastAppointementAefi != null) {
                aefiLastAppointementListAdapter = new AefiLastAppointementListAdapter(this, R.layout.item_last_appointment_aefi, lastAppointementAefiList);
                listViewAefiLastAppointement.setAdapter(aefiLastAppointementListAdapter);
                chkHadAefi.setChecked(true);
                if (lastAppointementAefi.getAefiDate() != null)
                    btnAefiDate.setText(format.format(lastAppointementAefi.getAefiDate()));
                else
                    btnAefiDate.setText(format.format(new Date()));
                edtNotesAefi.setText(lastAppointementAefi.getNotes());
            }
        }
        aefiNewDate = new Date();
    }

    public void initListeners() {
        back.setOnClickListener(this);
        save.setOnClickListener(this);
        btnAefiDate.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_back_button_aefi_activity) {
            Bundle extras = getIntent().getExtras();
            Intent back = new Intent(this, ViewChildActivity.class);
            back.putExtra("barcode", getIntent().getExtras().getString("barcode"));
            //back.putExtra("result", extras.getString("barcode"));
            startActivity(back);
        }
        if (v.getId() == R.id.btn_aefi_date_aefi_activity) {
            showAefiChooseDialog();
        }
        if (v.getId() == R.id.btn_save_aefi_activity) {
            saveChanges();
        }
    }

    private void saveChanges() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });
        ContentValues contentValues = new ContentValues();
        if (lastAppointementAefi != null) {
            if (chkHadAefi.isChecked() != lastAppointementAefi.isAefi()) {
                lastAppointementAefi.setAefi(chkHadAefi.isChecked());
                if (chkHadAefi.isChecked())
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI, "true");
                else
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI, "false");
            }
            if (lastAppointementAefi.getAefiDate() != null) {
                if (aefiNewDate != null && aefiNewDate.compareTo(lastAppointementAefi.getAefiDate()) != 0) {
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI_DATE, stringToDateParser(aefiNewDate));
                    lastAppointementAefi.setAefiDate(aefiNewDate);
                }
            } else {
                contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI_DATE, stringToDateParser(new Date()));
                lastAppointementAefi.setAefiDate(new Date());
            }
            if (edtNotesAefi.getText() != null && !lastAppointementAefi.getNotes().equals(edtNotesAefi.getText().toString())) {
                lastAppointementAefi.setNotes(edtNotesAefi.getText().toString());
                contentValues.put(SQLHandler.VaccinationAppointmentColumns.NOTES, edtNotesAefi.getText().toString());
            }


            if (contentValues.size() > 0) {
                lastAppointementAefi.setModifiedById(app.getLOGGED_IN_USER_ID());
                lastAppointementAefi.setModifiedOn(new Date());
                if (mydb.updateVaccinationAppointementById(contentValues, lastAppointementAefi.getAppointementId()) > 0) {
                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_success);
                    thread = new Thread() {
                        @Override
                        public void run() {
                            String url = prepareUrl().toString();
                            if (!app.updateAefiAppointement(prepareUrl())) {
                                mydb.addPost(url, -1);
                                Log.d("Save Edited Child", "Error while saving edited aefi " + lastAppointementAefi.getAppointementId());
                            } else {
                            }
                        }
                    };
                    thread.start();
                }
                alertDialogBuilder.show();
                reloadAefiLists();
            }
        } else {
            alertDialogBuilder.setMessage(R.string.child_change_data_saved_error);
            alertDialogBuilder.show();
        }
    }

    private StringBuilder prepareUrl() {
        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.VACCINATION_APPOINTMENT_MANAGMENT_SVC).append(BackboneApplication.REGISTER_CHILD_AEFI);
        webServiceUrl.append("appId=" + lastAppointementAefi.getAppointementId());
        if (lastAppointementAefi.isAefi())
            webServiceUrl.append("&aefi=" + "true");
        else
            webServiceUrl.append("&aefi=" + "true");
        webServiceUrl.append("&notes=" + lastAppointementAefi.getNotes());
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        if (lastAppointementAefi.getAefiDate() != null)
            try {
                webServiceUrl.append("&date=" + URLEncoder.encode(formatted.format(lastAppointementAefi.getAefiDate()), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        try {
            webServiceUrl.append("&modifiedon=" + URLEncoder.encode(formatted.format(lastAppointementAefi.getModifiedOn()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webServiceUrl.append("&modifiedby=" + lastAppointementAefi.getModifiedById());
        return webServiceUrl;
    }

    private void reloadAefiLists() {
        aefiItems.clear();
        ArrayList<AefiListItem> aefiListtemp = mydb.getAefiVaccinationAppointement(childId);
        if (aefiListtemp != null && aefiListtemp.size() > 0)
            aefiItems.addAll(aefiListtemp);
        aefiListAdapter.notifyDataSetChanged();

        lastAppointementAefiList.clear();
        ArrayList<AefiListItem> lastAppointementAefiListtemp = mydb.getAefiLastVaccinationAppointement(childId);
        if (lastAppointementAefiListtemp != null && lastAppointementAefiListtemp.size() > 0) {
            lastAppointementAefiList.addAll(lastAppointementAefiListtemp);
            lastAppointementAefi = lastAppointementAefiList.get(0);
        }
        aefiLastAppointementListAdapter.notifyDataSetChanged();
    }

    public void showAefiChooseDialog() {
        final Dialog d = new Dialog(AefiActivity.this);
        d.setTitle(getString(R.string.aefi_date));
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
        dp.setMaxDate(new Date().getTime());
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //birthdate.setText(Integer.toString(dp.getDayOfMonth()) + "/"  + Integer.toString(dp.getMonth()+1) + "/" + Integer.toString(dp.getYear()));
                Calendar calendar = Calendar.getInstance();
                calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                aefiNewDate = calendar.getTime();
                Date dNow = new Date();
                btnAefiDate.setText(ft.format(aefiNewDate));
                lastAppointementAefi.setAefiDate(calendar.getTime());
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
}
