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

package mobile.tiis.appv2;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.appv2.GCMCommunication.CommonUtilities;
import mobile.tiis.appv2.GCMCommunication.ServerUtilities;
import mobile.tiis.appv2.adapters.SingleTextViewAdapter;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.helpers.Utils;
import mobile.tiis.appv2.util.Constants;


/**
 * This activity is responsible for the initial login form and handles the authentication
 * by username / password verification. On first login also handles database filling, else
 * authentication is done by local database.
 *
 * @author Teodor Rupi
 * @author Melisa Aruci
 */
public class LoginActivity extends BackboneActivity implements View.OnClickListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "tiis_registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String TAG ="LoginActivity";
    public static final String TAGG ="selectedLang";

    //layout elements
    private MaterialEditText usernameEditText;
    private MaterialEditText passwordEditText;
    protected Button loginButton;

    private String username;
    private String password;

    //Thread handler
    protected Handler handler;
    private ProgressDialog progressDialog;
    private MaterialSpinner language;
    Locale myLocale;
    ArrayList<String> listLanguage;
    //@Arinela this variable is needed to save instance of activity for later recreation
    int languagePosition ;
    //since the spinner has always an item shown than on item selected gets called automatically when the spinner is created
    // we need this boolean to adjust to this behaviour
    boolean autoSelect = false;

    private SharedPreferences login_preferences;
    public static final String LOGINPREFERENCE = "loginPrefs" ;


    private static AsyncHttpClient client = new AsyncHttpClient();
    final int DEFAULT_TIMEOUT = 6000000;
    public static String regId;
    private GoogleCloudMessaging gcm;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    private DatabaseHandler databaseHandler;
    private BackboneApplication app;




    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the appv2 needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if appv2 was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // appv2 version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(HomeActivityRevised.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    regId = gcm.register(CommonUtilities.SENDER_ID);
                    msg = "Device registered,sender ID= " + CommonUtilities.SENDER_ID + "| registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your appv2.
                    // The request to your server should be authenticated if your appv2
                    // is using accounts.
