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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.postman.RoutineAlarmReceiver;

/**
 * Created by Teodor on 1/31/2015.
 */
public class HomeActivity extends BackboneActivity {

    private static final String TAG = "HomeActivity";
    protected Button scanBtn;
    private TextView username;
    private TextView formatTxt, contentTxt;

    IntentFilter filter = new IntentFilter();
    private boolean mainSyncNeeded = true;
    private SharedPreferences sync_preferences;


    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);


        Log.e(TAG, TAG);
        setContentView(R.layout.home_activity);

        BackboneApplication app = (BackboneApplication) getApplication();

        Button signout = (Button)findViewById(R.id.sign_out_homepage_button);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        scanBtn = (Button) findViewById(R.id.home_btn_scan);
        username = (TextView) findViewById(R.id.home_username);

        if (getActionBar() != null) getActionBar().hide();

        //username.setText(app.getUsername());
//        username.setText(app.getLOGGED_IN_FIRSTNAME() + " " + app.getLOGGED_IN_LASTNAME() + " " + "(" + app.getUsername() + ")");

        sync_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean sync_needed = true, secondSyncNeeded = false, firstLoginOfDaySyncNeeded = false;
        sync_needed = sync_preferences.getBoolean("synchronization_needed", true);
        secondSyncNeeded = sync_preferences.getBoolean("secondSyncNeeded", false);
        firstLoginOfDaySyncNeeded = sync_preferences.getBoolean("firstLoginOfDaySyncNeeded", false);
        if(app.getLOGGED_IN_FIRSTNAME() == null && app.getLOGGED_IN_LASTNAME() == null){
            signout.performClick();
        }else {
            //Do main sync if necessary
            if (Utils.isOnline(this) && sync_needed && app.getMainSyncronizationNeededStatus()) {
                new mainSynchronisation().execute(0);
            }else if(Utils.isOnline(this) && firstLoginOfDaySyncNeeded){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new firstLoginOfDaySynchronisation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
                else
                    new firstLoginOfDaySynchronisation().execute(0);
            }else if(Utils.isOnline(this) && secondSyncNeeded) {
                Log.e("RUBIN","RUBIN secondSyncNeeded");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new updateSynchronisation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
                else
                    new updateSynchronisation().execute(0);

                //Starting the repeating synchronisation procedure that happens every 10 minutes
                // and pulls changes done to children or children added
                RoutineAlarmReceiver.setAlarmCheckForChangesInChild(this);
            }


            //Else do Second Sync call
//        else if(Utils.isOnline(this)){
//            Intent login_sync = new Intent(this, SynchronisationServiceOnLogin.class);
//            startService(login_sync);
//}

            try {
                String filePath = Environment.getExternalStorageDirectory() + "/giis_logcat.txt";
                Runtime.getRuntime().exec(new String[]{"logcat", "-v", filePath, "*:V", "*:S"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        BackboneApplication app = (BackboneApplication) getApplication();
        Log.d("Saving", "Saved" + app.getMainSyncronizationNeededStatus());
        savedInstanceState.putBoolean("mainSync", app.getMainSyncronizationNeededStatus());
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
        SharedPreferences.Editor editPrefs = sync_preferences.edit();
        BackboneApplication app = (BackboneApplication) getApplication();
        boolean sync_needed = true;
        if (sync_preferences.contains("synchronization_needed"))
        {
            sync_needed = sync_preferences.getBoolean("synchronization_needed", false);
        }
        editPrefs.putBoolean("synchronization_needed", sync_needed);

        boolean secondSyncNeeded = true;
        if (sync_preferences.contains("secondSyncNeeded"))
        {
            secondSyncNeeded = sync_preferences.getBoolean("secondSyncNeeded", false);
        }
        editPrefs.putBoolean("secondSyncNeeded", secondSyncNeeded);
        editPrefs.apply();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        SharedPreferences.Editor editPrefs = sync_preferences.edit();
        BackboneApplication app = (BackboneApplication) getApplication();
        boolean sync_needed = true;
        if (sync_preferences.contains("synchronization_needed"))
        {
            sync_needed = sync_preferences.getBoolean("synchronization_needed", false);
        }
        editPrefs.putBoolean("synchronization_needed", sync_needed);
        boolean secondSyncNeeded = true;
        if (sync_preferences.contains("secondSyncNeeded"))
        {
            secondSyncNeeded = sync_preferences.getBoolean("secondSyncNeeded", false);
        }
        editPrefs.putBoolean("secondSyncNeeded", secondSyncNeeded);
        editPrefs.apply();
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

    private class mainSynchronisation extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!DatabaseHandler.dbPreinstalled)Toast.makeText(getApplicationContext(), "Database synchronization started.", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            if (!DatabaseHandler.dbPreinstalled) {
                BackboneApplication application = (BackboneApplication) getApplication();

                application.parsePlace();
                application.parseBirthplace();
                application.parseStatus();
                application.parseWeight();
                application.parseNonVaccinationReason();
                application.parseAgeDefinitions();
                application.parseItem();
                application.parseScheduledVaccination();
                application.parseDose();
                application.parseHealthFacility();
                application.parseItemLots();
                application.parseStock();
                application.parseStockAdjustmentReasons();


                String hfidFoundInVaccEvOnlyAndNotInHealthFac = application.getDatabaseInstance().getHFIDFoundInVaccEvAndNotInHealthFac();
                if (hfidFoundInVaccEvOnlyAndNotInHealthFac != null) {
                    application.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
                }

                application.parseChildCollector();

                String placesFoundInChildOnlyAndNotInPlace = application.getDatabaseInstance().getDomicilesFoundInChildAndNotInPlace();
                if(placesFoundInChildOnlyAndNotInPlace != null){
                    application.parsePlacesThatAreInChildAndNotInPlaces(placesFoundInChildOnlyAndNotInPlace);
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.e("SYNC FINISHED", "Database synchronization finished.");

            if (!DatabaseHandler.dbPreinstalled)Toast.makeText(getApplicationContext(), "mainSynchronisation finished.", Toast.LENGTH_LONG).show();

            BackboneApplication app = (BackboneApplication) getApplication();

            SharedPreferences.Editor editor = sync_preferences.edit();
            editor.putBoolean("synchronization_needed", false);
            editor.putBoolean("secondSyncNeeded", false);
            editor.apply();
            app.setMainSyncronizationNeededStatus(false);
            //Starting the repeating synchronisation procedure that happens every week
            // and pulls changes done to some main tables
            RoutineAlarmReceiver.setAlarmWeeklyUpdateBaseTables(HomeActivity.this.getApplicationContext());
        }
    }

    private class updateSynchronisation extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast toast = Toast.makeText(getApplicationContext(), "Database synchronization started.", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            application.continuousModificationParser();

            if(application.getLOGGED_IN_USER_ID()!=null && !application.getLOGGED_IN_USER_ID().equals("")){
                application.getGetChildByIdList();
            }

            Log.e("getting places","getting places that are not found in the table but are in the childrens records");
            String placesFoundInChildOnlyAndNotInPlace = application.getDatabaseInstance().getDomicilesFoundInChildAndNotInPlace();
            if(placesFoundInChildOnlyAndNotInPlace != null){
                application.parsePlacesThatAreInChildAndNotInPlaces(placesFoundInChildOnlyAndNotInPlace);
            }

            String hfidFoundInVaccEvOnlyAndNotInHealthFac = application.getDatabaseInstance().getHFIDFoundInVaccEvAndNotInHealthFac();
            if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
                application.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            }

            application.parseStock();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            Log.e("SYNC FINISHED","Database synchronization finished.");
            Toast toast = Toast.makeText(getApplicationContext(), "updateSynchronisation finished.", Toast.LENGTH_LONG);
            toast.show();

            SharedPreferences.Editor editor = sync_preferences.edit();
            editor.putBoolean("secondSyncNeeded", false);
            editor.commit();
        }
    }

    private class firstLoginOfDaySynchronisation extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast toast = Toast.makeText(getApplicationContext(), "First synchronization of the day started.", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            BackboneApplication application = (BackboneApplication) getApplication();
            application.firstLoginOfTheDay();
            if(application.getLOGGED_IN_USER_ID()!=null && !application.getLOGGED_IN_USER_ID().equals("")){
                application.getGetChildByIdList();
            }

            Log.e("getting places","getting places that are not found in the table but are in the childrens records");
            String placesFoundInChildOnlyAndNotInPlace = application.getDatabaseInstance().getDomicilesFoundInChildAndNotInPlace();
            if(placesFoundInChildOnlyAndNotInPlace != null){
                application.parsePlacesThatAreInChildAndNotInPlaces(placesFoundInChildOnlyAndNotInPlace);
            }

            String hfidFoundInVaccEvOnlyAndNotInHealthFac = application.getDatabaseInstance().getHFIDFoundInVaccEvAndNotInHealthFac();
            if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
                application.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            }

            application.parseStock();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            Log.e("SYNC FINISHED", "Database synchronization finished. FirstSyncOfDay");
            Toast toast = Toast.makeText(getApplicationContext(), "firstLoginOfDaySynchronisation finished.", Toast.LENGTH_LONG);
            toast.show();

            SharedPreferences.Editor editor = sync_preferences.edit();
            editor.putBoolean("firstLoginOfDaySyncNeeded", false);
            editor.apply();
        }
    }

}


