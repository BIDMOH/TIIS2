package mobile.tiis.staging.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import mobile.tiis.staging.R;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.Child;
import mobile.tiis.staging.helpers.Utils;

import static mobile.tiis.staging.ChildDetailsActivity.childId;

/**
 * Created by issymac on 26/01/16.
 */
public class ChildWeightPagerFragment extends Fragment {

    private static final String CHILD_OBJECT = "child";

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    private Child currentChild;

    private String today, birthday, mfgender, weight_value;

    private boolean isWeightSetForChild = false;


    DatabaseHandler mydb;

    BackboneApplication app;

    MaterialEditText metDOB, metWeightValue, metWeightDecimalValue;

    TextView title, previousWeightTitle, prevWeightValue, prevWeightDate;

    LinearLayout lnPreviousDateAndWeight;

    HashMap<String,String> dateAndWeight;

    Button saveButton;

    Cursor childWeightCursor = null;

    public static ChildWeightPagerFragment newInstance(Child currentChild) {
        ChildWeightPagerFragment f  = new ChildWeightPagerFragment();
        Bundle b                    = new Bundle();
        b                           .putSerializable(CHILD_OBJECT, currentChild);
        f                           .setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentChild     = (Child) getArguments().getSerializable(CHILD_OBJECT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v;
        v = (ViewGroup) inflater.inflate(R.layout.fragment_child_weight_pager, null);
        setUpView(v);
        setTypeFace();

        app = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        if (currentChild.getBarcodeID() == null || currentChild.getBarcodeID().isEmpty()) {
            Toast.makeText(ChildWeightPagerFragment.this.getActivity(), getString(R.string.empty_barcode), Toast.LENGTH_SHORT).show();
        }

        if (currentChild.getGender() != null) {
            if (currentChild.getGender().equalsIgnoreCase("true")) {
                mfgender = "M";
            } else {
                mfgender = "F";
            }
        }

        if (currentChild.getBirthdate() != null && !currentChild.getBirthdate().isEmpty()) {
            birthday = ft.format(BackboneActivity.dateParser(currentChild.getBirthdate()));
        }

        metDOB.setText(ft.format(dNow));
        today = ft.format(dNow);

        updateChildsWeight();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentChild = mydb.getChildById(childId);

                if (Utils.isStringBlank((metWeightValue.getText().toString())) || metWeightValue.getText().toString().substring(0, 1).equals("0")) {

                    String message = getString(R.string.weight_not_correct);
                    showWarningDialogue(message, "-3");

                } else {
                    if (!isWeightSetForChild) {
                        isWeightSetForChild = true;

                        updateWeight((metWeightValue.getText().toString()) + "." + (metWeightDecimalValue.getText().toString().trim().equals("") ? "00" : metWeightDecimalValue.getText().toString()));

                        if (!Utils.isStringBlank(birthday) && !Utils.isStringBlank((metWeightValue.getText().toString()))) {
                            long difference;
                            String sd3neg, sd2neg, sd3, sd2;

                            try {
                                SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                Date date1 = myFormat.parse(birthday);
                                Date date2 = myFormat.parse(today);
                                long diff = date2.getTime() - date1.getTime();
                                difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                Log.d("", "The diff" + difference);

                                BackboneApplication app = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
                                DatabaseHandler mydb = app.getDatabaseInstance();
                                Cursor cursor = null;

                                cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM weight WHERE DAY=? AND GENDER=?", new String[]{String.valueOf(difference), String.valueOf(mfgender)});
                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    sd3 = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD3));
                                    sd2 = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD2));
                                    sd3neg = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD3NEG));
                                    sd2neg = cursor.getString(cursor.getColumnIndex(SQLHandler.WeightColumns.SD2NEG));

                                    String str = "0";
                                    String message = "";
                                    int flag = 0;

