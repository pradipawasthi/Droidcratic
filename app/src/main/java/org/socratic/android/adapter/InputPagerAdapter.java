package org.socratic.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.socratic.android.activities.CameraActivity;
import org.socratic.android.fragments.TextSearchFragment;

/**
 * Created by williamxu on 8/4/17.
 */

public class InputPagerAdapter extends FragmentPagerAdapter {

    public static final int CAMERA_SCREEN = 0;
    public static final int TEXT_SEARCH_SCREEN = 1;
    private boolean locked;

    public InputPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        Fragment fragment = null;

        if (position == CAMERA_SCREEN) {
            fragment = CameraActivity.PlaceholderFragment.newInstance();
        } else if (position == TEXT_SEARCH_SCREEN) {
            fragment = TextSearchFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages unless in locked state
        if (locked) {
            return 1;
        } else {
            return 2;
        }
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

    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyDataSetChanged();
    }

}
