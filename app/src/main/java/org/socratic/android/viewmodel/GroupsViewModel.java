package org.socratic.android.viewmodel;

import org.socratic.android.contract.GroupsContract;
import org.socratic.android.dagger.scopes.PerFragment;

import javax.inject.Inject;

/**
 * Created by williamxu on 8/4/17.
 */

@PerFragment
public class GroupsViewModel extends BaseViewModel<GroupsContract.View> implements GroupsContract.ViewModel {

    @Inject
    public GroupsViewModel() {

    }
}
