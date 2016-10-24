package mobile.tiis.staging;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.adapters.PlacesOfBirthAdapter;
import mobile.tiis.staging.adapters.SpinnerAdapter;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.GIISContract;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.MonthEntity;

import java.util.Calendar;

import static mobile.tiis.staging.base.BackboneApplication.CHILD_MANAGEMENT_SVC;
import static mobile.tiis.staging.base.BackboneApplication.HEALTH_FACILITY_SVC;
import static mobile.tiis.staging.base.BackboneApplication.WCF_URL;
import static mobile.tiis.staging.util.Constants.ADS_O5ML;
import static mobile.tiis.staging.util.Constants.ADS_OO5ML;
import static mobile.tiis.staging.util.Constants.SAFETY_BOXES;
import static mobile.tiis.staging.util.Constants.SDILLUTION;
import static mobile.tiis.staging.util.Constants.VITAMIN_A_100000_IU;
import static mobile.tiis.staging.util.Constants.VITAMIN_A_200000_IU;

public class MonthlyReportsActivity extends AppCompatActivity implements View.OnClickListener {

    //UI ELEMENTS
    private Button suveillanceSubmit, refrigeratorSubmit, immunizationButton, vaccinationsButton, otherMajorImmunizationActivitiesButton, syringesSubmitButton, vitaminASubmitButton;
    private TextView feverCases, feverDeaths, afpCases, afpDeaths, tetanusCases, tetanusDeaths,tempMax, tempMin, alarmHigh, alarmLow, lastMonthTitle, surveillanceTitle, refrigderatorTitle;
    private MaterialSpinner monthYearSpinner, yearSpinner;
    private TextView toolbarTitle;
    private EditText fixedConducted, outreachPlanned, outreachConducted, outreachCancelled;
    private EditText bcgFemaleServiceArea, bcgMaleServiceArea, bcgFemaleCatchmentArea, bcgMaleCatchmentArea, opvFemaleServiceArea, opvMaleServiceArea, opvFemaleCatchmentArea, opvMaleCatchmentArea;
    private EditText ttFemaleServiceArea, ttMaleServiceArea, ttFemaleCatchmentArea, ttMaleCatchmentArea;
    private EditText otherMajorImmunizationActivities, otherMajorImmunizationActivitiesNew;
    private EditText ml005Balance, ml005Received, ml005Used, ml005Wastage, ml005StockInHand, ml005StockedOutDays;
    private EditText ads05Balance, ads05Received, ads05Used, ads05Wastage, ads05StockInHand, ads05StockedOutDays;
    private EditText dillutionBalance, dillutionReceived, dillutionUsed, dillutionWastage, dillutionStockInHand, dillutionStockedOutDays;
    private EditText safetyBoxBalance, safetyBoxReceived, safetyBoxUsed, safetyBoxWastage, safetyBoxStockInHand, safetyBoxStockedOutDays;
    private EditText vitA1Opening, vitA1Received, vitA1Administered, vitA1Wastage, vitA1StockInHand;
    private EditText vitA2Opening, vitA2Received, vitA2Administered, vitA2Wastage, vitA2StockInHand;

    //Variables
    private String strFeverCases, strFeverDeaths, strAfpCases, strAfpDeaths, strTetanusCases, strTetanusDeaths;
    private String strTempMax, strTempMin, strAlarmHigh, strAlarmLow;
    private String strFixedConducted, strOutreachConducted, strOutreachPlanned, strOutreachCancelled, strOtherMajorImmunizationActivitiesNew;
    private String strBcgFemaleService, strBcgMaleService, strBcgFemaleCatchment, strBcgMaleCatchment, strOpvFemaleService, strOpvMaleService, strOpvFemaleCatchment, strOpvMaleCatchment;
    private String strTtFemaleService, strTtMaleServicec, strTtFemaleCatchment, strTtMaleCatchment, strOtherMajorImmunizationActivities;
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

