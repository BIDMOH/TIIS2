package mobile.giis.app.fragments;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.giis.app.CustomViews.ButteryProgressBar;
import mobile.giis.app.CustomViews.NestedListView;
import mobile.giis.app.R;
import mobile.giis.app.adapters.AdapterGridDataSearchResult;
import mobile.giis.app.adapters.MonthlyPlanListAdapter;
import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.adapters.VaccinationQueueListAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.GIISContract;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.util.EndlessScrollListener;
import mobile.giis.app.util.ViewAppointmentRow;

/**
 * Created by issymac on 16/12/15.
 */
public class MonthlyPlanFragment extends android.support.v4.app.Fragment{

    private List<String>  ages;

    private NestedListView lvMonthlyPlanList;

    private MonthlyPlanListAdapter adapter;

    private MaterialSpinner agesSpinner;

    Button vaccineQuantityButton;

    BackboneApplication app;

    DatabaseHandler this_database;

    ArrayList<VQAgeDefinitions> ageDef;

    ArrayList<ViewAppointmentRow> var;

    public String currentCount = "0";
    public String currentCategory = "";

    public View listviewFooter;
    public ImageButton previous, next;
    public RelativeLayout prevLayout, nextLayout;

    public ProgressBar loadingBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_monthly_plan, null);
        setUpView(root);

        app = (BackboneApplication) this.getActivity().getApplication();
        this_database = app.getDatabaseInstance();
        ageDef = getAllAgeDeffinitions();

        var = new ArrayList<>();

        listviewFooter  = inflater.inflate(R.layout.loading_list_footer, null);
        previous        = (ImageButton) listviewFooter.findViewById(R.id.previous_10_contents);
        next            = (ImageButton) listviewFooter.findViewById(R.id.next_10_contents);
        prevLayout              = (RelativeLayout) listviewFooter.findViewById(R.id.prev_rl);
        nextLayout              = (RelativeLayout) listviewFooter.findViewById(R.id.next_rl);


        nextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count+10;
                currentCount = count+"";
                new filterList().execute(currentCategory, currentCount);
            }
        });

        prevLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count-10;
                currentCount = count+"";
                new filterList().execute(currentCategory, currentCount);
            }
        });


        ArrayList<String> ageDefStr = new ArrayList<>();
        ageDefStr.add("--------");
        for(VQAgeDefinitions vqad : ageDef){
            ageDefStr.add(vqad.getName());
        }

        SingleTextViewAdapter spinnerAdapter = new SingleTextViewAdapter(MonthlyPlanFragment.this.getActivity(),R.layout.single_text_spinner_item_drop_down,ageDefStr);
        agesSpinner.setAdapter(spinnerAdapter);

        View lvHeader = inflater.inflate(R.layout.monthly_plan_list_item_header, null);
        TextView nameTitle = (TextView) lvHeader.findViewById(R.id.name_title);
        nameTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView vaccineTitle = (TextView) lvHeader.findViewById(R.id.vaccine_title);
        vaccineTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView ageTitle = (TextView) lvHeader.findViewById(R.id.age_title);
        ageTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView dateTitle = (TextView) lvHeader.findViewById(R.id.date_title);
        dateTitle.setTypeface(BackboneActivity.Rosario_Regular);

//        adapter = new MonthlyPlanListAdapter(MonthlyPlanFragment.this.getActivity(), children);
//        var = getViewAppointmentRows("");
//        adapter = new VaccinationQueueListAdapter(MonthlyPlanFragment.this.getActivity(), var);
//        lvMonthlyPlanList.setAdapter(adapter);

        setListViewHeightBasedOnChildren(lvMonthlyPlanList);
        lvMonthlyPlanList.addFooterView(listviewFooter);
        adapter = new MonthlyPlanListAdapter(MonthlyPlanFragment.this.getActivity(), var);
        lvMonthlyPlanList.setAdapter(adapter);
        new filterList().execute(currentCategory, "0"); //pass the initial data index on second parameter

        agesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    currentCategory = ageDef.get(position - 1).getName();
                    new filterList().execute(currentCategory, "0");
//                    var = getViewAppointmentRows(ageDef.get(position - 1).getId());
                } else {
                    currentCategory = "";
                    new filterList().execute(currentCategory, "0");
                }
//                lvMonthlyPlanList.setAdapter(null);
//                adapter = new VaccinationQueueListAdapter(MonthlyPlanFragment.this.getActivity(), var);
//                lvMonthlyPlanList.setAdapter(adapter);
//                pbar.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        vaccineQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder keyBuilder = new AlertDialog.Builder(MonthlyPlanFragment.this.getActivity());
                keyBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                keyBuilder.setView(View.inflate(MonthlyPlanFragment.this.getActivity(), R.layout.vaccination_quantity_custom_dialog, null));
                AlertDialog dialog = keyBuilder.create();
                dialog.show();
            }
        });

        return root;
    }

    public void setUpView(View v){
        lvMonthlyPlanList       =  (NestedListView)  v.findViewById(R.id.lv_monthly_plan);
        agesSpinner             = (MaterialSpinner) v.findViewById(R.id.age_spinner);
        vaccineQuantityButton   = (Button)          v.findViewById(R.id.vac_qnt_btn);
        loadingBar              = (ProgressBar)     v.findViewById(R.id.loading_bar);
        loadingBar              .setVisibility(View.GONE);
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

    private ArrayList<VQAgeDefinitions> getAllAgeDeffinitions(){

        ArrayList<VQAgeDefinitions> list = new ArrayList<>();
        String SQLAgeDef = "SELECT * from "+ SQLHandler.Tables.AGE_DEFINITIONS;
        Cursor cursor = this_database.getReadableDatabase().rawQuery(SQLAgeDef, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    VQAgeDefinitions row = new VQAgeDefinitions();
                    row.setId(cursor.getString(cursor.getColumnIndex(GIISContract.AgeDefinitionsTable.ID)));
                    row.setName(cursor.getString(cursor.getColumnIndex(GIISContract.AgeDefinitionsTable.NAME)));
                    list.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    private class VQAgeDefinitions{
        private String name , id;

        private VQAgeDefinitions() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class filterList extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewAppointmentRow> mVar;


        @Override
        protected void onPreExecute() {
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            String ageName = params[0];
            String startRow = params[1];
            currentCount = params[1];

            Cursor cursor;
            mVar = new ArrayList<>();

            String SQLVaccinationQueue =
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
            Log.e("SQLVaccinationQueue", SQLVaccinationQueue);
            cursor = this_database.getReadableDatabase().rawQuery(SQLVaccinationQueue, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                        row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                        row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                        row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                        row.setChild_id(cursor.getString(cursor.getColumnIndex("CHILD_ID")));
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
            adapter.updateData(mVar);
            loadingBar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

}