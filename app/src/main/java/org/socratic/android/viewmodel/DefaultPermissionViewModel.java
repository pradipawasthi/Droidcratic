package org.socratic.android.viewmodel;

import org.socratic.android.contract.DefaultPermissionContract;
import org.socratic.android.dagger.scopes.PerActivity;

import javax.inject.Inject;

/**
 * Created by williamxu on 8/21/17.
 */

@PerActivity
public class DefaultPermissionViewModel extends BaseViewModel<DefaultPermissionContract.View>
    implements DefaultPermissionContract.ViewModel {

    @Inject
    public DefaultPermissionViewModel() {

    }
}
