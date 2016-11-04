package mobile.tiis.appv2.fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.adapters.PlacesOfBirthAdapter;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.database.SQLHandler;
import mobile.tiis.appv2.entity.StockStatusEntity;

/**
 *  Created by issy on 10/18/16.
 */

public class StockStatusFragment extends Fragment{

    //UI Elements
    private TableLayout stockStatusTable;
    private MaterialSpinner reportingPeriod;
    private Button generateReport;

    List<String> monthYear = new ArrayList<>();
    String selectedMonth = "";
    ProgressDialog progressDialog;
    List<StockStatusEntity> stockStatusEntities = new ArrayList<>();

    public static StockStatusFragment newInstance() {
        StockStatusFragment f = new StockStatusFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        String year = cal.get(Calendar.YEAR)+"";

        monthYear.add("Jan "+year);
        monthYear.add("Feb "+year);
        monthYear.add("Mar "+year);
        monthYear.add("Apr "+year);
        monthYear.add("May "+year);
        monthYear.add("Jun "+year);
        monthYear.add("Jul "+year);
        monthYear.add("Aug "+year);
        monthYear.add("Sep "+year);
        monthYear.add("Oct "+year);
        monthYear.add("Nov "+year);
        monthYear.add("Dec "+year);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.stock_status_report, null);
        setUpViews(root);

        progressDialog = new ProgressDialog(StockStatusFragment.this.getActivity());

        PlacesOfBirthAdapter adapter   = new PlacesOfBirthAdapter(StockStatusFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, monthYear);
        reportingPeriod.setAdapter(adapter);

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginGeneratingReport();
            }
        });

        return root;

    }

    public void beginGeneratingReport(){
        selectedMonth = reportingPeriod.getSelectedItem().toString();
        progressDialog.show();
        stockStatusEntities = getDataFromStockStatusTable();

        stockStatusTable.removeAllViews();

        for (StockStatusEntity stockStatusEntity : stockStatusEntities){

            View view = LayoutInflater.from(StockStatusFragment.this.getActivity()).inflate(R.layout.stock_status_list_item, null);

            TextView itemName = (TextView) view.findViewById(R.id.antigen);
            TextView dosesReceived = (TextView) view.findViewById(R.id.doses_received);
            TextView discardedUnopened = (TextView) view.findViewById(R.id.discarded_unopened);

            itemName.setText(stockStatusEntity.getItemName());
            dosesReceived.setText(stockStatusEntity.getDosesReceived());
            discardedUnopened.setText(stockStatusEntity.getDiscardedUnopened());

            stockStatusTable.addView(view);
        }

        progressDialog.hide();
        stockStatusTable.setVisibility(View.VISIBLE);

    }

    public List<StockStatusEntity> getDataFromStockStatusTable(){
        BackboneApplication app = (BackboneApplication) StockStatusFragment.this.getActivity().getApplication();
        DatabaseHandler databaseHandler = app.getDatabaseInstance();

        List<StockStatusEntity> entities = new ArrayList<>();

        String query =  "";
        query   = "SELECT * FROM "+ SQLHandler.Tables.STOCK_STATUS_REPORT+" WHERE "+ SQLHandler.StockStatusColumns.REPORTED_MONTH+ " = '"
                +databaseHandler.getCurrentMonthName(app)+ " 2016'";
        Cursor cursor = databaseHandler.getReadableDatabase().rawQuery(query, null);
        Log.d("STOCK_STATUS", "Cursor count = "+cursor.getCount());
        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                do {
                    StockStatusEntity row = new StockStatusEntity();
                    row.setItemName(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.ITEM_NAME)));
                    row.setDiscardedUnopened(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DISCARDED_UNOPENED)));
                    row.setDosesReceived(cursor.getString(cursor.getColumnIndex(SQLHandler.StockStatusColumns.DOSES_RECEIVED)));

                    entities.add(row);
                }while (cursor.moveToNext());
            }
        }

        return entities;
    }

    public void setUpViews(View v){
        stockStatusTable    = (TableLayout) v.findViewById(R.id.stock_status_table);
        stockStatusTable    .setVisibility(View.GONE);
        reportingPeriod     = (MaterialSpinner) v.findViewById(R.id.mon_year_spiner);
        generateReport      = (Button) v.findViewById(R.id.generate_report);
    }



}
