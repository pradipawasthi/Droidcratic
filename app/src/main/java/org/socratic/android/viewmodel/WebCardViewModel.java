package org.socratic.android.viewmodel;

import org.socratic.android.contract.WebCardContract;
import org.socratic.android.dagger.scopes.PerFragment;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/14/17.
 */

@PerFragment
public class WebCardViewModel extends BaseViewModel<WebCardContract.View> implements WebCardContract.ViewModel {

    @Inject
    public WebCardViewModel() {

    }
}
