package org.socratic.android.dagger.modules;

import android.support.v7.widget.RecyclerView;

import dagger.Module;

/**
 * Created by williamxu on 9/18/17.
 */

@Module
public class ViewHolderModule {

    private final RecyclerView.Adapter adapter;

    public ViewHolderModule(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }
}
