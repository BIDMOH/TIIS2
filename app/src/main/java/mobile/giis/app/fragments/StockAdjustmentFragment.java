package mobile.giis.app.fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.giis.app.R;
import mobile.giis.app.adapters.AdapterStockAdjustmentReasons;
import mobile.giis.app.adapters.SingleTextViewAdapter;
import mobile.giis.app.adapters.StockAdjustmentListAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.AdjustmentReasons;
import mobile.giis.app.entity.HealthFacilityBalance;

/**
 *  Created by issymac on 09/02/16.
 */
public class StockAdjustmentFragment extends Fragment{

    private static final String ARG_POSITION = "position";

    StockAdjustmentListAdapter adapter;

    private BackboneApplication application;

    private DatabaseHandler database;

    public static List<HealthFacilityBalance> rowCollectorList;

    private List<AdjustmentReasons> listAdjustmentReasons;

    private TableLayout stockHostTable;

    public static StockAdjustmentFragment newInstance() {
        StockAdjustmentFragment f = new StockAdjustmentFragment();
        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stock_adjustment, null);

        View saveFooterView             = inflater.inflate(R.layout.stock_adjustment_footer, null);
        Button saveButton               = (Button) saveFooterView.findViewById(R.id.save_btn);
        stockHostTable                  = (TableLayout) root.findViewById(R.id.stock_table_container);

        application = (BackboneApplication) this.getActivity().getApplication();
        database = application.getDatabaseInstance();

        listAdjustmentReasons = database.getAdjustmentReasons();
        rowCollectorList = getHealthFacilityBalanceRows();

