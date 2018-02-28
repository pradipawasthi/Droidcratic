package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by byfieldj on 10/6/17.
 */

public interface ExplainerCardContract {

    interface View extends ViewAdapter{

    }

    interface ViewModel extends ViewModelAdapter<View> {

    }
}
