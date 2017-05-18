package mobile.tiis.staging.postman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.SyncHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import mobile.tiis.staging.DatabaseModals.SessionsModel;
import mobile.tiis.staging.GCMCommunication.CommonUtilities;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.entity.User;
import mobile.tiis.staging.helpers.Utils;

public class PostmanSynchronizationService extends Service {

    private static final String TAG = PostmanSynchronizationService.class.getSimpleName();

    public static final long NOTIFY_INTERVAL = 30 * 60000; // 30 minutes
    public static final String SynchronisationService_MESSAGE = "mobile.giis.app.SynchronisationService.MSG";
    // timer handling
    private Timer mTimer = null;
    private DatabaseHandler db;
    private BackboneApplication app;
    private AsyncHttpClient aClient = new SyncHttpClient();
    private String userName,password;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        app = (BackboneApplication) getApplication();
        db = app.getDatabaseInstance();

        if (app.getLOGGED_IN_USERNAME() == null) {
            Log.d(TAG,"userid null");
            List<User> allUsers = db.getAllUsers();
            User user = allUsers.get(0);
            userName = user.getUsername();
            password = user.getPassword();

            Log.d(TAG,"user = "+userName);
        } else {
            userName = app.getLOGGED_IN_USERNAME();
            password = app.getLOGGED_IN_USER_PASS();
        }


