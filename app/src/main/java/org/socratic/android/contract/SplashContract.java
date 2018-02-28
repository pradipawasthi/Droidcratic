package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by jessicaweinberg on 7/21/17.
 */

public interface SplashContract {

    interface View extends ViewAdapter {

        void navigateToNextScreen();
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void callInit();
    }
}
