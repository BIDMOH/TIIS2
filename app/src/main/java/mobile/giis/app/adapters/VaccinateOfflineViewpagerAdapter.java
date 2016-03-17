package mobile.giis.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mobile.giis.app.fragments.AdministerVaccineOfflineFragment;
import mobile.giis.app.fragments.HealthFacilityBalanceFragment;
import mobile.giis.app.fragments.StockAdjustmentFragment;
import mobile.giis.app.fragments.WeightOflineFragment;

/**
 * Created by issymac on 14/03/16.
 */
public class VaccinateOfflineViewpagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = { "Weight", "Administer Vaccine" };

    String barcodeVessle = "";

    public VaccinateOfflineViewpagerAdapter(FragmentManager fm,String barcode) {
        super(fm);
        barcodeVessle = barcode;
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
                return WeightOflineFragment.newInstance(barcodeVessle);
            default:
                return AdministerVaccineOfflineFragment.newInstance(barcodeVessle);
        }
    }

}
