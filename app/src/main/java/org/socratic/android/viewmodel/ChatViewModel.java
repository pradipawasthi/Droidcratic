package org.socratic.android.viewmodel;

import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.socratic.android.contract.ChatContract;
import org.socratic.android.api.ContactsPostRequest;
import org.socratic.android.api.model.UserContact;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.dagger.scopes.PerFragment;
import org.socratic.android.globals.ChatListManager;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.storage.InstallPref;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by williamxu on 8/4/17.
 */

@PerFragment
public class ChatViewModel extends BaseViewModel<ChatContract.View> implements ChatContract.ViewModel {

    private static final String TAG = ChatViewModel.class.getSimpleName();

    private ChatListManager chatListManager;
    private HttpManager httpManager;
    private InstallPref installPref;
    private SharedPreferences sharedPreferences;
    private int personID;

    @Inject
    ChatViewModel(ChatListManager chatListManager, HttpManager httpManager, InstallPref installPref, SharedPreferences sharedPreferences) {
        this.chatListManager = chatListManager;
        this.httpManager = httpManager;
        this.installPref = installPref;
        this.sharedPreferences = sharedPreferences;

        personID = sharedPreferences.getInt("person_id", 0);
    }

    @Override
    public void getChatList() {

        if (personID == 0) {
            Crashlytics.log("User with Person ID 0 called get chat");
            Crashlytics.log("User device id:" + installPref.getUserId());
            Crashlytics.log("User experience setting:" + sharedPreferences.getString("experience", ""));
            Crashlytics.logException(new Exception("UserZeroIdException"));
        }

        if (!chatListManager.isGetChatListRunning()) {
            chatListManager.getChatList(personID);
        }
    }

    @Override
    public void createChat(UserContact userContact) {
        if (!chatListManager.isPostChatRunning()) {
            chatListManager.createChat(personID, userContact);
        }
    }

    @Override
    public void sendContacts(JSONArray contactsJSON, String deviceName) {
        ContactsPostRequest request = new ContactsPostRequest(contactsJSON, deviceName, installPref.getUserId());
        httpManager.request(request,
                new HttpManager.HttpCallback<BaseResponse>(BaseResponse.class) {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure(Request request, BaseResponse responseParsed, int statusCode, Exception e) {

                    }

                    @Override
                    public void onCancel(Request request) {

                    }

                    @Override
                    public void onSuccess(Response response, BaseResponse responseParsed) {
                    }

                    @Override
                    public void onFinished() {
                        if (isViewAttached()) {
                            getView().setupChatList();
                        }
                    }
                });
    }
}
