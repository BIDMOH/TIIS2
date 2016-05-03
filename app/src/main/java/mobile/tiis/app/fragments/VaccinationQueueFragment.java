package mobile.tiis.app.fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.app.ChildDetailsActivity;
import mobile.tiis.app.CustomViews.ButteryProgressBar;
import mobile.tiis.app.CustomViews.NestedListView;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.AdapterVaccineNameQuantity;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.adapters.VaccinationQueueListAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.GIISContract;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.helpers.Utils;
import mobile.tiis.app.util.Constants;
import mobile.tiis.app.util.ViewAppointmentRow;

/**
 * Created by issymac on 16/12/15.
 */
public class VaccinationQueueFragment extends android.support.v4.app.DialogFragment implements
        android.view.View.OnClickListener{

    private List<String> ages;

    private NestedListView lvVaccQList;

    public VaccinationQueueListAdapter adapter;

    private MaterialSpinner agesSpinner;

    private ArrayList<ViewAppointmentRow> var;

    private Child currentChild;

    private String hf_id, barcode, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    RelativeLayout emptyStateLayout;

    Button vaccQuantity;

    BackboneApplication app;

    DatabaseHandler this_database;

    ArrayList<VQAgeDefinitions> ageDef;

    MaterialEditText txtChildBarcode;

    Button checkinButton;

    private Thread thread;

    ButteryProgressBar pbar;

    private Button vaccDoseQuantity;

    public String selectedAgeDefinition = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_vaccination_queue, null);
        setUpView(root);

        app = (BackboneApplication) this.getActivity().getApplication();
        this_database = app.getDatabaseInstance();
        ageDef = getAllAgeDeffinitions();

        ArrayList<String> ageDefStr = new ArrayList<>();
        ageDefStr.add("--------");
        for(VQAgeDefinitions vqad : ageDef){
            ageDefStr.add(vqad.getName());
        }

        checkinButton.setOnClickListener(this);

        var = compileVaccinationQueueTable("");

        if(!(var.size() > 0)){
            lvVaccQList.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }else {
            lvVaccQList.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }

        View lvHeader = inflater.inflate(R.layout.monthly_plan_list_item_header, null);
        TextView nameTitle = (TextView) lvHeader.findViewById(R.id.name_title);
        nameTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView vaccineTitle = (TextView) lvHeader.findViewById(R.id.vaccine_title);
        vaccineTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView ageTitle = (TextView) lvHeader.findViewById(R.id.age_title);
        ageTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView dateTitle = (TextView) lvHeader.findViewById(R.id.date_title);
        dateTitle.setTypeface(BackboneActivity.Rosario_Regular);

        vaccDoseQuantity=(Button)root.findViewById(R.id.vacc_dose_quantity);

        adapter = new VaccinationQueueListAdapter(VaccinationQueueFragment.this.getActivity(), var);
        setListViewHeightBasedOnChildren(lvVaccQList);
        lvVaccQList.addHeaderView(lvHeader);
        lvVaccQList.setAdapter(adapter);

        SingleTextViewAdapter spinnerAdapter = new SingleTextViewAdapter(VaccinationQueueFragment.this.getActivity(),R.layout.single_text_spinner_item_drop_down,ageDefStr);
        agesSpinner.setAdapter(spinnerAdapter);

        vaccQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder keyBuilder = new AlertDialog.Builder(VaccinationQueueFragment.this.getActivity());
                keyBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                keyBuilder.setView(View.inflate(VaccinationQueueFragment.this.getActivity(), R.layout.vaccination_quantity_custom_dialog, null));
                AlertDialog dialog = keyBuilder.create();
                dialog.show();
            }
        });

        lvVaccQList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    //Header Clicked Do Nothing
                } else {
                    Cursor cursor = null;
                    cursor = this_database.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.ID + "=?",
                            new String[]{String.valueOf(var.get(i - 1).getChild_id())});

                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        //parsing the data in a value object from the cursor
                        currentChild = getChildFromCursror(cursor);
                        if (currentChild.getBarcodeID() == null || currentChild.getBarcodeID().isEmpty()) {
                            Toast.makeText(VaccinationQueueFragment.this.getActivity(),
                                    getString(R.string.empty_barcode), Toast.LENGTH_LONG).show();
                        }
                        barcode = currentChild.getBarcodeID();
                    }

                    Intent childDetailsActivity = new Intent(VaccinationQueueFragment.this.getActivity(), ChildDetailsActivity.class);
                    childDetailsActivity.putExtra("barcode", barcode);
                    childDetailsActivity.putExtra("current", 2);
                    VaccinationQueueFragment.this.getActivity().startActivity(childDetailsActivity);

                }
            }
        });

        vaccDoseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder keyBuilder = new AlertDialog.Builder(getActivity());
                keyBuilder
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                View dialogLayout = View.inflate(getActivity(), R.layout.vaccination_quantity_custom_dialog, null);
                ListView lvNameQuantity = (ListView)dialogLayout.findViewById(R.id.lv_result);
                ArrayList<FragmentVaccineNameQuantity.VacineNameQuantity> list = this_database.getQuantityOfVaccinesNeededVaccinationQueue(selectedAgeDefinition);
                Context ctx = getActivity().getApplicationContext();
                try {
                    AdapterVaccineNameQuantity adapter = new AdapterVaccineNameQuantity(ctx, R.layout.item_vaccine_name_quantity, list);
                    lvNameQuantity.setAdapter(adapter);

                    keyBuilder.setView(dialogLayout);


                    AlertDialog dialog = keyBuilder.create();
                    dialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        agesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                pbar.setVisibility(View.VISIBLE);
                if(position > 0) {
                    selectedAgeDefinition = ageDef.get(position - 1).getName();
                    var = compileVaccinationQueueTable(ageDef.get(position - 1).getId());
                }else{
                    selectedAgeDefinition = "";
                    var = compileVaccinationQueueTable("");
                }
                lvVaccQList.setAdapter(null);
                adapter = new VaccinationQueueListAdapter(VaccinationQueueFragment.this.getActivity(), var);
                lvVaccQList.setAdapter(adapter);
                pbar.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return root;

    }

    public void updateList(){
        try {
            Log.d("day5", "get the new data to display");
            var = compileVaccinationQueueTable("");
            adapter.updateReceiptsList(var);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUpView(View v){
        lvVaccQList     = (NestedListView) v.findViewById(R.id.lv_vacc_queue);
        agesSpinner     = (MaterialSpinner) v.findViewById(R.id.vacc_age_spinner);
        vaccQuantity    = (Button) v.findViewById(R.id.vac_q_title);
        txtChildBarcode = (MaterialEditText) v.findViewById(R.id.met_vacc_queue_barcode);

        emptyStateLayout= (RelativeLayout) v.findViewById(R.id.empty_state_layout);
        emptyStateLayout.setVisibility(View.GONE);

        checkinButton   = (Button) v.findViewById(R.id.checkin_child_btn);

        pbar            = (ButteryProgressBar) v.findViewById(R.id.check_in_pbar);
        pbar            .setVisibility(View.GONE);

    }

    //send to camera scan
    public void onClick(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_camera_button) {
//            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//            if (origine != null) {
//                if (origine.equalsIgnoreCase(ACTIVITY_CHECK_IN)) {
//                    BackboneApplication app = (BackboneApplication) getApplication();
//                    app.setCurrentActivity(ACTIVITY_CHECK_IN);
//                }
//            }
            //TODO modified by coze
//            scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            scanIntegrator.initiateScan();
        }

        if (v.getId() == R.id.checkin_child_btn) {
            if (Utils.isStringBlank(txtChildBarcode.getText().toString())) {
                txtChildBarcode.setError("Cannot Be Empty");
                txtChildBarcode.setErrorColor(Color.RED);
            } else {
                String contents = txtChildBarcode.getText().toString();
                pbar.setVisibility(View.VISIBLE);
                checkinChild(contents);
            }
        }
    }

    public void checkinChild(final String barcode){

        thread = new Thread() {
            @Override
            public void run() {

                synchronized (this) {
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());

                    try {
                        dateNow = URLEncoder.encode(dateNow, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    DatabaseHandler db = app.getDatabaseInstance();
                    // check if child is in DB , if not than get child data from server
                    if (!db.isChildInDB(barcode)) {
                        app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());

                        app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);

                        int parseResult = app.parseChildCollectorSearchByBarcode(barcode);
                        if (parseResult == 2) {
                            VaccinationQueueFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.not_found), getString(R.string.barcode_does_not_exist));
                                    pbar.setVisibility(View.GONE);
                                    txtChildBarcode.setText(getString(R.string.not_found));
                                    txtChildBarcode.setText("");
                                }
                            });
                            return;
                        } else if (parseResult == 3) {
                            VaccinationQueueFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    delayVisibilityGoneChange(2000, R.drawable.on_check_in_failed, ivCheckIn, overlay, getString(R.string.msg_error), getString(R.string.error_retrieving_child_data));
                                    pbar.setVisibility(View.GONE);
                                    txtChildBarcode.setText(getString(R.string.msg_error));
                                    txtChildBarcode.setText("");
                                }
                            });
                            return;
                        }

                        parseHFIDWhenNotInDb(db, app);

                    }
                    // this should never be null in this part of the app according to the lines of code above
                    String childId = db.getChildIdByBarcode(barcode);
                    if (db.isChildToBeAddedInVaccinationQueue(childId)) {

                        ContentValues cv = new ContentValues();
                        cv.put(SQLHandler.VaccinationQueueColumns.CHILD_ID, childId);
                        cv.put(SQLHandler.VaccinationQueueColumns.DATE, dateNow);

                        if (db.addChildToVaccinationQueue(cv) > -1) {
                            app.updateVaccinationQueue(barcode, app.getLOGGED_IN_USER_HF_ID(), dateNow, app.getLOGGED_IN_USER_ID());
                        }
                    } else {
                        VaccinationQueueFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VaccinationQueueFragment.this.getActivity(), "Child was not to be added to queue", Toast.LENGTH_LONG).show();
                                pbar.setVisibility(View.GONE);
                                txtChildBarcode.setText("");
                            }
                        });
                    }

                    app.registerAudit(BackboneApplication.CHILD_AUDIT, barcode, dateNow, app.getLOGGED_IN_USER_ID(), BackboneApplication.ACTION_CHECKIN);

                    VaccinationQueueFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VaccinationQueueFragment.this.getActivity(), "Child checkin finished", Toast.LENGTH_LONG).show();
                            var = compileVaccinationQueueTable("");
                            lvVaccQList.setAdapter(null);
                            adapter = new VaccinationQueueListAdapter(VaccinationQueueFragment.this.getActivity(), var);
                            lvVaccQList.setAdapter(adapter);
                            pbar.setVisibility(View.GONE);
                            txtChildBarcode.setText("");
                        }
                    });

                }
            }
        };

        thread.start();

    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
        }
    }

    private ArrayList<ViewAppointmentRow> compileVaccinationQueueTable(String ageId) {

        Cursor cursor;
        ArrayList<ViewAppointmentRow> var = new ArrayList<ViewAppointmentRow>();
        String result="";
        child_id="";

        //Getting child_id
        child_id = this_database.getAllChilcIdInVaccinationQueue();
        if(child_id!=null) {
            String SQLVaccinationQueue =
                    "SELECT v.APPOINTMENT_ID, v.CHILD_ID," +
                            "(SELECT GROUP_CONCAT(dose.FULLNAME)" +
                            " FROM vaccination_event INNER JOIN dose" +
                            " ON vaccination_event.DOSE_ID = dose.ID left join age_definitions on dose.TO_AGE_DEFINITON_ID = age_definitions.ID" +
                            " WHERE CHILD_ID in ("+child_id+")" +
                            " AND v.APPOINTMENT_ID=vaccination_event.APPOINTMENT_ID" +
                            " AND datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+"+ Constants.EligibleForVaccinationVal+" days')" +
                            " AND vaccination_event.IS_ACTIVE='true'" +
                            " AND vaccination_event.VACCINATION_STATUS='false'" +
                            " AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) AND (DAYS IS NULL or\n" +
                            "(datetime(substr(vaccination_event.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || DAYS || ' days')) ))" +
                            " AS VACCINES, " +
                            "a.NAME AS SCHEDULE, " +
                            "v.SCHEDULED_DATE  " +
                            "FROM vaccination_event v INNER JOIN dose " +
                            "ON v.DOSE_ID = dose.ID INNER JOIN age_definitions a" +
                            " ON dose.AGE_DEFINITON_ID=a.ID" +
                            " WHERE v.CHILD_ID in ("+child_id+")" +
                            " AND datetime(substr(v.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+"+ Constants.EligibleForVaccinationVal+" days')" +
                            " AND v.IS_ACTIVE='true'" +
                            " AND v.VACCINATION_STATUS='false'" +
                            " AND (v.NONVACCINATION_REASON_ID=0  OR v.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true')) " +
                            "AND ( (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) IS NULL \n" +
                            " OR (datetime(substr(v.SCHEDULED_DATE,7,10),'unixepoch') > datetime('now','-' || (Select DAYS from age_definitions WHERE ID = dose.TO_AGE_DEFINITON_ID ) || ' days' )) )" +
                            ((!ageId.equals(""))?" AND a.ID = '"+ ageId +"'":"") +
                            " GROUP BY v.APPOINTMENT_ID, v.SCHEDULED_DATE, a.NAME " +
                            " ORDER BY v.SCHEDULED_DATE";

            Log.d("dayTwo", SQLVaccinationQueue);
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
        }
        else{

            //Toast there are no children
        }

        //Show loading dialog
        ProgressDialog progressDialog =  new ProgressDialog(this.getActivity(), 0);
        progressDialog.setMessage("Loading Children from database...");
        progressDialog.show();

        progressDialog.dismiss();
        return var;

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
        Cursor cursor1 = this_database.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = this_database.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }

        parsedChild.setDomicile(villagestr);
        parsedChild.setHealthcenterId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
        try {
            Cursor cursor3 = this_database.getReadableDatabase().rawQuery("SELECT * FROM health_facility WHERE ID=?", new String[]{parsedChild.getHealthcenterId()});
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                hfstr = cursor3.getString(cursor3.getColumnIndex(SQLHandler.HealthFacilityColumns.NAME));
            }
        }catch (Exception e){
            hfstr = "";
        }
        parsedChild.setHealthcenter(hfstr);

        parsedChild.setStatusId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.STATUS_ID)));
        Cursor cursor4 = this_database.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

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

}