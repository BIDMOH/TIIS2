package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import mobile.tiis.appv2.entity.Child;
import mobile.tiis.appv2.fragments.ChildAefiPagerFragment;
import mobile.tiis.appv2.fragments.ChildImmCardPagerFragment;
import mobile.tiis.appv2.fragments.ChildSummaryPagerFragment;
import mobile.tiis.appv2.fragments.ChildVaccinatePagerFragment;
import mobile.tiis.appv2.fragments.ChildWeightPagerFragment;

/**
 * Created by issymac on 25/01/16.
 */
public class ChildDetailsViewPager extends FragmentPagerAdapter {

    private final String[] TITLES = { "Child Summary", "Weight", "Vaccinate Child" , "AEFI", "Immunization Card" };
    private FragmentManager fragmentManager;
    private FragmentTransaction tx;
    private Context context;
    private Child currentChild;
    private String appointmentId;
    private boolean newRegisteredChild;
    public ChildDetailsViewPager(Context ctx, FragmentManager fm, Child currentChild, String appointmentId,boolean newRegisteredChild) {
        super(fm);
        fragmentManager = fm;
        tx              = fragmentManager.beginTransaction();
        context         = ctx;
        this.currentChild    = currentChild;
        this.appointmentId = appointmentId;
        this.newRegisteredChild = newRegisteredChild;
    }

    @Override
    public void destroyItem(View collection, int position, Object o) {
        View view = (View)o;
        ((ViewPager) collection).removeView(view);
        view = null;
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

        if (position == 0){
            tx.addToBackStack("ChildSummaryFragment");
            return ChildSummaryPagerFragment.newInstance(position, currentChild.getId(),newRegisteredChild);
        }
        else if (position == 1){
            tx.addToBackStack(TITLES[position]);
            return ChildWeightPagerFragment.newInstance(currentChild);
        }
        else if (position == 2){
            tx.addToBackStack(TITLES[position]);
            return ChildVaccinatePagerFragment.newInstance(currentChild, appointmentId);
        }
        else if (position == 3){
            tx.addToBackStack(TITLES[position]);
            return ChildAefiPagerFragment.newInstance(currentChild);
        }
        else{
            tx.addToBackStack(TITLES[position]);
            return ChildImmCardPagerFragment.newInstance(currentChild);
        }
    }

}
