package mobile.tiis.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
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
import mobile.tiis.app.ChildDetailsActivity;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.PlacesOfBirthAdapter;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Birthplace;
import mobile.tiis.app.entity.Place;

/**
 * Created by issymac on 11/12/15.
 */
public class RegisterChildFragment extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    public List<String> placesOfBirthsList, gender, placesOfDomicileList;

    private Date bdate;

    List<Place> placeList;

    List<Birthplace> birthplaceList;

    PlacesOfBirthAdapter pobAdapter, genderAdapter, podAdapter;

    public MaterialSpinner placeOfBirthSpinner, genderSpinner, placeOfDomicileSpinner;

    public MaterialEditText dateOfBirth;

    public Button scanButton, submitButton;

    int spPlacePos, spVillagePos , notApplicablePos = -1;

    private boolean isSavingData = false;

    protected MaterialEditText etbarcode, etFirstName, etSurname, etMotherFirstName, etMotherSurname, etPhone, etNotes,etFirstname2;

    String barcode, firstanme, surname, motherFirstname, motherLastname, gender_val, gen, genderChildWithoutApp,firstname2;


    private ProgressDialog progressDialog;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register_child, null);
        setUpView(root);

        BackboneApplication app = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();

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


        gender = new ArrayList<>();
        gender.add("Male");
        gender.add("Female");

        dateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    pickDate();
                }
            }
        });

        scanButton.setOnClickListener(RegisterChildFragment.this);
        submitButton.setOnClickListener(RegisterChildFragment.this);

//        pobAdapter = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, placesOfBirthsList);
//        podAdapter = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, placesOfDomicileList);
        genderAdapter = new PlacesOfBirthAdapter(RegisterChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, gender);

//        placeOfBirthSpinner.setAdapter(pobAdapter);
//        placeOfDomicileSpinner.setAdapter(podAdapter);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
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

        etbarcode = (MaterialEditText) v. findViewById(R.id.reg_barcode);
        etFirstName = (MaterialEditText) v. findViewById(R.id.reg_fname);
        etFirstname2 = (MaterialEditText) v.findViewById(R.id.reg_mname);
        etSurname = (MaterialEditText) v. findViewById(R.id.reg_surname);
        etMotherFirstName = (MaterialEditText) v. findViewById(R.id.reg_mot_fname);
        etMotherSurname = (MaterialEditText) v. findViewById(R.id.reg_mot_sname);
        etPhone = (MaterialEditText) v. findViewById(R.id.reg_phone);
        etNotes = (MaterialEditText) v. findViewById(R.id.reg_notes);
        dateOfBirth = (MaterialEditText) v.findViewById(R.id.reg_dob);

        placeOfBirthSpinner = (MaterialSpinner) v.findViewById(R.id.reg_spin_pob);
        placeOfDomicileSpinner = (MaterialSpinner) v.findViewById(R.id.reg_spin_pod);
        genderSpinner = (MaterialSpinner) v.findViewById(R.id.reg_spin_gender);

        scanButton = (Button) v.findViewById(R.id.reg_scan_btn);
        submitButton = (Button) v.findViewById(R.id.reg_submit_btn);

        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Saving the child. \nPlease wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);



