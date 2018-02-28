package org.socratic.android.globals;

import org.socratic.android.api.CanceledException;
import org.socratic.android.api.ShareURLPostRequest;
import org.socratic.android.api.response.BaseResponse;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jessicaweinberg on 8/16/17.
 */

public class ShareURLManager {
    private static final String TAG = ShareURLManager.class.getSimpleName();

    private HttpManager httpManager;
    private BaseResponse shareURLResponse;
    private Exception responseError;

    @Inject
    public ShareURLManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void postShareURL(String code, String source) {
        ShareURLPostRequest request = new ShareURLPostRequest(code, source);
        httpManager.request(request, new HttpManager.HttpCallback<BaseResponse>(BaseResponse.class) {
            @Override
            public void onStart() {
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
                shareURLResponse = responseParsed;
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
