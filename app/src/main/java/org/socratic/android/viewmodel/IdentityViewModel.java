package org.socratic.android.viewmodel;

import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;


import org.socratic.android.contract.IdentityContract;
import org.socratic.android.api.IdentityPostRequest;
import org.socratic.android.api.response.IdentityResponse;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.storage.InstallPref;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by williamxu on 7/25/17.
 */

@PerActivity
public class IdentityViewModel extends BaseViewModel<IdentityContract.View> implements IdentityContract.ViewModel {

    private HttpManager httpManager;
    private SharedPreferences sharedPreferences;
    private InstallPref installPref;

    private boolean success;

    @Inject
    public IdentityViewModel(HttpManager httpManager, SharedPreferences sharedPreferences, InstallPref installPref) {
        this.httpManager = httpManager;
        this.sharedPreferences = sharedPreferences;
        this.installPref = installPref;
    }

    @Override
    public void sendIdentity(final String name) {
        getView().navigateToPermissionsScreen();


//        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//            @Override
//            public void onSuccess(Account account) {
//                String phoneNumber = account.getPhoneNumber().toString();
//
//                IdentityPostRequest request = new IdentityPostRequest(name, phoneNumber);
//
//                httpManager.request(request,
//                        new HttpManager.HttpCallback<IdentityResponse>(IdentityResponse.class) {
//                            @Override
//                            public void onStart() {
//                            }
//
//                            @Override
//                            public void onFailure(Request request, IdentityResponse responseParsed, int statusCode, Exception e) {
//                                if (isViewAttached()) {
//                                    getView().showCreatePersonErrorDialog();
//                                }
//                            }
//
//                            @Override
//                            public void onCancel(Request request) {
//                                if (isViewAttached()) {
//                                    getView().showCreatePersonErrorDialog();
//                                }
//                            }
//
//                            @Override
//                            public void onSuccess(Response response, IdentityResponse responseParsed) {
//                                if (responseParsed.getPerson() != null) {
//                                    sharedPreferences.edit().putInt("person_id", responseParsed.getPerson().getID()).apply();
//                                    sharedPreferences.edit().putString("person_name", responseParsed.getPerson().getName()).apply();
//
//                                    if (responseParsed.getPerson().getID() == 0) {
//                                        Crashlytics.log("User saved with Person ID 0 in Identity Screen");
//                                        Crashlytics.log("User device id:" + installPref.getUserId());
//                                        Crashlytics.log("User experience setting:" + sharedPreferences.getString("experience", ""));
//                                        Crashlytics.logException(new Exception("UserZeroIdException"));
//                                    }
//                                }
//                                success = true;
//                            }
//
//                            @Override
//                            public void onFinished() {
//                                if (success && isViewAttached()) {
//                                    getView().navigateToPermissionsScreen();
//                                }
//                            }
//                        });
            }


//            @Override
//            public void onError(AccountKitError accountKitError) {
//                Crashlytics.log("AccountKitError:" + accountKitError.toString());
//                Crashlytics.logException(new Exception("AccountKitErrorException"));
//                getView().showCreatePersonErrorDialog();
//            }
//        });
//    }
}