                                    weight_value = metWeightValue.getText().toString() + "." + metWeightDecimalValue.getText().toString();
                                    if (Double.parseDouble(weight_value) <= Double.parseDouble(sd3neg)) {
                                        str = "-3";
                                    } else if (Double.parseDouble(weight_value) <= Double.parseDouble(sd2neg)) {
                                        str = "-2";
                                    } else if (Double.parseDouble(weight_value) >= Double.parseDouble(sd3)) {
                                        str = "3";
                                    } else if (Double.parseDouble(weight_value) >= Double.parseDouble(sd2)) {
                                        str = "2";
                                    } else if (Double.parseDouble(sd2neg) < Double.parseDouble(weight_value) && Double.parseDouble(weight_value) < Double.parseDouble(sd2)) {
                                        str = "OK";
                                    }

                                    showWarningDialogue(message, str);

                                } else {
                                    final AlertDialog.Builder ad = new AlertDialog.Builder(ChildWeightPagerFragment.this.getActivity());
                                    ad.setTitle(getString(R.string.weight_analysis));
                                    ad.setMessage(getString(R.string.weight_analyzer_not_reached));

                                    ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    // this will solve your error
                                    AlertDialog alert = ad.create();
                                    alert.show();
                                    alert.getWindow().getAttributes();

                                    TextView textView = (TextView) alert.findViewById(android.R.id.message);
                                    textView.setTextSize(30);
                                }


                            } catch (ParseException e) {
                                Toast toast = Toast.makeText(ChildWeightPagerFragment.this.getActivity().getApplicationContext(), "Weight Analyser stopped!", Toast.LENGTH_SHORT);
                                toast.show();

                                e.printStackTrace();
                            }

                        }


                        String dateToday = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());

                        BackboneApplication app = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
                        new Thread() {
                            String threadDateToday
                                    ,
                                    threadDateModON
                                    ,
                                    threadbarcode
                                    ,
                                    threadWeight
                                    , threadModBy;

                            public Thread setData(String threadbarcode, String threadDateToday, String threadDateModON, String threadWeight, String threadModBy) {
                                try {
                                    this.threadDateToday = URLEncoder.encode(threadDateToday, "utf-8");
                                    this.threadDateModON = URLEncoder.encode(threadDateModON, "utf-8");
                                    this.threadbarcode = threadbarcode;
                                    this.threadModBy = threadModBy;
                                    this.threadWeight = threadWeight;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                return this;
                            }

                            @Override
                            public void run() {


                                synchronized (this) {


                                    BackboneApplication backbone = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
                                    backbone.saveWeight(threadbarcode,
                                            threadDateToday, threadDateModON, threadWeight,
                                            threadModBy);
                                    //Register Audit
                                    backbone.registerAudit(BackboneApplication.CHILD_AUDIT, threadbarcode, threadDateModON,
                                            backbone.getLOGGED_IN_USER_ID(), 6);

                                }

                            }
                        }.setData(currentChild.getBarcodeID()
                                , dateToday
                                , dateTodayTimestamp
                                , metWeightValue.getText().toString() + "." + (metWeightDecimalValue.getText().toString().trim().equals("") ? "00" : metWeightDecimalValue.getText().toString())
                                , app.getLOGGED_IN_USER_ID()).start();

                        final DatabaseHandler mydb = app.getDatabaseInstance();
                        String childID = mydb.getChildIdByBarcode(currentChild.getBarcodeID());

                        if (childID != null) {
                            if (mydb.isChildToBeAddedInVaccinationQueue(childID)) {
                                ContentValues cv = new ContentValues();
                                cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childID);
                                cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateTodayTimestamp);

                                if (mydb.addChildToVaccinationQueue(cv) > -1) {
                                    // hfid should never be null since a child allways has to have a hfid
                                    String hfid = mydb.getChildHFIDByChildId(childID + "");

                                    new Thread() {
                                        String thredHfid
                                                ,
                                                threadTodayTimestamp;

                                        public Thread setDataToServer(String thredHfid, String threadTodayTimestamp) {
                                            try {
                                                this.threadTodayTimestamp = URLEncoder.encode(threadTodayTimestamp, "utf-8");
                                                this.thredHfid = thredHfid;
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            return this;
                                        }
                                        @Override
                                        public void run() {
                                            synchronized (this) {
                                                BackboneApplication app = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
                                                app.updateVaccinationQueue(currentChild.getBarcodeID(), app.getLOGGED_IN_USER_HF_ID(), threadTodayTimestamp, app.getLOGGED_IN_USER_ID());
                                            }

                                        }
                                    }.setDataToServer(hfid, dateTodayTimestamp).start();

                                }

                            }
                        }

                    }else{
                        showAlertThatChildHasWeightInDB();
                    }
                }
            }
        });

        return v;
    }

    public void updateChildsWeight(){
        Log.e("delay","updating weight called");

        if(currentChild.getBarcodeID().isEmpty()) {
            currentChild = mydb.getChildById(childId);
            prevWeightValue.setText("");
            metWeightValue.setText("");
            metWeightDecimalValue.setText("");
            isWeightSetForChild = false;
            if (dateAndWeight != null) {
                SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Date date = new Date(Long.parseLong(dateAndWeight.get("Date")) * 1000);
                String previousDate = myFormat.format(date);
                prevWeightDate.setText(previousDate);
                prevWeightValue.setText(dateAndWeight.get("Weight"));
            } else {
                lnPreviousDateAndWeight.setVisibility(View.GONE);
            }
        }

    }

    public void showWarningDialogue(String message, String str){
        android.support.v7.app.AlertDialog.Builder keyBuilder = new android.support.v7.app.AlertDialog.Builder(ChildWeightPagerFragment.this.getActivity());
        keyBuilder
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        View dialogLayout = View.inflate(ChildWeightPagerFragment.this.getActivity(), R.layout.child_weight_warning_dialogue, null);
        LinearLayout topBar = (LinearLayout) dialogLayout.findViewById(R.id.dialogue_top_bar);
        TextView warningMessage = (TextView) dialogLayout.findViewById(R.id.warning_message);
        warningMessage.setTypeface(BackboneActivity.Rosario_Regular);
        TextView title          = (TextView) dialogLayout.findViewById(R.id.dialogue_title);
        String titleText = "";

        switch (str) {
            case "-3":
                message = (getString(R.string.child_significantly_underweight));
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.red_500));
                break;
            case "-2":
                message = (getString(R.string.child_weight_too_low));
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.yellow_500));
                break;
            case "3":
                message = (getString(R.string.child_weight_too_hight));
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.red_500));
                break;
            case "2":
                message = (getString(R.string.child_weight_too_hight));
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.red_300));
                break;
            case "OK":
                message = (getString(R.string.normal_child_weight));
                titleText = "MESSAGE";
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.green_400));
                break;
            case "DONE":
                titleText = "MESSAGE";
                topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.green_400));
                break;
            default:
                message = (getString(R.string.error_ocurred));
                break;
        }
        title.setText(titleText);
        warningMessage.setText(message);
        keyBuilder.setView(dialogLayout);

        android.support.v7.app.AlertDialog dialog = keyBuilder.create();
        dialog.show();
    }

    public void setUpView(View v){
        metDOB              = (MaterialEditText) v.findViewById(R.id.met_date_value);
        title               = (TextView) v.findViewById(R.id.title);
        previousWeightTitle = (TextView) v.findViewById(R.id.previous_weight_title);
        prevWeightValue     = (TextView) v.findViewById(R.id.prev_weight_value);
        prevWeightDate      = (TextView) v.findViewById(R.id.prev_weight_date);
        metWeightValue      = (MaterialEditText) v.findViewById(R.id.met_weight_value);
        metWeightDecimalValue       = (MaterialEditText) v.findViewById(R.id.met_weight_decimal_value);
        lnPreviousDateAndWeight     = (LinearLayout) v.findViewById(R.id.lin_prev_date_and_weight);

        saveButton          = (Button) v.findViewById(R.id.weight_save_btn);

    }

    public void setTypeFace(){
        title.setTypeface(BackboneActivity.Roboto_Regular);
        previousWeightTitle.setTypeface(BackboneActivity.Rosario_Regular);
    }

    public Child getChildFromCursror(Cursor cursor) {
        Child parsedChild = new Child();
        parsedChild.setId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.ID)));
        parsedChild.setBarcodeID(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BARCODE_ID)));
        parsedChild.setTempId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.TEMP_ID)));
        parsedChild.setFirstname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1)));
        parsedChild.setFirstname2(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2)));
        parsedChild.setLastname1(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1)));
        parsedChild.setBirthdate(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHDATE)));
        parsedChild.setMotherFirstname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_FIRSTNAME)));
        parsedChild.setMotherLastname(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_LASTNAME)));
        parsedChild.setPhone(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.PHONE)));
        parsedChild.setNotes(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.NOTES)));
        parsedChild.setBirthplaceId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHPLACE_ID)));
        parsedChild.setGender(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.GENDER)));
        Cursor cursor1 = mydb.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }

        parsedChild.setDomicile(villagestr);
        parsedChild.setHealthcenterId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
        try {
            Cursor cursor3 = mydb.getReadableDatabase().rawQuery("SELECT * FROM health_facility WHERE ID=?", new String[]{parsedChild.getHealthcenterId()});
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                hfstr = cursor3.getString(cursor3.getColumnIndex(SQLHandler.HealthFacilityColumns.NAME));
            }
        }catch (Exception e){
            hfstr = "";
        }
        parsedChild.setHealthcenter(hfstr);

        parsedChild.setStatusId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.STATUS_ID)));
        Cursor cursor4 = mydb.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

    public void updateWeight(String weight) {
        boolean isweightsaved = false;
        Log.d("day6", "Weight Of a child is : "+weight);

        BackboneApplication app = (BackboneApplication) ChildWeightPagerFragment.this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        ContentValues child = new ContentValues();
        child.put(SQLHandler.SyncColumns.UPDATED, 1);
        child.put(SQLHandler.ChildWeightColumns.WEIGHT, weight);
        child.put(SQLHandler.ChildWeightColumns.DATE,Calendar.getInstance().getTimeInMillis()/1000);
        child.put(SQLHandler.ChildWeightColumns.CHILD_BARCODE, currentChild.getBarcodeID());
        Cursor cursor = null;

        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child_weight WHERE CHILD_BARCODE=? ", new String[]{String.valueOf(currentChild.getBarcodeID())});
        if (cursor.getCount() > 0) {
            isweightsaved = true;
            String message = getString(R.string.weight_updated);
            showWarningDialogue(message, "DONE");
        } else {
            isweightsaved = true;
            mydb.addChildWeight(child);
            String message  = getString(R.string.weight_registered);
            showWarningDialogue(message, "DONE");
        }

        lnPreviousDateAndWeight.setVisibility(View.VISIBLE);
        SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        prevWeightValue.setText(weight);
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        prevWeightDate.setText(ft.format(now));


    }

    private void showAlertThatChildHasWeightInDB() {

        android.support.v7.app.AlertDialog.Builder keyBuilder = new android.support.v7.app.AlertDialog.Builder(ChildWeightPagerFragment.this.getActivity());
        keyBuilder
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isWeightSetForChild = false;
                        saveButton.callOnClick();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        View dialogLayout = View.inflate(ChildWeightPagerFragment.this.getActivity(), R.layout.child_weight_warning_dialogue, null);
        LinearLayout topBar = (LinearLayout) dialogLayout.findViewById(R.id.dialogue_top_bar);
        topBar.setBackgroundColor(ChildWeightPagerFragment.this.getActivity().getResources().getColor(R.color.red_500));
        TextView warningMessage = (TextView) dialogLayout.findViewById(R.id.warning_message);
        TextView title          = (TextView) dialogLayout.findViewById(R.id.dialogue_title);
        String message = getString(R.string.child_weight_already_entered);
        warningMessage.setText(message);
        keyBuilder.setView(dialogLayout);

        android.support.v7.app.AlertDialog dialog = keyBuilder.create();
        dialog.show();

    }



}
