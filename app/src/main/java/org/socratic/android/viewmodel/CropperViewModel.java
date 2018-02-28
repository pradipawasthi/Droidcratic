package org.socratic.android.viewmodel;

import android.content.Context;

import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.contract.CropperContract;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/12/17.
 */

@PerActivity
public class CropperViewModel extends BaseViewModel<CropperContract.View> implements CropperContract.ViewModel {

    @Inject
    public CropperViewModel(@AppContext Context context) {

    }
}
