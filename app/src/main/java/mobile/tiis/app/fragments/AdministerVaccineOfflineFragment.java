package mobile.tiis.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.adapters.vaccinateOfflineListAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.entity.NonVaccinationReason;
import mobile.tiis.app.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by issymac on 14/03/16.
 */
public class AdministerVaccineOfflineFragment extends Fragment {
    private static final String TAG = AdministerVaccineOfflineFragment.class.getSimpleName();
    String barcode;

    private TextView vaccinesTitle, VaccineLotTitle, VaccinationDateTitle, doneTitle, reasonsTitle;
    private ListView allDosesList;
    private Button saveButton;
    private vaccinateOfflineListAdapter adapter;
    private ArrayList<RowCollector> rowCollectorContainer;
    private BackboneApplication application;
    private DatabaseHandler database;
    private TableLayout tableLayout;
    private ProgressDialog progressDialog;


    private Looper backgroundLooper;

    private Subscription subscription;

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

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

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

            View rowView = inflater.inflate(R.layout.vaccine_offline_list_item, null);
            TextView vaccineName                = (TextView) rowView.findViewById(R.id.vaccine_name);
            final MaterialSpinner vaccineLot          = (MaterialSpinner) rowView.findViewById(R.id.vaccine_lot_spinner);
            TextView vaccineDate                = (TextView) rowView.findViewById(R.id.vaccination_date);
            CheckBox done                       = (CheckBox) rowView.findViewById(R.id.done_checkbox);
            final MaterialSpinner nonVAccineReason    = (MaterialSpinner) rowView.findViewById(R.id.reason_spinner);

            vaccineName.setText(rowCollector.getScheduled_vaccination_name());

            Map<String, String> vaccine_lot_map = new HashMap<String, String>();
            List<String> vaccine_lot_names_list = new ArrayList<String>();
            cursor = database.getReadableDatabase().rawQuery("SELECT '-1' AS id, '-----' AS lot_number, datetime('now') as expire_date, '0' as balance, 1 as r_order UNION " +
                     " SELECT '-2' AS id, 'No Lot' AS lot_number, datetime('now') as expire_date, '0' as balance, 1 as r_order UNION " +
                     " SELECT item_lot.id, item_lot.lot_number, datetime(substr(item_lot.expire_date,7,10), 'unixepoch'), balance, (balance > 0) as r_order " +
                     " FROM item_lot  join health_facility_balance ON item_lot.ID = health_facility_balance.lot_id " +
                     " WHERE item_lot.item_id = ? AND health_facility_balance.LotIsActive = 'true'" +
                     " AND datetime(substr(item_lot.expire_date,7,10), 'unixepoch') >= datetime('now') ORDER BY r_order desc, expire_date", new String[]{rowCollector.getScheduled_vaccination_item_id()});

//            cursor = database.getReadableDatabase().rawQuery("SELECT '-1' AS id, '-----' AS lot_number, datetime('now') as expire_date UNION " +
//                    "SELECT '-2' AS id, 'No Lot' AS lot_number, datetime('now') as expire_date UNION " +
//                    "SELECT item_lot.id, item_lot.lot_number, datetime(substr(item_lot.expire_date,7,10), 'unixepoch') FROM item_lot  join health_facility_balance ON item_lot.ID = health_facility_balance.lot_id WHERE item_lot.item_id = ? AND health_facility_balance.LotIsActive = 'true'" +
//                    " AND datetime(substr(item_lot.expire_date,7,10), 'unixepoch') >= datetime('now') ORDER BY expire_date", new String[]{rowCollector.getScheduled_vaccination_item_id()});

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

            /*
            Vaccine LOT spinner implementation
             */
            SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(this.getActivity(), R.layout.single_text_spinner_item_drop_down, rowCollector.getVaccine_lot_names_list());
            vaccineLot.setAdapter(statusAdapter);

