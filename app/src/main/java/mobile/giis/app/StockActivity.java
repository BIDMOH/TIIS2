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

import mobile.giis.app.adapters.AdapterHealthFacilityBalance;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.entity.HealthFacilityBalance;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Teodor on 2/8/2015.
 */
public class StockActivity extends BackboneActivity implements View.OnClickListener {
    LinearLayout listLayout;
    Button btnSave,btnBack;
    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.home_wifi_icon);
            if(Utils.isOnline(context)){
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
    private AdapterHealthFacilityBalance ListAdapter;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.stock_activity);

        listLayout = (LinearLayout) findViewById(R.id.hf_balancee_listview);
        btnSave = (Button)findViewById(R.id.save_button);
        btnSave.setOnClickListener(this);
        btnBack = (Button)findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        application = (BackboneApplication) getApplication();
        database = application.getDatabaseInstance();
        rowCollectorList = getHealthFacilityBalanceRows();

        ListAdapter = new AdapterHealthFacilityBalance(this,listLayout,rowCollectorList);

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
        if(v.getId()== R.id.save_button){
            boolean success = true;
        for (HealthFacilityBalance healthFacilityBalance : rowCollectorList){
            if(!healthFacilityBalance.getTempBalance().equals("")){
                healthFacilityBalance.setBalance(Integer.parseInt(healthFacilityBalance.getTempBalance()));
                if(database.updateHealthFacilityBalance(healthFacilityBalance) != -1) {
                    startThread(healthFacilityBalance);
                }else{
                    Toast.makeText(this, "Not saved", Toast.LENGTH_LONG).show();
                    success= false;
                }
            }
        }
            if(success){

                showDialogWhenSavedSucc();
                listLayout.removeAllViews();
                rowCollectorList = getHealthFacilityBalanceRows();
                ListAdapter = new AdapterHealthFacilityBalance(this, listLayout, rowCollectorList);
            }
        }
        if(v.getId()==R.id.back_btn){
            Intent i = new Intent(this,StockMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
    }

    public void showDialogWhenSavedSucc() {
        final Dialog d = new Dialog(StockActivity.this);
        d.setContentView(R.layout.layout_stock_child);
        TextView tvView = (TextView)d.findViewById(R.id.textView2);
        tvView.setText(getString(R.string.saved_successfully));

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
                BackboneApplication backbone = (BackboneApplication) getApplication();

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
