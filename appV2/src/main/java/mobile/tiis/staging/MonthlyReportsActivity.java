package mobile.tiis.staging;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.adapters.PlacesOfBirthAdapter;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.fragments.ChildWeightPagerFragment;
import mobile.tiis.staging.fragments.RegisterChildFragment;

public class MonthlyReportsActivity extends AppCompatActivity {

    //UI ELEMENTS
    private Button suveillanceSubmit, refrigeratorSubmit;
    private TextView feverCases, feverDeaths, afpCases, afpDeaths, tetanusCases, tetanusDeaths,tempMax, tempMin, alarmHigh, alarmLow, lastMonthTitle, surveillanceTitle, refrigderatorTitle;
    private MaterialSpinner monthYearSpinner;

    //Variables
    private String strFeverCases, strFeverDeaths, strAfpCases, strAfpDeaths, strTetanusCases, strTetanusDeaths;
    private String strTempMax, strTempMin, strAlarmHigh, strAlarmLow;

    private List<String> monthYear = new ArrayList<>();

    private DatabaseHandler mydb;
    private BackboneApplication app;

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

        monthYear.add("Jan 2016");
        monthYear.add("Feb 2016");
        monthYear.add("Mar 2016");
        monthYear.add("Apr 2016");
        monthYear.add("May 2016");
        monthYear.add("Jun 2016");
        monthYear.add("Jul 2016");
        monthYear.add("Aug 2016");
        monthYear.add("Sep 2016");
        monthYear.add("Oct 2016");
        monthYear.add("Nov 2016");
        monthYear.add("Dec 2016");

        PlacesOfBirthAdapter adapter   = new PlacesOfBirthAdapter(this, R.layout.single_text_spinner_item_drop_down, monthYear);
        monthYearSpinner.setAdapter(adapter);

        suveillanceSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetValues();
                if (verifyInputs()){
                    getValues();
                    if (saveValues()){
                        //Saved Successfully
                        Toast.makeText(MonthlyReportsActivity.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                    }else {
                        //TODO Error, Save Failed
                    }
                }else {
                    //TODO Error Please Fill in all fields
                }

            }
        });

        refrigeratorSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

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

    public boolean saveValues(){
        ContentValues cv = new ContentValues();
        cv.put(SQLHandler.SurveillanceColumns.FEVER_MONTHLY_CASES, strFeverCases);
        cv.put(SQLHandler.SurveillanceColumns.FEVER_DEATHS, strFeverDeaths);
        cv.put(SQLHandler.SurveillanceColumns.APF_MONTHLY_CASES, strAfpCases);
        cv.put(SQLHandler.SurveillanceColumns.APF_DEATHS, strAfpDeaths);
        cv.put(SQLHandler.SurveillanceColumns.NEONATAL_TT_CASES, strTetanusCases);
        cv.put(SQLHandler.SurveillanceColumns.NEONATAL_TT_DEATHS, strTetanusDeaths);
        cv.put(SQLHandler.SurveillanceColumns.REPORTED_MONTH, monthYearSpinner.getSelectedItem().toString());

        //Save Surveillance to local DB
        DatabaseHandler db = new DatabaseHandler(this);
        db.addDeseaseSurveillance(cv);

        return true;

    }

    public boolean saveRefrigeratorValues(){
        ContentValues cv = new ContentValues();
        cv.put(SQLHandler.RefrigeratorColums.ALARM_HIGH_TEMP, strAlarmHigh);
        cv.put(SQLHandler.RefrigeratorColums.ALARM_LOW_TEMP, strAlarmLow);
        cv.put(SQLHandler.RefrigeratorColums.TEMP_MAX, strTempMax);
        cv.put(SQLHandler.RefrigeratorColums.TEMP_MIN, strTempMin);
        cv.put(SQLHandler.RefrigeratorColums.REPORTED_MONTH, monthYearSpinner.getSelectedItem().toString());

        DatabaseHandler db = new DatabaseHandler(this);
        db.addRefrigeratorTemperature(cv);

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
    }

    public void setupview(){
        //MaterialSpinner
        monthYearSpinner    = (MaterialSpinner) findViewById(R.id.mon_year_spiner);

        //Buttons
        suveillanceSubmit   = (Button) findViewById(R.id.surveillance_submit_button);
        refrigeratorSubmit  = (Button) findViewById(R.id.refrigerator_submit_button);

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

        refrigderatorTitle  = (TextView) findViewById(R.id.refrigerator_txt);
        refrigderatorTitle  .setTypeface(HomeActivityRevised.Rosario_Regular);
        surveillanceTitle   = (TextView) findViewById(R.id.surveillance_txt);
        surveillanceTitle   .setTypeface(HomeActivityRevised.Rosario_Regular);
    }

}
