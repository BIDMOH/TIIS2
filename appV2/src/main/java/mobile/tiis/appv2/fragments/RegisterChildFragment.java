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
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.ExpandableWeightLayout;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
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
import mobile.tiis.appv2.entity.Place;
import mobile.tiis.appv2.ChildDetailsActivity;
import mobile.tiis.appv2.adapters.AdapterGridDataSearchResult;
import mobile.tiis.appv2.entity.Child;
import mobile.tiis.appv2.entity.HealthFacility;

/**
 * Created by issymac on 11/12/15.
 */
public class RegisterChildFragment extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    public List<String> motherVVU, gender, motherTT2, spinnerYears;

    public List<MaterialEditText> searchSectionFields, registerSectionFields;

    private Date bdate;

    List<Place> placeList;

    List<Birthplace> birthplaceList;

    PlacesOfBirthAdapter vvuAdapter, genderAdapter, tt2Adapter, yearSpinnerAdapter;

    public MaterialSpinner placeOfBirthSpinner, genderSpinner, placeOfDomicileSpinner, motherVVUStatusSpinner, motherTT2StatusSpinner, registryYearSpinner;

    public MaterialEditText dateOfBirth;

    public CardView searchButton, clearButton;

    public ImageButton searchbtn;

    public ExpandableWeightLayout expandableResultLayout;

    public TableLayout resultTableLayout;

    public Button scanButton, submitButton;

    int spPlacePos, spVillagePos , notApplicablePos = -1;

    private boolean isSavingData = false;

    private MaterialEditText etChildCumulativeSn, etbarcode, etFirstName, etSurname, etMotherFirstName, etMotherSurname, etPhone, etNotes,etFirstname2;
    private MaterialEditText searchSectionBarcode, searchSectionFname, searchSectionSname, searchSectionDob, searchSectionMotherFname, searchSectionMotherSname;

    private String barcode, firstanme, surname, motherFirstname, motherLastname, gender_val, gen, genderChildWithoutApp,firstname2, childRegistryYear;
    private DatabaseHandler mydb;
    private ProgressDialog progressDialog;
    private BackboneApplication app;

    public AVLoadingIndicatorView avi;
    public TextView infoText;

    public boolean isSearch = false;
    public boolean childListFromOutsideFacility = false;

    public Child childOfInterest;

    ArrayList<Child> childrensrv = new ArrayList<>();

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register_child, null);
        setUpView(root);

        //call search section listener
        searchSectionListener();

        app = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

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



        dateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    pickDate();
                }
            }
        });

