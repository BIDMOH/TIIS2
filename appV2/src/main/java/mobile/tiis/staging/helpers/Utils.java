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

package mobile.tiis.staging.helpers;

/**
 * Created by Teodor on 1/31/2015.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import mobile.tiis.staging.base.BackboneApplication;

public final class Utils {

    public final static String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
    public final static String PARAM_DATE_PATTERN = "yyyy-MM-dd";
    public final static String HIDDEN_DATE_STRING = "01/01/0001";

    private static Pattern nationalIdPattern = Pattern.compile("([A-Z])(\\d{8})([A-Z])");


    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        try {
            return cm.getActiveNetworkInfo() != null &&
                    cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }catch (Exception e){
            return false;
        }

    }



    public static void setSpinnerSelection(Spinner spinner, String value) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner
                .getAdapter();
        int spinnerPosition = adapter.getPosition(value);
        spinner.setSelection(spinnerPosition);
    }

    public static DatePickerDialog.OnDateSetListener defaultOnDateSetListener(final EditText editText) {
        return new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        DEFAULT_DATE_PATTERN);
                editText.setText(dateFormat.format(c.getTime()));
            }
        };
    }

    // convert InputStream to String
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    // convert InputStream to String
    public static String getStringFromInputStreamAndLeaveStreamOpen(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br = null;
        }

        return sb.toString();

    }

    public static ByteArrayInputStream getMultiReadInputStream(InputStream is){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(is, baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    public static boolean isStringBlank(CharSequence str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isStringNotBlank(CharSequence str) {
        return !isStringBlank(str);
    }

    public static void appendParameter(StringBuilder sb, String key, String value) {
        if (isStringNotBlank(key) && isStringNotBlank(value)) {
            if (isStringNotBlank(sb)) {
                sb.append("&");
            }
            sb.append(key).append("=");
            try {
                sb.append(URLEncoder.encode(value.trim(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String convertToParamDateFormat(String dateStr) {
        if (isStringNotBlank(dateStr)) {
            SimpleDateFormat fromDateFormat = new SimpleDateFormat(
                    DEFAULT_DATE_PATTERN);
            SimpleDateFormat toDateFormat = new SimpleDateFormat(
                    PARAM_DATE_PATTERN);
            try {
                Date date = fromDateFormat.parse(dateStr);
                return toDateFormat.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static String convertToParamDate(String dateStr) {
        if (isStringNotBlank(dateStr)) {
            SimpleDateFormat fromDateFormat = new SimpleDateFormat(
                    PARAM_DATE_PATTERN);
            SimpleDateFormat toDateFormat = new SimpleDateFormat(
                    DEFAULT_DATE_PATTERN);
            try {
                Date date = fromDateFormat.parse(dateStr);
                return toDateFormat.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static boolean validateNationalID(String nationalID) {
        return nationalIdPattern.matcher(nationalID).matches();
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }



    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date addMonths(Date date, int mmonths)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, mmonths);
        return cal.getTime();
    }

    public static Date addYears(Date date, int years)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    public static void writeNetworkLogFileOnSD(String sBody){
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "TIIS");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "network_log_file.txt");

            PrintWriter fileStream = new PrintWriter(new FileOutputStream(gpxfile,true));
            fileStream.append(System.getProperty("line.separator"));
            fileStream.append(sBody);
            fileStream.flush();
            fileStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String returnDeviceIdAndTimestamp(Context ctx){
        String deviceIdandTimestamp;
        String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
        deviceIdandTimestamp = BackboneApplication.getDeviceId(ctx)+ " "+ dateTodayTimestamp + " ";
        return deviceIdandTimestamp;
    }

}
