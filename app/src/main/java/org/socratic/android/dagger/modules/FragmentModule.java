package org.socratic.android.dagger.modules;

import android.support.v4.app.Fragment;

import dagger.Module;

/**
 * Created by williamxu on 7/12/17.
 */

@Module
public class FragmentModule {

    private final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }
}
