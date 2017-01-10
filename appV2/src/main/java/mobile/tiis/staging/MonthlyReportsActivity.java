package mobile.tiis.staging;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.adapters.AefiBottomListAdapter;
import mobile.tiis.staging.adapters.AefiTopListAdapter;
import mobile.tiis.staging.adapters.PlacesOfBirthAdapter;
import mobile.tiis.staging.adapters.SpinnerAdapter;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.GIISContract;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.MonthEntity;
import mobile.tiis.staging.fragments.ChildAefiPagerFragment;
import mobile.tiis.staging.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

import java.util.Calendar;

import static mobile.tiis.staging.util.Constants.ADS_O5ML;
import static mobile.tiis.staging.util.Constants.ADS_OO5ML;
import static mobile.tiis.staging.util.Constants.SAFETY_BOXES;
import static mobile.tiis.staging.util.Constants.SDILLUTION;
import static mobile.tiis.staging.util.Constants.VITAMIN_A_100000_IU;
import static mobile.tiis.staging.util.Constants.VITAMIN_A_200000_IU;

public class MonthlyReportsActivity extends RxAppCompatActivity implements View.OnClickListener {
    private static final String TAG = MonthlyReportsActivity.class.getSimpleName();

    //UI ELEMENTS
    private Button suveillanceSubmit, refrigeratorSubmit, immunizationButton, vaccinationsButton, otherMajorImmunizationActivitiesButton, syringesSubmitButton, vitaminASubmitButton;
    private TextView feverCases, feverDeaths, afpCases, afpDeaths, tetanusCases, tetanusDeaths,tempMax, tempMin, alarmHigh, alarmLow, lastMonthTitle, surveillanceTitle, refrigderatorTitle;
    private MaterialSpinner monthYearSpinner, yearSpinner;
    private TextView toolbarTitle;
    private EditText fixedConducted, outreachPlanned, outreachConducted, outreachCancelled;
    private EditText bcgFemaleServiceArea, bcgMaleServiceArea, bcgFemaleCatchmentArea, bcgMaleCatchmentArea, opvFemaleServiceArea, opvMaleServiceArea, opvFemaleCatchmentArea, opvMaleCatchmentArea;
    private EditText ttFemaleServiceArea, ttMaleServiceArea, ttFemaleCatchmentArea, ttMaleCatchmentArea;
    private EditText tt1FemaleServiceArea, tt1MaleServiceArea, tt1FemaleCatchmentArea, tt1MaleCatchmentArea;
    private EditText tt2FemaleServiceArea, tt2MaleServiceArea, tt2FemaleCatchmentArea, tt2MaleCatchmentArea;
    private EditText tt3FemaleServiceArea, tt3MaleServiceArea, tt3FemaleCatchmentArea, tt3MaleCatchmentArea;
    private EditText tt4FemaleServiceArea, tt4MaleServiceArea, tt4FemaleCatchmentArea, tt4MaleCatchmentArea;
    private EditText tt5FemaleServiceArea, tt5MaleServiceArea, tt5FemaleCatchmentArea, tt5MaleCatchmentArea;
    private EditText tt5CompletedFemaleServiceArea, tt5CompletedMaleServiceArea, tt5CompletedFemaleCatchmentArea, tt5CompletedMaleCatchmentArea;


    private EditText otherMajorImmunizationActivities;
    private EditText ml005Balance, ml005Received, ml005Used, ml005Wastage, ml005StockInHand, ml005StockedOutDays;
    private EditText ads05Balance, ads05Received, ads05Used, ads05Wastage, ads05StockInHand, ads05StockedOutDays;
    private EditText dillutionBalance, dillutionReceived, dillutionUsed, dillutionWastage, dillutionStockInHand, dillutionStockedOutDays;
    private EditText safetyBoxBalance, safetyBoxReceived, safetyBoxUsed, safetyBoxWastage, safetyBoxStockInHand, safetyBoxStockedOutDays;
    private EditText vitA1Opening, vitA1Received, vitA1Administered, vitA1Wastage, vitA1StockInHand;
    private EditText vitA2Opening, vitA2Received, vitA2Administered, vitA2Wastage, vitA2StockInHand;

    //Variables
    private String strFeverCases, strFeverDeaths, strAfpCases, strAfpDeaths, strTetanusCases, strTetanusDeaths;
    private String strTempMax, strTempMin, strAlarmHigh, strAlarmLow;
    private String strFixedConducted, strOutreachConducted, strOutreachPlanned, strOutreachCancelled;
    private String strBcgFemaleService, strBcgMaleService, strBcgFemaleCatchment, strBcgMaleCatchment, strOpvFemaleService, strOpvMaleService, strOpvFemaleCatchment, strOpvMaleCatchment;
    private String strTt1FemaleService, strTt1MaleServicec, strTt1FemaleCatchment, strTt1MaleCatchment;
    private String strTt2FemaleService, strTt2MaleServicec, strTt2FemaleCatchment, strTt2MaleCatchment;
    private String strTt3FemaleService, strTt3MaleServicec, strTt3FemaleCatchment, strTt3MaleCatchment;
    private String strTt4FemaleService, strTt4MaleServicec, strTt4FemaleCatchment, strTt4MaleCatchment;
    private String strTt5FemaleService, strTt5MaleServicec, strTt5FemaleCatchment, strTt5MaleCatchment;
    private String strTt5CompletedFemaleService, strTt5CompletedMaleServicec, strTt5CompletedFemaleCatchment, strTt5CompletedMaleCatchment;

    private String strOtherMajorImmunizationActivities;


    private String strml005Balance, strml005Received, strml005Used, strml005Wastage, strml005StockInHand, strml005StockedOutDays;
    private String strads05Balance, strads05Received, strads05Used, strads05Wastage, strads05StockInHand, strads05StockedOutDays;
    private String strDillutionBalance, strDillutionReceived, strDillutionUsed, strDillutionWastage, strDillutionStockInHand, strDillutionStockedOutDays;
    private String strSafetyBoxBalance, strSafetyBoxReceived, strSafetyBoxUsed, strSafetyBoxWastage, strSafetyBoxStockInHand, strSafetyBoxStockedOutDays;
    private String strVitA1Opening, strVitA1Received, strVitA1Administered, strVitA1Wastage, strVitA1StockInHand;
    private String strVitA2Opening, strVitA2Received, strVitA2Administered, strVitA2Wastage, strVitA2StockInHand;

    private List<String> monthYear  = new ArrayList<>();
    private List<String> years      = new ArrayList<>();

    private List<MonthEntity> monthEntities = new ArrayList<>();

    private DatabaseHandler mydb;
    private BackboneApplication app;
    private SQLiteDatabase db;

    private MonthEntity currentSelectedMonth;
    private String currentlySelectedYear;

