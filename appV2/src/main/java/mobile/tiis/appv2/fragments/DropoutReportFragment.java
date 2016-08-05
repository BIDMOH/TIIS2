package mobile.tiis.appv2.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.adapters.SingleTextViewAdapter;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;

/**
 * Created by issymac on 31/03/16.
 */
public class DropoutReportFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private TextView region, district, healthFacility, reportingPeriod, title;

    BackboneApplication app;

    private ProgressBar progressBar;

    private DatabaseHandler mydb;

    private TableLayout dropoutTable;

    private MaterialSpinner dateSpinner;

    public String[] years = {"2014", "2015", "2016" };

    private String currentYearSelected = "2015";

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    private LayoutInflater inflator;

    public static DropoutReportFragment newInstance(int position) {
        DropoutReportFragment f = new DropoutReportFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflator = inflater;
        View rowview;
        rowview = inflater.inflate(R.layout.fragment_dropout_report, null);
        prepareUIElements(rowview);

        TextView districtValue = (TextView)rowview.findViewById(R.id.district_value);
        districtValue.setText(app.getHealthFacilityDistrictName(app.getLOGGED_IN_USER_HF_ID()));

        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));

        List<String> dateVAlues = new ArrayList<>();
        for (String year : years){
            dateVAlues.add(year);
        }

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(DropoutReportFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, dateVAlues);
        dateSpinner.setAdapter(dataAdapter);
        dateSpinner.setSelection(1);
        currentYearSelected = dateVAlues.get(1);

        new getDropOutTable().execute(currentYearSelected);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("SpinnerSelection", "Positions is : "+i);
                if (i != -1){
                    currentYearSelected = years[i];
                    new getDropOutTable().execute(currentYearSelected);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//
        return rowview;
    }

    public void prepareUIElements(View v){
        region          = (TextView) v.findViewById(R.id.region_title);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
        dropoutTable = (TableLayout) v.findViewById(R.id.dropout_table);
        dateSpinner     = (MaterialSpinner) v.findViewById(R.id.spin_date);
    }

    public class getDropOutTable extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewRows> mVar;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {

            Cursor cursor;
            mVar = new ArrayList<>();


            Cursor monthCursor;
            String SQLMonthQuery;
            SQLMonthQuery = "SELECT strftime('%m',substr("+ SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') as month" +
                    " FROM vaccination_event WHERE strftime('%Y',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') =  strftime('%Y','now') "+
                    " AND "+SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+" = '"+app.getLOGGED_IN_USER_HF_ID()+"'"+
                    " group by strftime('%m',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch')";
            monthCursor = mydb.getReadableDatabase().rawQuery(SQLMonthQuery, null);

            if (monthCursor != null){
                if (monthCursor.moveToFirst()) {
                    do {
                        String dateRange = params[0];
                        dateRange = dateRange+"-"+monthCursor.getString(monthCursor.getColumnIndex("month"));

                        ViewRows row = new ViewRows();
                        row.clearData();

                        row.setMonthName(monthCursor.getString(monthCursor.getColumnIndex("month")));
                        Cursor cursor1, cursor2, cursor3, cursor4;
                        String SQLBcgCount, SQLMr1Count, SQLPenta1Count, SQLPenta3Count;

                        SQLBcgCount = "SELECT COUNT (DISTINCT ("+SQLHandler.VaccinationEventColumns.ID +")) AS BCG_COUNT FROM vaccination_event "+
                                " WHERE strftime('%Y-%m',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') =  '"+dateRange+"' "+
                                " AND "+SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+" = '"+app.getLOGGED_IN_USER_HF_ID()+"'"+
                                " AND "+SQLHandler.VaccinationEventColumns.VACCINATION_STATUS+" = 'true' "+
                                " AND "+ SQLHandler.VaccinationEventColumns.DOSE_ID +" = '61' ";

                        SQLMr1Count = "SELECT COUNT (DISTINCT("+SQLHandler.VaccinationEventColumns.ID +")) AS MR1_COUNT FROM vaccination_event "+
                                " WHERE strftime('%Y-%m',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') = '"+dateRange+"' "+
                                " AND "+SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+" = '"+app.getLOGGED_IN_USER_HF_ID()+"'"+
                                " AND "+SQLHandler.VaccinationEventColumns.VACCINATION_STATUS+" = 'true' "+
                                " AND "+ SQLHandler.VaccinationEventColumns.DOSE_ID +" = '77' ";

                        SQLPenta1Count = "SELECT COUNT (DISTINCT("+SQLHandler.VaccinationEventColumns.ID +")) AS PENTA1_COUNT FROM vaccination_event "+
                                " WHERE strftime('%Y-%m',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') =  '"+dateRange+"' "+
                                " AND "+SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+" = '"+app.getLOGGED_IN_USER_HF_ID()+"'"+
                                " AND "+SQLHandler.VaccinationEventColumns.VACCINATION_STATUS+" = 'true' "+
                                " AND "+ SQLHandler.VaccinationEventColumns.DOSE_ID +" = '66' ";

                        SQLPenta3Count = "SELECT COUNT (DISTINCT("+SQLHandler.VaccinationEventColumns.ID +")) AS PENTA3_COUNT FROM vaccination_event "+
                                " WHERE strftime('%Y-%m',substr("+SQLHandler.VaccinationEventColumns.VACCINATION_DATE+",7,10), 'unixepoch') =  '"+dateRange+"' "+
                                " AND "+SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID+" = '"+app.getLOGGED_IN_USER_HF_ID()+"'"+
                                " AND "+SQLHandler.VaccinationEventColumns.VACCINATION_STATUS+" = 'true' "+
                                " AND "+ SQLHandler.VaccinationEventColumns.DOSE_ID +" = '68' ";

                        Log.d("DropoutQueries", SQLBcgCount);
                        Log.d("DropoutQueries", SQLMr1Count);
                        Log.d("DropoutQueries", SQLPenta1Count);
                        Log.d("DropoutQueries", SQLPenta3Count);

                        cursor1 = mydb.getReadableDatabase().rawQuery(SQLBcgCount, null);
                        cursor2 = mydb.getReadableDatabase().rawQuery(SQLMr1Count, null);
                        cursor3 = mydb.getReadableDatabase().rawQuery(SQLPenta1Count, null);
                        cursor4 = mydb.getReadableDatabase().rawQuery(SQLPenta3Count, null);

//                        Log.d("day14", cursor1.getInt(cursor1.getColumnIndex("BCG_COUNT"))+ " of BCG for "+monthCursor.getString(monthCursor.getColumnIndex("month")));
//                        Log.d("day14", cursor2.getInt(cursor1.getColumnIndex("MR1_COUNT"))+ " of MR1 for "+monthCursor.getString(monthCursor.getColumnIndex("month")));
//                        Log.d("day14", cursor3.getInt(cursor1.getColumnIndex("PENTA1_COUNT"))+ " of PENTA 1 for "+monthCursor.getString(monthCursor.getColumnIndex("month")));
//                        Log.d("day14", cursor4.getInt(cursor1.getColumnIndex("PENTA3_COUNT"))+ " of PENTA 3 for "+monthCursor.getString(monthCursor.getColumnIndex("month")));

                        if (cursor1 != null){
                            cursor1.moveToFirst();
                            row.setBcgAmount(cursor1.getInt(cursor1.getColumnIndex("BCG_COUNT")));
                        }

                        if (cursor2 != null){
                            cursor2.moveToFirst();
                            row.setMr1Amount(cursor2.getInt(cursor2.getColumnIndex("MR1_COUNT")));
                        }

                        if (cursor3 != null){
                            cursor3.moveToFirst();
                            row.setPenta1Amount(cursor3.getInt(cursor3.getColumnIndex("PENTA1_COUNT")));
                        }

                        if (cursor4 != null){
                            cursor4.moveToFirst();
                            row.setPenta3Amount(cursor4.getInt(cursor4.getColumnIndex("PENTA3_COUNT")));
                        }
                        row.generateData();
                        mVar.add(row);

                    }while (monthCursor.moveToNext());
                }
                monthCursor.close();
            }

            return mVar.size();
        }

        @Override
        protected void onPostExecute(Integer result) {

//            adapter.updateData(mVar);
            if (result!=0){
                dropoutTable.removeAllViews();
                FillDroupoutTable(mVar, inflator);
                progressBar.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    private void FillDroupoutTable(ArrayList<ViewRows> items, LayoutInflater inflater) {
        ArrayList<ViewRows> VR = items;
        for (ViewRows row : VR) {

            View v = inflater.inflate(R.layout.dropout_report_item, null);

            TextView monthValue = (TextView) v.findViewById(R.id.month_value);
            TextView bcgMriNumber = (TextView) v.findViewById(R.id.bcg_mri_number_value);
            TextView bcgMriPercent = (TextView) v.findViewById(R.id.bcg_mri_percent_value);
            TextView pentaNumber = (TextView) v.findViewById(R.id.penta1_penta3_number_value);
            TextView pentaPercent = (TextView) v.findViewById(R.id.penta1_penta3_percent_value);

            monthValue.setText(returnMonthName(row.getMonthName()));

            bcgMriNumber.setText(row.getBcgNumber()+ "");
            bcgMriPercent.setText(row.getBcgPercent()+ "");
            if (row.getBcgPercent() > 10 || row.getBcgPercent() < 0){
                bcgMriPercent.setBackgroundColor(getResources().getColor(R.color.red_400));
            }

            pentaNumber.setText(row.getPentaNumber() + "");
            pentaPercent.setText(row.getPentaPercent()+"");
            if (row.getPentaPercent() > 10 || row.getPentaPercent() < 0){
                pentaPercent.setBackgroundColor(getResources().getColor(R.color.red_400));
            }

            dropoutTable.addView(v);
        }
    }

    public String returnMonthName(String number){
        switch (number){
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return "";
        }
    }

    class ViewRows{

        public ViewRows(){}

        String monthName;

        int bcgAmount, mr1Amount, penta1Amount, penta3Amount, bcgNumber, pentaNumber;

        double bcgPercent, pentaPercent;

        public String getMonthName() {
            return monthName;
        }

        public void setMonthName(String monthName) {
            this.monthName = monthName;
        }

        public int getBcgAmount() {
            return bcgAmount;
        }

        public void setBcgAmount(int bcgAmount) {
            this.bcgAmount = bcgAmount;
        }

        public int getMr1Amount() {
            return mr1Amount;
        }

        public void setMr1Amount(int mr1Amount) {
            this.mr1Amount = mr1Amount;
        }

        public int getPenta1Amount() {
            return penta1Amount;
        }

        public void setPenta1Amount(int penta1Amount) {
            this.penta1Amount = penta1Amount;
        }

        public int getPenta3Amount() {
            return penta3Amount;
        }

        public void setPenta3Amount(int penta3Amount) {
            this.penta3Amount = penta3Amount;
        }

        public double getBcgPercent() {
            return bcgPercent;
        }

        public double getPentaPercent() {
            return pentaPercent;
        }

        public int getBcgNumber() {
            return bcgNumber;
        }

        public void setBcgNumber(int bcgNumber) {
            this.bcgNumber = bcgNumber;
        }

        public int getPentaNumber() {
            return pentaNumber;
        }

        public void setPentaNumber(int pentaNumber) {
            this.pentaNumber = pentaNumber;
        }

        public void clearData(){
            this.monthName = "";
            bcgAmount = mr1Amount = penta1Amount = penta3Amount = bcgNumber = pentaNumber = 0;
            bcgPercent = pentaPercent = 0;
        }

        public void generateData(){

            double deviderBCG = bcgAmount+0.000000001;
            double deviderPENTA = penta1Amount + 0.000000001;

            bcgNumber = bcgAmount - mr1Amount;
            pentaNumber = penta1Amount - penta3Amount;

            try{
                bcgPercent = ((bcgAmount - mr1Amount)/deviderBCG)*100;
                bcgPercent = Math.round(bcgPercent * 100) / 100;
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                pentaPercent = ((penta1Amount - penta3Amount)/deviderPENTA)*100;
                pentaPercent = Math.round(pentaPercent * 100) / 100;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

}