        for (final HealthFacilityBalance healthFacilityBalance : rowCollectorList){
            View rowView = inflater.inflate(R.layout.stock_adjustment_list_item, null);

            TextView vaccineName = (TextView) rowView.findViewById(R.id.item_name);
            vaccineName.setTypeface(BackboneActivity.Rosario_Regular);
            vaccineName.setText(healthFacilityBalance.getItem_name());

            TextView vaccineLotNumber = (TextView) rowView.findViewById(R.id.lot_number);
            vaccineLotNumber.setTypeface(BackboneActivity.Rosario_Regular);
            vaccineLotNumber.setText(healthFacilityBalance.getLot_number());

            TextView vacccineBalance = (TextView) rowView.findViewById(R.id.balance);
            vacccineBalance.setTypeface(BackboneActivity.Rosario_Regular);
            vacccineBalance.setText(healthFacilityBalance.getBalance()+"");

            final MaterialEditText stockAdjustmentQuantity = (MaterialEditText) rowView.findViewById(R.id.met_quantity);

            MaterialSpinner stockAdjustmentReason = (MaterialSpinner)rowView.findViewById(R.id.reason_spinner);
            SingleTextViewAdapter spAdjustmentReasons = new SingleTextViewAdapter(this.getActivity(), R.layout.single_text_spinner_item_drop_down, database.getNameFromAdjustmentReasons());
            stockAdjustmentReason.setAdapter(spAdjustmentReasons);

            stockAdjustmentQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                item.setTempBalance(etQuantity.getText().toString());
                    healthFacilityBalance.setTempBalance(stockAdjustmentQuantity.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            stockAdjustmentReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
//                item.setSelectedAdjustmentReasonPosition(i);
                    healthFacilityBalance.setSelectedAdjustmentReasonPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            stockHostTable.addView(rowView);
        }

        stockHostTable.addView(saveFooterView);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClicked();
            }
        });

        return root;
    }

    private void saveButtonClicked(){

        boolean canContinue = true;
        boolean balanceNegative = false;

        // check if there are rows that have only qty or reason selected
        for (HealthFacilityBalance item : rowCollectorList) {
            if ((item.getSelectedAdjustmentReasonPosition() == 0 && !item.getTempBalance().equals("")) || (item.getSelectedAdjustmentReasonPosition() != 0 && item.getTempBalance().equals(""))) {
                canContinue = false;
                break;
            }
        }
        if (canContinue) {
            //check if qty is to be subtracted from balance and if that would bring a negative balance
            for (HealthFacilityBalance item : rowCollectorList) {
                if (!item.getTempBalance().equals("")
                        && listAdjustmentReasons.get(item.getSelectedAdjustmentReasonPosition()-1).getPozitive().equals("false")) {
                    if((item.getBalance()-Integer.parseInt(item.getTempBalance())) < 0){
                        balanceNegative = true;
                        break;
                    }
                }
            }

            if (!balanceNegative) {
                boolean success = true;
                for (HealthFacilityBalance item : rowCollectorList) {
                    if (!item.getTempBalance().equals("")) {
                        item.setBalance(
                                (listAdjustmentReasons.get(item.getSelectedAdjustmentReasonPosition() - 1).getPozitive().equals("true"))
                                        ?item.getBalance()+Integer.parseInt(item.getTempBalance())
                                        :item.getBalance()-Integer.parseInt(item.getTempBalance()));
                        if (database.updateHealthFacilityBalance(item) != -1) {
                            startThread(item, listAdjustmentReasons.get(item.getSelectedAdjustmentReasonPosition() - 1).getId());
                        } else {
                            Toast.makeText(StockAdjustmentFragment.this.getActivity(), "Not saved", Toast.LENGTH_LONG).show();
                            success = false;
                        }
                    }
                }
                if (success) {

                    showDialogWhenSavedSucc(getResources().getString(R.string.saved_successfully));

                    rowCollectorList = getHealthFacilityBalanceRows();
                    adapter = new StockAdjustmentListAdapter(StockAdjustmentFragment.this.getActivity(), rowCollectorList, database.getNameFromAdjustmentReasons());
                }
            } else {
                showDialogWhenSavedSucc(getResources().getString(R.string.balance_negative));
            }
        } else {
            showDialogWhenSavedSucc(getResources().getString(R.string.choose_both_qty_reason));
        }
    }

    public void showDialogWhenSavedSucc(String text) {

        Toast.makeText(StockAdjustmentFragment.this.getActivity(), text, Toast.LENGTH_LONG).show();

    }

    private ArrayList<HealthFacilityBalance> getHealthFacilityBalanceRows(){
        ArrayList<HealthFacilityBalance> list = new ArrayList<>();
        Cursor cursor = null;
        cursor = database.getReadableDatabase().rawQuery("SELECT * FROM health_facility_balance where GtinIsActive='true' AND LotIsActive='true'  AND datetime(substr(expire_date,7,10), 'unixepoch') >= datetime('now')  ", null);
        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    HealthFacilityBalance row = new HealthFacilityBalance();
                    row.setBalance(cursor.getInt(cursor.getColumnIndex("balance")));
                    Log.d("balance", row.getBalance() + "");
                    row.setExpire_date(cursor.getString(cursor.getColumnIndex("expire_date")));
                    Log.d("expdate", row.getExpire_date());
                    row.setGtin(cursor.getString(cursor.getColumnIndex("gtin")));
                    Log.d("gtin", row.getGtin());

                    row.setLot_number(cursor.getString(cursor.getColumnIndex("lot_number")));
                    Log.d("lot_number", row.getLot_number());
                    row.setLot_id(cursor.getString(cursor.getColumnIndex("lot_id")));
                    Log.d("lot_id", row.getLot_id());
                    row.setItem_name(cursor.getString(cursor.getColumnIndex("item")));
                    Log.d("item", row.getItem_name());
//
//                    row.setItem_name(cursor.getString(cursor.getColumnIndex("reorder_qty")));
//                    Log.d("reorder_qty", row.getReorder_qty());
                    list.add(row);
                } while (cursor.moveToNext());
            }
        }

        return list;
    }

    private void startThread(HealthFacilityBalance healthFacilityBalance, String reasonId){
        new Thread(){
            HealthFacilityBalance healthFacilityBalance;
            String reasonId;
            public Thread setData(HealthFacilityBalance healthFacilityBalance, String reasonId) {
                this.healthFacilityBalance = healthFacilityBalance;
                this.reasonId  = reasonId;
                return this;
            }

            @Override
            public void run() {
                super.run();
                BackboneApplication backbone = (BackboneApplication) StockAdjustmentFragment.this.getActivity().getApplication();

                try {
                    backbone.saveStockAdjustmentReasons(URLEncoder.encode(healthFacilityBalance.getGtin(), "utf-8")
                            , URLEncoder.encode(healthFacilityBalance.getLot_number(), "utf-8")
                            , URLEncoder.encode(healthFacilityBalance.getTempBalance(), "utf-8")
                            , URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()), "utf-8")
                            , URLEncoder.encode(reasonId, "utf-8")
                            , URLEncoder.encode(backbone.getLOGGED_IN_USER_ID(), "utf-8"));
                }catch (Exception e){e.printStackTrace();}

            }
        }.setData(healthFacilityBalance, reasonId).start();
    }

}
