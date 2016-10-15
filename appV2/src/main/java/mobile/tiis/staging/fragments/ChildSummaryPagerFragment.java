package mobile.tiis.staging.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.staging.ChildDetailsActivity;
import mobile.tiis.staging.R;
import mobile.tiis.staging.adapters.PlacesOfBirthAdapter;
import mobile.tiis.staging.adapters.SingleTextViewAdapter;
import mobile.tiis.staging.adapters.VaccinationHistoryListAdapter;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.entity.Birthplace;
import mobile.tiis.staging.entity.Child;
import mobile.tiis.staging.entity.HealthFacility;
import mobile.tiis.staging.entity.Place;
import mobile.tiis.staging.entity.Status;
import mobile.tiis.staging.util.ViewAppointmentRow;
import mobile.tiis.staging.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

import static mobile.tiis.staging.ChildDetailsActivity.childId;

/**
 *  Created by issymac on 25/01/16.
 */

public class ChildSummaryPagerFragment extends RxFragment {
    private static final String TAG = ChildSummaryPagerFragment.class.getSimpleName();

    private static final String ARG_POSITION = "position";

    private static final String VALUE = "value";
    private static final String NEW_REGISTERED_CHILD = "new_registered_child";

    private int position;

    private Date bdate;

    private long birthDatesDiff = 0;

    public Child currentChild;

    private String hf_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    private ArrayList<String> gender, vvuStatusList, tt2StatusList, registryYearList;

    private String localBarcode = "";

    private String tempIdOrig, firstnameOrig, lastnameOrig, birthdateOrig, motherFirOrig, motherLastOrig, phoneOrig, notesOrig, barcodeOrig,firstname2Orig, vvuStatusOrig, tt2StatusOrig, childCummulativeSnOrig, childRegistryYearOrig;

    private int birthplaceOrig, villageOrig, healthFacOrig, statusOrig, genderOrig;

    private int notApplicablePos = -1;

    private List<Place> placeList;

    private List<Birthplace> birthplaceList;

    private List<Status> statusList;

    private String  childRegistryYear;

    private ArrayList<ViewAppointmentRow> var;

    private Thread thread;

    private String value;

    private boolean editable = false;

    private MaterialEditText metBarcodeValue, metFirstName, metNotesValue,metMiddleName, metLastName, metMothersFirstName, metMothersSurname, metPhoneNumber, metDOB, metCummulativeSn;

    private VaccinationHistoryListAdapter adapter;

    private PlacesOfBirthAdapter spinnerAdapter, vvuSpinnerAdapter, tt2SpinnerAdapter, registryYearAdapter;

    private ListView lvImmunizationHistory;

    private TableLayout summaryTableLayout;

    private Button editButton, saveButton;

    private MaterialSpinner ms, pobSpinner, villageSpinner, statusSpinner, VVUSpinner, TT2Spinner, registryYearSpinner;

    private AutoCompleteTextView healthFacilitySpinner;
    private DatabaseHandler mydb;

    private  String registerHealthFacilityId="";

    private BackboneApplication app;

    private List<String> place_names;

    final DatePickerDialog doBDatePicker = new DatePickerDialog();

    private  View header,appointmentTableHeader;

    /** childWithEditableChildCumulativeSnAndChildRegistryYear is a boolean value used to specify children whose child cumulative registration numbers should not be editable
     * these children include children from outside catchment whereby child cumulative number and child registration year should be left unchanged and
     * new registered children who have not been synched to the server, child cumulative number and child registration year should also be left unchaged
     */
    private boolean childWithEditableChildCumulativeSnAndChildRegistryYear;
    private Looper backgroundLooper;
    private boolean barcodeChanged = false;
    private boolean registryYearChanged = false;
    private boolean cummulativeSnChanged = false;

    private ViewGroup viewGroup;
    private Bundle saveBundle;

    private LayoutInflater inflator;


