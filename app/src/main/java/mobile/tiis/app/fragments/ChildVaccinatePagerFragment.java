package mobile.tiis.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobile.tiis.app.CustomViews.NestedListView;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.ChildAppointmentListAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.AdministerVaccinesModel;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.mObjects.RowCollector;
import mobile.tiis.app.util.ViewAppointmentRow;

/**
 *  Created by issymac on 27/01/16.
 */
public class ChildVaccinatePagerFragment extends Fragment {

    private BackboneApplication app;

    private DatabaseHandler dbh;

    private Child currentChild;

    private ArrayList<ViewAppointmentRow> var;

    private static boolean barcodeIsEmpty = false;

    private static final String CHILD_OBJECT = "child";

    private ArrayList<RowCollector> rowCollectorContainer;

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    final String VACC_ID_LOG = "Vaccination Id";

    final String VACC_NAME_LOG = "Vaccination Name";

    final String VACC__ITEM_ID_LOG = "Vaccination Item Id";


    private RelativeLayout noBarcode;

    private RelativeLayout frameLayout;

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

    public static Date dateParser(String date_str) {
        Date date = null;
        Pattern pattern = Pattern.compile("\\((.*?)-");
        Pattern pattern_plus = Pattern.compile("\\((.*?)\\+");
        Matcher matcher = pattern.matcher(date_str);
        Matcher matcher_plus = pattern_plus.matcher(date_str);
        if (matcher.find()) {
            date = new Date(Long.parseLong(matcher.group(1)));
        } else if (matcher_plus.find()) {
            date = new Date(Long.parseLong(matcher_plus.group(1)));
        } else {
            date = new Date();
        }
        return date;
    }

    public static ChildVaccinatePagerFragment newInstance(Child currentChild, String appointmentId) {
        ChildVaccinatePagerFragment f = new ChildVaccinatePagerFragment();
        Bundle b                    = new Bundle();
        b                           .putSerializable(CHILD_OBJECT, currentChild);
        b                           .putString("appointmentId", appointmentId);
        f                           .setArguments(b);
        return f;
    }

    private NestedListView vaccineDosesListView;

    private FrameLayout vaccinateFrame;

    private ArrayList<AdministerVaccinesModel> arrayListAdminVacc;

    private FragmentStackManager fm;

    private String appointmentID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentChild     = (Child) getArguments().getSerializable(CHILD_OBJECT);

        if (getArguments().getString("appointmentId") != null){
            appointmentID   = getArguments().getString("appointmentId");
        }


        app = (BackboneApplication) ChildVaccinatePagerFragment.this.getActivity().getApplication();
        dbh = app.getDatabaseInstance();