    private MonthEntity currentSelectedMonth;
    private String currentlySelectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_reports);
        setupview();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        app = (BackboneApplication) this.getApplication();
        mydb = app.getDatabaseInstance();

        setLastMonthReported();

        Date now  = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        int month   = cal.get(Calendar.MONTH);
        int year    = cal.get(Calendar.YEAR);

        years.add("2015");
        years.add("2016");
        years.add("2017");
        years.add("2018");
        years.add("2019");
        years.add("2020");

        monthEntities.add(new MonthEntity("January", "0"));
        monthEntities.add(new MonthEntity("February", "1"));
        monthEntities.add(new MonthEntity("March", "2"));
        monthEntities.add(new MonthEntity("April", "3"));
        monthEntities.add(new MonthEntity("May", "4"));
        monthEntities.add(new MonthEntity("June", "5"));
        monthEntities.add(new MonthEntity("July", "6"));
        monthEntities.add(new MonthEntity("August", "7"));
        monthEntities.add(new MonthEntity("September", "8"));
        monthEntities.add(new MonthEntity("October", "9"));
        monthEntities.add(new MonthEntity("November", "10"));
        monthEntities.add(new MonthEntity("December", "11"));

        PlacesOfBirthAdapter adapter = new PlacesOfBirthAdapter(this, R.layout.single_text_spinner_dropdown_toolbar,years);
        yearSpinner.setAdapter(adapter);

        for (int i=0; i<years.size(); i++){
            if (years.get(i).equals(year+"")){
                yearSpinner.setSelection(i+1);
                currentlySelectedYear = years.get(i);
            }
        }


        SpinnerAdapter spinnerAdapter   = new SpinnerAdapter(this, monthEntities);
        monthYearSpinner.setAdapter(spinnerAdapter);

        for (int i =0; i<monthEntities.size(); i++){
            if (Integer.parseInt(monthEntities.get(i).getMonth_number()) == month){
                monthYearSpinner.setSelection(i+1);
                currentSelectedMonth = monthEntities.get(i);
            }
        }

        Log.d("SOMA", currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        clearFields();
        checkdatabaseForAlreadyReportedFormsForThisMonth();

        monthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0){
                    currentSelectedMonth = monthEntities.get(i);
                    clearFields();
                    checkdatabaseForAlreadyReportedFormsForThisMonth();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void checkdatabaseForAlreadyReportedFormsForThisMonth(){
        querySurveillanceInformation();
        queryRefrigeratorTemperature();
        queryImmunizationSessions();
        queryVaccinationsBcgOpvTt();
        queryOtherImmunizationActivities();
        querySafeInjectionEquipments();
        queryVitaminAStock();
    }

    public void queryRefrigeratorTemperature(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.REFRIGERATOR_TEMPERATURE
                +" WHERE "+ SQLHandler.RefrigeratorColums.REPORTED_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.moveToFirst()){
            tempMax.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.TEMP_MAX)));
            tempMin.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.TEMP_MIN)));
            alarmLow.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.ALARM_LOW_TEMP)));
            alarmHigh.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.RefrigeratorColums.ALARM_HIGH_TEMP)));

        }
    }

    public void querySurveillanceInformation(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.DESEASES_SURVEILLANCE
                +" WHERE "+ SQLHandler.SurveillanceColumns.REPORTED_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.moveToFirst()){
            feverCases.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.FEVER_MONTHLY_CASES)));
            feverDeaths.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.FEVER_DEATHS)));
            afpCases.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.APF_MONTHLY_CASES)));
            afpDeaths.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.APF_DEATHS)));
            tetanusCases.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.NEONATAL_TT_CASES)));
            tetanusDeaths.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SurveillanceColumns.NEONATAL_TT_DEATHS)));

        }
    }

    public void queryImmunizationSessions(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.IMMUNIZATION_SESSION
                +" WHERE "+ SQLHandler.ImmunizationSessionColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.moveToFirst()){
            fixedConducted.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.FIXED_CONDUCTED)));
            outreachPlanned.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_PLANNED)));
            outreachConducted.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_CONDUCTED)));
            outreachCancelled.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OUTREACH_CANCELLED)));
