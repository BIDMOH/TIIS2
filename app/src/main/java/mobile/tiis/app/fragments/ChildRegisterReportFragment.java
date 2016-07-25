package mobile.tiis.app.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mobile.tiis.app.CustomViews.DividerItemDecoration;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.ChildRegisterReportRecyclerAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.util.ViewChildRegisterInfoRow;

/**
 * Created by issy on 7/7/16.
 */
public class ChildRegisterReportFragment extends android.support.v4.app.Fragment{

    //Table Layout to be used to loop the list of the children information in
    private TableLayout childRegisterTable;

    public String currentCount = "0";
    private BackboneApplication app;
    private DatabaseHandler this_database;
    private ArrayList<ViewChildRegisterInfoRow> var;
    private LayoutInflater inflater;
    private RecyclerView childRegisterListView;
    private ChildRegisterReportRecyclerAdapter adapter;
    private ProgressBar progressBar;
    private MaterialEditText metDOBFrom,metDOBTo;
    private DatePickerDialog fromDatePicker = new DatePickerDialog();
    private DatePickerDialog toDatePicker = new DatePickerDialog();
    private String toDateString = "", fromDateString = "";

    public static ChildRegisterReportFragment newInstance(int position) {
        ChildRegisterReportFragment f = new ChildRegisterReportFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_register, null);
        app = (BackboneApplication) this.getActivity().getApplication();
        this_database = app.getDatabaseInstance();
        this.inflater = inflater;
        setUpView(root);


        metDOBFrom = (MaterialEditText) root.findViewById(R.id.met_dob_from);
        metDOBTo = (MaterialEditText) root.findViewById(R.id.met_dob_value);




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

