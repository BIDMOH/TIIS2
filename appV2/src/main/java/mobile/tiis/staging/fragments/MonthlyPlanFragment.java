package mobile.tiis.staging.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.ChildDetailsActivity;
import mobile.tiis.staging.CustomViews.NestedListView;
import mobile.tiis.staging.R;
import mobile.tiis.staging.adapters.AdapterVaccineNameQuantity;
import mobile.tiis.staging.adapters.SingleTextViewAdapter;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.GIISContract;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.util.ViewAppointmentRow;
import mobile.tiis.staging.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by issymac on 16/12/15.
 */
public class MonthlyPlanFragment extends RxFragment {
    private static final String TAG = MonthlyPlanFragment.class.getSimpleName();
    private List<String> ages;

    private NestedListView lvMonthlyPlanList;

    private TableLayout monthlyPlanTable;


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

    public View loadingBar;
    public ImageView previousTenItems, nextTenItems;
    public String childBarcode = "";


    final DatePickerDialog fromDatePicker = new DatePickerDialog();
    final DatePickerDialog toDatePicker = new DatePickerDialog();
    private MaterialEditText metDOBFrom, metDOBTo;
    private String toDateString = "", fromDateString = "";

    private EditText editTextUsedToRequestFocus;

    private int selectedPage = 0;
    private int previousSelectedPage = 0;
    private int entriesPerPage = 20;
    private List<TextView> indicatorItems = new ArrayList<>();
    private LinearLayout llPagesContainer;
    private String selectedAgeDefinition = "";

    private LayoutInflater inflater;

    private CardView previousCard, nextCard;

    //Dose Quantity VAriables
    private ListView lvNameQuantity;
    private RelativeLayout progressBarLayout;
    private LinearLayout listAndTitleWrapper;

    private boolean blockPrevious = false;
    private boolean blockNext = false;
    private Looper backgroundLooper;

