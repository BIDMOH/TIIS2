package mobile.giis.app.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mobile.giis.app.CustomViews.NestedListView;
import mobile.giis.app.R;
import mobile.giis.app.SubClassed.BackHandledFragment;
import mobile.giis.app.adapters.AdapterAdministerVaccines;
import mobile.giis.app.adapters.VaccineDoseListAdapter;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.AdministerVaccinesModel;

/**
 * Created by issymac on 27/01/16.
 */
public class AdministerVaccineFragment extends BackHandledFragment implements View.OnClickListener{

    private String appointment_id, birthdate, barcode, childId;
    private BackboneApplication app;
    private DatabaseHandler dbh;
    private Boolean SavedState = false;
    private boolean outreachChecked = false;
    private boolean outreach = false;
    private Thread thread;
    boolean starter_set = false;
    long daysDiff;
    int counter = 0,DateDiffDialog = 0;
    ArrayList<String> dosekeeper;
    ArrayList<AdministerVaccinesModel> arrayListAdminVacc;
    Date newest_date;
    NestedListView vaccineDosesList;
    CheckBox    vitACheckbox, mabendazolCheckbox, cbOutreach;
    TextView vitADate, mabendazolDate;
    Button saveButton;
    MaterialEditText etNotes;
    FragmentStackManager fm;

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
        c1.set(Calendar.HOUR,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.HOUR,0);
        c2.set(Calendar.MINUTE,0);
        c2.set(Calendar.SECOND,0);
        c2.set(Calendar.MILLISECOND,0);

        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return Math.abs(difference);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_vaccinate, null);
        setUpView(root);

        app = (BackboneApplication) this.getActivity().getApplication();
        dbh = app.getDatabaseInstance();
        fm  = new FragmentStackManager(this.getActivity());

        app.saveNeeded = true;

        appointment_id  = getArguments().getString("appointment_id");
        birthdate       = getArguments().getString("birthdate");
        barcode         = getArguments().getString("barcode");

        Log.d("EBENSEARCH", "Birth date is : "+birthdate);

        try {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            String today = ft.format(dNow);
            Date date1 = ft.parse(birthdate);
            Date date2 = ft.parse(today);
            int month = getMonthsDifference(date1, date2);
            daysDiff = getDaysDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date todayD = new Date();
        SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");
        vitADate.setText(ftD.format(todayD));
        mabendazolDate.setText(ftD.format(todayD));

        dosekeeper = dbh.getDosesForAppointmentID(appointment_id);
        arrayListAdminVacc = new ArrayList<AdministerVaccinesModel>();
        newest_date = new Date();


        for (String dose : dosekeeper) {
            final AdministerVaccinesModel adminVacc = dbh.getPartOneAdminVaccModel(starter_set, appointment_id, dose);
            starter_set = true;

            dbh.getPartTwoAdminVacc(adminVacc, daysDiff, DateDiffDialog);
            
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            adminVacc.setTime(ft.format(newest_date));
            adminVacc.setTime2(newest_date);
            //rowObjects.setInput(ft.format(newest_date));
            //rowObjects.setDate(vaccination_date_col);

            arrayListAdminVacc.add(adminVacc);
        }


        setListViewHeightBasedOnChildren(vaccineDosesList);
        VaccineDoseListAdapter adapterList = new VaccineDoseListAdapter(this.getActivity(),R.layout.item_listview_admin_vacc,arrayListAdminVacc,birthdate,1);
        vaccineDosesList.setAdapter(adapterList);

        DateDiffDialog();

        getChildId();

        if (dbh.isChildSupplementedVitAToday(childId)) {
            vitACheckbox.setChecked(true);
            vitACheckbox.setEnabled(false);
        }
        if (dbh.isChildSupplementedMebendezolrToday(childId)) {
            mabendazolCheckbox.setChecked(true);
            mabendazolCheckbox.setEnabled(false);
        }

        return root;
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

    public void setUpView(View v){
        vaccineDosesList        = (NestedListView) v.findViewById(R.id.lv_dose_list);
        vitACheckbox            = (CheckBox) v.findViewById(R.id.vit_a_check);
        mabendazolCheckbox      = (CheckBox) v.findViewById(R.id.mabendazol_check);
        cbOutreach              = (CheckBox) v.findViewById(R.id.cb_outreach);
        vitADate                = (TextView) v.findViewById(R.id.vit_a_date);
        mabendazolDate          = (TextView) v.findViewById(R.id.mabendazol_date);
        etNotes                 = (MaterialEditText) v.findViewById(R.id.notes);
        saveButton              = (Button) v.findViewById(R.id.addminister_vaccine_save_button);
        saveButton              .setOnClickListener(this);
    }

    private void getChildId() {
        Cursor getChildIdCursor = dbh.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                new String[]{String.valueOf(barcode)});
        if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
            getChildIdCursor.moveToFirst();
            childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            getChildIdCursor.close();
        } else {
//          toastMessage(getString(R.string.empty_child_id));
            getChildIdCursor.close();
            //TODO: Call the fragmentStackManager to replace the current fragment (if it was the activity its supposed to be finished)
//            finish();
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addminister_vaccine_save_button:
                administerVaccineSaveButtonClicked();
                if (SavedState) {
                    for (AdministerVaccinesModel a : arrayListAdminVacc) {
                        Log.d("lastupdates", "updating the data to server I think");
                        updateAdministerVaccine task = new updateAdministerVaccine();
                        task.execute(a.getUpdateURL(), barcode);

                        updateAppointmentOutreach task2 = new updateAppointmentOutreach();
                        task2.execute();

                        updateBalance balance = new updateBalance();
                        balance.execute(a);
                    }
                }
                app.saveNeeded = false;
                break;
        }
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
                Log.d("Time sent to update database", "/Date(" + a.getTime2().getTime() + "-0500)/");
                updateRow.put(SQLHandler.VaccinationEventColumns.VACCINATION_DATE, "/Date(" + a.getTime2().getTime() + "-0500)/");
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

        final AlertDialog ad2 = new AlertDialog.Builder(this.getActivity()).create();
        ad2.setTitle("Saved");
        ad2.setMessage(getString(R.string.changes_saved));
        ad2.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad2.dismiss();
            }
        });
        ad2.show();

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
                                    FragmentManager fm = AdministerVaccineFragment.this.getActivity().getSupportFragmentManager();
                                    fm.popBackStack();
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

    private class updateAdministerVaccine extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) AdministerVaccineFragment.this.getActivity().getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            int status = application.updateVaccinationEventOnServer(params[0]);
            Log.d("The status", status + "");
            String dateTodayTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
            try {
                dateTodayTimestamp = URLEncoder.encode(dateTodayTimestamp, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Register Audit
            application.registerAudit(BackboneApplication.CHILD_AUDIT, params[1], dateTodayTimestamp,
                    application.getLOGGED_IN_USER_ID(), 7);

            //a.syncVaccines();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class updateAppointmentOutreach extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            outreachChecked = false;
            BackboneApplication application = (BackboneApplication) AdministerVaccineFragment.this.getActivity().getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            AdministerVaccineFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 if (cbOutreach.isChecked()){
                     outreachChecked = true;
                 }
                }
            });
            if(outreachChecked && arrayListAdminVacc!=null && arrayListAdminVacc.size()>0) {
                application.updateVaccinationAppOutreach(barcode, arrayListAdminVacc.get(0).getDose_id());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class updateBalance extends AsyncTask<AdministerVaccinesModel, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(AdministerVaccinesModel... params) {
            BackboneApplication application = (BackboneApplication) AdministerVaccineFragment.this.getActivity().getApplication();
            DatabaseHandler db = application.getDatabaseInstance();
            for (AdministerVaccinesModel item : params) {
                Log.d("Updating balance", "");
                if (item.getStatus().equalsIgnoreCase("true")) {
                    Log.d("Starting update protocol", "");
                    Cursor cursor = db.getReadableDatabase().rawQuery("SELECT balance FROM health_facility_balance WHERE lot_id=?", new String[]{item.getVaccination_lot()});
                    //Cursor cursor = db.getReadableDatabase().rawQuery("UPDATE health_facility_balance SET balance = balance - 1 WHERE lot_id=?", new String[]{a.getVaccination_lot()});
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int bal = cursor.getInt(cursor.getColumnIndex("balance"));
                        Log.d("Balance found on database: ", bal + "");
                        bal = bal - 1;
                        Log.d("Balance being set: ", bal + "");
                        ContentValues cv = new ContentValues();
                        cv.put(SQLHandler.HealthFacilityBalanceColumns.BALANCE, bal);
                        db.updateStockBalance(cv, item.getVaccination_lot());
                        //cursor = db.getReadableDatabase().rawQuery("UPDATE health_facility_balance SET balance=? WHERE lot_id=?", new String[]{String.valueOf(bal), a.getVaccination_lot()});
                    }
                    //database.updateStockBalance(a.getDose_id());
                }
            }
            //a.syncVaccines();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

}