    public Dialog dialog;
    public TextView dialogueMessage, dialogueOKButton;
    public boolean fieldsEditable = true;
    private boolean databaseisfree = true;
    public ProgressBar pbar;
    public LinearLayout sessionsLayouts;
    private Looper backgroundLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_reports);
        setupview();

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("IVD Monthly Report Forms");

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogueView   = LayoutInflater.from(this).inflate(R.layout.monthly_report_dialogue, null);
        dialog.setContentView(dialogueView);
        dialogueMessage     = (TextView) dialogueView.findViewById(R.id.mesage);
        dialogueMessage.setTypeface(HomeActivityRevised.Roboto_Light);
        dialogueOKButton    = (TextView) dialogueView.findViewById(R.id.tv_ok);
        dialogueOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        app = (BackboneApplication) this.getApplication();
        mydb = app.getDatabaseInstance();



        Calendar cal = Calendar.getInstance();

        int month   = cal.get(Calendar.MONTH)+1;
        int year    = cal.get(Calendar.YEAR);

        years.add("2015");
        years.add("2016");
        years.add("2017");
        years.add("2018");
        years.add("2019");
        years.add("2020");

        monthEntities.add(new MonthEntity("January", "1"));
        monthEntities.add(new MonthEntity("February", "2"));
        monthEntities.add(new MonthEntity("March", "3"));
        monthEntities.add(new MonthEntity("April", "4"));
        monthEntities.add(new MonthEntity("May", "5"));
        monthEntities.add(new MonthEntity("June", "6"));
        monthEntities.add(new MonthEntity("July", "7"));
        monthEntities.add(new MonthEntity("August", "8"));
        monthEntities.add(new MonthEntity("September", "9"));
        monthEntities.add(new MonthEntity("October", "10"));
        monthEntities.add(new MonthEntity("November", "11"));
        monthEntities.add(new MonthEntity("December", "12"));

        PlacesOfBirthAdapter adapter = new PlacesOfBirthAdapter(this, R.layout.single_text_spinner_dropdown_toolbar,years);
        yearSpinner.setAdapter(adapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentlySelectedYear = years.get(position);

                if (databaseisfree){
                    databaseisfree = false;
                    checkdatabaseForAlreadyReportedFormsForThisMonth();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i=0; i<years.size(); i++){
            if (years.get(i).equals(year+"")){
                yearSpinner.setSelection(i+1);
                currentlySelectedYear = years.get(i);
            }
        }


        SpinnerAdapter spinnerAdapter   = new SpinnerAdapter(this, monthEntities);
        monthYearSpinner.setAdapter(spinnerAdapter);
        clearFields();

        for (int i =0; i<monthEntities.size(); i++){
            if (Integer.parseInt(monthEntities.get(i).getMonth_number()) == month){
                Log.d("monthlyReport",i+"");
                monthYearSpinner.setSelection((i+1));
                currentSelectedMonth = monthEntities.get(i);
            }
        }

        monthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0){
                    currentSelectedMonth = monthEntities.get(i);
                    clearFields();
                    Date today = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(today);

                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    int month   = calendar.get(Calendar.MONTH);
                    month = month+1;

                    int selmonth = Integer.parseInt(currentSelectedMonth.getMonth_number());

                    Log.d("THURSDAY_TOUCHUPS", "Selected month is "+selmonth);
                    Log.d("THURSDAY_TOUCHUPS", "This month is "+month);

                    if ((month-selmonth)==1 && dayOfMonth>10){
                        setFieldsAccessibility(false);
                        fieldsEditable = false;
                    }else if ((month-selmonth)==1 && dayOfMonth<10){
                        setFieldsAccessibility(true);
                        fieldsEditable = true;
                    }else if ((month-selmonth)==0){
                        setFieldsAccessibility(true);
                        fieldsEditable = true;
                    }else {
                        setFieldsAccessibility(false);
                        fieldsEditable = false;
                    }

                    if (databaseisfree){
                        databaseisfree = false;
                        checkdatabaseForAlreadyReportedFormsForThisMonth();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int monthVal   = calendar.get(Calendar.MONTH);
        monthVal = monthVal+1;

        int selectedmonth = Integer.parseInt(currentSelectedMonth.getMonth_number());


        if ((monthVal-selectedmonth)==1 && dayOfMonth>10){
            Log.d(TAG,"monthVal-selectedmonth)==1 && dayOfMonth>10");
            setFieldsAccessibility(false);
            fieldsEditable = false;
        }else if((monthVal-selectedmonth)==0 && dayOfMonth<=10) {
            Log.d(TAG,"(monthVal-selectedmonth)==1 && dayOfMonth<=10");

            if((monthVal-1)==0){
                monthYearSpinner.setSelection(12);
                yearSpinner.setSelection(years.indexOf(currentlySelectedYear));
                setFieldsAccessibility(false);
            }else {
                setFieldsAccessibility(true);
                monthYearSpinner.setSelection(monthVal - 1);
            }
            fieldsEditable = true;
        }else if ((monthVal-selectedmonth)>1){
            Log.d(TAG,"(monthVal-selectedmonth)>1");
            setFieldsAccessibility(false);
            fieldsEditable = false;
        }else {
            Log.d(TAG,"else");
            setFieldsAccessibility(true);
            fieldsEditable = true;
        }

        if (databaseisfree){
            databaseisfree = false;
            checkdatabaseForAlreadyReportedFormsForThisMonth();
        }

    }


    private void checkdatabaseForAlreadyReportedFormsForThisMonth(){
        db = mydb.getReadableDatabase();
        pbar.setVisibility(View.VISIBLE);
        sessionsLayouts.setVisibility(View.INVISIBLE);
        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                //IMMUNiZATION SESSIONS
                fixedConducted.setText("");
                outreachConducted.setText("");
                outreachPlanned.setText("");
                outreachCancelled.setText("");
                otherMajorImmunizationActivities.setText("");

                querySurveillanceInformation( );
                queryRefrigeratorTemperature( );
                queryVaccinationsBcgOpvTt( );
                querySafeInjectionEquipments( );
                queryVitaminAStock( );
                queryImmunizationSessions( );
                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        pbar.setVisibility(View.GONE);
                        sessionsLayouts.setVisibility(View.VISIBLE);

                        //dESEASE SURVEILANCE
                        feverCases.setText(strFeverCases);
                        feverDeaths.setText(strFeverDeaths);
                        afpCases.setText(strAfpCases);
                        afpDeaths.setText(strAfpDeaths);
                        tetanusCases.setText(strTetanusCases);
                        tetanusDeaths.setText(strTetanusDeaths);

                        //COLD CHAIN
                        tempMax.setText(strTempMax);
                        tempMin.setText(strTempMin);
                        alarmHigh.setText(strAlarmHigh);
                        alarmLow.setText(strAlarmLow);

                        //IMMUNiZATION SESSIONS
                        fixedConducted.setText(strFixedConducted);
                        outreachConducted.setText(strOutreachConducted);
                        outreachPlanned.setText(strOutreachPlanned);
                        outreachCancelled.setText(strOutreachCancelled);
                        otherMajorImmunizationActivities.setText(strOtherMajorImmunizationActivities);

                        //VACCINATIONS
                        bcgFemaleServiceArea.setText(strBcgFemaleService);
                        bcgMaleServiceArea.setText(strBcgMaleService);
                        bcgFemaleCatchmentArea.setText(strBcgFemaleCatchment);
                        bcgMaleCatchmentArea.setText(strBcgMaleCatchment);
                        opvFemaleServiceArea.setText(strOpvFemaleService);
                        opvMaleServiceArea.setText(strOpvMaleService);
                        opvFemaleCatchmentArea.setText(strOpvFemaleCatchment);
                        opvMaleCatchmentArea.setText(strOpvMaleCatchment);

                        //SAFE INJECTIONS
                        ml005Balance.setText(strml005Balance);
                        ml005Received.setText(strml005Received);
                        ml005Used.setText(strml005Used);
                        ml005Wastage.setText(strml005Wastage);
                        ml005StockInHand.setText(strml005StockInHand);
                        ml005StockedOutDays.setText(strml005StockedOutDays);
                        ads05Balance.setText(strads05Balance);
                        ads05Received.setText(strads05Received);
                        ads05Used.setText(strads05Used);
                        ads05Wastage.setText(strads05Wastage);
                        ads05StockInHand.setText(strads05StockInHand);
                        ads05StockedOutDays.setText(strads05StockedOutDays);
                        dillutionBalance.setText(strDillutionBalance);
                        dillutionReceived.setText(strDillutionReceived);
                        dillutionUsed.setText(strDillutionUsed);
                        dillutionWastage.setText(strDillutionWastage);
                        dillutionStockInHand.setText(strDillutionStockInHand);
                        dillutionStockedOutDays.setText(strDillutionStockedOutDays);
                        safetyBoxBalance.setText(strSafetyBoxBalance);
                        safetyBoxReceived.setText(strSafetyBoxReceived);
                        safetyBoxUsed.setText(strSafetyBoxUsed);
                        safetyBoxWastage.setText(strSafetyBoxWastage);
                        safetyBoxStockInHand.setText(strSafetyBoxStockInHand);
                        safetyBoxStockedOutDays.setText(strSafetyBoxStockedOutDays);

                        //VITAMINS
                        vitA1Opening.setText(strVitA1Opening);
                        vitA1Received.setText(strVitA1Received);
                        vitA1Administered.setText(strVitA1Administered);
                        vitA1Wastage.setText(strVitA1Wastage);
                        vitA1StockInHand.setText(strVitA1StockInHand);
                        vitA2Opening.setText(strVitA2Opening);
                        vitA2Received.setText(strVitA2Received);
                        vitA2Administered.setText(strVitA2Administered);
                        vitA2Wastage.setText(strVitA2Wastage);
                        vitA2StockInHand.setText(strVitA2StockInHand);

                        pbar.setVisibility(View.GONE);
                        databaseisfree = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(Boolean string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                });

    }

    public void queryRefrigeratorTemperature(){

        String query = "SELECT * FROM "+ SQLHandler.Tables.REFRIGERATOR_TEMPERATURE
                +" WHERE "+ SQLHandler.RefrigeratorColums.REPORTED_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            strTempMax = (cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.TEMP_MAX)));
            strTempMin = (cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.TEMP_MIN)));
            strAlarmLow = (cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.ALARM_LOW_TEMP)));
            strAlarmHigh = (cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.ALARM_HIGH_TEMP)));
        }
    }

    public void querySurveillanceInformation(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.DESEASES_SURVEILLANCE
                +" WHERE "+ SQLHandler.SurveillanceColumns.REPORTED_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            strFeverCases = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.FEVER_MONTHLY_CASES)));
            strFeverDeaths = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.FEVER_DEATHS)));
            strAfpCases = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.APF_MONTHLY_CASES)));
            strAfpDeaths = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.APF_DEATHS)));
            strTetanusCases = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.NEONATAL_TT_CASES)));
            strTetanusDeaths = (cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.NEONATAL_TT_DEATHS)));
        }
    }

    public void queryImmunizationSessions(){

        long fromDate = 0;
        long toDate   = 0;

        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar;
        Date lastmonth;



        if(Integer.parseInt(currentSelectedMonth.getMonth_number())+1>12){
            calendar = Calendar.getInstance(); // this would default to now
            calendar.set(Integer.parseInt(currentlySelectedYear),Integer.parseInt(currentSelectedMonth.getMonth_number())-1,1);
            fromDate = calendar.getTimeInMillis()/1000;

            calendar.set(Integer.parseInt(currentlySelectedYear),0,1);
            toDate = calendar.getTimeInMillis()/1000;

            Log.d(TAG,"from date = "+fromDate);
            Log.d(TAG,"to date = "+toDate);
        }else{
            calendar = Calendar.getInstance(); // this would default to now
            calendar.set(Integer.parseInt(currentlySelectedYear)-1,Integer.parseInt(currentSelectedMonth.getMonth_number())-1,1);
            fromDate = calendar.getTimeInMillis()/1000;


            calendar.set(Integer.parseInt(currentlySelectedYear)-1,Integer.parseInt(currentSelectedMonth.getMonth_number()),1);
            toDate = calendar.getTimeInMillis()/1000;

            Log.d(TAG,"from date = "+fromDate);
            Log.d(TAG,"to date = "+toDate);
        }


        String modifiedAtQuery = "SELECT "+GIISContract.SyncColumns.MODIFIED_AT+" FROM "+ SQLHandler.Tables.IMMUNIZATION_SESSION
                +" WHERE "+ SQLHandler.ImmunizationSessionColumns.REPORTING_MONTH+" = '"+mydb.getMonthNameFromNumber((Integer.parseInt(currentSelectedMonth.getMonth_number())-1)+"", app)+" "+currentlySelectedYear+"'";

        SQLiteDatabase dbX = mydb.getReadableDatabase();
        Cursor modifiedAtCursor = dbX.rawQuery(modifiedAtQuery, null);


        SQLiteDatabase dbY = mydb.getReadableDatabase();
        String query = "SELECT * FROM "+ SQLHandler.Tables.IMMUNIZATION_SESSION
                +" WHERE "+ SQLHandler.ImmunizationSessionColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = dbY.rawQuery(query, null);


        if (cursor.moveToFirst()){
            strFixedConducted = (cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.FIXED_CONDUCTED)));
            strOutreachPlanned = (cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_PLANNED)));
            strOutreachConducted = (cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_CONDUCTED)));
            strOutreachCancelled = (cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_CANCELLED)));
            strOtherMajorImmunizationActivities = (cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OTHERACTIVITIES)));
        }else{
            String SQLCountOutReach = "SELECT COUNT (DISTINCT strftime('%d', datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch') )) AS IDS FROM vaccination_appointment as va " +
                    "INNER JOIN " +
                    "   vaccination_event as ve on va.ID = ve.APPOINTMENT_ID " +
                    "WHERE ve.APPOINTMENT_ID = va.ID " +
                    "   AND ve.HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' " +
                    "   AND ve.VACCINATION_STATUS = 'true'" +
                    "   AND va.OUTREACH = 'true'" +
                    "   AND datetime('"+fromDate+"','unixepoch') <= datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch') " +
                    "   AND datetime('"+toDate+"','unixepoch') >= datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch') ";


            String SQLCountFixed = "SELECT COUNT (DISTINCT strftime('%d', datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch') )) AS IDS FROM vaccination_appointment as va " +
                    "INNER JOIN " +
                    "   vaccination_event as ve on va.ID = ve.APPOINTMENT_ID " +
                    "WHERE ve.APPOINTMENT_ID = va.ID " +
                    "   AND ve.HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' " +
                    "   AND ve.VACCINATION_STATUS = 'true'" +
                    "   AND va.OUTREACH = 'false' " +
                    "   AND datetime('"+fromDate+"','unixepoch') <= datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch') " +
                    "   AND datetime('"+toDate+"','unixepoch') >= datetime(substr(ve.VACCINATION_DATE,7,10), 'unixepoch')";

            SQLiteDatabase dbZ = mydb.getReadableDatabase();
            Cursor outreachCursor = dbZ.rawQuery(SQLCountOutReach, null);
            if (outreachCursor.moveToFirst()){
                int val = outreachCursor.getInt(outreachCursor.getColumnIndex("IDS"));
                strOutreachConducted = (val+"");
            }

            Cursor fixedCursor   = dbZ.rawQuery(SQLCountFixed, null);
            if (fixedCursor.moveToFirst()){
                int val = fixedCursor.getInt(fixedCursor.getColumnIndex("IDS"));
                strFixedConducted = (val+"");
            }
        }

    }

    public void queryVaccinationsBcgOpvTt(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.VACCINATIONS_BCG_OPV_TT
                +" WHERE "+ SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("61")){
                    strBcgMaleCatchment = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    strBcgFemaleCatchment = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    strBcgMaleService = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    strBcgFemaleService = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("62")){
                    strOpvMaleCatchment = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    strOpvFemaleCatchment = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    strOpvMaleService = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    strOpvFemaleService = (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("79")){
                    Log.d("SOMA", "TT1 found");
                    tt1MaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt1FemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt1MaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt1FemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));

                } else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("80")){
                    Log.d("SOMA", "TT1 found");
                    tt2MaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt2FemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt2MaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt2FemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));

                } else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("81")){
                    Log.d("SOMA", "TT1 found");
                    tt3MaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt3FemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt3MaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt3FemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));

                } else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("82")){
                    Log.d("SOMA", "TT1 found");
                    tt4MaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt4FemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt4MaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt4FemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));

                } else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("83")){
                    Log.d("SOMA", "TT1 found");
                    tt5MaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt5FemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt5MaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt5FemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));

                } else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID)).equals("84")){
                    Log.d("SOMA", "TT1 found");
                    tt5CompletedMaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    tt5CompletedFemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    tt5CompletedMaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    tt5CompletedFemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
                }

            }while (cursor.moveToNext());
        }
    }

    public void queryOtherImmunizationActivities(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.MAJOR_IMMUNIZATION_ACTIVITIES
                +" WHERE "+ SQLHandler.OtherMajorImmunizationActivitiesColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.moveToFirst()){
            otherMajorImmunizationActivities.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.OtherMajorImmunizationActivitiesColumns.OTHER_ACTIVITIES)));
            otherMajorImmunizationActivities.setEnabled(false);
        }
    }

    public void querySafeInjectionEquipments(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.SYRINGES_AND_SAFETY_BOXES
                +" WHERE "+ SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(ADS_OO5ML)){
                    strml005Balance = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    strml005Received = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    strml005Used = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    strml005Wastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    strml005StockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    strml005StockedOutDays = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(ADS_O5ML)){
                    strads05Balance = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    strads05Received = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    strads05Used = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    strads05Wastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    strads05StockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    strads05StockedOutDays = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(SDILLUTION)){
                    strDillutionBalance = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    strDillutionReceived = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    strDillutionUsed = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    strDillutionWastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    strDillutionStockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    strDillutionStockedOutDays = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(SAFETY_BOXES)){
                    strSafetyBoxBalance = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    strSafetyBoxReceived = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    strSafetyBoxUsed = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    strSafetyBoxWastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    strSafetyBoxStockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    strSafetyBoxStockedOutDays = (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));

                }

            }while (cursor.moveToNext());

        }
    }

    public void queryVitaminAStock(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.HF_VITAMIN_A
                +" WHERE "+ SQLHandler.HfVitaminAColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.VITAMIN_NAME)).equals(VITAMIN_A_100000_IU)){
                    strVitA1Opening = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.OPENING_BALANCE)));
                    strVitA1Received = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.RECEIVED)));
                    strVitA1Administered = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED)));
                    strVitA1Wastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.WASTAGE)));
                    strVitA1StockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND)));

                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.VITAMIN_NAME)).equals(VITAMIN_A_200000_IU)){
                    strVitA2Opening = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.OPENING_BALANCE)));
                    strVitA2Received = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.RECEIVED)));
                    strVitA2Administered =(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED)));
                    strVitA2Wastage = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.WASTAGE)));
                    strVitA2StockInHand = (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND)));

                }

            }while (cursor.moveToNext());
        }
    }

    public void setFieldsAccessibility(boolean flag){

        //Cold Chain
        tempMax.setEnabled(flag);
        tempMin.setEnabled(flag);
        alarmLow.setEnabled(flag);
        alarmHigh.setEnabled(flag);

        //Desease Surveillance
        feverCases.setEnabled(flag);
        feverDeaths.setEnabled(flag);
        afpCases.setEnabled(flag);
        afpDeaths.setEnabled(flag);
        tetanusCases.setEnabled(flag);
        tetanusDeaths.setEnabled(flag);

        //Immunization Sessions
        otherMajorImmunizationActivities.setEnabled(flag);
        outreachCancelled.setEnabled(false);
        outreachConducted.setEnabled(false);
        outreachPlanned.setEnabled(false);
        fixedConducted.setEnabled(false);

        //BCG OPV TT
        bcgFemaleServiceArea.setEnabled(flag);
        bcgMaleServiceArea.setEnabled(flag);
        bcgFemaleCatchmentArea.setEnabled(flag);
        bcgMaleCatchmentArea.setEnabled(flag);
        opvMaleCatchmentArea.setEnabled(flag);
        opvMaleServiceArea.setEnabled(flag);
        opvFemaleCatchmentArea.setEnabled(flag);
        opvFemaleServiceArea.setEnabled(flag);
        tt1MaleServiceArea.setEnabled(flag);
        tt1MaleCatchmentArea.setEnabled(flag);
        tt1FemaleCatchmentArea.setEnabled(flag);
        tt1FemaleServiceArea.setEnabled(flag);

        tt2MaleServiceArea.setEnabled(flag);
        tt2MaleCatchmentArea.setEnabled(flag);
        tt2FemaleCatchmentArea.setEnabled(flag);
        tt2FemaleServiceArea.setEnabled(flag);

        tt3MaleServiceArea.setEnabled(flag);
        tt3MaleCatchmentArea.setEnabled(flag);
        tt3FemaleCatchmentArea.setEnabled(flag);
        tt3FemaleServiceArea.setEnabled(flag);

        tt4MaleServiceArea.setEnabled(flag);
        tt4MaleCatchmentArea.setEnabled(flag);
        tt4FemaleCatchmentArea.setEnabled(flag);
        tt4FemaleServiceArea.setEnabled(flag);

        tt5MaleServiceArea.setEnabled(flag);
        tt5MaleCatchmentArea.setEnabled(flag);
        tt5FemaleCatchmentArea.setEnabled(flag);
        tt5FemaleServiceArea.setEnabled(flag);

        tt5CompletedMaleServiceArea.setEnabled(flag);
        tt5CompletedMaleCatchmentArea.setEnabled(flag);
        tt5CompletedFemaleCatchmentArea.setEnabled(flag);
        tt5CompletedFemaleServiceArea.setEnabled(flag);




        //Vitamins
        vitA2Opening.setEnabled(flag);
        vitA2Wastage.setEnabled(flag);
        vitA2Administered.setEnabled(flag);
        vitA2Received.setEnabled(flag);
        vitA2StockInHand.setEnabled(flag);
        vitA1Administered.setEnabled(flag);
        vitA1Received.setEnabled(flag);
        vitA1Wastage.setEnabled(flag);
        vitA1StockInHand.setEnabled(flag);
        vitA1Opening.setEnabled(flag);

        //Safe Injections
        ml005Wastage.setEnabled(flag);
        ml005StockedOutDays.setEnabled(flag);
        ml005StockInHand.setEnabled(flag);
        ml005Balance.setEnabled(flag);
        ml005Received.setEnabled(flag);
        ml005Used.setEnabled(flag);
        ads05StockedOutDays.setEnabled(flag);
        ads05Wastage.setEnabled(flag);
        ads05StockInHand.setEnabled(flag);
        ads05Used.setEnabled(flag);
        ads05Received.setEnabled(flag);
        ads05Balance.setEnabled(flag);
        dillutionWastage.setEnabled(flag);
        dillutionStockedOutDays.setEnabled(flag);
        dillutionStockInHand.setEnabled(flag);
        dillutionBalance.setEnabled(flag);
        dillutionReceived.setEnabled(flag);
        dillutionUsed.setEnabled(flag);
        safetyBoxStockedOutDays.setEnabled(flag);
        safetyBoxWastage.setEnabled(flag);
        safetyBoxStockInHand.setEnabled(flag);
        safetyBoxReceived.setEnabled(flag);
        safetyBoxUsed.setEnabled(flag);
        safetyBoxBalance.setEnabled(flag);
    }

    public boolean verifyInputs(){

        int fevermonthlycases, feverdeaths, afpmonthlycases, afpdeaths, ttmonthlycases, ttdeaths;


        if (feverCases.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else if (feverDeaths.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else if (afpCases.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else if (afpDeaths.getText().toString().equals("")) {
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else if (tetanusCases.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else if (tetanusDeaths.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }

        fevermonthlycases   = Integer.parseInt(feverCases.getText().toString());
        feverdeaths         = Integer.parseInt(feverDeaths.getText().toString());
        afpmonthlycases     = Integer.parseInt(afpCases.getText().toString());
        afpdeaths           = Integer.parseInt(afpDeaths.getText().toString());
        ttmonthlycases      = Integer.parseInt(tetanusCases.getText().toString());
        ttdeaths            = Integer.parseInt(tetanusDeaths.getText().toString());

        if ((feverdeaths > fevermonthlycases) || (afpdeaths > afpmonthlycases) || (ttdeaths > ttmonthlycases)){
            sayThis(
                    "Verify the data first and try again",
                    1
            );
            return false;
        }

        else
            return true;
    }

    public boolean verifyInputs2(){

        if (Float.parseFloat(tempMin.getText().toString()) > Float.parseFloat(tempMax.getText().toString()))
        {
            sayThis(
                    "Please recheck your data, minimum temperature cannot exceed maximum temperature",
                    1
            );
            return false;
        }else if (tempMin.getText().toString().equals("")) {
            sayThis(
                    "Please fill the Minimum temperature",
                    1
            );
            return false;
        }

        else if (tempMax.getText().toString().equals("")) {
            sayThis(
                    "Please fill the Maximum temperature",
                    1
            );
            return false;
        }
        else if (alarmHigh.getText().toString().equals("")){
            sayThis(
                    "Please fill the number of High alarm episodes",
                    1
            );
            return false;
        }
        else if (alarmLow.getText().toString().equals("")){
            sayThis(
                    "Please fill the number of Low alarm episodes",
                    1
            );
            return false;
        }
        else if (monthYearSpinner.getSelectedItemPosition() <= 0){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else
            return true;
    }

    public boolean verifyInputs3(){
        if(fixedConducted.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (outreachPlanned.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (outreachConducted.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else
            return true;
    }

    public boolean verifyInputs4(){
        if(bcgFemaleServiceArea.getText().toString().trim().equals("")){
            bcgFemaleServiceArea.setError("Please fill the number of BCG vaccination for female children within your service area");
            sayThis(
                    "Please fill the number of BCG vaccination for female children within your service area",
                    1
            );
            return false;
        }
        if(bcgMaleServiceArea.getText().toString().trim().equals("")){
            bcgMaleServiceArea.setError("Please fill the number of BCG vaccination for male children within your service area");
            sayThis(
                    "Please fill the number of BCG vaccination for male children within your service area",
                    1
            );
            return false;
        }
        if(bcgFemaleCatchmentArea.getText().toString().trim().equals("")){
            bcgFemaleCatchmentArea.setError("Please fill the number of BCG vaccination for female children within your catchment area");
            sayThis(
                    "Please fill the number of BCG vaccination for female children within your catchment area",
                    1
            );
            return false;
        }
        if(bcgMaleCatchmentArea.getText().toString().trim().equals("")){
            bcgMaleCatchmentArea.setError("Please fill the number of BCG vaccination for male children within your catchment area");
            sayThis(
                    "Please fill the number of BCG vaccination for male children within your catchment area",
                    1
            );
            return false;
        }
        if (opvFemaleServiceArea.getText().toString().trim().equals("")){
            opvFemaleServiceArea.setError("Please fill the number of OPV0 vaccination for female children within your service area");
            sayThis(
                    "Please fill the number of OPV0 vaccination for female children within your service area",
                    1
            );
            return false;
        }
        if(opvMaleServiceArea.getText().toString().trim().equals("")){
            opvMaleServiceArea.setError("Please fill the number of OPV0 vaccination for female children within your service area");
            sayThis(
                    "Please fill the number of OPV0 vaccination for male children within your service area",
                    1
            );
            return false;
        }
        if(opvFemaleCatchmentArea.getText().toString().trim().equals("")){
            opvFemaleCatchmentArea.setError("Please fill the number of OPV0 vaccination for female children within your catchment area");
            sayThis(
                    "Please fill the number of OPV0 vaccination for female children within your catchment area",
                    1
            );
            return false;
        }
        if(opvMaleCatchmentArea.getText().toString().trim().equals("")){
            opvMaleCatchmentArea.setError("Please fill the number of OPV0 vaccination for male children within your catchment area");
            sayThis(
                    "Please fill the number of OPV0 vaccination for male children within your catchment area",
                    1
            );
            return false;
        }
        if (tt1FemaleServiceArea.getText().toString().trim().equals("") ){
            tt1FemaleServiceArea.setError("Please fill the number of TT1 vaccination for female children within your service area");
            sayThis(
                    "Please fill the number of TT1 vaccination for female children within your service area",
                    1
            );
            return false;
        }

        if(tt1MaleServiceArea.getText().toString().trim().equals("")){
            tt1MaleServiceArea.setError("Please fill the number of TT1 vaccination for male children within your service area");
            sayThis(
                    "Please fill the number of TT1 vaccination for male children within your service area",
                    1
            );
            return false;

        }

        if(tt1FemaleCatchmentArea.getText().toString().trim().equals("")){
            tt1FemaleCatchmentArea.setError("Please fill the number of TT1 vaccination for female children within your catchment area");
            sayThis(
                    "Please fill the number of TT1 vaccination for female children within your catchment area",
                    1
            );
            return false;

        }

        if(tt1MaleCatchmentArea.getText().toString().trim().equals("")){
            tt1MaleCatchmentArea.setError("Please fill the number of TT1 vaccination for male children within your catchment area");
            sayThis(
                    "Please fill the number of TT1 vaccination for male children within your catchment area",
                    1
            );
            return false;

        }

        if (tt2FemaleServiceArea.getText().toString().trim().equals("") || tt2MaleServiceArea.getText().toString().trim().equals("") || tt2FemaleCatchmentArea.getText().toString().trim().equals("") || tt2MaleCatchmentArea.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (tt3FemaleServiceArea.getText().toString().trim().equals("") || tt3MaleServiceArea.getText().toString().trim().equals("") || tt3FemaleCatchmentArea.getText().toString().trim().equals("") || tt3MaleCatchmentArea.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (tt4FemaleServiceArea.getText().toString().trim().equals("") || tt4MaleServiceArea.getText().toString().trim().equals("") || tt4FemaleCatchmentArea.getText().toString().trim().equals("") || tt4MaleCatchmentArea.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (tt5FemaleServiceArea.getText().toString().trim().equals("") || tt5MaleServiceArea.getText().toString().trim().equals("") || tt5FemaleCatchmentArea.getText().toString().trim().equals("") || tt5MaleCatchmentArea.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (tt5CompletedFemaleServiceArea.getText().toString().trim().equals("") || tt5CompletedMaleServiceArea.getText().toString().trim().equals("") || tt5CompletedFemaleCatchmentArea.getText().toString().trim().equals("") || tt5CompletedMaleCatchmentArea.getText().toString().trim().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        else
            return true;
    }

    public boolean verifyInputs5(){
        if (otherMajorImmunizationActivities.getText().toString().equals("")){
            otherMajorImmunizationActivities.setError("Please fill the major immunization activities conducted");
            sayThis(
                    "Please fill the major immunization activities conducted",
                    1
            );
            return false;
        }
        else return true;
    }

    public boolean verifyInputs6(){
        if (ml005Balance.getText().toString().equals("")){
            ml005Balance.setError("Please fill the balance");
            sayThis(
                    "Please fill the balance",
                    1
            );
            return false;
        }
        if (ml005Received.getText().toString().equals("")){
            ml005Received.setError("Please fill the balance");
            sayThis(
                    "Please fill the balance",
                    1
            );
            return false;
        }
        if (ml005Used.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
//        if (ml005Wastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (ml005StockInHand.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (ml005StockedOutDays.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (ads05Balance.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (ads05Received.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (ads05Used.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
//        if (ads05Wastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (ads05StockInHand.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (ads05StockedOutDays.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (dillutionBalance.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (dillutionReceived.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (dillutionUsed.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
//        if (dillutionWastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (dillutionStockInHand.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (dillutionStockedOutDays.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (safetyBoxBalance.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (safetyBoxReceived.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (safetyBoxUsed.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
//        if (safetyBoxWastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (safetyBoxStockInHand.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (safetyBoxStockedOutDays.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }

        int mlOpen, mlRec, mlUsed, adsOpen, adsRec, adsUsed, dilOpen, dilRec, dilUsed, safetyOpen, safetyRec, safetyUsed;
        int mlStockInHand, adsStockInHand, dilStockInHand, safetyboxStockInHand;

        mlOpen      = Integer.parseInt(ml005Balance.getText().toString());
        mlRec       = Integer.parseInt(ml005Received.getText().toString());
        mlUsed      = Integer.parseInt(ml005Used.getText().toString());
        mlStockInHand   = Integer.parseInt(ml005StockInHand.getText().toString());

        adsOpen     = Integer.parseInt(ads05Balance.getText().toString());
        adsRec      = Integer.parseInt(ads05Received.getText().toString());
        adsUsed     = Integer.parseInt(ads05Used.getText().toString());
        adsStockInHand  = Integer.parseInt(ads05StockInHand.getText().toString());

        dilOpen     = Integer.parseInt(dillutionBalance.getText().toString());
        dilRec      = Integer.parseInt(dillutionReceived.getText().toString());
        dilUsed     = Integer.parseInt(dillutionUsed.getText().toString());
        dilStockInHand  = Integer.parseInt(dillutionStockInHand.getText().toString());

        safetyOpen  = Integer.parseInt(safetyBoxBalance.getText().toString());
        safetyRec   = Integer.parseInt(safetyBoxReceived.getText().toString());
        safetyUsed  = Integer.parseInt(safetyBoxUsed.getText().toString());
        safetyboxStockInHand = Integer.parseInt(safetyBoxStockInHand.getText().toString());

        if ((mlOpen+mlRec) < mlUsed || (mlStockInHand > ((mlOpen+mlRec)-mlUsed))){
            sayThis(
                    "Invalid Safety Injection Equipment data, Please Verify data and submit again",
                    1
            );
            return false;
        }

        if ((adsOpen+adsRec)<adsUsed || (adsStockInHand > ((adsOpen+adsRec)-adsUsed))){
            sayThis(
                    "Invalid Safety Injection Equipment data, Please Verify data and submit again",
                    1
            );
            return false;
        }

        if ((dilOpen+dilRec)<dilUsed || (dilStockInHand > ((dilOpen+dilRec)-dilUsed))){
            sayThis(
                    "Invalid Safety Injection Equipment data, Please Verify data and submit again",
                    1
            );
            return false;
        }

        if ((safetyOpen+safetyRec)<safetyUsed || (safetyboxStockInHand > ((safetyOpen+safetyRec)-safetyUsed))){
            sayThis(
                    "Invalid Safety Injection Equipment data, Please Verify data and submit again",
                    1
            );
            return false;
        }
        else
            return true;
    }

    public boolean verifyInputs7(){
        if (vitA1Opening.getText().toString().equals("")){
            vitA1Opening.setError("please fill this field");
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (vitA1Received.getText().toString().equals("")){
            vitA1Received.setError("You must fill all the fields before submitting");
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (vitA1Administered.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
//        if (vitA1Wastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (vitA1StockInHand.getText().toString().equals("")){
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }

        if (vitA2Opening.getText().toString().equals("")){
            vitA2Opening.setError("You must fill all the fields before submitting");
            sayThis(
                    "You must fill all the fields before submitting",
                    1
            );
            return false;
        }
        if (vitA2Received.getText().toString().equals("")){
            vitA2Received.setError("Please receive the amount of vitamin A stock received");
            sayThis(
                    "Please receive the amount of vitamin A stock received",
                    1
            );
            return false;
        }
        if (vitA2Administered.getText().toString().equals("")){
            vitA2Administered.setError("Please enter the number of children administered with vitamin A");
            sayThis(
                    "Please enter the number of children administered with vitamin A",
                    1
            );
            return false;
        }
//        if (vitA2Wastage.getText().toString().equals("")){
//            sayThis(
//                    "You must fill all the fields before submitting",
//                    1
//            );
//            return false;
//        }
        if (vitA2StockInHand.getText().toString().equals("")){
            vitA2StockInHand.setError("Please enter your stock in hand");
            sayThis(
                    "Please enter your stock in hand",
                    1
            );
            return false;
        }

        int vit1Open, vit1Rec, vit1Adm, vit1StockInHand, vit2Open, vit2Rec, vit2Adm, vit2StockInHand;
        vit1Open    = Integer.parseInt(vitA1Opening.getText().toString());
        vit2Open    = Integer.parseInt(vitA2Opening.getText().toString());
        vit1Rec     = Integer.parseInt(vitA1Received.getText().toString());
        vit2Rec     = Integer.parseInt(vitA2Received.getText().toString());
        vit1Adm     = Integer.parseInt(vitA1Administered.getText().toString());
        vit2Adm     = Integer.parseInt(vitA2Administered.getText().toString());
        vit1StockInHand = Integer.parseInt(vitA1StockInHand.getText().toString());
        vit2StockInHand = Integer.parseInt(vitA2StockInHand.getText().toString());

        if ((vit1Open+vit1Rec)<vit1Adm || (vit1StockInHand > ((vit1Open+vit1Rec)-vit1Adm))){
            sayThis(
                    "Invalid Vitamin A data, Please Verify data and submit again",
                    1
            );
            return false;
        }
        if ((vit2Open+vit2Rec)<vit2Adm || (vit2StockInHand > ((vit2Open+vit2Rec)-vit2Adm))){
            sayThis(
                    "Invalid Vitamin A data, Please Verify data and submit again",
                    1
            );
            return false;
        }

        return true;
    }

    public void getValues(){
        strFeverCases       = feverCases.getText().toString().trim();
        strFeverDeaths      = feverDeaths.getText().toString().trim();
        strAfpCases         = afpCases.getText().toString().trim();
        strAfpDeaths        = afpDeaths.getText().toString().trim();
        strTetanusCases     = tetanusCases.getText().toString().trim();
        strTetanusDeaths    = tetanusDeaths.getText().toString().trim();
    }

    public void getValues2(){
        strTempMax      = tempMax.getText().toString().trim();
        strTempMin      = tempMin.getText().toString().trim();
        strAlarmHigh    = alarmHigh.getText().toString().trim();
        strAlarmLow     = alarmLow.getText().toString().trim();
    }

    public void getValue3(){
        strFixedConducted       = fixedConducted.getText().toString().trim();
        strOutreachPlanned      = outreachPlanned.getText().toString().trim();
        strOutreachConducted    = outreachConducted.getText().toString().trim();

        int outreachPlanedInt   = Integer.parseInt(strOutreachPlanned);
        int outreachConductedInt    = Integer.parseInt(strOutreachConducted);

        int outreachCancelledInt    = outreachPlanedInt - outreachConductedInt;

        strOutreachCancelled    = outreachCancelledInt+"";
        outreachCancelled.setText(strOutreachCancelled);

        strOtherMajorImmunizationActivities  = otherMajorImmunizationActivities.getText().toString().trim();
    }

    public void getValue4(){

        strBcgFemaleService     = bcgFemaleServiceArea.getText().toString().trim();
        strBcgMaleService       = bcgMaleServiceArea.getText().toString().trim();
        strBcgFemaleCatchment   = bcgFemaleCatchmentArea.getText().toString().trim();
        strBcgMaleCatchment     = bcgMaleCatchmentArea.getText().toString().trim();

        strOpvFemaleService     = opvFemaleServiceArea.getText().toString().trim();
        strOpvMaleService       = opvMaleServiceArea.getText().toString().trim();
        strOpvFemaleCatchment   = opvFemaleCatchmentArea.getText().toString().trim();
        strOpvMaleCatchment     = opvMaleCatchmentArea.getText().toString().trim();

        strTt1FemaleService      = tt1FemaleServiceArea.getText().toString().trim();
        strTt1MaleServicec       = tt1MaleServiceArea.getText().toString().trim();
        strTt1FemaleCatchment    = tt1FemaleCatchmentArea.getText().toString().trim();
        strTt1MaleCatchment      = tt1MaleCatchmentArea.getText().toString().trim();


        strTt2FemaleService      = tt2FemaleServiceArea.getText().toString().trim();
        strTt2MaleServicec       = tt2MaleServiceArea.getText().toString().trim();
        strTt2FemaleCatchment    = tt2FemaleCatchmentArea.getText().toString().trim();
        strTt2MaleCatchment      = tt2MaleCatchmentArea.getText().toString().trim();


        strTt3FemaleService      = tt3FemaleServiceArea.getText().toString().trim();
        strTt3MaleServicec       = tt3MaleServiceArea.getText().toString().trim();
        strTt3FemaleCatchment    = tt3FemaleCatchmentArea.getText().toString().trim();
        strTt3MaleCatchment      = tt3MaleCatchmentArea.getText().toString().trim();


        strTt4FemaleService      = tt4FemaleServiceArea.getText().toString().trim();
        strTt4MaleServicec       = tt4MaleServiceArea.getText().toString().trim();
        strTt4FemaleCatchment    = tt4FemaleCatchmentArea.getText().toString().trim();
        strTt4MaleCatchment      = tt4MaleCatchmentArea.getText().toString().trim();


        strTt5FemaleService      = tt5FemaleServiceArea.getText().toString().trim();
        strTt5MaleServicec       = tt5MaleServiceArea.getText().toString().trim();
        strTt5FemaleCatchment    = tt5FemaleCatchmentArea.getText().toString().trim();
        strTt5MaleCatchment      = tt5MaleCatchmentArea.getText().toString().trim();


        strTt5CompletedFemaleService      = tt5CompletedFemaleServiceArea.getText().toString().trim();
        strTt5CompletedMaleServicec       = tt5CompletedMaleServiceArea.getText().toString().trim();
        strTt5CompletedFemaleCatchment    = tt5CompletedFemaleCatchmentArea.getText().toString().trim();
        strTt5CompletedMaleCatchment      = tt5CompletedMaleCatchmentArea.getText().toString().trim();

    }

    public void getValue5(){
        strOtherMajorImmunizationActivities = otherMajorImmunizationActivities.getText().toString().trim();
    }

    public void getValue6() {
        strml005Balance     = ml005Balance.getText().toString();
        strml005Received    = ml005Received.getText().toString();
        strml005Used        = ml005Used.getText().toString();
        strml005Wastage     = ml005Wastage.getText().toString();
        strml005StockInHand = ml005StockInHand.getText().toString();
        strml005StockedOutDays =  ml005StockedOutDays.getText().toString();
        strads05Balance     = ads05Balance.getText().toString();
        strads05Received    = ads05Received.getText().toString();
        strads05Used        = ads05Used.getText().toString();
        strads05Wastage     = ads05Wastage.getText().toString();
        strads05StockInHand = ads05StockInHand.getText().toString();
        strads05StockedOutDays = ads05StockedOutDays.getText().toString();
        strDillutionBalance = dillutionBalance.getText().toString();
        strDillutionReceived   = dillutionReceived.getText().toString();
        strDillutionUsed    = dillutionUsed.getText().toString();
        strDillutionWastage = dillutionWastage.getText().toString();
        strDillutionStockInHand = dillutionStockInHand.getText().toString();
        strDillutionStockedOutDays = dillutionStockedOutDays.getText().toString();
        strSafetyBoxBalance = safetyBoxBalance.getText().toString();
        strSafetyBoxReceived    = safetyBoxReceived.getText().toString();
        strSafetyBoxUsed    = safetyBoxUsed.getText().toString();
        strSafetyBoxWastage = safetyBoxWastage.getText().toString();
        strSafetyBoxStockInHand = safetyBoxStockInHand.getText().toString();
        strSafetyBoxStockedOutDays  = safetyBoxStockedOutDays.getText().toString();
    }

    public void getValue7(){
        strVitA1Opening     = vitA1Opening.getText().toString().trim();
        strVitA1Received    = vitA1Received.getText().toString().trim();
        strVitA1Administered    = vitA1Administered.getText().toString().trim();
        strVitA1Wastage     = vitA1Wastage.getText().toString().trim();
        strVitA1StockInHand = vitA1StockInHand.getText().toString().trim();
        strVitA2Opening     = vitA2Opening.getText().toString().trim();
        strVitA2Received    = vitA2Received.getText().toString().trim();
        strVitA2Administered    = vitA2Administered.getText().toString().trim();
        strVitA2Wastage     = vitA2Wastage.getText().toString().trim();
        strVitA2StockInHand = vitA2StockInHand.getText().toString().trim();
    }

    public boolean saveDeseasesSurveillance(){
        ContentValues cv = new ContentValues();
        cv.put(GIISContract.SyncColumns.UPDATED, 1);
        cv.put(SQLHandler.SurveillanceColumns.FEVER_MONTHLY_CASES, strFeverCases);
        cv.put(SQLHandler.SurveillanceColumns.FEVER_DEATHS, strFeverDeaths);
        cv.put(SQLHandler.SurveillanceColumns.APF_MONTHLY_CASES, strAfpCases);
        cv.put(SQLHandler.SurveillanceColumns.APF_DEATHS, strAfpDeaths);
        cv.put(SQLHandler.SurveillanceColumns.NEONATAL_TT_CASES, strTetanusCases);
        cv.put(SQLHandler.SurveillanceColumns.NEONATAL_TT_DEATHS, strTetanusDeaths);
        cv.put(SQLHandler.SurveillanceColumns.REPORTED_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        //Save Surveillance to local DB
        DatabaseHandler db = new DatabaseHandler(this);
        db.addUpdateDeseasesSurveillance(cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        int feverMCases, feverDeaths, apfMcases, apfDeaths, ttMCases, ttDeaths, selectedMonth, selectedYear, selectedMonthNumber;
        String modifiedOnString = "";

        feverMCases = Integer.parseInt(strFeverCases);
        feverDeaths = Integer.parseInt(strFeverDeaths);
        apfMcases   = Integer.parseInt(strAfpCases);
        apfDeaths   = Integer.parseInt(strAfpDeaths);
        ttMCases    = Integer.parseInt(strTetanusCases);
        ttDeaths    = Integer.parseInt(strTetanusDeaths);
        selectedMonth   = Integer.parseInt(currentSelectedMonth.getMonth_number());
        selectedYear    = Integer.parseInt(currentlySelectedYear);

        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        app.sendDeseaseSurveillanceToServer(feverMCases, feverDeaths, apfMcases, apfDeaths, ttMCases, ttDeaths, selectedMonth, selectedYear, modifiedOnString);

        return true;

    }

    public boolean saveRefrigeratorValues(){
        ContentValues cv = new ContentValues();
        cv.put(GIISContract.SyncColumns.UPDATED, 1);
        cv.put(SQLHandler.RefrigeratorColums.ALARM_HIGH_TEMP, strAlarmHigh);
        cv.put(SQLHandler.RefrigeratorColums.ALARM_LOW_TEMP, strAlarmLow);
        cv.put(SQLHandler.RefrigeratorColums.TEMP_MAX, strTempMax);
        cv.put(SQLHandler.RefrigeratorColums.TEMP_MIN, strTempMin);
        cv.put(SQLHandler.RefrigeratorColums.REPORTED_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        DatabaseHandler db = new DatabaseHandler(this);
        db.addUpdateRefrigeratorTemperature(cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        float tempMax, tempMin;
        int alarmLow,alarmHigh;

        String nowString = URLEncoder.encode(fmt.format(Calendar.getInstance().getTime()));

        alarmHigh   = Integer.parseInt(strAlarmHigh);
        alarmLow    = Integer.parseInt(strAlarmLow);
        tempMax     = Float.parseFloat(strTempMax);
        tempMin     = Float.parseFloat(strTempMin);
        int selectedMonth    = Integer.parseInt(currentSelectedMonth.getMonth_number());
        int selectedYear     = Integer.parseInt(currentlySelectedYear);

        String modifiedOnString = "";
        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        app.sendColdChainToServer(tempMax,tempMin,alarmHigh,alarmLow,selectedMonth,selectedYear,modifiedOnString);

        return true;
    }

    public boolean saveImmunizationSessionValue(){

        int selectedMonth    = Integer.parseInt(currentSelectedMonth.getMonth_number());
        int selectedYear     = Integer.parseInt(currentlySelectedYear);

        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

        String modifiedOnString = "";
        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int outreachPlanned = Integer.parseInt(strOutreachPlanned);
        int fixedConducted  = Integer.parseInt(strFixedConducted);
        int cancelled       = outreachPlanned - fixedConducted;

        Date modifiedAt = null;

        try {
            modifiedAt = formatted.parse(modifiedOnString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ContentValues cv    = new ContentValues();
        cv.put(GIISContract.SyncColumns.UPDATED, 1);
        cv.put(GIISContract.SyncColumns.MODIFIED_AT, modifiedOnString);
        cv.put(SQLHandler.ImmunizationSessionColumns.FIXED_CONDUCTED, strFixedConducted);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_CANCELLED, strOutreachCancelled);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_CONDUCTED, strOutreachConducted);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_PLANNED, strOutreachPlanned);
        cv.put(SQLHandler.ImmunizationSessionColumns.OTHERACTIVITIES, strOtherMajorImmunizationActivities);
        cv.put(SQLHandler.ImmunizationSessionColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        DatabaseHandler db = new DatabaseHandler(this);
        db.addUpdateImmunizationSessions(cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        app.sendImmunizationSessionsToServer(outreachPlanned,URLEncoder.encode(strOtherMajorImmunizationActivities), selectedMonth, selectedYear, modifiedOnString);

        return true;
    }

    public boolean saveVaccinationsValues(){

        DatabaseHandler db = new DatabaseHandler(this);
        //TODO REMOVE THE HARDCORDING OF DOSE IDS, GET THEM FROM THE SERVER OR DATABASE
        int BCG_DOSE_ID  = 61;
        int OPV_0_DOSE_ID = 62;
        int TT1_DOSE_ID   = 79;
        int TT2_DOSE_ID   = 80;
        int TT3_DOSE_ID   = 81;
        int TT4_DOSE_ID   = 82;
        int TT5_DOSE_ID   = 83;
        int TT5_COMPLETED_DOSE_ID   = 84;

        int selectedMonth    = Integer.parseInt(currentSelectedMonth.getMonth_number());
        int selectedYear     = Integer.parseInt(currentlySelectedYear);

        String modifiedOnString = "";
        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int bcgMaleService = Integer.parseInt(strBcgMaleService);
        int bcgFemaleService    = Integer.parseInt(strBcgFemaleService);
        int bcgMaleCatchment    = Integer.parseInt(strBcgMaleCatchment);
        int bcgFemaleCatchment  = Integer.parseInt(strBcgFemaleCatchment);
        int bcgTotalService     = bcgMaleService+bcgFemaleService;
        int bcgTotalCatchment   = bcgMaleCatchment+bcgFemaleCatchment;
        int bcgTotalServiceAndCatchment = bcgTotalService + bcgTotalCatchment;

        ContentValues bcgCv    = new ContentValues();
        bcgCv.put(GIISContract.SyncColumns.UPDATED, 1);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, BCG_DOSE_ID+"");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strBcgFemaleService);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strBcgMaleService);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, bcgTotalService+"");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strBcgFemaleCatchment);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strBcgMaleCatchment);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, bcgTotalCatchment+"");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, bcgTotalServiceAndCatchment+"");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);


        db.addUpdateVaccinationsBcgOpvTt(bcgCv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, BCG_DOSE_ID+"");
        app.sendBcgOpvTtToServer(BCG_DOSE_ID, bcgMaleService, bcgMaleCatchment, bcgFemaleService, bcgFemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);

        int opvMaleService = Integer.parseInt(strOpvMaleService);
        int opvFemaleService    = Integer.parseInt(strOpvFemaleService);
        int opvMaleCatchment    = Integer.parseInt(strOpvMaleCatchment);
        int opvFemaleCatchment  = Integer.parseInt(strOpvFemaleCatchment);
        int opvTotalService     = opvMaleService+opvFemaleService;
        int opvTotalCatchment   = opvMaleCatchment+opvFemaleCatchment;
        int opvTotalServiceAndCatchment = opvTotalService + opvTotalCatchment;

        ContentValues opvCv = new ContentValues();
        opvCv.put(GIISContract.SyncColumns.UPDATED, 1);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, OPV_0_DOSE_ID+"");
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strOpvFemaleService);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strOpvMaleService);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, opvTotalService+"");
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strOpvFemaleCatchment);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strOpvMaleCatchment);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, opvTotalCatchment+"");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, opvTotalServiceAndCatchment+"");
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(opvCv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, OPV_0_DOSE_ID+"");
        app.sendBcgOpvTtToServer(OPV_0_DOSE_ID, opvMaleService, opvMaleCatchment, opvFemaleService, opvFemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);


        int tt1MaleService = Integer.parseInt(strTt1MaleServicec);
        int tt1FemaleService    = Integer.parseInt(strTt1FemaleService);
        int tt1MaleCatchment    = Integer.parseInt(strTt1MaleCatchment);
        int tt1FemaleCatchment  = Integer.parseInt(strTt1FemaleCatchment);
        int tt1TotalService     = tt1MaleService+tt1FemaleService;
        int tt1TotalCatchment   = tt1MaleCatchment+tt1FemaleCatchment;
        int tt1TotalServiceAndCatchment = tt1TotalService + tt1TotalCatchment;

        ContentValues tt1Cv = new ContentValues();
        tt1Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT1_DOSE_ID+"");
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt1FemaleService);
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt1MaleServicec);
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt1TotalService+"");
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt1FemaleCatchment);
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt1MaleCatchment);
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt1TotalCatchment+"");
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt1TotalServiceAndCatchment+"");
        tt1Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt1Cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT1_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT1_DOSE_ID, tt1MaleService, tt1MaleCatchment, tt1FemaleService, tt1FemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);




        int tt2MaleService = Integer.parseInt(strTt2MaleServicec);
        int tt2FemaleService    = Integer.parseInt(strTt2FemaleService);
        int tt2MaleCatchment    = Integer.parseInt(strTt2MaleCatchment);
        int tt2FemaleCatchment  = Integer.parseInt(strTt2FemaleCatchment);
        int tt2TotalService     = tt2MaleService+tt2FemaleService;
        int tt2TotalCatchment   = tt2MaleCatchment+tt2FemaleCatchment;
        int tt2TotalServiceAndCatchment = tt2TotalService + tt2TotalCatchment;

        ContentValues tt2Cv = new ContentValues();
        tt2Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT2_DOSE_ID+"");
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt2FemaleService);
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt2MaleServicec);
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt2TotalService+"");
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt2FemaleCatchment);
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt2MaleCatchment);
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt2TotalCatchment+"");
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt2TotalServiceAndCatchment+"");
        tt2Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt2Cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT2_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT2_DOSE_ID, tt2MaleService, tt2MaleCatchment, tt2FemaleService, tt2FemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);



        int tt3MaleService = Integer.parseInt(strTt3MaleServicec);
        int tt3FemaleService    = Integer.parseInt(strTt3FemaleService);
        int tt3MaleCatchment    = Integer.parseInt(strTt3MaleCatchment);
        int tt3FemaleCatchment  = Integer.parseInt(strTt3FemaleCatchment);
        int tt3TotalService     = tt3MaleService+tt3FemaleService;
        int tt3TotalCatchment   = tt3MaleCatchment+tt3FemaleCatchment;
        int tt3TotalServiceAndCatchment = tt3TotalService + tt3TotalCatchment;

        ContentValues tt3Cv = new ContentValues();
        tt3Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT3_DOSE_ID+"");
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt3FemaleService);
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt3MaleServicec);
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt3TotalService+"");
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt3FemaleCatchment);
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt3MaleCatchment);
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt3TotalCatchment+"");
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt3TotalServiceAndCatchment+"");
        tt3Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt3Cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT3_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT3_DOSE_ID, tt3MaleService, tt3MaleCatchment, tt3FemaleService, tt3FemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);



        int tt4MaleService = Integer.parseInt(strTt4MaleServicec);
        int tt4FemaleService    = Integer.parseInt(strTt4FemaleService);
        int tt4MaleCatchment    = Integer.parseInt(strTt4MaleCatchment);
        int tt4FemaleCatchment  = Integer.parseInt(strTt4FemaleCatchment);
        int tt4TotalService     = tt4MaleService+tt4FemaleService;
        int tt4TotalCatchment   = tt4MaleCatchment+tt4FemaleCatchment;
        int tt4TotalServiceAndCatchment = tt4TotalService + tt4TotalCatchment;

        ContentValues tt4Cv = new ContentValues();
        tt4Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT4_DOSE_ID+"");
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt4FemaleService);
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt4MaleServicec);
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt4TotalService+"");
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt4FemaleCatchment);
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt4MaleCatchment);
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt4TotalCatchment+"");
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt4TotalServiceAndCatchment+"");
        tt4Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt4Cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT4_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT4_DOSE_ID, tt4MaleService, tt4MaleCatchment, tt4FemaleService, tt4FemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);


        int tt5MaleService = Integer.parseInt(strTt5MaleServicec);
        int tt5FemaleService    = Integer.parseInt(strTt5FemaleService);
        int tt5MaleCatchment    = Integer.parseInt(strTt5MaleCatchment);
        int tt5FemaleCatchment  = Integer.parseInt(strTt5FemaleCatchment);
        int tt5TotalService     = tt5MaleService+tt5FemaleService;
        int tt5TotalCatchment   = tt5MaleCatchment+tt5FemaleCatchment;
        int tt5TotalServiceAndCatchment = tt5TotalService + tt5TotalCatchment;

        ContentValues tt5Cv = new ContentValues();
        tt5Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT5_DOSE_ID+"");
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt5FemaleService);
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt5MaleServicec);
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt5TotalService+"");
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt5FemaleCatchment);
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt5MaleCatchment);
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt5TotalCatchment+"");
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt5TotalServiceAndCatchment+"");
        tt5Cv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt5Cv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT5_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT5_DOSE_ID, tt5MaleService, tt5MaleCatchment, tt5FemaleService, tt5FemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);


        int tt5CompletedMaleService = Integer.parseInt(strTt5CompletedMaleServicec);
        int tt5CompletedFemaleService    = Integer.parseInt(strTt5CompletedFemaleService);
        int tt5CompletedMaleCatchment    = Integer.parseInt(strTt5CompletedMaleCatchment);
        int tt5CompletedFemaleCatchment  = Integer.parseInt(strTt5CompletedFemaleCatchment);
        int tt5CompletedTotalService     = tt5CompletedMaleService+tt5CompletedFemaleService;
        int tt5CompletedTotalCatchment   = tt5CompletedMaleCatchment+tt5CompletedFemaleCatchment;
        int tt5CompletedTotalServiceAndCatchment = tt5CompletedTotalService + tt5CompletedTotalCatchment;

        ContentValues tt5CompletedCv = new ContentValues();
        tt5CompletedCv.put(GIISContract.SyncColumns.UPDATED, 1);
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_ID, TT5_COMPLETED_DOSE_ID+"");
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTt5CompletedFemaleService);
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTt5CompletedMaleServicec);
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_SERVICE_AREA, tt5CompletedTotalService+"");
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTt5CompletedFemaleCatchment);
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTt5CompletedMaleCatchment);
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AREA, tt5CompletedTotalCatchment+"");
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.TOTAL_CATCHMENT_AND_SERVICE_AREA, tt5CompletedTotalServiceAndCatchment+"");
        tt5CompletedCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        db.addUpdateVaccinationsBcgOpvTt(tt5CompletedCv, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear, TT5_COMPLETED_DOSE_ID+"");
        app.sendBcgOpvTtToServer(TT5_COMPLETED_DOSE_ID, tt5CompletedMaleService, tt5CompletedMaleCatchment, tt5CompletedFemaleService, tt5CompletedFemaleCatchment, 0, 0, 0, selectedMonth, selectedYear, modifiedOnString);

        return true;
    }

    public boolean saveOtherImmunizationActivities(){

        ContentValues cv    = new ContentValues();
        cv.put(GIISContract.SyncColumns.UPDATED, 1);
        cv.put(SQLHandler.OtherMajorImmunizationActivitiesColumns.OTHER_ACTIVITIES, strOtherMajorImmunizationActivities);
        cv.put(SQLHandler.OtherMajorImmunizationActivitiesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        DatabaseHandler db = new DatabaseHandler(this);
        db.addOtherMajorImmunizationActivities(cv);

        return true;
    }

    public boolean saveInjectionEquipments(){
        DatabaseHandler db = new DatabaseHandler(this);
        String selectedMon  = currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear;

        int openingBalance  = Integer.parseInt(strml005Balance);
        int received        = Integer.parseInt(strml005Received);
        int used            = Integer.parseInt(strml005Used);
        int wastage         = Integer.parseInt(ml005Wastage.getText().toString());
        int stockInHand     = Integer.parseInt(strml005StockInHand);
        int stockesOutDays  = Integer.parseInt(strml005StockedOutDays);

        int selectedMonth    = Integer.parseInt(currentSelectedMonth.getMonth_number());
        int selectedYear     = Integer.parseInt(currentlySelectedYear);

        wastage = ((openingBalance + received) - used)-stockInHand;

        String modifiedOnString = "";
        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ContentValues ads005Cv    = new ContentValues();
        ads005Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, ADS_OO5ML);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strml005Balance);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strml005Received);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strml005Used);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, wastage);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strml005StockInHand);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strml005StockedOutDays);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, selectedMon);

        app.sendSyrinjesAndSafetyBoxesToServer(ADS_OO5ML, openingBalance, received, stockInHand, used, wastage, stockesOutDays, selectedMonth, selectedYear, modifiedOnString);
        db.addUpdateInjectionEquipment(ads005Cv, selectedMon, ADS_OO5ML);

        openingBalance  = Integer.parseInt(strads05Balance);
        received        = Integer.parseInt(strads05Received);
        used            = Integer.parseInt(strads05Used);
        wastage         = Integer.parseInt(ads05Wastage.getText().toString());
        stockInHand     = Integer.parseInt(strads05StockInHand);
        stockesOutDays  = Integer.parseInt(strads05StockedOutDays);

        wastage = ((openingBalance + received) - used)-stockInHand;

        ContentValues ads05Cv    = new ContentValues();
        ads05Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, ADS_O5ML);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strads05Balance);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strads05Received);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strads05Used);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, wastage);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strads05StockInHand);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strads05StockedOutDays);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);


        app.sendSyrinjesAndSafetyBoxesToServer(ADS_O5ML, openingBalance, received, stockInHand, used, wastage, stockesOutDays, selectedMonth, selectedYear, modifiedOnString);
        db.addUpdateInjectionEquipment(ads05Cv, selectedMon, ADS_O5ML);

        openingBalance  = Integer.parseInt(strDillutionBalance);
        received        = Integer.parseInt(strDillutionReceived);
        used            = Integer.parseInt(strDillutionUsed);
        wastage         = Integer.parseInt(dillutionWastage.getText().toString());
        stockInHand     = Integer.parseInt(strDillutionStockInHand);
        stockesOutDays  = Integer.parseInt(strDillutionStockedOutDays);

        wastage = ((openingBalance + received) - used)-stockInHand;

        ContentValues dillutionCv    = new ContentValues();
        dillutionCv.put(GIISContract.SyncColumns.UPDATED, 1);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, SDILLUTION);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strDillutionBalance);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strDillutionReceived);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strDillutionUsed);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, wastage);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strDillutionStockInHand);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strDillutionStockedOutDays);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        app.sendSyrinjesAndSafetyBoxesToServer(SDILLUTION, openingBalance, received, stockInHand, used, wastage, stockesOutDays, selectedMonth, selectedYear, modifiedOnString);
        db.addUpdateInjectionEquipment(dillutionCv, selectedMon, SDILLUTION);


        openingBalance  = Integer.parseInt(strSafetyBoxBalance);
        received        = Integer.parseInt(strSafetyBoxReceived);
        used            = Integer.parseInt(strSafetyBoxUsed);
        wastage         = Integer.parseInt(safetyBoxWastage.getText().toString());
        stockInHand     = Integer.parseInt(strSafetyBoxStockInHand);
        stockesOutDays  = Integer.parseInt(strSafetyBoxStockedOutDays);

        wastage = ((openingBalance + received) - used)-stockInHand;

        ContentValues safetyBoxesCV    = new ContentValues();
        safetyBoxesCV.put(GIISContract.SyncColumns.UPDATED, 1);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, SAFETY_BOXES);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strSafetyBoxBalance);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strSafetyBoxReceived);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strSafetyBoxUsed);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, wastage);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strSafetyBoxStockInHand);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strSafetyBoxStockedOutDays);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        app.sendSyrinjesAndSafetyBoxesToServer(SAFETY_BOXES, openingBalance, received, stockInHand, used, wastage, stockesOutDays, selectedMonth, selectedYear, modifiedOnString);
        db.addUpdateInjectionEquipment(safetyBoxesCV, selectedMon, SAFETY_BOXES);

        return true;
    }

    public boolean saveVitaminAStock(){
        DatabaseHandler db = new DatabaseHandler(this);
        String reportingMonth   = currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear;

        int openingBalance  = Integer.parseInt(strVitA1Opening);
        int received        = Integer.parseInt(strVitA1Received);
        int administered    = Integer.parseInt(strVitA1Administered);
        int wastage         = Integer.parseInt(vitA1Wastage.getText().toString());
        int stockInHand     = Integer.parseInt(strVitA1StockInHand);

        int selectedMonth    = Integer.parseInt(currentSelectedMonth.getMonth_number());
        int selectedYear     = Integer.parseInt(currentlySelectedYear);

        wastage = ((openingBalance + received) - administered)-stockInHand;

        String modifiedOnString = "";
        try {
            modifiedOnString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ContentValues vitA1    = new ContentValues();
        vitA1.put(GIISContract.SyncColumns.UPDATED, 1);
        vitA1.put(SQLHandler.HfVitaminAColumns.VITAMIN_NAME, VITAMIN_A_100000_IU);
        vitA1.put(SQLHandler.HfVitaminAColumns.OPENING_BALANCE, strVitA1Opening);
        vitA1.put(SQLHandler.HfVitaminAColumns.RECEIVED, strVitA1Received);
        vitA1.put(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED, strVitA1Administered);
        vitA1.put(SQLHandler.HfVitaminAColumns.WASTAGE, wastage);
        vitA1.put(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND, strVitA1StockInHand);
        vitA1.put(SQLHandler.HfVitaminAColumns.REPORTING_MONTH, reportingMonth);

        app.sendVitaminAStockToServer(VITAMIN_A_100000_IU, openingBalance, received, stockInHand, administered, selectedMonth, selectedYear, modifiedOnString, wastage );
        db.addUpdateVitaminAStock(vitA1, reportingMonth, VITAMIN_A_100000_IU);

        openingBalance  = Integer.parseInt(strVitA2Opening);
        received        = Integer.parseInt(strVitA2Received);
        administered    = Integer.parseInt(strVitA2Administered);
        wastage         = Integer.parseInt(vitA2Wastage.getText().toString());
        stockInHand     = Integer.parseInt(strVitA2StockInHand);

        wastage = ((openingBalance + received) - administered)-stockInHand;

        ContentValues vitA2    = new ContentValues();
        vitA2.put(GIISContract.SyncColumns.UPDATED, 1);
        vitA2.put(SQLHandler.HfVitaminAColumns.VITAMIN_NAME, VITAMIN_A_200000_IU);
        vitA2.put(SQLHandler.HfVitaminAColumns.OPENING_BALANCE, strVitA2Opening);
        vitA2.put(SQLHandler.HfVitaminAColumns.RECEIVED, strVitA2Received);
        vitA2.put(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED, strVitA2Administered);
        vitA2.put(SQLHandler.HfVitaminAColumns.WASTAGE, wastage);
        vitA2.put(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND, strVitA2StockInHand);
        vitA2.put(SQLHandler.HfVitaminAColumns.REPORTING_MONTH, reportingMonth);

        app.sendVitaminAStockToServer(VITAMIN_A_200000_IU, openingBalance, received, stockInHand, administered, selectedMonth, selectedYear, modifiedOnString, wastage );
        db.addUpdateVitaminAStock(vitA2, reportingMonth, VITAMIN_A_200000_IU);

        return true;
    }

    public void resetValues(){
        strAlarmLow = "";
        strFeverCases = "";
        strAlarmHigh = "";
        strAfpCases = "";
        strAfpDeaths = "";
        strFeverDeaths = "";
        strTempMax = "";
        strTempMin = "";
        strTetanusCases = "";
        strTetanusDeaths = "";
        strFixedConducted = "";
        strOutreachConducted = "";
        strOutreachPlanned = "";
        strOutreachCancelled = "";
        strBcgFemaleService = "";
        strBcgMaleService = "";
        strBcgFemaleCatchment = "";
        strBcgMaleCatchment = "";
        strOpvFemaleService = "";
        strOpvMaleService = "";
        strOpvFemaleCatchment = "";
        strOpvMaleCatchment = "";
        strTt1FemaleService = "";
        strTt1MaleServicec = "";
        strTt1FemaleCatchment = "";
        strTt1MaleCatchment = "";


        strTt2FemaleService = "";
        strTt2MaleServicec = "";
        strTt2FemaleCatchment = "";
        strTt2MaleCatchment = "";

        strTt3FemaleService = "";
        strTt3MaleServicec = "";
        strTt3FemaleCatchment = "";
        strTt3MaleCatchment = "";

        strTt4FemaleService = "";
        strTt4MaleServicec = "";
        strTt4FemaleCatchment = "";
        strTt4MaleCatchment = "";

        strTt5FemaleService = "";
        strTt5MaleServicec = "";
        strTt5FemaleCatchment = "";
        strTt5MaleCatchment = "";

        strTt5CompletedFemaleService = "";
        strTt5CompletedMaleServicec = "";
        strTt5CompletedFemaleCatchment = "";
        strTt5CompletedMaleCatchment = "";

        strml005StockedOutDays = "";
        strml005StockInHand = "";
        strml005Wastage = "";
        strml005Used = "";
        strml005Received = "";
        strml005Balance = "";
        strads05StockedOutDays = "";
        strads05StockInHand = "";
        strads05Wastage = "";
        strads05Used = "";
        strads05Balance = "";
        strads05Received = "";
        strDillutionStockedOutDays = "";
        strDillutionStockInHand ="";
        strDillutionWastage = "";
        strDillutionUsed = "";
        strDillutionBalance = "";
        strDillutionReceived = "";
        strSafetyBoxBalance = "";
        strSafetyBoxStockedOutDays = "";
        strSafetyBoxStockInHand =  "";
        strSafetyBoxWastage = "";
        strSafetyBoxUsed = "";
        strSafetyBoxReceived = "";
        strVitA1Administered = "";
        strVitA1Opening = "";
        strVitA1Received = "";
        strVitA1StockInHand = "";
        strVitA1Wastage = "";
        strVitA2Administered = "";
        strVitA2Opening = "";
        strVitA2Received = "";
        strVitA2StockInHand = "";
        strVitA2Wastage = "";
    }

    public void clearFields(){
        feverCases.setText("");
        feverDeaths.setText("");
        afpCases.setText("");
        afpDeaths.setText("");
        tetanusCases.setText("");
        tetanusDeaths.setText("");

        tempMax.setText("");
        tempMin.setText("");
        alarmHigh.setText("");
        alarmLow.setText("");

        fixedConducted.setText("");
        outreachConducted.setText("");
        outreachPlanned.setText("");
        outreachCancelled.setText("");

        bcgFemaleServiceArea.setText("");
        bcgMaleServiceArea.setText("");
        bcgFemaleCatchmentArea.setText("");
        bcgMaleCatchmentArea.setText("");
        opvFemaleServiceArea.setText("");
        opvMaleServiceArea.setText("");
        opvFemaleCatchmentArea.setText("");
        opvMaleCatchmentArea.setText("");
        tt1FemaleCatchmentArea.setText("");
        tt1FemaleServiceArea.setText("");
        tt1MaleCatchmentArea.setText("");
        tt1MaleServiceArea.setText("");


        tt2FemaleCatchmentArea.setText("");
        tt2FemaleServiceArea.setText("");
        tt2MaleCatchmentArea.setText("");
        tt2MaleServiceArea.setText("");


        tt3FemaleCatchmentArea.setText("");
        tt3FemaleServiceArea.setText("");
        tt3MaleCatchmentArea.setText("");
        tt3MaleServiceArea.setText("");


        tt4FemaleCatchmentArea.setText("");
        tt4FemaleServiceArea.setText("");
        tt4MaleCatchmentArea.setText("");
        tt4MaleServiceArea.setText("");


        tt5FemaleCatchmentArea.setText("");
        tt5FemaleServiceArea.setText("");
        tt5MaleCatchmentArea.setText("");
        tt5MaleServiceArea.setText("");


        tt5CompletedFemaleCatchmentArea.setText("");
        tt5CompletedFemaleServiceArea.setText("");
        tt5CompletedMaleCatchmentArea.setText("");
        tt5CompletedMaleServiceArea.setText("");


        otherMajorImmunizationActivities.setText("");

        ml005Balance.setText("");
        ml005Received.setText("");
        ml005Used.setText("");
        ml005Wastage.setText("");
        ml005StockInHand.setText("");
        ml005StockedOutDays.setText("");
        ads05Balance.setText("");
        ads05Received.setText("");
        ads05Used.setText("");
        ads05Wastage.setText("");
        ads05StockInHand.setText("");
        ads05StockedOutDays.setText("");
        dillutionBalance.setText("");
        dillutionReceived.setText("");
        dillutionUsed.setText("");
        dillutionWastage.setText("");
        dillutionStockInHand.setText("");
        dillutionStockedOutDays.setText("");
        safetyBoxBalance.setText("");
        safetyBoxReceived.setText("");
        safetyBoxUsed.setText("");
        safetyBoxWastage.setText("");
        safetyBoxStockInHand.setText("");
        safetyBoxStockedOutDays.setText("");
        vitA1Opening.setText("");
        vitA1Received.setText("");
        vitA1Administered.setText("");
        vitA1Wastage.setText("");
        vitA1StockInHand.setText("");
        vitA2Opening.setText("");
        vitA2Received.setText("");
        vitA2Administered.setText("");
        vitA2Wastage.setText("");
        vitA2StockInHand.setText("");

    }

    public void calculateWastageSafeInjection(){

        int ads05Opening, ads05Received, ads05Used, ads05StockInHand, mlOpening, mlUsed, mlReceived, mlStockInHand, dilOpening, dilReceived, dilUsed, dilStockInHand, safetyBoxOpening, safetyBoxReceived, safetyBoxUsed, safetyBoxStockInHand;
        ads05Opening    = Integer.parseInt(strads05Balance);
        ads05Received   = Integer.parseInt(strads05Received);
        ads05Used       = Integer.parseInt(strads05Used);
        ads05StockInHand    = Integer.parseInt(strads05StockInHand);

        mlOpening       = Integer.parseInt(strml005Balance);
        mlReceived      = Integer.parseInt(strml005Received);
        mlUsed          = Integer.parseInt(strml005Used);
        mlStockInHand   = Integer.parseInt(strml005StockInHand);

        dilOpening      = Integer.parseInt(strDillutionBalance);
        dilReceived     = Integer.parseInt(strDillutionReceived);
        dilUsed         = Integer.parseInt(strDillutionUsed);
        dilStockInHand  = Integer.parseInt(strDillutionStockInHand);

        safetyBoxOpening    = Integer.parseInt(strSafetyBoxBalance);
        safetyBoxReceived   = Integer.parseInt(strSafetyBoxReceived);
        safetyBoxUsed       = Integer.parseInt(strSafetyBoxUsed);
        safetyBoxStockInHand= Integer.parseInt(strSafetyBoxStockInHand);

        ads05Wastage.setText((((ads05Opening+ads05Received)-ads05Used)-ads05StockInHand)+"");
        ml005Wastage.setText((((mlOpening+mlReceived)-mlUsed)-mlStockInHand)+"");
        dillutionWastage.setText((((dilOpening+dilReceived)-dilUsed)-dilStockInHand)+"");
        safetyBoxWastage.setText((((safetyBoxOpening+safetyBoxReceived)-safetyBoxUsed)-safetyBoxStockInHand)+"");

    }

    public void calculateWastageVitaminA(){
        int vitA1Opening, vitA1Received, vitA1Administered, vitA1StockInAHand, vitA2Opening, vitA2Received, vitA2Administered, vitA2StockInAHand;

        vitA1Opening    = Integer.parseInt(strVitA1Opening);
        vitA1Received   = Integer.parseInt(strVitA1Received);
        vitA1Administered   = Integer.parseInt(strVitA1Administered);
        vitA1StockInAHand   = Integer.parseInt(strVitA1StockInHand);

        vitA2Opening    = Integer.parseInt(strVitA2Opening);
        vitA2Received   = Integer.parseInt(strVitA2Received);
        vitA2Administered   = Integer.parseInt(strVitA2Administered);
        vitA2StockInAHand   = Integer.parseInt(strVitA2StockInHand);

        vitA1Wastage.setText((((vitA1Opening+vitA1Received)-vitA1Administered)-vitA1StockInAHand)+"");
        vitA2Wastage.setText((((vitA2Opening+vitA2Received)-vitA2Administered)-vitA2StockInAHand)+"");
    }

    public void setupview(){

        //EditText

        ml005Balance    = (EditText) findViewById(R.id.ml005_open);
        ml005Received   = (EditText) findViewById(R.id.ml005_received);
        ml005Used       = (EditText) findViewById(R.id.ml005_used);
        ml005Wastage    = (EditText) findViewById(R.id.ml005_wastage);
        ml005Wastage.setTextColor(getResources().getColor(R.color.card_border));
        ml005Wastage.setEnabled(false);

        ml005StockInHand= (EditText) findViewById(R.id.ml005_stock_in_hand);
        ml005StockedOutDays = (EditText) findViewById(R.id.ml005_stocked_out_days);


        ads05Balance    = (EditText) findViewById(R.id.ads05_opening);
        ads05Received   = (EditText) findViewById(R.id.ads05_received);
        ads05Used       = (EditText) findViewById(R.id.ads05_used);
        ads05Wastage    = (EditText) findViewById(R.id.ads05_wastage);
        ads05Wastage.setEnabled(false);
        ads05Wastage.setTextColor(getResources().getColor(R.color.card_border));

        ads05StockInHand    = (EditText) findViewById(R.id.ads05_stock_in_hand);
        ads05StockedOutDays = (EditText) findViewById(R.id.ads05_stocked_out_days);
        dillutionBalance    = (EditText) findViewById(R.id.dillution_opening);
        dillutionReceived   = (EditText) findViewById(R.id.dillution_received);
        dillutionUsed       = (EditText) findViewById(R.id.dillution_used);
        dillutionWastage    = (EditText) findViewById(R.id.dillution_wastage);
        dillutionWastage.setEnabled(false);
        dillutionWastage.setTextColor(getResources().getColor(R.color.card_border));

        dillutionStockInHand    = (EditText) findViewById(R.id.dillution_stock_in_hand);
        dillutionStockedOutDays= (EditText) findViewById(R.id.dillution_stocked_out_hand);
        safetyBoxBalance    = (EditText) findViewById(R.id.safety_box_opening);
        safetyBoxReceived   = (EditText) findViewById(R.id.safety_box_received);
        safetyBoxUsed       = (EditText) findViewById(R.id.safety_box_used);
        safetyBoxWastage    = (EditText) findViewById(R.id.safety_box_wastage);
        safetyBoxWastage.setEnabled(false);
        safetyBoxWastage.setTextColor(getResources().getColor(R.color.card_border));

        safetyBoxStockInHand    = (EditText) findViewById(R.id.safety_box_stock_in_hand);
        safetyBoxStockedOutDays = (EditText) findViewById(R.id.safety_box_stocked_out_days);
        vitA1Opening        = (EditText) findViewById(R.id.vit_a_1_opening);
        vitA1Received       = (EditText) findViewById(R.id.vit_a_1_received);
        vitA1Administered   = (EditText) findViewById(R.id.vit_a_1_administered);
        vitA1Wastage        = (EditText) findViewById(R.id.vit_a_1_wastage);
        vitA1Wastage.setTextColor(getResources().getColor(R.color.card_border));
        vitA1Wastage.setEnabled(false);

        vitA1StockInHand    = (EditText) findViewById(R.id.vit_a_1_stock_in_hand);
        vitA2Opening        = (EditText) findViewById(R.id.vit_a_2_opening);
        vitA2Received       = (EditText) findViewById(R.id.vit_a_2_received);
        vitA2Administered   = (EditText) findViewById(R.id.vit_a_2_administered);
        vitA2Wastage        = (EditText) findViewById(R.id.vit_a_2_wastage);
        vitA2Wastage.setTextColor(getResources().getColor(R.color.card_border));
        vitA2Wastage.setEnabled(false);

        vitA2StockInHand    = (EditText) findViewById(R.id.vit_a_2_stock_in_hand);


        fixedConducted      = (EditText) findViewById(R.id.fixed_conducted);
        fixedConducted.setEnabled(false);
        outreachPlanned     = (EditText) findViewById(R.id.outreach_planned);
        outreachPlanned.setEnabled(false);
        outreachConducted   = (EditText) findViewById(R.id.outreach_conducted);
        outreachConducted.setEnabled(false);
        outreachCancelled   = (EditText) findViewById(R.id.outreach_cancelled);

        bcgFemaleServiceArea    = (EditText) findViewById(R.id.bcg_female_service);
        bcgMaleServiceArea      = (EditText) findViewById(R.id.bcg_male_service);
        bcgFemaleCatchmentArea  = (EditText) findViewById(R.id.bcg_female_catchment);
        bcgMaleCatchmentArea    = (EditText) findViewById(R.id.bcg_male_catchment);
        opvFemaleServiceArea    = (EditText) findViewById(R.id.opv_female_service);
        opvMaleServiceArea      = (EditText) findViewById(R.id.opv_male_service);
        opvFemaleCatchmentArea  = (EditText) findViewById(R.id.opv_female_catchment);
        opvMaleCatchmentArea    = (EditText) findViewById(R.id.opv_male_catchment);
        tt1FemaleServiceArea     = (EditText) findViewById(R.id.tt1_female_service);
        tt1MaleServiceArea       = (EditText) findViewById(R.id.tt1_male_service);
        tt1FemaleCatchmentArea   = (EditText) findViewById(R.id.tt1_female_catchment);
        tt1MaleCatchmentArea     = (EditText) findViewById(R.id.tt1_male_catchment);


        tt2FemaleServiceArea     = (EditText) findViewById(R.id.tt2_female_service);
        tt2MaleServiceArea       = (EditText) findViewById(R.id.tt2_male_service);
        tt2FemaleCatchmentArea   = (EditText) findViewById(R.id.tt2_female_catchment);
        tt2MaleCatchmentArea     = (EditText) findViewById(R.id.tt2_male_catchment);


        tt3FemaleServiceArea     = (EditText) findViewById(R.id.tt3_female_service);
        tt3MaleServiceArea       = (EditText) findViewById(R.id.tt3_male_service);
        tt3FemaleCatchmentArea   = (EditText) findViewById(R.id.tt3_female_catchment);
        tt3MaleCatchmentArea     = (EditText) findViewById(R.id.tt3_male_catchment);


        tt4FemaleServiceArea     = (EditText) findViewById(R.id.tt4_female_service);
        tt4MaleServiceArea       = (EditText) findViewById(R.id.tt4_male_service);
        tt4FemaleCatchmentArea   = (EditText) findViewById(R.id.tt4_female_catchment);
        tt4MaleCatchmentArea     = (EditText) findViewById(R.id.tt4_male_catchment);


        tt5FemaleServiceArea     = (EditText) findViewById(R.id.tt5_female_service);
        tt5MaleServiceArea       = (EditText) findViewById(R.id.tt5_male_service);
        tt5FemaleCatchmentArea   = (EditText) findViewById(R.id.tt5_female_catchment);
        tt5MaleCatchmentArea     = (EditText) findViewById(R.id.tt5_male_catchment);


        tt5CompletedFemaleServiceArea     = (EditText) findViewById(R.id.tt5_Completed_female_service);
        tt5CompletedMaleServiceArea       = (EditText) findViewById(R.id.tt5_Completed_male_service);
        tt5CompletedFemaleCatchmentArea   = (EditText) findViewById(R.id.tt5_Completed_female_catchment);
        tt5CompletedMaleCatchmentArea     = (EditText) findViewById(R.id.tt5_Completed_male_catchment);


        otherMajorImmunizationActivities    = (EditText) findViewById(R.id.other_major_immunization_activities_new);

        //MaterialSpinner
        monthYearSpinner    = (MaterialSpinner) findViewById(R.id.mon_year_spiner);
        yearSpinner         = (MaterialSpinner) findViewById(R.id.year_spinner);

        //Buttons
        suveillanceSubmit   = (Button) findViewById(R.id.surveillance_submit_button);
        refrigeratorSubmit  = (Button) findViewById(R.id.refrigerator_submit_button);
        immunizationButton  = (Button) findViewById(R.id.immunization_sessions_submit_button);
        vaccinationsButton  = (Button) findViewById(R.id.vaccinations_submit_button);
        otherMajorImmunizationActivitiesButton  = (Button) findViewById(R.id.major_immunization_submit_button);
        syringesSubmitButton    = (Button) findViewById(R.id.syringes_submit_button);
        vitaminASubmitButton    = (Button) findViewById(R.id.vitamin_submit_button);

        suveillanceSubmit   .
                setOnClickListener(this);
        refrigeratorSubmit  .
                setOnClickListener(this);
        immunizationButton  .
                setOnClickListener(this);
        vaccinationsButton  .
                setOnClickListener(this);
        syringesSubmitButton.
                setOnClickListener(this);
        vitaminASubmitButton.
                setOnClickListener(this);
        otherMajorImmunizationActivitiesButton.
                setOnClickListener(this);

        //TextViews
        feverCases      = (TextView) findViewById(R.id.fever_monthly_cases);
        feverDeaths     = (TextView) findViewById(R.id.fever_deaths);
        afpCases        = (TextView) findViewById(R.id.afp_monthly_cases);
        afpDeaths       = (TextView) findViewById(R.id.afp_deaths);
        tetanusCases    = (TextView) findViewById(R.id.tetanus_monthly_cases);
        tetanusDeaths   = (TextView) findViewById(R.id.tetanus_deaths);
        tempMax         = (TextView) findViewById(R.id.temp_max);
        tempMin         = (TextView) findViewById(R.id.temp_min);
        alarmHigh       = (TextView) findViewById(R.id.alarm_high);
        alarmLow        = (TextView) findViewById(R.id.alarm_low);
        lastMonthTitle  = (TextView) findViewById(R.id.last_month_title);
        toolbarTitle    = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(HomeActivityRevised.Roboto_Regular);

        refrigderatorTitle  = (TextView) findViewById(R.id.refrigerator_txt);
        refrigderatorTitle.setTypeface(HomeActivityRevised.Rosario_Regular);
        surveillanceTitle   = (TextView) findViewById(R.id.surveillance_txt);
        surveillanceTitle.setTypeface(HomeActivityRevised.Rosario_Regular);

        pbar                = (ProgressBar) findViewById(R.id.immunization_session_progress_bar);
        sessionsLayouts     = (LinearLayout) findViewById(R.id.immunizationSessinonsLayouts);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.surveillance_submit_button:
                resetValues();
                if (verifyInputs()){
                    getValues();
                    if (saveDeseasesSurveillance()){
                        //Saved Successfully
                        sayThis("Desease Surveillance saved Successfully", 2);
                    }else {
                        //TODO Error, Save Failed
                    }
                }else {
                    //TODO Error Please Fill in all fields
                }
                break;
            case R.id.refrigerator_submit_button:
                resetValues();
                if (verifyInputs2()){
                    getValues2();
                    if (saveRefrigeratorValues()){
                        sayThis("Cold chain saved Successfully", 2);
                    }
                }
                break;
            case R.id.immunization_sessions_submit_button:
                resetValues();
                if (verifyInputs3()){
                    getValue3();
                    if (saveImmunizationSessionValue()){
                        sayThis("Immunization session saved successfully", 2);
                    }
                }
                break;
            case R.id.vaccinations_submit_button:
                resetValues();
                if (verifyInputs4()){
                    getValue4();
                    if (saveVaccinationsValues()){
                        sayThis("Vaccinations Saved Successfully", 2);
                    }
                }
                break;
            case R.id.major_immunization_submit_button:
                resetValues();
                if (verifyInputs5()){
                    getValue5();
                    if (saveOtherImmunizationActivities()){
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Other Immunization Activities Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
                break;
            case R.id.syringes_submit_button:
                resetValues();
                if (verifyInputs6()){
                    getValue6();
                    calculateWastageSafeInjection();
                    if (saveInjectionEquipments()){
                        sayThis("Syringes and Safety boxes saved successfully", 2);
                    }
                }
                break;
            case R.id.vitamin_submit_button:
                resetValues();
                if (verifyInputs7()){
                    getValue7();
                    calculateWastageVitaminA();
                    if (saveVitaminAStock()){
                        sayThis("Vitamins Data Saved Successfully", 2);
                    }
                }
                break;
        }
    }

    public void sayThis(String message, int code){
        dialogueMessage.setText(message);
        if (code == 1){
            dialogueMessage.setTextColor(getResources().getColor(R.color.red_600));
        }else if (code == 2){
            dialogueMessage.setTextColor(getResources().getColor(R.color.green_600));
        }

        dialog.show();
    }


    @Override
    public void onBackPressed(){
        //dont leave the acitivity
    }



}
