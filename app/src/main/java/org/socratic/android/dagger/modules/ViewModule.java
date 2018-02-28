package org.socratic.android.dagger.modules;

import android.view.View;


import dagger.Module;

/**
 * Created by williamxu on 7/13/17.
 */

@Module
public class ViewModule {

    private final View view;

    public ViewModule(View view) {
        this.view = view;
    }
}
