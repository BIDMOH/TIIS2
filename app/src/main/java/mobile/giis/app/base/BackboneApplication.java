/*******************************************************************************
 * <--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

package mobile.giis.app.base;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckBox;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.AdjustmentReasons;
import mobile.giis.app.entity.AdministerVaccinesModel;
import mobile.giis.app.entity.AgeDefinitions;
import mobile.giis.app.entity.Child;
import mobile.giis.app.entity.ChildCollector;
import mobile.giis.app.entity.ChildCollector2;
import mobile.giis.app.entity.Dose;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.entity.Item;
import mobile.giis.app.entity.ItemLot;
import mobile.giis.app.entity.NonVaccinationReason;
import mobile.giis.app.entity.Place;
import mobile.giis.app.entity.ScheduledVaccination;
import mobile.giis.app.entity.Status;
import mobile.giis.app.entity.Stock;
import mobile.giis.app.entity.User;
import mobile.giis.app.entity.VaccinationAppointment;
import mobile.giis.app.entity.VaccinationEvent;
import mobile.giis.app.entity.Weight;
import mobile.giis.app.helpers.Utils;
import mobile.giis.app.postman.PostmanModel;
import mobile.giis.app.util.Constants;

import cz.msebera.android.httpclient.Header;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.Volley;

/**
 * Created by Teodor on 2/3/2015.
 */
public class BackboneApplication extends Application {
    private static final String TAG = BackboneApplication.class.getSimpleName();

    /**
     * Testing WCF
     */
    public static final String WCF_URL = "https://ec2-54-187-21-117.us-west-2.compute.amazonaws.com/SVC/";
    /**
     * Live WCF
     */
    //public static final String WCF_URL = "https://ec2-52-11-215-89.us-west-2.compute.amazonaws.com/SVC/";
    public static final String USER_MANAGEMENT_SVC = "UserManagement.svc/";
    public static final String PLACE_MANAGEMENT_SVC = "PlaceManagement.svc/";
    public static final String HEALTH_FACILITY_SVC = "HealthFacilityManagement.svc/";
    public static final String ITEM_MANAGEMENT_SVC = "ItemManagement.svc/";
    public static final String DOSE_MANAGEMENT_SVC = "DoseManagement.svc/";
    public static final String STATUS_MANAGEMENT_SVC = "StatusManagement.svc/";
    public static final String CHILD_MANAGEMENT_SVC = "ChildManagement.svc/";
    public static final String CHILD_SUPPLEMENTS_SVC = "SupplementsManagement.svc/";
    public static final String AUDIT_MANAGEMENT_SVC = "AuditManagement.svc/";
    public static final String SCHEDULED_VACCINATION_MANAGEMENT_SVC = "ScheduledVaccinationManagement.svc/";
    public static final String AGE_DEFINITION_MANAGEMENT_SVC = "AgeDefinitionManagement.svc/";
    public static final String NON_VACCINATION_REASON_MANAGEMENT_SVC = "NonVaccinationReasonManagement.svc/";
    public static final String VACCINATION_EVENT_SVC = "VaccinationEvent.svc/";
    public static final String VACCINATION_QUEUE_MANAGEMENT_SVC = "VaccinationQueueManagement.svc/";
    public static final String STOCK_MANAGEMENT_SVC = "StockManagement.svc/";
    public static final String VACCINATION_APPOINTMENT_MANAGMENT_SVC = "VaccinationAppointmentManagement.svc/";
    public static final String USER_MANAGEMENT_SVC_GETTER = "GetUser";
    public static final String PLACE_MANAGEMENT_SVC_GETTER = "GetPlaceByHealthFacilityId?hf_id=";
    public static final String GET_PLACES_BY_LIST = "GetPlacesByList?pList=";
    public static final String PLACE_MANAGEMENT_SVC_GETTER_BY_ID = "GetPlaceById?id=";
    public static final String STOCK_MANAGEMENT_SVC_GETTER = "GetCurrentStockByLot?hfId=";
    public static final String HEALTH_FACILITY_SVC_GETTER = "GetHealthFacilityById?id=";
    public static final String HEALTH_FACILITY_SVC_GETTER_BY_LIST = "GetHealthFacilityByList?hList=";
    public static final String ITEM_MANAGEMENT_SVC_GETTER = "getitemlist";
    public static final String ITEM_LOT_MANAGEMENT_SVC_GETTER = "getitemlots";
    public static final String STATUS_MANAGEMENT_SVC_GETTER = "getstatuslist";
    public static final String DOSE_MANAGEMENT_SVC_GETTER = "getdoselist";
    public static final String CHILD_MANAGEMENT_SVC_GETTER = "GetChildrenByHealthFacility?healthFacilityId=";
    public static final String CHILD_UPDATE = "UpdateChild?";
    public static final String REGISTER_CHILD_AEFI = "RegisterChildAEFI?";
    public static final String REGISTER_CHILD_AEFI_BARCODE = "RegisterChildAEFIBarcode?";
    public static final String CHILD_SUPPLEMENTS_INSERT = "RegisterSupplementsBarcode";
    public static final String WEIGHT_MANAGEMENT_SVC_GETTER = "getweight";
    public static final String AGE_DEFINITION_MANAGEMENT_SVC_GETTER = "getagedefinitionslist";
    public static final String SCHEDULED_VACCINATION_MANAGEMENT_SVC_GETTER = "getscheduledvaccinationlist";
    public static final String NON_VACCINATION_REASON_MANAGEMENT_SVC_GETTER = "getnonvaccinationreasonlist";
    public static final String URL_BUILDER_ERROR = "URL_BUILDER_ERROR";
    public static final String GET_PLACE = "GET_PLACE";
    public static final String GET_PLACE_LIST_ID = "GET_PLACE_LIST_ID";
    public static final String GET_PLACE_BY_ID = "GET_PLACE_BY_ID";
    public static final String GET_STOCK = "GET_STOCK";
    public static final String GET_STOCK_ADJUSTMENT = "GetAdjustmentReasons";
    public static final String GET_ITEM_LOT_ID = "GET_ITEM_LOT_ID";
    public static final String GET_HEALTH_FACILITY = "GET_HEALTH_FACILITY";
    public static final String GET_HEALTH_FACILITY_LIST_ID = "GET_HEALTH_FACILITY_LIST_ID";
    public static final String GET_ITEM_LIST = "GET_ITEM_LIST";
    public static final String GET_DOSE_LIST = "GET_DOSE_LIST";
    public static final String GET_WEIGHT_LIST = "GET_CHILD_WEIGHT_LIST";
    public static final String GET_CHILD = "GET_CHILD";
    public static final String GET_STATUS_LIST = "GET_STATUS_LIST";
    public static final String GET_AGE_DEFINITIONS_LIST = "GET_AGE_DEFINITIONS_LIST";
    public static final String GET_SCHEDULED_VACCINATION_LIST = "GET_SCHEDULED_VACCINATION_LIST";
    public static final String GET_NON_VACCINATION_REASON_LIST = "GET_NON_VACCINATION_REASON_LIST";
    //checkin
    public static final String SEARCH_BY_BARCODE = "SearchByBarcode";
    public static final String UPDATE_VACCINATION_QUEUE = "UpdateVaccinationQueue";
    public static final String REGISTER_AUDIT = "RegisterAudit";
    public static final String GET_VACCINATION_QUEUE_BY_DATE_AND_USER = "GetVaccinationQueueByDateAndUser";
    public static final String PLACEMANAGEMENT_GETBIRTHPLACELIST = "PlaceManagement.svc/GetBirthplaceList";
    //ChildID
    public static final String CHILD_ID = "childID";
    //register audit Constants
    public static final String CHILD_AUDIT = "CHILD";
    public static final int ACTION_CHECKIN = 5;
    private static final String CHECK_PREINSTALLED_DB_KEY = "check_preinstalled_db_key";

    //Fields Edited Watcher
    public static boolean saveNeeded = false;
    public static String PROMPT_MESSAGE = "Are you sure you want to Leave?";

    //Thread handler
    protected Handler handler;
    private String USERNAME = "default";
    private boolean ONLINE_STATUS = false;
    private String CURRENT_ACTIVITY = "default";
    public String APPOINTMENT_LIST_FRAGMENT = "appointmentListFragment";
    public String VACCINATE_CHILD_FRAGMENT = "vaccinateChildFragment";

    //On login
    private String LOGGED_IN_USER_ID;
    private String LOGGED_IN_USERNAME;
    private String LOGGED_IN_USER_PASS;
    private String LOGGED_IN_FIRSTNAME;
    private String LOGGED_IN_LASTNAME;
    private String LOGGED_IN_USER_HF_ID;
    private DatabaseHandler databaseInstance;
    private boolean main_sync_needed = true;
    private boolean MODIFICATION_SYNCRONIZATION_COMPLETED_STATUS = false;
    private int SYNCRONIZATION_STATUS = 0;
    private boolean administerVaccineHidden = false;
    public static final String AUDIT_MANAGEMENT_GET_CONFIGURATION = "AuditManagement.svc/GetConfiguration";

    public String LAST_FRAGMENT = "mobile.giis.app.fragments.HomeFragment";
    public String LAST_FRAGMENT_TITLE = "Home";
    private String CURRENT_FRAGMENT = "HOME";

