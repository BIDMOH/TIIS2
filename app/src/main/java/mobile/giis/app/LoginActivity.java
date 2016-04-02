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

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.ChildCollector;
import mobile.giis.app.entity.ChildCollector2;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.postman.RoutineAlarmReceiver;
import mobile.giis.app.util.Constants;


/**
 * This activity is responsible for the initial login form and handles the authentication
 * by username / password verification. On first login also handles database filling, else
 * authentication is done by local database.
 *
 * @author Teodor Rupi
 * @author Melisa Aruci
 */
public class LoginActivity extends BackboneActivity implements View.OnClickListener {

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
    private View rootView;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.login_activity);
        rootView = findViewById(R.id.rootView);


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

        BackboneApplication app = (BackboneApplication)getApplication();
        String dateNow = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        app.getDatabaseInstance().deleteVaccinationQueueEntriesOfOtherDays(dateNow);

        app.LAST_FRAGMENT = "mobile.giis.app.fragments.HomeFragment";
        app.LAST_FRAGMENT_TITLE = "Home";

        //Starting the repeating synchronisation procedure that happens every 5 minutes
        RoutineAlarmReceiver.setPostmanAlarm(this);

        login_preferences = getSharedPreferences(LOGINPREFERENCE, Context.MODE_PRIVATE);

        if (login_preferences.getBoolean("isLoggedIn", false)){
            Intent intent = new Intent(LoginActivity.this, HomeActivityRevised.class);
            startActivity(intent);
            finish();
        }

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
        //get inserted username and password
        username = usernameEditText.getText().toString().trim();
        username = username.toLowerCase();
        password = passwordEditText.getText().toString().trim();

        //check if required fields are not empty
        if(checkRequiredFields())
        {
            progressDialog.setMessage("Signing in. \nPlease wait ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            loginButton.setEnabled(false);
            //continue with device online
            if(Utils.isOnline(LoginActivity.this))
            {
                //build webservice url
                StringBuilder webServiceLoginURL = createWebServiceLoginURL(username, password);

                //call web service to pull user info and send to account manager
                try{
                       startWebService(webServiceLoginURL ,username, password);
                }catch (NullPointerException e){
                       startWebService(webServiceLoginURL ,username, password);
                }

            }

            //continue with device offline
            else
            {
//                fakeLogin(username, password);

                //check if user is already registered with AccountManager
                AccountManager accountManager = AccountManager.get(LoginActivity.this);
                Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
                boolean loggedIn = false;

                //go through all accounts found in AM
                for(Account account : accounts)
                {
                    //if there is a match set login as true and go to Home Activity
                    if(account.name.equalsIgnoreCase(username) && accountManager.getPassword(account).equals(password))
                    {
                        //Activity mobile.giis.app.LoginActivity has leaked window error was showing
                        //this piece of code handles it, nonetheless in prod time the error  would not show
                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        editor.putBoolean("secondSyncNeeded", true);
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, HomeActivityRevised.class);
                        BackboneApplication app = (BackboneApplication) getApplication();

//                        String hfid="";
//                        String userid = "";
//                        login_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        if (login_preferences.contains(username))
//                        {
//                            hfid = login_preferences.getString(username, hfid);
//                            app.setLoggedInUserHealthFacilityId(hfid);
//                            Log.d("Retrieved hfid", hfid);
//                        }
//
//                        if (login_preferences.contains(username + "1"))
//                        {
//                            userid = login_preferences.getString(username + "1", userid);
//                            app.setLoggedInUserHealthFacilityId(userid);
//                            Log.d("Retrieved userid", userid);
//                        }


                        app.setUsername(username);
                        app.initializeOffline(username, password);
                        startActivity(intent);
                        loggedIn = true;
                    }
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

        RequestHandle message = client.get(loginURL.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // This callback is now running within the pool thread execution
                // scope and not within Android's UI thread, so if we must update
                // the UI, we'll have to dispatch a runnable to the UI thread.
                Log.d(TAG, "Error = ");
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                final Snackbar snackbar=Snackbar.make(rootView,"Login Failed. Please try again",Snackbar.LENGTH_LONG);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG,"receiving data in streams");

                BackboneApplication app = (BackboneApplication)getApplication();

                try {
                    JsonFactory factory = new JsonFactory();
                    JsonParser jsonParser = factory.createJsonParser(responseString);
                    com.fasterxml.jackson.core.JsonToken token = jsonParser.nextToken();

                    int balanceCounter = 0;

                    if (token == JsonToken.START_OBJECT) {
                        balanceCounter++;
                        boolean idNextToHfId = false;
                        while (!(balanceCounter == 0)) {
                            token = jsonParser.nextToken();

                            if (token == JsonToken.START_OBJECT) {
                                balanceCounter++;
                            } else if (token == JsonToken.END_OBJECT) {
                                balanceCounter--;
                            } else if (token == JsonToken.FIELD_NAME) {
                                String object = jsonParser.getCurrentName();
                                switch (object) {
                                    case "HealthFacilityId":
                                        token = jsonParser.nextToken();
                                        app.setLoggedInUserHealthFacilityId(jsonParser.getText());
                                        Log.d("", "healthFacilityId is: " + jsonParser.getText());
                                        idNextToHfId = true;
                                        break;
                                    case "Firstname":
                                        token = jsonParser.nextToken();
                                        app.setLoggedInFirstname(jsonParser.getText());
                                        Log.d("", "firstname is: " + jsonParser.getText());
                                        break;
                                    case "Lastname":
                                        token = jsonParser.nextToken();
                                        app.setLoggedInLastname(jsonParser.getText());
                                        Log.d("", "lastname is: " + jsonParser.getText());
                                        break;
                                    case "Username":
                                        token = jsonParser.nextToken();
                                        app.setLoggedInUsername(jsonParser.getText());
                                        Log.d("", "username is: " + jsonParser.getText());
                                        break;
                                    case "Lastlogin":
                                        token = jsonParser.nextToken();
                                        Log.d("", "lastlogin is: " + jsonParser.getText());
                                        break;
                                    case "Id":
                                        if (idNextToHfId) {
                                            token = jsonParser.nextToken();
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
//                        login_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        SharedPreferences.Editor login_editor = login_preferences.edit();
//                        login_editor.putString(app.getLOGGED_IN_USERNAME(), app.getLOGGED_IN_USER_HF_ID());
//                        login_editor.putString(app.getLOGGED_IN_USERNAME() + "1", app.getLOGGED_IN_USER_ID());
//                        login_editor.commit();

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
                        DatabaseHandler db = app.getDatabaseInstance();
                        db.addUser(values);

                        app.initializeOffline(username, password);

                        Intent intent;
                        if (prefs.getBoolean("synchronization_needed", true)) {
                            intent = new Intent(LoginActivity.this, HomeActivityRevised.class);
                        } else {
                            intent = new Intent(LoginActivity.this, HomeActivityRevised.class);
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
