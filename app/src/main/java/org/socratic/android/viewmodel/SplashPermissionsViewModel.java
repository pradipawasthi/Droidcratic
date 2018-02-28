package org.socratic.android.viewmodel;

import org.json.JSONArray;
import org.socratic.android.contract.SplashPermissionsContract;
import org.socratic.android.api.ContactsPostRequest;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.storage.InstallPref;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by williamxu on 7/13/17.
 */

@PerActivity
public class SplashPermissionsViewModel extends BaseViewModel<SplashPermissionsContract.View> implements SplashPermissionsContract.ViewModel {

    private HttpManager httpManager;
    private InstallPref installPref;

    @Inject
    public SplashPermissionsViewModel(HttpManager httpManager, InstallPref installPref) {
        this.httpManager = httpManager;
        this.installPref = installPref;
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
                    }
                });
    }
}