    public static String getWcfUrl() {
        return WCF_URL;
    }

    public String getUsername() {
        return USERNAME;
    }

    public void setUsername(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public boolean getOnlineStatus() {
        return ONLINE_STATUS;
    }

    public void setOnlineStatus(Boolean ONLINE_STATUS) {
        this.ONLINE_STATUS = ONLINE_STATUS;
    }

    public String getCurrentActivity() {
        return CURRENT_ACTIVITY;
    }

    public void setCurrentActivity(String CURRENT_ACTIVITY) {
        this.CURRENT_ACTIVITY = CURRENT_ACTIVITY;
    }

    public String getCurrentFragment() {
        return CURRENT_FRAGMENT;
    }

    public void setCurrentFragment(String CURRENT_FRAGMENT) {
        this.CURRENT_FRAGMENT = CURRENT_FRAGMENT;
    }

    public DatabaseHandler getDatabaseInstance() {
        if (databaseInstance == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (!prefs.contains(CHECK_PREINSTALLED_DB_KEY)) {
                DatabaseHandler.dbPreinstalled = DatabaseHandler.checkIfThereIsDatabaseFile(this);
                prefs.edit().putBoolean(CHECK_PREINSTALLED_DB_KEY, DatabaseHandler.dbPreinstalled).commit();
            } else {
                DatabaseHandler.dbPreinstalled = prefs.getBoolean(CHECK_PREINSTALLED_DB_KEY, false);
            }
            databaseInstance = new DatabaseHandler(this);
        }
        return databaseInstance;
    }

    public boolean getMainSyncronizationNeededStatus() {
        return main_sync_needed;
    }

    public void setMainSyncronizationNeededStatus(boolean MAIN_SYNCRONIZATION_NEEDED_STATUS) {
        this.main_sync_needed = MAIN_SYNCRONIZATION_NEEDED_STATUS;
    }

    public int getSyncronizationStatus() {
        return SYNCRONIZATION_STATUS;
    }

    public void setSyncronizationStatus(int SYNCRONIZATION_STATUS) {
        /**
         * -1 Not started
         * 0 In progress
         */
        this.SYNCRONIZATION_STATUS = SYNCRONIZATION_STATUS;
    }

    public void initializeOffline(String username, String password) {
        if (databaseInstance != null) {
            List<User> allUsers = databaseInstance.getAllUsers();
            for (User thisUser : allUsers) {
                if (thisUser.getUsername().equals(username)) {
                    //Log.d("UserId is now offline", thisUser.getId());
                    LOGGED_IN_USER_ID = thisUser.getId();
                    LOGGED_IN_USERNAME = thisUser.getUsername();
                    //Log.d("Initializied offline username", thisUser.getUsername());
                    LOGGED_IN_FIRSTNAME = thisUser.getFirstname();
                    //Log.d("Initializied offline firstname", thisUser.getFirstname());
                    LOGGED_IN_LASTNAME = thisUser.getLastname();
                    //Log.d("Initializied offline lastname", thisUser.getLastname());
                    LOGGED_IN_USER_HF_ID = thisUser.getHealthFacilityId();
                    LOGGED_IN_USER_PASS = password;
                    return;
                }
            }
        }


    }

    public void setLoggedInUserId(String value) {
        LOGGED_IN_USER_ID = value;
    }

    public String getLOGGED_IN_USER_ID() {
        return LOGGED_IN_USER_ID;
    }

    public String getLOGGED_IN_USER_HF_ID() {
        return LOGGED_IN_USER_HF_ID;
    }

    public String getLOGGED_IN_LASTNAME() {
        return LOGGED_IN_LASTNAME;
    }

    public String getLOGGED_IN_FIRSTNAME() {
        return LOGGED_IN_FIRSTNAME;
    }

    public String getLOGGED_IN_USERNAME() {
        return LOGGED_IN_USERNAME;
    }

    public String getLOGGED_IN_USER_PASS() {
        return LOGGED_IN_USER_PASS;
    }

    public void setLOGGED_IN_USER_PASS(String LOGGED_IN_USER_PASS) {
        this.LOGGED_IN_USER_PASS = LOGGED_IN_USER_PASS;
    }

    public boolean getAdministerVaccineHidden() {
        return administerVaccineHidden;
    }

    public void setAdministerVaccineHidden(Boolean a) {
        administerVaccineHidden = a;
    }

    public void setLoggedInUsername(String value) {
        LOGGED_IN_USERNAME = value;
    }

    public void setLoggedInFirstname(String value) {
        LOGGED_IN_FIRSTNAME = value;
    }

    public void setLoggedInLastname(String value) {
        LOGGED_IN_LASTNAME = value;
    }

    public void setLoggedInUserHealthFacilityId(String value) {
        LOGGED_IN_USER_HF_ID = value;
    }

    private static String deviceId = null;

    public static String getDeviceId(Context ctx) {
        if (deviceId == null) {
            TelephonyManager tm = (TelephonyManager) ctx.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        }
        return deviceId;
    }


    public void parsePlace() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_PLACE);


