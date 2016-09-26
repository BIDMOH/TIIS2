package mobile.tiis.appV2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mobile.tiis.appV2.fragments.HealthFacilityBalanceFragment;
import mobile.tiis.appV2.fragments.StockAdjustmentFragment;

/**
 *  Created by issymac on 16/12/15.
 */

public class StockViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Stock Adjustments", "Health Facility Balance" };

    public StockViewPagerAdapter(FragmentManager fm) {
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
        switch (position){
            case 0:
                return StockAdjustmentFragment.newInstance();
            default:
                return HealthFacilityBalanceFragment.newInstance();
        }
    }

}
