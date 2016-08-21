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

package mobile.tiis.app.postman;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mobile.tiis.app.GCMCommunication.CommonUtilities;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by Rubin on 3/18/2015.
 * subclass for handling asynchronous task requests in
 * a service on a separate thread.
 */
public class SynchronisationService extends IntentService {


    public static final String SynchronisationService_MESSAGE = "mobile.giis.app.SynchronisationService.MSG";
    private static final String TAG = "SynchronisationService";

    public SynchronisationService() {
        super(SynchronisationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG,"synchronizing postman data");
        BackboneApplication app = (BackboneApplication) getApplication();
        synchronized (app) {
            DatabaseHandler db = app.getDatabaseInstance();
            List<PostmanModel> listPosts = db.getAllPosts();
            try {
                sendResult(listPosts.size() + "", getApplicationContext());
            }catch (Exception e){
                sendResult("0", getApplicationContext());
            }
            if (listPosts != null && app.getLOGGED_IN_USER_PASS() != null && app.getLOGGED_IN_USERNAME() != null) {
                for (PostmanModel p : listPosts) {
                    sendResult(db.getAllPosts().size()+"",getApplicationContext());
                    Log.d("POSTMAN PROCESSING","url = "+p.getUrl());
                    if(p.getUrl() == null || p.getUrl().trim().equals(""))continue;
                    try {
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(p.getUrl());
                        Utils.writeNetworkLogFileOnSD("SynchronisationService" + " "+ Utils.returnDeviceIdAndTimestamp(getApplicationContext())+p.getUrl() );
                        httpGet.setHeader("Authorization", "Basic " + Base64.encodeToString((app.getLOGGED_IN_USERNAME() + ":" + app.getLOGGED_IN_USER_PASS()).getBytes(), Base64.NO_WRAP));
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                            Log.d("POSTMAN Processing","code not 200. Code = "+httpResponse.getStatusLine().getStatusCode());
                            InputStream inputStream = httpResponse.getEntity().getContent();
                            // the response as a string if needed
                            String response = Utils.getStringFromInputStream(inputStream);
                            Log.d("POSTMAN Processing","code not 200. Responce = "+response);
                            Utils.writeNetworkLogFileOnSD(
                                    Utils.returnDeviceIdAndTimestamp(getApplicationContext())
                                            + " StatusCode " + httpResponse.getStatusLine().getStatusCode()
                                            + " ReasonPhrase " + httpResponse.getStatusLine().getReasonPhrase()
                                            + " ProtocolVersion " + httpResponse.getStatusLine().getProtocolVersion());
                            return;
                        }

                        // the input stream of the response
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        // the response as a string if needed
                        String response = Utils.getStringFromInputStream(inputStream);
                        Utils.writeNetworkLogFileOnSD(Utils.returnDeviceIdAndTimestamp(getApplicationContext())+response);

                        int codeNeg99 = 0;
                        try{
                            JSONObject jobj= null;
                            jobj = new JSONObject(response);
                            codeNeg99 = jobj.getInt("id");

                            Log.d("POSTMAN Processing","received id = "+codeNeg99);
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
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IllegalStateException ise){
                        ise.printStackTrace();
                        continue;
                    }
                }
            }
            app.parseGCMChildrenInQueueById();
        }

        this.stopSelf();
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