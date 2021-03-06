package mobile.tiis.appv2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import mobile.tiis.appv2.ChildDetailsActivity;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.SubClassed.BackHandledFragment;
import mobile.tiis.appv2.adapters.SingleTextViewAdapter;
import mobile.tiis.appv2.adapters.SingleTextViewAdapterForVaccineLot;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.entity.AdministerVaccinesModel;
import mobile.tiis.appv2.entity.NonVaccinationReason;
import mobile.tiis.appv2.entity.VaccinationAppointment;
import mobile.tiis.appv2.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

import static mobile.tiis.appv2.ChildDetailsActivity.childId;
import static mobile.tiis.appv2.base.BackboneApplication.TABLET_REGISTRATION_MODE_PREFERENCE_NAME;


/**
 * Created by issymac on 27/01/16.
 */
public class AdministerVaccineFragment extends BackHandledFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener{
    private static final String TAG = AdministerVaccineFragment.class.getSimpleName();
    private String appointment_id, birthdate, barcode;
    private BackboneApplication app;
    private DatabaseHandler dbh;
    private Boolean SavedState = false;
    private boolean outreachChecked = false;
    private boolean outreach = false;
    public  static boolean correnctDateSelected  = true;
    private Thread thread;
    boolean starter_set = false;
    private long daysDiff;
    private int counter = 0,DateDiffDialog = 0;
    private ArrayList<String> dosekeeper;
    private ArrayList<AdministerVaccinesModel> arrayListAdminVacc;
    private ListView vaccineDosesList;
    private TableLayout vaccinesListTableLayout;
    private CheckBox    vitACheckbox, mabendazolCheckbox, cbOutreach;
    private TextView vitADate, mabendazolDate;
    private Button saveButton;
    private MaterialEditText etNotes;
    private FragmentStackManager fm;
    private ProgressDialog progressDialog;
    private LinearLayout llSup;
    private String age = "";
    private boolean savingInProgress = false;
    private int pos;

    private boolean thereIsNoVaccinesInLot = false;
    private boolean isChildBackEntered = false;
    private Date backEnteredDate = new Date();
    private Date selectedDate = new Date();

    private String emptyVaccineLotsVaccines = "";

    AlertDialog.Builder alertDialogBuilder;

    Date new_date = new Date();
    AdministerVaccinesModel tempHoldingVaccineModel;
    View view;

    private Handler _hRedraw;

    private ProgressBar progressBar;
    private View scrollLayout;
    protected static final int REFRESH = 0;
    private Looper backgroundLooper;
    private TextView backEnteringChild;


