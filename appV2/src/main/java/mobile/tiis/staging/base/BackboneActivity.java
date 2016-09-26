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

package mobile.tiis.staging.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobile.tiis.staging.HomeActivityRevised;
import mobile.tiis.staging.helpers.Utils;

/**
 * This activity will be the backbone activity to be implemented by the
 * stack of activities which extend Activity Class and share the basic methods.
 *
 * @author Teodor Rupi
 * @author Melisa Aruci
 */
public class BackboneActivity extends RxAppCompatActivity {

    public static final String WCF_URL = BackboneApplication.getWcfUrl(); //"http://142.222.45.61/svc/";
    public static final String USER_MANAGEMENT_SVC = "UserManagement.svc/";
    public static final String USER_MANAGEMENT_SVC_GETTER = "GetUser";
    public static final String USER_MANAGEMENT_WITH_GCM_SVC_GETTER = "GetUserWithGcm";
    public static final String ACCOUNT_TYPE = "tiis.mobile.account";
    public static final String ACTIVITY_CHECK_IN = "CHECK_IN";
    public static final String FRAGMENT_HOME = "HOME";
    public static final String LANGUAGELOGIN = "SELECTEDLANGUAGELOGIN";

    public static Typeface Athletic, Fun_Raiser, Roboto_Condensed, Roboto_Black, Roboto_Light, Roboto_BoldCondensedItalic, Roboto_BoldCondensed, Rosario_Regular, Rosario_Bold, Rosario_Italic, Roboto_Regular, Roboto_Medium;

    BackboneApplication application = new BackboneApplication();
    private SharedPreferences sync_preferences;
    private boolean sync_needed;

    public static Date dateParser(String date_str){
        Date date = null;
        Pattern pattern = Pattern.compile("\\((.*?)-");
        Pattern pattern_plus = Pattern.compile("\\((.*?)\\+");
        Matcher matcher = pattern.matcher(date_str);
        Matcher matcher_plus = pattern_plus.matcher(date_str);
        if (matcher.find()) {
            date = new Date(Long.parseLong(matcher.group(1)));
        } else if (matcher_plus.find()) {
            date = new Date(Long.parseLong(matcher_plus.group(1)));
        } else {
            date = new Date();
        }
        return date;
    }

    /**
     * Funciton that helps us to reformat the date in the string pattern that we have in the database
     * @param date
     * @return
     */
    public static String stringToDateParser(Date date) {
        String timestamp = date.getTime() + "";
        SimpleDateFormat formater = new SimpleDateFormat("Z");
        String timeZone = formater.format(date);
        return "/Date(" + timestamp + timeZone+")/";
    }


    public static void setupTypeface(Context ctx){
        Roboto_Light = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Light.ttf");
        Roboto_Black = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Black.ttf");
        Roboto_Condensed = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Condensed.ttf");
        Roboto_BoldCondensedItalic = Typeface.createFromAsset(ctx.getAssets(), "Roboto-BoldCondensedItalic.ttf");
        Roboto_BoldCondensed = Typeface.createFromAsset(ctx.getAssets(), "Roboto-BoldCondensed.ttf");
        Roboto_Regular = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Medium.ttf");
        Rosario_Regular = Typeface.createFromAsset(ctx.getAssets(), "Rosario-Regular.ttf");
        Rosario_Italic = Typeface.createFromAsset(ctx.getAssets(), "Rosario-Italic.ttf");
        Rosario_Bold = Typeface.createFromAsset(ctx.getAssets(), "Rosario-Bold.ttf");
        Fun_Raiser = Typeface.createFromAsset(ctx.getAssets(), "Fun-Raiser.ttf");
        Athletic = Typeface.createFromAsset(ctx.getAssets(), "athletic.ttf");
    }

