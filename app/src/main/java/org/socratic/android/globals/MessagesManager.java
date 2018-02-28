package org.socratic.android.globals;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.api.AllMessagesGetRequest;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.NewMessagePostRequest;
import org.socratic.android.api.UpdateMessagePutRequest;
import org.socratic.android.api.model.Message;
import org.socratic.android.api.response.AllMessagesResponse;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.api.response.NewMessageResponse;
import org.socratic.android.events.AllMessagesEvent;
import org.socratic.android.events.NewMessageEvent;
import org.socratic.android.events.UpdateMessageEvent;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class MessagesManager {
    private static final String TAG = MessagesManager.class.getSimpleName();

    private HttpManager httpManager;
    private AllMessagesResponse responseAllMessages;
    private NewMessageResponse responseNewMessage;
    private Exception responseError;
    private boolean isGetAllMessagesRunning;
    private boolean isCreateMessageRunning;
    private boolean isUpdateMessageRunning;

    @Inject
    public MessagesManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void getAllMessages(int personID, int channelID, String before, String after) {
        AllMessagesGetRequest request = new AllMessagesGetRequest(personID, channelID, before, after);

        httpManager.request(request,
                new HttpManager.HttpCallback<AllMessagesResponse>(AllMessagesResponse.class) {
                    @Override
                    public void onStart() {
                        isGetAllMessagesRunning = true;
                        EventBus.getDefault().post(new AllMessagesEvent(
                                AllMessagesEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, AllMessagesResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, AllMessagesResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                        responseAllMessages = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        isGetAllMessagesRunning = false;
                        EventBus.getDefault().post(new AllMessagesEvent(
                                AllMessagesEvent.CONTEXT_FINISHED));
                    }
                });
    }

    public boolean isGetAllMessagesRunning() { return isGetAllMessagesRunning; }
    
    public AllMessagesResponse getAllMessagesResponse() {
        return responseAllMessages;
    }

    public void setAllMessagesResponse(AllMessagesResponse response) {
        this.responseAllMessages = response;
    }

    public void createMessage(int personID, int channelID, final Message message, String contentType, byte[] imageData) {

        String content;
        if (contentType.equals("image")) {
            content = "";
        } else {
            content = message.getText();
        }

        NewMessagePostRequest request = new NewMessagePostRequest(personID, channelID, content, contentType, imageData);

        httpManager.request(request,
                new HttpManager.HttpCallback<NewMessageResponse>(NewMessageResponse.class) {
                    @Override
                    public void onStart() {
                        isCreateMessageRunning = true;
                        EventBus.getDefault().post(new NewMessageEvent(
                                NewMessageEvent.CONTEXT_STARTING, message));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, NewMessageResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, NewMessageResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                        responseNewMessage = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        isCreateMessageRunning = false;
                        EventBus.getDefault().post(new NewMessageEvent(
                                NewMessageEvent.CONTEXT_FINISHED, message));
                    }
                });
    }
    
    public boolean isCreateMessageRunning() { return isCreateMessageRunning; }

    public NewMessageResponse getNewMessageResponse() {
        return responseNewMessage;
    }

    public void setNewMessageResponse(NewMessageResponse response) {
        this.responseNewMessage = response;
    }

    public void updateMessage(int personID, int channelID) {
        UpdateMessagePutRequest request = new UpdateMessagePutRequest(personID, channelID);

        httpManager.request(request,
                new HttpManager.HttpCallback<BaseResponse>(BaseResponse.class) {
                    @Override
                    public void onStart() {
                        isUpdateMessageRunning = true;
                        EventBus.getDefault().post(new UpdateMessageEvent(
                                UpdateMessageEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, BaseResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, BaseResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");

                    }

                    @Override
                    public void onFinished() {
                        isUpdateMessageRunning = false;
                        EventBus.getDefault().post(new UpdateMessageEvent(
                                UpdateMessageEvent.CONTEXT_FINISHED));
                    }
                });
    }

    public boolean isUpdateMessageRunning() { return isUpdateMessageRunning; }


}