    private Subscription subscription;

    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1;
    }

    public static final long getDaysDifference(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.HOUR, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.HOUR, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);

        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return Math.abs(difference);
    }

    private AppCompatActivity parent;

    private android.support.v7.app.ActionBar actionBar;
    private VaccinationAppointment appointment;

    Toolbar toolbar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_vaccinate, null);
        setUpView(root);

        parent = (AppCompatActivity) getActivity();
        actionBar = parent.getSupportActionBar();
        View v = (View) container;

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        app = (BackboneApplication) this.getActivity().getApplication();
        dbh = app.getDatabaseInstance();
        fm  = new FragmentStackManager(this.getActivity());

        app.saveNeeded = true;
        appointment_id  = getArguments().getString("appointment_id");
        birthdate       = getArguments().getString("birthdate");
        barcode         = getArguments().getString("barcode");
        SimpleDateFormat ft1 = new SimpleDateFormat("dd-MMM-yyyy");

        appointment = dbh.getVaccinationAppointmentById(appointment_id);

        try {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            String today = ft.format(dNow);
            Date bdate = BackboneActivity.dateParser(birthdate);
            String birthdateString = ft.format(bdate);
            Date date1 = ft.parse(birthdateString);
            Date date2 = ft.parse(today);
            int month = getMonthsDifference(date1, date2);
            daysDiff = getDaysDifference(date1, date2);

            if (month != 0) {
                age = month + " months";
            } else {
                long diff = date2.getTime() - date1.getTime();
                long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                Log.d("", "The diff" + difference);
                age = difference + " days";
            }

            Log.d("day14", "Age is : "+age);
            if(age.equals("9 months") || age.equals("6 months") || age.equals("18 months")){
                llSup.setVisibility(View.VISIBLE);
            }
            else{
                llSup.setVisibility(View.GONE);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        Date todayD = new Date();
        SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");
        vitADate.setText(ftD.format(todayD));
        mabendazolDate.setText(ftD.format(todayD));

        progressBar.setVisibility(View.VISIBLE);
        subscription = Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                dosekeeper = dbh.getDosesForAppointmentID(appointment_id);
                arrayListAdminVacc = new ArrayList<AdministerVaccinesModel>();
                Calendar c = Calendar.getInstance();
                Calendar c2 = new GregorianCalendar();
                c2.setTimeZone(TimeZone.getTimeZone("GMT"));
                c2.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),0,0,0);

                new_date = c2.getTime();

                for (String dose : dosekeeper) {
                    final AdministerVaccinesModel adminVacc = dbh.getPartOneAdminVaccModel(starter_set, appointment_id, dose);
                    starter_set = true;
                    dbh.getPartTwoAdminVacc(adminVacc, daysDiff, DateDiffDialog);
                    SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
                    adminVacc.setTime(ft.format(new_date));
                    adminVacc.setTime2(new_date);
                    //rowObjects.setInput(ft.format(newest_date));
                    //rowObjects.setDate(vaccination_date_col);

                    Log.d("timezone","default set time = "+new_date.getTime());
                    arrayListAdminVacc.add(adminVacc);
                }

                getChildId();

                return Observable.just(true);
            }
        }).subscribeOn(AndroidSchedulers.from(backgroundLooper))
        // Be notified on the main thread
        .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Boolean>bindToLifecycle())
        .subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted()");
                fillVaccineTableLayout(arrayListAdminVacc);
                //after receiving the result of getchild() method
                if (dbh.isChildSupplementedVitAToday(childId)) {
                    vitACheckbox.setChecked(true);
                    vitACheckbox.setEnabled(false);
                }
                if (dbh.isChildSupplementedMebendezolrToday(childId)) {
                    mabendazolCheckbox.setChecked(true);
                    mabendazolCheckbox.setEnabled(false);
                }

                progressBar.setVisibility(View.GONE);
                scrollLayout.setVisibility(View.VISIBLE);
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

        _hRedraw=new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what) {
                    case REFRESH:
                        redrawEverything();
                        break;
                }
            }
        };

        DateDiffDialog();

        return root;
    }

    public void setUpView(View v){
        vaccineDosesList        = (ListView) v.findViewById(R.id.lv_dose_list);
        vaccinesListTableLayout = (TableLayout) v.findViewById(R.id.vaccine_list_table);
        vitACheckbox = (CheckBox) v.findViewById(R.id.vit_a_check);
        mabendazolCheckbox      = (CheckBox) v.findViewById(R.id.mabendazol_check);
        cbOutreach              = (CheckBox) v.findViewById(R.id.cb_outreach);
        cbOutreach.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                outreachChecked=isChecked;
            }
        });

        vitADate                = (TextView) v.findViewById(R.id.vit_a_date);
        mabendazolDate          = (TextView) v.findViewById(R.id.mabendazol_date);
        etNotes                 = (MaterialEditText) v.findViewById(R.id.notes);

        llSup                   = (LinearLayout) v.findViewById(R.id.ll_sup);
        llSup                   . setVisibility(View.GONE);

        saveButton              = (Button) v.findViewById(R.id.addminister_vaccine_save_button);
        saveButton              .setOnClickListener(this);

        progressBar             = (ProgressBar) v.findViewById(R.id.progress_bar);
        scrollLayout             = v.findViewById(R.id.vaccinate_layout);
        progressBar             .setVisibility(View.GONE);
    }

    public void redrawEverything(){
        vaccinesListTableLayout.invalidate();
        vaccinesListTableLayout.refreshDrawableState();
    }

    public void fillVaccineTableLayout(ArrayList<AdministerVaccinesModel> arr){
        vaccinesListTableLayout.removeAllViews();
        emptyVaccineLotsVaccines = "";
        LayoutInflater li = (LayoutInflater) AdministerVaccineFragment.this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        app = (BackboneApplication) AdministerVaccineFragment.this.getActivity().getApplicationContext();
        dbh = app.getDatabaseInstance();

        int x = 0;
        for (final AdministerVaccinesModel item : arr){
            View rowView = li.inflate(R.layout.vaccine_dose_quantity_item, null);

            SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");
//            Date schedulddate = BackboneActivity.dateParser(item.getScheduled_Date_field());

            TextView tvDose                 = (TextView) rowView.findViewById(R.id.dose);
            final TextView tvVaccineDate    = (TextView)rowView.findViewById(R.id.vaccine_date);
            final Spinner spVaccLot         = (Spinner)rowView.findViewById(R.id.lot_spinner);
            final Spinner spReason          = (Spinner)rowView.findViewById(R.id.non_vacc_reason_spinner);
            final CheckBox chDone           = (CheckBox)rowView.findViewById(R.id.vaccine_administered_done_checkbox);
            final View view                 = (View) rowView.findViewById(R.id.split_dose);
            TextView backEnteringChild      = (TextView) rowView.findViewById(R.id.back_enter_info);

            tvDose.setText(item.getDoseName());
            tvVaccineDate.setText(item.getTime());
            final int y=x;
            tvVaccineDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = y;
                    tempHoldingVaccineModel = item;
                    pickDate();
                }
            });

            String [] tm = item.getScheduled_Date_field().split("\\(");
            String [] tLong;
            if(tm[1].contains("+")){
                tLong =  tm[1].split("\\+");
            }else if(tm[1].contains("-")){
                tLong =  tm[1].split("-");
            }else {
                tLong = tm;
            }

            String timeLong = tLong[0];

            Date scheduledDate  = new Date(Long.parseLong(timeLong));

            Calendar now = Calendar.getInstance();

            Date compDateOne = getZeroTimeDate(now.getTime());
            Date compDateTwo = getZeroTimeDate(scheduledDate);

            if (compDateOne.before(compDateTwo)){
                correnctDateSelected = false;
                Log.d("DATECOMPARE", "Date is not correct");
            }else{
                correnctDateSelected = true;
                Log.d("DATECOMPARE", "Correct Date");
            }

            chDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setStatus(String.valueOf(b));
                    //Toast.makeText(AdministerVaccinesActivity.this, "Value changed to " + b, Toast.LENGTH_SHORT).show();
                    if (!b) {
                        spReason.setVisibility(View.VISIBLE);
                        spVaccLot.setSelection(0);
                        view.setVisibility(View.VISIBLE);
                    }
                    if (b) {
                        spReason.setVisibility(View.GONE);
                        item.setNon_vac_reason("-1");
                        view.setVisibility(View.GONE);
                    }
                }
            });
            chDone.setChecked(Boolean.parseBoolean(item.getStatus()));