    /**
     * onCreate - called when the activity is first created.
     *
     * Called when the activity is first created. This is where you should do
     * all of your normal static set up: create views, bind data to lists, etc.
     * This method also provides you with a Bundle containing the activity's
     * previously frozen state, if there was one.
     *
     * Always followed by onStart().
     *
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onDestroy The final call you receive before your activity is destroyed.
     * This can happen either because the activity is finishing (someone called
     * finish() on it, or because the system is temporarily destroying this
     * instance of the activity to save space. You can distinguish between these
     * two scenarios with the isFinishing() method.
     *
     */

    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * onPause Called when the system is about to start resuming a previous
     * activity. This is typically used to commit unsaved changes to persistent
     * data, stop animations and other things that may be consuming CPU, etc.
     * Implementations of this method must be very quick because the next
     * activity will not be resumed until this method returns. Followed by
     * either onResume() if the activity returns back to the front, or onStop()
     * if it becomes invisible to the user.
     *
     */

    protected void onPause() {
        super.onPause();
    }

    /**
     * onRestart Called after your activity has been stopped, prior to it being
     * started again. Always followed by onStart().
     *
     */

    protected void onRestart() {
        super.onRestart();
    }

    /**
     * onResume Called when the activity will start interacting with the user.
     * At this point your activity is at the top of the activity stack, with
     * user input going to it. Always followed by onPause().
     *
     */

    protected void onResume() {
        super.onResume();
    }

    /**
     * onStart Called when the activity is becoming visible to the user.
     * Followed by onResume() if the activity comes to the foreground, or
     * onStop() if it becomes hidden.
     *
     */

    protected void onStart() {
        super.onStart();
    }

    /**
     * onStop Called when the activity is no longer visible to the user because
     * another activity has been resumed and is covering this one. This may
     * happen either because a new activity is being started, an existing one is
     * being brought in front of this one, or this one is being destroyed.
     *
     * Followed by either onRestart() if this activity is coming back to
     * interact with the user, or onDestroy() if this activity is going away.
     */

    protected void onStop() {
        super.onStop();
    }

    /**
     * This method will build URL to the webservice
     * where we can collect login information.
     *
     * @param username
     * @param password
     * @return StringBuilder webServiceLoginURL
     */

    public StringBuilder createWebServiceLoginURL(String username, String password, String gcmId ) {

        StringBuilder webServiceLoginURL = null;
        try {
            webServiceLoginURL = new StringBuilder(WCF_URL).append(USER_MANAGEMENT_SVC)
                    .append(USER_MANAGEMENT_WITH_GCM_SVC_GETTER)
                    .append("?username=").append(URLEncoder.encode(username, "utf-8"))
                    .append("&password=").append(URLEncoder.encode(password, "utf-8"))
                    .append("&gcmID=").append(URLEncoder.encode(gcmId, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return webServiceLoginURL;
    }

    /**
     * This method will produce a Toast message from the given input
     * and post it on screen.
     *
     */

    public void toastMessage(CharSequence message) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void onClickHome(View v){
        Intent i = new Intent(this, HomeActivityRevised.class);
        startActivity(i);
    }

    public void onClickContinuousSync(View v){
        if(Utils.isOnline(this)) {
            SharedPreferences sync_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editPrefs = sync_preferences.edit();
            editPrefs.putBoolean("secondSyncNeeded", true);
            editPrefs.commit();
            Intent i = new Intent(this, HomeActivityRevised.class);
            startActivity(i);
        }else{
            toastMessage("Need internet connection to perform this action!");
        }
    }

    public void onClickFeature(View v) {
        sync_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sync_needed = true;
        if (sync_preferences.contains("synchronization_needed")) {
            sync_needed = sync_preferences.getBoolean("synchronization_needed", false);
        }

        boolean secondSyncNeeded = false;
        if (sync_preferences.contains("secondSyncNeeded") && Utils.isOnline(this)) {
            secondSyncNeeded = sync_preferences.getBoolean("secondSyncNeeded", true);
        }

        if (sync_needed /*|| secondSyncNeeded*/) {
            toastMessage("Device is synchronizing. Please wait ...");
        } else {
            int id = v.getId();
        }

    }

    @Override
    public void onBackPressed() {

        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

    public void onBackPressedInViewChildWhenCameFromSearch() {
        super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

}