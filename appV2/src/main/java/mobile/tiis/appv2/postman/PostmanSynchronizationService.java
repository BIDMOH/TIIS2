package mobile.tiis.appv2.postman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import mobile.tiis.appv2.GCMCommunication.CommonUtilities;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.entity.User;
import mobile.tiis.appv2.helpers.Utils;
import mobile.tiis.appv2.DatabaseModals.SessionsModel;


public class PostmanSynchronizationService extends Service {

    private static final String TAG = PostmanSynchronizationService.class.getSimpleName();

    public static final long NOTIFY_INTERVAL = 1 * 60000; // 60 seconds
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




        aClient.setURLEncodingEnabled(false);
        aClient.setThreadPool(Executors.newFixedThreadPool(50));
        aClient.setBasicAuth(userName,password);

        Log.d(TAG, ">>>onCreate()");

        List<PostmanModel> listPosts = db.getAllPosts();
        Log.d(TAG,"getting all posts");
        Log.d(TAG,"user = "+userName);
        Log.d(TAG,"password = "+password);
        try {
            sendResult(listPosts.size() + "", getApplicationContext());
        }catch (Exception e){
            sendResult("0", getApplicationContext());
        }
        JSONArray postmanArray = new JSONArray();

        Log.d(TAG,"postman count  = "+listPosts.size());
        for (final PostmanModel p : listPosts) {
//                        sendResult(db.getAllPosts().size()+"",getApplicationContext());
//                        Log.d(TAG,"url = "+p.getUrl());
            if(p.getUrl() == null || p.getUrl().trim().equals("")){
                try {
                    Thread.sleep(5000);
                    db.deletePostFromPostman(p.getPostId());
                    Log.d("POSTMAN DELETED","ID = "+p.getPostId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {

                postmanArray.put(p.getUrl());
            }
        }

        List<SessionsModel> listModels = app.GetHealthFacilitySessionUpdateUrl();

        for (final SessionsModel p : listModels) {
            Log.d(TAG,"session url = "+p.getUrl());

            if(p.getUrl() == null || p.getUrl().trim().equals("")){
                try {
                    Thread.sleep(5000);
                    db.updateHealthFacilityStatus(p.get_id(),1);
                    Log.d("SESSION DELETED","ID = "+p.get_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                postmanArray.put(p.getUrl());
            }

        }

        storeToFile(postmanArray);
    }

    public void sendResult(String message,Context context) {
        Log.d(TAG,"sending postman count "+message);
        try {
            Intent intent = new Intent(CommonUtilities.DISPLAY_POSTMAN_COUNT_ACTION);
            if (message != null)
                intent.putExtra(SynchronisationService_MESSAGE, message);
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void storeToFile(JSONArray array){
        Log.d(TAG,"storing data to file  = "+array.toString());

        try {
            Writer output = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/tiis/" + "postman.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(array.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "postman saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}