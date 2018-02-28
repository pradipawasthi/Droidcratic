package org.socratic.android.globals;

import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.OCRAnalytics;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.SearchGetRequest;
import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.model.MetaData;
import org.socratic.android.api.model.SearchResults;
import org.socratic.android.api.response.SearchResponse;
import org.socratic.android.events.TextSearchEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by williamxu on 6/21/17.
 */

public class TextSearchManager extends BaseSearchManager {

    private HttpManager httpManager;
    private AnalyticsManager analyticsManager;

    private static final String TAG = TextSearchManager.class.getSimpleName();
    private static final String TAG_TEXT = "textTag";

    private boolean mIsRunning;
    private SearchResults mSearchResults;
    private Exception mResponseError;
    private String mRequestTag;

    @Inject
    public TextSearchManager(HttpManager httpManager, AnalyticsManager analyticsManager) {
        this.httpManager = httpManager;
        this.analyticsManager = analyticsManager;
    }

    public void startTextSearch(String query) {

        mRequestTag = TAG_TEXT + System.currentTimeMillis();

        SearchGetRequest request = new SearchGetRequest(query, "qa", "definitions", "video");

        request.setTag(mRequestTag);

        httpManager.request(request,
                new HttpManager.HttpCallback<SearchResults>(SearchResults.class) {
                    @Override
                    public void onStart() {
                        mIsRunning = true;
                        EventBus.getDefault().post(new TextSearchEvent(
                                TextSearchEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onFailure(Request request, SearchResults responseParsed, int statusCode, Exception e) {
                        OCRAnalytics.OCRRequestError event
                                = new OCRAnalytics.OCRRequestError();
                        event.searchType = AnalyticsManager.EventPropertyOptions.SearchType.userTyping;
                        if (e != null) {
                            event.errorDescription = e.getMessage();
                        } else {
                            event.errorDescription = String.valueOf(statusCode);
                        }

                        analyticsManager.track(event);
                    }

                    @Override
                    public void onCancel(Request request) {
                        mResponseError = new CanceledException();
                    }

                    @Override
                    public void onSuccess(Response response, SearchResults responseParsed) {
                        if (responseParsed != null) {
                            if (responseParsed.getErrors() == null
                                    || responseParsed.getErrors().isEmpty()) {

                                OCRAnalytics.QuerySearched event
                                        = new OCRAnalytics.QuerySearched();
                                event.searchType = AnalyticsManager.EventPropertyOptions.SearchType.userTyping;
                                event.queryText = responseParsed.getQuestionText();

                                addMetaData(event, responseParsed.getMetadata());

                                analyticsManager.track(event);
                            }
                        } else {
                            OCRAnalytics.QuerySearchError eventError
                                    = new OCRAnalytics.QuerySearchError();
                            eventError.errorDescription = "Empty response";
                            eventError.searchType = AnalyticsManager.EventPropertyOptions.SearchType.userTyping;
                            analyticsManager.track(eventError);
                        }

                        mSearchResults = responseParsed;
                    }

                    @Override
                    public void onFinished() {
                        mIsRunning = false;
                        EventBus.getDefault().post(new TextSearchEvent(
                                TextSearchEvent.CONTEXT_FINISHED));
                    }
                });
    }

    private void addMetaData(OCRAnalytics.QuerySearched event, MetaData metaData) {

        Map<Object, Object> metadataMap = new HashMap<>();
        Field[] datafields = metaData.getClass().getDeclaredFields();
        for (Field field : datafields) {
            try {
                Object val = field.get(metaData);
                if (val == null) {
                    continue;
                }
                if (val.getClass().isEnum()) {
                    val = val.toString();
                }
                metadataMap.put(field.getName(), val);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Error adding field: " + field.getName(), e);
            }
        }

        event.metadata = metadataMap;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public SearchResponse getResponse() {
        return mSearchResults;
    }

    @Override
    public void setResponse(SearchResponse response) {
        this.mSearchResults = (SearchResults) response;
    }

    @Override
    public CardResponse getResponseCard(int index) {
        if (mSearchResults != null) {
            if (mSearchResults.getCardResults() != null) {
                if (index < mSearchResults.getCardResults().size()) {
                    return mSearchResults.getCardResults().get(index);
                }
            }
        }

        return null;
    }

    @Override
    public void cancel() {
        httpManager.cancelRequest(mRequestTag);
        mRequestTag = null;
        mSearchResults = null;
        mResponseError = null;
        mIsRunning = false;
    }
}
