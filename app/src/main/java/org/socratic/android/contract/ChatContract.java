package org.socratic.android.contract;

import org.json.JSONArray;
import org.socratic.android.api.model.UserContact;
import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by williamxu on 8/4/17.
 */

public interface ChatContract {

    interface View extends ViewAdapter {

        void setupChatList();
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void getChatList();

        void createChat(UserContact userContact);

        void sendContacts(JSONArray contactsJSON, String deviceName);
    }
}
