package mobile.tiis.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import mobile.tiis.app.fragments.DefaultersReportFragment;
import mobile.tiis.app.fragments.HealthFacilityImmunizationCoverageScheduledReportFragment;
import mobile.tiis.app.fragments.HealthFacilityImmunizationCoverageTargetReportFragment;
import mobile.tiis.app.fragments.HealthFacilityVisitsAndVaccinationSummaryFragment;
import mobile.tiis.app.fragments.DropoutReportFragment;
import mobile.tiis.app.fragments.ImmunizationChartFragment;
import mobile.tiis.app.fragments.ImmunizedChildrenFragment;
import mobile.tiis.app.fragments.StockTabFragment;
import mobile.tiis.app.fragments.TabFragment;
import mobile.tiis.app.fragments.VaccinationCoverageFragment;

/**
 *  Created by issymac on 14/12/15.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Vaccination Summary", "Immunization Coverage Report (Scheduled)", "Immunization Coverage Report (Target)", "Defaulters List", "Dropout Report", "Immunized Children", "Immunization Chart", "Stock",  "Vaccination Coverage" };

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
            return HealthFacilityVisitsAndVaccinationSummaryFragment.newInstance(position);
        }
        else if (position == 1){
            return HealthFacilityImmunizationCoverageScheduledReportFragment.newInstance(position);
        }
        else if (position == 2){
            return HealthFacilityImmunizationCoverageTargetReportFragment.newInstance(position);
        }
//        else if (position == 2){
//            return HealthFacilityImmunizationCoverageChartFragment.newInstance(position);
//        }
        else if (position == 3){
            return DefaultersReportFragment.newInstance(position);
        }
        else if (position == 4){
            return DropoutReportFragment.newInstance(position);
        }
        else if (position == 5){
            return ImmunizedChildrenFragment.newInstance();
        }
        else if (position == 6){
            return ImmunizationChartFragment.newInstance();
        }
        else if (position == 7){
            return StockTabFragment.newInstance();
        }
        else if (position == 8){
            return VaccinationCoverageFragment.newInstance();
        }
        else{
            return TabFragment.newInstance(position);
        }
    }

}