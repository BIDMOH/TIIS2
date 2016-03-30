package mobile.giis.app.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.HealthFacility;
import mobile.giis.app.util.ViewAppointmentRow;

/**
 * Created by issymac on 30/03/16.
 */
public class DefaultersReportFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private TextView region, district, healthFacility, reportingPeriod, title;

    BackboneApplication app;

    private ProgressBar progressBar;

    private DatabaseHandler mydb;

    public static DefaultersReportFragment newInstance(int position) {
        DefaultersReportFragment f = new DefaultersReportFragment();
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

        View rowview;
        rowview = inflater.inflate(R.layout.fragment_defaulters, null);
        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));
        reportingPeriod.setText(mydb.getUserHFIDByUserId(app.getLOGGED_IN_USER_ID()));



        return rowview;
    }

    public void prepareUIElements(View v){
        region          = (TextView) v.findViewById(R.id.region_title);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_title);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
    }

    public class filterList extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewRows> mVar;


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String ageName = params[0];
            String startRow = params[1];
            String fromDate ="";
            String toDate ="";

            try{
                fromDate = params[2];
                toDate = params[3];
            }catch (Exception e){
                e.printStackTrace();
            }

            Cursor cursor;
            mVar = new ArrayList<>();

            String SQLDefaultersList;
            SQLDefaultersList =
                    "SELECT DISTINCT APPOINTMENT_ID, CHILD_ID "+
                            " ,(SELECT GROUP_CONCAT(dose.FULLNAME) FROM vaccination_event INNER JOIN dose ON vaccination_event.DOSE_ID = dose.ID left join age_definitions on dose.TO_AGE_DEFINITON_ID = age_definitions.ID WHERE monthly_plan.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') AND vaccination_event.IS_ACTIVE='true' AND vaccination_event.VACCINATION_STATUS='false' AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) AND (DAYS IS NULL or (datetime(substr(vaccination_event.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || DAYS || ' days')) )) AS VACCINES " +
                            " , SCHEDULE, SCHEDULED_DATE "+
                            " FROM monthly_plan join dose on DOSE_ID = dose.ID" +
                            " WHERE HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' AND SCHEDULE like '%"+ageName+"%' AND datetime(substr(SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') "+
                            "AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                            " OR (datetime(substr(SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                            " GROUP BY APPOINTMENT_ID, SCHEDULED_DATE, DOMICILE, NAME, SCHEDULE, CHILD_ID, SCHEDULE_ID "+
                            " ORDER BY SCHEDULED_DATE "+
                            " LIMIT "+startRow+", 10 ; ";

            Log.e("SQLDefaultersList", SQLDefaultersList);
            cursor = mydb.getReadableDatabase().rawQuery(SQLDefaultersList, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewRows row = new ViewRows();
                        mVar.add(row);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d("MPLAN", mVar.size() + "");
//            adapter.updateData(mVar);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    class ViewRows {

        public ViewRows(){
        }

        String ChildName, ChildId, Gender, ChildDOB, MotherNames, MotherContacts, Village;

        public String getChildName() {
            return ChildName;
        }

        public void setChildName(String childName) {
            ChildName = childName;
        }

        public String getChildId() {
            return ChildId;
        }

        public void setChildId(String childId) {
            ChildId = childId;
        }

        public String getGender() {
            return Gender;
        }

        public void setGender(String gender) {
            Gender = gender;
        }

        public String getChildDOB() {
            return ChildDOB;
        }

        public void setChildDOB(String childDOB) {
            ChildDOB = childDOB;
        }

        public String getMotherNames() {
            return MotherNames;
        }

        public void setMotherNames(String motherNames) {
            MotherNames = motherNames;
        }

        public String getMotherContacts() {
            return MotherContacts;
        }

        public void setMotherContacts(String motherContacts) {
            MotherContacts = motherContacts;
        }

        public String getVillage() {
            return Village;
        }

        public void setVillage(String village) {
            Village = village;
        }
    }

}
