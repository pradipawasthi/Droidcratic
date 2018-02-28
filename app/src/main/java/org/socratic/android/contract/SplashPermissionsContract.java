package org.socratic.android.contract;

import org.json.JSONArray;
import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by williamxu on 7/13/17.
 */

public interface SplashPermissionsContract {

    interface View extends ViewAdapter {

    }

    interface ViewModel extends ViewModelAdapter<View> {

        void sendContacts(JSONArray contactsJSON, String deviceName);
    }
}