        RequestParams params = new RequestParams();
        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + responseString);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

                List<Place> objects = new ArrayList<Place>();
                try {
                    objects = mapper.readValue(responseString, new TypeReference<List<Place>>() {
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    for (Place object : objects) {
                        ContentValues values = new ContentValues();
                        //Log.d("Place ID", object.getId());
                        values.put(SQLHandler.PlaceColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.PlaceColumns.NAME, object.getName());
                        //Log.d("Place NAME", object.getName());
                        values.put(SQLHandler.PlaceColumns.CODE, object.getCode());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addPlacesThatWereNotInDB(values, object.getId());
                    }
                }

            }
        });


    }

    public void parseBirthplace() {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(PLACEMANAGEMENT_GETBIRTHPLACELIST);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONArray jarr = new JSONArray(responseString);
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + jarr.toString());
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject jobj = jarr.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        cv.put(SQLHandler.PlaceColumns.ID, jobj.getInt("Id") + "");
                        cv.put(SQLHandler.PlaceColumns.NAME, jobj.getString("Name"));
                        getDatabaseInstance().addBirthplaces(cv, jobj.getInt("Id") + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void parseConfiguration() {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(AUDIT_MANAGEMENT_GET_CONFIGURATION);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONArray jarr = new JSONArray(responseString);
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + jarr.toString());
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject jobj = jarr.getJSONObject(i);
                        if (jobj.getString("key").equals(Constants.LimitNumberOfDaysBeforeExpire)) {
                            Constants.LimitNumberOfDaysBeforeExpireVal = jobj.getInt("value");
                            saveConfiguration(Constants.LimitNumberOfDaysBeforeExpire, Constants.LimitNumberOfDaysBeforeExpireVal);
                        } else if (jobj.getString("key").equals(Constants.EligibleForVaccination)) {
                            Constants.EligibleForVaccinationVal = jobj.getInt("value");
                            saveConfiguration(Constants.EligibleForVaccination, Constants.EligibleForVaccinationVal);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void saveConfiguration(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.CONFIG, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * method takes a string that contains potentially many ID of places and than sends them to server in order to get the other info for these places
     *
     * @param idsTokenized ids in the format "1,123,231..."
     */
    public void parsePlacesThatAreInChildAndNotInPlaces(String idsTokenized) {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_PLACE_LIST_ID);
        webServiceUrl.append(URLEncoder.encode(idsTokenized));
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<Place> objects = new ArrayList<Place>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<Place>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Place object : objects) {
                        ContentValues values = new ContentValues();
                        //Log.d("Place ID", object.getId());
                        values.put(SQLHandler.PlaceColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.PlaceColumns.NAME, object.getName());
                        //Log.d("Place NAME", object.getName());
                        values.put(SQLHandler.PlaceColumns.CODE, object.getCode());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addPlacesThatWereNotInDB(values, object.getId());
                    }
                }
            }
        });
    }

    public void parseHealthFacilityThatAreInVaccEventButNotInHealthFac(String idsTokenized) {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_HEALTH_FACILITY_LIST_ID);
        webServiceUrl.append(URLEncoder.encode(idsTokenized));
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<HealthFacility> objects = new ArrayList<HealthFacility>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<HealthFacility>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (HealthFacility object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.HealthFacilityColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.HealthFacilityColumns.CODE, object.getCode());
                        values.put(SQLHandler.HealthFacilityColumns.PARENT_ID, object.getParentId());
                        values.put(SQLHandler.HealthFacilityColumns.NAME, object.getName());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addHFIDThatWereNotInDB(values, object.getId());
                    }
                }
            }
        });
    }

    public void parseStock() {
        if (LOGGED_IN_USER_HF_ID == null || LOGGED_IN_USER_HF_ID.equals("0")) return;
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_STOCK);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<Stock> objects = new ArrayList<Stock>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<Stock>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Stock object : objects) {
                        ContentValues values = new ContentValues();
//                Log.d("balance", object.getBalance()+"");
//                Log.d("expdate", object.getExpireDate());
//                Log.d("gtin", object.getGtin());
//                Log.d("item", object.getItem());
//                Log.d("lotid", object.getLotId());
//                Log.d("lotnumber", object.getLotNumber());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.BALANCE, object.getBalance());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.EXPIRE_DATE, object.getExpireDate());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.GTIN, object.getGtin());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.LOT_ID, object.getLotId());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.LOT_NUMBER, object.getLotNumber());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.ITEM, object.getItem());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.REORDER_QTY, object.getReorderQty());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.GTIN_ISACTIVE, object.getGtinIsActive());
                        values.put(SQLHandler.HealthFacilityBalanceColumns.LOT_ISACTIVE, object.getLotIsActive());
//                Log.d("LotIsActive", object.getLotIsActive());
//                Log.d("GtinIsActive", object.getGtinIsActive());
                        DatabaseHandler db = getDatabaseInstance();
                        if (!db.isStockInDB(object.getLotId(), object.getGtin())) {
                            db.addStock(values);
                        } else {
                            db.updateStock(values, object.getLotId());
                        }
                    }
                }
            }
        });
    }

    public int parseChildCollector2() {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append("ChildManagement.svc/GetOnlyChildrenDataByHealthFacility?healthfacilityId=").append(LOGGED_IN_USER_HF_ID);

        ChildCollector2 childCollector2;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return 3;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            childCollector2 = mapper.readValue(response, ChildCollector2.class);

            addChildVaccinationEventVaccinationAppointment(childCollector2);
            return 1;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return 2;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
        } finally {
            childCollector2 = null; // clearing references so that it can be identified as GC material more easilly
        }
    }

    public void addChildVaccinationEventVaccinationAppointment(ChildCollector2 childCollector) {
        List<Child> children = childCollector.getChildList();
        List<VaccinationEvent> vaccinationEvents = childCollector.getVeList();
        List<VaccinationAppointment> vaccinationAppointments = childCollector.getVaList();
        DatabaseHandler db = getDatabaseInstance();

        if (children != null) {
            for (Child child : children) {
                ContentValues childCV = new ContentValues();
                childCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                childCV.put(SQLHandler.ChildColumns.ID, child.getId());
                childCV.put(SQLHandler.ChildColumns.BARCODE_ID, child.getBarcodeID());
                childCV.put(SQLHandler.ChildColumns.FIRSTNAME1, child.getFirstname1());
                childCV.put(SQLHandler.ChildColumns.FIRSTNAME2, child.getFirstname2());
                childCV.put(SQLHandler.ChildColumns.LASTNAME1, child.getLastname1());
                childCV.put(SQLHandler.ChildColumns.BIRTHDATE, child.getBirthdate());
                childCV.put(SQLHandler.ChildColumns.GENDER, child.getGender());
                childCV.put(SQLHandler.ChildColumns.TEMP_ID, child.getTempId());
                childCV.put(SQLHandler.ChildColumns.HEALTH_FACILITY, child.getHealthcenter());
                childCV.put(SQLHandler.ChildColumns.DOMICILE, child.getDomicile());
                childCV.put(SQLHandler.ChildColumns.DOMICILE_ID, child.getDomicileId());
                childCV.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, child.getHealthcenterId());
                childCV.put(SQLHandler.ChildColumns.STATUS_ID, child.getStatusId());
                childCV.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, child.getBirthplaceId());
                childCV.put(SQLHandler.ChildColumns.NOTES, child.getNotes());
                childCV.put(SQLHandler.ChildColumns.STATUS, child.getDomicile());
                childCV.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, child.getMotherFirstname());
                childCV.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, child.getMotherLastname());
                childCV.put(SQLHandler.ChildColumns.PHONE, child.getPhone());

                if (!db.isChildIDInChildTable(child.getId())) {
                    db.addChild(childCV);
                } else {
                    db.updateChild(childCV, child.getId());
                }
            }
        }

        if (vaccinationEvents != null) {
            for (VaccinationEvent vaccinationEvent : vaccinationEvents) {
                ContentValues vaccEventCV = new ContentValues();
                vaccEventCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.APPOINTMENT_ID, vaccinationEvent.getAppointmentId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.CHILD_ID, vaccinationEvent.getChildId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.DOSE_ID, vaccinationEvent.getDoseId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID, vaccinationEvent.getHealthFacilityId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.ID, vaccinationEvent.getId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.IS_ACTIVE, vaccinationEvent.getIsActive());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.MODIFIED_BY, vaccinationEvent.getModifiedBy());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.MODIFIED_ON, vaccinationEvent.getModifiedOn());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.NONVACCINATION_REASON_ID, vaccinationEvent.getNonvaccinationReasonId());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.SCHEDULED_DATE, vaccinationEvent.getScheduledDate());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.VACCINATION_DATE, vaccinationEvent.getVaccinationDate());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.VACCINATION_STATUS, vaccinationEvent.getVaccinationStatus());
                vaccEventCV.put(SQLHandler.VaccinationEventColumns.VACCINE_LOT_ID, vaccinationEvent.getVaccineLotId());

                if (!db.isVaccinationEventInDb(vaccinationEvent.getChildId(), vaccinationEvent.getDoseId())) {
                    db.addVaccinationEvent(vaccEventCV);
                } else {
                    db.updateVaccinationEvent(vaccEventCV, vaccinationEvent.getId());
                }
            }
        }

        if (vaccinationAppointments != null) {
            for (VaccinationAppointment vaccinationAppointment : vaccinationAppointments) {
                ContentValues vaccAppointmentCV = new ContentValues();
                vaccAppointmentCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.CHILD_ID, vaccinationAppointment.getChildId());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.ID, vaccinationAppointment.getId());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.IS_ACTIVE, vaccinationAppointment.getIsActive());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.MODIFIED_BY, vaccinationAppointment.getModifiedBy());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.MODIFIED_ON, vaccinationAppointment.getModifiedOn());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.NOTES, vaccinationAppointment.getNotes());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.SCHEDULED_DATE, vaccinationAppointment.getScheduledDate());
                vaccAppointmentCV.put(SQLHandler.VaccinationAppointmentColumns.SCHEDULED_FACILITY_ID, vaccinationAppointment.getScheduledFacilityId());

                if (!db.isVaccinationAppointmentInDb(vaccinationAppointment.getChildId(), vaccinationAppointment.getScheduledDate())) {
                    db.addVaccinationAppointment(vaccAppointmentCV);
                } else {
                    db.updateVaccinationAppointment(vaccAppointmentCV, vaccinationAppointment.getId());
                }

            }
        }

        childCollector = null; // clearing references so that it can be identified as GC material more easilly
    }

    public void parseChildCollector() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_CHILD);
        Log.e("parseChildCollector", webServiceUrl.toString());
        List<ChildCollector> objects = new ArrayList<ChildCollector>();

        UsePoolThreadResponseHandler poolThreadResponseHandler= new UsePoolThreadResponseHandler();
        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), poolThreadResponseHandler);
    }

    /**
     * @param firstname
     * @param motherFistname
     * @param DobFrom
     * @param DobTo
     * @param tempId
     * @param surname
     * @param motherSurname
     * @param placeID
     * @param healthID
     * @param villageId
     * @param statusId
     * @return
     * @Arinela this method returns an empty list if we dont get any child from service
     * , arraylist with size more than 0 if we get results
     * or null if communication was not successful
     */
    public ArrayList<Child> searchChild(String Barcode, String firstname, String firstname2, String motherFistname, String DobFrom, String DobTo, String tempId, String surname, String motherSurname,
                                        String placeID, String healthID, String villageId, String statusId) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC);
        webServiceUrl.append("Search?where=");
        boolean isFirstParam = true;
        if (Barcode != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("barcode=" + URLEncoder.encode(Barcode));
        }
        if (firstname != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("firstname1=" + URLEncoder.encode(firstname));
        }
        if (firstname2 != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("firstname2=" + URLEncoder.encode(firstname2));
        }
        if (motherFistname != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("motherfirstname=" + URLEncoder.encode(motherFistname));
        }
        if (DobFrom != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            if (!DobFrom.equals("")) webServiceUrl.append("birthdatefrom=" + DobFrom);
        }
        if (DobTo != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            if (!DobTo.equals("")) webServiceUrl.append("birthdateto=" + DobTo);
        }
        if (tempId != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("tempId=" + URLEncoder.encode(tempId));
        }
        if (surname != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("lastname1=" + URLEncoder.encode(surname));
        }
        if (motherSurname != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("motherlastname=" + URLEncoder.encode(motherSurname));
        }
        if (villageId != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("domicileid=" + villageId);
        }
        if (placeID != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("birthplaceId=" + placeID);
        }
        if (healthID != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("healthfacilityid=" + healthID);
        }
        if (statusId != null) {
            if (!isFirstParam) webServiceUrl.append("!");
            else isFirstParam = false;
            webServiceUrl.append("statusid=" + statusId);
        }
        webServiceUrl.append("!");

        Log.d("searchChild", webServiceUrl.toString());
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return null;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            ArrayList<Child> children = new ArrayList<>();
            JSONArray jChildren = new JSONArray(result);
            for (int i = 0; i < jChildren.length(); i++) {
                try {

                    long tStart = System.currentTimeMillis();
                    Log.e("TIMING LOG", "Parsing start search children");

                    Child c = new Child();
                    JSONObject jc = jChildren.getJSONObject(i);
                    c.setFirstname1(jc.getString("Firstname1"));
                    c.setFirstname2(jc.getString("Firstname2"));
                    c.setBarcodeID(jc.getString("BarcodeId"));
                    c.setLastname1(jc.getString("Lastname1"));
                    c.setMotherFirstname(jc.getString("MotherFirstname"));
                    c.setMotherLastname(jc.getString("MotherLastname"));
                    c.setBirthdate(jc.getString("Birthdate").substring(6, 19));
                    c.setDomicile(jc.getString("DomicileId"));
                    c.setGender(jc.getBoolean("Gender") == true ? "Male" : "Female");
                    c.setHealthcenter(jc.getString("HealthcenterId"));
                    c.setId(jc.getInt("Id") + "");
                    children.add(c);

                    Log.e("TIMING LOG", "elapsed time parsing search children (milliseconds): " + (System.currentTimeMillis() - tStart));
                } catch (Exception e) {
                }
            }
            Log.e("", "");
            return children;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * @param barcode
     * @param dateToday
     * @param dateTodayTimestamp
     * @param weight
     * @param modBy
     * @return
     * @Arinela
     */
    public boolean saveWeight(String barcode, String dateToday, String dateTodayTimestamp, String weight, String modBy) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC);
        webServiceUrl.append("RegisterChildWeightBarcode?barcode=").append(URLEncoder.encode(barcode)).append("&date=").append(dateToday).append("&weight=").append(weight)
                .append("&modifiedon=").append(dateTodayTimestamp).append("&modifiedby=").append(modBy);

        Log.e("service weight", webServiceUrl + "");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            return true;

        } catch (Exception e) {
            getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            return false;
        }

    }

    /**
     * this method takes barcode as input and returns 1 and inserts child if child is registered(we get data from server),
     * 2 if child is not regstered(we dont get data from server) and 3 if statusCode not 200
     *
     * @param barcode
     */
    public int parseChildCollectorSearchByBarcode(String barcode) {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, SEARCH_BY_BARCODE);
        webServiceUrl.append("?barcodeId=").append(URLEncoder.encode(barcode));
        ChildCollector childCollector = new ChildCollector();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return 3;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            childCollector = mapper.readValue(new JSONArray(response).getJSONObject(0).toString(), ChildCollector.class);

            addChildVaccinationEventVaccinationAppointment(childCollector);
            return 1;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return 2;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
        } catch (JSONException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Method that sends the updated child data to the server.
     * The server responds 1 for succes and 0 for error.
     *
     * @param url
     * @return
     */
    public boolean updateChild(StringBuilder url) {
        url.append("&userId=" + LOGGED_IN_USER_ID);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                String result = Utils.getStringFromInputStream(inputStream);
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                if (jsonObject != null && jsonObject.optString("id", "-1").equalsIgnoreCase("1")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Method that sends the updated of an aefi appointement for teh child data to the server.
     * The server responds 1 for succes and 0 for error.
     *
     * @param url
     * @return
     */
    public boolean updateAefiAppointement(StringBuilder url) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                String result = Utils.getStringFromInputStream(inputStream);
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                if (jsonObject != null && jsonObject.optString("id", "-1").equalsIgnoreCase("1")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method that insert a new Child Supplement id in the server
     *
     * @param url
     * @return - the newly inserted id in the server, or a negative value in case of error
     */
    public long insertChildSupplementidChild(String url) {
        if (url != null && !url.isEmpty()) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url.toString());
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
                httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
                HttpResponse httpResponse = httpClient.execute(httpGet);

                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    String result = Utils.getStringFromInputStream(inputStream);
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return -1;
                    }
                    if (jsonObject != null) {
                        long newId = Long.parseLong(jsonObject.optString("id", "-1"));
                        return newId;
                    } else {
                        return -1;
                    }
                } else {
                    Utils.writeNetworkLogFileOnSD(
                            Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                    + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                    + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                    + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * this method  updates vaccination Queue table in Server DB and returns true if statusCode == 200
     * or false if not
     */
    public boolean updateVaccinationQueue(String barcode, String childHfid, String dateNow, String userId) {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, UPDATE_VACCINATION_QUEUE);
        webServiceUrl.append("?barcode=").append(URLEncoder.encode(barcode)).append("&hfid=").append(childHfid)
                .append("&date=").append(dateNow).append("&userId=").append(userId);
        Log.e("URL upadte VAc queue", webServiceUrl.toString());
        ChildCollector childCollector = new ChildCollector();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);


            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            return false;
        }
    }

    private int requestCode;
    /**
     * this method takes the date of today and the logged in user id and returns 1 and .....(we get data from server),
     * 2 if there is no entry for the queue(we dont get data from server) and 3 if statusCode not 200
     *
     * @return int that shows result interpretation
     */
    public int getVaccinationQueueByDateAndUser() {
        final StringBuilder webServiceUrl = createWebServiceURL("", GET_VACCINATION_QUEUE_BY_DATE_AND_USER);
        webServiceUrl.append("?date=").append(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())).append("&userId=").append(getLOGGED_IN_USER_ID());
        Log.e("getVaccQueueByDt&Usr", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestCode=3;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseAsString) {
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + responseAsString);
                    JSONArray jarr = new JSONArray(responseAsString);
                    DatabaseHandler db = getDatabaseInstance();
                    String childBarcodesNotInDB = "";
                    for (int i = 0; i < jarr.length(); i++) {
                        JSONObject jobj = jarr.getJSONObject(i);
                        if (db.isBarcodeInChildTable(jobj.getString("BarcodeId"))) {
                            String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(new Date(Long.parseLong(jobj.getString("Date").substring(6, 19))));

                            ContentValues cv = new ContentValues();
                            cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, db.getChildIdByBarcode(jobj.getString("BarcodeId")));
                            cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateNow);
                            db.addChildToVaccinationQueue(cv);
                        } else {
                            if (childBarcodesNotInDB.length() > 0) childBarcodesNotInDB += ",";
                            childBarcodesNotInDB += jobj.getString("BarcodeId");
                        }
                    }

                    if (childBarcodesNotInDB.length() > 0) {
                        getChildByBarcodeList(childBarcodesNotInDB);
                    }
                    requestCode = 1;
                }catch (Exception e) {
                    e.printStackTrace();
                    requestCode = 2;
                }
            }
        });
        return requestCode;

    }

    /**
     * this method  regiters Audit in Server DB and returns true if statusCode == 200
     * or false if not
     */
    public boolean registerAudit(String table, String barcode, String dateNow, String userId, int actionId) {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, REGISTER_AUDIT);
        webServiceUrl.append("?table=").append(table).append("&recordId=").append(barcode)
                .append("&userId=").append(userId).append("&date=").append(dateNow).append("&activityId=").append(actionId);
        Log.d("", webServiceUrl.toString());

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            return false;
        }
    }

    private static AsyncHttpClient client = new SyncHttpClient();
    final int DEFAULT_TIMEOUT = 6000000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "application created");
        client.setTimeout(DEFAULT_TIMEOUT);


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


        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(IllegalArgumentException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);
        AsyncHttpClient.blockRetryExceptionClass(UnknownHostException.class);
        AsyncHttpClient.blockRetryExceptionClass(ConnectionPoolTimeoutException.class);
        client.setMaxConnections(20);


    }

    /**
     * method to be used internaly since there were two other methods that needed this code to execute
     *
     * @param childCollector
     */
    public void addChildVaccinationEventVaccinationAppointment(ChildCollector childCollector) {
        Child child = childCollector.getChildEntity();
        List<VaccinationEvent> vaccinationEvents = childCollector.getVeList();
        List<VaccinationAppointment> vaccinationAppointments = childCollector.getVaList();
        ContentValues childCV = new ContentValues();
        DatabaseHandler db = getDatabaseInstance();

        SQLiteDatabase db1 = db.getWritableDatabase();
        db1.beginTransactionNonExclusive();
        try {
            String sql0 = "INSERT OR REPLACE INTO " + SQLHandler.Tables.CHILD + " ( "+
                    SQLHandler.SyncColumns.UPDATED+", "+
                    SQLHandler.ChildColumns.ID+","+
                    SQLHandler.ChildColumns.BARCODE_ID+","+
                    SQLHandler.ChildColumns.FIRSTNAME1+","+
                    SQLHandler.ChildColumns.FIRSTNAME2+","+
                    SQLHandler.ChildColumns.LASTNAME1+","+
                    SQLHandler.ChildColumns.BIRTHDATE+","+
                    SQLHandler.ChildColumns.GENDER+","+
                    SQLHandler.ChildColumns.TEMP_ID+","+
                    SQLHandler.ChildColumns.HEALTH_FACILITY+","+
                    SQLHandler.ChildColumns.DOMICILE+","+
                    SQLHandler.ChildColumns.DOMICILE_ID+","+
                    SQLHandler.ChildColumns.HEALTH_FACILITY_ID+","+
                    SQLHandler.ChildColumns.STATUS_ID+","+
                    SQLHandler.ChildColumns.BIRTHPLACE_ID+","+
                    SQLHandler.ChildColumns.NOTES+","+
                    SQLHandler.ChildColumns.STATUS+","+
                    SQLHandler.ChildColumns.MOTHER_FIRSTNAME+","+
                    SQLHandler.ChildColumns.MOTHER_LASTNAME+","+
                    SQLHandler.ChildColumns.PHONE+
                    " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement stmt0 = db1.compileStatement(sql0);
            stmt0.bindString(1, "1");
            stmt0.bindString(2, child.getId()==null?"":child.getId());
            stmt0.bindString(3, child.getBarcodeID()==null?"":child.getBarcodeID());
            stmt0.bindString(4, child.getFirstname1()==null?"":child.getFirstname1());
            stmt0.bindString(5, child.getFirstname2()==null?"":child.getFirstname2());
            stmt0.bindString(6, child.getLastname1()==null?"":child.getLastname1());
            stmt0.bindString(7, child.getBirthdate()==null?"":child.getBirthdate());
            stmt0.bindString(8, child.getGender()==null?"":child.getGender());
            stmt0.bindString(9, child.getTempId()==null?"":child.getTempId());
            stmt0.bindString(10, child.getHealthcenter()==null?"":child.getHealthcenter());
            stmt0.bindString(11, child.getDomicile()==null?"":child.getDomicile());
            stmt0.bindString(12, child.getDomicileId()==null?"":child.getDomicileId());
            stmt0.bindString(13, child.getHealthcenterId()==null?"":child.getHealthcenterId());
            stmt0.bindString(14, child.getStatusId()==null?"":child.getStatusId());
            stmt0.bindString(15, child.getBirthplaceId()==null?"":child.getBirthplaceId());
            stmt0.bindString(16, child.getNotes()==null?"":child.getNotes());
            stmt0.bindString(17, child.getDomicile()==null?"":child.getDomicile());
            stmt0.bindString(18, child.getMotherFirstname()==null?"":child.getMotherFirstname());
            stmt0.bindString(19, child.getMotherLastname()==null?"":child.getMotherLastname());
            stmt0.bindString(20, child.getPhone()==null?"":child.getPhone());
            stmt0.execute();
            stmt0.clearBindings();

            String sql = "INSERT OR REPLACE INTO " + SQLHandler.Tables.VACCINATION_EVENT + " ( "+
                    SQLHandler.SyncColumns.UPDATED+", "+
                    SQLHandler.VaccinationEventColumns.APPOINTMENT_ID+","+
                    SQLHandler.VaccinationEventColumns.CHILD_ID+","+
                    SQLHandler.VaccinationEventColumns.DOSE_ID+","+
                    SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+","+
                    SQLHandler.VaccinationEventColumns.ID+","+
                    SQLHandler.VaccinationEventColumns.IS_ACTIVE+","+
                    SQLHandler.VaccinationEventColumns.MODIFIED_BY+","+
                    SQLHandler.VaccinationEventColumns.MODIFIED_ON+","+
                    SQLHandler.VaccinationEventColumns.NONVACCINATION_REASON_ID+","+
                    SQLHandler.VaccinationEventColumns.SCHEDULED_DATE+","+
                    SQLHandler.VaccinationEventColumns.VACCINATION_DATE+","+
                    SQLHandler.VaccinationEventColumns.VACCINATION_STATUS+","+
                    SQLHandler.VaccinationEventColumns.VACCINE_LOT_ID+
                    " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement stmt = db1.compileStatement(sql);

            for (VaccinationEvent vaccinationEvent : vaccinationEvents) {
                stmt.bindString(1, "1");
                stmt.bindString(2, vaccinationEvent.getAppointmentId());
                stmt.bindString(3, vaccinationEvent.getChildId());
                stmt.bindString(4, vaccinationEvent.getDoseId());
                stmt.bindString(5, vaccinationEvent.getHealthFacilityId());
                stmt.bindString(6, vaccinationEvent.getId());
                stmt.bindString(7, vaccinationEvent.getIsActive());
                stmt.bindString(8, vaccinationEvent.getModifiedBy());
                stmt.bindString(9, vaccinationEvent.getModifiedOn());
                stmt.bindString(10, vaccinationEvent.getNonvaccinationReasonId());
                stmt.bindString(11, vaccinationEvent.getScheduledDate());
                stmt.bindString(12, vaccinationEvent.getVaccinationDate());
                stmt.bindString(13, vaccinationEvent.getVaccinationStatus());
                stmt.bindString(14, vaccinationEvent.getVaccineLotId());
                stmt.execute();
                stmt.clearBindings();
            }

            String sql1 = "INSERT OR REPLACE INTO " + SQLHandler.Tables.VACCINATION_APPOINTMENT + " ( "+
                    SQLHandler.SyncColumns.UPDATED+", "+
                    SQLHandler.VaccinationAppointmentColumns.CHILD_ID+","+
                    SQLHandler.VaccinationAppointmentColumns.ID+","+
                    SQLHandler.VaccinationAppointmentColumns.IS_ACTIVE+","+
                    SQLHandler.VaccinationAppointmentColumns.MODIFIED_BY+","+
                    SQLHandler.VaccinationAppointmentColumns.MODIFIED_ON+","+
                    SQLHandler.VaccinationAppointmentColumns.NOTES+","+
                    SQLHandler.VaccinationAppointmentColumns.SCHEDULED_DATE+","+
                    SQLHandler.VaccinationAppointmentColumns.SCHEDULED_FACILITY_ID+
                    " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement stmt1 = db1.compileStatement(sql1);

            for (VaccinationAppointment vaccinationAppointment : vaccinationAppointments) {
                stmt1.bindString(1, "1");
                stmt1.bindString(2, vaccinationAppointment.getChildId());
                stmt1.bindString(3, vaccinationAppointment.getId());
                stmt1.bindString(4, vaccinationAppointment.getIsActive());
                stmt1.bindString(5, vaccinationAppointment.getModifiedBy());
                stmt1.bindString(6, vaccinationAppointment.getModifiedOn());
                stmt1.bindString(7, vaccinationAppointment.getNotes());
                stmt1.bindString(8, vaccinationAppointment.getScheduledDate());
                stmt1.bindString(9, vaccinationAppointment.getScheduledFacilityId());

                stmt1.execute();
                stmt1.clearBindings();

            }

            db1.setTransactionSuccessful();
            db1.endTransaction();
        } catch (Exception e) {
            db1.endTransaction();
            e.printStackTrace();
        }
    }


    public void parseHealthFacility() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_HEALTH_FACILITY);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<HealthFacility> objects = new ArrayList<HealthFacility>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<HealthFacility>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (HealthFacility object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.HealthFacilityColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.HealthFacilityColumns.CODE, object.getCode());
                        values.put(SQLHandler.HealthFacilityColumns.PARENT_ID, object.getParentId());
                        values.put(SQLHandler.HealthFacilityColumns.NAME, object.getName());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addHealthFacility(values);
                    }
                }
            }
        });
    }

    public void parseStatus() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_STATUS_LIST);
        Log.d("", webServiceUrl.toString());


        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                List<Status> objects = new ArrayList<Status>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + responseString);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(responseString, new TypeReference<List<Status>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Status object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.StatusColumns.ID, object.getId());
                        //Log.d("Status ID", object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.StatusColumns.NAME, object.getName());
                        //Log.d("Status NAME", object.getName());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addUpdateStatus(values, object.getId());
                    }
                }
            }
        });


    }

    public void parseItemLots() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_ITEM_LOT_ID);
        Log.d("", webServiceUrl.toString());


        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<ItemLot> objects = new ArrayList<ItemLot>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<ItemLot>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (ItemLot object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.ItemLotColumns.ID, object.getId());
                        values.put(SQLHandler.ItemLotColumns.EXPIRE_DATE, object.getExpireDate());
                        values.put(SQLHandler.ItemLotColumns.GTIN, object.getGtin());
                        values.put(SQLHandler.ItemLotColumns.ITEM_ID, object.getItemId());
                        values.put(SQLHandler.ItemLotColumns.LOT_NUMBER, object.getLotNumber());
                        //values.put(SQLHandler.ItemLotColumns.NOTES, object.getNotes());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addUpdateItemLot(values, object.getId());
                    }
                }
            }
        });
    }

    public void parseWeight() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_WEIGHT_LIST);
        Log.d("", webServiceUrl.toString());
        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                List<Weight> objects = new ArrayList<Weight>();

                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + responseString);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(responseString, new TypeReference<List<Weight>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Weight object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.WeightColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.WeightColumns.DAY, object.getDay());
                        values.put(SQLHandler.WeightColumns.GENDER, object.getGender());
                        values.put(SQLHandler.WeightColumns.SD0, object.getSD0());
                        values.put(SQLHandler.WeightColumns.SD1, object.getSD1());
                        values.put(SQLHandler.WeightColumns.SD2, object.getSD2());
                        values.put(SQLHandler.WeightColumns.SD3, object.getSD3());
                        values.put(SQLHandler.WeightColumns.SD4, object.getSD4());
                        values.put(SQLHandler.WeightColumns.SD1NEG, object.getSD1neg());
                        values.put(SQLHandler.WeightColumns.SD2NEG, object.getSD2neg());
                        values.put(SQLHandler.WeightColumns.SD3NEG, object.getSD3neg());
                        values.put(SQLHandler.WeightColumns.SD4NEG, object.getSD4neg());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addUpdateWeightList(values, object.getId());
                    }
                }
            }
        });
    }

    public void parseNonVaccinationReason() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_NON_VACCINATION_REASON_LIST);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<NonVaccinationReason> objects = new ArrayList<NonVaccinationReason>();

                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<NonVaccinationReason>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (NonVaccinationReason object : objects) {
                        ContentValues values = new ContentValues();
                        values.put(SQLHandler.NonVaccinationReasonColumns.ID, object.getId());
                        values.put(SQLHandler.SyncColumns.UPDATED, 1);
                        values.put(SQLHandler.NonVaccinationReasonColumns.NAME, object.getName());
                        values.put(SQLHandler.NonVaccinationReasonColumns.KEEP_CHILD_DUE, object.getKeepChildDue());
                        DatabaseHandler db = getDatabaseInstance();
                        db.addNonVaccinationReason(values);
                    }
                }
            }
        });
    }

    public void parseAgeDefinitions() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_AGE_DEFINITIONS_LIST);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<AgeDefinitions> objects = new ArrayList<AgeDefinitions>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<AgeDefinitions>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (AgeDefinitions object : objects) {
                        ContentValues adCV = new ContentValues();
                        DatabaseHandler db = getDatabaseInstance();

                        adCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                        adCV.put(SQLHandler.AgeDefinitionsColumns.DAYS, object.getDays());
                        adCV.put(SQLHandler.AgeDefinitionsColumns.ID, object.getId());
                        adCV.put(SQLHandler.AgeDefinitionsColumns.NAME, object.getName());
                        db.addAgeDefinitions(adCV);
                    }
                }
            }
        });
    }

    public void parseItem() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_ITEM_LIST);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<Item> objects = new ArrayList<Item>();

                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<Item>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Item object : objects) {
                        ContentValues itemCV = new ContentValues();
                        DatabaseHandler db = getDatabaseInstance();

                        itemCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                        itemCV.put(SQLHandler.ItemColumns.CODE, object.getCode());
                        itemCV.put(SQLHandler.ItemColumns.ENTRY_DATE, object.getEntryDate());
                        itemCV.put(SQLHandler.ItemColumns.EXIT_DATE, object.getExitDate());
                        itemCV.put(SQLHandler.ItemColumns.ID, object.getId());
                        itemCV.put(SQLHandler.ItemColumns.ITEM_CATEGORY_ID, object.getItemCategoryId());
                        itemCV.put(SQLHandler.ItemColumns.NAME, object.getName());
                        db.addItem(itemCV);
                    }
                }
            }
        });
    }

    public void parseScheduledVaccination() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_SCHEDULED_VACCINATION_LIST);
        Log.d("", webServiceUrl.toString());
        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<ScheduledVaccination> objects = new ArrayList<ScheduledVaccination>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    objects = mapper.readValue(response, new TypeReference<List<ScheduledVaccination>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (ScheduledVaccination object : objects) {
                        ContentValues scheduledVaccinationCV = new ContentValues();
                        DatabaseHandler db = getDatabaseInstance();

                        scheduledVaccinationCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.CODE, object.getCode());
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.ENTRY_DATE, object.getEntryDate());
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.EXIT_DATE, object.getExitDate());
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.ID, object.getId());
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.ITEM_ID, object.getItemId());
                        scheduledVaccinationCV.put(SQLHandler.ScheduledVaccinationColumns.NAME, object.getName());
                        db.addScheduledVaccination(scheduledVaccinationCV);
                    }
                }
            }
        });
    }

    public void parseDose() {
        final StringBuilder webServiceUrl = createWebServiceURL(LOGGED_IN_USER_HF_ID, GET_DOSE_LIST);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<Dose> objects = new ArrayList<Dose>();

                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<Dose>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (Dose object : objects) {
                        ContentValues doseCV = new ContentValues();
                        DatabaseHandler db = getDatabaseInstance();

                        doseCV.put(SQLHandler.SyncColumns.UPDATED, 1);
                        doseCV.put(SQLHandler.DoseColumns.AGE_DEFINITON_ID, object.getAgeDefinitionId());
                        doseCV.put(SQLHandler.DoseColumns.FULLNAME, object.getFullname());
                        doseCV.put(SQLHandler.DoseColumns.DOSE_NUMBER, object.getDoseNumber());
                        doseCV.put(SQLHandler.DoseColumns.ID, object.getId());
                        doseCV.put(SQLHandler.DoseColumns.FROM_AGE_DEFINITON_ID, object.getFromAgeDefId());
                        doseCV.put(SQLHandler.DoseColumns.TO_AGE_DEFINITON_ID, object.getToAgeDefId());
                        doseCV.put(SQLHandler.DoseColumns.SCHEDULED_VACCINATION_ID, object.getScheduledVaccinationId());
                        db.addDose(doseCV);
                    }
                }
            }
        });
    }

    public int parseChildById(String id) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC).append("GetChildById?childId=").append(id);
        //createWebServiceURL(LOGGED_IN_USER_HF_ID, SEARCH_BY_BARCODE); webServiceUrl.append("?barcodeId=").append(barcode);
        Log.d("parseChildCollectorbyId", webServiceUrl.toString());
        ChildCollector childCollector = new ChildCollector();
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return 3;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            childCollector = mapper.readValue(new JSONArray(response).getJSONObject(0).toString(), ChildCollector.class);

            addChildVaccinationEventVaccinationAppointment(childCollector);
            return 1;
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return 2;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 3;
        } catch (JSONException e) {
            e.printStackTrace();
            return 2;
        }
    }

    public void parseCustomHealthFacility(String hf_id) {
        final StringBuilder webServiceUrl = createWebServiceURL(hf_id, GET_HEALTH_FACILITY);
        Log.d("", webServiceUrl.toString());
        List<HealthFacility> objects = new ArrayList<HealthFacility>();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objects = mapper.readValue(response, new TypeReference<List<HealthFacility>>() {
            });

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (HealthFacility object : objects) {
                ContentValues values = new ContentValues();
                values.put(SQLHandler.HealthFacilityColumns.ID, object.getId());
                values.put(SQLHandler.SyncColumns.UPDATED, 1);
                values.put(SQLHandler.HealthFacilityColumns.CODE, object.getCode());
                values.put(SQLHandler.HealthFacilityColumns.PARENT_ID, object.getParentId());
                values.put(SQLHandler.HealthFacilityColumns.NAME, object.getName());
                DatabaseHandler db = getDatabaseInstance();
                db.addUpdateHealthFacility(values, object.getId());
            }
        }
    }

    public void parsePlaceById(String placeId) {
        final StringBuilder webServiceUrl = createWebServiceURL(placeId, GET_PLACE_BY_ID);
        Log.d("", webServiceUrl.toString());
        Place place = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            place = mapper.readValue(response, Place.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (place != null) {
                ContentValues values = new ContentValues();
                //Log.d("Place ID", object.getId());
                values.put(SQLHandler.PlaceColumns.ID, place.getId());
                values.put(SQLHandler.SyncColumns.UPDATED, 1);
                values.put(SQLHandler.PlaceColumns.NAME, place.getName());
                //Log.d("Place NAME", object.getName());
                values.put(SQLHandler.PlaceColumns.CODE, place.getCode());
                DatabaseHandler db = getDatabaseInstance();
                db.addPlacesThatWereNotInDB(values, place.getId());
            }
        }
    }

    public void parsePlaceByCustomHfId(String hf_id) {
        final StringBuilder webServiceUrl = createWebServiceURL(hf_id, GET_PLACE);
        Log.d("", webServiceUrl.toString());
        List<Place> objects = new ArrayList<Place>();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            objects = mapper.readValue(response, new TypeReference<List<Place>>() {
            });

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (Place object : objects) {
                ContentValues values = new ContentValues();
                //Log.d("Place ID", object.getId());
                values.put(SQLHandler.PlaceColumns.ID, object.getId());
                values.put(SQLHandler.SyncColumns.UPDATED, 1);
                values.put(SQLHandler.PlaceColumns.NAME, object.getName());
                //Log.d("Place NAME", object.getName());
                values.put(SQLHandler.PlaceColumns.CODE, object.getCode());
                DatabaseHandler db = getDatabaseInstance();
                db.addPlacesThatWereNotInDB(values, object.getId());
            }
        }
    }

    //needs to be merged with createWebServiceLoginURL and used with usr/pass as null in case not Login, hf as null in case of Login
    public StringBuilder createWebServiceURL(String rec_id, String service) {
        StringBuilder webServiceURL;

        switch (service) {
            case GET_PLACE:
                webServiceURL = new StringBuilder(WCF_URL).append(PLACE_MANAGEMENT_SVC).append(PLACE_MANAGEMENT_SVC_GETTER).append(rec_id);
                break;
            case GET_PLACE_LIST_ID:
                webServiceURL = new StringBuilder(WCF_URL).append(PLACE_MANAGEMENT_SVC).append(GET_PLACES_BY_LIST);
                break;
            case GET_HEALTH_FACILITY_LIST_ID:
                webServiceURL = new StringBuilder(WCF_URL).append(HEALTH_FACILITY_SVC).append(HEALTH_FACILITY_SVC_GETTER_BY_LIST);
                break;
            case GET_PLACE_BY_ID:
                webServiceURL = new StringBuilder(WCF_URL).append(PLACE_MANAGEMENT_SVC).append(PLACE_MANAGEMENT_SVC_GETTER_BY_ID).append(rec_id);
                break;
            case GET_CHILD:
                webServiceURL = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC).append(CHILD_MANAGEMENT_SVC_GETTER).append(rec_id);
                break;
            case GET_DOSE_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(DOSE_MANAGEMENT_SVC).append(DOSE_MANAGEMENT_SVC_GETTER);
                break;
            case GET_STATUS_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(STATUS_MANAGEMENT_SVC).append(STATUS_MANAGEMENT_SVC_GETTER);
                break;
            case GET_AGE_DEFINITIONS_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(AGE_DEFINITION_MANAGEMENT_SVC).append(AGE_DEFINITION_MANAGEMENT_SVC_GETTER);
                break;
            case GET_WEIGHT_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC).append(WEIGHT_MANAGEMENT_SVC_GETTER);
                break;
            case GET_NON_VACCINATION_REASON_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(NON_VACCINATION_REASON_MANAGEMENT_SVC).append(NON_VACCINATION_REASON_MANAGEMENT_SVC_GETTER);
                break;
            case GET_ITEM_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(ITEM_MANAGEMENT_SVC).append(ITEM_MANAGEMENT_SVC_GETTER);
                break;
            case GET_SCHEDULED_VACCINATION_LIST:
                webServiceURL = new StringBuilder(WCF_URL).append(SCHEDULED_VACCINATION_MANAGEMENT_SVC).append(SCHEDULED_VACCINATION_MANAGEMENT_SVC_GETTER);
                break;
            case SEARCH_BY_BARCODE:
                webServiceURL = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC).append(SEARCH_BY_BARCODE);
                break;
            case REGISTER_AUDIT:
                webServiceURL = new StringBuilder(WCF_URL).append(AUDIT_MANAGEMENT_SVC).append(REGISTER_AUDIT);
                break;
            case UPDATE_VACCINATION_QUEUE:
                webServiceURL = new StringBuilder(WCF_URL).append(VACCINATION_EVENT_SVC).append(UPDATE_VACCINATION_QUEUE);
                break;
            case GET_HEALTH_FACILITY:
                webServiceURL = new StringBuilder(WCF_URL).append(HEALTH_FACILITY_SVC).append(HEALTH_FACILITY_SVC_GETTER).append(rec_id);
                break;
            case GET_VACCINATION_QUEUE_BY_DATE_AND_USER:
                webServiceURL = new StringBuilder(WCF_URL).append(VACCINATION_QUEUE_MANAGEMENT_SVC).append(GET_VACCINATION_QUEUE_BY_DATE_AND_USER);
                break;
            case GET_ITEM_LOT_ID:
                webServiceURL = new StringBuilder(WCF_URL).append(STOCK_MANAGEMENT_SVC).append(ITEM_LOT_MANAGEMENT_SVC_GETTER);
                break;
            case GET_STOCK:
                webServiceURL = new StringBuilder(WCF_URL).append(STOCK_MANAGEMENT_SVC).append(STOCK_MANAGEMENT_SVC_GETTER).append(rec_id);
                break;
            default:
                webServiceURL = new StringBuilder(URL_BUILDER_ERROR);
                break;
        }

        return webServiceURL;
    }


    /**
     * @param lastname
     * @param bDate
     * @param gender
     * @return
     * @Arinela
     */
    public boolean checkChildInServer(String lastname, String bDate, String gender) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC);
        webServiceUrl.append("ChildExistsByLastnameAndBirthdateAndGender?lastname1=").append(URLEncoder.encode(lastname)).append("&birthdate=")
                .append(bDate).append("&gender=").append(gender);

        Log.e("service weight", webServiceUrl + "");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            if (result.equals("true")) return true;
            else return false;
        } catch (Exception e) {
            getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            return false;
        }

    }

    public int updateVaccinationEventOnServer(String url) {
        Log.e("Adm Vacc Server Upd URL", url);

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(url, 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return -1;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            JSONObject jobj = new JSONObject(result);
            int childID = jobj.getInt("id");
            if (childID == 1) {
                return childID;
            } else {
                getDatabaseInstance().addPost(url, 1);
                return -1;
            }

        } catch (Exception e) {
            getDatabaseInstance().addPost(url, 1);
            return -1;
        }
    }

    /**
     * @return child ID
     * @Arinela
     */
    public int registerChildWithAppoitments(String barcode, String fristname, String lastname, String bDate, String gender, String hfid, String birthPlaceId, String domId,
                                            String addr, String phone, String motherFirstname, String motherLastname, String notes, String userID, String modOn,
                                            PostmanModel postmanModel, String firstname2) {
        final StringBuilder webServiceUrl;
        if (postmanModel == null) {
            webServiceUrl = new StringBuilder(WCF_URL).append(CHILD_MANAGEMENT_SVC);
            webServiceUrl.append("RegisterChildWithAppoitments?barcodeid=").append(barcode).append("&firstname1=")
                    .append(URLEncoder.encode(fristname)).append("&lastname1=").append(URLEncoder.encode(lastname))
                    .append("&birthdate=").append(bDate).append("&gender=").append(gender)
                    .append("&healthFacilityId=").append(hfid).append("&birthplaceId=").append(birthPlaceId).append("&domicileId=")
                    .append(domId).append("&address=").append(URLEncoder.encode(addr))
                    .append("&phone=").append(URLEncoder.encode(phone))
                    .append("&motherFirstname=").append(URLEncoder.encode(motherFirstname)).append("&motherLastname=").append(URLEncoder.encode(motherLastname))
                    .append("&notes=").append(URLEncoder.encode(notes))
                    .append("&userId=").append(userID).append("&modifiedOn=").append(modOn)
                    .append("&firstname2=").append((firstname2 != null) ? firstname2 : "");


        } else {
            webServiceUrl = new StringBuilder(postmanModel.getUrl());
        }

        Log.e("service weight", webServiceUrl + "");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 3);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return -1;
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            JSONObject jobj = new JSONObject(result);
            int childID = jobj.getInt("id");

            return childID;

        } catch (Exception e) {
            getDatabaseInstance().addPost(webServiceUrl.toString(), 3);
            return -1;
        }

    }


    /**
     * this method expects the childBarcode value and one of doseId(not needed a precise one)
     * // @param childBarcode
     * // @param doseId
     */
    public int updateVaccinationAppOutreach(String childBarcode, String doseId) {
        final StringBuilder webServiceUrl;
        webServiceUrl = new StringBuilder(WCF_URL).append("VaccinationAppointmentManagement.svc/UpdateVaccinationApp?outreach=true&userId=")
                .append(getLOGGED_IN_USER_ID())
                .append("&barcode=").append(childBarcode)
                .append("&doseId=").append(doseId);


        Log.e("service appointment outreach", webServiceUrl + "");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
            }
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            JSONObject jobj = new JSONObject(result);
            int idReturned = jobj.getInt("id");
            // if any check is needed to be performed after communicating here you have the result parsed into this int
            return idReturned;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return -1;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            if (webServiceUrl != null) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            }
            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * Parsing data from Server after the First login information parser on home activity
     */
    public void continuousModificationParser() {
        if (!USERNAME.equalsIgnoreCase("default")) {

            String url = WCF_URL + "ChildManagement.svc/GetChildrenByHealthFacilityBeforeLastLogin?idUser=" + getLOGGED_IN_USER_ID();
            Log.d("secondLoginURL", url);

            ChildCollector2 objects2 = new ChildCollector2();

            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
                httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStream inputStream = httpResponse.getEntity().getContent();
                String response = Utils.getStringFromInputStream(inputStream);
                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                ObjectMapper mapper = new ObjectMapper();
                objects2 = mapper.readValue(response, ChildCollector2.class);

            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                addChildVaccinationEventVaccinationAppointment(objects2);
            }
        }

    }

    /**
     * Parsing data from Server after the First login information parser on home activity
     */
    public void firstLoginOfTheDay() {
        if (!USERNAME.equalsIgnoreCase("default")) {

            String url = WCF_URL + "ChildManagement.svc/GetChildrenByHealthFacilityDayFirstLogin?idUser=" + getLOGGED_IN_USER_ID();
            Log.d("secondLoginURL", url);

            client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
            RequestHandle message = client.get(url, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    ChildCollector2 objects2 = new ChildCollector2();

                    try {
                        Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                        ObjectMapper mapper = new ObjectMapper();
                        objects2 = mapper.readValue(response, new TypeReference<List<ChildCollector>>() {
                        });

                    } catch (JsonGenerationException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        addChildVaccinationEventVaccinationAppointment(objects2);
                    }
                }
            });
        }

    }

    /**
     * Parsing data from Server after the First login in intervals
     */
    public void intervalGetChildrenByHealthFacilitySinceLastLogin() {

        String url = WCF_URL + "ChildManagement.svc/GetChildrenByHealthFacilitySinceLastLogin?idUser=" + getLOGGED_IN_USER_ID();
        Log.e("SinceLastLogin", "GetChildrenByHealthFacilitySinceLastLogin url is: " + url);

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(url.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                ChildCollector2 objects2 = new ChildCollector2();

                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    objects2 = mapper.readValue(response, new TypeReference<List<ChildCollector>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    addChildVaccinationEventVaccinationAppointment(objects2);
                }

            }
        });


    }

    /**
     * Parsing data from Server getChildByBarcodeList to get children that we dont have but are found in the vacc queue of server
     */
    public void getChildByBarcodeList(String childIds) {

        String url = WCF_URL + "Childmanagement.svc/GetChildByBarcodeList?childList=" + childIds;
        Log.d("getChildByBarcodeList", url);

        List<ChildCollector> objects2 = new ArrayList<ChildCollector>();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            objects2 = mapper.readValue(response, new TypeReference<List<ChildCollector>>() {
            });

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (ChildCollector object : objects2) {
                addChildVaccinationEventVaccinationAppointment(object);
            }
        }


    }

    /**
     * Parsing data from Server GetChildByIdListSince to get children or update
     * before using this method we need to check if there is a logged in user in the app
     */
    public void getGetChildByIdListSince() {
        String childIds = getDatabaseInstance().getChildrenFromOtherHFIDThanLoggedUser(getLOGGED_IN_USER_HF_ID());
        if (childIds == null) return;
        String url = WCF_URL + "ChildManagement.svc/GetChildByIdListSince?childIdList=" + childIds + "&userId=" + getLOGGED_IN_USER_ID();
        Log.d("getChildByBarcodeList", url);

        ChildCollector2 objects2 = new ChildCollector2();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            objects2 = mapper.readValue(response, new TypeReference<List<ChildCollector>>() {
            });

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            addChildVaccinationEventVaccinationAppointment(objects2);
        }


    }

    /**
     * Parsing data from Server GetChildByIdList to get children or update
     * before using this method we need to check if there is a logged in user in the app
     */
    public void getGetChildByIdList() {
        String childIds = getDatabaseInstance().getChildrenFromOtherHFIDThanLoggedUser(getLOGGED_IN_USER_HF_ID());
        if (childIds == null) return;
        String url = WCF_URL + "ChildManagement.svc/GetChildByIdList?childIdList=" + childIds + "&userId=" + getLOGGED_IN_USER_ID();
        Log.d("getChildByBarcodeList", url);

        ChildCollector2 objects2 = new ChildCollector2();

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + url.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
            ObjectMapper mapper = new ObjectMapper();
            objects2 = mapper.readValue(response, new TypeReference<List<ChildCollector>>() {
            });

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            addChildVaccinationEventVaccinationAppointment(objects2);
        }


    }


    //method for AdminVacc

    public void setUpdateURL(AdministerVaccinesModel a, String strNotes, String strBarcode) {
        String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
        try {
            dateTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        String vnotes = "";
        if (!(strNotes.equalsIgnoreCase(""))) {
            vnotes = strNotes;
        }
        final StringBuilder VaccinationEventUpdateURL = new StringBuilder(WCF_URL + "VaccinationEvent.svc/UpdateVaccinationEventBarcodeAndDoseId?")
                .append("barcode=").append(strBarcode)
                .append("&doseId=").append(a.getDose_id())
                .append("&vaccineLotId=").append(a.getVaccination_lot()).append("&healthFacilityId=").append(getLOGGED_IN_USER_HF_ID())
                .append("&vaccinationDate=").append(URLEncoder.encode(formatted.format(a.getTime2())))
                .append("&notes=").append(vnotes)
                .append("&vaccinationStatus=").append(a.getStatus())
                .append("&nonvaccinationReasonId=").append(a.getNon_vac_reason())
                .append("&userId=").append(this.getLOGGED_IN_USER_ID())
                .append("&modifiedOn=").append(dateTodayTimestamp);
        //.append("&vaccinationEventId=").append(vacc_ev_id);

        a.setUpdateURL(VaccinationEventUpdateURL.toString());
    }

    public void setAppointmentUpdateURL(AdministerVaccinesModel a, String appointment_id, CheckBox cbOutreach) {
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

        final StringBuilder VaccinationAppointmentUpdateURL = new StringBuilder(WCF_URL + "VaccinationAppointmentManagement.svc/UpdateVaccinationApp?")
                .append("outreach=").append(String.valueOf(cbOutreach.isChecked()))
                .append("&userId=").append(getLOGGED_IN_USER_ID())
                .append("&vaccinationAppointmentId=").append(appointment_id);

        a.setUpdateURLAppointment(VaccinationAppointmentUpdateURL.toString());

    }


/*
    //Shtesa per volley

    public static final String TAG = BackboneApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static BackboneApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized BackboneApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }*/

    private  boolean isHealthFacilityBalanceSaved;
    public boolean saveHealthFacilityBalance(String gtin, String lotno, String qty, String date, String userId) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(STOCK_MANAGEMENT_SVC);
        webServiceUrl.append("StockCount?gtin=").append(gtin).append("&lotno=").append(lotno).append("&qty=").append(qty)
                .append("&date=").append(date).append("&userId=").append(userId);

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e(" save health faci", webServiceUrl + "");
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(webServiceUrl.toString());
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
                    httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    String result = Utils.getStringFromInputStream(inputStream);
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
                    if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                        Utils.writeNetworkLogFileOnSD(
                                Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                        + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                        + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                        + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                        isHealthFacilityBalanceSaved = false;
                    }
                    isHealthFacilityBalanceSaved = true;

                } catch (Exception e) {
                    getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                    isHealthFacilityBalanceSaved = false;
                }
            }
        });
        return isHealthFacilityBalanceSaved;

    }

    public void parseStockAdjustmentReasons() {

        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(STOCK_MANAGEMENT_SVC).append(GET_STOCK_ADJUSTMENT);
        Log.d("", webServiceUrl.toString());

        client.setBasicAuth(LOGGED_IN_USERNAME, LOGGED_IN_USER_PASS, true);
        RequestHandle message = client.get(webServiceUrl.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                List<AdjustmentReasons> objects = new ArrayList<AdjustmentReasons>();
                try {
                    Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + response);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                    objects = mapper.readValue(response, new TypeReference<List<AdjustmentReasons>>() {
                    });

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    for (AdjustmentReasons object : objects) {
                        ContentValues adCV = new ContentValues();
                        DatabaseHandler db = getDatabaseInstance();

                        adCV.put(SQLHandler.AdjustmentColumns.NAME, object.getName());
                        adCV.put(SQLHandler.AdjustmentColumns.ID, object.getId());
                        adCV.put(SQLHandler.AdjustmentColumns.POSITIVE, object.getPozitive());
                        adCV.put(SQLHandler.AdjustmentColumns.IS_ACTIVE, object.getIsActive());
                        db.addStockAdjustment(adCV);
                    }
                }
            }
        });
    }


    public boolean saveStockAdjustmentReasons(String gtin, String lotno, String qty, String date, String reasonId, String userId) {
        final StringBuilder webServiceUrl = new StringBuilder(WCF_URL).append(STOCK_MANAGEMENT_SVC);
        webServiceUrl.append("StockAdjustment?gtin=").append(gtin).append("&lotno=").append(lotno).append("&qty=").append(qty)
                .append("&date=").append(date).append("&reasonId=").append(reasonId).
                append("&userId=").append(userId);

        Log.e(" save health faci", webServiceUrl + "");
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(webServiceUrl.toString());
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + webServiceUrl.toString());
            httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((LOGGED_IN_USERNAME + ":" + LOGGED_IN_USER_PASS).getBytes(), Base64.NO_WRAP));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String result = Utils.getStringFromInputStream(inputStream);
            Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext()) + result);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
                Utils.writeNetworkLogFileOnSD(
                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                return false;
            }
            return true;

        } catch (Exception e) {
            getDatabaseInstance().addPost(webServiceUrl.toString(), 1);
            return false;
        }

    }


    private class UsePoolThreadResponseHandler extends AsyncHttpResponseHandler {

        public UsePoolThreadResponseHandler() {
            super();

            // We wish to use the same pool thread to run the response.
            setUsePoolThread(true);
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBody) {
            Log.d(TAG,"receiving data in streams");
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = mapper.getJsonFactory();
            try {
                JsonParser jp = jsonFactory.createJsonParser(responseBody);
                JsonToken token;
                token = jp.nextToken();

                long tStart = System.currentTimeMillis();
                Log.e("TIMING LOG", "Parsing start ");
                while ((token = jp.nextToken()) != null) {
                    switch (token) {
                        case START_OBJECT:
                            JsonNode node = jp.readValueAsTree();
                            ChildCollector obj = mapper.treeToValue(node, ChildCollector.class);
                            addChildVaccinationEventVaccinationAppointment(obj);
                            break;
                    }
                }
                Log.e("TIMING LOG", "elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(final int statusCode, final Header[] headers, final byte[] responseBody, final Throwable error) {
            // This callback is now running within the pool thread execution
            // scope and not within Android's UI thread, so if we must update
            // the UI, we'll have to dispatch a runnable to the UI thread.
            Log.d(TAG, "Error = "+statusCode);
            error.printStackTrace();

        }


    }
}
