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

package mobile.tiis.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.ImmunizationCardAdapterForLinearLayout;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.ImmunizationCardItem;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by olsi on 15-03-24.
 */
public class ImmunizationCardActivity extends BackboneActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

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
    private Button back;
    private TextView barcode, name, mother_name, dob, age;
    private String barcodeStr, nameStr, motherNameStr,bdayStr, genderStr, ageStr;
    private DatabaseHandler mydb;
    private BackboneApplication app;
    private String childId;
    private LinearLayout listViewVaccines;
    private ArrayList<ImmunizationCardItem> immunizationCardList;
    private ImmunizationCardAdapterForLinearLayout immunizationCardlListAdapter;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.immunization_card_activity);
        app = (BackboneApplication) getApplication();
        initViews();
        initDb();
        renderViews();
        initListeners();
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

    private void initViews() {
        back = (Button) findViewById(R.id.btn_back_button_immuniz_card_activity);
//        barcode = (TextView) findViewById(R.id.txt_barcode_supplement_activity);
        name = (TextView) findViewById(R.id.child_name_supplements_activity);
        mother_name = (TextView) findViewById(R.id.txt_mother_name_immuniz_card_activity);
//        dob = (TextView) findViewById(R.id.txt_dob_immuniz_card_activity);
//        age = (TextView) findViewById(R.id.age_immuniz_card_activity);
        listViewVaccines = (LinearLayout) findViewById(R.id.lst_immunization_card);
    }

    private void initDb(){
        mydb = app.getDatabaseInstance();
    }

    private void renderViews() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            barcode.setText(extras.getString("barcode"));
            barcodeStr = extras.getString("barcode");
            nameStr = extras.getString("name");
            motherNameStr = extras.getString("mother_name");
            bdayStr = extras.getString("dob");
            genderStr = extras.getString("gender");
            childId = extras.getString("childId");
            name.setText(extras.getString("name"));
            mother_name.setText(extras.getString("mother_name"));
//            dob.setText(extras.getString("dob"));
//            bday = extras.getString("dob");
//            gender = extras.getString("gender");
        }
        if(childId==null && barcodeStr==null){
            toastMessage(getString(R.string.empty_child_id));
            finish();
        }else if(childId==null){
            Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                    new String[]{String.valueOf(extras.getString("barcode"))});
            if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                getChildIdCursor.moveToFirst();
                childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            }
        }

        immunizationCardList = mydb.getImmunizationCard(childId);
        immunizationCardlListAdapter = new ImmunizationCardAdapterForLinearLayout(this,listViewVaccines,immunizationCardList);
    }

    public void initListeners(){
        back.setOnClickListener(this);
        listViewVaccines.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.btn_back_button_immuniz_card_activity) {
            Bundle extras = getIntent().getExtras();
            Intent back = new Intent(this, ViewChildActivity.class);
            back.putExtra("barcode", getIntent().getExtras().getString("barcode"));
            back.putExtra(BackboneApplication.CHILD_ID,getIntent().getExtras().getString("childId"));
            //back.putExtra("result", extras.getString("barcode"));
            startActivity(back);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //   startAdministrerVaccinesAct(immunizationCardList.get(i));
    }

}
