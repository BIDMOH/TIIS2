package mobile.giis.app.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import mobile.giis.app.R;
import mobile.giis.app.adapters.ViewPagerAdapter;

/**
 * Created by issymac on 16/12/15.
 */
public class SettingsFragment extends android.support.v4.app.Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);
        setUpView(root);

        return root;
    }

    public void setUpView(View v){

    }

}