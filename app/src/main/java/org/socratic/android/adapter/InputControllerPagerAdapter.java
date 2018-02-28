package org.socratic.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.socratic.android.activities.CameraActivity;

/**
 * Created by williamxu on 9/11/17.
 */

public class InputControllerPagerAdapter extends FragmentPagerAdapter {

    public InputControllerPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return CameraActivity.PlaceholderFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CAMERA";
            case 1:
                return "ABC";
        }
        return null;
    }
}
