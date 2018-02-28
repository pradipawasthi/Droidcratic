package org.socratic.android.viewmodel;

import android.content.Context;

import org.socratic.android.contract.ResultsContract;
import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerActivity;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/12/17.
 */

@PerActivity
public class ResultsViewModel extends BaseViewModel<ResultsContract.View> implements ResultsContract.ViewModel {

    @Inject
    public ResultsViewModel(@AppContext Context context) {

    }
}
