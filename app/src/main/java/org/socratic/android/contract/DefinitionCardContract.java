package org.socratic.android.contract;

import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by byfieldj on 10/3/17.
 */

public interface DefinitionCardContract {

   interface View extends ViewAdapter{

   }

   interface ViewModel extends ViewModelAdapter<View> {

   }
}
