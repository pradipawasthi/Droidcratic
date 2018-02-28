package org.socratic.android.viewmodel;

import android.content.SharedPreferences;

import org.socratic.android.contract.ChatDetailContract;
import org.socratic.android.api.model.Author;
import org.socratic.android.api.model.Message;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.globals.MessagesManager;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 10/2/17.
 */

@PerActivity
public class ChatDetailViewModel extends BaseViewModel<ChatDetailContract.View> implements ChatDetailContract.ViewModel {

    private MessagesManager messagesManager;
    private int personID;
    private String personName;

    @Inject
    public ChatDetailViewModel(SharedPreferences sharedPreferences, MessagesManager messagesManager) {
        this.messagesManager = messagesManager;

        personID = sharedPreferences.getInt("person_id", 0);
        personName = sharedPreferences.getString("person_name", "");
    }

    @Override
    public void getAllMessages(int channelID) {
        if (!messagesManager.isGetAllMessagesRunning()) {
            messagesManager.getAllMessages(personID, channelID, null, null);
        }
    }

    @Override
    public void createTextMessage(int channelID, CharSequence input) {
        if (!messagesManager.isCreateMessageRunning()) {
            String content = String.valueOf(input);

            Author author = new Author();
            author.setName(personName);
            author.setID(String.valueOf(personID));

            Message message = new Message();
            message.setAuthor(author);
            message.setContent(content);
            message.setContentType("text");

            Date currentTime = Calendar.getInstance().getTime();
            java.sql.Date date = new java.sql.Date(currentTime.getTime());

            message.setCreatedAtDate(date);

            getView().addMessage(message);

            messagesManager.createMessage(personID, channelID, message, "text", null);
        }
    }

    @Override
    public void getMoreMessages(int channelID, String lastMessageTime) {
        if (!messagesManager.isGetAllMessagesRunning()) {
            messagesManager.getAllMessages(personID, channelID, lastMessageTime, null);
        }
    }

    @Override
    public void createImageMessage(int channelID, byte[] imageData, String imageContent) {
        if (!messagesManager.isCreateMessageRunning()) {
            Author author = new Author();
            author.setName(personName);
            author.setID(String.valueOf(personID));

            Message message = new Message();
            message.setAuthor(author);
            message.setContent(imageContent);
            message.setContentType("image");

            Date currentTime = Calendar.getInstance().getTime();
            java.sql.Date date = new java.sql.Date(currentTime.getTime());

            message.setCreatedAtDate(date);

            getView().addMessage(message);

            messagesManager.createMessage(personID, channelID, message, "image", imageData);
        }

    }

    @Override
    public void updateMessage(int senderID, int channelID) {
        messagesManager.updateMessage(senderID, channelID);
        messagesManager.updateMessage(personID, channelID);
    }
}
