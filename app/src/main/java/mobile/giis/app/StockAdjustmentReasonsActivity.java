/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.giis.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobile.giis.app.adapters.AdapterStockAdjustmentReasons;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.AdjustmentReasons;
import mobile.giis.app.entity.HealthFacilityBalance;

/**
 * Created by Rubin on 6/3/2015.
 */
public class StockAdjustmentReasonsActivity extends BackboneActivity implements View.OnClickListener {
    LinearLayout lnStockAdj;
    Button btnSave,btnBack;
    TextView txView;
    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.home_wifi_icon);
            if(mobile.giis.app.helpers.Utils.isOnline(context)){
                wifi_logo.setBackgroundColor(0xff00ff00);
                app.setOnlineStatus(true);
            }
            else{
                wifi_logo.setBackgroundColor(0xffff0000);
                app.setOnlineStatus(false);
            }
        }
    };
    private BackboneApplication application;
    private DatabaseHandler database;
    private List<HealthFacilityBalance> rowCollectorList;
    private List<AdjustmentReasons> listAdjustmentReasons;
    private AdapterStockAdjustmentReasons listAdapter;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.stock_adjustments_activity);

        lnStockAdj = (LinearLayout) findViewById(R.id.ln_stock_adjustment);
        btnSave = (Button)findViewById(R.id.save_button);
        btnSave.setOnClickListener(this);
        btnBack = (Button)findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        application = (BackboneApplication) getApplication();
        database = application.getDatabaseInstance();

        listAdjustmentReasons = database.getAdjustmentReasons();

        rowCollectorList = getHealthFacilityBalanceRows();

        listAdapter = new AdapterStockAdjustmentReasons(this,lnStockAdj,rowCollectorList, database.getNameFromAdjustmentReasons());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(status_receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(status_receiver);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.save_button) {

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
                                Toast.makeText(this, "Not saved", Toast.LENGTH_LONG).show();
                                success = false;
                            }
                        }
                    }
                    if (success) {

                        showDialogWhenSavedSucc(getResources().getString(R.string.saved_successfully));
                        lnStockAdj.removeAllViews();
                        rowCollectorList = getHealthFacilityBalanceRows();
                        listAdapter = new AdapterStockAdjustmentReasons(this, lnStockAdj, rowCollectorList, database.getNameFromAdjustmentReasons());
                    }
                } else {
                    showDialogWhenSavedSucc(getResources().getString(R.string.balance_negative));
                }
            } else {
                showDialogWhenSavedSucc(getResources().getString(R.string.choose_both_qty_reason));
            }
        }
        if(v.getId()==R.id.back_btn){
            Intent i = new Intent(this,StockMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
    }

    public void showDialogWhenSavedSucc(String text) {
        final Dialog d = new Dialog(StockAdjustmentReasonsActivity.this);
        d.setContentView(R.layout.layout_stock_child);
        txView = (TextView)d.findViewById(R.id.textView2);
        txView.setText(text);
        d.show();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
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
                BackboneApplication backbone = (BackboneApplication) getApplication();

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
