package org.socratic.android.globals;

import android.graphics.Rect;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.OCRAnalytics;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.request.OcrTextGetRequest;
import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.model.MetaData;
import org.socratic.android.api.response.OcrTextResponse;
import org.socratic.android.api.response.SearchResponse;
import org.socratic.android.events.OcrSearchEvent;
import org.socratic.android.util.MultiLog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @date 2017-02-06
 */
public class OcrSearchManager extends BaseSearchManager {

    private HttpManager httpManager;
    private AnalyticsManager analyticsManager;

    private static final String TAG = OcrSearchManager.class.getSimpleName();
    private static final String TAG_OCR = "ocrTag";

    private boolean mIsRunning;
    private OcrTextResponse mResponseOcrText;
    private Exception mResponseError;
    private String mRequestTag;

    @Inject
    public OcrSearchManager(HttpManager httpManager, AnalyticsManager analyticsManager) {
        this.httpManager = httpManager;
        this.analyticsManager = analyticsManager;
    }

    public void startSearch(final String imageId,
                            Rect rcCrop) {
        mRequestTag = TAG_OCR + System.currentTimeMillis();

        final OcrTextGetRequest request = new OcrTextGetRequest(
                imageId,
                rcCrop.left,
                rcCrop.top,
                rcCrop.right,
                rcCrop.bottom,
                OcrTextGetRequest.NATIVE_CARD_TYPE_QA,
                OcrTextGetRequest.NATIVE_CARD_TYPE_MATH_STEPS,
                OcrTextGetRequest.NATIVE_CARD_TYPE_DEFINITIONS,
                OcrTextGetRequest.NATIVE_CARD_TYPE_VIDEO);

        request.setTag(mRequestTag);

        httpManager.request(request,
                new HttpManager.HttpCallback<OcrTextResponse>(OcrTextResponse.class) {
                    @Override
                    public void onStart() {
                        mIsRunning = true;
                        EventBus.getDefault().post(new OcrSearchEvent(
                                OcrSearchEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        mResponseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, OcrTextResponse responseParsed,
                                          int statusCode, Exception e) {

                        OCRAnalytics.OCRRequestError event
                                = new OCRAnalytics.OCRRequestError();
                        event.searchType = AnalyticsManager.EventPropertyOptions.SearchType.ocr;
                        event.imageId = imageId;
                        if (e != null) {
                            event.errorDescription = e.getMessage();
                        } else {
                            event.errorDescription = String.valueOf(statusCode);
                        }

                        analyticsManager.track(event);
                    }

                    @Override
                    public void onSuccess(Response response, OcrTextResponse responseParsed) {
                        if (responseParsed != null) {
                            if (responseParsed.getErrors() == null
                                    || responseParsed.getErrors().isEmpty()) {

                                OCRAnalytics.QuerySearched event
                                        = new OCRAnalytics.QuerySearched();
                                event.searchType = AnalyticsManager.EventPropertyOptions.SearchType.ocr;
                                event.queryText = responseParsed.getText();

                                if (responseParsed.getSearchResults() != null) {
                                    addMetaData(event, responseParsed.getSearchResults().getMetadata());
                                }

                                analyticsManager.track(event);
                            }
                        } else {
                            OCRAnalytics.QuerySearchError eventError
                                    = new OCRAnalytics.QuerySearchError();
                            eventError.errorDescription = "Empty response";
                            eventError.searchType = AnalyticsManager.EventPropertyOptions.SearchType.ocr;
                            eventError.imageId = imageId;
                            analyticsManager.track(eventError);
                        }

                        mResponseOcrText = responseParsed;
                        MultiLog.d(TAG, "Response -> " + responseParsed.getQuestionText());
                    }

                    @Override
                    public void onFinished() {
                        mIsRunning = false;
                        EventBus.getDefault().post(new OcrSearchEvent(
                                OcrSearchEvent.CONTEXT_FINISHED));
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
        return mResponseOcrText;
    }

    @Override
    public void setResponse(SearchResponse response) {
        this.mResponseOcrText = (OcrTextResponse) response;
    }

    @Override
    public CardResponse getResponseCard(int index) {
        if (mResponseOcrText != null) {
            if (mResponseOcrText.getCardResults() != null) {
                if (index < mResponseOcrText.getCardResults().size()) {
                    return mResponseOcrText.getCardResults().get(index);
                }
            }
        }

        return null;
    }

    @Override
    public void cancel() {
        httpManager.cancelRequest(mRequestTag);
        mRequestTag = null;
        mResponseOcrText = null;
        mResponseError = null;
        mIsRunning = false;
    }
}