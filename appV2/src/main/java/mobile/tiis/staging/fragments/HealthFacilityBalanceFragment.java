package mobile.tiis.staging.fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mobile.tiis.staging.R;
import mobile.tiis.staging.adapters.HealthFacilityBalanceListAdapter;
import mobile.tiis.staging.base.BackboneActivity;
import mobile.tiis.staging.base.BackboneApplication;
import mobile.tiis.staging.database.DatabaseHandler;
import mobile.tiis.staging.entity.HealthFacilityBalance;
import mobile.tiis.staging.util.Constants;

/**
 * Created by issymac on 09/02/16.
 */
public class HealthFacilityBalanceFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private List<HealthFacilityBalance> rowCollectorList;

    HealthFacilityBalanceListAdapter adapter;

    private BackboneApplication application;

    private DatabaseHandler database;

    private TableLayout hfBalanceTable;

    public static HealthFacilityBalanceFragment newInstance() {
        HealthFacilityBalanceFragment f = new HealthFacilityBalanceFragment();
        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_health_facility_balance, null);
        setUpView(root);

        application = (BackboneApplication) this.getActivity().getApplication();
        database = application.getDatabaseInstance();
        rowCollectorList = getHealthFacilityBalanceRows();

        hfBalanceTable  = (TableLayout) root.findViewById(R.id.hf_balance_table);

        TextView nameTitle       = (TextView) root.findViewById(R.id.txt_name_title);
        nameTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView lotNumberTitle = (TextView) root.findViewById(R.id.txt_lot_number_title);
        lotNumberTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView expiryDateTitle = (TextView) root.findViewById(R.id.txt_exp_date_title);
        expiryDateTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView balanceTitle = (TextView) root.findViewById(R.id.txt_balance_title);
        balanceTitle.setTypeface(BackboneActivity.Rosario_Regular);
        TextView editBalance = (TextView) root.findViewById(R.id.dozi_title);
        editBalance.setTypeface(BackboneActivity.Rosario_Regular);
        Button saveButton = (Button) root.findViewById(R.id.savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButtonClicked();
            }
        });



        fillHFBalanceTable();

        return root;
    }

    public void setUpView(View v){

    }

    public void fillHFBalanceTable(){

        for (HealthFacilityBalance items : rowCollectorList){
            final HealthFacilityBalance item = items;
            View rowView = View.inflate(HealthFacilityBalanceFragment.this.getActivity(), R.layout.health_facility_balance_list_item, null);
            TextView name = (TextView) rowView.findViewById(R.id.item_name);
            name.setTypeface(BackboneActivity.Rosario_Regular);
            TextView lotNumber = (TextView) rowView.findViewById(R.id.lot_number);
            lotNumber.setTypeface(BackboneActivity.Rosario_Regular);
            TextView expiryDate = (TextView) rowView.findViewById(R.id.expiry_date);
            expiryDate.setTypeface(BackboneActivity.Rosario_Regular);
            TextView balance = (TextView) rowView.findViewById(R.id.balance);
            balance.setTypeface(BackboneActivity.Rosario_Regular);
            final MaterialEditText changed = (MaterialEditText) rowView.findViewById(R.id.hf_balance_adjustment_value);

            name.setText(item.getItem_name().toString());
            lotNumber.setText(item.getLot_number());
            SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
            Log.d("Setting exp date", ft.format(BackboneActivity.dateParser(item.getExpire_date())));
            if(calculateDateDiffToExpiryRowColor(BackboneActivity.dateParser(item.getExpire_date()))){
                expiryDate.setBackgroundColor(Color.YELLOW);
            }
            expiryDate.setText(ft.format(BackboneActivity.dateParser(item.getExpire_date())));
            balance.setText(item.getBalance() + "");

            changed.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setTempBalance(changed.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            hfBalanceTable.addView(rowView);
        }
    }

    public void saveButtonClicked() {
            boolean success = true;
            for (HealthFacilityBalance healthFacilityBalance : rowCollectorList){
                if(!healthFacilityBalance.getTempBalance().equals("")){
                    healthFacilityBalance.setBalance(Integer.parseInt(healthFacilityBalance.getTempBalance()));
                    if(database.updateHealthFacilityBalance(healthFacilityBalance) != -1) {
                        startThread(healthFacilityBalance);
                    }else{
                        Toast.makeText(HealthFacilityBalanceFragment.this.getActivity(), "Not saved", Toast.LENGTH_LONG).show();
                        success= false;
                    }
                }
            }
            if(success){

                showDialogWhenSavedSucc();
                hfBalanceTable.removeAllViews();
                rowCollectorList = getHealthFacilityBalanceRows();
                fillHFBalanceTable();
            }
    }

    public void showDialogWhenSavedSucc() {
        final Dialog d = new Dialog(HealthFacilityBalanceFragment.this.getActivity());
        d.setContentView(R.layout.layout_stock_child);
        d.setCancelable(false);
        TextView tvView = (TextView)d.findViewById(R.id.textView2);
        tvView.setText(getString(R.string.saved_successfully));

        d.show();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        HealthFacilityBalanceFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(d!=null)d.dismiss();
                            }
                        });
                    }
                },
                3500
        );
    }

    public boolean calculateDateDiffToExpiryRowColor(Date expiry) {
        Date now = new Date();
        long diff = getDaysDifference(now, expiry);
        return diff < Constants.LimitNumberOfDaysBeforeExpireVal;
    }

    public boolean expDateValue(Date expiry) {
        Date now = new Date();
        long diff = getDaysDifference(now, expiry);
        if (diff<0){
            return true;
        }
        else{
            return false;
        }
    }

    public static final long  getDaysDifference(Date d1, Date d2){
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
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

    private void startThread(HealthFacilityBalance healthFacilityBalance){
        new Thread(){
            HealthFacilityBalance healthFacilityBalance;
            public Thread setData(HealthFacilityBalance healthFacilityBalance) {
                this.healthFacilityBalance = healthFacilityBalance;
                return this;
            }

            @Override
            public void run() {
                super.run();
                BackboneApplication backbone = (BackboneApplication) HealthFacilityBalanceFragment.this.getActivity().getApplication();

                try {
                    backbone.saveHealthFacilityBalance(URLEncoder.encode(healthFacilityBalance.getGtin(), "utf-8")
                            , URLEncoder.encode(healthFacilityBalance.getLot_number(), "utf-8")
                            , healthFacilityBalance.getBalance()+""
                            , URLEncoder.encode(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()), "utf-8")
                            , URLEncoder.encode(backbone.getLOGGED_IN_USER_ID(), "utf-8"));
                }catch (Exception e){e.printStackTrace();}

            }
        }.setData(healthFacilityBalance).start();
    }

}
