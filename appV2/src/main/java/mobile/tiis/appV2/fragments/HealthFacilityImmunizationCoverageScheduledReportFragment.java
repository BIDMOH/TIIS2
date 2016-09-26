package mobile.tiis.appV2.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobile.tiis.appV2.DatabaseModals.Dose;
import mobile.tiis.appV2.DatabaseModals.Scheduled_Vaccination;
import mobile.tiis.appV2.DatabaseModals.ViewChartData;
import mobile.tiis.appV2.DatabaseModals.ViewRow;
import mobile.tiis.appV2.R;
import mobile.tiis.appV2.base.BackboneApplication;
import mobile.tiis.appV2.database.DatabaseHandler;

/**
 * Created by issymac on 30/03/16.
 */
public class HealthFacilityImmunizationCoverageScheduledReportFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private TextView region, district, healthFacility, reportingPeriod, title;

    BackboneApplication app;

    private ProgressBar progressBar;

    private DatabaseHandler mydb;
    private View rowview,chartList,chart_view;
    private MaterialEditText metDOBFrom,metDOBTo;

    final DatePickerDialog fromDatePicker = new DatePickerDialog();
    final DatePickerDialog toDatePicker = new DatePickerDialog();

    private String toDateString="",fromDateString="";
    private EditText editTextUsedToRequestFocus;
    private LayoutInflater inflater;
    private LinearLayout chart_list;

    public static HealthFacilityImmunizationCoverageScheduledReportFragment newInstance(int position) {
        HealthFacilityImmunizationCoverageScheduledReportFragment f = new HealthFacilityImmunizationCoverageScheduledReportFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        app = (BackboneApplication) this.getActivity().getApplication();
        toDatePicker.setMaxDate(Calendar.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rowview = inflater.inflate(R.layout.fragment_health_facility_immunization_coverage_scheduled_report, null);

        TextView districtValue = (TextView)rowview.findViewById(R.id.district_value);
        districtValue.setText(app.getHealthFacilityDistrictName(app.getLOGGED_IN_USER_HF_ID()));

        editTextUsedToRequestFocus = (EditText) rowview.findViewById(R.id.edit_text_used_to_request_focus2);
        editTextUsedToRequestFocus.requestFocus();

        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));
