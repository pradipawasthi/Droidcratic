package org.socratic.android.globals;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.ChatListGetRequest;
import org.socratic.android.api.NewChatPostRequest;
import org.socratic.android.api.model.Contact;
import org.socratic.android.api.model.UserContact;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.api.response.ChatListResponse;
import org.socratic.android.api.response.NewChatResponse;
import org.socratic.android.events.ChatListEvent;
import org.socratic.android.events.NewChatEvent;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 9/28/17.
 */

public class ChatListManager {
    private static final String TAG = ChatListManager.class.getSimpleName();

    private HttpManager httpManager;
    private ChatListResponse responseChatList;
    private NewChatResponse responseNewChat;
    private Exception responseError;
    private boolean isGetChatListRunning;
    private boolean isPostChatRunning;

    @Inject
    public ChatListManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void getChatList(int personID) {
        ChatListGetRequest request = new ChatListGetRequest(personID);

        httpManager.request(request,
                new HttpManager.HttpCallback<ChatListResponse>(ChatListResponse.class) {
                    @Override
                    public void onStart() {
                        isGetChatListRunning = true;
                        EventBus.getDefault().post(new ChatListEvent(
                                ChatListEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, ChatListResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, ChatListResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                        responseChatList = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        isGetChatListRunning = false;

                        EventBus.getDefault().post(new ChatListEvent(
                                ChatListEvent.CONTEXT_FINISHED));
                    }
                });
    }

    public boolean isGetChatListRunning() {
        return isGetChatListRunning;
    }

    public ChatListResponse getChatListResponse() {
        return responseChatList;
    }

    public void setChatListResponse(ChatListResponse response) {
        this.responseChatList = response;
    }

    public void createChat(int personID, final UserContact contact) {
        NewChatPostRequest request = new NewChatPostRequest(personID, contact);

        httpManager.request(request,
                new HttpManager.HttpCallback<NewChatResponse>(NewChatResponse.class) {
                    @Override
                    public void onStart() {
                        isPostChatRunning = true;

                        EventBus.getDefault().post(new NewChatEvent(
                                NewChatEvent.CONTEXT_STARTING,
                                        contact.getName()));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, NewChatResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, NewChatResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                        responseNewChat = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        isPostChatRunning = false;

                        EventBus.getDefault().post(new NewChatEvent(
                                NewChatEvent.CONTEXT_FINISHED,
                                contact.getName()));
                    }
                });
    }

    public boolean isPostChatRunning() {
        return isPostChatRunning;
    }

    public NewChatResponse getNewChatResponse() {
        return responseNewChat;
    }

    public void setResponse(NewChatResponse response) {
        this.responseNewChat = response;
    }

}
