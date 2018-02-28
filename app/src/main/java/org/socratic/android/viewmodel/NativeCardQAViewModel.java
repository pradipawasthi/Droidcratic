package org.socratic.android.viewmodel;

import org.socratic.android.contract.NativeCardQAContract;
import org.socratic.android.dagger.scopes.PerFragment;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/14/17.
 */

@PerFragment
public class NativeCardQAViewModel extends BaseViewModel<NativeCardQAContract.View> implements NativeCardQAContract.ViewModel {

    @Inject
    public NativeCardQAViewModel() {

    }
}
