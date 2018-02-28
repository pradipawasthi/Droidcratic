package org.socratic.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.socratic.android.activities.CameraActivity;
import org.socratic.android.fragments.ChatFragment;
import org.socratic.android.fragments.GroupsFragment;

/**
 * Created by williamxu on 8/4/17.
 */

public class NavPagerAdapter extends FragmentPagerAdapter {

    public static final int NAV_CHAT = 0;
    public static final int NAV_CAMERA = 1;
    public static final int NAV_GROUPS = 2;

    private boolean isSocialUser;

    public NavPagerAdapter(FragmentManager fm, boolean isSocialUser) {
        super(fm);

        this.isSocialUser = isSocialUser;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case NAV_CHAT:
                if (!isSocialUser) {
                    fragment = CameraActivity.PlaceholderFragment.newInstance();
                } else {
                    fragment = ChatFragment.newInstance();
                }
                break;
            case NAV_CAMERA:
                fragment = CameraActivity.PlaceholderFragment.newInstance();
                break;
            case NAV_GROUPS:
                fragment = GroupsFragment.newInstance();
                break;
        }

        return fragment;
    }


    @Override
    public int getCount() {

        //return 2 for now before groups is implemented
        return 2;
    }
}