    private List<HealthFacility> healthFacilityList;
    private List<HealthFacility> districtCouncilsList;
    private List<String> healthFacilities,healthFacilitiesNames;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    public static ChildSummaryPagerFragment newInstance(int position, String value, boolean isNewChilc) {
        ChildSummaryPagerFragment f = new ChildSummaryPagerFragment();
        Bundle b                    = new Bundle();
        b                           .putInt(ARG_POSITION, position);
        b                           .putString(VALUE, value);
        b                           .putBoolean(NEW_REGISTERED_CHILD, isNewChilc);
        f                           .setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position    = getArguments().getInt(ARG_POSITION);
        value     = getArguments().getString(VALUE);
        childWithEditableChildCumulativeSnAndChildRegistryYear = getArguments().getBoolean(NEW_REGISTERED_CHILD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v;
        inflator = inflater;
        viewGroup = container;
        saveBundle = savedInstanceState;
        v = (ViewGroup) inflater.inflate(R.layout.fragment_child_summary, null);
        app = (BackboneApplication) ChildSummaryPagerFragment.this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        setUpView(v);

        gender = new ArrayList<>();
        gender.add("Male");
        gender.add("Female");

        tt2StatusList   = new ArrayList<>();
        tt2StatusList.add("Ndio");
        tt2StatusList.add("Hapana");
        tt2StatusList.add("Sijui");

        vvuStatusList   = new ArrayList<>();
        vvuStatusList.add("1");
        vvuStatusList.add("2");
        vvuStatusList.add("U");

        registryYearList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());


        int y =c.get(Calendar.YEAR);

        Log.d("time","year = "+y);
        while(y>2010){
            registryYearList.add(y+"");
            y--;
        }

        spinnerAdapter      = new PlacesOfBirthAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, gender);
        vvuSpinnerAdapter   = new PlacesOfBirthAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, vvuStatusList);
        tt2SpinnerAdapter   = new PlacesOfBirthAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, tt2StatusList);
        registryYearAdapter = new PlacesOfBirthAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, registryYearList);

        header = (View) inflater.inflate(R.layout.childinfo_summary_header, null);

        lvImmunizationHistory = (ListView) v.findViewById(R.id.vaccination_history_list);
        metBarcodeValue     = (MaterialEditText) header.findViewById(R.id.met_barcode_value);
        metFirstName        = (MaterialEditText) header.findViewById(R.id.met_fname_value);
        metMiddleName       = (MaterialEditText) header.findViewById(R.id.met_mname_value);
        metLastName         = (MaterialEditText) header.findViewById(R.id.met_surname_value);
        metMothersFirstName = (MaterialEditText) header.findViewById(R.id.met_mother_fname_value);
        metMothersSurname   = (MaterialEditText) header.findViewById(R.id.met_mother_surname_value);
        metPhoneNumber      = (MaterialEditText) header.findViewById(R.id.met_phone_value);
        metDOB              = (MaterialEditText) header.findViewById(R.id.met_dob_value);
        metNotesValue       = (MaterialEditText) header.findViewById(R.id.met_notes_value);
        metCummulativeSn    = (MaterialEditText) header.findViewById(R.id.met_cummulative_sn);

        ms                  = (MaterialSpinner) header.findViewById(R.id.spin_gender);
        pobSpinner          = (MaterialSpinner) header.findViewById(R.id.spin_pob);
        villageSpinner      = (MaterialSpinner) header.findViewById(R.id.spin_village);
        healthFacilitySpinner=(AutoCompleteTextView) header.findViewById(R.id.spin_health_facility);
        statusSpinner       = (MaterialSpinner) header.findViewById(R.id.spin_status);
        VVUSpinner          = (MaterialSpinner) header.findViewById(R.id.spin_vvu_status);
        TT2Spinner          = (MaterialSpinner) header.findViewById(R.id.spin_tt2_status);
        registryYearSpinner = (MaterialSpinner) header.findViewById(R.id.spin_register_year);

        TT2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){
                    tt2StatusOrig = tt2StatusList.get(position - 1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        VVUSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    vvuStatusOrig = vvuStatusList.get(position - 1);
                }
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registryYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    registryYearChanged = true;
                    childRegistryYearOrig = registryYearList.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        metCummulativeSn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cummulativeSnChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(childWithEditableChildCumulativeSnAndChildRegistryYear){
            registryYearSpinner.setVisibility(View.GONE);
            metCummulativeSn.setVisibility(View.GONE);
        }
        editButton          = (Button) header.findViewById(R.id.edit_button);
        saveButton          = (Button) header.findViewById(R.id.save_button);

        metDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editable) {
                    doBDatePicker.show(((Activity) getActivity()).getFragmentManager(), "DatePickerDialogue");
                    doBDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                            Cursor vacinationCursor = mydb.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + SQLHandler.Tables.VACCINATION_EVENT +
                                            " where " + SQLHandler.VaccinationEventColumns.CHILD_ID + "=? and " +
                                            SQLHandler.VaccinationEventColumns.VACCINATION_STATUS + "= 'true'",
                                    new String[]{currentChild.getId()});
                            vacinationCursor.moveToFirst();
                            if (vacinationCursor.getInt(0) > 0) {
                                //TODO : Something has to be done here
                                final AlertDialog ad2 = new AlertDialog.Builder((Activity)getActivity()).create();
                                ad2.setTitle(getResources().getString(R.string.error_editing_dob));
                                ad2.setMessage(getResources().getString(R.string.error_message_editing_dob));
                                ad2.setButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ad2.dismiss();
                                    }
                                });
                                ad2.show();

                                return;
                            }


                            metDOB.setText((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1) + "-" + year);
                            Calendar toCalendar = Calendar.getInstance();
                            toCalendar.set(year, monthOfYear, dayOfMonth);
                            bdate = toCalendar.getTime();
                        }

                    });
                }
            }
        });

        summaryTableLayout.addView(header);
        appointmentTableHeader = inflater.inflate(R.layout.appointment_table_header, null);
        summaryTableLayout.addView(appointmentTableHeader);
        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                Cursor mCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                        new String[]{String.valueOf(value)});
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    currentChild = getChildFromCursror(mCursor);
                }
                mCursor.close();
                placeList = mydb.getAllPlaces();

                birthplaceList = mydb.getAllBirthplaces();

                healthFacilityList = mydb.getAllHealthFacility();
                districtCouncilsList = mydb.getAllDistrictCoucils();

                statusList = mydb.getStatus();
                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        enableUserInputs(false);
                        fillUIElements();
                        updateAppointmentTable();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(Boolean string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                });



        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableUserInputs(true);
                app.saveNeeded = true;
                cummulativeSnChanged    = false;
                registryYearChanged     = false;

                editButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataIntegrityBeforeSave()) {
                    if (!localBarcode.equals(metBarcodeValue.getText().toString()) && !localBarcode.equals("")) {
                        showAlertThatChildHadABarcode();
                        enableUserInputs(false);
                    } else {
                        editButton.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                        saveChangedData();
                        enableUserInputs(false);
                        ChildDetailsActivity.changeTitle(metFirstName.getText().toString()+" "+metMiddleName.getText().toString()+" "+metLastName.getText().toString());
                    }
                }
            }
        });

        return v;
    }

    public void updateAppointmentTable(){
        Log.d("delay","updating appointment table");
        var = new ArrayList<ViewAppointmentRow>();
        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                Cursor mCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                        new String[]{String.valueOf(value)});
                if (mCursor.getCount() > 0) {
                    Log.d("delay","updating appointment table with child id = "+value);
                    mCursor.moveToFirst();
                    currentChild = getChildFromCursror(mCursor);
                }else{

                    //at times when the child has just been registered on the tablet, he/she is assigned a temp id which is later by the id received from the server
                    //to ensure updating of appointment information of child we reobtain the child from the database by his/her barcode inorder to show his/her appointments.
                    Cursor c = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                            new String[]{String.valueOf(barcodeOrig)});
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        currentChild = getChildFromCursror(c);
                        Log.d("delay","updating appointment table with barcode id = "+barcodeOrig+" AND childID = "+currentChild.getId());
                    }

                }
                mCursor.close();

                if (currentChild.getId() != null && !currentChild.getId().isEmpty()) {
                    childId = currentChild.getId();

                    DatabaseHandler this_database = app.getDatabaseInstance();
                    SQLHandler handler = new SQLHandler();
                    Cursor cursor = this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[]{childId, childId});

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            Log.d("delay","adding vaccination appointments = "+barcodeOrig);
                            do {
                                ViewAppointmentRow row = new ViewAppointmentRow();
                                row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                                row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                                row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                                row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                                var.add(row);
                            } while (cursor.moveToNext());
                        }
                    }
                    cursor.close();

                }

                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        fillAppointmentTableLayout();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(Boolean string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                });
    }

    private void fillAppointmentTableLayout(){
        for (ViewAppointmentRow va : var){
            View convertView = inflator.inflate(R.layout.vaccination_history_item, null);

            TextView antigen = (TextView) convertView.findViewById(R.id.antigen_value);
            TextView week0 = (TextView) convertView.findViewById(R.id.week_o_value);
            TextView week2 = (TextView) convertView.findViewById(R.id.week_2_value);
            TextView month1 = (TextView) convertView.findViewById(R.id.month_1_value);
            TextView week6 = (TextView) convertView.findViewById(R.id.week_6_value);
            TextView week10 = (TextView) convertView.findViewById(R.id.week_10_value);
            TextView week14 = (TextView) convertView.findViewById(R.id.week_14_value);
            TextView month9 = (TextView) convertView.findViewById(R.id.month_9_value);
            TextView month18= (TextView) convertView.findViewById(R.id.month_18_value);
            TextView month21= (TextView) convertView.findViewById(R.id.month_21_value);

            antigen.setText(va.getVaccine_dose());

            Date scheduled_date = BackboneActivity.dateParser(va.getScheduled_date());
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");

            switch (va.getSchedule()) {
                case "At birth":
                    week0.setText(ft.format(scheduled_date));
                    break;
                case "2 weeks":
                    week2.setText(ft.format(scheduled_date));
                    break;
                case "1 Month":
                    month1.setText(ft.format(scheduled_date));
                    break;
                case "6 weeks":
                    week6.setText(ft.format(scheduled_date));
                    break;
                case "10 weeks":
                    week10.setText(ft.format(scheduled_date));
                    break;
                case "14 weeks":
                    week14.setText(ft.format(scheduled_date));
                    break;
                case "9 Months":
                    month9.setText(ft.format(scheduled_date));
                    break;
                case "18 Months":
                    month18.setText(ft.format(scheduled_date));
                    break;
                case "21 Months":
                    month21.setText(ft.format(scheduled_date));
                    break;
            }

            summaryTableLayout.addView(convertView);

        }

    }

    public void enableUserInputs(boolean fieldStatus){
        editable = fieldStatus;
        Log.d("childSumarry","Child registry year = "+childRegistryYearOrig);
        if(fieldStatus) {
            if (tt2StatusOrig.equals(""))
                TT2Spinner.setAdapter(tt2SpinnerAdapter);

            if (vvuStatusOrig.equals(""))
                VVUSpinner.setAdapter(vvuSpinnerAdapter);

            if (childRegistryYearOrig.equals("")) {
                registryYearSpinner.setAdapter(registryYearAdapter);
            }
        }

        metBarcodeValue .setFocusableInTouchMode(fieldStatus);
        metFirstName    .setFocusableInTouchMode(fieldStatus);
        metMiddleName   .setFocusableInTouchMode(fieldStatus);
        metLastName     .setFocusableInTouchMode(fieldStatus);
        metNotesValue     .setFocusableInTouchMode(fieldStatus);
        healthFacilitySpinner.setEnabled(fieldStatus);

        metMothersFirstName     .setFocusableInTouchMode(fieldStatus);
        metMothersSurname       .setFocusableInTouchMode(fieldStatus);
        metPhoneNumber          .setFocusableInTouchMode(fieldStatus);
        metDOB                  .setFocusableInTouchMode(false);
        metCummulativeSn        .setFocusableInTouchMode(fieldStatus);

        ms                      .setEnabled(fieldStatus);
        pobSpinner              .setEnabled(fieldStatus);
        villageSpinner          .setEnabled(fieldStatus);
        statusSpinner           .setEnabled(fieldStatus);
        VVUSpinner              .setEnabled(fieldStatus);
        TT2Spinner              .setEnabled(fieldStatus);
        registryYearSpinner     .setEnabled(fieldStatus);

        if(!fieldStatus){
            ms.setBaseColor(R.color.card_light_text);
            pobSpinner.setBaseColor(R.color.card_light_text);
            villageSpinner.setBaseColor(R.color.card_light_text);
            statusSpinner.setBaseColor(R.color.card_light_text);
        }else{
            ms.setBaseColor(R.color.black);
            pobSpinner.setBaseColor(R.color.black);
            villageSpinner.setBaseColor(R.color.black);
            statusSpinner.setBaseColor(R.color.black);
        }

    }

    private void loadViewAppointementsTable(Boolean b){
        DatabaseHandler this_database = app.getDatabaseInstance();
        SQLHandler handler = new SQLHandler();

        var = new ArrayList<ViewAppointmentRow>();
        String result = "";


        if (currentChild.getId() != null && !currentChild.getId().isEmpty()) {
            childId = currentChild.getId();

            Cursor cursor = null;
            cursor = this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[]{childId, childId});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(cursor.getString(cursor.getColumnIndex("APPOINTMENT_ID")));
                        row.setVaccine_dose(cursor.getString(cursor.getColumnIndex("VACCINES")));
                        row.setSchedule(cursor.getString(cursor.getColumnIndex("SCHEDULE")));
                        row.setScheduled_date(cursor.getString(cursor.getColumnIndex("SCHEDULED_DATE")));
                        var.add(row);
                    } while (cursor.moveToNext());
                }
            }

        }

        adapter = new VaccinationHistoryListAdapter(ChildSummaryPagerFragment.this.getActivity(), var, app);
        lvImmunizationHistory.setAdapter(adapter);

    }

    public void setUpView(View v){
        summaryTableLayout      = (TableLayout) v.findViewById(R.id.child_summary_table_layout);
    }

    private String getDistrictCouncilName(String id){
        String name = "";
        for (HealthFacility districtCouncil : districtCouncilsList){
            if(districtCouncil.getId().equals(id)) {
                name = districtCouncil.getName();
            }

        }
        return name;
    }

    private void fillUIElements(){
        if (currentChild!=null){
            Log.d("issy", "child gotten "+currentChild.getFirstname1());
            if (currentChild.getBarcodeID() == null || currentChild.getBarcodeID().isEmpty()) {
                Toast.makeText(ChildSummaryPagerFragment.this.getActivity(), getString(R.string.empty_barcode), Toast.LENGTH_LONG).show();
            }

            localBarcode = currentChild.getBarcodeID();

            Log.d("CSN", "cummulative saved");




            try{
                if (currentChild.getChildCumulativeSn().equals("")) {
                    metCummulativeSn.setError("Please Fill this field");
                } else {
                    metCummulativeSn.setText(currentChild.getChildCumulativeSn());
                }
            }catch (NullPointerException e){
                metCummulativeSn.setError("Please Fill this field");
            }

            childCummulativeSnOrig  = currentChild.getChildCumulativeSn();

            metBarcodeValue     .setText(currentChild.getBarcodeID());
            barcodeOrig         = currentChild.getBarcodeID();
            tempIdOrig          =currentChild.getTempId();

            childId = currentChild.getId();

            metFirstName.setText(currentChild.getFirstname1());
            metNotesValue.setText(currentChild.getNotes());
            metMiddleName.setText(currentChild.getFirstname2());
            metLastName.setText(currentChild.getLastname1());

            firstnameOrig = currentChild.getFirstname1();
            firstname2Orig = currentChild.getFirstname2();
            lastnameOrig = currentChild.getLastname1();

            bdate = BackboneActivity.dateParser(currentChild.getBirthdate());
            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
            metDOB.setText(ft.format(bdate));
            birthdateOrig = ft.format(bdate);
            birthdate_val = ft.format(bdate);

            metMothersFirstName.setText(currentChild.getMotherFirstname());
            metMothersSurname.setText(currentChild.getMotherLastname());

            motherFirOrig = currentChild.getMotherFirstname();
            motherLastOrig = currentChild.getMotherLastname();

            phoneOrig = currentChild.getPhone();
            metPhoneNumber.setText(currentChild.getPhone());

            notesOrig = currentChild.getNotes();

            if (currentChild.getChildRegistryYear()!= null){
                childRegistryYearOrig = currentChild.getChildRegistryYear();

                registryYearSpinner.setAdapter(registryYearAdapter);
                registryYearSpinner.setSelection(registryYearList.indexOf(childRegistryYearOrig)+1);
            }else {
                Log.d("childSumarry","Setting Child registry year to empty ");
                childRegistryYearOrig = "";
                registryYearSpinner.setError("Please select Child's Registration Year");
            }


            if (currentChild.getMotherHivStatus()!= null){
                vvuStatusOrig = currentChild.getMotherHivStatus();

                VVUSpinner.setAdapter(vvuSpinnerAdapter);
                switch (vvuStatusOrig){
                    case "1":
                        VVUSpinner.setSelection(1);
                        break;
                    case "2":
                        VVUSpinner.setSelection(2);
                        break;
                    case "U":
                        VVUSpinner.setSelection(3);
                        break;
                }
            }else {
                vvuStatusOrig = "";
                VVUSpinner.setError("Please select mothers HIV status");
            }

            if (currentChild.getMotherTT2Status() != null){
                tt2StatusOrig   = currentChild.getMotherTT2Status() ;

                TT2Spinner.setAdapter(tt2SpinnerAdapter);
                switch (tt2StatusOrig){
                    case "Ndio":
                        TT2Spinner.setSelection(1);
                        break;
                    case "Hapana":
                        TT2Spinner.setSelection(2);
                        break;
                    case "Sijui":
                        TT2Spinner.setSelection(3);
                        break;
                }
            }else {
                tt2StatusOrig = "";
                TT2Spinner.setError("Please select mothers TT2 status");
            }

            if (Boolean.parseBoolean(currentChild.getGender())) {
                ms.setAdapter(spinnerAdapter);
                ms.setSelection(1);
                //                gender.setText("Male");
            } else {
                ms.setAdapter(spinnerAdapter);
                ms.setSelection(2);
                //                gender.setText("Female");
            }


            for(int i = 0 ; i<placeList.size();i++){
                if(placeList.get(i).getId().equals("-100")){
                    notApplicablePos = i;
                    break;
                }
            }

            place_names = new ArrayList<String>();
            for (Place element : placeList) {
                place_names.add(element.getName());
            }
            place_names.add("--------");

            List<String> birthplaceNames = new ArrayList<String>();
            for (Birthplace element : birthplaceList) {
                birthplaceNames.add(element.getName());
            }
            birthplaceNames.add("--------");

            SingleTextViewAdapter birthplaceAdapter = new SingleTextViewAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, birthplaceNames);
            pobSpinner.setAdapter(birthplaceAdapter);
            pobSpinner.setEnabled(false);

            int pos = birthplaceAdapter.getPosition(currentChild.getBirthplace());
            if (pos != -1) {
                pobSpinner.setSelection(pos+1);
                birthplaceOrig = pos;
            } else {
                pobSpinner.setSelection(birthplaceAdapter.getCount() - 1);
                birthplaceOrig = birthplaceAdapter.getCount() - 1;
            }

            SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, place_names);
            //@Teodor -> Modification -> E njejta liste si per Place of Birth dhe per Village
            villageSpinner.setAdapter(dataAdapter);
            villageSpinner.setEnabled(false);
            pos = place_names.indexOf(currentChild.getDomicile())+1;
            if (pos != -1) {
                villageSpinner.setSelection(pos);
                villageOrig = pos;
            } else {
                villageSpinner.setSelection(dataAdapter.getCount() - 1);
                villageOrig = dataAdapter.getCount() - 1;
            }

            healthFacilities = new ArrayList<>();
            healthFacilitiesNames = new ArrayList<>();
            for (HealthFacility healthFacility : healthFacilityList){
                healthFacilities.add(healthFacility.getName()+" >> "+getDistrictCouncilName(healthFacility.getParentId()));
                healthFacilitiesNames.add(healthFacility.getName());
            }

            final ArrayAdapter<String> healthAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_layout,R.id.item,healthFacilities);


            healthFacilitySpinner.setAdapter(healthAdapter);
            healthFacilitySpinner.setEnabled(false);

            healthFacilitySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    int pos = healthFacilities.indexOf(((TextView) v.findViewById(R.id.item)).getText().toString());
                    if (pos == -1) {
                        healthFacilitySpinner.setError("Error in selecting health facility");
                    } else {
                        healthFacOrig = pos;
                        registerHealthFacilityId = healthFacilityList.get(pos).getId();
                        healthFacilitySpinner.setText(healthFacilitiesNames.get(pos));
                        ((EditText)header.findViewById(R.id.focus_request_view)).requestFocus();
                    }
                }
            });
            healthFacilitySpinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        int pos = healthFacilitiesNames.indexOf(healthFacilitySpinner.getText().toString());
                        if (pos == -1) {
                            healthFacilitySpinner.setError("Error in selecting health facility");
                            registerHealthFacilityId="";
                            healthFacOrig = -1;
                        }
                    }
                }
            });

            Log.d(TAG,"current child health facility = "+currentChild.getHealthcenter());

            int index =healthFacilitiesNames.indexOf(currentChild.getHealthcenter());

            Log.d(TAG,"current child health facility index = "+index);
            if (index != -1) {
                healthFacilitySpinner.setText(healthFacilitiesNames.get(index));
                healthFacOrig = index;
                registerHealthFacilityId = healthFacilityList.get(index).getId();
            } else {
                healthFacOrig = -1;
                registerHealthFacilityId="";
            }


            List<String> status_name = new ArrayList<String>();

            for (Status element : statusList) {
                Log.d("Added status", element.getName());
                status_name.add(element.getName());
            }
            status_name.add("");


            SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(ChildSummaryPagerFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, status_name);
            statusSpinner.setAdapter(statusAdapter);
            statusSpinner.setEnabled(false);
            pos = statusAdapter.getPosition(currentChild.getStatus());


            //TODO: Check at what time is the Status of A child inserted because in the
            //TODO: current version it is not captured during registering the child

            if (pos != -1) {
                statusSpinner.setSelection(pos+1);
                statusOrig = pos;
            } else {
                statusSpinner.setSelection(statusAdapter.getCount() - 1);
                statusOrig = statusAdapter.getCount() - 1;
            }

        }
    }

    private void initListeners() {
        pobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pobSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        //        birthplace.setEnabled(true);

        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                villageSpinner.setSelection(position);
                Log.d("coze",place_names.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        //        village.setEnabled(true);

        healthFacilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                healthFacilitySpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        //        healthFacility.setEnabled(true);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                statusSpinner.setSelection(position);
                // check if status is not active, if so than block everything else for being editable
                if (statusSpinner.getSelectedItemPosition() != 2) {
                    enableUserInputs(false);
                } else {
                    enableUserInputs(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //no changes
            }

        });
        //        status.setEnabled(true);

        //if the child have done vacinations in the past we can not anymore change birthday

        //        weight.setOnClickListener(this);
        //        aefi.setOnClickListener(this);
        //        immunization_card.setOnClickListener(this);
        //        save.setOnClickListener(this);
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
        parsedChild.setChildRegistryYear(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR)));
        parsedChild.setChildCumulativeSn(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER)));
        parsedChild.setMotherHivStatus(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_VVU_STS)));
        parsedChild.setMotherTT2Status(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.MOTHER_TT2_STS)));
        Cursor cursor1 = mydb.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = mydb.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
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
        Cursor cursor4 = mydb.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

    /**
     * This funcition is for checking if the data that we are trying to update for the child are
     * accepptable.
     */
    private boolean checkDataIntegrityBeforeSave() {

        //removing focus from any edit texts
        ((EditText)header.findViewById(R.id.focus_request_view)).requestFocus();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChildSummaryPagerFragment.this.getActivity())
                .setTitle(getString(R.string.alert_empty_fields))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });
        if (metBarcodeValue.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_barcode));
            alertDialogBuilder.show();
            return false;
        }
        if (metBarcodeValue.getText().length()!=10) {
            alertDialogBuilder.setMessage(getString(R.string.barcode_ten_numbers_constraint));
            alertDialogBuilder.show();
            return false;
        }
        if (metLastName.getText().toString().isEmpty() || metLastName.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_names));
            alertDialogBuilder.show();
            return false;
        }


        if (!metBarcodeValue.getText().toString().equalsIgnoreCase(currentChild.getBarcodeID())) {
            if (mydb.isBarcodeInChildTable(metBarcodeValue.getText().toString())) {
                alertDialogBuilder.setMessage(getString(R.string.barcode_assigned));
                alertDialogBuilder.show();
                return false;
            }
        }

        if(checkIfTheEditTextContainsSpaces(metFirstName)){
            return false;
        }

        if(checkIfTheEditTextContainsSpaces(metMiddleName)){
            return false;
        }


        if(checkIfTheEditTextContainsSpaces(metLastName)){
            return false;
        }


        if (metFirstName.getText().toString().isEmpty() || metFirstName.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_names));
            alertDialogBuilder.show();
            return false;
        }

        if (metMothersFirstName.getText().toString().isEmpty() || metMothersFirstName.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_mother_names));
            alertDialogBuilder.show();
            return false;
        }


        if(checkIfTheEditTextContainsSpaces(metMothersFirstName)){
            return false;
        }


        if(checkIfTheEditTextContainsSpaces(metMothersSurname)){
            return false;
        }


        if (metMothersSurname.getText().toString().isEmpty() || metMothersSurname.getText().toString().isEmpty()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_mother_names));
            alertDialogBuilder.show();
            return false;
        }


        if (bdate.compareTo(new Date()) > 0) {
            alertDialogBuilder.setMessage(getString(R.string.future_birth_date));
            alertDialogBuilder.show();
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (pobSpinner.getSelectedItemPosition() == birthplaceList.size()+1) {
            alertDialogBuilder.setMessage(getString(R.string.empty_birthplace));
            alertDialogBuilder.show();
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        if (villageSpinner.getSelectedItemPosition() == placeList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_village));
            alertDialogBuilder.show();
            return false;
        }
        if (registerHealthFacilityId.equals("")) {
            alertDialogBuilder.setMessage(getString(R.string.empty_healthfacility));
            alertDialogBuilder.show();
            return false;
        }

        if (statusSpinner.getSelectedItemPosition() == statusList.size()) {
            alertDialogBuilder.setMessage(getString(R.string.empty_status));
            alertDialogBuilder.show();
            return false;
        }

        if (TT2Spinner.getSelectedItemPosition()==0) {
            alertDialogBuilder.setMessage("Please select mother TT2 vaccination status");
            alertDialogBuilder.show();
            TT2Spinner.setError("Please select mother TT2 vaccination status");
            return false;
        }

        if (VVUSpinner.getSelectedItemPosition()==0) {
            alertDialogBuilder.setMessage("Please select mothers VVU status");
            alertDialogBuilder.show();
            VVUSpinner.setError("Please select mothers vvu status");
            return false;
        }

