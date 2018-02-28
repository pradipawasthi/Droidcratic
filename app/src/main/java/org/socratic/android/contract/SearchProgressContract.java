package org.socratic.android.contract;

import android.graphics.Rect;

import org.socratic.android.api.model.ApiError;
import org.socratic.android.viewmodel.ViewModelAdapter;

import java.util.List;

/**
 * Created by williamxu on 7/14/17.
 */

public interface SearchProgressContract {

    interface View extends ViewAdapter {

        void showErrorToast();

        void finishActivity();

        void showSearchErrorToast();

        void showErrorDialog(List<ApiError> errors);

        void navigateToResults(boolean setResult);
    }

    interface ViewModel extends ViewModelAdapter<View> {

        void startOcrSearch(Rect rcApi);

        void startTextSearch(String query);

        boolean handlePhotoUploadErrors();

        void cancelSearch();

        void validateTextSearchResults();

        void validateOcrSearchResults();

    }
}
