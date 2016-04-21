package mobile.tiis.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.VaccinateOfflineViewpagerAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.helpers.Utils;

/**
 * Created by issymac on 14/03/16.
 */
public class AdministerVaccineOfflineRevisedActivity extends BackboneActivity {

    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView) findViewById(R.id.vacc_offline_wifi_icon);
            if (Utils.isOnline(context)) {
                wifi_logo.setImageResource(R.drawable.network_on);
                app.setOnlineStatus(true);
            } else {
                wifi_logo.setImageResource(R.drawable.network_off);
                app.setOnlineStatus(false);
            }
        }
    };

    private DatabaseHandler mydb;

    private PagerSlidingTabStrip tabs;

    private ViewPager pager;

    public Toolbar toolbar;

    public static TextView toolbarTitle;

    String barcodeValue;

    VaccinateOfflineViewpagerAdapter adapter;

    public AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle starter) {
        super.onCreate(starter);
        setContentView(R.layout.vaccinate_offline_revicsed_activity);
        setupview();
        final BackboneApplication app = (BackboneApplication) getApplication();

        Bundle extras = getIntent().getExtras();
        mydb = app.getDatabaseInstance();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (extras != null) {
            barcodeValue = extras.getString("barcode");
            toolbarTitle.setText("Child's Barcode : "+barcodeValue);
        }

        //Create dialogue to prompt syncronization
        LayoutInflater li = LayoutInflater.from(AdministerVaccineOfflineRevisedActivity.this);
        View promptsView = li.inflate(R.layout.custom_alert_dialogue, null);
        alertDialogBuilder = new AlertDialog.Builder(AdministerVaccineOfflineRevisedActivity.this);
        alertDialogBuilder.setView(promptsView);
        TextView message = (TextView) promptsView.findViewById(R.id.dialogMessage);
        message.setText("Child was not found locally and there is no internet connection click ok to proceed");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        adapter = new VaccinateOfflineViewpagerAdapter(getSupportFragmentManager(), barcodeValue);
        pager.setOffscreenPageLimit(2);

        pager.setAdapter(adapter);

        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);

    }

    public void setupview(){
        toolbar         = (Toolbar) findViewById(R.id.child_details_activity_toolbar);
        toolbarTitle    = (TextView) findViewById(R.id.vacc_offline_activity_toolbar_title);
        tabs            = (PagerSlidingTabStrip) findViewById(R.id.tabs_vacc_offline);
        pager           = (ViewPager) findViewById(R.id.pager_vacc_offline);
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
