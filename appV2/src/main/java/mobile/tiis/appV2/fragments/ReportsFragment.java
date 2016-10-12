package mobile.tiis.appv2.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import mobile.tiis.appv2.R;
import mobile.tiis.appv2.adapters.ViewPagerAdapter;

/**
 * Created by issymac on 12/12/15.
 */
public class ReportsFragment extends android.support.v4.app.Fragment{

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private ViewPagerAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_report, null);
        setUpView(root);

        adapter = new ViewPagerAdapter(this.getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabs.setViewPager(pager);


        return root;
    }

    public void setUpView(View v){
        tabs = (PagerSlidingTabStrip)v. findViewById(R.id.tabs);
        pager = (ViewPager)v. findViewById(R.id.pager);

    }

}