                        if (!fromDateString.equals("")) {
                            new filterList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fromDateString, toDateString);
                        } else {
                            final Snackbar snackbar = Snackbar.make(root, "Please select a start date to view the chart", Snackbar.LENGTH_LONG);
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
                        fromDateString = (fromCalendar.getTimeInMillis() / 1000) + "";

                        if (!toDateString.equals("")) {
                            new filterList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fromDateString, toDateString);
                        } else {
                            final Snackbar snackbar = Snackbar.make(root, "Please select an end date to view the report", Snackbar.LENGTH_LONG);
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


        new filterList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return root;
    }

    public void setUpView(View v){
//        progressBar             = (ProgressBar) v.findViewById(R.id.pbar);
//        progressBar             .setVisibility(View.VISIBLE);
        childRegisterTable      = (TableLayout) v.findViewById(R.id.child_register_table);
        childRegisterListView   = (RecyclerView) v.findViewById(R.id.child_register_nested_listview);
        childRegisterListView   .setVisibility(View.GONE);
    }


    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public  class filterList extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewChildRegisterInfoRow> mVar;

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
//            lvMonthlyPlanList.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            long t = Calendar.getInstance().getTimeInMillis()/1000;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.getInstance().get(Calendar.YEAR),1,1);

            long t1 = (t + (30 * 24 * 60 * 60));
            long t2 = (c.getTimeInMillis());
            String to_date =  t1+ "";
            String from_date = t2+ "";

            try {
                if (!params[0].equals("") && !params[1].equals("")) {
                    from_date = (Long.parseLong(params[0])-(24*60*60))+"";
                    Log.d("day13", "from picker : "+from_date);
                    to_date = params[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Cursor cursor;
            mVar = new ArrayList<>();

            DatabaseHandler mydb = app.getDatabaseInstance();

            String SQLChildRegistry = "SELECT DISTINCT child.ID, FIRSTNAME1,FIRSTNAME2,LASTNAME1, BIRTHDATE,GENDER,MOTHER_FIRSTNAME,MOTHER_LASTNAME,MOTHER_VVU_STS,MOTHER_TT2_STS,CUMULATIVE_SERIAL_NUMBER,CHILD_REGISTRY_YEAR, \n" +
                    "\t\t(CASE \n" +
                    "\t\t\tWHEN (place.ID = '-100') \n" +
                    "\t\t\t\tTHEN child.NOTES\n" +
                    "                ELSE place.NAME \n" +
                    "\t\tEND) AS DOMICILE,\n" +
                    "\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'BCG'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS BCG,\n" +
                    "\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV0'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV0,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV3,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP3,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Rota 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Rota1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Rota 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Rota2,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Measles1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Measles2,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV3,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles Rubella 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS MeaslesRubella1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles Rubella 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS MeaslesRubella2\n" +
                    "\t\t\n" +
                    "\t\n" +
                    "         FROM CHILD \n" +
                    "\t\t INNER JOIN \n" +
                    "\t\t place on child.DOMICILE_ID = place.ID" +
                    "     INNER JOIN vaccination_event on child.ID = vaccination_event.CHILD_ID " +
                    "     WHERE child.HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' " +
                    "     AND ((substr(vaccination_event.VACCINATION_DATE,7,10)) >= ('" +from_date+ "') AND (substr(vaccination_event.VACCINATION_DATE,7,10)) <= ('" +to_date+ "') AND vaccination_event.VACCINATION_STATUS='true')" ;



            Log.e("optimization", SQLChildRegistry);
            long tStart = System.currentTimeMillis();
            cursor = mydb.getReadableDatabase().rawQuery(SQLChildRegistry, null);



            Log.e("optimization", "Querying time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));

            Log.d("optimization", "Done with getting the Monthly plan data");
            if (cursor != null) {
                tStart = System.currentTimeMillis();
                Log.d("optimization", "cursor not null SIZE IS : "+cursor.getCount());
                Log.e("optimization", "Getting total size time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));


                tStart = System.currentTimeMillis();
                if (cursor.moveToFirst()) {
                    Log.d("optimization", "Moved to first item in the cursor");
                    Log.e("optimization", "moving cursor to first time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));

                    tStart = System.currentTimeMillis();
                    int counter = 0;
                    do {
                        Log.d("childregistry","BCG = "+cursor.getString(cursor.getColumnIndex("BCG")));
                        counter++;
                        ViewChildRegisterInfoRow row = new ViewChildRegisterInfoRow();
                        row.sn = counter;
                        row.childFirstName = cursor.getString(cursor.getColumnIndex("FIRSTNAME1"));
                        row.childMiddleName = cursor.getString(cursor.getColumnIndex("FIRSTNAME2"));
                        row.childSurname = cursor.getString(cursor.getColumnIndex("LASTNAME1"));
                        row.birthdate = cursor.getString(cursor.getColumnIndex("BIRTHDATE"));
                        row.gender = cursor.getString(cursor.getColumnIndex("GENDER"));
                        row.motherFirstName = cursor.getString(cursor.getColumnIndex("MOTHER_FIRSTNAME"));
                        row.motherLastName = cursor.getString(cursor.getColumnIndex("MOTHER_LASTNAME"));
                        row.domicile = cursor.getString(cursor.getColumnIndex("DOMICILE"));
                        row.bcg = cursor.getString(cursor.getColumnIndex("BCG"));
                        row.OPV0 = cursor.getString(cursor.getColumnIndex("OPV0"));
                        row.OPV1 = cursor.getString(cursor.getColumnIndex("OPV1"));
                        row.OPV2 = cursor.getString(cursor.getColumnIndex("OPV2"));
                        row.OPV3 = cursor.getString(cursor.getColumnIndex("OPV3"));
                        row.DTP1 = cursor.getString(cursor.getColumnIndex("DTP1"));
                        row.DTP2 = cursor.getString(cursor.getColumnIndex("DTP2"));
                        row.DTP3 = cursor.getString(cursor.getColumnIndex("DTP3"));
                        row.Rota1 = cursor.getString(cursor.getColumnIndex("Rota1"));
                        row.Rota2 = cursor.getString(cursor.getColumnIndex("Rota2"));
                        row.measles1 = cursor.getString(cursor.getColumnIndex("Measles1"));
                        row.measles2 = cursor.getString(cursor.getColumnIndex("Measles2"));
                        row.PCV1 = cursor.getString(cursor.getColumnIndex("PCV1"));
                        row.PCV2 = cursor.getString(cursor.getColumnIndex("PCV2"));
                        row.PCV3 = cursor.getString(cursor.getColumnIndex("PCV3"));
                        row.measlesRubella1 = cursor.getString(cursor.getColumnIndex("MeaslesRubella1"));
                        row.measlesRubella2 = cursor.getString(cursor.getColumnIndex("MeaslesRubella2"));
                        row.childRegistrationYear = cursor.getString(cursor.getColumnIndex("CHILD_REGISTRY_YEAR"));
                        row.childCumulativeSn = cursor.getString(cursor.getColumnIndex("CUMULATIVE_SERIAL_NUMBER"));
                        row.motherTT2Status = cursor.getString(cursor.getColumnIndex("MOTHER_TT2_STS"));
                        row.motherHivStatus = cursor.getString(cursor.getColumnIndex("MOTHER_VVU_STS"));

                        mVar.add(row);
                    } while (cursor.moveToNext());
                    Log.e("optimization", "Looping to get data  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));


                }
                cursor.close();
                adapter = new ChildRegisterReportRecyclerAdapter(mVar);
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            childRegisterListView   .setVisibility(View.VISIBLE);
            var                     = mVar;

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

            childRegisterListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

            childRegisterListView.setHasFixedSize(true);
            childRegisterListView.setLayoutManager(mLayoutManager);
            childRegisterListView.setItemAnimator(new DefaultItemAnimator());
            childRegisterListView.setAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }
}
