package mobile.tiis.app.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.StockViewPagerAdapter;

/**
 * Created by issymac on 16/12/15.
 */
public class StockFragment extends android.support.v4.app.Fragment{

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private StockViewPagerAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stock, null);
        setUpView(root);

        adapter = new StockViewPagerAdapter(this.getActivity().getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        return root;
    }

    public void setUpView(View v){
        tabs = (PagerSlidingTabStrip)v. findViewById(R.id.tabs_stock);
        pager = (ViewPager)v. findViewById(R.id.pager_stock);
    }

}