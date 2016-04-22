package mobile.tiis.app.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;

/**
 * Created by issymac on 31/03/16.
 */
public class StockBalanceReportFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private TextView region, district, healthFacility, reportingPeriod, title;

    BackboneApplication app;

    private ProgressBar progressBar;

    private DatabaseHandler mydb;

    private TableLayout stockBalanceTable;

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    public static StockBalanceReportFragment newInstance(int position) {
        StockBalanceReportFragment f = new StockBalanceReportFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        app = (BackboneApplication) this.getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rowview;
        rowview = inflater.inflate(R.layout.fragment_stock_balance, null);
        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));

        new getStockBalance().execute("");

        return rowview;
    }

    public void prepareUIElements(View v){
        region          = (TextView) v.findViewById(R.id.region_title);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
        stockBalanceTable = (TableLayout) v.findViewById(R.id.stock_balance_table);
    }

    public class getStockBalance extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewRows> mVar;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {

            Cursor cursor;
            mVar = new ArrayList<>();
            /*
            "datetime(substr(vaccination_event.SCHEDULED_DATE,7,10), 'unixepoch') <= datetime('now','+30 days') AND vaccination_event.IS_ACTIVE='true' AND vaccination_event.VACCINATION_STATUS='false' AND (vaccination_event.NONVACCINATION_REASON_ID=0  OR vaccination_event.NONVACCINATION_REASON_ID in (Select ID from nonvaccination_reason where KEEP_CHILD_DUE = 'true'))" +
             */
            String SQLStockBalance;
            SQLStockBalance =
                    "SELECT item, balance, expire_date, gtin  "+
                            " FROM health_facility_balance " +
                            " WHERE  GtinIsActive = 'true' AND LotIsActive = 'true' "+
                            " GROUP BY item " ; //+
//                            " ORDER BY SCHEDULED_DATE "+
//                            " LIMIT "+startRow+", 10 ; ";

            Log.e("Stockbalance", SQLStockBalance);
            cursor = mydb.getReadableDatabase().rawQuery(SQLStockBalance, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ViewRows row = new ViewRows();
                        row.setAntigen(cursor.getString(cursor.getColumnIndex("item")));
                        row.setBatchNumber(cursor.getString(cursor.getColumnIndex("gtin")));
                        row.setPhysicalCount(cursor.getString(cursor.getColumnIndex("balance")));
                        row.setExpDate(BackboneActivity.dateParser(cursor.getString(cursor.getColumnIndex("expire_date"))));
                        mVar.add(row);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            return mVar.size();
        }

        @Override
        protected void onPostExecute(Integer result) {

//            adapter.updateData(mVar);
            if (result!=0){
                stockBalanceTable.removeAllViews();
                FillStockBalanceTable(mVar);
                progressBar.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    private void FillStockBalanceTable(ArrayList<ViewRows> items){
        ArrayList<ViewRows> VR = items;
        for (ViewRows row : VR){
            View v  = View.inflate(StockBalanceReportFragment.this.getActivity(), R.layout.stock_balance_item, null);

            TextView antigen = (TextView) v.findViewById(R.id.stock_antigen);
            TextView batchNumber    = (TextView) v.findViewById(R.id.stock_batch_number);
            TextView expiredDate = (TextView) v.findViewById(R.id.stock_exp_date);
            TextView physicalCount = (TextView) v.findViewById(R.id.physical_count);
            TextView amountVVM = (TextView) v.findViewById(R.id.stock_reason_vvm);
            TextView amountExpired = (TextView) v.findViewById(R.id.stock_reason_expiry);
            TextView amountPhysicalDamage = (TextView) v.findViewById(R.id.stock_reason_physical_damage);

            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
            antigen.setText(row.getAntigen());
            batchNumber.setText(row.getBatchNumber());
            expiredDate.setText(ft.format(row.getExpDate()));
            physicalCount.setText(row.getPhysicalCount());

            stockBalanceTable.addView(v);

        }
    }

    class ViewRows {

        public ViewRows(){
        }

        String antigen, batchNumber, physicalCount, vvmCount, expCount, phyDamageCount;

        Date expDate;

        public String getAntigen() {
            return antigen;
        }

        public void setAntigen(String antigen) {
            this.antigen = antigen;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public Date getExpDate() {
            return expDate;
        }

        public void setExpDate(Date expDate) {
            this.expDate = expDate;
        }

        public String getPhysicalCount() {
            return physicalCount;
        }

        public void setPhysicalCount(String physicalCount) {
            this.physicalCount = physicalCount;
        }

        public String getVvmCount() {
            return vvmCount;
        }

        public void setVvmCount(String vvmCount) {
            this.vvmCount = vvmCount;
        }

        public String getExpCount() {
            return expCount;
        }

        public void setExpCount(String expCount) {
            this.expCount = expCount;
        }

        public String getPhyDamageCount() {
            return phyDamageCount;
        }

        public void setPhyDamageCount(String phyDamageCount) {
            this.phyDamageCount = phyDamageCount;
        }
    }

}