//#############################################################NON VACCINATION REASON SPINNER#################################################

            //NonVaccinationReason Column Spinner
            List<String> reasons = new ArrayList<String>();
            reasons.add("----");
            for (NonVaccinationReason nvElement : dbh.getAllNonvaccinationReasons()) {
                reasons.add(nvElement.getName());
            }


            final List<NonVaccinationReason> non_vaccination_reason_list_with_additions = dbh.getAllNonvaccinationReasons();
            NonVaccinationReason empty = new NonVaccinationReason();
            empty.setName("----");
            empty.setId("0");
            empty.setKeepChildDue("false");
            non_vaccination_reason_list_with_additions.add(empty);


            final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(AdministerVaccineFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, reasons);
            spReason.setAdapter(statusAdapterNonVaccinationReason);
            //item.setNon_vac_reason_pos(0);
            spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    spReason.setSelection(position);
                    item.setNon_vac_reason_pos(position);
                    for(NonVaccinationReason a : non_vaccination_reason_list_with_additions)
                    {
                        if (statusAdapterNonVaccinationReason.getItem(position).equalsIgnoreCase(a.getName())){
                            item.setNon_vac_reason(a.getId());
                            item.setKeep_child_due(Boolean.parseBoolean(a.getKeepChildDue()));
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });
            spReason.setSelection(item.getNon_vac_reason_pos());
            if(item.getStatus().equals(String.valueOf(true)))spReason.setVisibility(View.GONE);
            spReason.setMinimumWidth(220);