            // -Condition to set Lot on start (first good one / No lot)
            if (rowCollector.getVaccine_lot_names_list().size() > 2) {
                vaccineLot.setSelection(2);
                rowCollector.setVaccine_lot_current_position(2);
            } else {
                vaccineLot.setSelection(1);
                rowCollector.setVaccine_lot_current_position(1);
            }
            vaccineLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    vaccineLot.setSelection(position);
                    rowCollector.setVaccine_lot_current_position(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });

            /*
            Vaccination Date TextView implementation
             */
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            vaccineDate.setText(ft.format(dNow));
            rowCollector.setVaccination_date(dNow);

            /*
            Non Vaccination Reason Spinner Implementation
             */
            //NonVaccinationReason Column Spinner
            List<String> reasons = new ArrayList<String>();
            reasons.add("----");
            for (NonVaccinationReason nvElement : database.getAllNonvaccinationReasons()) {
                reasons.add(nvElement.getName());
            }


            final List<NonVaccinationReason> non_vaccination_reason_list_with_additions = database.getAllNonvaccinationReasons();
            NonVaccinationReason empty = new NonVaccinationReason();
            empty.setName("----");
            empty.setId("0");
            non_vaccination_reason_list_with_additions.add(empty);

            final SingleTextViewAdapter statusAdapterNonVaccinationReason = new SingleTextViewAdapter(this.getActivity(), R.layout.single_text_spinner_item_drop_down, reasons);
            nonVAccineReason.setAdapter(statusAdapterNonVaccinationReason);
            nonVAccineReason.setSelection(0);
            rowCollector.setNonvaccination_reason_position(0);
            rowCollector.setNon_vac_reason("0");

            nonVAccineReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    nonVAccineReason.setSelection(position);
                    rowCollector.setNonvaccination_reason_position(position);

                    for (NonVaccinationReason a : non_vaccination_reason_list_with_additions) {
                        if (statusAdapterNonVaccinationReason.getItem(position).toString().equalsIgnoreCase(a.getName())) {
                            rowCollector.setNon_vac_reason(a.getId());
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    //no changes
                }

            });

            /*
            Vaccination Done Checkbox implementation
             */
            rowCollector.setVaccination_done_status("false");
            done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    rowCollector.setVaccination_done_status(String.valueOf(b));
                    if (!b) {
                        nonVAccineReason.setVisibility(View.VISIBLE);
                        //VaccineLotColumn.setSelection(0);
                        rowCollector.setNonvaccination_reason_position(0);

                    }
                    if (b) {
                        nonVAccineReason.setVisibility(View.INVISIBLE);
                        rowCollector.setNon_vac_reason("-1");
                    }
                }
            });

            tableLayout.addView(rowView);

        }
        View v = inflater.inflate(R.layout.stock_adjustment_footer, null);
        tableLayout.addView(v);

        saveButton  = (Button) v.findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("skip", "Button Clicked clicked");
                saveButtonClickEvent();
            }
        });


        progressDialog =  new ProgressDialog(getActivity());
        progressDialog.setMessage("Saving data. \nPlease wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        return rootview;
    }

    public void setupviews(View v) {
        vaccinesTitle       = (TextView) v.findViewById(R.id.vaccine_title);
        VaccineLotTitle     = (TextView) v.findViewById(R.id.vaccine_lot_title);
        VaccinationDateTitle= (TextView) v.findViewById(R.id.vaccine_date_title);
        doneTitle           = (TextView) v.findViewById(R.id.done_title);
        reasonsTitle        = (TextView) v.findViewById(R.id.reasons_title);
        allDosesList        = (ListView) v.findViewById(R.id.all_doses_list);
        tableLayout         = (TableLayout) v.findViewById(R.id.administer_vaccines_offline_table_layout);
    }

    public void saveButtonClickEvent(){
        int done = 0;

        for (RowCollector a : rowCollectorContainer) {
            if (a.getVaccination_done_status().equalsIgnoreCase("false")) {
                if (a.getNonvaccination_reason_position() != 0) {
                    done += 1;
                }
            } else if (a.getVaccination_done_status().equalsIgnoreCase("true")) {
                done += 1;
            }

        }

        if (done == 0) {
            final AlertDialog ad22 = new AlertDialog.Builder(this.getActivity()).create();
            ad22.setTitle(getString(R.string.warning));
            ad22.setMessage(getString(R.string.select_non_vacc_reason));
            ad22.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ad22.dismiss();
                }
            });
            ad22.show();
            return;
        }


        for (final RowCollector a : rowCollectorContainer) {

            if (a.getVaccination_done_status().equalsIgnoreCase("true") && a.getVaccine_lot_names_list().get(a.getVaccine_lot_current_position()).equalsIgnoreCase("-----")) {
                final AlertDialog ad22 = new AlertDialog.Builder(this.getActivity()).create();
                ad22.setTitle("Not Saved");
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

            if (a.getVaccination_done_status().equalsIgnoreCase("false") && a.getNonvaccination_reason_position() == 0) {
                Log.d("Skip", "Skipping vaccine");
            } else {
                final SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");

                progressDialog.show();
                //adding implementation for RXAndroid to substitute the use of AsyncTasks
                subscription = Observable.defer(new Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        // Do some long running operation
                        BackboneApplication application = (BackboneApplication) AdministerVaccineOfflineFragment.this.getActivity().getApplication();
                        DatabaseHandler db = application.getDatabaseInstance();
                        StringBuilder updateUrl = new StringBuilder(BackboneActivity.WCF_URL + "VaccinationEvent.svc/UpdateVaccinationEventByBarcodeVaccine?")
                                .append("barcodeId=").append(barcode)
                                .append("&vaccineId=").append(a.getScheduled_vaccination_id())
                                .append("&vaccinelot=").append(a.getVaccine_lot_id_name_map().get(a.getVaccine_lot_names_list().get(a.getVaccine_lot_current_position())))
                                .append("&healthFacilityId=").append(application.getLOGGED_IN_USER_HF_ID())
                                .append("&vaccinationDate=").append(URLEncoder.encode(formatted.format(a.getVaccination_date())))
                                .append("&notes=").append("")
                                .append("&vaccinationStatus=").append(a.getVaccination_done_status())
                                .append("&nonvaccinationReasonId=").append(a.getNon_vac_reason())
                                .append("&userId=").append(application.getLOGGED_IN_USER_ID());
                        Log.d("Created URL", updateUrl.toString());

                        int status = application.updateVaccinationEventOnServer(updateUrl.toString());
                        Log.d("Saving offline status", status + "");

                        return Observable.just(true);
                        }
                    }).subscribeOn(AndroidSchedulers.from(backgroundLooper))
                    // Be notified on the main thread
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
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

        }


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


    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();

    }
}
