package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by byfieldj on 8/28/17.
 */

public interface MathCardContract {

    interface View extends ViewAdapter{

    }

    interface ViewModel extends ViewModelAdapter<View> {

    }
}
