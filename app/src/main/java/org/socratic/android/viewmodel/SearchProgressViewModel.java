package org.socratic.android.viewmodel;

import android.graphics.Rect;
import android.util.Log;

import org.socratic.android.BuildConfig;
import org.socratic.android.contract.SearchProgressContract;
import org.socratic.android.api.model.SearchResults;
import org.socratic.android.api.response.OcrTextResponse;
import org.socratic.android.dagger.scopes.PerActivity;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.MultiLog;

import javax.inject.Inject;

/**
 * Created by williamxu on 7/12/17.
 */

@PerActivity
public class SearchProgressViewModel extends BaseViewModel<SearchProgressContract.View> implements SearchProgressContract.ViewModel {

    private static final String TAG = SearchProgressViewModel.class.getSimpleName();

    OcrSearchManager ocrSearchManager;
    TextSearchManager textSearchManager;
    PhotoManager photoManager;
    DiskStorage diskStorage;

    @Inject
    public SearchProgressViewModel(OcrSearchManager ocrSearchManager, TextSearchManager textSearchManager, PhotoManager photoManager,
                                   DiskStorage diskStorage) {
        this.ocrSearchManager = ocrSearchManager;
        this.textSearchManager = textSearchManager;
        this.photoManager = photoManager;
        this.diskStorage = diskStorage;
    }

    @Override
    public void startOcrSearch(Rect rcApi) {
        if (ocrSearchManager.getResponse() == null) {

            if (!ocrSearchManager.isRunning()) {

                MultiLog.d(TAG, "startOcrSearch.");

                if (BuildConfig.SKIP_API_FOR_PHOTO) {
                    Log.w(TAG, "Skipping OCR search, check flag: SKIP_API_FOR_PHOTO.");
                    return;
                }

                ocrSearchManager.startSearch(
                        photoManager.getResponseOcrImage().getImageId(),
                        rcApi);
            }
        }
    }

    @Override
    public void startTextSearch(String query) {
        if (textSearchManager.getResponse() == null) {

            if (!textSearchManager.isRunning()) {
                MultiLog.d(TAG, "start text search");

                textSearchManager.startTextSearch(query);
            }
        }
    }

    /**
     * Return true if photo upload has finished and we can continue to the OCR stage.
     * Will finish the activity and show an error message in case of a problem.
     */
    @Override
    public boolean handlePhotoUploadErrors() {
        if (photoManager.getResponseError() != null) {
            MultiLog.e(TAG, "There was an error during photo upload.", photoManager.getResponseError());
            getView().showErrorToast();
            cancelSearch();
            return false;
        } else if (photoManager.getResponseOcrImage() == null) {
            MultiLog.e(TAG, "Missing photo upload response.");
            getView().showErrorToast();
            cancelSearch();
            return false;
        }

        return true;
    }

    @Override
    public void cancelSearch() {
        photoManager.cancel(false);
        ocrSearchManager.cancel();
        textSearchManager.cancel();
        getView().finishActivity();
    }

    @Override
    public void validateTextSearchResults() {
        SearchResults response = (SearchResults) textSearchManager.getResponse();
        if (response != null) {
            if (response.getErrors() != null && !response.getErrors().isEmpty()) {

                MultiLog.e(TAG, "Response has errors.");

                getView().showErrorDialog(response.getErrors());

            } else if (response.hasResults()) {

                MultiLog.d(TAG, "going to results screen");

                getView().navigateToResults(false);
            }
        } else {
            getView().showSearchErrorToast();
            cancelSearch();
        }
    }

    @Override
    public void validateOcrSearchResults() {
        OcrTextResponse response = (OcrTextResponse) ocrSearchManager.getResponse();
        if (response != null) {
            if (response.getErrors() != null && !response.getErrors().isEmpty()) {

                MultiLog.e(TAG, "Response has errors.");

                getView().showErrorDialog(response.getErrors());

            } else if (response.hasResults()) {
                MultiLog.d(TAG, "Search count is > 1, so going to results screen.");
                getView().navigateToResults(true);
            }
        } else {
            getView().showSearchErrorToast();
            cancelSearch();
        }
    }

}
