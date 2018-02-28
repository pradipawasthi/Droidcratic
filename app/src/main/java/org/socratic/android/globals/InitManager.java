package org.socratic.android.globals;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.response.InitResponse;
import org.socratic.android.api.InitGetRequest;
import org.socratic.android.events.InitEvent;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InitManager {

    private static final String TAG = InitManager.class.getSimpleName();

    private HttpManager httpManager;
    private InitResponse mResponseInit;
    private Exception mResponseError;

    @Inject
    public InitManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void startInit(String numSessions) {
        InitGetRequest request = new InitGetRequest(numSessions);

        httpManager.request(request,
                new HttpManager.HttpCallback<InitResponse>(InitResponse.class) {
                    @Override
                    public void onStart() {
                        EventBus.getDefault().post(new InitEvent(
                                InitEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        mResponseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, InitResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onSuccess(Response response, InitResponse responseParsed) {
                        MultiLog.d(TAG, "Success.");
                        MultiLog.d(TAG, String.valueOf(responseParsed));
                        mResponseInit = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        EventBus.getDefault().post(new InitEvent(
                                InitEvent.CONTEXT_FINISHED));
                    }
                });
    }


    public InitResponse getResponse() {
        return mResponseInit;
    }

    public void setResponse(InitResponse response) {
        this.mResponseInit = response;
    }


}