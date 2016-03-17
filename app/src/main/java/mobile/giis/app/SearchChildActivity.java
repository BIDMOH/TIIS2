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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobile.giis.app.adapters.AdapterGridDataSearchResult;
import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.Birthplace;
import mobile.giis.app.entity.Child;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.entity.Place;
import mobile.giis.app.entity.Status;
import mobile.giis.app.fragments.FragmentSearchResultDataGrid;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Arinela on 3/12/2015.
 */
public class SearchChildActivity extends BackboneActivity implements View.OnClickListener {
    //@Arinela on 11/3/2015
    EditText etBarcode, etFirstName, etMotherfirstName, etTempId, etSurname, etMotherSurname,etFistname2;
    Spinner spPlaceOfBirth, spHealthFacility, spVillage, spStatus;
    Button btnSearch, btnDobFrom, btnDobTo, btnRegister, btnClear;
    List<Place> placeList;
    List<Birthplace> birthplaceList;
    List<HealthFacility> healthFacilityList;
    List<Status> statusList;
    TextView tvNoChild;
    LinearLayout linLista;
    ListView listSearch;
    ProgressDialog add;
    int parse_status;
    String childidToParse;

    boolean saveStateCalled = false;
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Intent scan = new Intent(getApplicationContext(), ViewChildActivity.class);
            scan.putExtra("cameFromSearch", true);
            Bundle bnd = new Bundle();
            bnd.putString(BackboneApplication.CHILD_ID, childidToParse);
            scan.putExtras(bnd);

