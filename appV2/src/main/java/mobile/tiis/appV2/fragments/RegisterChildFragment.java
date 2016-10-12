package mobile.tiis.appv2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableWeightLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.appv2.ChildDetailsActivity;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.adapters.PlacesOfBirthAdapter;
import mobile.tiis.appv2.adapters.SingleTextViewAdapter;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.entity.Birthplace;
import mobile.tiis.appv2.entity.Child;
import mobile.tiis.appv2.entity.HealthFacility;
import mobile.tiis.appv2.entity.Place;
import mobile.tiis.appv2.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by issymac on 11/12/15.
 */
public class RegisterChildFragment extends RxFragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    private static final String TAG = RegisterChildFragment.class.getSimpleName();
    public List<String> motherVVU, gender, motherTT2, spinnerYears;

    public List<MaterialEditText> registerSectionFields;

    private Date bdate;

    List<Place> placeList;

    List<Birthplace> birthplaceList;

    PlacesOfBirthAdapter vvuAdapter, genderAdapter, tt2Adapter, yearSpinnerAdapter;

    public MaterialSpinner placeOfBirthSpinner, genderSpinner, placeOfDomicileSpinner, motherVVUStatusSpinner, motherTT2StatusSpinner, registryYearSpinner,catchmentAreaSpinner;

    public MaterialEditText etDateOfBirth;

    public ExpandableWeightLayout expandableResultLayout;

    public TableLayout resultTableLayout;

    public Button scanButton, submitButton;

    int spPlacePos, spVillagePos , notApplicablePos = -1;

    private boolean isSavingData = false;

    private MaterialEditText etChildCumulativeSn, etbarcode, etFirstName, etSurname, etMotherFirstName, etMotherSurname, etPhone, etNotes,etFirstname2;
    private String barcode, firstanme, surname, motherFirstname, motherLastname, gen="", genderChildWithoutApp,firstname2, childRegistryYear;
    private DatabaseHandler mydb;
    private ProgressDialog progressDialog;
    private BackboneApplication app;

    public AVLoadingIndicatorView avi;
    public TextView infoText;

    public boolean isSearch = false;
    public boolean childListFromOutsideFacility = false;

    public Child childOfInterest;
    private RelativeLayout loadersInfo;

    ArrayList<Child> childrensrv = new ArrayList<>();

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    private View.OnFocusChangeListener localChildSearchTriggerfocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (!b) {
                if(!etMotherFirstName.getText().toString().isEmpty() && !etMotherSurname.getText().toString().isEmpty() && !etDateOfBirth.toString().isEmpty() && !gen.equals(""))
                    searchChildTask();
            }
        }
    };
    private  String genderValue="";
    private  String registerHealthFacilityId="";

    private List<HealthFacility> healthFacilityList;
    private List<HealthFacility> districtCouncilsList;
    private List<String> healthFacilities,healthFacilitiesNames;
    private AutoCompleteTextView healthFacilitiesAutoSearch;


    private String getDistrictCouncilName(String id){
        Log.d(TAG,"health facility id = "+id);
        String name = "";
        for (HealthFacility districtCouncil : districtCouncilsList){
            Log.d(TAG,"district council ids = "+districtCouncil.getId());
            if(districtCouncil.getId().equals(id)) {
                name = districtCouncil.getName();
            }

        }
        return name;
    }

    private String catchment="inside";
    private Looper backgroundLooper;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register_child, null);
        setUpView(root);

        app = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();


        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        healthFacilityList = mydb.getAllHealthFacility();
        districtCouncilsList = mydb.getAllDistrictCoucils();
        healthFacilities = new ArrayList<>();
        healthFacilitiesNames = new ArrayList<>();
        for (HealthFacility healthFacility : healthFacilityList){
            healthFacilities.add(healthFacility.getName()+" >> "+getDistrictCouncilName(healthFacility.getParentId()));
            healthFacilitiesNames.add(healthFacility.getName());
        }



        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item_layout,R.id.item,healthFacilities);
        healthFacilitiesAutoSearch.setAdapter(adapter);
        healthFacilitiesAutoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int pos = healthFacilities.indexOf(((TextView) v.findViewById(R.id.item)).getText().toString());
                if (pos == -1) {
                    healthFacilitiesAutoSearch.setError("Error in selecting health facility");
                } else {

                    registerHealthFacilityId = healthFacilityList.get(pos).getId();
                    healthFacilitiesAutoSearch.setText(healthFacilitiesNames.get(pos));
                    ((EditText)root.findViewById(R.id.focus_request_view)).requestFocus();
                }
            }
        });
        healthFacilitiesAutoSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    int pos = healthFacilitiesNames.indexOf(healthFacilitiesAutoSearch.getText().toString());
                    if (pos == -1) {
                        healthFacilitiesAutoSearch.setError("Error in selecting health facility");
                        registerHealthFacilityId="";
                    }
                }
            }
        });


        app.saveNeeded = true;

        placeList = mydb.getAllPlaces();
        for(int i = 0 ; i<placeList.size();i++){
            if(placeList.get(i).getId().equals("-100")){
                notApplicablePos = i+1;
                break;
            }
        }
        List<String> place_names = new ArrayList<String>();
        for (Place element : placeList) {
            place_names.add(element.getName());
        }

        birthplaceList = mydb.getAllBirthplaces();
        List<String> birthplaceNames = new ArrayList<String>();
        for (Birthplace element : birthplaceList) {
            birthplaceNames.add(element.getName());
        }

        SingleTextViewAdapter spBirthOfPlace = new SingleTextViewAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, birthplaceNames);
        placeOfBirthSpinner.setAdapter(spBirthOfPlace);
        if (spPlacePos != 0) {
            placeOfBirthSpinner.setSelection(spPlacePos);
        } else {
            placeOfBirthSpinner.setSelection(0);
        }

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, place_names);
        placeOfDomicileSpinner.setAdapter(dataAdapter);
        if (spVillagePos != 0) {
            placeOfDomicileSpinner.setSelection(spVillagePos);
        }
        placeOfDomicileSpinner.setSelection(spVillagePos);


        motherTT2 = new ArrayList<>();
        motherTT2.add("Ndio");
        motherTT2.add("Hapana");
        motherTT2.add("Sijui");

        motherVVU = new ArrayList<>();
        motherVVU.add("1");
        motherVVU.add("2");
        motherVVU.add("U");

        gender = new ArrayList<>();
        gender.add("Male");
        gender.add("Female");

        spinnerYears = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Date d = new Date(c.getTimeInMillis());


        int y =c.get(Calendar.YEAR);

        Log.d("time", "year = " + y);
        while(y>2010){
            spinnerYears.add(y+"");
            y--;
        }


        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });


        submitButton.setOnClickListener(RegisterChildFragment.this);

        genderAdapter   = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, gender);
        vvuAdapter      = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, motherVVU);
        tt2Adapter      = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, motherTT2);
        yearSpinnerAdapter      = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, spinnerYears);

        genderSpinner.setAdapter(genderAdapter);
        motherTT2StatusSpinner.setAdapter(tt2Adapter);
        motherVVUStatusSpinner.setAdapter(vvuAdapter);
        registryYearSpinner.setAdapter(yearSpinnerAdapter);

        registryYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i == -1)){
                    Log.d("vvu_tt2", motherVVU.get(i));
                    childRegistryYear = spinnerYears.get(registryYearSpinner.getSelectedItemPosition()-1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        catchmentAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    catchment = "inside";
                    healthFacilitiesAutoSearch.setVisibility(View.INVISIBLE);
                }else{
                    healthFacilitiesAutoSearch.setVisibility(View.VISIBLE);
                    catchment = "outside";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        motherVVUStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i == -1)) {
                    Log.d("vvu_tt2", motherVVU.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        motherTT2StatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!(i == -1)) {
                    Log.d("vvu_tt2", motherTT2.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        gen = "M";
                        Log.d("gender", gen);
                        break;
                    case 1:
                        gen = "F";
                        Log.d("gender", gen);
                        break;
                    default:
                        gen="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
    }

    public void setUpView(View v){
        registerSectionFields   = new ArrayList<>();

        avi                     = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        infoText                = (TextView) v.findViewById(R.id.info_text);
//        loadersInfo = (RelativeLayout)v.findViewById(R.id.loading_info);

        expandableResultLayout  = (ExpandableWeightLayout) v.findViewById(R.id.expandable_result_layout);
        resultTableLayout       = (TableLayout) v.findViewById(R.id.result_table_layout);
        resultTableLayout       .setVisibility(View.GONE);

        etbarcode           = (MaterialEditText) v. findViewById(R.id.reg_barcode);
        etbarcode.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etbarcode);

        healthFacilitiesAutoSearch = (AutoCompleteTextView)v.findViewById(R.id.health_facilities_lists);

        etFirstName         = (MaterialEditText) v. findViewById(R.id.reg_fname);
        etFirstName.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etFirstName);

        etFirstname2        = (MaterialEditText) v. findViewById(R.id.reg_mname);
        etFirstname2.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);


        etSurname           = (MaterialEditText) v. findViewById(R.id.reg_surname);
        etSurname.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etSurname);

        etDateOfBirth = (MaterialEditText) v.findViewById(R.id.reg_dob);
        etDateOfBirth.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etDateOfBirth);

        etMotherFirstName   = (MaterialEditText) v. findViewById(R.id.reg_mot_fname);
        etMotherFirstName.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etMotherFirstName);

        etMotherSurname     = (MaterialEditText) v. findViewById(R.id.reg_mot_sname);
        etMotherSurname.setOnFocusChangeListener(localChildSearchTriggerfocusChangeListener);
        registerSectionFields.add(etMotherSurname);

        etPhone             = (MaterialEditText) v. findViewById(R.id.reg_phone);
        etNotes             = (MaterialEditText) v. findViewById(R.id.reg_notes);

        etChildCumulativeSn = (MaterialEditText) v.findViewById(R.id.cummulative_sn);

        placeOfBirthSpinner     = (MaterialSpinner) v.findViewById(R.id.reg_spin_pob);
        placeOfDomicileSpinner  = (MaterialSpinner) v.findViewById(R.id.reg_spin_pod);
        genderSpinner           = (MaterialSpinner) v.findViewById(R.id.reg_spin_gender);
        motherVVUStatusSpinner  = (MaterialSpinner) v.findViewById(R.id.reg_spin_mother_vvu_status);
        motherTT2StatusSpinner  = (MaterialSpinner) v.findViewById(R.id.reg_spin_mother_tt2_status);
        registryYearSpinner     = (MaterialSpinner) v.findViewById(R.id.reg_spin_register_year);
        catchmentAreaSpinner     = (MaterialSpinner) v.findViewById(R.id.catchment_area);

        submitButton = (Button) v.findViewById(R.id.reg_submit_btn);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Saving the child. \nPlease wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.reg_submit_btn){
            if(!isSavingData) {
                isSavingData = true;
                progressDialog.show();
                DatabaseHandler mydb = app.getDatabaseInstance();

                if (checkDataIntegrityBeforeSave()) {
                    if (mydb.isBarcodeInChildTable(etbarcode.getText().toString())) {
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterChildFragment.this.getActivity())
                                .setTitle(getString(R.string.same_barcode))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((AlertDialog) dialog).dismiss();
                                    }
                                });
                        alertDialogBuilder.show();
                        isSavingData =false;
                        return;
                    }
                    registerChildInDB();
                }else{
                    progressDialog.dismiss();
                    isSavingData =false;
                }
            }

        }

    }

    public void showDialogWhenRegisterIsDone() {
        final Dialog d = new Dialog(RegisterChildFragment.this.getActivity());
        d.setTitle(getString(R.string.title_register_child));
        d.setContentView(R.layout.layout_register_child);
        d.show();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        RegisterChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (d != null) {
                                    d.dismiss();
//                                    progBar.setVisibility(View.GONE);

                                }
                            }
                        });
                    }
                },
                3500
        );
    }


    private void showDialogGoingScanWhenHasBarcode() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterChildFragment.this.getActivity())
                .setMessage(getString(R.string.change_barcode_scan_when_exists_dialog))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((AlertDialog) dialogInterface).dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });
        alertDialogBuilder.show();
    }


    public void pickDate(){
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+0500"));
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                RegisterChildFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(Color.DKGRAY);
        dpd.setMaxDate(Calendar.getInstance());
        dpd.show(this.getActivity().getFragmentManager(), "DatePickerDialogue");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,monthOfYear,dayOfMonth,3,0,0);


        Log.d(TAG,"selected date = "+calendar.getTimeInMillis());

        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        bdate = calendar.getTime();

        Log.d(TAG,"selected date with time zone = "+bdate.getTime());

        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");

        Date dNow = new Date();

        etDateOfBirth.setText(ft.format(calendar.getTime()));
        if(!etMotherFirstName.getText().toString().isEmpty() && !etMotherSurname.getText().toString().isEmpty() && !etDateOfBirth.toString().isEmpty() && !gen.equals(""));
            searchChildTask();
    }

    public String getMonth(int month){
        switch (month){

            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "March";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            default:
                return "Dec";
        }
    }

    private boolean checkDataIntegrityBeforeSave() {

        Log.d("registrar", placeOfBirthSpinner.getSelectedItemPosition()+"");
        Log.d("registrar", placeOfDomicileSpinner.getSelectedItemPosition()+"");

        if (etbarcode.getText().toString().isEmpty() || etbarcode.getText().equals("")) {
            etbarcode.setErrorColor(Color.RED);
            etbarcode.setError(getString(R.string.empty_barcode));
            return false;
        }
        if (etbarcode.getText().length()!=10) {
            etbarcode.setErrorColor(Color.RED);
            etbarcode.setError(getString(R.string.barcode_ten_numbers_constraint));
            return false;
        }

        if (etMotherFirstName.getText().toString().isEmpty()) {
            etMotherFirstName.setError(getString(R.string.empty_mother_names));
            etMotherFirstName.setErrorColor(Color.RED);
            return false;
        }

        if (etMotherSurname.getText().toString().isEmpty()) {
            etMotherSurname.setError(getString(R.string.empty_mother_names));
            etMotherSurname.setErrorColor(Color.RED);
            return false;
        }

        if (motherVVUStatusSpinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)motherVVUStatusSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.empty_mother_vvu));//changes the selected item text to this
            return false;
        }

        if (motherTT2StatusSpinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)motherTT2StatusSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.empty_mother_vvu));//changes the selected item text to this
            return false;
        }

        if (bdate == null || bdate.compareTo(new Date()) > 0) {
            etDateOfBirth.setError(getString(R.string.future_birth_date));
            etDateOfBirth.setErrorColor(Color.RED);
            return false;
        }
        // we have as the last element the one that is empty element. We can not select it.
        Log.d("registrar", placeOfBirthSpinner.getSelectedItemPosition()+"");
        Log.d("registrar", placeOfDomicileSpinner.getSelectedItemPosition()+"");

        if (placeOfBirthSpinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)placeOfBirthSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.empty_birthplace));//changes the selected item text to this
            return false;
        }

        // we have as the last element the one that is empty element. We can not select it.
        if (placeOfDomicileSpinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)placeOfDomicileSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.empty_village));//changes the selected item text to this
            return false;
        }


        if (gen.equals("")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.alert_empty_fields))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((AlertDialog) dialog).dismiss();
                        }
                    });
            alertDialogBuilder.setMessage(getString(R.string.empty_gender));
            alertDialogBuilder.show();
            genderSpinner.setError(getString(R.string.empty_gender));
            return false;
        }


        if (etChildCumulativeSn.length() > 0){
            try {
                if(registryYearSpinner.getSelectedItemPosition() - 1==0){
                    registryYearSpinner.setError("Please Select the registration Year");
                }else if(mydb.isChildRegistrationNoPresentInDb(spinnerYears.get(registryYearSpinner.getSelectedItemPosition() - 1), etChildCumulativeSn.getText().toString(), etbarcode.getText().toString())) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.alert_empty_fields))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((AlertDialog) dialog).dismiss();
                                }
                            });
                    alertDialogBuilder.setMessage("The entered Child Cumulative Sn and Year have already been used");
                    alertDialogBuilder.show();
                    etChildCumulativeSn.setError("Please fill a valid Child Cumulative Sn");
                    return false;
                }
            }catch (Exception e ){
                e.printStackTrace();
            }
        }

        if(!catchment.equals("inside") && registerHealthFacilityId.equals("")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.alert_empty_fields))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((AlertDialog) dialog).dismiss();
                        }
                    });
            alertDialogBuilder.setMessage("Please select the correct health facility");
            alertDialogBuilder.show();
            healthFacilitiesAutoSearch.setError("Please select the correct health facility");
            return false;
        }




        return true;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void registerChildInDB() {

        ContentValues contentValues = new ContentValues();

        if (!etbarcode.getText().toString().equalsIgnoreCase("")) {

            contentValues.put(SQLHandler.ChildColumns.BARCODE_ID, removeWhiteSpaces(etbarcode.getText().toString()));
        }


        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME1, removeWhiteSpaces(etFirstName.getText().toString()));
        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME2, removeWhiteSpaces(etFirstname2.getText().toString()));


        if (!etSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.LASTNAME1, removeWhiteSpaces(etSurname.getText().toString()));
        }
        if (bdate != null && bdate.compareTo(new Date()) < 0) {
            contentValues.put(SQLHandler.ChildColumns.BIRTHDATE, BackboneActivity.stringToDateParser(bdate));
        }
        if (!etMotherFirstName.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, removeWhiteSpaces(etMotherFirstName.getText().toString()));
        }
        if (!etMotherSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, removeWhiteSpaces(etMotherSurname.getText().toString()));
        }


        if(catchment.equals("inside")) {
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, app.getLOGGED_IN_USER_HF_ID());
        }else{
            contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, registerHealthFacilityId);
        }

        contentValues.put(SQLHandler.ChildColumns.ADDRESS, "");
        contentValues.put(SQLHandler.ChildColumns.MODIFIED_ON, new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));


        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE, birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getName());
        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getId());

        contentValues.put(SQLHandler.ChildColumns.MOTHER_VVU_STS, motherVVU.get(motherVVUStatusSpinner.getSelectedItemPosition() - 1));
        contentValues.put(SQLHandler.ChildColumns.MOTHER_TT2_STS, motherTT2.get(motherTT2StatusSpinner.getSelectedItemPosition() - 1));

        contentValues.put(SQLHandler.ChildColumns.DOMICILE, placeList.get(placeOfDomicileSpinner.getSelectedItemPosition() - 1).getName());
        contentValues.put(SQLHandler.ChildColumns.DOMICILE_ID, placeList.get(placeOfDomicileSpinner.getSelectedItemPosition() - 1).getId());


        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace('\'','a');
        uuid = uuid.replace('\"','a');
        contentValues.put(SQLHandler.ChildColumns.ID, uuid);

        if (gen.equals("M")) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "true");
        } else if (gen.equals("F")) {
            contentValues.put(SQLHandler.ChildColumns.GENDER, "false");
        }

        contentValues.put("modfied_at", "/Date(" + Calendar.getInstance().getTime().getTime() + "-0500)/");
        contentValues.put(SQLHandler.ChildColumns.PHONE, removeWhiteSpaces(etPhone.getText().toString()));
        contentValues.put(SQLHandler.ChildColumns.NOTES, removeWhiteSpaces(etNotes.getText().toString()));
        if(etChildCumulativeSn.length()==0) {
            contentValues.put(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER, "");
            contentValues.put(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR, "");
        }else{
            contentValues.put(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER, removeWhiteSpaces(etChildCumulativeSn.getText().toString()));
            contentValues.put(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR, removeWhiteSpaces(childRegistryYear));
        }
        contentValues.put("updated", 1);
        contentValues.put("owners_username", "");
        contentValues.put("STATUS_ID", "");
        contentValues.put("HEALTH_FACILITY", "");
        contentValues.put("COMMUNITY_ID", "");
        contentValues.put("MOBILE", "");
        contentValues.put("MOTHER_ID", "");
        contentValues.put("STATUS_ID", 1);
        contentValues.put("STATUS", "");
        contentValues.put("SYSTEM_ID", "");
        contentValues.put("TEMP_ID", "");


        if (contentValues.size() > 0) {

            contentValues.put("MODIFIED_BY", app.getLOGGED_IN_USER_ID());
            if (mydb.registerChild(contentValues) > -1) {
                mydb.InsertVaccinationsForChild(uuid, app.getLOGGED_IN_USER_ID());
                progressDialog.dismiss();
                showDialogWhenRegisterIsDone();
                if (gen.equals("M")) {
                    genderChildWithoutApp = "true";
                } else {
                    genderChildWithoutApp = "false";
                }

                Calendar modifiedOn =Calendar.getInstance();
                try {
                    String hfid;
                    if(catchment.equals("inside")) {
                        hfid = app.getLOGGED_IN_USER_HF_ID();
                    }else{
                        hfid = registerHealthFacilityId;
                    }
                    registerChildWithoutAppointments(removeWhiteSpaces(etbarcode.getText().toString()),
                            removeWhiteSpaces(etFirstName.getText().toString()),
                            removeWhiteSpaces(etSurname.getText().toString()),
                            bdate,
                            genderChildWithoutApp,
                            hfid,
                            birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getId(),
                            placeList.get(placeOfDomicileSpinner.getSelectedItemPosition() - 1).getId(),
                            "",
                            removeWhiteSpaces(etPhone.getText().toString()),
                            removeWhiteSpaces(etMotherFirstName.getText().toString()),
                            removeWhiteSpaces(etMotherSurname.getText().toString()),
                            removeWhiteSpaces(etNotes.getText().toString()),
                            app.getLOGGED_IN_USER_ID(),
                            modifiedOn.getTime(),
                            uuid,
                            removeWhiteSpaces(etFirstname2.getText().toString()),
                            motherVVU.get(motherVVUStatusSpinner.getSelectedItemPosition() - 1),
                            motherTT2.get(motherTT2StatusSpinner.getSelectedItemPosition() - 1),etChildCumulativeSn.length() > 0?removeWhiteSpaces(etChildCumulativeSn.getText().toString()):"",
                            etChildCumulativeSn.length() > 0? childRegistryYear : "",catchment.equals("inside")?1:-1);
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }else{
                progressDialog.dismiss();
            }
            isSavingData =false;

        }

    }

    private String removeWhiteSpaces(String withWhiteSpace){
        try {
            String withoutWhiteSpace;
            withoutWhiteSpace = withWhiteSpace.replaceAll("\\s+", "");
            return withoutWhiteSpace;
        }catch (Exception e){
            e.printStackTrace();
            return withWhiteSpace;
        }
    }

    /**
     *
     * @param barcode
     * @param fristname
     * @param lastname
     * @param bDate
     * @param gender
     * @param hfid
     * @param birthPlaceId
     * @param domId
     * @param addr
     * @param phone
     * @param motherFirstname
     * @param motherLastname
     * @param notes
     * @param userID
     * @param modOn
     * @param tempId
     * @param firstname2
     * @param threadMotherVVUStatus
     * @param threadMotherTT2Status
     * @param childCummulativeSn
     * @param childRegistryYear
     * @param catchment 1=inside 0=outside
     */
    private synchronized void registerChildWithoutAppointments(String barcode, String fristname, String lastname, Date bDate, String gender, String  hfid, String birthPlaceId, String domId,
                                                               String addr, String phone, String motherFirstname, String motherLastname, String notes, String userID, Date modOn, final String tempId, String firstname2, String threadMotherVVUStatus, String threadMotherTT2Status, String childCummulativeSn, String childRegistryYear, final int catchment) {
        new Thread() {
            String threadBDateString;
            String threadModOn;
            String threadbarcode
                    ,
                    threadfristname
                    ,
                    threadLastname
                    ,
                    threadGender
                    ,
                    threadAddr
                    ,
                    threadPhone
                    ,
                    threadMotherFirstname
                    ,
                    threadMotherLastname
                    ,
                    threadhfid
                    ,
                    threadBirthPlaceID
                    ,
                    threadDomID
                    ,
                    threadNotes;
            String threadUserID;
            String threadTempId;
            String threadFirstname2;
            String threadMotherVVUStatus;
            String threadMotherTT2Status;
            String childCummulativeSn;
            String childRegistryYear;

            public Thread setData(String threadbarcode, String threadfristname, String threadLastname, Date threadBDate, String threadGender, String threadhfid, String threadBirthPlaceID, String threadDomID,
                                  String threadAddr, String threadPhone, String threadMotherFirstname, String threadMotherLastname, String threadNotes, String threadUserID, Date threadModOn, String tempId,String threadFirstname2, String threadMotherVVUStatus, String threadMotherTT2Status, String threadCummulativeSn, String threadChildRegistryYear, int catchment) {

                try {
                    this.threadbarcode          = threadbarcode;
                    this.threadfristname        = threadfristname;
                    this.threadFirstname2       = threadFirstname2;
                    this.threadLastname         = threadLastname;
                    this.threadGender           = threadGender;
                    this.threadhfid             = threadhfid;
                    this.threadBirthPlaceID     = threadBirthPlaceID;
                    this.threadDomID            = threadDomID;
                    this.threadAddr             = threadAddr;
                    this.threadPhone            = threadPhone;
                    this.threadMotherFirstname  = threadMotherFirstname;
                    this.threadMotherLastname   = threadMotherLastname;
                    this.threadNotes            = threadNotes;
                    this.threadUserID           = threadUserID;
                    this.threadTempId           = tempId;
                    this.threadModOn            = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(threadModOn), "utf-8");
                    this.threadBDateString      = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(threadBDate), "utf-8");
                    this.threadGender           = threadGender;
                    this.threadMotherVVUStatus  = threadMotherVVUStatus;
                    this.threadMotherTT2Status  = threadMotherTT2Status;
                    this.childCummulativeSn     = threadCummulativeSn;
                    this.childRegistryYear      = threadChildRegistryYear;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public void run() {
                super.run();

                int results = app.registerChildWithAppoitments(threadbarcode, threadfristname, threadLastname, threadBDateString, threadGender, threadhfid, threadBirthPlaceID, threadDomID, threadAddr
                        , threadPhone, threadMotherFirstname, threadMotherLastname, threadNotes, threadUserID, threadModOn, null,threadFirstname2, threadTempId, threadbarcode, threadMotherVVUStatus, threadMotherTT2Status, childCummulativeSn, childRegistryYear,catchment);
                Log.d("CSN", "Result from server is : "+results);
                if(results!=-1) {
                    Intent childDetailsActivity = new Intent(getActivity(), ChildDetailsActivity.class);
                    Bundle bnd = new Bundle();
                    bnd.putString(BackboneApplication.CHILD_ID, results + "");
                    bnd.putString("barcode", threadbarcode);
                    bnd.putInt("current", 0);
                    bnd.putBoolean("isNewChild", true);
                    childDetailsActivity.putExtras(bnd);

                    Log.d("coze", "starting activity");
                    startActivity(childDetailsActivity);
                }else{
                    Intent childDetailsActivity = new Intent(getActivity(), ChildDetailsActivity.class);
                    Bundle bnd = new Bundle();
                    bnd.putString(BackboneApplication.CHILD_ID, threadTempId);
                    bnd.putString("barcode", threadbarcode);
                    bnd.putBoolean("isNewChild", true);
                    childDetailsActivity.putExtras(bnd);

                    startActivity(childDetailsActivity);
                }
            }

        }.setData(barcode, fristname, lastname, bDate, gender, hfid, birthPlaceId, domId, addr, phone, motherFirstname, motherLastname, notes, userID, modOn, tempId,firstname2, threadMotherVVUStatus, threadMotherTT2Status, childCummulativeSn, childRegistryYear,catchment).start();
    }

    private void searchChildTask(){
        expandableResultLayout.expand();
        avi.show();
        infoText.setText("Searching within catchment");
        resultTableLayout.removeAllViews();
        Observable.defer(new Func0<Observable<List<Child>>>() {
            @Override
            public Observable<List<Child>> call() {

                String num = "0";
                List<Child> children = null;
                List<Child> childrenFromMaternityApp = null;

                String searchBarcode, firstName, surName, motherFirstName, motherSurname;
                searchBarcode = "";
//                if (!etbarcode.getText().toString().isEmpty()){
//                    searchBarcode = etbarcode.getText().toString();
//                }
//
//                firstName = "";
//                if (!etFirstName.getText().toString().isEmpty()){
//                    firstName = etFirstName.getText().toString();
//                }
//
//                surName = "";
//                if (!etSurname.getText().toString().isEmpty()){
//                    surName = etSurname.getText().toString();
//                }

                motherFirstName = "";
                if (!etMotherFirstName.getText().toString().isEmpty()){
                    motherFirstName = etMotherFirstName.getText().toString();
                }

                motherSurname = "";
                if (!etMotherSurname.getText().toString().isEmpty()){
                    motherSurname = etMotherSurname.getText().toString();
                }

                if (gen.equals("M")) {
                    genderValue =  "true";
                } else if (gen.equals("F")) {
                    genderValue =  "false";
                }

                childrenFromMaternityApp = mydb.searchIfChildIsRegisteredFromMaternityApp( motherFirstName, motherSurname, bdate.getTime(), genderValue);


                return Observable.just(children,childrenFromMaternityApp);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<List<Child>>bindToLifecycle())
                .subscribe(new Subscriber<List<Child>>() {
                    @Override
                    public void onCompleted() {
                        searchOutsideFacility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Child> children) {
                        if(children == null){
                            avi.hide();
                            infoText.setText("");
                        }else{
                            try {
                                Toast.makeText(getActivity(), R.string.similar_children_exist, Toast.LENGTH_LONG).show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            avi.hide();
                            infoText.setText("The child you are trying to register was registered in Maternity ward. Please confirm on the above list");
                            childListFromOutsideFacility = false;
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etFirstName.getWindowToken(), 0);
                            fillSearchResultTable(children, true);
                        }
                    }
                });
    }




    public void fillSearchResultTable(List<Child> children,boolean clearTable){

        resultTableLayout.setVisibility(View.VISIBLE);
        LayoutInflater li = (LayoutInflater) RegisterChildFragment.this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = li.inflate(R.layout.child_register_search_results_header, null);
        if(clearTable) {
            resultTableLayout.removeAllViews();
            resultTableLayout.addView(titleView);
        }
        int n = 1;




        for (final Child item : children){
            View rowView = li.inflate(R.layout.children_list_item, null);

            TextView sn     = (TextView) rowView.findViewById(R.id.sn_number);
            sn              .setText(n + "");
            n++;

            TextView childNames = (TextView)rowView.findViewById(R.id.reg_search_txt_child_names);
            childNames  .setText(item.getFirstname1() + " " + item.getLastname1());
            childNames  .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);

            TextView motherNames    = (TextView) rowView.findViewById(R.id.txt_mother_names);
            motherNames .setText(item.getMotherFirstname() + " " + item.getMotherLastname());
            motherNames .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);


            TextView childDOB       = (TextView) rowView.findViewById(R.id.txt_child_dob);
            childDOB    .setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date(Long.parseLong(item.getBirthdate().toString()))));
            childDOB    .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);

            TextView gender         = (TextView) rowView.findViewById(R.id.txt_child_gender);
            gender                  .setText(item.getGender());
            gender      .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);

            TextView village        = (TextView) rowView.findViewById(R.id.txt_child_village_domicile);
            village                 .setText(item.getDomicile());
            village     .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);

            TextView hf             = (TextView) rowView.findViewById(R.id.txt_child_health_facility);
            hf                      .setText(item.getHealthcenter());
            hf          .setTypeface(BackboneActivity.Roboto_BoldCondensedItalic);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(RegisterChildFragment.this.getActivity(), item.getFirstname1(), Toast.LENGTH_LONG).show();
                    app.saveNeeded = false;
                    childOfInterest = item;
                    if (childListFromOutsideFacility) {
                        //Parse child first then view details
                        Log.d("PANDA", "Here at Outside Facility");
                        childSynchronization(childOfInterest.getId());
                    } else {
                        Intent childDetailsActivity = new Intent(RegisterChildFragment.this.getActivity(), ChildDetailsActivity.class);
                        childDetailsActivity.putExtra("barcode", item.getBarcodeID());
                        childDetailsActivity.putExtra("myChild", item);
                        childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, item.getId());
                        startActivity(childDetailsActivity);
                    }
                }
            });

            resultTableLayout.addView(rowView);

        }

    }

    public void searchOutsideFacility(){
        if (isThereCorrectNumberOfCriteria()){
            searchChildrenOutsideFacility();
            Log.d(TAG,"searching for the child from outside facility");
        }else {
            Toast.makeText(RegisterChildFragment.this.getActivity(), "Add criterias to search outside facility", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isThereCorrectNumberOfCriteria(){
        if (etDateOfBirth.getText().toString().isEmpty()
                || etMotherFirstName.getText().toString().isEmpty() || etMotherSurname.getText().toString().isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    public void searchChildrenOutsideFacility(){

        final android.support.v7.app.AlertDialog alertDialog;

        SimpleDateFormat fmt = new SimpleDateFormat("d/M/yyyy");

        Date dateOfBirth = null;

        avi.show();

        infoText.setText("Searching Outside Facility");

        expandableResultLayout.expand();

        try {
            dateOfBirth = fmt.parse(etDateOfBirth.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        String dob   = "";

        try {
            dob     = formatted.format(dateOfBirth);
        }catch (Exception e){
            e.printStackTrace();
        }

        new Thread (){

            String threadDob;

            public Thread setData(String threadDateOfBirth) {
                try {
                    this.threadDob = URLEncoder.encode(threadDateOfBirth, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public void run() {

                synchronized (this) {

                    int emptyInputDetected = 0;

                    String childBarcode = null;
                    if (!(etbarcode.getText().toString().equals("") || etbarcode.getText().toString().isEmpty())){
                        childBarcode = etbarcode.getText().toString();
                    }

                    String childFName  = null;
                    if (!(etFirstName.getText().toString().equals("") || etFirstName.getText().toString().isEmpty())){
                        childFName = etFirstName.getText().toString();
                    }


                    String motherFname = null;
                    if (!(etMotherFirstName.getText().toString().equals("") || etMotherFirstName.getText().toString().isEmpty())) {
                        motherFname = etMotherFirstName.getText().toString();
                    }else{
                        emptyInputDetected++;
                    }

                    String surname = null;
                    if (!(etSurname.getText().toString().equals("") || etSurname.getText().toString().isEmpty())) {
                        surname = etSurname.getText().toString();
                    }else{
                        emptyInputDetected++;
                    }

                    String motherSName = null;
                    if (!(etMotherSurname.getText().toString().equals("") || etMotherSurname.getText().toString().isEmpty())) {
                        motherSName = etMotherSurname.getText().toString();
                    }else{
                        emptyInputDetected++;
                    }

                    String placeOBId = null;
                    String healthFacility = null;
                    String villageName = null;
                    String status = null;

                    if (emptyInputDetected == 6){
                        //wont be executed!
                    }else {
                        try {
                            childrensrv = app.searchChild(null, null, null, motherFname, new SimpleDateFormat("yyyy-MM-dd").format(bdate), new SimpleDateFormat("yyyy-MM-dd").format(bdate), null, null,
                                    motherSName, null, null, null, null);

                            if (childrensrv == null || childrensrv.isEmpty()) {
                                RegisterChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        avi.hide();
                                        infoText.setText("");
                                    }
                                });
                            } else if (childrensrv.size() > 0) {
                                RegisterChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Create and show the dialog.
                                        if (childrensrv.size() > 0) {
                                            childListFromOutsideFacility = true;
                                            infoText.setText("The child you are trying to register was registered in Maternity ward. Please confirm on the above list");
                                            avi.hide();
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(etFirstName.getWindowToken(), 0);
                                            fillSearchResultTable(childrensrv, true);
                                        }
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.setData(dob).start();

    }

    private void childSynchronization(final String id){
        Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                // Do some long running operation
                int parse_status = 0;
                String village_id, hf_id;

                parse_status = app.parseChildById(id);
                Log.d("parseChildCollectorbyId", parse_status+"");
                if (parse_status != 2 && parse_status != 3) {
                    DatabaseHandler db = app.getDatabaseInstance();
                    parseHFIDWhenNotInDb(db, app);
                    Cursor cursor = null;
                    Log.d("child id", id);
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE ID=?", new String[]{id});
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));
                        hf_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID));
                        Log.d("search hf id", hf_id);

                        int found = 0;
                        List<HealthFacility> a = db.getAllHealthFacility();
                        for (HealthFacility b : a) {
                            if (b.getId().equalsIgnoreCase(hf_id)) {
                                found = 1;
                            }
                        }

                        if (found == 0 && hf_id != null) {
                            app.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                app.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return Observable.just(parse_status);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<Integer>bindToLifecycle())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Intent childDetailsActivity = new Intent(RegisterChildFragment.this.getActivity(), ChildDetailsActivity.class);
                        childDetailsActivity.putExtra("barcode", childOfInterest.getBarcodeID());
                        childDetailsActivity.putExtra("myChild", childOfInterest);
                        childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, childOfInterest.getId());
                        startActivity(childDetailsActivity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });

    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            Log.d("parseChildCollectorbyId", "Parsed the HF");
        }
    }

}
