package org.socratic.android.viewmodel;

import android.content.Context;

import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.contract.CameraContract;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/11/17.
 */

@PerActivity
public class CameraViewModel extends BaseViewModel<CameraContract.View> implements CameraContract.ViewModel {

    @Inject
    public CameraViewModel(@AppContext Context context) {

    }
}
