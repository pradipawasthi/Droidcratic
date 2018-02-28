package org.socratic.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.R;
import org.socratic.android.api.model.ApiError;
import org.socratic.android.databinding.ActivitySearchProgressBinding;
import org.socratic.android.dialogs.SearchErrorDialog;
import org.socratic.android.events.OcrSearchEvent;
import org.socratic.android.events.PhotoUploadEvent;
import org.socratic.android.events.TextSearchEvent;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.contract.SearchProgressContract;
import org.socratic.android.util.DialogDismissListener;
import org.socratic.android.util.MultiLog;

import java.util.List;

import javax.inject.Inject;


/**
 * @date 2017-02-13
 */
public class SearchProgressActivity extends BaseActivity<ActivitySearchProgressBinding, SearchProgressContract.ViewModel>
    implements SearchProgressContract.View, DialogDismissListener {

    @Inject PhotoManager photoManager;

    private static final String TAG = SearchProgressActivity.class.getSimpleName();
    private static final String EXTRA_CROPPING_RECTANGLE =
            "@ecr";
    private static final String TAG_ERROR_DLG =
            "TAG_ERROR_DLG";
    private static final String EXTRA_QUERY = "@eq";
    private static final String EXTRA_IS_OCR = "@eio";

    Rect mRcApi;
    String query;
    boolean isOCR;

    ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_search_progress);
        setRegisterEventBus();

        MultiLog.d(TAG, "Search screen created.");

        progressSpinner = binding.progressSpinner;

        if (getIntent() == null || getIntent().getExtras() == null) {
            MultiLog.e(TAG, "Missing intent or extras.");
            finish();
            return;
        }

        isOCR = getIntent().getExtras().getBoolean(EXTRA_IS_OCR, true);
        if (isOCR) {
            mRcApi = getIntent().getExtras().getParcelable(EXTRA_CROPPING_RECTANGLE);
            if (mRcApi == null) {
                MultiLog.e(TAG, "Missing crop rectangle.");
                finish();
                return;
            }
        } else {
            query = getIntent().getExtras().getString(EXTRA_QUERY);
            if (query == null) {
                MultiLog.e(TAG, "Missing query");
                finish();
                return;
            }
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiLog.d(TAG, "User is canceling the search.");
                viewModel.cancelSearch();
            }
        });

        binding.inAppMessageView.setup(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Search progress screen resumed.");

        if (photoManager.isUploading()) {
            return;
        }

        if (isOCR) {

            if (!viewModel.handlePhotoUploadErrors()) {
                return;
            }

            viewModel.startOcrSearch(mRcApi);
        } else {
            viewModel.startTextSearch(query);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        MultiLog.d(TAG, "Search progress screen paused.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhotoUploadEvent event) {
        switch (event.getContext()) {
            case PhotoUploadEvent.CONTEXT_FINISHED:

                MultiLog.d(TAG, "Photo upload has completed.");

                if (!viewModel.handlePhotoUploadErrors()) {
                    return;
                }

                viewModel.startOcrSearch(mRcApi);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TextSearchEvent event) {
        switch (event.getContext()) {
            case TextSearchEvent.CONTEXT_FINISHED:
                MultiLog.d(TAG, "Text search has completed");

                progressSpinner.setVisibility(View.INVISIBLE);

                viewModel.validateTextSearchResults();

                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OcrSearchEvent event) {
        switch (event.getContext()) {
            case OcrSearchEvent.CONTEXT_FINISHED:
                MultiLog.d(TAG, "OCR search has completed.");

                progressSpinner.setVisibility(View.INVISIBLE);

                viewModel.validateOcrSearchResults();

                break;
        }
    }

    public static void prepareIntent(Intent intent, Rect rcCrop) {
        intent.putExtra(EXTRA_CROPPING_RECTANGLE, rcCrop);
    }

    public static void prepareIntent(Intent intent, String query) {
        intent.putExtra(EXTRA_IS_OCR, false);
        intent.putExtra(EXTRA_QUERY, query);
    }

    @Override
    public void showErrorDialog(List<ApiError> errors) {

        // We'll only render the first error for now.
        ApiError error = errors.get(0);

        FragmentManager fm = getSupportFragmentManager();
        SearchErrorDialog dlg = SearchErrorDialog.newInstance(error);
        dlg.show(fm, TAG_ERROR_DLG);
    }

    @Override
    public void navigateToResults(boolean setResult) {
        Intent intent = null;
        MultiLog.d(TAG, "going to results screen");
        intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(EXTRA_IS_OCR, isOCR);
        startActivity(intent);

        if (setResult) {
            // The user will be at the results screen after this,
            // and if they hit the back button, we want to skip
            // the cropping stage and go directly back to the
            // capture activity.
            setResult(Activity.RESULT_OK);
        }
        finish();
    }

    @Override
    public void onDialogDismissed(DialogFragment dlg) {
        viewModel.cancelSearch();
    }

    @Override
    public void showErrorToast() {
        Toast.makeText(this, R.string.image_upload_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishActivity() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void showSearchErrorToast() {
        Toast.makeText(this, R.string.we_couldnt_complete_this_search, Toast.LENGTH_SHORT).show();
    }
}