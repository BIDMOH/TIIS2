package mobile.tiis.app;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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

import com.astuetz.PagerSlidingTabStrip;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import mobile.tiis.app.R;
import mobile.tiis.app.SubClassed.BackHandledFragment;
import mobile.tiis.app.adapters.ChildDetailsViewPager;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.fragments.ChildAppointmentsListFragment;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by issymac on 25/02/16.
 */
public class ChildDetailsActivity extends BackboneActivity implements BackHandledFragment.BackHandlerInterface {

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    private PagerSlidingTabStrip tabs;

    private ViewPager pager;

    private ChildDetailsViewPager adapter;

    private BackHandledFragment selectedFragment;

    public String handlerBarcode = "";

    public Toolbar toolbar;

    public static TextView toolbarTitle;

    String value = "";

    int currentPagerPage = 0;

    private DatabaseHandler mydb;

    boolean barcodeNull = true;

    public static String age = "";

    public String appointmentId = "";

    private Child currentChild;

    private ProgressBar childInfoLoader;

    public boolean navigationClickEventFlag = true;

    public BackboneApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_details_activity);
        setUpView();
        currentChild = null;

        app = (BackboneApplication) getApplication();
        final Bundle extras = getIntent().getExtras();
        mydb = app.getDatabaseInstance();

        Log.d("currentpage", "Here at Details");
        if (extras != null) {
            value = extras.getString(BackboneApplication.CHILD_ID);
            currentPagerPage    = extras.getInt("current");
            handlerBarcode      = extras.getString("barcode");
            appointmentId       = extras.getString("appointmentId");
            currentChild        = (Child)getIntent().getSerializableExtra("myChild");


            if (true){
                if (value == null || value.equalsIgnoreCase("")) {
                    if (extras.getString("barcode") != null) {
                        /**
                         * Get Child Information and store all into one place
                         */
                        new AsyncTask<Void, Void, Void>(){

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                currentChild = null;
                                value = extras.getString("barcode");
                                handlerBarcode  = value;
                                childInfoLoader.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                                        new String[]{String.valueOf(value)});
                                if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                                    getChildIdCursor.moveToFirst();
                                    currentChild = getChildFromCursror(getChildIdCursor);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                String name = currentChild.getFirstname1()+" "+currentChild.getFirstname2()+" "+currentChild.getLastname1();
                                toolbarTitle.setText(name);
                                initializePagers();
                                childInfoLoader.setVisibility(View.GONE);
                            }

                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                    }else {
                        toastMessage(getString(R.string.empty_barcode));
                        finish();
                    }
                }
                else{
                    new AsyncTask<Void, Void, Void>(){
                        Cursor cursor = null;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            childInfoLoader.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                                    new String[]{String.valueOf(value)});
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                currentChild = getChildFromCursror(cursor);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            String name = currentChild.getFirstname1()+" "+currentChild.getFirstname2()+" "+currentChild.getLastname1();
                            toolbarTitle.setText(name);
                            initializePagers();
                            childInfoLoader.setVisibility(View.GONE);
                        }

                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }else{
                String name = currentChild.getFirstname1()+" "+currentChild.getFirstname2()+" "+currentChild.getLastname1();
                toolbarTitle.setText(name);
                childInfoLoader.setVisibility(View.VISIBLE);
                childInfoLoader.setVisibility(View.GONE);
                initializePagers();
            }

        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

    }

    private void initializePagers(){
        adapter = new ChildDetailsViewPager(this, getSupportFragmentManager(), currentChild, appointmentId);
        pager.setOffscreenPageLimit(6);


        pager.setAdapter(adapter);

        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);

        pager.setCurrentItem(currentPagerPage);
    }

    public static void changeTitle(String title){
        toolbarTitle.setText(title);
    }


    public void setUpView(){
        toolbar         = (Toolbar) findViewById(R.id.child_details_activity_toolbar);
        toolbarTitle    = (TextView) findViewById(R.id.child_details_activity_toolbar_title);
        tabs            = (PagerSlidingTabStrip) findViewById(R.id.tabs_stock);
        pager           = (ViewPager) findViewById(R.id.pager_stock);
        childInfoLoader = (ProgressBar) findViewById(R.id.child_info_loader);
        childInfoLoader .setVisibility(View.GONE);
    }

    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.details_wifi_icon);
            if(Utils.isOnline(context)){
                app.setOnlineStatus(true);
                wifi_logo.setImageResource(R.drawable.network_on);
            }
            else{
                app.setOnlineStatus(false);
                wifi_logo.setImageResource(R.drawable.network_off);
            }
        }
    };

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
        }catch (Exception e){
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
        app = (BackboneApplication)this.getApplication();
        if (id == android.R.id.home) {
            if (app.saveNeeded){
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
                                        onBackPressed();
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
            }
            else{
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
    protected void onPause(){
        super.onPause();
        unregisterReceiver(status_receiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

}
