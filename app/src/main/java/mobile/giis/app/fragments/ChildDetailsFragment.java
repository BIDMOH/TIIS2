package mobile.giis.app.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import mobile.giis.app.HomeActivityRevised;
import mobile.giis.app.R;
import mobile.giis.app.adapters.ChildDetailsViewPager;
import mobile.giis.app.adapters.StockViewPagerAdapter;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;

/**
 * Created by issymac on 25/01/16.
 */

public class ChildDetailsFragment  extends android.support.v4.app.Fragment {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private ChildDetailsViewPager adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_details, null);
        setUpView(root);

        int currentTab = getArguments().getInt("current");

        HomeActivityRevised activity = (HomeActivityRevised) ChildDetailsFragment.this.getActivity();

        //Retrieve the value
        String childBarcode = getArguments().getString("barcode");

        BackboneApplication app = (BackboneApplication) ChildDetailsFragment.this.getActivity().getApplication();
        DatabaseHandler mydb = app.getDatabaseInstance();
        Cursor cursor = null;
        cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE BARCODE_ID=?", new String[]{childBarcode});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME1))+" "+
            cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.FIRSTNAME2))+ " "+
            cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.LASTNAME1));

            activity.toolbarTitle.setText(name);

        }

//        adapter = new ChildDetailsViewPager(ChildDetailsFragment.this.getActivity(), this.getActivity().getSupportFragmentManager(), childBarcode);
        pager.setOffscreenPageLimit(1);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        if(!(currentTab > 1)){
            pager.setCurrentItem(currentTab);
        }

        pager.setAdapter(adapter);

        tabs.setViewPager(pager);

        return root;
    }
    
    public void setUpView(View v){
        tabs = (PagerSlidingTabStrip)v. findViewById(R.id.tabs_stock);
        pager = (ViewPager)v. findViewById(R.id.pager_stock);

    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentTransaction tx  = ChildDetailsFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
        tx.remove(ChildDetailsFragment.this).commit();
    }
}