    private Subscription subscription;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_monthly_plan, null);
        setUpView(root);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();


        this.inflater = inflater;

        app = (BackboneApplication) this.getActivity().getApplication();
        this_database = app.getDatabaseInstance();
        ageDef = getAllAgeDeffinitions();

        var = new ArrayList<>();
        llPagesContainer = (LinearLayout)root. findViewById(R.id.ll_pages_container);
        listviewFooter = inflater.inflate(R.layout.loading_list_footer, null);
        previous = (ImageButton) listviewFooter.findViewById(R.id.previous_10_contents);
        next = (ImageButton) listviewFooter.findViewById(R.id.next_10_contents);
        prevLayout = (RelativeLayout) listviewFooter.findViewById(R.id.prev_rl);
        nextLayout = (RelativeLayout) listviewFooter.findViewById(R.id.next_rl);

        editTextUsedToRequestFocus = (EditText) root.findViewById(R.id.edit_text_used_to_request_focus2);
        editTextUsedToRequestFocus.requestFocus();


        nextTenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count + 10;
                currentCount = count + "";
                filterList(currentCategory, currentCount, fromDateString, toDateString);
            }
        });

        previousTenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count - 10;
                currentCount = count + "";
                filterList(currentCategory, currentCount, fromDateString, toDateString);
            }
        });


        metDOBFrom = (MaterialEditText) root.findViewById(R.id.met_dob_from);
        metDOBTo = (MaterialEditText) root.findViewById(R.id.met_dob_value);

        metDOBTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
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
                                filterList(currentCategory, "0", fromDateString, toDateString);
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
            }
        });


        metDOBFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
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
                            editTextUsedToRequestFocus.requestFocus();

                            if (!toDateString.equals("")) {
                                filterList(currentCategory, "0", fromDateString, toDateString);
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
            }
        });


        ArrayList<String> ageDefStr = new ArrayList<>();
        ageDefStr.add("--------");
        for (VQAgeDefinitions vqad : ageDef) {
            ageDefStr.add(vqad.getName());
        }

        SingleTextViewAdapter spinnerAdapter = new SingleTextViewAdapter(MonthlyPlanFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, ageDefStr);
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

        agesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedPage = 0;
                previousSelectedPage = 0;
                if (position > 0) {
                    Log.d("day13", "spinner selected");
                    selectedAgeDefinition = ageDef.get(position - 1).getName();
                    currentCategory = selectedAgeDefinition;
//                    populatePageIndicatorContainer(getNumPages(selectedAgeDefinition));
//                    compileVaccinationQueueTable(selectedAgeDefinition, selectedPage);
                    filterList(currentCategory, "0", fromDateString, toDateString);
                } else {
                    selectedAgeDefinition = "";
                    currentCategory = selectedAgeDefinition;
                    filterList(currentCategory, "0", fromDateString, toDateString);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        vaccineQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (var.size() > 0 ){

                    try {
                        AlertDialog.Builder keyBuilder = new AlertDialog.Builder(MonthlyPlanFragment.this.getActivity());
                        keyBuilder
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        final View dialogLayout = inflater.inflate(R.layout.vaccination_quantity_custom_dialog, null);
                        lvNameQuantity = (ListView) dialogLayout.findViewById(R.id.lv_result);
                        progressBarLayout  = (RelativeLayout) dialogLayout.findViewById(R.id.progress_bar_layout);
                        listAndTitleWrapper = (LinearLayout) dialogLayout.findViewById(R.id.list_and_title_wrapper);
                        ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> list;

                        new AsyncTask<Void, Void, ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity>>(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();

                                listAndTitleWrapper.setVisibility(View.GONE);
                                progressBarLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> doInBackground(Void... voids) {
                                ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> list = this_database.getQuantityOfVaccinesNeededMonthlyPlan(app.getLOGGED_IN_USER_HF_ID(), currentCategory, fromDateString, toDateString);
                                return list;
                            }

                            @Override
                            protected void onPostExecute(ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> mList) {
                                progressBarLayout.setVisibility(View.GONE);
                                listAndTitleWrapper.setVisibility(View.VISIBLE);
                                Context ctx = getActivity().getApplicationContext();
                                AdapterVaccineNameQuantity adapter = new AdapterVaccineNameQuantity(ctx, R.layout.item_vaccine_name_quantity, mList);
                                lvNameQuantity.setAdapter(adapter);
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        keyBuilder.setView(dialogLayout);
                        AlertDialog dialog = keyBuilder.create();
                        dialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        filterList(currentCategory, "0", "", "");

        return root;
    }

    public void setUpView(View v) {
        lvMonthlyPlanList = (NestedListView) v.findViewById(R.id.lv_monthly_plan);
        monthlyPlanTable = (TableLayout) v.findViewById(R.id.monthly_plan_table);
        agesSpinner = (MaterialSpinner) v.findViewById(R.id.age_spinner);
        vaccineQuantityButton = (Button) v.findViewById(R.id.vac_qnt_btn);
        loadingBar = v.findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.GONE);

        previousTenItems = (ImageView) v.findViewById(R.id.previous_10_contents);
        nextTenItems    = (ImageView) v.findViewById(R.id.next_10_contents);
        previousCard    = (CardView) v.findViewById(R.id.prev_card);
        nextCard        = (CardView) v.findViewById(R.id.next_card);

    }

    private ArrayList<VQAgeDefinitions> getAllAgeDeffinitions() {

        ArrayList<VQAgeDefinitions> list = new ArrayList<>();
        String SQLAgeDef = "SELECT * from " + SQLHandler.Tables.AGE_DEFINITIONS;
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

    private class VQAgeDefinitions {
        private String name, id;

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


    private void filterList(final String ageName,final String currentC,final String fromDate,final String toDate){

        loadingBar.setVisibility(View.VISIBLE);
        lvMonthlyPlanList.setVisibility(View.GONE);

        subscription = Observable.defer(new Func0<Observable<ArrayList<ViewAppointmentRow>>>() {
            @Override
            public Observable<ArrayList<ViewAppointmentRow>> call() {
                // Do some long running operation

                if(!currentC.equals(""))
                    currentCount = currentC;

                long t = Calendar.getInstance().getTimeInMillis()/1000;
                long t1 = (t + (30 * 24 * 60 * 60));
                long t2 = (t - (30 * 24 * 60 * 60));
                String to_date =  t1+ "";
                String from_date = t2+ "";

                try {
                    if (!fromDate.equals("") && !toDate.equals("")) {
                        from_date = (Long.parseLong(fromDate)-(24*60*60))+"";
                        Log.d("day13", "from picker : "+from_date);
                        to_date = toDate;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Cursor cursor;
                ArrayList<ViewAppointmentRow> mVar = new ArrayList<>();

                BackboneApplication application = (BackboneApplication) MonthlyPlanFragment.this.getActivity().getApplication();
                DatabaseHandler mydb            = application.getDatabaseInstance();

                String SQLVaccinationQueue =
                        "SELECT DISTINCT APPOINTMENT_ID, CHILD_ID " +
                                " , SCHEDULE, SCHEDULED_DATE " +
                                " FROM MONTHLY_PLAN join dose on DOSE_ID = dose.ID" +
                                " WHERE HEALTH_FACILITY_ID = '" + app.getLOGGED_IN_USER_HF_ID() + "' AND SCHEDULE like '%" + ageName + "%' " +
                                " AND (substr(SCHEDULED_DATE,7,10)) >= ('" +from_date+ "') " +
                                " AND (substr(SCHEDULED_DATE,7,10)) <= ('" +to_date+ "') " +
                                " AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL " +
                                " OR (datetime(substr(SCHEDULED_DATE,7,10),'unixepoch') >= datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                                " GROUP BY APPOINTMENT_ID, SCHEDULED_DATE, DOMICILE, NAME, SCHEDULE, CHILD_ID, SCHEDULE_ID " +
                                " ORDER BY SCHEDULED_DATE "+
                                " LIMIT " + currentCount + ", 10; ";



                Log.e("optimization", SQLVaccinationQueue);
                long tStart = System.currentTimeMillis();
                cursor = mydb.getReadableDatabase().rawQuery(SQLVaccinationQueue, null);

                if (currentCount.equals("0")){
                    blockPrevious = true;
                }else {
                    blockPrevious = false;
                }

                if (cursor.getCount() < 10 && blockPrevious == false){
                    blockNext = true;
                }else {
                    blockNext = false;
                }

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
                        do {
                            Log.d("optimization", "the loop here");
                            ViewAppointmentRow row = new ViewAppointmentRow();
                            row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                            row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                            row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                            row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                            row.setChild_id(cursor.getString(cursor.getColumnIndex("CHILD_ID")));
                            mVar.add(row);
                        } while (cursor.moveToNext());
                        Log.e("optimization", "Looping to get data  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));


                    }
                    cursor.close();
                }
                return Observable.just(mVar);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(Object mVar) {
                        Log.d(TAG, "onNext(" + mVar + ")");

                        var = (ArrayList<ViewAppointmentRow>)mVar;
                        displayMonthlyPlanList(var);
                        loadingBar.setVisibility(View.GONE);
                        lvMonthlyPlanList.setVisibility(View.VISIBLE);
                        if (blockPrevious) {
                            previousCard.setVisibility(View.INVISIBLE);
                        }
                        else{
                            previousCard.setVisibility(View.VISIBLE);
                        }
                        if (blockNext){
                            nextCard.setVisibility(View.INVISIBLE);
                        }
                        else {
                            nextCard.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void displayMonthlyPlanList(ArrayList<ViewAppointmentRow> mVar) {
        ArrayList<ViewAppointmentRow> nVar = mVar;
        monthlyPlanTable.removeAllViews();
        for (final ViewAppointmentRow a : nVar) {

            View convertView = inflater.inflate(R.layout.monthly_plan_list_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.vacc_txt_child_names);
            name.setTypeface(BackboneActivity.Rosario_Regular);
            TextView age = (TextView) convertView.findViewById(R.id.age);
            age.setTypeface(BackboneActivity.Rosario_Regular);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            date.setTypeface(BackboneActivity.Rosario_Regular);

            DatabaseHandler db = app.getDatabaseInstance();
            String barcode_id = "";
            Cursor naming = null;
            naming = db.getReadableDatabase().rawQuery("SELECT BARCODE_ID, FIRSTNAME1 , LASTNAME1,FIRSTNAME2 FROM child WHERE ID=?", new String[]{a.getChild_id()});
            if (naming != null) {
                if (naming.moveToFirst()) {
                    String childName = "";
                    if (!(naming.getString(naming.getColumnIndex("FIRSTNAME1")) == null && naming.getString(naming.getColumnIndex("FIRSTNAME2")) == null && naming.getString(naming.getColumnIndex("LASTNAME1")) ==  null)){
                        childName = naming.getString(naming.getColumnIndex("FIRSTNAME1"))+ " "+naming.getString(naming.getColumnIndex("FIRSTNAME2"))+" "+naming.getString(naming.getColumnIndex("LASTNAME1"));
                    }
                    name.setText(childName);
                    barcode_id = naming.getString(naming.getColumnIndex("BARCODE_ID"));
                    setChildBarcode(barcode_id);
                }
                naming.close();
            }

            age.setText(a.getSchedule());

            Date scheduled_date = BackboneActivity.dateParser(a.getScheduled_date());
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            date.setText(ft.format(scheduled_date));

            convertView.setOnClickListener(new View.OnClickListener() {
                String barcodeId = getChildBarcode();
                @Override
                public void onClick(View v) {
                    Intent childDetailsActivity = new Intent(MonthlyPlanFragment.this.getActivity(), ChildDetailsActivity.class);
                    childDetailsActivity.putExtra("barcode", barcodeId);
                    childDetailsActivity.putExtra("current", 2);
                    childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, a.getChild_id());
                    startActivity(childDetailsActivity);

                }

            });

            monthlyPlanTable.addView(convertView);
        }


    }

    public void setChildBarcode(String bc){
        childBarcode = bc;
    }

    public String getChildBarcode(){
        return childBarcode;
    }

    private int getNumPages(String ageName){
        int numPages = 0;
        int numEntries = 0;
        Cursor cursor;
        var = new ArrayList<>();

        String SQLVaccinationQueue =
                "SELECT count(DISTINCT APPOINTMENT_ID)" +
                        " FROM monthly_plan join dose on DOSE_ID = dose.ID" +
                        " WHERE HEALTH_FACILITY_ID = '" + app.getLOGGED_IN_USER_HF_ID()+"' AND SCHEDULE like '%"+ageName+"%' AND datetime(substr(SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') AND datetime(substr(SCHEDULED_DATE,7,10), 'unixepoch') > datetime('now','-30 days')"+
                        " AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                        " OR (datetime(substr(SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                        " ";
        Log.e("SQLVaccinationQueue",SQLVaccinationQueue);
        cursor = this_database.getReadableDatabase().rawQuery(SQLVaccinationQueue, null);
        if (cursor != null) {
            cursor.moveToFirst();
            numEntries = cursor.getInt(0);
            cursor.close();
        }

        return numPages = numEntries/entriesPerPage + ((numEntries%entriesPerPage>0)?1:0);
    }

    private void populatePageIndicatorContainer(int numPages){
        llPagesContainer.removeAllViews();
        Context context = MonthlyPlanFragment.this.getActivity();
        final TextView tvPagesLabel = new TextView(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margins = (int)getResources().getDimension(R.dimen.ten_dp_width);
        p.setMargins(margins, 0, margins, 0);
        tvPagesLabel.setLayoutParams(p);
        tvPagesLabel.setVisibility(View.GONE);
        tvPagesLabel.setText(getString(R.string.pages));
        tvPagesLabel.setTextSize(getResources().getDimension(R.dimen.fifteen_dp_width));
        tvPagesLabel.setTextColor(Color.BLACK);
        tvPagesLabel.setOnClickListener(indicatorListener);
        llPagesContainer.addView(tvPagesLabel);


        indicatorItems.clear();
        for (int i = 0; i < numPages; i++) {
            final TextView textView = new TextView(context);
            textView.setLayoutParams(p);
            textView.setText("" + (i + 1));
            textView.setTextSize(getResources().getDimension(R.dimen.thirty_seven_dp_width));
            if ( i == 0){
                textView.setTextColor(Color.BLUE);
            } else {
                textView.setTextColor(Color.GRAY);
            }
            textView.setOnClickListener(indicatorListener);
            llPagesContainer.addView(textView);
            indicatorItems.add(textView);
        }
    }

    View.OnClickListener indicatorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = indicatorItems.indexOf(v);
            previousSelectedPage = selectedPage;
            selectedPage = index;
            indicatorItems.get(previousSelectedPage).setTextColor(Color.GRAY);
            indicatorItems.get(selectedPage).setTextColor(Color.BLUE);
//            compileVaccinationQueueTable(selectedAgeDefinition, selectedPage);
//            new filterList().execute(currentCategory, "0", "", "");
        }
    };


    private void compileVaccinationQueueTable(String ageName , int page) {

        monthlyPlanTable.removeAllViews();

        Cursor cursor;
        var = new ArrayList<>();

        String SQLVaccinationQueue =
                "SELECT DISTINCT APPOINTMENT_ID, CHILD_ID "+
                        " ,(SELECT GROUP_CONCAT(dose.FULLNAME) FROM vaccination_event INNER JOIN dose ON vaccination_event.DOSE_ID = dose.ID left join age_definitions on dose.TO_AGE_DEFINITON_ID = age_definitions.ID WHERE monthly_plan.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') AND vaccination_event.IS_ACTIVE='true' AND vaccination_event.VACCINATION_STATUS='false' AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) AND (DAYS IS NULL or (datetime(substr(vaccination_event.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || DAYS || ' days')) )) AS VACCINES " +
                        " , SCHEDULE, SCHEDULED_DATE "+
                        " FROM monthly_plan join dose on DOSE_ID = dose.ID" +
                        " WHERE HEALTH_FACILITY_ID = '"+app.getLOGGED_IN_USER_HF_ID()+"' AND SCHEDULE like '%"+ageName+"%' AND datetime(substr(SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') "+
                        " AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                        " OR (datetime(substr(SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                        " GROUP BY APPOINTMENT_ID, SCHEDULED_DATE, DOMICILE, NAME, SCHEDULE, CHILD_ID, SCHEDULE_ID "+
                        " ORDER BY SCHEDULED_DATE" +
                        " limit "+ (page * 10) +","+ entriesPerPage +"; ";
        Log.e("day13",SQLVaccinationQueue);
        Log.d("day13", "New implementation");
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
                    var.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        //Show loading dialog
        ProgressDialog progressDialog =  new ProgressDialog(MonthlyPlanFragment.this.getActivity(), 0);
        progressDialog.setMessage("Loading table from database...");
        progressDialog.show();

        displayMonthlyPlanList(var);

        progressDialog.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            subscription.unsubscribe();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}