//            otherMajorImmunizationActivitiesNew.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.ImmunizationSessionColumns.OTHERACTIVITIES)));
        }
    }

    public void queryVaccinationsBcgOpvTt(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.VACCINATIONS_BCG_OPV_TT
                +" WHERE "+ SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME)).equals("BCG")){
                    bcgMaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    bcgFemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    bcgMaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    bcgFemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME)).equals("OPV")){
                    opvMaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    opvFemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    opvMaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    opvFemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME)).equals("TT")){
                    ttMaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA)));
                    ttFemaleCatchmentArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA)));
                    ttMaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA)));
                    ttFemaleServiceArea.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA)));
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
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(ADS_OO5ML)){
                    ml005Balance.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    ml005Received.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    ml005Used.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    ml005Wastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    ml005StockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    ml005StockedOutDays.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(ADS_O5ML)){
                    ads05Balance.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    ads05Received.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    ads05Used.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    ads05Wastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    ads05StockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    ads05StockedOutDays.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(SDILLUTION)){
                    dillutionBalance.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    dillutionReceived.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    dillutionUsed.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    dillutionWastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    dillutionStockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    dillutionStockedOutDays.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME)).equals(SAFETY_BOXES)){
                    safetyBoxBalance.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE)));
                    safetyBoxReceived.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED)));
                    safetyBoxUsed.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.USED)));
                    safetyBoxWastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE)));
                    safetyBoxStockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND)));
                    safetyBoxStockedOutDays.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS)));
                }

            }while (cursor.moveToNext());
        }
    }

    public void queryVitaminAStock(){
        String query = "SELECT * FROM "+ SQLHandler.Tables.HF_VITAMIN_A
                +" WHERE "+ SQLHandler.HfVitaminAColumns.REPORTING_MONTH+" = '"+currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear+"'";
        Log.d("SOMA", "Query is : "+query);
        SQLiteDatabase db = mydb.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("SOMA", "cursor size "+cursor.getCount());
        if (cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.VITAMIN_NAME)).equals(VITAMIN_A_100000_IU)){
                    vitA1Opening.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.OPENING_BALANCE)));
                    vitA1Received.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.RECEIVED)));
                    vitA1Administered.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED)));
                    vitA1Wastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.WASTAGE)));
                    vitA1StockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND)));
                }
                else if (cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.VITAMIN_NAME)).equals(VITAMIN_A_200000_IU)){
                    vitA2Opening.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.OPENING_BALANCE)));
                    vitA2Received.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.RECEIVED)));
                    vitA2Administered.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED)));
                    vitA2Wastage.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.WASTAGE)));
                    vitA2StockInHand.setText(cursor.getString(cursor.getColumnIndex(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND)));
                }

            }while (cursor.moveToNext());
        }
    }

    public void setLastMonthReported(){

    }

    public boolean verifyInputs(){

        if (feverCases.getText().toString().equals("")){
            feverCases.setError("Fill This");
            return false;
        }
        else if (feverDeaths.getText().toString().equals("")){
            feverDeaths.setError("Fill This");
            return false;
        }
        else if (afpCases.getText().toString().equals("")){
            afpCases.setError("Fill This");
            return false;
        }
        else if (afpDeaths.getText().toString().equals("")) {
            afpDeaths.setError("Fill This");
            return false;
        }
        else if (tetanusCases.getText().toString().equals("")){
            tetanusCases.setError("Fill This");
            return false;
        }
        else if (tetanusDeaths.getText().toString().equals("")){
            tetanusDeaths.setError("Fill This");
            return false;
        }
        else if (monthYearSpinner.getSelectedItemPosition() <= 0){
            monthYearSpinner.setError("Select Reporting Month");
            monthYearSpinner.setErrorColor(getResources().getColor(R.color.red_500));
            return false;
        }
        else
            return true;
    }

    public boolean verifyInputs2(){
        if (tempMin.getText().toString().equals(""))
            return false;
        else if (tempMax.getText().toString().equals(""))
            return false;
        else if (alarmHigh.getText().toString().equals(""))
            return false;
        else if (alarmLow.getText().toString().equals(""))
            return false;
        else if (monthYearSpinner.getSelectedItemPosition() <= 0)
            return false;
        else
            return true;
    }

    public boolean verifyInputs3(){
        if(fixedConducted.getText().toString().trim().equals(""))
            return false;
        if (outreachPlanned.getText().toString().trim().equals(""))
            return false;
        if (outreachConducted.getText().toString().trim().equals(""))
            return false;
        if (outreachCancelled.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public boolean verifyInputs4(){
        if(bcgFemaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if(bcgMaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if(bcgFemaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        if(bcgMaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        if (opvFemaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if(opvMaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if(opvFemaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        if(opvMaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        if (ttFemaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if (ttMaleServiceArea.getText().toString().trim().equals(""))
            return false;
        if(ttFemaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        if(ttMaleCatchmentArea.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public boolean verifyInputs5(){
        if (otherMajorImmunizationActivities.getText().toString().equals(""))
            return false;
        else return true;
    }

    public boolean verifyInputs6(){
        if (ml005Balance.getText().toString().equals(""))
            return false;
        if (ml005Received.getText().toString().equals(""))
            return false;
        if (ml005Used.getText().toString().equals(""))
            return false;
        if (ml005Wastage.getText().toString().equals(""))
            return false;
        if (ml005StockInHand.getText().toString().equals(""))
            return false;
        if (ml005StockedOutDays.getText().toString().equals(""))
            return false;
        if (ads05Balance.getText().toString().equals(""))
            return false;
        if (ads05Received.getText().toString().equals(""))
            return false;
        if (ads05Used.getText().toString().equals(""))
            return false;
        if (ads05Wastage.getText().toString().equals(""))
            return false;
        if (ads05StockInHand.getText().toString().equals(""))
            return false;
        if (ads05StockedOutDays.getText().toString().equals(""))
            return false;
        if (dillutionBalance.getText().toString().equals(""))
            return false;
        if (dillutionReceived.getText().toString().equals(""))
            return false;
        if (dillutionUsed.getText().toString().equals(""))
            return false;
        if (dillutionWastage.getText().toString().equals(""))
            return false;
        if (dillutionStockInHand.getText().toString().equals(""))
            return false;
        if (dillutionStockedOutDays.getText().toString().equals(""))
            return false;
        if (safetyBoxBalance.getText().toString().equals(""))
            return false;
        if (safetyBoxReceived.getText().toString().equals(""))
            return false;
        if (safetyBoxUsed.getText().toString().equals(""))
            return false;
        if (safetyBoxWastage.getText().toString().equals(""))
            return false;
        if (safetyBoxStockInHand.getText().toString().equals(""))
            return false;
        if (safetyBoxStockedOutDays.getText().toString().equals(""))
            return false;
        else
            return true;
    }

    public boolean verifyInputs7(){
        if (vitA1Opening.getText().toString().equals(""))
            return false;
        if (vitA1Received.getText().toString().equals(""))
            return false;
        if (vitA1Administered.getText().toString().equals(""))
            return false;
        if (vitA1Wastage.getText().toString().equals(""))
            return false;
        if (vitA1StockInHand.getText().toString().equals(""))
            return false;

        if (vitA2Opening.getText().toString().equals(""))
            return false;
        if (vitA2Received.getText().toString().equals(""))
            return false;
        if (vitA2Administered.getText().toString().equals(""))
            return false;
        if (vitA2Wastage.getText().toString().equals(""))
            return false;
        if (vitA2StockInHand.getText().toString().equals(""))
            return false;

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
        strOutreachCancelled    = outreachCancelled.getText().toString().trim();
        strOtherMajorImmunizationActivitiesNew  = otherMajorImmunizationActivitiesNew.getText().toString().trim();
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

        strTtFemaleService      = ttFemaleServiceArea.getText().toString().trim();
        strTtMaleServicec       = ttMaleServiceArea.getText().toString().trim();
        strTtFemaleCatchment    = ttFemaleCatchmentArea.getText().toString().trim();
        strTtMaleCatchment      = ttMaleCatchmentArea.getText().toString().trim();

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
        db.addDeseaseSurveillance(cv);

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
        db.addRefrigeratorTemperature(cv);

        //TODO Create Service URL and send it to postmast

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

        ContentValues cv    = new ContentValues();
        cv.put(GIISContract.SyncColumns.UPDATED, 1);
        cv.put(SQLHandler.ImmunizationSessionColumns.FIXED_CONDUCTED, strFixedConducted);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_CANCELLED, strOutreachCancelled);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_CONDUCTED, strOutreachConducted);
        cv.put(SQLHandler.ImmunizationSessionColumns.OUTREACH_PLANNED, strOutreachPlanned);
        cv.put(SQLHandler.ImmunizationSessionColumns.OTHERACTIVITIES, strOtherMajorImmunizationActivitiesNew);
        cv.put(SQLHandler.ImmunizationSessionColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);

        DatabaseHandler db = new DatabaseHandler(this);
        db.addImmunizationSessions(cv);

        return true;
    }

    public boolean saveVaccinationsValues(){

        DatabaseHandler db = new DatabaseHandler(this);

        ContentValues bcgCv    = new ContentValues();
        bcgCv.put(GIISContract.SyncColumns.UPDATED, 1);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME, "BCG");
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strBcgFemaleService);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strBcgMaleService);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strBcgFemaleCatchment);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strBcgMaleCatchment);
        bcgCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addVaccinationsBcgOpvTt(bcgCv);

        ContentValues opvCv = new ContentValues();
        opvCv.put(GIISContract.SyncColumns.UPDATED, 1);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME, "OPV");
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strOpvFemaleService);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strOpvMaleService);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strOpvFemaleCatchment);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strOpvMaleCatchment);
        opvCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addVaccinationsBcgOpvTt(opvCv);

        ContentValues ttCv = new ContentValues();
        ttCv.put(GIISContract.SyncColumns.UPDATED, 1);
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.DOSE_NAME, "TT");
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_SERVICE_AREA, strTtFemaleService);
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_SERVICE_AREA, strTtMaleServicec);
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.FEMALE_CATCHMENT_AREA, strTtFemaleCatchment);
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.MALE_CATCHMENT_AREA, strTtMaleCatchment);
        ttCv.put(SQLHandler.VaccinationsBcgOpvTtColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addVaccinationsBcgOpvTt(ttCv);

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

        ContentValues ads005Cv    = new ContentValues();
        ads005Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, ADS_OO5ML);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strml005Balance);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strml005Received);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strml005Used);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, strml005Wastage);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strml005StockInHand);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strml005StockedOutDays);
        ads005Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addInjectionEquipment(ads005Cv);

        ContentValues ads05Cv    = new ContentValues();
        ads05Cv.put(GIISContract.SyncColumns.UPDATED, 1);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, ADS_O5ML);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strads05Balance);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strads05Received);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strads05Used);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, strads05Wastage);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strads05StockInHand);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strads05StockedOutDays);
        ads05Cv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addInjectionEquipment(ads05Cv);


        ContentValues dillutionCv    = new ContentValues();
        dillutionCv.put(GIISContract.SyncColumns.UPDATED, 1);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, SDILLUTION);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strDillutionBalance);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strDillutionReceived);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strDillutionUsed);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, strDillutionWastage);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strDillutionStockInHand);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strDillutionStockedOutDays);
        dillutionCv.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addInjectionEquipment(dillutionCv);


        ContentValues safetyBoxesCV    = new ContentValues();
        safetyBoxesCV.put(GIISContract.SyncColumns.UPDATED, 1);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.ITEM_NAME, SAFETY_BOXES);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.OPENING_BALANCE, strSafetyBoxBalance);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.RECEIVED, strSafetyBoxReceived);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.USED, strSafetyBoxUsed);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.WASTAGE, strSafetyBoxWastage);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCK_AT_HAND, strSafetyBoxStockInHand);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.STOCKED_OUT_DAYS, strSafetyBoxStockedOutDays);
        safetyBoxesCV.put(SQLHandler.SyringesAndSafetyBoxesColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addInjectionEquipment(safetyBoxesCV);

        return true;
    }

    public boolean saveVitaminAStock(){
        DatabaseHandler db = new DatabaseHandler(this);

        ContentValues vitA1    = new ContentValues();
        vitA1.put(GIISContract.SyncColumns.UPDATED, 1);
        vitA1.put(SQLHandler.HfVitaminAColumns.VITAMIN_NAME, VITAMIN_A_100000_IU);
        vitA1.put(SQLHandler.HfVitaminAColumns.OPENING_BALANCE, strVitA1Opening);
        vitA1.put(SQLHandler.HfVitaminAColumns.RECEIVED, strVitA1Received);
        vitA1.put(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED, strVitA1Administered);
        vitA1.put(SQLHandler.HfVitaminAColumns.WASTAGE, strVitA1Wastage);
        vitA1.put(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND, strVitA1StockInHand);
        vitA1.put(SQLHandler.HfVitaminAColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addVitaminAStock(vitA1);

        ContentValues vitA2    = new ContentValues();
        vitA2.put(GIISContract.SyncColumns.UPDATED, 1);
        vitA2.put(SQLHandler.HfVitaminAColumns.VITAMIN_NAME, VITAMIN_A_200000_IU);
        vitA2.put(SQLHandler.HfVitaminAColumns.OPENING_BALANCE, strVitA2Opening);
        vitA2.put(SQLHandler.HfVitaminAColumns.RECEIVED, strVitA2Received);
        vitA2.put(SQLHandler.HfVitaminAColumns.TOTAL_ADMINISTERED, strVitA2Administered);
        vitA2.put(SQLHandler.HfVitaminAColumns.WASTAGE, strVitA2Wastage);
        vitA2.put(SQLHandler.HfVitaminAColumns.STOCK_ON_HAND, strVitA2StockInHand);
        vitA2.put(SQLHandler.HfVitaminAColumns.REPORTING_MONTH, currentSelectedMonth.getMonth_name()+" "+currentlySelectedYear);
        db.addVitaminAStock(vitA2);

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
        strTtFemaleService = "";
        strTtMaleServicec = "";
        strTtFemaleCatchment = "";
        strTtMaleCatchment = "";
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
        ttFemaleCatchmentArea.setText("");
        ttFemaleServiceArea.setText("");
        ttMaleCatchmentArea.setText("");
        ttMaleServiceArea.setText("");

        otherMajorImmunizationActivities.setText("");
        otherMajorImmunizationActivitiesNew.setText("");
    }

    public void setupview(){

        //EditText

        ml005Balance    = (EditText) findViewById(R.id.ml005_open);
        ml005Received   = (EditText) findViewById(R.id.ml005_received);
        ml005Used       = (EditText) findViewById(R.id.ml005_used);
        ml005Wastage    = (EditText) findViewById(R.id.ml005_wastage);
        ml005StockInHand= (EditText) findViewById(R.id.ml005_stock_in_hand);
        ml005StockedOutDays = (EditText) findViewById(R.id.ml005_stocked_out_days);


        ads05Balance    = (EditText) findViewById(R.id.ads05_opening);
        ads05Received   = (EditText) findViewById(R.id.ads05_received);
        ads05Used       = (EditText) findViewById(R.id.ads05_used);
        ads05Wastage    = (EditText) findViewById(R.id.ads05_wastage);
        ads05StockInHand    = (EditText) findViewById(R.id.ads05_stock_in_hand);
        ads05StockedOutDays = (EditText) findViewById(R.id.ads05_stocked_out_days);
        dillutionBalance    = (EditText) findViewById(R.id.dillution_opening);
        dillutionReceived   = (EditText) findViewById(R.id.dillution_received);
        dillutionUsed       = (EditText) findViewById(R.id.dillution_used);
        dillutionWastage    = (EditText) findViewById(R.id.dillution_wastage);
        dillutionStockInHand    = (EditText) findViewById(R.id.dillution_stock_in_hand);
        dillutionStockedOutDays= (EditText) findViewById(R.id.dillution_stocked_out_hand);
        safetyBoxBalance    = (EditText) findViewById(R.id.safety_box_opening);
        safetyBoxReceived   = (EditText) findViewById(R.id.safety_box_received);
        safetyBoxUsed       = (EditText) findViewById(R.id.safety_box_used);
        safetyBoxWastage    = (EditText) findViewById(R.id.safety_box_wastage);
        safetyBoxStockInHand    = (EditText) findViewById(R.id.safety_box_stock_in_hand);
        safetyBoxStockedOutDays = (EditText) findViewById(R.id.safety_box_stocked_out_days);
        vitA1Opening        = (EditText) findViewById(R.id.vit_a_1_opening);
        vitA1Received       = (EditText) findViewById(R.id.vit_a_1_received);
        vitA1Administered   = (EditText) findViewById(R.id.vit_a_1_administered);
        vitA1Wastage        = (EditText) findViewById(R.id.vit_a_1_wastage);
        vitA1StockInHand    = (EditText) findViewById(R.id.vit_a_1_stock_in_hand);
        vitA2Opening        = (EditText) findViewById(R.id.vit_a_2_opening);
        vitA2Received       = (EditText) findViewById(R.id.vit_a_2_received);
        vitA2Administered   = (EditText) findViewById(R.id.vit_a_2_administered);
        vitA2Wastage        = (EditText) findViewById(R.id.vit_a_2_wastage);
        vitA2StockInHand    = (EditText) findViewById(R.id.vit_a_2_stock_in_hand);


        fixedConducted      = (EditText) findViewById(R.id.fixed_conducted);
        outreachPlanned     = (EditText) findViewById(R.id.outreach_planned);
        outreachConducted   = (EditText) findViewById(R.id.outreach_conducted);
        outreachCancelled   = (EditText) findViewById(R.id.outreach_cancelled);

        bcgFemaleServiceArea    = (EditText) findViewById(R.id.bcg_female_service);
        bcgMaleServiceArea      = (EditText) findViewById(R.id.bcg_male_service);
        bcgFemaleCatchmentArea  = (EditText) findViewById(R.id.bcg_female_catchment);
        bcgMaleCatchmentArea    = (EditText) findViewById(R.id.bcg_male_catchment);
        opvFemaleServiceArea    = (EditText) findViewById(R.id.opv_female_service);
        opvMaleServiceArea      = (EditText) findViewById(R.id.opv_male_service);
        opvFemaleCatchmentArea  = (EditText) findViewById(R.id.opv_female_catchment);
        opvMaleCatchmentArea    = (EditText) findViewById(R.id.opv_male_catchment);
        ttFemaleServiceArea     = (EditText) findViewById(R.id.tt_female_service);
        ttMaleServiceArea       = (EditText) findViewById(R.id.tt_male_service);
        ttFemaleCatchmentArea   = (EditText) findViewById(R.id.tt_female_catchment);
        ttMaleCatchmentArea     = (EditText) findViewById(R.id.tt_male_catchment);

        otherMajorImmunizationActivities    = (EditText) findViewById(R.id.other_major_immunization_activities);
        otherMajorImmunizationActivitiesNew = (EditText) findViewById(R.id.other_major_immunization_activities_new);

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
        refrigderatorTitle  .setTypeface(HomeActivityRevised.Rosario_Regular);
        surveillanceTitle   = (TextView) findViewById(R.id.surveillance_txt);
        surveillanceTitle   .setTypeface(HomeActivityRevised.Rosario_Regular);
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
                        Toast.makeText(MonthlyReportsActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Refrigerator Information Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
                break;
            case R.id.immunization_sessions_submit_button:
                resetValues();
                if (verifyInputs3()){
                    getValue3();
                    if (saveImmunizationSessionValue()){
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Immunization Sessions Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
                break;
            case R.id.vaccinations_submit_button:
                resetValues();
                if (verifyInputs4()){
                    getValue4();
                    if (saveVaccinationsValues()){
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Vaccinations Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
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
                    if (saveInjectionEquipments()){
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Safe Injection Equipments Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
                break;
            case R.id.vitamin_submit_button:
                resetValues();
                if (verifyInputs7()){
                    getValue7();
                    if (saveVitaminAStock()){
                        Toast.makeText(
                                MonthlyReportsActivity.this,
                                "Vitamin A Stock Saved Successfully",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
                break;
        }
    }


}