        if (currentChild.getBarcodeID().isEmpty() || currentChild.getBarcodeID() == null || currentChild.getBarcodeID().equals("")){
            barcodeIsEmpty = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v;
        v = (ViewGroup) inflater.inflate(R.layout.child_faccinate_pager_fragment, null);
        setUpView(v);

        if (currentChild.getBarcodeID().isEmpty() || currentChild.getBarcodeID().equals("")){
            noBarcode.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        }else{
            noBarcode.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        }

        if (currentChild.getBarcodeID() == null || currentChild.getBarcodeID().isEmpty()) {
            Toast.makeText(ChildVaccinatePagerFragment.this.getActivity(), getString(R.string.empty_barcode), Toast.LENGTH_SHORT).show();
        }

        if (!appointmentID.isEmpty()){
            AdministerVaccineFragment administerVaccineFragment = new AdministerVaccineFragment();
            Bundle bundle = new Bundle();
            bundle.putString("appointment_id", appointmentID);
            bundle.putString("birthdate", currentChild.getBirthdate());



            bundle.putString("barcode", currentChild.getBarcodeID());
            Log.d("appointmentID", "Appointment Id is : " + appointmentID);
            administerVaccineFragment.setArguments(bundle);
            app.setCurrentFragment(app.VACCINATE_CHILD_FRAGMENT);

//            fm.addFragment(administerVaccineFragment, R.id.vacc_fragment_frame, true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, false);

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.vacc_fragment_frame, administerVaccineFragment);
            ft.addToBackStack("AdministerVaccineFragment");
            ft.commit();
        }else{
            ChildAppointmentsListFragment appointmentsListFragment = new ChildAppointmentsListFragment();
            Bundle bundle=new Bundle();
            bundle.putString("child_id", currentChild.getId());
            bundle.putString("barcode", currentChild.getBarcodeID());
            bundle.putString("birthdate", currentChild.getBirthdate());


            SimpleDateFormat ft1 = new SimpleDateFormat("dd-MMM-yyyy");
            Log.d("optisation", "Birth date is : "+ft1.format(BackboneActivity.dateParser(currentChild.getBirthdate())));
            appointmentsListFragment.setArguments(bundle);
//
//        //add the Fragment to display a list of current child's appointments
//        fm = new FragmentStackManager(this.getActivity());
//        app.setCurrentFragment(app.APPOINTMENT_LIST_FRAGMENT);
//        fm.addFragment(appointmentsListFragment, R.id.vacc_fragment_frame, true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, false);

            app.setCurrentFragment(app.APPOINTMENT_LIST_FRAGMENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.vacc_fragment_frame, appointmentsListFragment);
            ft.addToBackStack("fragmentVaccineList");
            ft.commit();
        }

        Date todayD = new Date();
        SimpleDateFormat ftD = new SimpleDateFormat("dd-MMM-yyyy");

        return v;
    }

    public void setUpView(View v){
        vaccineDosesListView        = (NestedListView) v.findViewById(R.id.lv_dose_list);
        vaccinateFrame              = (FrameLayout) v.findViewById(R.id.vacc_fragment_frame);
        noBarcode    = (RelativeLayout) v.findViewById(R.id.child_no_barcode_layout);
        noBarcode.setVisibility(View.GONE);
        frameLayout  = (RelativeLayout) v.findViewById(R.id.frame_layout);
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
        Cursor cursor1 = dbh.getReadableDatabase().rawQuery("SELECT * FROM birthplace WHERE ID=?", new String[]{parsedChild.getBirthplaceId()});
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            birthplacestr = cursor1.getString(cursor1.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }
        parsedChild.setBirthplace(birthplacestr);

        parsedChild.setDomicileId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID)));
        Cursor cursor2 = dbh.getReadableDatabase().rawQuery("SELECT * FROM place WHERE ID=?", new String[]{parsedChild.getDomicileId()});
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            villagestr = cursor2.getString(cursor2.getColumnIndex(SQLHandler.PlaceColumns.NAME));
        }

        parsedChild.setDomicile(villagestr);
        parsedChild.setHealthcenterId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID)));
        try {
            Cursor cursor3 = dbh.getReadableDatabase().rawQuery("SELECT * FROM health_facility WHERE ID=?", new String[]{parsedChild.getHealthcenterId()});
            if (cursor3.getCount() > 0) {
                cursor3.moveToFirst();
                hfstr = cursor3.getString(cursor3.getColumnIndex(SQLHandler.HealthFacilityColumns.NAME));
            }
        }catch (Exception e){
            hfstr = "";
        }
        parsedChild.setHealthcenter(hfstr);

        parsedChild.setStatusId(cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.STATUS_ID)));
        Cursor cursor4 = dbh.getReadableDatabase().rawQuery("SELECT * FROM status WHERE ID=?", new String[]{parsedChild.getStatusId()});
        if (cursor4.getCount() > 0) {
            cursor4.moveToFirst();
            statusstr = cursor4.getString(cursor4.getColumnIndex(SQLHandler.StatusColumns.NAME));
        }
        parsedChild.setStatus(statusstr);
        return parsedChild;

    }

}