//        reportingPeriod.setText(mydb.getUserHFIDByUserId(appV2.getLOGGED_IN_USER_ID()));

        return rowview;
    }

    public void prepareUIElements(View v){
        chart_view      = v.findViewById(R.id.chart_view);
        chart_list      = (LinearLayout)v.findViewById(R.id.chart_list);
        region          = (TextView) v.findViewById(R.id.region_value);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
        chartList     = v.findViewById(R.id.chartList);

        metDOBFrom              = (MaterialEditText) v.findViewById(R.id.met_dob_from);
        metDOBTo                = (MaterialEditText) v.findViewById(R.id.met_dob_value);


        metDOBTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePicker.show(((Activity) getActivity()).getFragmentManager(), "DatePickerDialogue");
                toDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        metDOBTo.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "-"
                                + year);

                        Calendar toCalendar = Calendar.getInstance();
                        toCalendar.set(year, monthOfYear, dayOfMonth);
                        fromDatePicker.setMaxDate(toCalendar);
                        toDateString = (toCalendar.getTimeInMillis() / 1000) + "";
                        editTextUsedToRequestFocus.requestFocus();

                        if (!fromDateString.equals("")) {
                            chart_view.setVisibility(View.VISIBLE);
                            new FilterList().execute(app.getLOGGED_IN_USER_HF_ID(), fromDateString, toDateString);
                        } else {
                            final Snackbar snackbar = Snackbar.make(rowview, "Please select a start date to view the chart", Snackbar.LENGTH_LONG);
                            snackbar.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
            }
        });


        metDOBFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePicker.show(((Activity) getActivity()).getFragmentManager(), "DatePickerDialogue");
                fromDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        metDOBFrom.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "-"
                                + year);

                        Calendar fromCalendar = Calendar.getInstance();
                        fromCalendar.set(year, monthOfYear, dayOfMonth);
                        toDatePicker.setMinDate(fromCalendar);
                        fromDateString = ((fromCalendar.getTimeInMillis() - 24*60*60*1000) / 1000) + "";
                        editTextUsedToRequestFocus.requestFocus();

                        if(!toDateString.equals("")){
                            chart_view.setVisibility(View.VISIBLE);
                            new FilterList().execute(app.getLOGGED_IN_USER_HF_ID(),fromDateString,toDateString);
                        }else{
                            final Snackbar snackbar=Snackbar.make(rowview,"Please select an end date to view the report",Snackbar.LENGTH_LONG);
                            snackbar.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
            }
        });

    }

    public class FilterList extends AsyncTask<String, Void, List<ViewChartData>> {



        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            chartList.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<ViewChartData> doInBackground(String... params) {

            SQLiteDatabase db = mydb.getReadableDatabase();

            String healthFacilityId = params[0];
            String fromDate ="";
            String toDate ="";

            try{
                fromDate = params[1];
                toDate = params[2];
            }catch (Exception e){
                e.printStackTrace();
            }



            String SQLScheduled_Vaccinations = "SELECT NAME,ID FROM scheduled_vaccination";
            Cursor cursor = db.rawQuery(SQLScheduled_Vaccinations,null);
            List<ViewChartData> viewChartDatas = new ArrayList<>();
            int size = cursor.getCount();

            for(int i=0;i<size;i++){
                cursor.moveToPosition(i);

                //creating a viewchart object
                ViewChartData viewChartData = new ViewChartData();

                //creating a schedule vaccination object from the queried cursor via Java reflection
                Scheduled_Vaccination scheduled_vaccination = new Scheduled_Vaccination();
                scheduled_vaccination.setModel(cursor, scheduled_vaccination);

                viewChartData.setScheduled_vaccination(scheduled_vaccination);


                List<ViewRow> rowList = new ArrayList<>();

                String SQLDose = "SELECT ID,FULLNAME,DOSE_NUMBER,SCHEDULED_VACCINATION_ID FROM dose WHERE SCHEDULED_VACCINATION_ID = '"+scheduled_vaccination.getId()+"' ORDER BY DOSE_NUMBER ASC";
                Cursor cursor1 = db.rawQuery(SQLDose, null);

                int size1= cursor1.getCount();
                for(int j=0;j<size1;j++){
                    ViewRow viewRow =new ViewRow();
                    cursor1.moveToPosition(j);
                    Dose dose = new Dose();
                    dose.setModel(cursor1, dose);

                    viewRow.setDose(dose);

                    String SQLWithinCatchmentTotal = "SELECT COUNT(DISTINCT(vaccination_event.ID)) AS number " +
                            "FROM vaccination_event,dose,child WHERE vaccination_event.DOSE_ID=dose.ID  " +
                            "AND child.ID=vaccination_event.CHILD_ID AND dose.ID = '"+dose.getId()+"' " +
                            "AND child.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND vaccination_event.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')";

                    Cursor c = db.rawQuery(SQLWithinCatchmentTotal,null);
                    c.moveToFirst();
                    viewRow.setExpectedCatchmentTotal(c.getInt(c.getColumnIndex("number")));


                    String SQLWithinCatchmentMale = "SELECT COUNT(DISTINCT(vaccination_event.ID)) AS number " +
                            "FROM vaccination_event,dose,child WHERE vaccination_event.DOSE_ID=dose.ID  " +
                            "AND child.ID=vaccination_event.CHILD_ID AND dose.ID = '"+dose.getId()+"' " +
                            "AND child.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND vaccination_event.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND vaccination_event.VACCINATION_STATUS = 'true'" +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')" +
                            "AND child.GENDER='true'";



                    Cursor cursor2 = db.rawQuery(SQLWithinCatchmentMale,null);
                    cursor2.moveToFirst();
                    viewRow.setWithinCatchmentMale(cursor2.getInt(cursor2.getColumnIndex("number")));


                    String SQLWithinCatchmentFemale = "SELECT COUNT(DISTINCT(vaccination_event.ID)) AS number " +
                            "FROM vaccination_event,dose,child WHERE vaccination_event.DOSE_ID=dose.ID  " +
                            "AND child.ID=vaccination_event.CHILD_ID " +
                            "AND child.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND vaccination_event.VACCINATION_STATUS = 'true'" +
                            "AND dose.ID = '"+dose.getId()+"' AND vaccination_event.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')" +
                            "AND child.GENDER='false'";

                    Cursor cursor3 = db.rawQuery(SQLWithinCatchmentFemale,null);
                    cursor3.moveToFirst();
                    viewRow.setWithinCatchmentFemale(cursor3.getInt(cursor3.getColumnIndex("number")));

                    String SQLOutsideCatchmentMale = "SELECT COUNT(DISTINCT(vaccination_event.ID)) AS number " +
                            "FROM vaccination_event,dose,child WHERE vaccination_event.DOSE_ID=dose.ID  " +
                            "AND child.ID=vaccination_event.CHILD_ID AND dose.ID = '"+dose.getId()+"' " +
                            "AND child.HEALTH_FACILITY_ID <> '"+healthFacilityId+"' " +
                            "AND vaccination_event.VACCINATION_STATUS = 'true'" +
                            "AND vaccination_event.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')" +
                            "AND child.GENDER='true'";


                    Log.d("coze", "catchmentMaleSql = " + SQLOutsideCatchmentMale);
                    Cursor cursor4 = db.rawQuery(SQLOutsideCatchmentMale,null);
                    cursor4.moveToFirst();
                    viewRow.setOutsideCatchmentMale(cursor4.getInt(cursor4.getColumnIndex("number")));

                    Log.d("coze", "catchmentMale = " + viewRow.getOutsideCatchmentMale());


                    String SQLOutsideCatchmentFemale = "SELECT COUNT(DISTINCT(vaccination_event.ID)) AS number " +
                            "FROM vaccination_event,dose,child WHERE vaccination_event.DOSE_ID=dose.ID  " +
                            "AND child.ID=vaccination_event.CHILD_ID AND dose.ID = '"+dose.getId()+"' " +
                            "AND child.HEALTH_FACILITY_ID <> '"+healthFacilityId+"' " +
                            "AND vaccination_event.HEALTH_FACILITY_ID = '"+healthFacilityId+"' " +
                            "AND vaccination_event.VACCINATION_STATUS = 'true'" +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')" +
                            "AND child.GENDER='false'";

                    Cursor cursor5 = db.rawQuery(SQLOutsideCatchmentFemale,null);
                    cursor5.moveToFirst();
                    viewRow.setOutsideCatchmentFemale(cursor5.getInt(cursor3.getColumnIndex("number")));


                    rowList.add(viewRow);

                }

                viewChartData.setViewRows(rowList);

                viewChartDatas.add(viewChartData);

            }





            return viewChartDatas;
        }

        @Override
        protected void onPostExecute(List<ViewChartData> result) {
            chart_list.removeAllViews();

            int size = result.size();
            for (int i=0;i<size;i++){
                ViewChartData viewChartData = result.get(i);
                LinearLayout row = (LinearLayout)inflater.inflate(R.layout.view_health_facility_immunization_coverage_row, null);
                ((TextView)row.findViewById(R.id.antigen)).setText(viewChartData.getScheduled_vaccination().getName());

                List<ViewRow> rowList= viewChartData.getViewRows();
                int size1 = rowList.size();
                for (int j =0; j<size1;j++){
                    ViewRow viewRow = rowList.get(j);
                    LinearLayout doseData = (LinearLayout)inflater.inflate(R.layout.view_health_facility_immunization_coverage_dosage_scheduled_row,null);


                    String dosageNumber = size1 == 1?(viewChartData.getScheduled_vaccination().getName()):(viewChartData.getScheduled_vaccination().getName() + " " + viewRow.getDose().getDose_number());
                    ((TextView) doseData.findViewById(R.id.dose)).setText(dosageNumber);

                    String maleWithin = viewRow.getWithinCatchmentMale()+"";
                    ((TextView)doseData.findViewById(R.id.male_within)).setText(maleWithin);


                    String femaleWithin = viewRow.getWithinCatchmentFemale() + "";
                    ((TextView) doseData.findViewById(R.id.female_within)).setText(femaleWithin);


                    int totalCatchment = viewRow.getWithinCatchmentFemale()+viewRow.getWithinCatchmentMale();
                    String totalCatchmentString = totalCatchment+"";
                    ((TextView) doseData.findViewById(R.id.total_within)).setText(totalCatchmentString);


                    String maleOutside = viewRow.getOutsideCatchmentMale() + "";
                    ((TextView) doseData.findViewById(R.id.male_outside)).setText(maleOutside);

                    String femaleOutside = viewRow.getOutsideCatchmentFemale() + "";
                    ((TextView) doseData.findViewById(R.id.female_outside)).setText(femaleOutside);


                    int outsideTotal = viewRow.getOutsideCatchmentMale()+viewRow.getOutsideCatchmentFemale();
                    String outsideTotalString = outsideTotal+"";
                    ((TextView)doseData.findViewById(R.id.total_outside)).setText(outsideTotalString);


                    String totalData = (outsideTotal+totalCatchment)+"";

                    ((TextView)doseData.findViewById(R.id.total)).setText(totalData);

                    int coveragePercentage=0;
                    try {
                        int z = viewRow.getExpectedCatchmentTotal();
                        Log.d("coze","Expected = "+ z);
                        coveragePercentage= (totalCatchment*100)/z;
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    String coverage = coveragePercentage+"%";

                    TextView coverageText = (TextView) doseData.findViewById(R.id.coverage);
                    coverageText.setText(coverage);

                    if (coveragePercentage > 90){
                        coverageText.setBackgroundColor(getResources().getColor(R.color.green_500));
                    }
                    if (coveragePercentage <= 90){
                        coverageText.setBackgroundColor(getResources().getColor(R.color.yellow_500));
                    }
                    if (coveragePercentage < 80){
                        coverageText.setBackgroundColor(getResources().getColor(R.color.red_500));
                    }

                    if(j==(size1-1)){
                        Log.d("coze","adding a divider");
                        doseData.findViewById(R.id.divider).setVisibility(View.GONE);
                    }

                    ((LinearLayout)row.findViewById(R.id.dosage_list)).addView(doseData);


                }


                chart_list.addView(row);
            }


            progressBar.setVisibility(View.GONE);
            chartList.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    class ViewRows {

        public ViewRows(){
        }

        int totalFixed,totalOutreach,total,totalWithin,totalOutside;
        int vaccineFixed,vaccineOutreach,vaccineTotal,vaccineWithin,vaccineOutside;
        int NewVisitsFixed,NewVisitsOutreach,NewVisitsTotal,NewVisitsWithin,NewVisitsOutside;
        int underImmunizedFixed,underImmunizedOutreach,underImmunizedTotal,underImmunizedWithin,underImmunizedOutside;
        int fullyImmunizedFixed,fullyImmunizedOutreach,fullyImmunizedTotal,fullyImmunizedWithin,fullyImmunizedOutside;

        public int getTotalFixed() {
            return totalFixed;
        }

        public void setTotalFixed(int totalFixed) {
            this.totalFixed = totalFixed;
        }

        public int getTotalOutreach() {
            return totalOutreach;
        }

        public void setTotalOutreach(int totalOutreach) {
            this.totalOutreach = totalOutreach;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalWithin() {
            return totalWithin;
        }

        public void setTotalWithin(int totalWithin) {
            this.totalWithin = totalWithin;
        }

        public int getTotalOutside() {
            return totalOutside;
        }

        public void setTotalOutside(int totalOutside) {
            this.totalOutside = totalOutside;
        }

        public int getVaccineFixed() {
            return vaccineFixed;
        }

        public void setVaccineFixed(int vaccineFixed) {
            this.vaccineFixed = vaccineFixed;
        }

        public int getVaccineOutreach() {
            return vaccineOutreach;
        }

        public void setVaccineOutreach(int vaccineOutreach) {
            this.vaccineOutreach = vaccineOutreach;
        }

        public int getVaccineTotal() {
            return vaccineTotal;
        }

        public void setVaccineTotal(int vaccineTotal) {
            this.vaccineTotal = vaccineTotal;
        }

        public int getVaccineWithin() {
            return vaccineWithin;
        }

        public void setVaccineWithin(int vaccineWithin) {
            this.vaccineWithin = vaccineWithin;
        }

        public int getVaccineOutside() {
            return vaccineOutside;
        }

        public void setVaccineOutside(int vaccineOutside) {
            this.vaccineOutside = vaccineOutside;
        }

        public int getNewVisitsFixed() {
            return NewVisitsFixed;
        }

        public void setNewVisitsFixed(int newVisitsFixed) {
            NewVisitsFixed = newVisitsFixed;
        }

        public int getNewVisitsOutreach() {
            return NewVisitsOutreach;
        }

        public void setNewVisitsOutreach(int newVisitsOutreach) {
            NewVisitsOutreach = newVisitsOutreach;
        }

        public int getNewVisitsTotal() {
            return NewVisitsTotal;
        }

        public void setNewVisitsTotal(int newVisitsTotal) {
            NewVisitsTotal = newVisitsTotal;
        }

        public int getNewVisitsWithin() {
            return NewVisitsWithin;
        }

        public void setNewVisitsWithin(int newVisitsWithin) {
            NewVisitsWithin = newVisitsWithin;
        }

        public int getNewVisitsOutside() {
            return NewVisitsOutside;
        }

        public void setNewVisitsOutside(int newVisitsOutside) {
            NewVisitsOutside = newVisitsOutside;
        }


        public int getUnderImmunizedFixed() {
            return underImmunizedFixed;
        }

        public void setUnderImmunizedFixed(int underImmunizedFixed) {
            this.underImmunizedFixed = underImmunizedFixed;
        }

        public int getUnderImmunizedOutreach() {
            return underImmunizedOutreach;
        }

        public void setUnderImmunizedOutreach(int underImmunizedOutreach) {
            this.underImmunizedOutreach = underImmunizedOutreach;
        }

        public int getUnderImmunizedTotal() {
            return underImmunizedTotal;
        }

        public void setUnderImmunizedTotal(int underImmunizedTotal) {
            this.underImmunizedTotal = underImmunizedTotal;
        }

        public int getUnderImmunizedWithin() {
            return underImmunizedWithin;
        }

        public void setUnderImmunizedWithin(int underImmunizedWithin) {
            this.underImmunizedWithin = underImmunizedWithin;
        }

        public int getUnderImmunizedOutside() {
            return underImmunizedOutside;
        }

        public void setUnderImmunizedOutside(int underImmunizedOutside) {
            this.underImmunizedOutside = underImmunizedOutside;
        }

        public int getFullyImmunizedFixed() {
            return fullyImmunizedFixed;
        }

        public void setFullyImmunizedFixed(int fullyImmunizedFixed) {
            this.fullyImmunizedFixed = fullyImmunizedFixed;
        }

        public int getFullyImmunizedOutreach() {
            return fullyImmunizedOutreach;
        }

        public void setFullyImmunizedOutreach(int fullyImmunizedOutreach) {
            this.fullyImmunizedOutreach = fullyImmunizedOutreach;
        }

        public int getFullyImmunizedTotal() {
            return fullyImmunizedTotal;
        }

        public void setFullyImmunizedTotal(int fullyImmunizedTotal) {
            this.fullyImmunizedTotal = fullyImmunizedTotal;
        }

        public int getFullyImmunizedWithin() {
            return fullyImmunizedWithin;
        }

        public void setFullyImmunizedWithin(int fullyImmunizedWithin) {
            this.fullyImmunizedWithin = fullyImmunizedWithin;
        }

        public int getFullyImmunizedOutside() {
            return fullyImmunizedOutside;
        }

        public void setFullyImmunizedOutside(int fullyImmunizedOutside) {
            this.fullyImmunizedOutside = fullyImmunizedOutside;
        }
    }

}
