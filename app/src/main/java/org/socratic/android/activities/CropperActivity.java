package org.socratic.android.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.databinding.ActivityCropperBinding;
import org.socratic.android.fragments.CropDialogFragment;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.contract.CropperContract;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.Util;
import org.socratic.android.views.CropWidgetView;

import javax.inject.Inject;

/**
 * @date 2017-02-02
 */
public class CropperActivity extends BaseActivity<ActivityCropperBinding, CropperContract.ViewModel> implements CropperContract.View {

    @Inject DiskStorage diskStorage;
    @Inject PhotoManager photoManager;
    @Inject OcrSearchManager ocrSearchManager;
    @Inject SharedPreferences sharedPreferences;

    private static final String TAG = CropperActivity.class.getSimpleName();
    private static final int ACTIVITY_CODE_SEARCH = 500;
    private static final int SHOW_DIALOG_FRAGMENT = 200;

    ImageView viewPhotoPreview;

    CropWidgetView cropWidgetView;

    View debugProgressSpinner;

    RectF mRcPendingCrop;

    boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_cropper);

        isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        MultiLog.d(TAG, "Cropper screen created.");

        viewPhotoPreview = binding.photoPreview;
        cropWidgetView = binding.cropWidget;
        debugProgressSpinner = binding.debugProgressSpinner;

        if (photoManager.getPhoto() == null) {
            MultiLog.e(TAG, "PhotoManager is not initialized, returning to capture activity.");
            finish();
            return;
        }

        // Set editable mode for the cropping widget.
        cropWidgetView.setMode(CropWidgetView.MODE_EDITING);

        // Use the photo from the capture activity.
        viewPhotoPreview.setImageBitmap(photoManager.getPhoto());
        viewPhotoPreview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Bitmap photo = photoManager.getPhoto();

                viewPhotoPreview.setImageMatrix(
                        photoManager.getTransformInfo().toMatrixForCropScreen(
                                right-left, bottom-top,
                                photo.getWidth(), photo.getHeight()));
            }
        });

        binding.btnSearch.setOnClickListener(mOnClickListenerBtnSearch);

        binding.inAppMessageView.setup(TAG);

        updateFirstCameraUse();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Util.hideStatusBar(this);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                final CropDialogFragment dialogFragment = new CropDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                dialogFragment.show(fm, "fragment_crop_dialog");
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.show(dialogFragment);
                ft.commit();
            }
        };

        if (hasFocus && isFirstTime) {
            handler.postDelayed(r, SHOW_DIALOG_FRAGMENT);
            isFirstTime = false;
        }

    }

    //update flag for first use of camera
    private void updateFirstCameraUse() {
        if (isFirstTime) {
            //update first time user flag
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Cropper screen resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();

        MultiLog.d(TAG, "Cropper screen paused.");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + ", " + resultCode);

        // We want to go back to the camera activity in all cases.
        finish();
    }

    private void startOcrTextSearch() {
        MultiLog.d(TAG, "Cropper is starting text search.");

        mRcPendingCrop = cropWidgetView.getCropRectangle();

        if (mRcPendingCrop == null) {
            MultiLog.d(TAG, "Crop rect is null.");
            return;
        }

        ocrSearchManager.setResponse(null);

        // Figure out the position of the cropping widget in relation to the
        // bitmap we uploaded to the API.
        Rect rcApi = photoManager.getPhotoCropHelper().computeUserCropRectForApi(
                mRcPendingCrop);

        if (BuildConfig.DEBUG) {
            photoManager.debugCroppedImage(this, rcApi);
        }

        diskStorage.incrementSearchCount();

        Intent intent = new Intent(this, SearchProgressActivity.class);
        SearchProgressActivity.prepareIntent(intent, rcApi);
        startActivityForResult(intent, ACTIVITY_CODE_SEARCH);
    }

    private View.OnClickListener mOnClickListenerBtnSearch = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startOcrTextSearch();
        }
    };
}
