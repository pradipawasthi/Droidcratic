package org.socratic.android.viewmodel;

import org.socratic.android.contract.NativeCardVideoContract;
import org.socratic.android.dagger.scopes.PerFragment;

import javax.inject.Inject;

/**
 * Created by byfieldj on 9/21/17.
 */

@PerFragment
public class NativeCardVideoViewModel extends BaseViewModel<NativeCardVideoContract.View> implements NativeCardVideoContract.ViewModel {

    @Inject
    public NativeCardVideoViewModel() {

    }
}