//        btnScan = (Button) findViewById(R.id.register_child_btn_scan);
//        btnScan.setOnClickListener(this);
//        btnSave = (Button) findViewById(R.id.save_button);
//        btnSave.setOnClickListener(this);
//        btnBirthdate = (Button) findViewById(R.id.birthdate_field);
//        btnBirthdate.setOnClickListener(this);
//        spPlaceOfBirth = (Spinner) findViewById(R.id.place_of_birth);
//        spVillage = (Spinner) findViewById(R.id.village_domicile);
//        rBtnMale = (RadioButton) findViewById(R.id.male_radio);
//        rbtnFemale = (RadioButton) findViewById(R.id.female_radio);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reg_scan_btn) {
            if (barcode != null) {
                if (!barcode.equalsIgnoreCase("")) {
                    showDialogGoingScanWhenHasBarcode();
                }

            }
        }

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
            dateOfBirth.setText(ft.format(new_date));
        } else {
            dateOfBirth.setText(ft.format(new_date));
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
        if (etMotherFirstName.getText().toString().isEmpty() || etMotherSurname.getText().toString().isEmpty()) {
            etMotherFirstName.setError(getString(R.string.empty_mother_names));
            etMotherFirstName.setErrorColor(Color.RED);
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
//        If not applicable is selected in the Birthplace or Domicile spinners and Notes is empty , than we need the user to fill the notes field
//        if (spVillage.getSelectedItemPosition() == notApplicablePos && etNotes.getText().toString().isEmpty()) {
//            alertDialogBuilder.setMessage(getString(R.string.empty_notes));
//            alertDialogBuilder.show();
//            progBar.setVisibility(View.GONE);
//            return false;
//        }

        if (genderSpinner.getSelectedItemPosition() == -1) {
            genderSpinner.setError(getString(R.string.empty_gender));
            genderSpinner.setErrorColor(Color.RED);
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

            contentValues.put(SQLHandler.ChildColumns.BARCODE_ID, etbarcode.getText().toString());
        }


        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME1, etFirstName.getText().toString());
        contentValues.put(SQLHandler.ChildColumns.FIRSTNAME2, etFirstname2.getText().toString());


        if (!etSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.LASTNAME1, etSurname.getText().toString());
        }
        if (bdate != null && bdate.compareTo(new Date()) < 0) {
            contentValues.put(SQLHandler.ChildColumns.BIRTHDATE, BackboneActivity.stringToDateParser(bdate));
        }
        if (!etMotherFirstName.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_FIRSTNAME, etMotherFirstName.getText().toString());
        }
        if (!etMotherSurname.getText().toString().equalsIgnoreCase("")) {
            contentValues.put(SQLHandler.ChildColumns.MOTHER_LASTNAME, etMotherSurname.getText().toString());
        }


        BackboneApplication backboneApplication = (BackboneApplication) RegisterChildFragment.this.getActivity().getApplication();
        contentValues.put(SQLHandler.ChildColumns.HEALTH_FACILITY_ID, backboneApplication.getLOGGED_IN_USER_HF_ID());
        contentValues.put(SQLHandler.ChildColumns.ADDRESS, "");
        contentValues.put(SQLHandler.ChildColumns.MODIFIED_ON, new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));


        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE, birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getName());
        contentValues.put(SQLHandler.ChildColumns.BIRTHPLACE_ID, birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getId());

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
        contentValues.put(SQLHandler.ChildColumns.PHONE, etPhone.getText().toString());
        contentValues.put(SQLHandler.ChildColumns.NOTES, etNotes.getText().toString());
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
            DatabaseHandler mydb = app.getDatabaseInstance();
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
                    registerChildWithoutAppointments(etbarcode.getText().toString(),
                            etFirstName.getText().toString(),
                            etSurname.getText().toString(),
                            bdate,
                            genderChildWithoutApp,
                            app.getLOGGED_IN_USER_HF_ID(),
                            birthplaceList.get(placeOfBirthSpinner.getSelectedItemPosition() - 1).getId(),
                            placeList.get(placeOfDomicileSpinner.getSelectedItemPosition() - 1).getId(),
                            "",
                            etPhone.getText().toString(),
                            etMotherFirstName.getText().toString(),
                            etMotherSurname.getText().toString(),
                            etNotes.getText().toString(),
                            app.getLOGGED_IN_USER_ID(),
                            modifiedOn.getTime(),
                            uuid,etFirstname2.getText().toString());
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

    private synchronized void registerChildWithoutAppointments(String barcode, String fristname, String lastname, Date bDate, String gender, String  hfid, String birthPlaceId, String domId,
                                                               String addr, String phone, String motherFirstname, String motherLastname, String notes, String userID, Date modOn, final String tempId,String firstname2) {
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

            public Thread setData(String threadbarcode, String threadfristname, String threadLastname, Date threadBDate, String threadGender, String threadhfid, String threadBirthPlaceID, String threadDomID,
                                  String threadAddr, String threadPhone, String threadMotherFirstname, String threadMotherLastname, String threadNotes, String threadUserID, Date threadModOn, String tempId,String threadFirstname2) {

                try {
                    this.threadbarcode = threadbarcode;
                    this.threadfristname = threadfristname;
                    this.threadFirstname2 = threadFirstname2;
                    this.threadLastname = threadLastname;
                    this.threadGender = threadGender;
                    this.threadhfid = threadhfid;
                    this.threadBirthPlaceID = threadBirthPlaceID;
                    this.threadDomID = threadDomID;
                    this.threadAddr = threadAddr;
                    this.threadPhone = threadPhone;
                    this.threadMotherFirstname = threadMotherFirstname;
                    this.threadMotherLastname = threadMotherLastname;
                    this.threadNotes = threadNotes;
                    this.threadUserID = threadUserID;
                    this.threadTempId = tempId;
                    this.threadModOn = URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(threadModOn), "utf-8");
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

                int results = backbone.registerChildWithAppoitments(threadbarcode, threadfristname, threadLastname, threadBDateString, threadGender, threadhfid, threadBirthPlaceID, threadDomID, threadAddr
                        , threadPhone, threadMotherFirstname, threadMotherLastname, threadNotes, threadUserID, threadModOn, null,threadFirstname2,threadTempId,threadbarcode);
                if(results!=-1) {
                    Intent childDetailsActivity = new Intent(getActivity(), ChildDetailsActivity.class);
                    Bundle bnd = new Bundle();
                    bnd.putString(BackboneApplication.CHILD_ID, results + "");
                    bnd.putString("barcode", threadbarcode);
                    bnd.putInt("current", 0);
                    childDetailsActivity.putExtras(bnd);

                    Log.d("coze", "starting activity");
                    startActivity(childDetailsActivity);
                }else{
                    Intent childDetailsActivity = new Intent(getActivity(), ChildDetailsActivity.class);
                    Bundle bnd = new Bundle();
                    bnd.putString(BackboneApplication.CHILD_ID, threadTempId);
                    bnd.putString("barcode", threadbarcode);
                    childDetailsActivity.putExtras(bnd);

                    startActivity(childDetailsActivity);
                }
            }

        }.setData(barcode, fristname, lastname, bDate, gender, hfid, birthPlaceId, domId, addr, phone, motherFirstname, motherLastname, notes, userID, modOn, tempId,firstname2).start();
    }

}
