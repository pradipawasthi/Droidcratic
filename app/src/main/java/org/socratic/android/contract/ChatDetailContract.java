package org.socratic.android.contract;

import org.socratic.android.api.model.Message;
import org.socratic.android.viewmodel.ViewModelAdapter;

/**
 * Created by jessicaweinberg on 10/2/17.
 */

public interface ChatDetailContract {

    interface View extends ViewAdapter {

        void addMessage(Message message);
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void getAllMessages(int channelID);

        void createTextMessage(int channelID, CharSequence input);

        void getMoreMessages(int channelID, String lastMessageTime);

        void createImageMessage(int channelID, byte[] imageData, String imageContent);

        void updateMessage(int senderID, int channelID);
    }
}