//        if (registryYearSpinner.getSelectedItemPosition()==0 && !childWithEditableChildCumulativeSnAndChildRegistryYear) {
//            alertDialogBuilder.setMessage("Please select Child Registration Year");
//            alertDialogBuilder.show();
//            registryYearSpinner.setError("Please select Child Registration Year");
//            return false;
//        }

//        if (metCummulativeSn.getText().toString().equals("") && !childWithEditableChildCumulativeSnAndChildRegistryYear) {
//            alertDialogBuilder.setMessage("Please fill Child Cumulative Sn");
//            alertDialogBuilder.show();
//            metCummulativeSn.setError("Please fill Child Cumulative Sn");
//            return false;
//        }

        if(!childWithEditableChildCumulativeSnAndChildRegistryYear){
            if (cummulativeSnChanged || registryYearChanged){
                if(mydb.isChildRegistrationNoPresentInDb(registryYearList.get(registryYearSpinner.getSelectedItemPosition() - 1),metCummulativeSn.getText().toString(),metBarcodeValue.getText().toString())){
                    alertDialogBuilder.setMessage("The entered Child Cumulative Sn and Year have already been used");
                    alertDialogBuilder.show();
                    metCummulativeSn.setError("Please fill Child Cumulative Sn");
                    registryYearSpinner.setError("Please select Child Registration Year");
                    return false;
                }
            }
        }

        return true;
    }

    private void showAlertThatChildHadABarcode() {
        ChildDetailsActivity parent = (ChildDetailsActivity)ChildSummaryPagerFragment.this.getActivity();
        final AlertDialog.Builder ad = new AlertDialog.Builder(ChildSummaryPagerFragment.this.getActivity());

        ad.setTitle(getString(R.string.warning));
        ad.setMessage(getString(R.string.barcode_already_entered));
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveChangedData();
                dialog.dismiss();
                editButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.GONE);
                enableUserInputs(false);
            }
        });

        // this will solve your error
        AlertDialog alert = ad.create();
        alert.show();
        alert.getWindow().getAttributes();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(30);
    }

    /**
     * This is a method that is used to check if the user has changed any data from the data of the child
     * If yes that we save the changes, if not then we toast this.
     */
    private void saveChangedData() {
        giveValueAfterSave();
        ContentValues contentValues = new ContentValues();

        if(!childWithEditableChildCumulativeSnAndChildRegistryYear) {
            if (!registryYearList.get(registryYearSpinner.getSelectedItemPosition() - 1).equalsIgnoreCase(currentChild.getChildRegistryYear())) {
                currentChild.setChildRegistryYear(registryYearList.get(registryYearSpinner.getSelectedItemPosition() - 1));
                contentValues.put(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR, registryYearList.get(registryYearSpinner.getSelectedItemPosition() - 1));
            }


            if (!metCummulativeSn.getText().toString().equalsIgnoreCase(currentChild.getChildCumulativeSn())) {
                currentChild.setChildCumulativeSn(metCummulativeSn.getText().toString());
                Log.d("CSN", "cummulative saved" + currentChild.getChildCumulativeSn());
                contentValues.put(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER, metCummulativeSn.getText().toString());
            }
        }

        if (!vvuStatusList.get(VVUSpinner.getSelectedItemPosition()-1).equalsIgnoreCase(currentChild.getMotherHivStatus())){
            currentChild.setMotherHivStatus(vvuStatusList.get(VVUSpinner.getSelectedItemPosition()-1));
            contentValues.put(SQLHandler.ChildColumns.MOTHER_VVU_STS, vvuStatusList.get(VVUSpinner.getSelectedItemPosition()-1));
        }

        if (!tt2StatusList.get(TT2Spinner.getSelectedItemPosition()-1).equalsIgnoreCase(currentChild.getMotherTT2Status())){
            currentChild.setMotherTT2Status(tt2StatusList.get(TT2Spinner.getSelectedItemPosition()-1));
            contentValues.put(SQLHandler.ChildColumns.MOTHER_TT2_STS, tt2StatusList.get(TT2Spinner.getSelectedItemPosition()-1));
        }

        if (!metBarcodeValue.getText().toString().equalsIgnoreCase(currentChild.getBarcodeID())) {
            currentChild.setBarcodeID(metBarcodeValue.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.BARCODE_ID, metBarcodeValue.getText().toString());
        }
        if (!metFirstName.getText().toString().equalsIgnoreCase(currentChild.getFirstname1())) {
            currentChild.setFirstname1(metFirstName.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.FIRSTNAME1, metFirstName.getText().toString());
        }
        if (!metNotesValue.getText().toString().equalsIgnoreCase(currentChild.getNotes())) {
            currentChild.setNotes(metNotesValue.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.NOTES, metNotesValue.getText().toString());
        }
        if (!metMiddleName.getText().toString().equalsIgnoreCase(currentChild.getFirstname2())) {
            currentChild.setFirstname2(metMiddleName.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.FIRSTNAME2, metMiddleName.getText().toString());
        }
        if (!metLastName.getText().toString().equalsIgnoreCase(currentChild.getLastname1())) {
            currentChild.setLastname1(metLastName.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.LASTNAME1, metLastName.getText().toString());
        }
        if (bdate.compareTo(BackboneActivity.dateParser(currentChild.getBirthdate())) != 0) {
            birthDatesDiff = bdate.getTime() - BackboneActivity.dateParser(currentChild.getBirthdate()).getTime();
            // trick qe te marrim sa dite diference kemi dhe te gjejme fiks me sa dite ndryshon datelindja ne terma timestamp
            // e bejme gjithashtu nje floor te divisionit keshtu qe marrim vetem pjesen e plote te pjestimit dhe nuk
            // ngaterrohemi me castimin ne int. Ne cdo rast duhet te kemi kujdes ne mos numrat na kastohen ne int per
            // arsye se int do te na japi nje overflow dhe si pasoje nuk do te na ktheje vleren e sakte.
            // tani nuk do te na duhet me qe te bejme trickun me kalimin e nje dite diference pasi ate e kemi pasur si problem nga
            // overflow qe na bente int.
            double daysDiff = Math.floor(birthDatesDiff / 86400000);
            birthDatesDiff = (long) daysDiff * 86400000;
            currentChild.setBirthdate(birthdate_val);
            contentValues.put(SQLHandler.ChildColumns.BIRTHDATE, BackboneActivity.stringToDateParser(bdate));
        }

        if (!metMothersFirstName.getText().toString().equalsIgnoreCase(currentChild.getMotherFirstname())) {
            currentChild.setMotherFirstname(metMothersFirstName.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, metMothersFirstName.getText().toString());
        }
        if (!metMothersSurname.getText().toString().equalsIgnoreCase(currentChild.getMotherLastname())) {
            currentChild.setMotherLastname(metMothersSurname.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, metMothersSurname.getText().toString());
        }
        if (!birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getName().equalsIgnoreCase(currentChild.getBirthplace())) {
            currentChild.setBirthplaceId(birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getId());
            currentChild.setBirthplace(birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE, birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getId());
        }
//        Log.d("coze","vilage name = "+placeList.get(villageSpinner.getSelectedItemPosition()-1).getName());
        if (!placeList.get(villageSpinner.getSelectedItemPosition()-1).getName().equalsIgnoreCase(currentChild.getDomicile())) {
            currentChild.setDomicileId(placeList.get(villageSpinner.getSelectedItemPosition()-1).getId());
            currentChild.setDomicile(placeList.get(villageSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.DOMICILE, placeList.get(villageSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.DOMICILE_ID, placeList.get(villageSpinner.getSelectedItemPosition()-1).getId());
        }
        if (!healthFacilityList.get(healthFacOrig).getName().equalsIgnoreCase(currentChild.getHealthcenter())) {
            currentChild.setHealthcenterId(healthFacilityList.get(healthFacOrig).getId());
            currentChild.setHealthcenter(healthFacilityList.get(healthFacOrig).getName());
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY, healthFacilityList.get(healthFacOrig).getName());
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, healthFacilityList.get(healthFacOrig).getId());
        }
        if (!statusList.get(statusSpinner.getSelectedItemPosition()-1).getName().equalsIgnoreCase(currentChild.getStatus())) {
            currentChild.setStatusId(statusList.get(statusSpinner.getSelectedItemPosition()-1).getId());
            currentChild.setStatus(statusList.get(statusSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.STATUS, statusList.get(statusSpinner.getSelectedItemPosition()-1).getName());
            contentValues.put(SQLHandler.ChildColumns.STATUS_ID, statusList.get(statusSpinner.getSelectedItemPosition()-1).getId());
        }

        //        if (male.isChecked() && !gender_val.equalsIgnoreCase("male")) {
        //            contentValues.put(SQLHandler.ChildColumns.GENDER, "true");
        //        } else if (female.isChecked() && !gender_val.equalsIgnoreCase("female")) {
        //            contentValues.put(SQLHandler.ChildColumns.GENDER, "false");
        //        }

        if (!metPhoneNumber.getText().toString().equalsIgnoreCase(currentChild.getPhone())) {
            currentChild.setPhone(metPhoneNumber.getText().toString());
            contentValues.put(SQLHandler.ChildColumns.PHONE, currentChild.getPhone());
        }
        //        if (!notes.getText().toString().equalsIgnoreCase(currentChild.getNotes())) {
        //            currentChild.setNotes(notes.getText().toString());
        //            contentValues.put(SQLHandler.ChildColumns.NOTES, currentChild.getNotes());
        //        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChildSummaryPagerFragment.this.getActivity())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });

        try {
            if (contentValues.size() > 0) {

                if (mydb.updateChild(contentValues, currentChild.getId()) > 0) {
                    if (birthDatesDiff != 0) {
                        mydb.updateVaccinationAppointementForBirthDtChangeChild(currentChild.getId(), birthDatesDiff);
                        mydb.updateVaccinationEventForBirthDtChangeChild(currentChild.getId(), birthDatesDiff);
                        loadViewAppointementsTable(true);
                    }

                    // bejme update statusin e appointement nese ka ndryshuar statusi i childit
                    if (!currentChild.getStatusId().equalsIgnoreCase("1"))
                        mydb.updateVaccinationAppointementDisactive(currentChild.getId());
                    // bejme update vacination appointement nese
                    if (contentValues.get(SQLHandler.ChildColumns.HEALTH_FACILITY_ID) != null) {
                        mydb.updateVaccinationAppointementNewFacility(currentChild.getId(), currentChild.getHealthcenterId());
                        mydb.updateVaccinationEventNewFacility(currentChild.getId(), currentChild.getHealthcenterId());
                    }

                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_success);
                    ((ChildDetailsActivity)getActivity()).enableViewPagerPaging(true);

                    thread = new Thread() {
                        @Override
                        public void run() {
                            String url = prepareUrl().toString();
                            String threadTodayTimestamp= null;
                            BackboneApplication backbone = (BackboneApplication) ChildSummaryPagerFragment.this.getActivity().getApplication();
                            if (!app.updateChild(prepareUrl())) {
                                mydb.addPost(url, -1);
                                Log.d("Save Edited Child", "Error while saving edited child " + currentChild.getId());
                                app.saveNeeded = false;
                            } else {
                                String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
                                try {
                                    threadTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                //Register Audit
                                try{
                                    backbone.registerAudit(BackboneApplication.CHILD_AUDIT, metBarcodeValue.getText().toString(), threadTodayTimestamp ,
                                            backbone.getLOGGED_IN_USER_ID(), 2);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                app.saveNeeded = false;
                            }
                        }
                    };
                    thread.start();
                } else {
                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_error);
                    initListeners();
                }
                alertDialogBuilder.show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(ChildSummaryPagerFragment.this.getActivity(), "Save failed", Toast.LENGTH_LONG).show();
            app.saveNeeded = false;
            enableUserInputs(false);
        }


    }

    private StringBuilder prepareUrl() {
        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.CHILD_MANAGEMENT_SVC).append(BackboneApplication.CHILD_UPDATE);
        try {
            webServiceUrl.append("barcode=" + URLEncoder.encode(metBarcodeValue.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&firstname1=" + URLEncoder.encode(metFirstName.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&notes=" + URLEncoder.encode(metNotesValue.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&lastname1=" + URLEncoder.encode(metLastName.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        try {
            webServiceUrl.append("&birthdate=" + URLEncoder.encode(formatted.format(bdate), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&motherFirstname=" + URLEncoder.encode(metMothersFirstName.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&motherLastname=" + URLEncoder.encode(metMothersSurname.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&mothersHivStatus=" + URLEncoder.encode(vvuStatusList.get(VVUSpinner.getSelectedItemPosition()-1), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            webServiceUrl.append("&mothersTT2Status=" + URLEncoder.encode(tt2StatusList.get(TT2Spinner.getSelectedItemPosition()-1), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            webServiceUrl.append("&birthplaceId=" + URLEncoder.encode(birthplaceList.get(pobSpinner.getSelectedItemPosition()-1).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&domicileId=" + URLEncoder.encode(placeList.get(villageSpinner.getSelectedItemPosition()-1).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&healthFacilityId=" + URLEncoder.encode(healthFacilityList.get(healthFacOrig).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&statusid=" + URLEncoder.encode(statusList.get(statusSpinner.getSelectedItemPosition()-1).getId(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (ms.getSelectedItemPosition() == 0)
            webServiceUrl.append("&gender=true");
        else if (ms.getSelectedItemPosition() == 1){
            webServiceUrl.append("&gender=true");
        }

        try {
            webServiceUrl.append("&phone=" + URLEncoder.encode(metPhoneNumber.getText().toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            webServiceUrl.append("&notes=" + URLEncoder.encode(notesOrig, "UTF-8"));
            if (currentChild.getId().matches("\\d+")) {
                webServiceUrl.append("&childId=" + URLEncoder.encode(currentChild.getId(), "UTF-8"));
            } else {
                webServiceUrl.append("&childId=" + 0); // hardcoded workaround for issues related to guid
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            webServiceUrl.append("&firstname2=" + URLEncoder.encode(metMiddleName.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            Log.d("coze", "updating child modified on = " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()));
            webServiceUrl.append("&modifiedOn=" + URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //
        try {
            webServiceUrl.append("&childCumulativeSn=" + URLEncoder.encode(metCummulativeSn.getText().toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            webServiceUrl.append("&childRegistryYear=" + URLEncoder.encode("2016", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return webServiceUrl;
    }


    private boolean checkIfTheEditTextContainsSpaces(MaterialEditText editText){
        if(editText.getText().toString().contains(" ")){
            editText.setError(app.getString(R.string.name_contains_spaces));
            return true;
        }else{
            return false;
        }
    }

    public void giveValueAfterSave(){
        firstnameOrig = metFirstName.getText().toString();
        firstname2Orig = metMiddleName.getText().toString();
        lastnameOrig = metLastName.getText().toString();
        birthdateOrig = metDOB.getText().toString();
        motherFirOrig = metMothersFirstName.getText().toString();
        motherLastOrig = metMothersSurname.getText().toString();
        phoneOrig = metPhoneNumber.getText().toString();
        notesOrig = currentChild.getNotes();
        barcodeOrig = metBarcodeValue.getText().toString();
        birthplaceOrig = pobSpinner.getSelectedItemPosition();
        villageOrig = villageSpinner.getSelectedItemPosition();
        statusOrig = statusSpinner.getSelectedItemPosition();
        genderOrig =   ms.getSelectedItemPosition();

        if (VVUSpinner.getSelectedItemPosition() != 0){
            vvuStatusOrig   = vvuStatusList.get(VVUSpinner.getSelectedItemPosition() - 1);
        }
        if (TT2Spinner.getSelectedItemPosition() != 0){
            tt2StatusOrig   = tt2StatusList.get(TT2Spinner.getSelectedItemPosition() - 1);
        }
        childCummulativeSnOrig  = metCummulativeSn.getText().toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateData() {
        Log.d(TAG,"updating the fragment");
        updateAppointmentTable();
        summaryTableLayout.removeAllViews();
        summaryTableLayout.addView(header);
        summaryTableLayout.addView(appointmentTableHeader);
        fillAppointmentTableLayout();
    }
}
