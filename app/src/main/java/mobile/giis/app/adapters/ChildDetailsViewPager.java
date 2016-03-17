package mobile.giis.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import mobile.giis.app.HomeActivityRevised;
import mobile.giis.app.fragments.ChildAefiPagerFragment;
import mobile.giis.app.fragments.ChildImmCardPagerFragment;
import mobile.giis.app.fragments.ChildSummaryPagerFragment;
import mobile.giis.app.fragments.ChildVaccinatePagerFragment;
import mobile.giis.app.fragments.ChildWeightPagerFragment;

/**
 * Created by issymac on 25/01/16.
 */
public class ChildDetailsViewPager extends FragmentPagerAdapter {

    private final String[] TITLES = { "Child Summary", "Weight", "Vaccinate Child" , "AEFI", "Immunization Card" };
    FragmentManager fragmentManager;
    FragmentTransaction tx;
    String mValue, Barcode;
    Context context;

    public ChildDetailsViewPager(Context ctx, FragmentManager fm, String value, String barcode) {
        super(fm);
        fragmentManager = fm;
        tx              = fragmentManager.beginTransaction();
        mValue          = value;
        context         = ctx;
        Barcode         = barcode;
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

//        HomeActivityRevised activity = (HomeActivityRevised) context;

        if (position == 0){
            tx.addToBackStack("ChildSummaryFragment");
//            activity.currentFragment = activity.CHILD_SUMMARY_FRAGMENT;
            return ChildSummaryPagerFragment.newInstance(position, mValue);
        }
        else if (position == 1){
            tx.addToBackStack(TITLES[position]);
            return ChildWeightPagerFragment.newInstance(mValue);
        }
        else if (position == 2){
            tx.addToBackStack(TITLES[position]);
            return ChildVaccinatePagerFragment.newInstance(mValue);
        }
        else if (position == 3){
            tx.addToBackStack(TITLES[position]);
            return ChildAefiPagerFragment.newInstance(Barcode);
        }
        else{
            tx.addToBackStack(TITLES[position]);
            return ChildImmCardPagerFragment.newInstance(Barcode);
        }
    }

}
