package mobile.giis.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.Locale;

import mobile.giis.app.fragments.ImmunizedChildrenFragment;
import mobile.giis.app.fragments.StockTabFragment;
import mobile.giis.app.fragments.TabFragment;
import mobile.giis.app.fragments.VaccinationCoverageFragment;

/**
 *  Created by issymac on 14/12/15.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Immunized Children", "Vaccination Coverage", "Stock" };

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("ChartsLog", position+"");
        if (position == 0){
            return ImmunizedChildrenFragment.newInstance();
        }
        else if (position == 1){
            return VaccinationCoverageFragment.newInstance();
        }
        else if (position == 2){
            return StockTabFragment.newInstance();
        }
        else{
            return TabFragment.newInstance(position);
        }
    }

}