            switch (msg.what) {
                case 10:
                    add.show();
                    break;
                case 2:
                    //Toast.makeText(ScanHandlerActivity.this, "Status 2", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();

                    final AlertDialog ad2 = new AlertDialog.Builder(SearchChildActivity.this).create();
                    ad2.setTitle(getString(R.string.error));
                    ad2.setMessage(getString(R.string.data_not_fetched));
                    ad2.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad2.dismiss();
                        }
                    });
                    ad2.show();
                    break;
                case 3:
                    final AlertDialog ad3 = new AlertDialog.Builder(SearchChildActivity.this).create();
                    ad3.setTitle(getString(R.string.error));
                    ad3.setMessage(getString(R.string.data_not_fetched));
                    ad3.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad3.dismiss();
                        }
                    });
                    ad3.show();
                    //Toast.makeText(ScanHandlerActivity.this, "Status 3", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    break;
                default:
                    //found=1;
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    startActivity(scan);
                    //Toast.makeText(ScanHandlerActivity.this, "Child found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_child_activity);

        etBarcode = (EditText) findViewById(R.id.barcode_id);
        etFistname2 =(EditText)findViewById(R.id.firstname2);
        etFirstName = (EditText) findViewById(R.id.firstname);
        etMotherfirstName = (EditText) findViewById(R.id.mother_firstname);
        etTempId = (EditText) findViewById(R.id.temp_id);
        etSurname = (EditText) findViewById(R.id.lastname);
        etMotherSurname = (EditText) findViewById(R.id.mother_lastname);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setVisibility(View.GONE);
        tvNoChild = (TextView) findViewById(R.id.tv_no_children);
        tvNoChild.setVisibility(View.GONE);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);


        spPlaceOfBirth = (Spinner) findViewById(R.id.place_of_birth);
        spHealthFacility = (Spinner) findViewById(R.id.health_facility);
        spVillage = (Spinner) findViewById(R.id.village_domicile);
        spStatus = (Spinner) findViewById(R.id.status);

        btnSearch = (Button) findViewById(R.id.btn_search_child_id);
        btnDobFrom = (Button) findViewById(R.id.dob_from);
        btnDobFrom.setOnClickListener(this);
        btnDobTo = (Button) findViewById(R.id.dob_to);
        btnDobTo.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        linLista = (LinearLayout) findViewById(R.id.lin_lista);
        linLista.setVisibility(View.GONE);

        listSearch = (ListView) findViewById(R.id.lv_search_list_result);

        BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();

        placeList = mydb.getAllPlaces();
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
        spPlaceOfBirth.setSelection(0);

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, place_names);
        spVillage.setAdapter(dataAdapter);
        spVillage.setSelection(0);

        healthFacilityList = mydb.getAllHealthFacility();
        List<String> facility_name = new ArrayList<String>();


        facility_name.add("-Please choose-");
        for (HealthFacility element : healthFacilityList) {

            facility_name.add(element.getName());
        }

        SingleTextViewAdapter healthAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, facility_name);
        spHealthFacility.setAdapter(healthAdapter);
        spHealthFacility.setSelection(0);


        statusList = mydb.getStatus();
        List<String> status_name = new ArrayList<String>();

        if (statusList.size() == 0) {
            spStatus.setEnabled(false);

        } else {
            for (Status element : statusList) {
                status_name.add(element.getName());
            }
        }

        SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(this, R.layout.single_text_spinner_item_drop_down, status_name);
        spStatus.setAdapter(statusAdapter);
        spStatus.setSelection(2);

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
        //search button
        if (v.getId() == R.id.btn_search_child_id) {
            tvNoChild.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            linLista.setVisibility(View.GONE);

            //@Arinela search lokalisht
            BackboneApplication app = (BackboneApplication) getApplication();
            final DatabaseHandler mydb = app.getDatabaseInstance();

            //get the date from and date to and format them
            SimpleDateFormat fmt = new SimpleDateFormat("d/M/yyyy");
            Date dateFrom = null, dateTo = null;
            try {
                dateFrom = fmt.parse(btnDobFrom.getText().toString());
                dateTo = fmt.parse(btnDobTo.getText().toString());

                Log.e("date into long", "dateFrom : " + dateFrom.getTime() + " dateTo : " + dateTo.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            String placeOBId = "";
            try {
                if (spPlaceOfBirth.getSelectedItemPosition() != 0) {
                    placeOBId = birthplaceList.get(spPlaceOfBirth.getSelectedItemPosition() - 1).getId();
                }
            } catch (Exception e) {
            }

            String healthFacility = "";
            try {
                if (spHealthFacility.getSelectedItemPosition() != 0) {
                    healthFacility = healthFacilityList.get(spHealthFacility.getSelectedItemPosition() - 1).getId();
                }

            } catch (Exception e) {
            }
            String villageName = "";
            try {
                if (spVillage.getSelectedItemPosition() != 0) {
                    villageName = placeList.get(spVillage.getSelectedItemPosition() - 1).getId();
                }

            } catch (Exception e) {
            }
            String status = "";
            try {
                status = statusList.get(spStatus.getSelectedItemPosition()).getId();
            } catch (Exception e) {
            }

            final ArrayList<Child> children = mydb.searchChild(etBarcode.getText().toString(),
                    etFirstName.getText().toString(),etFistname2.getText().toString(), etMotherfirstName.getText().toString(), (dateFrom != null) ? (dateFrom.getTime() / 1000) + "" : "",
                    (dateTo != null) ? (dateTo.getTime() / 1000) + "" : "", etTempId.getText().toString(), etSurname.getText().toString(), etMotherSurname.getText().toString(),
                    placeOBId, healthFacility, villageName, status, "0");
            if (children != null) {

                if (0 < children.size() && children.size() < 0) {
                    hideSoftKeyboard();
                    linLista.setVisibility(View.VISIBLE);
                    AdapterGridDataSearchResult adapter = new AdapterGridDataSearchResult(getApplicationContext(), R.layout.item_grid_search_result, children, mydb);
                    listSearch.setAdapter(adapter);
                    listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent viewChild = new Intent(getApplicationContext(), ViewChildActivity.class);
                            viewChild.putExtra("cameFromSearch", true);

                            Bundle bnd = new Bundle();
                            bnd.putString(BackboneApplication.CHILD_ID, children.get(position).getId());
                            viewChild.putExtras(bnd);
                            startActivity(viewChild);
                        }
                    });
                } else if (children.size() > 20) {
                    final AlertDialog warning = new AlertDialog.Builder(this).create();
                    warning.setTitle("Warning!");
                    warning.setMessage("Please supply more selection criteria!");
                    warning.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            warning.dismiss();
                        }
                    });
                    warning.show();

                } else {
                    // Create and show the dialog.
                    FragmentSearchResultDataGrid newFragment = new FragmentSearchResultDataGrid();
                    newFragment.setListWithSearchChild(children, mydb);
                    newFragment.show(getSupportFragmentManager(), "dialog");
                }
            }

            //Search server
            else {
                SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

                String dateFromForSRV = "";
                String dateToForSRV = "";
                try {
                    dateFromForSRV = formatted.format(dateFrom);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    dateToForSRV = formatted.format(dateTo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                add = new ProgressDialog(SearchChildActivity.this);
                add.setTitle("Retrieving child");
                add.setMessage(getString(R.string.child_not_saved_locally));
                add.setCanceledOnTouchOutside(false);
                add.setCancelable(false);

                new Thread() {
                    String threadDateFrom
                            ,
                            threadDateTo;

                    public Thread setData(String threadDateFrom, String threadDateTo) {
                        try {
                            this.threadDateFrom = URLEncoder.encode(threadDateFrom, "utf-8");
                            this.threadDateTo = URLEncoder.encode(threadDateTo, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return this;
                    }

                    @Override
                    public void run() {


                        synchronized (this) {

                            String placeOBId = "";
                            try {
                                placeOBId = birthplaceList.get(spPlaceOfBirth.getSelectedItemPosition() - 1).getId();
                            } catch (Exception e) {
                            }

                            String healthFacility = "";
                            try {
                                healthFacility = healthFacilityList.get(spHealthFacility.getSelectedItemPosition() - 1).getId();
                            } catch (Exception e) {
                            }
                            String villageName = "";
                            try {
                                villageName = placeList.get(spVillage.getSelectedItemPosition() - 1).getId();
                            } catch (Exception e) {
                            }
                            String status = "";
                            try {
                                status = statusList.get(spStatus.getSelectedItemPosition()).getId();
                            } catch (Exception e) {
                            }

                            BackboneApplication backbone = (BackboneApplication) getApplication();
                            final ArrayList<Child> childrensrv = backbone.searchChild(etBarcode.getText().toString(),
                                    etFirstName.getText().toString(),etFistname2.getText().toString(), etMotherfirstName.getText().toString(), threadDateFrom,
                                    threadDateTo, etTempId.getText().toString(), etSurname.getText().toString(), etMotherSurname.getText().toString(),
                                    placeOBId, healthFacility,
                                    villageName, status);

                            if (childrensrv == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Communication was not successful,try again", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else if (childrensrv.size() > 0) {


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Create and show the dialog.
                                        if (0 < childrensrv.size() && childrensrv.size() < 0) {
                                            hideSoftKeyboard();
                                            linLista.setVisibility(View.VISIBLE);

                                            AdapterGridDataSearchResult adapter = new AdapterGridDataSearchResult(getApplicationContext(), R.layout.item_grid_search_result, childrensrv, mydb);
                                            listSearch.setAdapter(adapter);
                                            listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                    Intent viewChild = new Intent(getApplicationContext(), ViewChildActivity.class);
                                                    childidToParse = childrensrv.get(position).getId();

                                                    myHandler.sendEmptyMessage(10);
                                                    ChildSynchronization task = new ChildSynchronization();
                                                    task.execute(childidToParse);
                                                }
                                            });
                                        } else if (childrensrv.size() > 20) {
                                            final AlertDialog warning2 = new AlertDialog.Builder(SearchChildActivity.this).create();
                                            warning2.setTitle("Warning!");
                                            warning2.setMessage("Please supply more selection criteria!");
                                            warning2.setButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    warning2.dismiss();
                                                }
                                            });
                                            warning2.show();
                                        } else {
                                            if (saveStateCalled) {
                                                FragmentSearchResultDataGrid newFragment = new FragmentSearchResultDataGrid();
                                                newFragment.setListWithSearchChild(childrensrv, mydb);
                                                newFragment.show(getSupportFragmentManager(), "dialog");
                                            }
                                        }
                                    }
                                });
                            } else if (childrensrv.isEmpty()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNoChild.setVisibility(View.VISIBLE);
                                        btnRegister.setVisibility(View.VISIBLE);

                                        btnRegister.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent registerChild = new Intent(getApplicationContext(), RegisterChildActivity.class);
                                                Bundle bnd = new Bundle();
                                                bnd.putString("barcode", etBarcode.getText().toString());
                                                bnd.putString("firstname", etFirstName.getText().toString());
                                                bnd.putString("firstname2", etFistname2.getText().toString());
                                                bnd.putString("surname", etSurname.getText().toString());
                                                bnd.putString("motherFirstname", etMotherfirstName.getText().toString());
                                                bnd.putString("motherSurname", etMotherSurname.getText().toString());
                                                bnd.putInt("placeOfBirth", spPlaceOfBirth.getSelectedItemPosition());
                                                bnd.putInt("domicile", spVillage.getSelectedItemPosition());
                                                registerChild.putExtras(bnd);
                                                startActivity(registerChild);
                                            }
                                        });


                                    }
                                });
                            }
                        }
                    }
                }.setData(dateFromForSRV, dateToForSRV).start();
            }


        }
        if (v.getId() == R.id.dob_from) {
            showDobFrom();
        }
        if (v.getId() == R.id.dob_to) {
            showDobTo();
        }
        if (v.getId() == R.id.btn_clear) {
            etBarcode.setText("");
            etFirstName.setText("");
            etFistname2.setText("");
            etMotherfirstName.setText("");
            etMotherSurname.setText("");
            etSurname.setText("");
            etTempId.setText("");
            spHealthFacility.setSelection(0);
            spPlaceOfBirth.setSelection(0);
            spVillage.setSelection(0);
            spStatus.setSelection(2);
            btnDobFrom.setText("");
            btnDobTo.setText("");


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateCalled = true;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showDobFrom() {

        final Dialog d = new Dialog(SearchChildActivity.this);
        d.setTitle(getString(R.string.birthdate_picker));
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
        dp.setMaxDate(System.currentTimeMillis());

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDobFrom.setText(Integer.toString(dp.getDayOfMonth()) + "/" + Integer.toString(dp.getMonth() + 1) + "/" + Integer.toString(dp.getYear()));
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

    public void showDobTo() {

        final Dialog d = new Dialog(SearchChildActivity.this);
        d.setTitle(getString(R.string.birthdate_picker));
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);

        dp.setMaxDate(System.currentTimeMillis());

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDobTo.setText(Integer.toString(dp.getDayOfMonth()) + "/" + Integer.toString(dp.getMonth() + 1) + "/" + Integer.toString(dp.getYear()));
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

    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            int parse_status = 0;
            String village_id, hf_id;

            for (String id : params) {
                parse_status = application.parseChildById(id);
                if (parse_status != 2 && parse_status != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();
                    parseHFIDWhenNotInDb(db, application);
                    Cursor cursor = null;
                    Log.d("child id", id);
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE ID=?", new String[]{id});
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));
                        hf_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID));
                        Log.d("search hf id", hf_id);

                        int found = 0;
                        List<HealthFacility> a = db.getAllHealthFacility();
                        for (HealthFacility b : a) {
                            if (b.getId().equalsIgnoreCase(hf_id)) {
                                found = 1;
                            }
                        }

                        if (found == 0 && hf_id != null) {
                            application.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                application.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return parse_status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //super.onPostExecute(result);;
            myHandler.sendEmptyMessage(result);
            //parseChildByBarcode = result;
        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
        }
    }
}