        // TODO Remove this from production code
        try {
            /// We initialize a default Keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            // We initialize a new SSLSocketFacrory
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            // We set that all host names are allowed in the socket factory
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // We set the SSL Factory
            aClient.setSSLSocketFactory(socketFactory);
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



        aClient.setURLEncodingEnabled(false);
        aClient.setThreadPool(Executors.newFixedThreadPool(50));
        aClient.setBasicAuth(userName,password);

        Log.d(TAG, ">>>onCreate()");
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {

            Log.d(TAG,"synchronizing postman data");
            synchronized (app) {
                app.CheckNewStockDistributionsFromVims();
                List<PostmanModel> listPosts = db.getAllPosts();
                Log.d(TAG,"getting all posts");
                Log.d(TAG,"user = "+userName);
                Log.d(TAG,"password = "+password);
                try {
                    sendResult(listPosts.size() + "", getApplicationContext());
                }catch (Exception e){
                    sendResult("0", getApplicationContext());
                }
                if (listPosts != null && userName != null && password != null) {
                    Log.d(TAG,"postman count  = "+listPosts.size());
                    for (final PostmanModel p : listPosts) {
                        sendResult(db.getAllPosts().size()+"",getApplicationContext());
                        Log.d(TAG,"url = "+p.getUrl());
                        if(p.getUrl() == null || p.getUrl().trim().equals("")){
                            try {
                                Thread.sleep(5000);
                                db.deletePostFromPostman(p.getPostId());
                                Log.d("POSTMAN COMPLETE","url = "+p.getUrl());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        aClient.get(p.getUrl(), new AsyncHttpResponseHandler(true) {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext())+response);
                                int codeNeg99 = 0;
                                try{
                                    JSONObject jobj= null;
                                    jobj = new JSONObject(response);
                                    codeNeg99 = jobj.getInt("id");

                                    Log.d(TAG,"received id = "+codeNeg99);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                if(codeNeg99 != -99 && codeNeg99 != -1) {
                                    boolean res = false;
                                    do {
                                        try {
                                            Thread.sleep(10000);
                                            res = db.deletePostFromPostman(p.getPostId());
                                            Log.d("POSTMAN COMPLETE","url = "+p.getUrl());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }while(!res);
                                }else{
                                    //check if this was a duplicate barcode registered in the server. if so delete the child
                                    if(p.getUrl().contains("RegisterChildWithAppoitmentsWithMothersHivStatusAndTT2VaccineStatusAndCatchment")){
                                        String barcode =  p.getUrl().split("=")[1].substring(0,10);
                                        Log.d(TAG,"barcode = "+barcode);
                                        db.removeChildFromChildTable(db.getChildIdByBarcode(barcode));

                                        //In some very minor cases a network connectivity may be disrupted after posting information to the server but before receiving the results
                                        //this  leads to some scenariors where the data remains in postman and on resending a childregistration request
                                        // that has already been registered in the server for the second time, the server will keep returning a -1 results code showing that the child is a dublicate.
                                        //To fix such issues after deletion of a dublicate registration entry from the postman and the database it is important to synch that childs details from the server into the device
                                        //by using the child's barcode.

                                        //TODO create a custome method for broadcasting child updates to only this specific device and not all other devices with this  child's data
                                        app.broadcastChildUpdatesWithBarcodeId(barcode);

                                        boolean res1 = false;
                                        do {
                                            try {
                                                Thread.sleep(10000);
                                                res1 = db.deletePostFromPostman(p.getPostId());
                                                Log.d("POSTMAN COMPLETE","url = "+p.getUrl());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }while(!res1);

                                    }else {
                                        if(p.getResponseType()==-10){
                                            //if the postman fails 10 times delete it from postman
                                            Log.d(TAG,"response type = "+(p.getResponseType()-1));
                                            boolean res1 = false;
                                            do {
                                                try {
                                                    Thread.sleep(10000);
                                                    res1 = db.deletePostFromPostman(p.getPostId());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }while(!res1);

                                        }else{
                                            //if the postman fails 10 times delete it from postman
                                            long res1 = -99;
                                            do {
                                                try {
                                                    Thread.sleep(10000);
                                                    int responseTypeId = p.getResponseType()-1;
                                                    Log.d(TAG,"updating response type = "+responseTypeId);
                                                    Log.d(TAG,"updating url = "+(p.getUrl()));
                                                    Log.d(TAG,"updating postman id = "+(p.getPostId()));
                                                    res1 = db.updatePost(p.getUrl(),responseTypeId,p.getPostId());
                                                    Log.d(TAG,"update post result = "+res1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }while(res1==-99);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Utils.writeNetworkLogFileOnSD(
                                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                                + " StatusCode " + statusCode);
                                return;
                            }
                        });
                    }
                }


                List<SessionsModel> listModels = app.GetHealthFacilitySessionUpdateUrl();

                if (listModels != null && userName != null && password != null) {
                    Log.d(TAG,"sessions count  = "+listModels.size());
                    for (final SessionsModel p : listModels) {
                        Log.d(TAG,"session url = "+p.getUrl());


                        if(p.getUrl() == null || p.getUrl().trim().equals("")){
                            try {
                                Thread.sleep(5000);
                                db.updateHealthFacilityStatus(p.get_id(),1);
                                Log.d("SESSION COMPLETE","url = "+p.getUrl());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        aClient.get(p.getUrl(), new AsyncHttpResponseHandler(true) {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext())+response);
                                int codeNeg99 = 0;
                                try{
                                    JSONObject jobj= null;
                                    jobj = new JSONObject(response);
                                    codeNeg99 = jobj.getInt("id");

                                    Log.d(TAG,"received session id = "+codeNeg99);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                if(codeNeg99 != -1) {
                                    long res = -1;
                                    do {
                                        try {
                                            Thread.sleep(10000);
                                            res = db.deleteHealthFacilityStatus(p.get_id());
                                            Log.d("POSTMAN COMPLETE","url = "+p.getUrl());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }while(res==-1);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Utils.writeNetworkLogFileOnSD(
                                        Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                                + " StatusCode " + statusCode);
                                return;
                            }
                        });
                    }
                }


                app.parseGCMChildrenInQueueById();
                app.parseItemLots();
                app.parseStock();
            }

        }

        public void sendResult(String message,Context context) {
            Log.d(TAG,"sending ppostman count "+message);
            try {
                Intent intent = new Intent(CommonUtilities.DISPLAY_POSTMAN_COUNT_ACTION);
                if (message != null)
                    intent.putExtra(SynchronisationService_MESSAGE, message);
                context.sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}