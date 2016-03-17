package mobile.giis.app.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.giis.app.R;
import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.adapters.vaccinateOfflineListAdapter;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.NonVaccinationReason;

/**
 * Created by issymac on 14/03/16.
 */
public class AdministerVaccineOfflineFragment extends Fragment {

    String barcode;

    private TextView vaccinesTitle, VaccineLotTitle, VaccinationDateTitle, doneTitle, reasonsTitle;
    private ListView allDosesList;
    private Button saveButton;
    private vaccinateOfflineListAdapter adapter;
    private ArrayList<RowCollector> rowCollectorContainer;
    private BackboneApplication application;
    private DatabaseHandler database;

    public static AdministerVaccineOfflineFragment newInstance(String barcode) {
        AdministerVaccineOfflineFragment f = new AdministerVaccineOfflineFragment();
        Bundle b = new Bundle();
        b.putString("barcode", barcode);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcode = getArguments().getString("barcode");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.administer_vaccines_offline_fragment, null);
        setupviews(rootview);


        rowCollectorContainer = new ArrayList<RowCollector>();

        //Getting all vaccinations from Scheduled Vaccination Table dhe their Id's
        application = (BackboneApplication) this.getActivity().getApplication();
        database = application.getDatabaseInstance();

        Cursor cursor = null;
        cursor = database.getReadableDatabase().rawQuery("SELECT * FROM scheduled_vaccination", null);

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) {
                do {
                    RowCollector rowCollector = new RowCollector(this.getActivity());
                    Log.d("Adding Scheduled Vaccination Id", cursor.getString(cursor.getColumnIndex("ID")));
                    rowCollector.setScheduled_vaccination_id(cursor.getString(cursor.getColumnIndex("ID")));

                    Log.d("Adding Scheduled Vaccination Name", cursor.getString(cursor.getColumnIndex("NAME")));
                    rowCollector.setScheduled_vaccination_name(cursor.getString(cursor.getColumnIndex("NAME")));

                    Log.d("Adding Scheduled Vaccination Item Id", cursor.getString(cursor.getColumnIndex("ITEM_ID")));
                    rowCollector.setScheduled_vaccination_item_id(cursor.getString(cursor.getColumnIndex("ITEM_ID")));

                    rowCollectorContainer.add(rowCollector);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        for (final RowCollector rowCollector : rowCollectorContainer) {

            Map<String, String> vaccine_lot_map = new HashMap<String, String>();
            List<String> vaccine_lot_names_list = new ArrayList<String>();
            cursor = database.getReadableDatabase().rawQuery("SELECT '-1' AS id, '-----' AS lot_number, datetime('now') as expire_date UNION " +
                    "SELECT '-2' AS id, 'No Lot' AS lot_number, datetime('now') as expire_date UNION " +
                    "SELECT item_lot.id, item_lot.lot_number, datetime(substr(item_lot.expire_date,7,10), 'unixepoch') FROM item_lot  join health_facility_balance ON item_lot.ID = health_facility_balance.lot_id WHERE item_lot.item_id = ? AND health_facility_balance.LotIsActive = 'true'" +
                    " AND datetime(substr(item_lot.expire_date,7,10), 'unixepoch') >= datetime('now') ORDER BY expire_date", new String[]{rowCollector.getScheduled_vaccination_item_id()});
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("", "Adding to map" + cursor.getString(cursor.getColumnIndex("lot_number")));
                        //Getting a map of vacine lot numbers and their id's
                        vaccine_lot_map.put(cursor.getString(cursor.getColumnIndex("lot_number")), cursor.getString(cursor.getColumnIndex("id")));
                        //Getting a list of the vaccine lot numbers for the spinner
                        vaccine_lot_names_list.add(cursor.getString(cursor.getColumnIndex("lot_number")));
                    } while (cursor.moveToNext());
                    rowCollector.setVaccine_lot_id_name_map(vaccine_lot_map);
                    rowCollector.setVaccine_lot_names_list(vaccine_lot_names_list);
                }
            }

            List<String> reasons = new ArrayList<String>();
            reasons.add("----");
            for (NonVaccinationReason nvElement : database.getAllNonvaccinationReasons()) {
                reasons.add(nvElement.getName());
            }
            rowCollector.setNon_vac_reason_list(reasons);

            List<NonVaccinationReason> non_vaccination_reason_list_with_additions = database.getAllNonvaccinationReasons();
            NonVaccinationReason empty = new NonVaccinationReason();
            empty.setName("----");
            empty.setId("0");
            non_vaccination_reason_list_with_additions.add(empty);
            rowCollector.setNon_vaccination_reason_list_with_additions(non_vaccination_reason_list_with_additions);



        }

        adapter = new vaccinateOfflineListAdapter(this.getActivity(), rowCollectorContainer);

        View v = inflater.inflate(R.layout.stock_adjustment_footer, null);
        saveButton  = (Button) v.findViewById(R.id.save_btn);
        allDosesList.addFooterView(v);
        allDosesList.setAdapter(adapter);

        return rootview;
    }

    public void setupviews(View v) {
        vaccinesTitle       = (TextView) v.findViewById(R.id.vaccine_title);
        VaccineLotTitle     = (TextView) v.findViewById(R.id.vaccine_lot_title);
        VaccinationDateTitle= (TextView) v.findViewById(R.id.vaccine_date_title);
        doneTitle           = (TextView) v.findViewById(R.id.done_title);
        reasonsTitle        = (TextView) v.findViewById(R.id.reasons_title);
        allDosesList        = (ListView) v.findViewById(R.id.all_doses_list);
    }

    public class RowCollector {
        //Holding values from Scheduled Vaccination Table
        private String scheduled_vaccination_name;
        private String scheduled_vaccination_id;
        private String scheduled_vaccination_item_id;

        //Holding Values from Item Lot Table
        private Map vaccine_lot_id_name_map;
        private List<String> vaccine_lot_names_list;
        private int vaccine_lot_current_position;

        private Date vaccination_date;

        //Holds status Done / NotDone
        private String vaccination_done_status = "false";

        private int nonvaccination_reason_position;
        private String non_vac_reason = "";

        private List<String> non_vac_reason_list;
        public List<NonVaccinationReason> non_vaccination_reason_list_with_additions;

        public RowCollector(Context parent) {
        }


        public String getScheduled_vaccination_name() {
            return scheduled_vaccination_name;
        }

        public void setScheduled_vaccination_name(String scheduled_vaccination_name) {
            this.scheduled_vaccination_name = scheduled_vaccination_name;
        }

        public List<String> getNon_vac_reason_list() {
            return non_vac_reason_list;
        }

        public void setNon_vac_reason_list(List<String> non_vac_reason_list) {
            this.non_vac_reason_list = non_vac_reason_list;
        }

        public List<NonVaccinationReason> getNon_vaccination_reason_list_with_additions() {
            return non_vaccination_reason_list_with_additions;
        }

        public void setNon_vaccination_reason_list_with_additions(List<NonVaccinationReason> non_vaccination_reason_list_with_additions) {
            this.non_vaccination_reason_list_with_additions = non_vaccination_reason_list_with_additions;
        }


        public String getScheduled_vaccination_item_id() {
            return scheduled_vaccination_item_id;
        }

        public void setScheduled_vaccination_item_id(String scheduled_vaccination_item_id) {
            this.scheduled_vaccination_item_id = scheduled_vaccination_item_id;
        }

        public String getScheduled_vaccination_id() {
            return scheduled_vaccination_id;
        }

        public void setScheduled_vaccination_id(String scheduled_vaccination_id) {
            this.scheduled_vaccination_id = scheduled_vaccination_id;
        }


        public Map getVaccine_lot_id_name_map() {
            return vaccine_lot_id_name_map;
        }

        public void setVaccine_lot_id_name_map(Map vaccine_lot_id_name_map) {
            this.vaccine_lot_id_name_map = vaccine_lot_id_name_map;
        }

        public List<String> getVaccine_lot_names_list() {
            return vaccine_lot_names_list;
        }

        public void setVaccine_lot_names_list(List<String> vaccine_lot_names_list) {
            this.vaccine_lot_names_list = vaccine_lot_names_list;
        }

        public int getVaccine_lot_current_position() {
            return vaccine_lot_current_position;
        }

        public void setVaccine_lot_current_position(int vaccine_lot_current_position) {
            this.vaccine_lot_current_position = vaccine_lot_current_position;
        }


        public Date getVaccination_date() {
            return vaccination_date;
        }

        public void setVaccination_date(Date vaccination_date) {
            this.vaccination_date = vaccination_date;
        }


        public String getVaccination_done_status() {
            return vaccination_done_status;
        }

        public void setVaccination_done_status(String vaccination_done_status) {
            this.vaccination_done_status = vaccination_done_status;
        }

        public int getNonvaccination_reason_position() {
            return nonvaccination_reason_position;
        }

        public void setNonvaccination_reason_position(int nonvaccination_reason_position) {
            this.nonvaccination_reason_position = nonvaccination_reason_position;
        }


        public String getNon_vac_reason() {
            return non_vac_reason;
        }

        public void setNon_vac_reason(String non_vac_reason) {
            this.non_vac_reason = non_vac_reason;
        }

    }



}
