package mobile.tiis.staging.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mobile.tiis.staging.CustomViews.NestedListView;
import mobile.tiis.staging.R;
import mobile.tiis.staging.SubClassed.BackHandledFragment;
import mobile.tiis.staging.adapters.ChildAppointmentListAdapter;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.database.SQLHandler;
import mobile.tiis.staging.util.ViewAppointmentRow;

/**
 * Created by issymac on 03/03/16.
 */
public class ChildAppointmentsListFragment extends BackHandledFragment{

    private String childBarcode, birthdate, child_id;

    private BackboneApplication app;

    private DatabaseHandler dbh;

    private List<ViewAppointmentRow> var;

    NestedListView appointmentList;

    ChildAppointmentListAdapter adapter;

    FragmentStackManager fm;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_appointement_row, null);
        setUpView(root);

        child_id    = getArguments().getString("child_id");
        childBarcode= getArguments().getString("barcode");
        birthdate   = getArguments().getString("birthdate");
        Log.d("ViewAppointment", "Child "+ childBarcode+"");

        fm = new FragmentStackManager(this.getActivity());

        app = (BackboneApplication) this.getActivity().getApplication();

        DatabaseHandler this_database = app.getDatabaseInstance();
        SQLHandler handler = new SQLHandler();
        var = new ArrayList<ViewAppointmentRow>();
        String result = "";

        if (child_id != null && !child_id.isEmpty()) {
            Log.d("ViewAppointment:", "Child_Id: " + child_id);
            Cursor mCursor = null;
            mCursor = this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[]{child_id, child_id});
            if (mCursor != null) {
                if (mCursor.getCount()==0) {
                    child_id = this_database.getChildIdByBarcode(childBarcode);
                    mCursor = this_database.getReadableDatabase().rawQuery(handler.SQLVaccinations, new String[]{child_id, child_id});
                }
                if(mCursor.moveToFirst()){
                    do {
                        ViewAppointmentRow row = new ViewAppointmentRow();
                        row.setAppointment_id(mCursor.getString(mCursor.getColumnIndex("APPOINTMENT_ID")));
                        row.setVaccine_dose(mCursor.getString(mCursor.getColumnIndex("VACCINES")));
                        row.setSchedule(mCursor.getString(mCursor.getColumnIndex("SCHEDULE")));
                        row.setScheduled_date(mCursor.getString(mCursor.getColumnIndex("SCHEDULED_DATE")));
                        var.add(row);
                    } while (mCursor.moveToNext());
                }
            }
        }

        Log.d("ViewAppointment", "Rows "+ var.size()+"");

        setListViewHeightBasedOnChildren(appointmentList);
        adapter = new ChildAppointmentListAdapter(this.getActivity(),R.layout.appointment_list_item, var);
        appointmentList.setAdapter(adapter);

        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AdministerVaccineFragment administerVaccineFragment = new AdministerVaccineFragment();
                Bundle bundle = new Bundle();
                ChildAppointmentListAdapter childAppointmentListAdapter = (ChildAppointmentListAdapter)adapterView.getAdapter();
                ViewAppointmentRow var = childAppointmentListAdapter.getItem(i);
                String appointmentId = var.getAppointment_id();
                bundle.putString("appointment_id", appointmentId);
                bundle.putString("birthdate", birthdate);
                bundle.putString("barcode", childBarcode);

                administerVaccineFragment.setArguments(bundle);
                app.setCurrentFragment(app.VACCINATE_CHILD_FRAGMENT);
//                fm.addFragment(administerVaccineFragment, R.id.vacc_fragment_frame, true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, false);


                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.vacc_fragment_frame, administerVaccineFragment);
                ft.addToBackStack("AdministerVaccineFragment");
                ft.commit();
            }
        });

        return root;
    }

    public void setUpView(View v){
        appointmentList     = (NestedListView) v.findViewById(R.id.lv_appointments_list);
    }

    @Override
    public String getTagText() {
        return null;
    }

    @Override
    public boolean onBackPressed() {

//        FragmentManager fm = ChildAppointmentsListFragment.this.getActivity().getSupportFragmentManager();
//        fm.popBackStack();

        //Do not consume the back pressing on this fragmen
        return false;
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

}
