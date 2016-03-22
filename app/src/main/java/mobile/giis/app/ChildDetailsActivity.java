package mobile.giis.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import mobile.giis.app.SubClassed.BackHandledFragment;
import mobile.giis.app.adapters.ChildDetailsViewPager;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.helpers.Utils;

/**
 * Created by issymac on 25/02/16.
 */
public class ChildDetailsActivity extends BackboneActivity implements BackHandledFragment.BackHandlerInterface {

    private PagerSlidingTabStrip tabs;

    private ViewPager pager;

    private ChildDetailsViewPager adapter;

    private BackHandledFragment selectedFragment;

    public String handlerBarcode = "";

    public Toolbar toolbar;

    public static TextView toolbarTitle;

    String value = "";

    int currentPagerPage = 0;

    private DatabaseHandler mydb;

    boolean barcodeNull = true;

    BroadcastReceiver status_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackboneApplication app = (BackboneApplication) getApplication();
            ImageView wifi_logo = (ImageView)findViewById(R.id.details_wifi_icon);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_details_activity);
        setUpView();

        final BackboneApplication app = (BackboneApplication) getApplication();
        Bundle extras = getIntent().getExtras();
        mydb = app.getDatabaseInstance();

        Log.d("currentpage", "Here at Details");
        if (extras != null) {
            value = extras.getString(BackboneApplication.CHILD_ID);
            currentPagerPage    = extras.getInt("current");
            handlerBarcode      = extras.getString("barcode");
            Log.d("currentpage", currentPagerPage + "");

            if (value == null || value.equalsIgnoreCase("")) {
                if (extras.getString("barcode") != null) {
                    value = extras.getString("barcode");
                    handlerBarcode  = value;
                    Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                            new String[]{String.valueOf(value)});
                    if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                        getChildIdCursor.moveToFirst();
                        value = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
                    }else {
                        toastMessage(getString(R.string.empty_child_id));
                        finish();
                    }
                }else {
                    toastMessage(getString(R.string.empty_barcode));
                    finish();
                }
            }

        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

        }

        DatabaseHandler mydb = app.getDatabaseInstance();
        Cursor cursor = null;
        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{handlerBarcode});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1))+" "+
                    cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2))+ " "+
                    cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1));

            toolbarTitle.setText(name);

        }

        adapter = new ChildDetailsViewPager(this, getSupportFragmentManager(), value, handlerBarcode);
        pager.setOffscreenPageLimit(1);

        pager.setAdapter(adapter);

        tabs.setTextColor(Color.WHITE);
        tabs.setViewPager(pager);

        pager.setCurrentItem(currentPagerPage);

    }

    public static void changeTitle(String title){
        toolbarTitle.setText(title);
    }

    public void setUpView(){
        toolbar         = (Toolbar) findViewById(R.id.child_details_activity_toolbar);
        toolbarTitle    = (TextView) findViewById(R.id.child_details_activity_toolbar_title);
        tabs            = (PagerSlidingTabStrip) findViewById(R.id.tabs_stock);
        pager           = (ViewPager) findViewById(R.id.pager_stock);
    }

    @Override
    public void onBackPressed() {
        if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

}
