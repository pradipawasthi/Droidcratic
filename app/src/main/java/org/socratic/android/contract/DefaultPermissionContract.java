package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by williamxu on 8/21/17.
 */

public interface DefaultPermissionContract {

    interface View extends ViewAdapter {

    }

    interface ViewModel extends ViewModelAdapter<View> {

    }
}
