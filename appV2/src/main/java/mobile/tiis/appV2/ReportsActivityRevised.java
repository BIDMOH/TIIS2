package mobile.tiis.appv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import mobile.tiis.appv2.adapters.ViewPagerAdapter;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.helpers.Utils;

/**
 * Created by issymac on 11/03/16.
 */
public class ReportsActivityRevised extends BackboneActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private ViewPagerAdapter adapter;public Toolbar toolbar;
    private String title = "";
    public TextView toolbarTitle;
    ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_report);
        setUpView();

        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbarTitle.setText(title);

        tabs.setTextColor(Color.WHITE);

        new stallRendering().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    class stallRendering extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadingBar.setVisibility(View.GONE);
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            pager.setOffscreenPageLimit(10);
            pager.setAdapter(adapter);
            tabs.setViewPager(pager);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setUpView(){
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        pager = (ViewPager) findViewById(R.id.pager);
        toolbar         = (Toolbar) findViewById(R.id.reports_activity_toolbar);
        toolbarTitle    = (TextView) findViewById(R.id.reports_activity_toolbar_title);
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
    public void onDestroy(){
        super.onDestroy();
    }

    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.report_wifi_icon);
            if(Utils.isOnline(context)){
//                wifi_logo.setBackgroundColor(0xff00ff00);
                wifi_logo.setImageResource(R.drawable.network_on);
                app.setOnlineStatus(true);
            }
            else{
//                wifi_logo.setBackgroundColor(0xffff0000);
                wifi_logo.setImageResource(R.drawable.network_off);
                app.setOnlineStatus(false);
            }
        }
    };

}
