package mobile.tiis.appV2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mobile.tiis.appV2.CustomViews.CustomTabStrip;
import mobile.tiis.appV2.CustomViews.SwipeControllableViewPager;
import mobile.tiis.appV2.SubClassed.BackHandledFragment;
import mobile.tiis.appV2.adapters.ChildDetailsViewPager;
import mobile.tiis.appV2.base.BackboneActivity;
import mobile.tiis.appV2.base.BackboneApplication;
import mobile.tiis.appV2.database.DatabaseHandler;
import mobile.tiis.appV2.database.SQLHandler;
import mobile.tiis.appV2.entity.Child;
import mobile.tiis.appV2.helpers.Utils;
import mobile.tiis.appV2.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;


/**
 * Created by issymac on 25/02/16.
 */
public class ChildDetailsActivity extends BackboneActivity implements BackHandledFragment.BackHandlerInterface {
    private static final String TAG = ChildDetailsActivity.class.getSimpleName();
    public static TextView toolbarTitle;
    public static String age = "";
    public String handlerBarcode = "";
    public Toolbar toolbar;
    public String appointmentId = "";
    public boolean navigationClickEventFlag = true;
    public BackboneApplication app;
    String value = "";
    int currentPagerPage = 0;
    boolean barcodeNull = true;
    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView) findViewById(R.id.details_wifi_icon);
            if (Utils.isOnline(context)) {
                app.setOnlineStatus(true);
                wifi_logo.setImageResource(R.drawable.network_on);
            } else {
                app.setOnlineStatus(false);
                wifi_logo.setImageResource(R.drawable.network_off);
            }
        }
    };
    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;
    private CustomTabStrip tabs;
    private SwipeControllableViewPager pager;
    private ChildDetailsViewPager adapter;
    private BackHandledFragment selectedFragment;
    private DatabaseHandler mydb;
    private boolean isNewChild;
    private Child currentChild;
    private ProgressBar childInfoLoader;
    private Looper backgroundLooper;

    public static void changeTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_details_activity);
        setUpView();
        currentChild = null;

        Log.d(TAG, "imeingia ");


        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        app = (BackboneApplication) getApplication();
        final Bundle extras = getIntent().getExtras();
        mydb = app.getDatabaseInstance();

        Log.d("currentpage", "Here at Details");
        if (extras != null) {
            value = extras.getString(BackboneApplication.CHILD_ID);
            currentPagerPage = extras.getInt("current");
            handlerBarcode = extras.getString("barcode");
            appointmentId = extras.getString("appointmentId");
            isNewChild = extras.getBoolean("isNewChild", false);
            currentChild = (Child) getIntent().getSerializableExtra("myChild");

            Log.d("currentpage", "extras not null");
            if (value == null || value.equalsIgnoreCase("")) {
                if (extras.getString("barcode") != null) {
                    Log.d("currentpage", "value is null");
                    /**
                     * Get Child Information and store all into one place
                     */

                    currentChild = null;
                    value = extras.getString("barcode");
                    handlerBarcode = value;
                    childInfoLoader.setVisibility(View.VISIBLE);


                    //adding implementation for RXAndroid to substitute the use of AsyncTasks
                    Observable.defer(new Func0<Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call() {
                            // Do some long running operation
                            Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                                    new String[]{String.valueOf(value)});
                            if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                                getChildIdCursor.moveToFirst();
                                currentChild = getChildFromCursror(getChildIdCursor);
                            }
                            return Observable.just(true);
                        }
                    })// Run on a background thread
                            .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                            // Be notified on the main thread
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(this.<Boolean>bindToLifecycle())
                            .subscribe(new Subscriber<Boolean>() {
                                @Override
                                public void onCompleted() {
                                    Log.d(TAG, "onCompleted()");

                                    String name = "";
                                    try {
                                        name = currentChild.getFirstname1() + " " + currentChild.getFirstname2() + " " + currentChild.getLastname1();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    toolbarTitle.setText(name);
                                    initializePagers();
                                    childInfoLoader.setVisibility(View.GONE);

                                    Log.d("currentpage", "current child health facility id = " + currentChild.getHealthcenterId());
                                    Log.d("currentpage", "current child health facility id = " + app.getLOGGED_IN_USER_HF_ID());

                                    if (currentChild.getHealthcenterId().equals(app.getLOGGED_IN_USER_HF_ID())) {
                                        Log.d("currentpage", "equal");
                                        try {
                                            if (currentChild.getChildCumulativeSn().equals("") || currentChild.getChildRegistryYear().equals("") || currentChild.getMotherHivStatus().equals("") || currentChild.getMotherTT2Status().equals("")) {
                                                enableViewPagerPaging(false);
                                                Log.d("currentpage", "disabling viewpager");
                                                Toast.makeText(ChildDetailsActivity.this, "Please edit and fill all relevant fields before continuing", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d("currentpage", "all is well");
                                            }
                                        } catch (NullPointerException e) {

                                            Log.d(TAG, "disabling viewpager due to null pointer exception");
                                            pager.setPagingEnabled(false);
                                            tabs.setDisabled(true);
                                            Toast.makeText(ChildDetailsActivity.this, "Please edit and fill all relevant fields before continuing", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError()", e);
                                }

                                @Override
                                public void onNext(Boolean string) {
                                    Log.d(TAG, "onNext(" + string + ")");
                                }
                            });

                } else {
                    toastMessage(getString(R.string.empty_barcode));
                    finish();
                }
            } else {
                Log.d("currentpage", "values not null");


                childInfoLoader.setVisibility(View.VISIBLE);
                Observable.defer(new Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        // Do some long running operation
                        Cursor cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                                new String[]{String.valueOf(value)});
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            currentChild = getChildFromCursror(cursor);
                        }
                        return Observable.just(true);
                    }
                })// Run on a background thread
                        .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                        // Be notified on the main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(this.<Boolean>bindToLifecycle())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "onCompleted()");
                                String name = currentChild.getFirstname1() + " " + currentChild.getFirstname2() + " " + currentChild.getLastname1();
                                toolbarTitle.setText(name);
                                initializePagers();
                                childInfoLoader.setVisibility(View.GONE);

                                Log.d("currentpage", "values not null current child health facility id = " + currentChild.getHealthcenterId());
                                Log.d("currentpage", "values not null current child health facility id = " + app.getLOGGED_IN_USER_HF_ID());
                                if (currentChild.getHealthcenterId().equals(app.getLOGGED_IN_USER_HF_ID())) {
                                    Log.d("currentpage", "equal");
                                    try {
                                        if (!isNewChild) {
                                            if (currentChild.getChildCumulativeSn().equals("") || currentChild.getChildRegistryYear().equals("") || currentChild.getMotherHivStatus().equals("") || currentChild.getMotherTT2Status().equals("")) {
                                                enableViewPagerPaging(false);
                                                Log.d("currentpage", "disabling viewpager");
                                                Toast.makeText(ChildDetailsActivity.this, "Please edit and fill all relevant fields before continuing", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d("currentpage", "all is well");
                                            }
                                        }
                                    } catch (NullPointerException e) {

                                        Log.d(TAG, "disabling viewpager due to null pointer exception");
                                        pager.setPagingEnabled(false);
                                        tabs.setDisabled(true);
                                        Toast.makeText(ChildDetailsActivity.this, "Please edit and fill all relevant fields before continuing", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError()", e);
                            }

                            @Override
                            public void onNext(Boolean string) {
                                Log.d(TAG, "onNext(" + string + ")");
                            }
                        });

            }

        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

    }

    private void initializePagers() {
        adapter = new ChildDetailsViewPager(this, getSupportFragmentManager(), currentChild, appointmentId, isNewChild);
        pager.setOffscreenPageLimit(6);


        pager.setAdapter(adapter);

        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);

        pager.setCurrentItem(currentPagerPage);
    }

    public void setUpView() {
        toolbar = (Toolbar) findViewById(R.id.child_details_activity_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.child_details_activity_toolbar_title);
        tabs = (CustomTabStrip) findViewById(R.id.tabs_stock);
        pager = (SwipeControllableViewPager) findViewById(R.id.pager_stock);
        childInfoLoader = (ProgressBar) findViewById(R.id.child_info_loader);
        childInfoLoader.setVisibility(View.GONE);
    }

    public Child getChildFromCursror(Cursor cursor) {
        Child parsedChild = new Child();
        parsedChild.setId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID)));
        parsedChild.setBarcodeID(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BARCODE_ID)));
        parsedChild.setTempId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.TEMP_ID)));
        parsedChild.setFirstname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1)));
        parsedChild.setFirstname2(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2)));
        parsedChild.setLastname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1)));
        parsedChild.setBirthdate(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHDATE)));
        parsedChild.setMotherFirstname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME)));
        parsedChild.setMotherLastname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME)));
        parsedChild.setPhone(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.PHONE)));
        parsedChild.setNotes(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.NOTES)));
        parsedChild.setBirthplaceId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHPLACE_ID)));
        parsedChild.setGender(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.GENDER)));

        parsedChild.setMotherHivStatus(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_VVU_STS)));
        parsedChild.setMotherTT2Status(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_TT2_STS)));
        parsedChild.setChildCumulativeSn(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER)));
        parsedChild.setChildRegistryYear(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR)));

        Cursor cursor1 = mydb.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }

        parsedChild.setDomicile(villagestr);
        parsedChild.setHealthcenterId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
        try {
            Cursor cursor3 = mydb.getReadableDatabase().rawQuery("SELECT * FROM health_facility WHERE ID=?", new String[]{parsedChild.getHealthcenterId()});
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                hfstr = cursor3.getString(cursor3.getColumnIndex(SQLHandler.HealthFacilityColumns.NAME));
            }
        } catch (Exception e) {
            hfstr = "";
        }
        parsedChild.setHealthcenter(hfstr);

        parsedChild.setStatusId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.STATUS_ID)));
        Cursor cursor4 = mydb.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        app = (BackboneApplication) this.getApplication();
        if (id == android.R.id.home) {
            if (app.saveNeeded) {
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.custom_alert_dialogue, null);

                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
                alertDialogBuilder.setView(promptsView);

                TextView message = (TextView) promptsView.findViewById(R.id.dialogMessage);
                message.setText("Are you sure you want to Leave Without Saving?");

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        app.saveNeeded = false;
                                        navigationClickEventFlag = false;

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        navigationClickEventFlag = true;
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            } else {
                navigationClickEventFlag = false;
            }

            Log.d("cek", "home selected");
        }

        return navigationClickEventFlag;
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

    public void enableViewPagerPaging(boolean status) {
        pager.setPagingEnabled(status);
        tabs.setDisabled(!status);
    }

    public void updateadapters(){
        Log.d(TAG,"notifying the adapter that the dataset has changed");
        adapter.notifyDataSetChanged();
    }

}
