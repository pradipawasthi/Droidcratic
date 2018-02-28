package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by williamxu on 7/25/17.
 */

public interface IdentityContract {

    interface View extends ViewAdapter {

        void showCreatePersonErrorDialog();

        void navigateToPermissionsScreen();
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void sendIdentity(String name);
    }
}
