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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.helpers.Utils;

/**
 * Created by utente1 on 6/7/2015.
 */
public class ReportsActivity extends BackboneActivity implements View.OnClickListener{

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

    Button btnImmunizedChildren , btnVaccinationCoverage, btnStockChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports_activity);

        btnImmunizedChildren = (Button)findViewById(R.id.btn_immunized_children);
        btnVaccinationCoverage = (Button)findViewById(R.id.btn_vaccination_coverage);
        btnStockChart = (Button)findViewById(R.id.btn_stock_chart);

        btnImmunizedChildren.setOnClickListener(this);
        btnVaccinationCoverage.setOnClickListener(this);
        btnStockChart.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_immunized_children){
            Intent intent = new Intent(this, ImmunizedChildrenActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.btn_vaccination_coverage){
            Intent intent = new Intent(this, VaccinationCoverageActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.btn_stock_chart){
            Intent intent = new Intent(this, HealthFacilityBalanceChartActivity.class);
            startActivity(intent);
        }
    }
}
