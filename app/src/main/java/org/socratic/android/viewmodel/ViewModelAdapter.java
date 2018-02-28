package org.socratic.android.viewmodel;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.socratic.android.contract.ViewAdapter;

/**
 * Created by williamxu on 7/12/17.
 */

public interface ViewModelAdapter<V extends ViewAdapter> extends Observable {

    void attachView(V view, Bundle savedInstanceState);
    void detachView();

    void saveInstanceState(@NonNull Bundle outState);
}
