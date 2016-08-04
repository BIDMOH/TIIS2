package mobile.tiis.appv2.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.realm.implementation.RealmRadarDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import mobile.tiis.appv2.DatabaseModals.Dose;
import mobile.tiis.appv2.DatabaseModals.RadarData0;
import mobile.tiis.appv2.DatabaseModals.RadarData1;
import mobile.tiis.appv2.DatabaseModals.RadarData2;
import mobile.tiis.appv2.DatabaseModals.RadarData3;
import mobile.tiis.appv2.DatabaseModals.Scheduled_Vaccination;
import mobile.tiis.appv2.DatabaseModals.ViewChartData;
import mobile.tiis.appv2.DatabaseModals.ViewRow;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;

/**
 * Created by issymac on 30/03/16.
 */
public class HealthFacilityImmunizationCoverageChartFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private int position;
    private TextView region, district, healthFacility, reportingPeriod, title;
    BackboneApplication app;
    private ProgressBar progressBar;
    private DatabaseHandler mydb;
    private View rowview,chart_view;;
    private  MaterialEditText metDOBFrom,metDOBTo;

    final DatePickerDialog fromDatePicker = new DatePickerDialog();
    final DatePickerDialog toDatePicker = new DatePickerDialog();

    private String toDateString="",fromDateString="";
    private EditText editTextUsedToRequestFocus;
    private LayoutInflater inflater;
    private com.github.mikephil.charting.charts.RadarChart mChart;
    private Typeface mTf;
    private  ArrayList<String> xVals = new ArrayList<String>();;

    private Realm mRealm;

    public static HealthFacilityImmunizationCoverageChartFragment newInstance(int position) {
        HealthFacilityImmunizationCoverageChartFragment f = new HealthFacilityImmunizationCoverageChartFragment();
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
        rowview = inflater.inflate(R.layout.radar_chart_layout, null);

        editTextUsedToRequestFocus = (EditText) rowview.findViewById(R.id.edit_text_used_to_request_focus2);
        editTextUsedToRequestFocus.requestFocus();

        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));






        return rowview;
    }

    public void prepareUIElements(View v){
        mChart          = (com.github.mikephil.charting.charts.RadarChart)v.findViewById(R.id.chart1);
        chart_view      = v.findViewById(R.id.chart_view);
        region          = (TextView) v.findViewById(R.id.region_value);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);

        metDOBFrom      = (MaterialEditText) v.findViewById(R.id.met_dob_from);
        metDOBTo        = (MaterialEditText) v.findViewById(R.id.met_dob_value);

        setup(mChart);

        mChart.getYAxis().setEnabled(false);
        mChart.setWebAlpha(180);
        mChart.setWebColorInner(Color.DKGRAY);
        mChart.setWebColor(Color.GRAY);

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

                        if (!toDateString.equals("")) {
                            chart_view.setVisibility(View.VISIBLE);
                            new FilterList().execute(app.getLOGGED_IN_USER_HF_ID(), fromDateString, toDateString);
                        } else {
                            final Snackbar snackbar = Snackbar.make(rowview, "Please select an end date to view the chart", Snackbar.LENGTH_LONG);
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

    @Override
    public void onResume() {
        super.onResume(); // setup realm

        RealmConfiguration config = new RealmConfiguration.Builder(getActivity())
                .name("myrealm.realm")
                .build();

        Realm.deleteRealm(config);

        Realm.setDefaultConfiguration(config);

        mRealm = Realm.getInstance(config);

        setData();
    }


    @Override
    public void onPause() {
        super.onPause();
        mRealm.close();
    }


    public class FilterList extends AsyncTask<String, Void, List<ViewChartData>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.INVISIBLE);
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

                String SQLDose = "SELECT ID,FULLNAME,DOSE_NUMBER,SCHEDULED_VACCINATION_ID FROM dose WHERE SCHEDULED_VACCINATION_ID = '"+scheduled_vaccination.getId()+"'";
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
        protected void onPostExecute(List<ViewChartData> chartDatas) {

            xVals.clear();

            mRealm.beginTransaction();

            mRealm.clear(RadarData0.class);

            int size = chartDatas.size();



            int count0=0,count1=0,count2=0,count3=0;
            for (int i=0;i<size;i++){
                ViewChartData viewChartData = chartDatas.get(i);
                xVals.add(viewChartData.getScheduled_vaccination().getName());

                List<ViewRow> rowList= viewChartData.getViewRows();
                int size1 = rowList.size();
                for (int j =0; j<size1;j++){
                    ViewRow viewRow = rowList.get(j);

                    int totalCatchment = viewRow.getWithinCatchmentFemale()+viewRow.getWithinCatchmentMale();
                    int z = viewRow.getExpectedCatchmentTotal();
                    float coveragePercentage= (totalCatchment*100f)/z;
                    if(viewRow.getDose().getDose_number().equals("0")){
                        RadarData0 radarData0 =  new RadarData0(coveragePercentage,count0,count0+"");
                        count0++;
                        mRealm.copyToRealm(radarData0);
                    }else if(viewRow.getDose().getDose_number().equals("1")){
                        RadarData1 radarData1 = new RadarData1(coveragePercentage,count1,count1+"");
                        count1++;
                        mRealm.copyToRealm(radarData1);
                    }else if(viewRow.getDose().getDose_number().equals("2")){
                        RadarData2 radarData2 = new RadarData2(coveragePercentage,count2,count2+"");
                        count2++;
                        mRealm.copyToRealm(radarData2);
                    }else if(viewRow.getDose().getDose_number().equals("3")){
                        RadarData3 radarData3 = new RadarData3(coveragePercentage,count3,count3+"");
                        count3++;
                        mRealm.copyToRealm(radarData3);
                    }
                }
            }


            mRealm.commitTransaction();

            setData();


            progressBar.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    private void setup(Chart<?> chart) {

        mTf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

        chart.setDescription("Health Facility Immunization Coverage Report");
        chart.setDescriptionTextSize(18f);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(18f);


        chart.setTouchEnabled(true);

        if (chart instanceof BarLineChartBase) {

            BarLineChartBase mChart = (BarLineChartBase) chart;

            mChart.setDrawGridBackground(false);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setTypeface(mTf);
            leftAxis.setTextSize(8f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setValueFormatter(new PercentFormatter());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTypeface(mTf);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(8f);
            xAxis.setTextColor(Color.DKGRAY);

            mChart.getAxisRight().setEnabled(false);
        }
    }

    private void setData() {

        RealmResults<RadarData0> result0 = mRealm.allObjects(RadarData0.class);
        RealmRadarDataSet<RadarData0> set0 = new RealmRadarDataSet<RadarData0>(result0, "value", "xIndex");
        set0.setLabel("Dose 0");
        set0.setDrawFilled(true);
        set0.setColor(ColorTemplate.rgb("#dd191d"));
        set0.setFillColor(ColorTemplate.rgb("#dd191d"));
        set0.setFillAlpha(130);
        set0.setLineWidth(2f);



        RealmResults<RadarData1> result1 = mRealm.allObjects(RadarData1.class);
        RealmRadarDataSet<RadarData1> set1 = new RealmRadarDataSet<RadarData1>(result1, "value", "xIndex");
        set1.setLabel("Dose 1");
        set1.setDrawFilled(true);
        set1.setColor(ColorTemplate.rgb("#5677fc"));
        set1.setFillColor(ColorTemplate.rgb("#5677fc"));
        set1.setFillAlpha(130);
        set1.setLineWidth(2f);



        RealmResults<RadarData2> result2 = mRealm.allObjects(RadarData2.class);
        RealmRadarDataSet<RadarData2> set2 = new RealmRadarDataSet<RadarData2>(result2, "value", "xIndex");
        set2.setLabel("Dose 2");
        set2.setDrawFilled(true);
        set2.setColor(ColorTemplate.rgb("#259b24"));
        set2.setFillColor(ColorTemplate.rgb("#259b24"));
        set2.setFillAlpha(130);
        set2.setLineWidth(2f);



        RealmResults<RadarData3> result3 = mRealm.allObjects(RadarData3.class);
        RealmRadarDataSet<RadarData3> set3 = new RealmRadarDataSet<RadarData3>(result3, "value", "xIndex");
        set3.setLabel("Dose 3");
        set3.setDrawFilled(true);
        set3.setColor(ColorTemplate.rgb("#9c27b0"));
        set3.setFillColor(ColorTemplate.rgb("#9c27b0"));
        set3.setFillAlpha(130);
        set3.setLineWidth(2f);




        ArrayList<IRadarDataSet> dataSets = new ArrayList<IRadarDataSet>();
        dataSets.add(set0);
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);


        // create a data object with the dataset list
        RadarData data = new RadarData(xVals, dataSets);
        styleData(data);

        // set data
        mChart.setData(data);
        mChart.animateY(1400);
    }


    private void styleData(ChartData data) {
        data.setValueTypeface(mTf);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueFormatter(new PercentFormatter());
    }

}
