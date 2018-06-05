package org.socratic.android.globals;

import org.socratic.android.api.CanceledException;
import org.socratic.android.api.request.InAppMessageDismissedPostRequest;
import org.socratic.android.api.request.InAppMessageSeenPostRequest;
import org.socratic.android.api.request.InAppMessageTappedPostRequest;
import org.socratic.android.api.response.InAppMessageDismissedResponse;
import org.socratic.android.api.response.InAppMessageSeenResponse;
import org.socratic.android.api.response.InAppMessageTappedResponse;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessageManager {

    private static final String TAG = InAppMessageManager.class.getSimpleName();

    private HttpManager httpManager;
    private InAppMessageSeenResponse mResponseInAppMessageSeen;
    private InAppMessageDismissedResponse mResponseInAppMessageDismissed;
    private InAppMessageTappedResponse mResponseInAppMessageTapped;
    private Exception mResponseError;

    @Inject
    public InAppMessageManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void postInAppMessageSeen(String id) {
        InAppMessageSeenPostRequest request = new InAppMessageSeenPostRequest(id);
        httpManager.request(request, new HttpManager.HttpCallback<InAppMessageSeenResponse>(InAppMessageSeenResponse.class) {
            @Override
            public void onStart(){

            }

            @Override
            public void onCancel(Request request) {
                mResponseError = new CanceledException();
            }

            @Override
            public void onFailure(Request request, InAppMessageSeenResponse responseParsed, int statusCode, Exception e) {
                String.valueOf(statusCode);
            }

            @Override
            public void onSuccess(Response response, InAppMessageSeenResponse responseParsed) {
                mResponseInAppMessageSeen = responseParsed;
            }

            @Override
            public void onFinished() {}
        });
    }

    public void postInAppMessageDismissed(String id) {
        InAppMessageDismissedPostRequest request = new InAppMessageDismissedPostRequest(id);
        httpManager.request(request, new HttpManager.HttpCallback<InAppMessageDismissedResponse>(InAppMessageDismissedResponse.class) {
            @Override
            public void onStart() {}

            @Override
            public void onCancel(Request request) {
                mResponseError = new CanceledException();
            }

            @Override
            public void onFailure(Request request, InAppMessageDismissedResponse responseParsed, int statusCode, Exception e) {
                String.valueOf(statusCode);
            }

            @Override
            public void onSuccess(Response response, InAppMessageDismissedResponse responseParsed) {
                mResponseInAppMessageDismissed = responseParsed;
            }

            @Override
            public void onFinished() {}
        });
    }

    public void postInAppMessageTapped(String id) {
        InAppMessageTappedPostRequest request = new InAppMessageTappedPostRequest(id);
        httpManager.request(request, new HttpManager.HttpCallback<InAppMessageTappedResponse>(InAppMessageTappedResponse.class) {
            @Override
            public void onStart() {}

            @Override
            public void onCancel(Request request) {
                mResponseError = new CanceledException();
            }

            @Override
            public void onFailure(Request request, InAppMessageTappedResponse responseParsed, int statusCode, Exception e) {
                String.valueOf(statusCode);
            }

            @Override
            public void onSuccess(Response response, InAppMessageTappedResponse responseParsed) {
                mResponseInAppMessageTapped = responseParsed;
            }

            @Override
            public void onFinished() {}

        });
    }

}
