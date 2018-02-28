package org.socratic.android.globals;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.BingPredictionsGetRequest;
import org.socratic.android.api.response.BingPredictionsResponse;
import org.socratic.android.events.BingPredictionsEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by williamxu on 6/27/17.
 */

public class BingPredictionsManager {

    private HttpManager httpManager;

    private static final String TAG = BingPredictionsManager.class.getSimpleName();

    private boolean mIsRunning;
    private Exception mResponseError;
    private List<String> mCurrentPredictions = new ArrayList<>();

    @Inject
    public BingPredictionsManager(HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public void getPredictions(String query) {

        BingPredictionsGetRequest request = new BingPredictionsGetRequest(query);

        httpManager.request(request,
                new HttpManager.HttpCallback<BingPredictionsResponse>(BingPredictionsResponse.class) {
                    @Override
                    public void onStart() {
                        mIsRunning = true;
                        EventBus.getDefault().post(new BingPredictionsEvent(
                                BingPredictionsEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onFailure(Request request, BingPredictionsResponse responseParsed, int statusCode, Exception e) {
                        String.valueOf(statusCode);
                    }

                    @Override
                    public void onCancel(Request request) {
                        mResponseError = new CanceledException();
                    }

                    @Override
                    public void onSuccess(Response response, BingPredictionsResponse responseParsed) {
                        mCurrentPredictions.clear();
                        mCurrentPredictions.addAll(responseParsed);
                    }

                    @Override
                    public void onFinished() {
                        mIsRunning = false;
                        EventBus.getDefault().post(new BingPredictionsEvent(
                                BingPredictionsEvent.CONTEXT_FINISHED));
                    }
                });
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public List<String> getCurrentPredictions() {
        return mCurrentPredictions;
    }

    public void setCurrentPredictions(List<String> predictions) {
        this.mCurrentPredictions = predictions;
    }

}