//                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    registerInBackground();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg.toString());
            }


        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        final Context context = this;
        mRegisterTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                /**
                 * Register on our server
                 * On server creates a new user
                 */
                ServerUtilities.register(context, regId);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
                Log.d(TAG, "The appv2 has been registered on ADR Hakiki dawa Server");
            }
        };
        mRegisterTask.execute();
    }

    /**
     * Stores the registration ID and appv2 versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on appv2 version " + appVersion);
        Log.d(TAG, "Saving reg ID  " + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * function to check if there is an internet connectivity
     */
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.login_activity);


        // We load the KeyStore
        try {
            /// We initialize a default Keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            // We initialize a new SSLSocketFacrory
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            // We set that all host names are allowed in the socket factory
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // We set the SSL Factory
            client.setSSLSocketFactory(socketFactory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }




        client.setTimeout(DEFAULT_TIMEOUT);
        client.setMaxConnections(20);

        app =(BackboneApplication)getApplication();
        databaseHandler =app.getDatabaseInstance();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            GCMRegistrar.checkDevice(this);
            GCMRegistrar.checkManifest(this);
            if (isInternetAvailable()) {
                /**
                 * registering the appv2 to Google Cloud Messaging
                 */
                regId = getRegistrationId(getApplicationContext());
                Log.d(TAG,"regID = "+regId);
                if (regId.equals("")) {
                    registerInBackground();
                }
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }


        //DatabaseHandler.getDBFile(this);
        //Delete vaccinationQueueRows that are not from today
        TextView titleText = (TextView) findViewById(R.id.login_screen_title);
        TextView ministryName = (TextView) findViewById(R.id.ministry_name);
        ministryName.setTypeface(BackboneActivity.Rosario_Regular);
        titleText.setTypeface(BackboneActivity.Rosario_Regular);
//        getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.light_blue_600)));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        String dateNow = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        databaseHandler.deleteVaccinationQueueEntriesOfOtherDays(dateNow);

        app.LAST_FRAGMENT = "mobile.tiis.appv2.fragments.HomeFragment";
        app.LAST_FRAGMENT_TITLE = getString(R.string.home);

        //Starting the repeating synchronisation procedure that happens every 5 minutes
        login_preferences = getSharedPreferences(LOGINPREFERENCE, Context.MODE_PRIVATE);

        if(getIntent().hasExtra(BackboneActivity.LANGUAGELOGIN)){
            languagePosition = getIntent().getIntExtra(BackboneActivity.LANGUAGELOGIN,0);
            Log.d(TAGG, "Language Position before select = "+languagePosition+"");
        }

        progressDialog =  new ProgressDialog(this, 0);
        language = (MaterialSpinner) findViewById(R.id.lang_spinner);
        listLanguage = new ArrayList<String>();
        listLanguage.add("Swahili");
        listLanguage.add("English");

        //Get username and password
        usernameEditText = (MaterialEditText) findViewById(R.id.username);
        usernameEditText.setFocusableInTouchMode(true);
        passwordEditText = (MaterialEditText) findViewById(R.id.password);
        passwordEditText.setFocusableInTouchMode(true);

        //Listen for a Login button click
        loginButton = (Button) findViewById(R.id.login_btn);
        SingleTextViewAdapter adapter = new SingleTextViewAdapter(this,R.layout.single_text_spinner_item_drop_down,listLanguage);
        language.setAdapter(adapter);

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        languagePosition = 0;
                        setLocale("sw");
                        Log.d(TAGG, "selected position is " + i + " I put 0");
                        break;
                    case 1:
                        languagePosition = 1;
                        setLocale("en");
                        Log.d(TAGG, "selected position is "+i+" I put 1");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loginButton.setOnClickListener(this);
        getSavedConfigurations();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Remove login information
        usernameEditText.getText().clear();
        passwordEditText.getText().clear();
        //usernameEditText.setText(null);
        // passwordEditText.setText(null);
        usernameEditText.requestFocus();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * This method will get called when the user presses the login
     * button.
     *
     * <p>This method will check with checkRequiredFields to make
     * sure if it will process the click.
     */

    public void onClick(View v) {
        Log.d(TAG,"clicked login");
        //get inserted username and password
        username = usernameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        //check if required fields are not empty
        if(checkRequiredFields())
        {
            Log.d(TAG,"check passed");
            progressDialog.setMessage("Signing in. \nPlease wait ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            loginButton.setEnabled(false);
            //continue with device online
            if(Utils.isOnline(LoginActivity.this))
            {

                boolean loggedIn = false;
                if(databaseHandler.getAllUsers().size()!=0){
                    //Checking if the user had once logged in
                    //check if user is already registered with AccountManager
                    AccountManager accountManager = AccountManager.get(LoginActivity.this);
                    Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

                    //go through all accounts found in Account Manager
                    for (Account account : accounts) {
                        //if there is a match set login as true and go to Home Activity
                        if (account.name.equalsIgnoreCase(username) && accountManager.getPassword(account).equals(password)) {
                            //Activity mobile.tiis.app.LoginActivity has leaked window error was showing
                            //this piece of code handles it, nonetheless in prod time the error  would not show
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putBoolean("secondSyncNeeded", true);
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, LotSettingsActivity.class);

                            Log.d(TAG, "initiating offline for " + username + " password = " + password);
                            app.initializeOffline(username, password);


                            Log.d(TAG, "initiating offline for " + username + " password = " + password);

                            app.setUsername(username);
                            Log.d("supportLog", "call the loggin first time before the account was found");
                            startActivity(intent);
                            loggedIn = true;
                        }
                    }
                }else{
                    SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    preferenceManager.edit().putBoolean("synchronization_needed", true);
                }
                if (!loggedIn){

                    //build webservice url
                    StringBuilder webServiceLoginURL = createWebServiceLoginURL(username, password,regId);

                    //call web service to pull user info and send to account manager
                    try{
                        startWebService(webServiceLoginURL ,username, password);
                    }catch (NullPointerException e){
                        startWebService(webServiceLoginURL ,username, password);
                    }
                }

            }else{


                //check if user is already registered with AccountManager
                AccountManager accountManager = AccountManager.get(LoginActivity.this);
                Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
                boolean loggedIn = false;
                if(databaseHandler.getAllUsers().size()!=0) {

                    //go through all accounts found in AM
                    for (Account account : accounts) {
                        //if there is a match set login as true and go to Home Activity
                        if (account.name.equalsIgnoreCase(username) && accountManager.getPassword(account).equals(password)) {
                            //Activity mobile.tiis.app.LoginActivity has leaked window error was showing
                            //this piece of code handles it, nonetheless in prod time the error  would not show
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putBoolean("secondSyncNeeded", true);
                            editor.commit();

                            Intent intent = new Intent(LoginActivity.this, LotSettingsActivity.class);
                            app.setUsername(username);
                            Log.d(TAG, "initiating offline for " + username + " password = " + password);
                            app.initializeOffline(username, password);
                            startActivity(intent);
                            loggedIn = true;
                        }
                    }
                }else{
                    SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    preferenceManager.edit().putBoolean("synchronization_needed", true);
                }
                //if login failed, show error
                if(!loggedIn){
                    progressDialog.dismiss();
                    toastMessage("Login failed.\nPlease check your details or be online for first login!");
                    loginButton.setEnabled(true);
                }
            }
        }
    }

    /**
     * This method will take the url built to use the webservice
     * and will try to parse JSON from the webservice stream to get
     * the user and password if they are correct or not. In case correct, fills
     * the Android Account Manager.
     *
     * <p>This method will throw a Toast message when user and password
     * are not valid
     *
     */

    protected void startWebService(final CharSequence loginURL , final String username, final String password){
        client.setBasicAuth(username, password, true);

        //new handler in case of login error in the thread
        handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try
                {
                    int balanceCounter = 0;
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(loginURL.toString());
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext())+loginURL.toString());
                    httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP));
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    Log.d("", loginURL.toString());


                    ByteArrayInputStream bais = Utils.getMultiReadInputStream(inputStream);
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext())+Utils.getStringFromInputStreamAndLeaveStreamOpen(bais));
                    bais.reset();
                    JsonFactory factory = new JsonFactory();
                    JsonParser jsonParser = factory.createJsonParser(bais);
                    JsonToken token = jsonParser.nextToken();

                    if (token == JsonToken.START_OBJECT){
                        balanceCounter++;
                        boolean idNextToHfId =false;
                        while (!(balanceCounter==0))
                        {
                            token = jsonParser.nextToken();

                            if(token == JsonToken.START_OBJECT){
                                balanceCounter++;
                            }
                            else if(token == JsonToken.END_OBJECT){
                                balanceCounter--;
                            }
                            else if(token == JsonToken.FIELD_NAME){
                                String object = jsonParser.getCurrentName();
                                switch (object){
                                    case "HealthFacilityId":
                                        token=jsonParser.nextToken();
                                        app.setLoggedInUserHealthFacilityId(jsonParser.getText());
                                        Log.d("", "healthFacilityId is: " + jsonParser.getText());
                                        idNextToHfId=true;
                                        break;
                                    case "Firstname":
                                        token=jsonParser.nextToken();
                                        app.setLoggedInFirstname(jsonParser.getText());
                                        Log.d("", "firstname is: " + jsonParser.getText());
                                        break;
                                    case "Lastname":
                                        token=jsonParser.nextToken();
                                        app.setLoggedInLastname(jsonParser.getText());
                                        Log.d("", "lastname is: " + jsonParser.getText());
                                        break;
                                    case "Username":
                                        token=jsonParser.nextToken();
                                        app.setLoggedInUsername(jsonParser.getText());
                                        Log.d("", "username is: " + jsonParser.getText());
                                        break;
                                    case "Lastlogin":
                                        token=jsonParser.nextToken();
                                        Log.d("", "lastlogin is: " + jsonParser.getText());
                                        break;
                                    case "Id":
                                        if(idNextToHfId){
                                            token=jsonParser.nextToken();
                                            app.setLoggedInUserId(jsonParser.getText());
                                            Log.d("", "Id is: " + jsonParser.getText());
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        Account account = new Account(username, ACCOUNT_TYPE);
                        AccountManager accountManager = AccountManager.get(LoginActivity.this);
//                        boolean accountCreated = accountManager.addAccountExplicitly(account, LoginActivity.this.password, null);
                        boolean accountCreated = accountManager.addAccountExplicitly(account, password, null);

                        Bundle extras = LoginActivity.this.getIntent().getExtras();
                        if (extras != null) {
                            if (accountCreated) {  //Pass the new account back to the account manager
                                AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
                                Bundle res = new Bundle();
                                res.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                                res.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                                res.putString(AccountManager.KEY_PASSWORD, password);
                                response.onResult(res);
                            }
                        }

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("secondSyncNeeded", true);
                        editor.commit();

                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.UserColumns.FIRSTNAME, app.getLOGGED_IN_FIRSTNAME());
                        values.put(SQLHandler.UserColumns.LASTNAME, app.getLOGGED_IN_LASTNAME());
                        values.put(SQLHandler.UserColumns.HEALTH_FACILITY_ID, app.getLOGGED_IN_USER_HF_ID());
                        values.put(SQLHandler.UserColumns.ID, app.getLOGGED_IN_USER_ID());
                        values.put(SQLHandler.UserColumns.USERNAME, app.getLOGGED_IN_USERNAME());
                        databaseHandler.addUser(values);

                        Log.d(TAG, "initiating offline for " + username + " password = " + password);
                        app.initializeOffline(username, password);

                        Intent intent;
                        if(prefs.getBoolean("synchronization_needed", true)){
                            Log.d("supportLog", "call the loggin second time before the account was found");
                            intent = new Intent(LoginActivity.this, LotSettingsActivity.class);
                        }else{
                            Log.d("supportLog", "call the loggin second time before the account was found");
                            intent = new Intent(LoginActivity.this, LotSettingsActivity.class);
                            evaluateIfFirstLogin(app.getLOGGED_IN_USER_ID());
                        }
                        app.setUsername(username);

                        startActivity(intent);
                    }
                    //if login failed show error
                    else {
                        handler.post(new Runnable() {
                            public void run() {
                                progressDialog.show();
                                progressDialog.dismiss();
                                toastMessage("Login failed.\nPlease check your details!");
                                loginButton.setEnabled(true);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    handler.post(new Runnable() {
                        public void run() {
                            progressDialog.show();
                            progressDialog.dismiss();
                            toastMessage("Login failed Login failed.\n" +
                                    "Please check your details or your web connectivity");
                            loginButton.setEnabled(true);

                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    /**
     * This method will check if username and passwords fields
     * have not been left empty when trying to login
     *
     * <p>This method will throw a Toast message on empty field(s) which might
     * have to be changed to a real message window instead.
     *
     * @return boolean result weather both fields are NON-empty
     */

    protected boolean checkRequiredFields() {

        boolean result = true;
        Context context = getApplicationContext();
        CharSequence text = "Fields missing: ";

        if (Utils.isStringBlank(username)) {
            result = false;
            text = text + " username";
        }

        if (Utils.isStringBlank(password)) {
            result = false;
            text = text + " password";
        }

        if(!result){
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return result;
    }

    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        refresh.putExtra(BackboneActivity.LANGUAGELOGIN , languagePosition);
        startActivity(refresh);
    }

    private void getSavedConfigurations(){
        SharedPreferences prefs = getSharedPreferences(Constants.CONFIG, MODE_PRIVATE);
        Constants.LimitNumberOfDaysBeforeExpireVal = prefs.getInt(Constants.LimitNumberOfDaysBeforeExpire,30);
        Constants.EligibleForVaccinationVal = prefs.getInt(Constants.EligibleForVaccination,10);
    }

    private void evaluateIfFirstLogin(String userID){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        boolean sameDate = false;

        long mostRecentLoginDay = prefs.getLong("mostRecentLoginDay", 0);
        Calendar mostRecentDate = Calendar.getInstance();
        mostRecentDate.setTime( new Date(mostRecentLoginDay));
        Calendar today = Calendar.getInstance();
        if (mostRecentDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                mostRecentDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                mostRecentDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            sameDate = true;
        }else{
            sameDate = false;
        }

        if (!sameDate){
            editor.putLong("mostRecentLoginDay", today.getTimeInMillis());
            editor.putBoolean("firstLoginOfDaySyncNeeded", true);
            editor.apply();
        }
    }

}