//############################################################# VACCINE LOT SPINNER ::::::.....

            item.removeZeroBalanceVaccineLots();

            SingleTextViewAdapterForVaccineLot statusAdapter = new SingleTextViewAdapterForVaccineLot(AdministerVaccineFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, item.getVaccine_lot_list());
            spVaccLot.setAdapter(statusAdapter);

            Date today          = new Date();

            Calendar cl = Calendar.getInstance();
            cl.setTime(scheduledDate);

            cl.setTime(today);
            Date compareDateOne = getZeroTimeDate(backEnteredDate);
            Date compareDateTwo = getZeroTimeDate(today);

            Log.d("SOMA", "selected Date "+compareDateOne);
            Log.d("SOMA", "today is "+compareDateTwo);
            Log.d("SOMA", "is Child Backentered "+isChildBackEntered);


            if (compareDateOne.compareTo(compareDateTwo)<0 && (isChildBackEntered)){
                spVaccLot.setSelection(1);
                item.setVaccination_lot_pos(1);
                item.setVaccination_lot(item.getVaccine_lot_map().get(item.getVaccine_lot_list().get(1)).toString());
                Log.d("RowCollId", item.getVaccination_lot());

                //Disable spinner and done checkbox
                spVaccLot.setEnabled(false);
            }else {
                if (item.getVaccine_lot_list().size() > 2) {
                    spVaccLot.setSelection(2);
                    item.setVaccination_lot_pos(2);
                    //setting the id of vaccine lot
                    item.setVaccination_lot(item.getVaccine_lot_map().get(item.getVaccine_lot_list().get(2)).toString());
                    Log.d("RowCollId", item.getVaccination_lot());
                } else {
//                    spVaccLot.setSelection(1);
//                    item.setVaccination_lot_pos(1);
//                    item.setVaccination_lot(item.getVaccine_lot_map().get(item.getVaccine_lot_list().get(1)).toString());
//                    Log.d("RowCollId", item.getVaccination_lot());
//
                    /**
                     * unchecking of the vaccine as not given and and setting the vaccination reason of "kukosekana kwa chanjo(out of stock) by default"
                     */
                    chDone.setChecked(false);
                    spReason.setSelection(reasons.indexOf("Kukosekana chanjo"));

                }
            }

            /**
            Check to see if the tablet is in registration mode then allow only backentering of childre
             */
            if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(TABLET_REGISTRATION_MODE_PREFERENCE_NAME, false)){
                spVaccLot.setSelection(1);
                item.setVaccination_lot_pos(1);
                item.setVaccination_lot(item.getVaccine_lot_map().get(item.getVaccine_lot_list().get(1)).toString());
                spReason.setVisibility(View.GONE);
                backEnteringChild.setVisibility(View.VISIBLE);
                //Disable spinner and done checkbox
                spVaccLot.setEnabled(false);
            }else {
                spReason.setVisibility(View.VISIBLE);
                backEnteringChild.setVisibility(View.GONE);
            }

            //rowCollector.setVaccination_lot_pos(1);
//        vac_lot_pos = 1;
            spVaccLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    spVaccLot.setSelection(position);
//                vac_lot_pos = position;
                    item.setVaccination_lot_pos(position);
