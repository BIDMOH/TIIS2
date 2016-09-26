package mobile.tiis.appV2.GCMCommunication;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Coze on 7/22/13.
 */
public class JSONParser {
    private static final String TAG = JSONParser.class.getSimpleName();
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArry = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String targetURL, String method,
                                      List<NameValuePair> params) {

        int size = params.size();

        String urlParameters = null;
        try {
            urlParameters = params.get(0).getName()+"=" + URLEncoder.encode(params.get(0).getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (int i=1;i<size;i++){
            try {
               urlParameters+= "&"+params.get(i).getName()+ URLEncoder.encode(params.get(0).getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        Log.d(TAG,"parameter = "+urlParameters);

        // Making HTTP request
        try {
            // check for request method
            if(method == "POST"){
                URL url;
                HttpURLConnection connection = null;
                try {
                    //Create connection
                    url = new URL(targetURL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");

                    connection.setUseCaches (false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream (
                            connection.getOutputStream ());
                    wr.writeBytes (urlParameters);
                    wr.flush ();
                    wr.close ();

                    //Get Response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();

                    // try parse the string to a JSON object
                    try {
                        jObj = new JSONObject(response.toString());
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }

                    // return JSON String
                    return jObj;

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;

                } finally {

                    if(connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }catch (Exception ex){
            Log.d("Netxxxxx", ex.toString());
        }
        return null;
    }

    public JSONArray makeHttpRequestJsonArray(String targetURL, String method,
                                      List<NameValuePair> params) {


        int size = params.size();

        String urlParameters = null;
        try {
            urlParameters = params.get(0).getName()+"=" + URLEncoder.encode(params.get(0).getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (int i=1;i<size;i++){
            try {
                urlParameters+= "&"+params.get(i).getName()+ URLEncoder.encode(params.get(0).getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        Log.d(TAG,"parameter = "+urlParameters);

        // Making HTTP request
        try {
            // check for request method
            if(method == "POST"){
                URL url;
                HttpURLConnection connection = null;
                try {
                    //Create connection
                    url = new URL(targetURL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");

                    connection.setUseCaches (false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream (
                            connection.getOutputStream ());
                    wr.writeBytes (urlParameters);
                    wr.flush ();
                    wr.close ();

                    //Get Response
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();

                    // try parse the string to a JSON object
                    try {
                        jArry = new JSONArray(response.toString());
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }

                    // return JSON String
                    return jArry;

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;

                } finally {

                    if(connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }catch (Exception ex){
            Log.d("Netxxxxx", ex.toString());
        }
        return null;
    }

    public JSONObject postJson(String url,JSONObject obj) {
        // Create a new HttpClient and Post Header

        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        HttpClient httpclient = new DefaultHttpClient(myParams );
        String json=obj.toString();
        Log.d(TAG,"string created from json"+json);

        try {

            HttpPost httppost = new HttpPost(url.toString());
            httppost.setHeader("Content-type", "application/json");

            Log.d(TAG, "URL Host: "+httppost.getURI().getHost());
            Log.d(TAG, "URL Path: "+httppost.getURI().getPath());
            Log.d(TAG, "URL:      "+url.trim());

            StringEntity se = new StringEntity(obj.toString());
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            Log.d(TAG,"json from server"+sb);
            is.close();
            json = sb.toString();
            if(json.endsWith("n")){
                json=json.substring(0,json.length() - 1);
            }
            Log.d("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        JSONObject object=null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return object;
    }
}