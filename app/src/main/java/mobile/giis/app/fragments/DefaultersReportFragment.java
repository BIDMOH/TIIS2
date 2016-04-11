package mobile.giis.app.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.Child;
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

    private TableLayout defaultersTable;
    private LinearLayout dataView;
    private MaterialEditText metDOBFrom, metDOBTo;
    final DatePickerDialog fromDatePicker = new DatePickerDialog();
    final DatePickerDialog toDatePicker = new DatePickerDialog();
    private String toDateString="",fromDateString="";

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

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
        toDatePicker.setMaxDate(Calendar.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rowview;
        rowview = inflater.inflate(R.layout.fragment_defaulters, null);
        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));


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
//                            editTextUsedToRequestFocus.requestFocus();

                        if (!fromDateString.equals("")) {
                            dataView.setVisibility(View.VISIBLE);
                            new getDefaultersList().execute(fromDateString, toDateString);
//                                chart_view.setVisibility(View.VISIBLE);
//                                new FilterList().execute(app.getLOGGED_IN_USER_HF_ID(), fromDateString, toDateString);
                        } else {
                            final Snackbar snackbar = Snackbar.make(dataView, "Please select a start date to view the chart", Snackbar.LENGTH_LONG);
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
                        fromDateString = (fromCalendar.getTimeInMillis()/1000)+"";
//                            editTextUsedToRequestFocus.requestFocus();

                        if(!toDateString.equals("")){
                            dataView.setVisibility(View.VISIBLE);
//                                new FilterList().execute(app.getLOGGED_IN_USER_HF_ID(), fromDateString, toDateString);
                            new getDefaultersList().execute(fromDateString, toDateString);
                        }else{
                            final Snackbar snackbar=Snackbar.make(dataView,"Please select an end date to view the chart",Snackbar.LENGTH_LONG);
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

        return rowview;
    }

    public void prepareUIElements(View v){
        region          = (TextView) v.findViewById(R.id.region_title);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
        defaultersTable = (TableLayout) v.findViewById(R.id.defaulter_table);

        metDOBFrom              = (MaterialEditText) v.findViewById(R.id.met_dob_from);
        metDOBTo                = (MaterialEditText) v.findViewById(R.id.met_dob_value);

        dataView        = (LinearLayout) v.findViewById(R.id.data_view);
        dataView        .setVisibility(View.GONE);
    }

    public class getDefaultersList extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewRows> mVar;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String fromDate ="";
            String toDate ="";

            try{
                fromDate = params[0];
                toDate = params[1];
            }
            catch (Exception e){
                e.printStackTrace();
            }

            Cursor cursor;
            mVar = new ArrayList<>();
            /*
            "datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') AND vaccination_event.IS_ACTIVE='true' AND vaccination_event.VACCINATION_STATUS='false' AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true'))" +
             */
            String SQLDefaultersList;
            SQLDefaultersList =
                    "SELECT DISTINCT CHILD_ID "+
                            " FROM vaccination_event " +
                            " WHERE HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' "+
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')>=datetime('"+fromDate+"','unixepoch') " +
                            "AND datetime(substr(vaccination_event.VACCINATION_DATE,7,10), 'unixepoch')<=datetime('"+toDate+"','unixepoch')" +
                            " AND vaccination_event.IS_ACTIVE='true' AND vaccination_event.VACCINATION_STATUS='false' "+
                            " GROUP BY CHILD_ID " ; //+
//                            " ORDER BY SCHEDULED_DATE "+
//                            " LIMIT "+startRow+", 10 ; ";

            Log.e("SQLDefaultersList", SQLDefaultersList);
            cursor = mydb.getReadableDatabase().rawQuery(SQLDefaultersList, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewRows row = new ViewRows();
                        row.setChildId(cursor.getString(cursor.getColumnIndex("CHILD_ID")));
                        mVar.add(row);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            return mVar.size();
        }

        @Override
        protected void onPostExecute(Integer result) {

//            adapter.updateData(mVar);
            if (result!=0){
                defaultersTable.removeAllViews();
                FillDefaultersTable(mVar);
                progressBar.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    private void FillDefaultersTable(ArrayList<ViewRows> items){
        ArrayList<ViewRows> VR = items;
        for (ViewRows row : VR){
            View v  = View.inflate(DefaultersReportFragment.this.getActivity(), R.layout.defaulter_list_table_item, null);

            TextView childNames = (TextView) v.findViewById(R.id.child_names_value);
            TextView childID    = (TextView) v.findViewById(R.id.child_id_value);
            TextView childGender = (TextView) v.findViewById(R.id.gender_value);
            TextView childDOB = (TextView) v.findViewById(R.id.child_dob_value);
            TextView childMotherNames = (TextView) v.findViewById(R.id.mothers_names_value);
            TextView childMotherContacts = (TextView) v.findViewById(R.id.mothers_contacts_value);
            TextView childVillage = (TextView) v.findViewById(R.id.village_value);

            Cursor cursor;
            cursor =  mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                    new String[]{String.valueOf(row.getChildId())});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Child currentChild = getChildFromCursror(cursor);
                childNames.setText(currentChild.getFirstname1()+" "+currentChild.getLastname1());
                childID.setText(currentChild.getBarcodeID());

                if (currentChild.getGender().equals("true")){
                    childGender.setText("Male");
                }else if (currentChild.getGender().equals("false")){
                    childGender.setText("Female");
                }

                SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
                Date date = BackboneActivity.dateParser(currentChild.getBirthdate());
                childDOB.setText(ft.format(date));

                childMotherNames.setText(currentChild.getMotherFirstname()+ " "+currentChild.getMotherLastname());

                childMotherContacts.setText(currentChild.getPhone());

                childVillage.setText(currentChild.getDomicile());

            }

            defaultersTable.addView(v);

        }
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
        Cursor cursor1;
        cursor1 = null;
        try {
            cursor1 = mydb.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
            }
        }finally {
            if(cursor1 != null)
                cursor1.close();
        }

        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2;
        cursor2 = null;
        try {
            cursor2 = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
            if (cursor2.getCount() > 0) {
                cursor2.moveToFirst();
                villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
            }
        }finally {
            if (cursor2 != null)
                cursor2.close();
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
        Cursor cursor4;
        cursor4 = null;
        try {
            cursor4 = mydb.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
            if (cursor4.getCount() > 0) {
                cursor4.moveToFirst();
                statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
            }
        }finally {
            if(cursor4 != null)
                cursor4.close();
        }

        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

    class ViewRows {

        public ViewRows(){
        }

        String childId;

        public String getChildId() {
            return childId;
        }

        public void setChildId(String childId) {
            this.childId = childId;
        }
    }

}