//                    item.setVaccination_lot(item.getVaccine_lot_map().get(item.getVaccine_lot_list().get(position)).toString());
                    Log.d("RowCollId", item.getVaccination_lot());
                    Log.d("RowCollId", "position is " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }
            });

            vaccinesListTableLayout.addView(rowView);
            x++;

        }

    }

    public void pickDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog reaction_start_date_picker = DatePickerDialog.newInstance(
                AdministerVaccineFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Date bdate = BackboneActivity.dateParser(birthdate);

        Calendar toCalendar = Calendar.getInstance();
        reaction_start_date_picker.setMaxDate(toCalendar);
        Calendar dob=Calendar.getInstance();
        dob.setTimeInMillis(bdate.getTime());
        reaction_start_date_picker.setMinDate(dob);

        reaction_start_date_picker.show(((Activity) AdministerVaccineFragment.this.getActivity()).getFragmentManager(), "DatePickerDialogue");

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        ft.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date bdate = BackboneActivity.dateParser(birthdate);

        Calendar cal = new GregorianCalendar();
        cal.set(year,monthOfYear,dayOfMonth,0,0,0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        new_date = cal.getTime();

        Log.d("timezone","date selected from date picker = "+new_date.getTime());

        try {
            new_date = ft.parse(ft.format(new_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //Obtaining time in milliseconds from the scheduled time in string
        String [] tm = tempHoldingVaccineModel.getScheduled_Date_field().split("\\(");
        String [] tLong;
        if(tm[1].contains("+")){
            tLong =  tm[1].split("\\+");
        }else if(tm[1].contains("-")){
            tLong =  tm[1].split("-");
        }else {
            tLong = tm;
        }

        String timeLong = tLong[0];

        Date scheduledDate = new Date(Long.parseLong(timeLong));
        Log.d("currentchilddates", "new date is " + ft.format(new_date));
        Log.d("currentchilddates", "Scheduled date is " + ft.format(scheduledDate));

        Date compareDateOne = getZeroTimeDate(new_date);
        Date compareDateTwo = getZeroTimeDate(scheduledDate);
        selectedDate = compareDateOne;

        if (compareDateOne.before(compareDateTwo)){
            correnctDateSelected = false;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdministerVaccineFragment.this.getActivity());

            // set title
            alertDialogBuilder.setTitle("Warning");

            // set dialog message
            alertDialogBuilder
                    .setMessage("The selected vaccination date is before due date. Are you sure you want to set this date?")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setdates(pos, tempHoldingVaccineModel);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }else{
            correnctDateSelected = true;
            setdates(pos, tempHoldingVaccineModel);
        }

    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime( fecha );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public void setNew_date(Date date){
        new_date = date;
    }

    public Date getNew_date(){
        return new_date;
    }

    public Date setdates(final int pos, final AdministerVaccinesModel coll){
        Log.d("timezone","setting dates = "+new_date.getTime());

        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        if (getDaysDifference(new_date, coll.getTime2()) > 0) {
            coll.setTime(ft.format(new_date));
            ((TextView)vaccinesListTableLayout.getChildAt(pos).findViewById(R.id.vaccine_date)).setText(ft.format(new_date));
            coll.setTime2(new_date);
            int cc = 0;
            if (pos==0) {
                for (AdministerVaccinesModel others : arrayListAdminVacc) {
                    others.setTime(ft.format(new_date));
                    others.setTime2(new_date);
                }
            }
        }else {
            coll.setTime(ft.format(coll.getTime2()));
            ((TextView)vaccinesListTableLayout.getChildAt(pos).findViewById(R.id.vaccine_date)).setText(ft.format(coll.getTime2()));
            coll.setTime2(coll.getTime2());
            if (pos==0) {
                for (AdministerVaccinesModel others : arrayListAdminVacc) {
                    others.setTime(ft.format(coll.getTime2()));
                    others.setTime2(coll.getTime2());
                }

            }
        }

        Date today = new Date();
        Date compareToday = getZeroTimeDate(today);

        //check to see if the child is being backentered so as to default to No Lot
        if (selectedDate.before(compareToday)){
            Log.d("SOMA", "The child is being back entered");
            backEnteredDate = selectedDate;
            isChildBackEntered = true;
            vaccinesListTableLayout.removeAllViews();
            fillVaccineTableLayout(arrayListAdminVacc);
        }else {
            isChildBackEntered = false;
            vaccinesListTableLayout.removeAllViews();
            fillVaccineTableLayout(arrayListAdminVacc);
        }

        return new_date;
    }

    public void DateDiffDialog(){
        switch (DateDiffDialog) {
            case 1:
                final AlertDialog ad22first = new AlertDialog.Builder(this.getActivity()).create();
                ad22first.setTitle(getString(R.string.warning));
                ad22first.setMessage(getString(R.string.too_early_vaccination));
                ad22first.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22first.dismiss();
                    }
                });
                ad22first.show();
                break;
            case 2:
                final AlertDialog ad22second = new AlertDialog.Builder(this.getActivity()).create();
                ad22second.setTitle(getString(R.string.warning));
                ad22second.setMessage(getString(R.string.too_late_vaccination));
                ad22second.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22second.dismiss();
                    }
                });
                ad22second.show();
                break;
            default:
                break;
        }

    }

    private void getChildId() {
        Cursor getChildIdCursor = dbh.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                new String[]{String.valueOf(barcode)});
        if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
            getChildIdCursor.moveToFirst();
            childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            getChildIdCursor.close();
        } else {
            getChildIdCursor.close();
        }
    }

    @Override
    public void onClick(View view) {
        if(!savingInProgress) {
            switch (view.getId()) {
                case R.id.addminister_vaccine_save_button:
                    savingInProgress = true;

                    if (correnctDateSelected == false) {
                        if (thereIsNoVaccinesInLot){
                            /**
                             * Call the dialogue builder method with nested true to display both the No LOT message and the wrong date message
                             */
                            String message = "There is no LOT number for \n\n " + emptyVaccineLotsVaccines + "\nPlease make the necessary adjustment to reflect the physical stock or untick the above antigens";
                            createDialogWithMessages(message, true);
                        }else {
                            /**
                             * There is vaccine in LOT but the date is not correct so call the dialogue message method with nested value FALSE
                             * so as to display the wrong date only message
                             */
                            String message = "The selected vaccination date is before due date. Are you sure you want to set this date?";
                            createDialogWithMessages(message, false);
                        }
                    } else {
                        if (thereIsNoVaccinesInLot){
                            /**
                             * The correct date is selected but the vaccine LOT is not there so call the dialogue method with the NO LOT message only
                             * and nested False so as not to repeat the second message
                             */
                            String message = "There is no LOT number for \n\n " + emptyVaccineLotsVaccines + "\nPlease make the necessary adjustment to reflect the physical stock or untick the above antigens";
                            createDialogWithMessages(message, false);
                        }else{
                            /**
                             * The correct date is selected and the vaccine LOT is there so here we just call the method to save the vaccines
                             */
                            saveVaccines();
                        }
                    }
                    app.saveNeeded = false;
                    break;
            }
        }
    }

    public void saveVaccines() {
        progressDialog = new ProgressDialog(AdministerVaccineFragment.this.getActivity());
        progressDialog.setMessage("Saving data. \nPlease wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Observable.defer(new Func0<Observable<Boolean>>() {
        @Override
        public Observable<Boolean> call() {


            //refreshing the required appointmentId, childId incase they were updated in the database after receiving a push notification update
            //it was noted that in some cases the values in the database was updated after receiving a push notification from the server before the uses clicked save vaccination
            // so it is relevant to reattain appointmentId, childId  to capture such scenarios.
            getChildId();
            Log.d("delay","saving vaccines for childId = "+childId);
            appointment_id = (dbh.getVaccinationAppointmentForList(childId,appointment.getScheduledDate()).get(0)).getId();
            //Administering the vaccines
            administerVaccineSaveButtonClicked();
            if (SavedState) {
                BackboneApplication application = (BackboneApplication) AdministerVaccineFragment.this.getActivity().getApplication();
                try {
                    application.broadcastChildUpdates(Integer.parseInt(childId));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    application.broadcastChildUpdatesWithBarcodeId(barcode);
                }
                for (AdministerVaccinesModel a : arrayListAdminVacc) {
                    try {
                        DatabaseHandler db = application.getDatabaseInstance();
                        int status = application.updateVaccinationEventOnServer(a.getUpdateURL());

                        String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
                        try {
                            dateTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //Register Audit
                        application.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateTodayTimestamp,
                                application.getLOGGED_IN_USER_ID(), 7);

                        if (outreachChecked) {
                            int i = application.updateVaccinationAppOutreach(barcode, a.getDose_id());
                        }


                        if (a.getStatus().equalsIgnoreCase("true") && !a.getVaccination_lot().toLowerCase().contains("no lot")) {
                            Log.d(TAG,"deducting stock");
                            Cursor cursor = db.getReadableDatabase().rawQuery("SELECT balance FROM health_facility_balance WHERE lot_id=?", new String[]{a.getVaccination_lot()});
                            if (cursor != null && cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                int bal = cursor.getInt(cursor.getColumnIndex("balance"));
                                bal = bal - 1;
                                Log.d("Balance being set: ", bal + "");
                                ContentValues cv = new ContentValues();
                                cv.put(SQLHandler.HealthFacilityBalanceColumns.BALANCE, bal);
                                db.updateStockBalance(cv, a.getVaccination_lot());
                            }
                        }else{
                            Log.d(TAG,"not deducting stock");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                try {
                    application.broadcastChildUpdates(Integer.parseInt(childId));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    application.broadcastChildUpdatesWithBarcodeId(barcode);
                }
            }

            return Observable.just(true);
            }
            }).subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
        @Override
        public void onCompleted() {
                Log.d(TAG, "onCompleted()");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            savingInProgress = false;
            final AlertDialog ad2 = new AlertDialog.Builder((Activity) getActivity()).create();
            ad2.setTitle("Saved");
            ad2.setMessage(getString(R.string.changes_saved));
            ad2.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ad2.dismiss();
                    Log.d("day6", "poping a fragment");
                    FragmentManager manager = getFragmentManager();
                    manager.popBackStack("AdministerVaccineFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }
            });
            try {
                ad2.show();
                ((ChildDetailsActivity) getActivity()).updateadapters();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
                Log.e(TAG, "onError()", e);
        }

        @Override
        public void onNext(Boolean string) {
                Log.d(TAG, "onNext(" + string + ")");
        }});

    }

    public void createDialogWithMessages(String dialogueMessage, final boolean isNested){
        // set title
        alertDialogBuilder = new AlertDialog.Builder(AdministerVaccineFragment.this.getActivity());
        alertDialogBuilder.setTitle("Warning");
        // set dialog message
        alertDialogBuilder
                .setMessage(dialogueMessage)
                .setCancelable(false)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if (isNested) {
                                    String theMessage = "The selected vaccination date is before due date. Are you sure you want to set this date?";
                                    createDialogWithMessages(theMessage, false);
                                } else {
                                    saveVaccines();
                                }
                            }
                        }
                )
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        savingInProgress = false;
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public String calculateDateDiff(int daysDiff,AdministerVaccinesModel a) {
        return "/Date(" + (a.getTime2().getTime() + ((long)daysDiff*(long)86400000)) + "-0500)/";
    }

    private void administerVaccineSaveButtonClicked(){
        BackboneApplication app = (BackboneApplication) this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();

        for (AdministerVaccinesModel a : arrayListAdminVacc) {
            if (a.getStatus().equalsIgnoreCase("true") && a.getVaccine_lot_list().get(a.getVaccination_lot_pos()).equalsIgnoreCase("-----")) {
                final AlertDialog ad22 = new AlertDialog.Builder(this.getActivity()).create();
                ad22.setTitle(getString(R.string.not_saved));
                ad22.setMessage("Please select vaccine lot");
                ad22.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22.dismiss();
                    }
                });
                ad22.show();
                return;
            }

            if (a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() == 0) {
                final AlertDialog ad22 = new AlertDialog.Builder(this.getActivity()).create();
                ad22.setTitle("Not Saved");
                ad22.setMessage("Please select a reason for not vaccinating child!");
                ad22.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad22.dismiss();
                    }
                });
                ad22.show();
                return;
            }

            Log.d("VacLotID", a.getVaccine_lot_map().get(a.getVaccine_lot_list().get(a.getVaccination_lot_pos())).toString());
            //Only the rows where done is checked or a reason is selected should be saved and sent to server
            if (!(a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() == 0)) {
                ContentValues updateRow = new ContentValues();
                updateRow.put(SQLHandler.SyncColumns.UPDATED, 1);
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_STATUS, a.getStatus());
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINE_LOT_ID, a.getVaccine_lot_map().get(a.getVaccine_lot_list().get(a.getVaccination_lot_pos())).toString());
                updateRow.put(SQLHandler.VaccinationEventColumns.HEALTH_FACILITY_ID, app.getLOGGED_IN_USER_HF_ID());
                if (!(etNotes.getText().toString().equalsIgnoreCase(""))) {
                    updateRow.put(SQLHandler.VaccinationEventColumns.NOTES, etNotes.getText().toString());
                }
                Log.d("TimeSentToUpdateDatabase", "/Date(" + a.getTime2().getTime() + "+0000)/");
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_DATE, "/Date(" + a.getTime2().getTime() + "+0000)/");
                if (!(a.getNon_vac_reason().equalsIgnoreCase(""))) {
                    updateRow.put(SQLHandler.VaccinationEventColumns.NONVACCINATION_REASON_ID, a.getNon_vac_reason());
                }

                app.setUpdateURL(a, etNotes.getText().toString(), barcode);
                app.setAppointmentUpdateURL(a, appointment_id, cbOutreach);
                SavedState = true;
                Log.d("Saving appointment id" + appointment_id + " status", "dose id is: " + a.getDose_id());
                mydb.updateAdministerVaccineDoneStatus(updateRow, appointment_id, a.getDose_id());

                if (a.getDose_Number_Parsed() > 1 && (!a.getStatus().equalsIgnoreCase("false") ||
                        (a.getStatus().equalsIgnoreCase("false") && a.getNon_vac_reason_pos() != 0 && !a.isKeep_child_due()))) {
                    Cursor crsCurrentAge = mydb.getReadableDatabase()
                            .rawQuery("Select DAYS from AGE_DEFINITIONS where ID in (select AGE_DEFINITON_ID from DOSE where ID=? )"
                                    , new String[]{ a.getDose_id()});
                    int currAgeDef = 0;
                    if(crsCurrentAge.moveToFirst()){
                        currAgeDef = Integer.parseInt(crsCurrentAge.getString(0));
                    }
                    crsCurrentAge.close();

                    Cursor crs = null;
                    crs = mydb.getReadableDatabase()
                            .rawQuery("SELECT vaccination_event.ID AS VACID, vaccination_event.DOSE_ID as DOSE_ID " +
                                            " FROM vaccination_event JOIN dose ON vaccination_event.DOSE_ID = dose.ID " +
                                            " WHERE dose.SCHEDULED_VACCINATION_ID = ? AND DOSE_NUMBER=? AND CHILD_ID=?"
                                    , new String[]{a.getScheduled_Vaccination_Id(), String.valueOf(a.getDose_Number_Parsed()), childId});

                    if (crs.moveToFirst()) {
                        do {
                            Log.d("Query", " is Working");
                            ContentValues cv = new ContentValues();
                            cv.put("IS_ACTIVE", "true");
                            Cursor crsNextAge = mydb.getReadableDatabase()
                                    .rawQuery("Select DAYS from age_definitions where ID in (select AGE_DEFINITON_ID from dose where ID=? )"
                                            , new String[]{ crs.getString(crs.getColumnIndex("DOSE_ID"))});
                            int nextAgeDef = 0;
                            if(crsNextAge.moveToFirst()){
                                nextAgeDef = Integer.parseInt(crsNextAge.getString(0));
                            }
                            crsNextAge.close();
                            int dayDiff = nextAgeDef - currAgeDef;
                            cv.put("SCHEDULED_DATE", calculateDateDiff(dayDiff, a));
                            //cv.put("SCHEDULED_DATE", a.calculateDateDiff());
                            String vaccination_event_id = crs.getString(crs.getColumnIndex("VACID"));
                            mydb.updateAdministerVaccineSchedule(cv, vaccination_event_id);
                        } while (crs.moveToNext());
                    }

                    crs.close();
                }
            }

        }

        saveChildSupplements();

    }

    private void saveChildSupplements() {
        boolean vitA = false, mebendezolr = false;
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.alert_empty_fields))
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        ((AlertDialog) dialog).dismiss();
//                    }
//                });

        vitA = vitACheckbox.isEnabled() && vitACheckbox.isChecked();
        mebendezolr = mabendazolCheckbox.isEnabled() && mabendazolCheckbox.isChecked();
        if (vitA || mebendezolr) {
            final long insertedChildSupplementsRowId = dbh.inserTodaySupplements(childId, vitA, mebendezolr, app.getLOGGED_IN_USER_ID());
            if (insertedChildSupplementsRowId > 0) {
//                alertDialogBuilder.setMessage(getString(R.string.supplement_data_saved));
//                alertDialogBuilder.show();

                // tentojme te bejme save te dhenat ne server
                thread = new Thread() {
                    @Override
                    public void run() {
                        String url = prepareUrlChildSupplements().toString();
                        long newInserterdTodaySupplementsId = app.insertChildSupplementidChild(url);
                        if (newInserterdTodaySupplementsId > 0) {
                            dbh.updateChildSupplementsNewid(insertedChildSupplementsRowId, newInserterdTodaySupplementsId);
                        } else {
                            dbh.addPost(url, -1);
                            Log.d("Save Edited Child", "Error while saving edited child " + childId);
                        }
                    }
                };
                thread.start();
            }
        } else {
//            alertDialogBuilder.setMessage(getString(R.string.select_one_supplement));
//            alertDialogBuilder.show();
        }
    }

    private StringBuilder prepareUrlChildSupplements() {
        boolean vitA, mebendezolr;
        vitA = vitACheckbox.isEnabled() && vitACheckbox.isChecked();
        mebendezolr = mabendazolCheckbox.isEnabled() && mabendazolCheckbox.isChecked();

        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.CHILD_SUPPLEMENTS_SVC).append(BackboneApplication.CHILD_SUPPLEMENTS_INSERT);
        webServiceUrl.append("?barcode=" + barcode);
        if (vitA)
            webServiceUrl.append("&vita=true");
        else
            webServiceUrl.append("&vita=false");
        if (mebendezolr)
            webServiceUrl.append("&mebendezol=true");
        else
            webServiceUrl.append("&mebendezol=false");

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        webServiceUrl.append("&date=" + format.format(date));
        webServiceUrl.append("&modifiedBy=" + app.getLOGGED_IN_USER_ID());
        return webServiceUrl;
    }

    @Override
    public String getTagText() {
        return null;
    }

    @Override
    public boolean onBackPressed() {

        if (app.saveNeeded){
            LayoutInflater li = LayoutInflater.from(AdministerVaccineFragment.this.getActivity());
            View promptsView = li.inflate(R.layout.custom_alert_dialogue, null);

            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(AdministerVaccineFragment.this.getActivity());
            alertDialogBuilder.setView(promptsView);

            TextView message = (TextView) promptsView.findViewById(R.id.dialogMessage);
            message.setText("Are you sure you want to Leave Without Saving?");

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FragmentManager fm = getFragmentManager();
                                    int backStackEntryCount = fm.getBackStackEntryCount();
                                    if (backStackEntryCount > 0){
                                        fm.popBackStack();
                                    }
                                    else{
                                        ChildAppointmentsListFragment appointmentsListFragment = new ChildAppointmentsListFragment();
                                        Bundle bundle=new Bundle();
                                        bundle.putString("child_id", childId);
                                        bundle.putString("barcode", barcode);
                                        bundle.putString("birthdate", birthdate);
                                        appointmentsListFragment.setArguments(bundle);

                                        app.setCurrentFragment(app.APPOINTMENT_LIST_FRAGMENT);
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.vacc_fragment_frame, appointmentsListFragment);
                                        ft.addToBackStack("fragmentVaccineList");
                                        ft.commit();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

        return true;
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