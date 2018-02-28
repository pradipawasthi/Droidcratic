package org.socratic.android.globals;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.UpdateTokenPutRequest;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.events.UpdateTokenEvent;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class TokenManager {
    private static final String TAG = TokenManager.class.getSimpleName();

    private HttpManager httpManager;
    private Exception responseError;

    @Inject
    public TokenManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void startSendToken(String firebaseRegistrationToken) {
        UpdateTokenPutRequest request = new UpdateTokenPutRequest(firebaseRegistrationToken);

        httpManager.request(request,
                new HttpManager.HttpCallback<BaseResponse>(BaseResponse.class) {
                    @Override
                    public void onStart() {
                        EventBus.getDefault().post(new UpdateTokenEvent(
                                UpdateTokenEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        responseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, BaseResponse responseParsed, int statusCode, Exception e) {
                        Log.d(TAG, request.toString());

                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, BaseResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                    }

                    @Override
                    public void onFinished() {
                        EventBus.getDefault().post(new UpdateTokenEvent(
                                UpdateTokenEvent.CONTEXT_FINISHED));
                    }
                });
    }
}
