package tw.edu.nsysu.morsenser_morfeeling;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import tw.edu.nsysu.fragment.CircleHeartbeat;
import tw.edu.nsysu.fragment.CircleSpo2;

public class ViewPagerAdapterSpO2 extends FragmentPagerAdapter {
    CharSequence Titles[];

    public ViewPagerAdapterSpO2(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        if(position == 0) // if the position is 0 we are returning the First tab
            return new CircleSpo2();
        else
            return new CircleHeartbeat();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
    @Override
    public int getCount() {
        return 2;
    }
}
