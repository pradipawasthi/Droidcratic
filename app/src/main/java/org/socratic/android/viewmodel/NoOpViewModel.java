package org.socratic.android.viewmodel;

import android.databinding.BaseObservable;
import android.os.Bundle;
import org.socratic.android.contract.ViewAdapter;

import javax.inject.Inject;

/**
 * Stub class for used as a stand-in view model for Activities/Fragments that don't need a functional one
 */
public final class NoOpViewModel extends BaseObservable implements ViewModelAdapter<ViewAdapter> {

    @Inject
    public NoOpViewModel() { }

    @Override
    public void attachView(ViewAdapter viewAdapter, Bundle savedInstanceState) { }

    @Override
    public void saveInstanceState(Bundle outState) { }

    @Override
    public void detachView() { }

}