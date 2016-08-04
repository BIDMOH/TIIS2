package mobile.tiis.appv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import mobile.tiis.appv2.adapters.StockViewPagerAdapter;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.helpers.Utils;

/**
 * Created by issymac on 10/03/16.
 */

public class StockActivityRevised extends BackboneActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private StockViewPagerAdapter adapter;
    public Toolbar toolbar;
    public TextView toolbarTitle;

    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_stock);
        setUpView();

        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbarTitle.setText(title);

        adapter = new StockViewPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        tabs.setTextColor(Color.WHITE);

        tabs.setViewPager(pager);

    }

    public void setUpView(){
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs_stock);
        pager = (ViewPager) findViewById(R.id.pager_stock);
        toolbar         = (Toolbar) findViewById(R.id.child_details_activity_toolbar);
        toolbarTitle    = (TextView) findViewById(R.id.child_details_activity_toolbar_title);
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
            ImageView wifi_logo = (ImageView)findViewById(R.id.home_wifi_icon);
            if(Utils.isOnline(context)){
                wifi_logo.setImageResource(R.drawable.network_on);
                app.setOnlineStatus(true);
            }
            else{
                wifi_logo.setImageResource(R.drawable.network_off);
                app.setOnlineStatus(false);
            }
        }
    };

}
