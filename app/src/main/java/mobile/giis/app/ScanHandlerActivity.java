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
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Teodor on 1/31/2015.
 */


public class ScanHandlerActivity extends BackboneActivity implements View.OnClickListener, ChildSynchronizationListener {

    protected Button scanBtn, submitBtn;
    protected TextView activity_title;
    protected EditText submitField;
    protected Handler handler;
    AlertDialog ad;
    Boolean scan_fetch = false;
    int parseChildByBarcode = 0;
    int status = 0;
    int found = 0;
    ProgressDialog add;
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
    private ImageView ivCheckIn;
    private String origine;
    private Thread thread;
    private RelativeLayout overlay;
    private String handleBarcode = "";
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Intent scan = new Intent(getApplicationContext(), ScanResultActivity.class);
            scan.putExtra("result", handleBarcode);

            switch (msg.what) {
                case 10:
                    add.show();
                    break;
                case 2:
                    //Toast.makeText(ScanHandlerActivity.this, "Status 2", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();

                    final AlertDialog ad2 = new AlertDialog.Builder(ScanHandlerActivity.this).create();
                    ad2.setTitle("Not Found");
                    ad2.setMessage("The inserted barcode does not belong to any child.\nPlease try registering child.");
                    ad2.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad2.dismiss();
                        }
                    });
                    ad2.show();
                    break;
                case 3:
                    final AlertDialog ad3 = new AlertDialog.Builder(ScanHandlerActivity.this).create();
                    ad3.setTitle(getString(R.string.error));
                    ad3.setMessage(getString(R.string.network_connectivity));
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
                    found = 1;
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    startActivity(scan);
                    //Toast.makeText(ScanHandlerActivity.this, "Child found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.scan_handler_activity);

        scanBtn = (Button) findViewById(R.id.scan_button);
        submitBtn = (Button) findViewById(R.id.manual_barcode_input_button);
        submitField = (EditText) findViewById(R.id.barcode_input_manually);
        submitField.requestFocus();
        activity_title = (TextView) findViewById(R.id.sc_handler_activity_title);
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        ivCheckIn = (ImageView) findViewById(R.id.iv_check_in);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            origine = extras.getString("origine");
        }

        scanBtn.setOnClickListener(this);
        submitBtn.setVisibility(View.GONE);
        submitBtn.setOnClickListener(this);

        submitField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, android.view.KeyEvent keyEvent) {
                if (keyEvent.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        //case android.view.KeyEvent.KEYCODE_DPAD_CENTER:
                        case android.view.KeyEvent.KEYCODE_ENTER:
                            if (Utils.isStringBlank(submitField.getText().toString())) {
                                toastMessage("No barcode inputted");
                            } else {
                                String contents = submitField.getText().toString();
                                onBarcodeInput(contents);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

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
    public void onTaskCompleted(int result) {
        parseChildByBarcode = result;
    }

    //send to camera scan
    public void onClick(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            if (origine != null) {
                if (origine.equalsIgnoreCase(ACTIVITY_CHECK_IN)) {
                    BackboneApplication app = (BackboneApplication) getApplication();
                    app.setCurrentActivity(ACTIVITY_CHECK_IN);
                }
            }
            //TODO modified by coze
//            scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            scanIntegrator.initiateScan();
        }

        if (v.getId() == R.id.manual_barcode_input_button) {
            if (Utils.isStringBlank(submitField.getText().toString())) {
                toastMessage("No barcode inputted");
            } else {
                String contents = submitField.getText().toString();
                onBarcodeInput(contents);
            }
        }
    }

    public void onBarcodeInput(final String barcode) {
        if (barcodeCheck(barcode)) {
            handleBarcode = barcode;
            final BackboneApplication app = (BackboneApplication) getApplication();
            switch (app.getCurrentActivity()) {
                case ACTIVITY_SCAN:
                    Intent scan = new Intent(getApplicationContext(), ScanResultActivity.class);
                    scan.putExtra("result", barcode);

                    //check database for current barcode
                    DatabaseHandler mydb = app.getDatabaseInstance();
                    Cursor cursor = null;
                    cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{barcode});
                    //Child found locally
                    if (cursor.getCount() > 0 && cursor != null) {


                        cursor.moveToFirst();
                        String village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));

                        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{village_id});
                        if (!(cursor.getCount() > 0)) {
                            pullPlaceFromServer task = new pullPlaceFromServer();
                            task.execute(village_id);
                        }


                        startActivity(scan);
                    } else {
                        if (Utils.isOnline(this)) {
                            add = new ProgressDialog(ScanHandlerActivity.this);
                            add.setTitle(getString(R.string.searching_online));
                            add.setMessage("Barcode not found locally.\nPlease wait for server results ...");
                            add.setCanceledOnTouchOutside(false);
                            add.setCancelable(false);

                            myHandler.sendEmptyMessage(10);
                            //Parse child from server.
                            ChildSynchronization task = new ChildSynchronization(this);
                            task.execute(barcode);
                        } else {
//                            final AlertDialog ad = new AlertDialog.Builder(this).create();
//                            ad.setTitle("Not Found");
//                            ad.setMessage("The inserted barcode does not belong to any child on the local database.\nPlease try scanning while" +
//                                    " device is online.");
//                            ad.setButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ad.dismiss();
//                                }
//                            });
//                            ad.show();
                            startActivity(scan);
                        }
                    }
                    break;
                case ACTIVITY_CHECK_IN:
                    //Do foreground processp
                    overlay.setVisibility(View.VISIBLE);
                    thread = new Thread() {
                        @Override
                        public void run() {

                            synchronized (this) {
                                try {
                                    wait(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());

                                try {
                                    dateNow = URLEncoder.encode(dateNow, "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                DatabaseHandler db = app.getDatabaseInstance();
                                // check if child is in DB , if not than get child data from server
                                if (!db.isChildInDB(barcode)) {
                                    app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());

                                    app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);

                                    int parseResult = app.parseChildCollectorSearchByBarcode(barcode);
                                    if (parseResult == 2) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.not_found), getString(R.string.barcode_does_not_exist));
                                                submitField.setText("");
                                            }
                                        });
                                        return;
                                    } else if (parseResult == 3) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.msg_error), getString(R.string.error_retrieving_child_data));
                                                submitField.setText("");
                                            }
                                        });
                                        return;
                                    }

                                    parseHFIDWhenNotInDb(db, app);


                                }
                                // this should never be null in this part of the app according to the lines of code above
                                String childId = db.getChildIdByBarcode(barcode);
                                if (db.isChildToBeAddedInVaccinationQueue(childId)) {

                                    ContentValues cv = new ContentValues();
                                    cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childId);
                                    cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateNow);

                                    if (db.addChildToVaccinationQueue(cv) > -1) {
                                        app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Child was not to be added to queue", Toast.LENGTH_LONG).show();
                                            overlay.setVisibility(View.GONE);
                                            submitField.setText("");
                                        }
                                    });
                                }

                                //

                                app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Child checkin finished", Toast.LENGTH_LONG).show();
                                        overlay.setVisibility(View.GONE);
                                        submitField.setText("");
                                    }
                                });

                            }
                        }
                    };

                    thread.start();

                    //Do the backgroud app logic
                    break;
                case ACTIVITY_REGISTER_CHILD_SCAN:
                    Intent register_child_scan = new Intent(getApplicationContext(), RegisterChildActivity.class);
                    register_child_scan.putExtra("result", barcode);
                    startActivity(register_child_scan);
                    break;
            }
        } else {
            toastMessage("Wrong barcode format");
        }

    }

    public boolean barcodeCheck(String barcode) {
        return true;
    }

    //on result pass it to next guy
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e("onActivityResult", "onActivityResult");
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                BackboneApplication app = (BackboneApplication) getApplication();
                switch (app.getCurrentActivity()) {
                    case ACTIVITY_SCAN:
                        Intent scan = new Intent(getApplicationContext(), ScanResultActivity.class);
                        scan.putExtra("result", scanContent);

                        DatabaseHandler mydb = app.getDatabaseInstance();
                        Cursor cursor = null;
                        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{scanContent});
                        //Child found locally
                        if (cursor.getCount() > 0 && cursor != null) {


                            cursor.moveToFirst();
                            String village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));

                           if(village_id!=null)
                           { cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{village_id});
                            if (!(cursor.getCount() > 0)) {
                                pullPlaceFromServer task = new pullPlaceFromServer();
                                task.execute(village_id);
                            }
                           }


                            startActivity(scan);
                        } else {
                            if (Utils.isOnline(this)) {
                                add = new ProgressDialog(ScanHandlerActivity.this);
                                add.setTitle(getString(R.string.searching_online));
                                add.setMessage(getString(R.string.barcode_not_found_locally));
                                add.setCanceledOnTouchOutside(false);
                                add.setCancelable(false);

                                myHandler.sendEmptyMessage(10);
                                //Parse child from server.
                                ChildSynchronization task = new ChildSynchronization(this);
                                task.execute(scanContent);
                            } else {
//                                final AlertDialog ad = new AlertDialog.Builder(this).create();
//                                ad.setTitle("Not Found");
//                                ad.setMessage("The inserted barcode does not belong to any child on the local database.\nPlease try scanning while" +
//                                        " device is online.");
//                                ad.setButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        ad.dismiss();
//                                    }
//                                });
//                                ad.show();
                                startActivity(scan);
                            }
                        }
                        break;
                    case ACTIVITY_CHECK_IN:
                        onBarcodeInput(scanContent);
                        break;
                    case ACTIVITY_REGISTER_CHILD_SCAN:
                        Intent register_child_scan = new Intent(getApplicationContext(), RegisterChildActivity.class);
                        register_child_scan.putExtra("result", scanContent);
                        startActivity(register_child_scan);
                        break;
                }
            }
            //TODO check the Zxing library,check this link for solution of this bug: https://github.com/journeyapps/zxing-android-embedded/issues/42
        }


    }

    public void fetchScanResultsFromServer(final String barcode) {

        handler = new Handler();
        final BackboneApplication app = (BackboneApplication) getApplication();
        DatabaseHandler db = app.getDatabaseInstance();

        thread = new Thread() {
            @Override
            public void run() {
                int parseChildByBarcode = app.parseChildCollectorSearchByBarcode(barcode);
                if (parseChildByBarcode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad = new AlertDialog.Builder(ScanHandlerActivity.this).create();
                            ad.setTitle(getString(R.string.not_found));
                            ad.setMessage(getString(R.string.barcode_non_registered));
                            ad.setButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });
                            ad.show();
                        }
                    });
                    return;
                } else if (parseChildByBarcode == 3) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad = new AlertDialog.Builder(ScanHandlerActivity.this).create();
                            ad.setTitle("Error");
                            ad.setMessage(getString(R.string.error_comunication_server));
                            ad.setButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });
                            ad.show();
                        }
                    });
                    return;
                }
                scan_fetch = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanHandlerActivity.this, "Fetching child from server ...", Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseHandler db = app.getDatabaseInstance();
                String childId = db.getChildIdByBarcode(barcode);
                if (db.isChildToBeAddedInVaccinationQueue(childId)) {

                    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());

                    ContentValues cv = new ContentValues();
                    cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childId);
                    cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateNow);

                    if (db.addChildToVaccinationQueue(cv) > -1) {
                        app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * This method is used to set an image resource to an image view and hide its container after some time has passed
     *
     * @param delay      the delay
     * @param resourceId the resourceId
     * @param iv         the imageView to which to set the resource
     */
    private void delayVisibilityGoneChange(final long delay, final int resourceId, final ImageView iv, final ViewGroup vg, final String alertTitle, final String alertContent) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageResource(resourceId);
                        }
                    });
                    sleep(delay);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog ad = new AlertDialog.Builder(ScanHandlerActivity.this).create();
                            ad.setTitle(alertTitle);
                            ad.setMessage(alertContent);
                            ad.setButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad.dismiss();
                                }
                            });
                            ad.show();
                            vg.setVisibility(View.GONE);
                            iv.setImageResource(R.drawable.on_check_in);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        private ChildSynchronizationListener listener;

        public ChildSynchronization(ChildSynchronizationListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            int parseChildByBarcode = 0;
            String village_id, hf_id;
            for (String barcode : params) {
                parseChildByBarcode = application.parseChildCollectorSearchByBarcode(barcode);
                if (parseChildByBarcode != 2 && parseChildByBarcode != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();

                    parseHFIDWhenNotInDb(db, application);

                    Cursor cursor = null;
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{barcode});
                    if (cursor.getCount() > 0) {
                        // used to fix the case of handler delivering maybe an empty message
                        handleBarcode = barcode;
                        cursor.moveToFirst();
                        village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));
                        hf_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID));

                        int found = 0;
                        List<HealthFacility> a = db.getAllHealthFacility();
                        for (HealthFacility b : a) {
                            if (b.getId().equalsIgnoreCase(hf_id)) {
                                found = 1;
                            }
                        }

                        if (found == 0) {
                            application.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                Log.d("Search. Parsing custom village_id", village_id);
                                application.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return parseChildByBarcode;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //super.onPostExecute(result);
            listener.onTaskCompleted(result);
            myHandler.sendEmptyMessage(result);
            try {
                submitField.setText("");
            } catch (Exception e) {
            }
            //parseChildByBarcode = result;
        }
    }

    private class pullPlaceFromServer extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getApplication();

            for (String id : params) {
                application.parsePlaceById(id);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("Scan","u be triger");
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("Scan","u be triger saveInstanc");
    }
}


