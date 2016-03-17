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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mobile.giis.app.adapters.AdapterListImmunizedChildren;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.fragments.FragmentImmunizations;
import mobile.giis.app.fragments.MonthlyPerformanceFragment;
import mobile.giis.app.helpers.Utils;

/**
 * Created by Rubin on 4/30/2015.
 */
public class ImmunizedChildrenActivity extends BackboneActivity implements View.OnClickListener {
    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView) findViewById(R.id.home_wifi_icon);
            if (Utils.isOnline(context)) {
                wifi_logo.setBackgroundColor(0xff00ff00);
                app.setOnlineStatus(true);
            } else {
                wifi_logo.setBackgroundColor(0xffff0000);
                app.setOnlineStatus(false);
            }
        }
    };

    Button btnDate, btnImmun,btnChart,btnBack;
    ListView listView;
    String dateVar = null;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.immunized_children_activity);
        app = (BackboneApplication) getApplication();
        initViews();

        mydb = app.getDatabaseInstance();
        AdapterListImmunizedChildren adapter = new AdapterListImmunizedChildren(getApplication(),R.layout.item_listview_vacc_children, mydb.getImmunizedChildren( getYesterdayDateStringDashyyyyMMdd(),app));
        listView.setAdapter(adapter);
        dateVar = getYesterdayDateStringDashyyyyMMdd();

    }

    public void showDate() {

        final Dialog d = new Dialog(ImmunizedChildrenActivity.this);
        d.setTitle(getString(R.string.birthdate_picker));
        d.setContentView(R.layout.birthdate_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDate.setText( ((dp.getDayOfMonth()<10)?"0"+dp.getDayOfMonth():dp.getDayOfMonth()) + "/"+ ((dp.getMonth() + 1<10)?"0"+(dp.getMonth() + 1):dp.getMonth() + 1)
                        +"/"+Integer.toString(dp.getYear()));
                dateVar = dp.getYear() + "-"
                        + ((dp.getMonth() + 1<10)?"0"+(dp.getMonth() + 1):dp.getMonth() + 1)
                        +"-"+ ((dp.getDayOfMonth()<10)?"0"+dp.getDayOfMonth():dp.getDayOfMonth());
                d.dismiss();

                mydb = app.getDatabaseInstance();
                AdapterListImmunizedChildren adapter = new AdapterListImmunizedChildren(getApplication(),R.layout.item_listview_vacc_children, mydb.getImmunizedChildren(dateVar,app));
                listView.setAdapter(adapter);
                Log.e("dataVar",dateVar);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });

        d.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_date_vacc){

            showDate();
        }
        if(v.getId() == R.id.btn_immun){
            // Create and show the dialog.
            FragmentImmunizations newFragment = new FragmentImmunizations();
            if(dateVar!=null) {
                newFragment.setDataChoosen(dateVar);
                newFragment.show(getSupportFragmentManager(), "dialog");
            }
            else{

                Toast.makeText(this,"Please choose one date",Toast.LENGTH_LONG).show();
            }
        }
        if(v.getId() == R.id.immun_chart){
            MonthlyPerformanceFragment newFragment = new MonthlyPerformanceFragment();
            newFragment.show(getSupportFragmentManager(), "dialogMonthlyPerformance");
        }
        if(v.getId()==R.id.back_btn){
            Intent i = new Intent(this,ReportsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }

    }
    private void initViews() {
        btnDate = (Button)findViewById(R.id.btn_date_vacc);
        btnDate.setOnClickListener(this);
        btnDate.setText( getYesterdayDateString());
        btnBack = (Button)findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        btnImmun = (Button)findViewById(R.id.btn_immun);
        btnImmun.setOnClickListener(this);
        btnChart = (Button)findViewById(R.id.immun_chart);
        btnChart.setOnClickListener(this);
        listView = (ListView)findViewById(R.id.list_with_children);
    }
    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    private String getYesterdayDateStringDashyyyyMMdd() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(status_receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(status_receiver);
    }

}
