package org.socratic.android.viewmodel;

import android.content.Context;

import org.socratic.android.contract.InvitationsContract;
import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerActivity;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 8/9/17.
 */

@PerActivity
public class InvitationsViewModel extends BaseViewModel<InvitationsContract.View> implements InvitationsContract.ViewModel {

    @Inject
    public InvitationsViewModel(@AppContext Context context) {

    }
}