//        scanButton.setOnClickListener(RegisterChildFragment.this);
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;
    }

    public void setUpView(View v){

        searchSectionFields     = new ArrayList<>();
        registerSectionFields   = new ArrayList<>();

        avi                     = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        infoText                = (TextView) v.findViewById(R.id.info_text);

        expandableResultLayout  = (ExpandableWeightLayout) v.findViewById(R.id.expandable_result_layout);
        resultTableLayout       = (TableLayout) v.findViewById(R.id.result_table_layout);
        resultTableLayout       .setVisibility(View.GONE);

        searchbtn               = (ImageButton) v.findViewById(R.id.search_btn_child);
        searchbtn               .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchChildLocally();
            }
        });

        searchButton            = (CardView) v.findViewById(R.id.search_button);
        searchButton            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchChildLocally();
            }
        });

        clearButton             = (CardView) v.findViewById(R.id.clear_button);
        clearButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        searchSectionBarcode    = (MaterialEditText) v.findViewById(R.id.search_barcode);
        searchSectionFields.add(searchSectionBarcode);

        searchSectionFname      = (MaterialEditText) v.findViewById(R.id.search_fname);
        searchSectionFields.add(searchSectionFname);

        searchSectionSname      = (MaterialEditText) v.findViewById(R.id.search_sname);
        searchSectionFields.add(searchSectionSname);

        searchSectionDob        = (MaterialEditText) v.findViewById(R.id.search_dob);
        searchSectionFields.add(searchSectionDob);

        searchSectionMotherFname= (MaterialEditText) v.findViewById(R.id.search_mother_fname);
        searchSectionFields.add(searchSectionMotherFname);

        searchSectionMotherSname= (MaterialEditText) v.findViewById(R.id.search_mother_sname);
        searchSectionFields.add(searchSectionMotherSname);

        etbarcode           = (MaterialEditText) v. findViewById(R.id.reg_barcode);
        registerSectionFields.add(etbarcode);

        etFirstName         = (MaterialEditText) v. findViewById(R.id.reg_fname);
        registerSectionFields.add(etFirstName);

        etFirstname2        = (MaterialEditText) v. findViewById(R.id.reg_mname);
        etSurname           = (MaterialEditText) v. findViewById(R.id.reg_surname);
        registerSectionFields.add(etSurname);

        dateOfBirth         = (MaterialEditText) v.findViewById(R.id.reg_dob);
        registerSectionFields.add(dateOfBirth);

        etMotherFirstName   = (MaterialEditText) v. findViewById(R.id.reg_mot_fname);
        registerSectionFields.add(etMotherFirstName);

        etMotherSurname     = (MaterialEditText) v. findViewById(R.id.reg_mot_sname);
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

        submitButton = (Button) v.findViewById(R.id.reg_submit_btn);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Saving the child. \nPlease wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    public void clearFields(){
        searchSectionBarcode    .setText("");
        searchSectionFname  .setText("");
        searchSectionSname  .setText("");
        searchSectionDob    .setText("");
        searchSectionMotherFname    .setText("");
        searchSectionMotherSname    .setText("");
        expandableResultLayout  .collapse();
    }

    public void searchChildLocally(){
        avi.show();
        if (!searchSectionBarcode.getText().toString().isEmpty() ||
                !searchSectionFname.getText().toString().isEmpty() ||
                !searchSectionSname.getText().toString().isEmpty() ||
                !searchSectionDob.getText().toString().isEmpty() ||
                !searchSectionMotherFname.getText().toString().isEmpty() ||
                !searchSectionMotherSname.getText().toString().isEmpty()){

            searchChildTask searchTask = new searchChildTask();
            searchTask.execute("");

        }else {
            //all the fields are empty show the all fields are empty message
            Toast.makeText(RegisterChildFragment.this.getActivity(),
                    "All the fields are empty, input fields to search",
                    Toast.LENGTH_LONG)
                    .show();
            avi.hide();
        }
    }

    /**
     * set the edit listener for the search section fields
     * If user types on the search section fields the same inputs should appear on the register section screen
     * This is to prevent user from typing the same thing more than once on the search section and during registration
     */
    public void searchSectionListener(){

//        TODO: Very sad that this first approach did not work : cool concept, will get back to it
//        for (int i=0; i<searchSectionFields.size(); i++){
//            MaterialEditText searchMet  = searchSectionFields.get(i);
//            final MaterialEditText regMet     = registerSectionFields.get(i);
//
//                searchMet.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                        regMet.setText(searchSectionFields.get(i).getText().toString());
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//
//                    }
//                });
//        }

        searchSectionBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etbarcode.setText(searchSectionBarcode.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchSectionFname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etFirstName.setText(searchSectionFname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchSectionSname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etSurname.setText(searchSectionSname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchSectionDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    isSearch = true;
                    pickDate();
                }
            }
        });

        searchSectionDob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dateOfBirth.setText(searchSectionDob.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchSectionMotherFname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etMotherFirstName.setText(searchSectionMotherFname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchSectionMotherSname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etMotherSurname.setText(searchSectionMotherSname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.reg_submit_btn){
            if(!isSavingData) {
                isSavingData = true;
                progressDialog.show();
                BackboneApplication app = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
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

//                  progBar.setVisibility(View.VISIBLE);
                    //kontrrollojme nese kemi ne db kete child me keto te dhena,nese true nxjerim dialog,nese false bejme regjistrimin
                    if (mydb.isChildinDb(etSurname.getText().toString(), bdate.getTime(), gender_val)) {
                        progressDialog.dismiss();
                        createDialogAlertIsInChild().show();
                        isSavingData =false;
                    } else {
                        if(!progressDialog.isShowing())
                            progressDialog.show();
                        askServerIfthereIsSimilarChild(etSurname.getText().toString(), bdate, gen);
                        Log.e("CheckInSever", "CheckInSever");
                    }

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
//        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
//        dp.setMaxDate(new Date().getTime());
//        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        dpd.setAccentColor(Color.DKGRAY);
        dpd.setMaxDate(Calendar.getInstance());
        dpd.show(this.getActivity().getFragmentManager(), "DatePickerDialogue");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

//        String date = ""+(monthOfYear + 1)+"/"+dayOfMonth+ "/" + year;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0);
        Date new_date = calendar.getTime();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+0300"));
        bdate = calendar.getTime();

        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");

        Date dNow = new Date();

        if (getDaysDifference(new_date, dNow) > 0) {
            if (isSearch){
                searchSectionDob.setText(ft.format(new_date));
                isSearch = false;
            }else {
                dateOfBirth.setText(ft.format(new_date));
            }
        } else {
            if (isSearch){
                searchSectionDob.setText(ft.format(new_date));
                isSearch = false;
            }else {
                dateOfBirth.setText(ft.format(new_date));
            }
        }

//        Calendar cal = new GregorianCalendar();
//        cal.set(year, (monthOfYear+1), dayOfMonth);
//        cal.add(cal.YEAR, 1);
//        cal.add(cal.DAY_OF_MONTH, -1);

//        String displayDate = dayOfMonth+"-"+getMonth(monthOfYear+1)+"-"+year;
//        dateOfBirth.setText(displayDate);

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
//        if (etFirstName.getText().toString().isEmpty()) {
//            etFirstName.setErrorColor(Color.RED);
//            etFirstName.setError(getString(R.string.empty_firstname));
//            return false;
//        }
//        if (etSurname.getText().toString().isEmpty()) {
//            etSurname.setError(getString(R.string.empty_surname));
//            etSurname.setErrorColor(Color.RED);
//            return false;
//        }

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
            dateOfBirth.setError(getString(R.string.future_birth_date));
            dateOfBirth.setErrorColor(Color.RED);
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


        if (genderSpinner.getSelectedItemPosition() == -1) {
            genderSpinner.setError(getString(R.string.empty_gender));
            genderSpinner.setErrorColor(Color.RED);
            return false;
        }


        if (etChildCumulativeSn.length() > 0){
            if(mydb.isChildRegistrationNoPresentInDb(spinnerYears.get(registryYearSpinner.getSelectedItemPosition() - 1),etChildCumulativeSn.getText().toString(),etbarcode.getText().toString())){
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


        BackboneApplication backboneApplication = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
        contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, backboneApplication.getLOGGED_IN_USER_HF_ID());
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
        contentValues.put(SQLHandler.ChildColumns.CUMULATIVE_SERIAL_NUMBER, removeWhiteSpaces(etChildCumulativeSn.getText().toString()));
        contentValues.put(SQLHandler.ChildColumns.CHILD_REGISTRY_YEAR, removeWhiteSpaces(childRegistryYear));
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

            BackboneApplication app = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
            contentValues.put("MODIFIED_BY", app.getLOGGED_IN_USER_ID());
            if (mydb.registerChild(contentValues) > -1) {
                mydb.InsertVaccinationsForChild(uuid, app.getLOGGED_IN_USER_ID());
                showDialogWhenRegisterIsDone();
                if (gen.equals("M")) {
                    genderChildWithoutApp = "true";
                } else {
                    genderChildWithoutApp = "false";
                }

                Calendar modifiedOn =Calendar.getInstance();
                try {
                    registerChildWithoutAppointments(removeWhiteSpaces(etbarcode.getText().toString()),
                            removeWhiteSpaces(etFirstName.getText().toString()),
                            removeWhiteSpaces(etSurname.getText().toString()),
                            bdate,
                            genderChildWithoutApp,
                            app.getLOGGED_IN_USER_HF_ID(),
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
                            motherTT2.get(motherTT2StatusSpinner.getSelectedItemPosition() - 1),
                            removeWhiteSpaces(etChildCumulativeSn.getText().toString()),
                            childRegistryYear);
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }else{
//                progBar.setVisibility(View.GONE);
            }
            isSavingData =false;

        }

    }

    public Dialog createDialogAlertIsInChild() {

        return new AlertDialog.Builder(RegisterChildFragment.this.getActivity())
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.child_with_this_data_exist))
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                registerChildInDB();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
//                                progBar.setVisibility(View.GONE);
                            }
                        }
                )
                .create();
    }

    private synchronized void askServerIfthereIsSimilarChild(String lastname, final Date bdate, String gender) {
        new Thread() {
            String threadBDateString;
            String threadLastname
                    ,
                    threadGender;

            public Thread setData(String threadLastname, Date threadBDate, String threadGender) {

                try {
                    this.threadLastname = threadLastname;
                    this.threadBDateString = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(threadBDate), "utf-8");
                    this.threadGender = threadGender;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return this;
            }

            @Override
            public void run() {
                super.run();

                BackboneApplication backbone = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();

                final boolean found = backbone.checkChildInServer(threadLastname, threadBDateString, threadGender);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (found) {
                            createDialogAlertIsInChild().show();
                        } else {
                            registerChildInDB();
                        }


                    }
                });
            }
        }.setData(lastname, bdate, gender).start();
    }

    private String removeWhiteSpaces(String withWhiteSpace){
        String withoutWhiteSpace;
        withoutWhiteSpace = withWhiteSpace.replaceAll("\\s+", "");
        return withoutWhiteSpace;
    }

    private synchronized void registerChildWithoutAppointments(String barcode, String fristname, String lastname, Date bDate, String gender, String  hfid, String birthPlaceId, String domId,
                                                               String addr, String phone, String motherFirstname, String motherLastname, String notes, String userID, Date modOn, final String tempId,String firstname2, String threadMotherVVUStatus, String threadMotherTT2Status, String childCummulativeSn, String childRegistryYear) {
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
                                  String threadAddr, String threadPhone, String threadMotherFirstname, String threadMotherLastname, String threadNotes, String threadUserID, Date threadModOn, String tempId,String threadFirstname2, String threadMotherVVUStatus, String threadMotherTT2Status, String threadCummulativeSn, String threadChildRegistryYear) {

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

                BackboneApplication backbone = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();

                int results = backbone.registerChildWithAppoitments(threadbarcode, threadfristname, threadLastname, threadBDateString, threadGender, threadhfid, threadBirthPlaceID, threadDomID, threadAddr
                        , threadPhone, threadMotherFirstname, threadMotherLastname, threadNotes, threadUserID, threadModOn, null,threadFirstname2, threadTempId, threadbarcode, threadMotherVVUStatus, threadMotherTT2Status, childCummulativeSn, childRegistryYear);
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

        }.setData(barcode, fristname, lastname, bDate, gender, hfid, birthPlaceId, domId, addr, phone, motherFirstname, motherLastname, notes, userID, modOn, tempId,firstname2, threadMotherVVUStatus, threadMotherTT2Status, childCummulativeSn, childRegistryYear).start();
    }

    private class searchChildTask extends AsyncTask<String, Void, Integer> {

        String num = "0";

        List<Child> children = new ArrayList<>();

        String searchBarcode, firstName, surName, dob, motherFirstName, motherSurname;

        Date dateOfBirth;

        @Override
        protected void onPreExecute() {
            expandableResultLayout.expand();
            avi.show();
            infoText.setText("Searching within catchment");
            resultTableLayout.removeAllViews();

            searchBarcode = "";
            if (!searchSectionBarcode.getText().toString().isEmpty()){
                searchBarcode = searchSectionBarcode.getText().toString();
            }

            firstName = "";
            if (!searchSectionFname.getText().toString().isEmpty()){
                firstName = searchSectionFname.getText().toString();
            }

            surName = "";
            if (!searchSectionSname.getText().toString().isEmpty()){
                surName = searchSectionSname.getText().toString();
            }

            dob = "";
            if (!searchSectionDob.getText().toString().isEmpty()){
                dob = searchSectionDob.getText().toString();
            }

            dateOfBirth = null;
            SimpleDateFormat fmt = new SimpleDateFormat("d/M/yyyy");
            try {
                dateOfBirth = fmt.parse(dob);
            } catch (ParseException e) {
                e.printStackTrace();
                dateOfBirth = null;
            }

            motherFirstName = "";
            if (!searchSectionMotherFname.getText().toString().isEmpty()){
                motherFirstName = searchSectionMotherFname.getText().toString();
            }

            motherSurname = "";
            if (!searchSectionMotherSname.getText().toString().isEmpty()){
                motherSurname = searchSectionMotherSname.getText().toString();
            }

        }

        @Override
        protected Integer doInBackground(String... params) {
            int responce = 0;
            children = mydb.searchChild(searchBarcode,
                    firstName, "", motherFirstName, ((dateOfBirth != null) ? (dateOfBirth.getTime() / 1000) + "" : ""), ((dateOfBirth != null) ? (dateOfBirth.getTime() / 1000) + "" : ""),"", surName, motherSurname,
                    "", "", "", "", num);

            return responce;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if((children == null)){
                avi.hide();
                infoText.setText("No Child was found with the given criteria, continue with registering");
                searchOutsideFacility();
            }else{
                Log.d("PANDA", "Size of result is "+children.size());
                avi.hide();
                infoText.setText("");
                childListFromOutsideFacility = false;
                fillSearchResultTable(children);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    public void fillSearchResultTable(List<Child> children){

        resultTableLayout.setVisibility(View.VISIBLE);
        resultTableLayout.removeAllViews();
        int n = 1;

        LayoutInflater li = (LayoutInflater) RegisterChildFragment.this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View titleView = li.inflate(R.layout.child_register_search_results_header, null);
        resultTableLayout.addView(titleView);

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
                        ChildSynchronization task = new ChildSynchronization();
                        task.execute(childOfInterest.getId());
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
        }else {
            Toast.makeText(RegisterChildFragment.this.getActivity(), "Add criterias to search outside facility", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isThereCorrectNumberOfCriteria(){
        if (searchSectionFname.getText().toString().isEmpty()
                || searchSectionSname.getText().toString().isEmpty()
                || searchSectionMotherFname.getText().toString().isEmpty() || searchSectionMotherSname.getText().toString().isEmpty()){
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
            dateOfBirth = fmt.parse(searchSectionDob.getText().toString());
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
                    if (!(searchSectionBarcode.getText().toString().equals("") || searchSectionBarcode.getText().toString().isEmpty())){
                        childBarcode = searchSectionBarcode.getText().toString();
                    }

                    String childFName  = null;
                    if (!(searchSectionFname.getText().toString().equals("") || searchSectionFname.getText().toString().isEmpty())){
                        childFName = searchSectionFname.getText().toString();
                    }

                    String ChildMName = null;
//                    if (!(searchSectionSname.getText().toString().equals("") || metMName.getText().toString().isEmpty())){
//                        ChildMName = metMName.getText().toString();
//                    }else{
//                        emptyInputDetected++;
//                    }

                    String motherFname = null;
                    if (!(searchSectionMotherFname.getText().toString().equals("") || searchSectionMotherFname.getText().toString().isEmpty())) {
                        motherFname = searchSectionMotherFname.getText().toString();
                    }else{
                        emptyInputDetected++;
                    }

                    String surname = null;
                    if (!(searchSectionSname.getText().toString().equals("") || searchSectionSname.getText().toString().isEmpty())) {
                        surname = searchSectionSname.getText().toString();
                    }else{
                        emptyInputDetected++;
                    }

                    String motherSName = null;
                    if (!(searchSectionMotherSname.getText().toString().equals("") || searchSectionMotherSname.getText().toString().isEmpty())) {
                        motherSName = searchSectionMotherSname.getText().toString();
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
                        BackboneApplication backbone = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();

                        childrensrv = backbone.searchChild(childBarcode, childFName, null, motherFname, null, null, null, surname,
                                motherSName, placeOBId, healthFacility, villageName, status);

                        if (childrensrv == null || childrensrv.isEmpty()) {
                            RegisterChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(getApplicationContext(), "Communication was not successful,try again", Toast.LENGTH_LONG).show();
                                    avi.hide();
                                    infoText.setText("Child not found, continue with registration");
                                }
                            });
                        } else if (childrensrv.size() > 0) {
                            RegisterChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Create and show the dialog.
                                    if (childrensrv.size() > 0) {
                                        childListFromOutsideFacility = true;
                                        infoText.setText("");
                                        avi.hide();
                                        fillSearchResultTable(childrensrv);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }.setData(dob).start();

    }


    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
            int parse_status = 0;
            String village_id, hf_id;

            for (String id : params) {
                parse_status = application.parseChildById(id);
                Log.d("parseChildCollectorbyId", parse_status+"");
                if (parse_status != 2 && parse_status != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();
                    parseHFIDWhenNotInDb(db, application);
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
                            application.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                application.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return parse_status;
        }

        @Override
        protected void onPostExecute(Integer result) {

            Intent childDetailsActivity = new Intent(RegisterChildFragment.this.getActivity(), ChildDetailsActivity.class);
            childDetailsActivity.putExtra("barcode", childOfInterest.getBarcodeID());
            childDetailsActivity.putExtra("myChild", childOfInterest);
            childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, childOfInterest.getId());
            startActivity(childDetailsActivity);

        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            Log.d("parseChildCollectorbyId", "Parsed the HF");
        }
    }

}
