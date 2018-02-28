package org.socratic.android.viewmodel;

import android.databinding.BaseObservable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.socratic.android.contract.ViewAdapter;

/**
 * Created by williamxu on 7/11/17.
 *
 * Base class for ViewModel classes that provides reference to the view adapter.
 *
 * Handles state changes and state restoration.
 */

public abstract class BaseViewModel<V extends ViewAdapter> extends BaseObservable implements ViewModelAdapter<V> {

    private V viewAdapter;

    @Override
    @CallSuper
    public void attachView(V viewAdapter, @Nullable Bundle savedInstanceState) {
        this.viewAdapter = viewAdapter;
        if(savedInstanceState != null) { restoreInstanceState(savedInstanceState); }
    }

    @Override
    @CallSuper
    public void detachView() {
        viewAdapter = null;
    }

    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) { }

    public void saveInstanceState(Bundle outState) { }

    public final boolean isViewAttached() {
        return viewAdapter != null;
    }

    public final V getView() {
        return viewAdapter;
    }
}
