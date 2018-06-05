package org.socratic.android.globals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.socratic.android.BuildConfig;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.OCRAnalytics;
import org.socratic.android.api.CanceledException;
import org.socratic.android.api.request.OcrImagePostRequest;
import org.socratic.android.api.response.OcrImageResponse;
import org.socratic.android.events.PhotoUploadEvent;
import org.socratic.android.util.CameraHelper;
import org.socratic.android.util.PhotoCropHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @date 2017-02-02
 */
public class PhotoManager {

    private AnalyticsManager analyticsManager;
    private HttpManager httpManager;

    private static final String TAG = PhotoManager.class.getSimpleName();
    private static final String TAG_UPLOAD = "photoUploadTag";

    private Bitmap mBitmap;
    private Bitmap mBitmapCropped;
    private CameraHelper.PreviewTransformInfo mPreviewTransformInfo;
    private boolean mIsUploading;
    private OcrImageResponse mResponseOcrImage;
    private Exception mResponseError;
    private PhotoCropHelper mPhotoCropHelper;
    private String mRequestTag;


    @Inject
    public PhotoManager(AnalyticsManager analyticsManager, HttpManager httpManager) {
        this.analyticsManager = analyticsManager;
        this.httpManager = httpManager;
        mPhotoCropHelper = new PhotoCropHelper();
    }

    public Bitmap getPhoto() {
        return mBitmap;
    }

    public void setPhoto(Context context, Bitmap bitmap, int screenWidth, int screenHeight,
                         CameraHelper.PreviewTransformInfo previewTransformInfo,
                         boolean debugSaveImagesToDisk) {

        // Overwrite previous photo, if any.
        mBitmap = bitmap;
        mPreviewTransformInfo = previewTransformInfo;

        // Generate cropped bitmap for API.
        Bitmap croppedBitmap = mPhotoCropHelper.buildCropBitmap(context, bitmap,
                screenWidth, screenHeight, previewTransformInfo);

        // Retain the croppd bitmap fo debugging.
        if (BuildConfig.DEBUG) {
            mBitmapCropped = croppedBitmap;
        }

        if (debugSaveImagesToDisk) {
            debugCaptureImage(context, croppedBitmap);
        }

        // Start uploading.
        if (BuildConfig.SKIP_API_FOR_PHOTO) {
            Log.w(TAG, "Skipping photo upload to API for testing.");

            mIsUploading = false;
            mResponseOcrImage =  new OcrImageResponse();
            mResponseOcrImage.setImageId("localtest");
            EventBus.getDefault().post(new PhotoUploadEvent(
                    PhotoUploadEvent.CONTEXT_FINISHED));
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] imageData = stream.toByteArray();
        final int bitmapSize = imageData.length;

        mRequestTag = TAG_UPLOAD + System.currentTimeMillis();

        mResponseOcrImage = null;
        mResponseError = null;
        OcrImagePostRequest request = new OcrImagePostRequest(imageData);
        request.setTag(mRequestTag);

        httpManager.request(request,
                new HttpManager.HttpCallback<OcrImageResponse>(OcrImageResponse.class) {
                    @Override
                    public void onStart() {
                        mIsUploading = true;
                        EventBus.getDefault().post(new PhotoUploadEvent(
                                PhotoUploadEvent.CONTEXT_STARTING));
                    }

                    @Override
                    public void onCancel(Request request) {
                        mResponseError = new CanceledException();
                    }

                    @Override
                    public void onFailure(Request request, OcrImageResponse responseParsed,
                                          int statusCode, Exception e)
                    {
                        OCRAnalytics.OCRImageUploadError event
                                = new OCRAnalytics.OCRImageUploadError();
                        event.fileSizeInBytes = bitmapSize;
                        if (e != null) {
                            event.errorDescription = e.getMessage();
                            mResponseError = e;
                        } else {
                            event.errorDescription = String.valueOf(statusCode);
                            mResponseError = new Exception();
                        }
                        analyticsManager.track(event);
                    }

                    @Override
                    public void onSuccess(Response response, OcrImageResponse responseParsed) {
                        mResponseOcrImage = responseParsed;

                        OCRAnalytics.OCRImageUploaded event = new OCRAnalytics.OCRImageUploaded();
                        event.imageId = responseParsed.getImageId();
                        event.fileSizeInBytes = bitmapSize;
                        analyticsManager.track(event);
                    }

                    @Override
                    public void onFinished() {
                        mIsUploading = false;
                        EventBus.getDefault().post(new PhotoUploadEvent(
                                PhotoUploadEvent.CONTEXT_FINISHED));
                    }
                });
    }

    private void debugCaptureImage(Context context, Bitmap croppedBitmap) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "Writing bitmap debug samples to disk.");

            Paint paint = new Paint();
            paint.setColor(0x3300ff00);
            paint.setStyle(Paint.Style.FILL);

            Bitmap bitmapPhoto = Bitmap.createBitmap(mBitmap);
            Rect rcMaxCropPhoto = mPhotoCropHelper.getMaxCropRectPhoto();

            Canvas canvas = new Canvas(bitmapPhoto);
            canvas.drawRect(rcMaxCropPhoto, paint);

            File root = context.getExternalCacheDir();
            Log.w(TAG, "Bitmaps will be at: " + root.getAbsolutePath());

            File file1 = new File(root, "fullsize.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file1);
                bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                fos.close();
            } catch (Exception ex) {
                Log.e(TAG, "Error saving debug image.", ex);
            }

            File file2 = new File(root, "max-crop-rect.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file2);
                croppedBitmap.compress(
                        Bitmap.CompressFormat.JPEG, 70, fos);
                fos.close();
            } catch (Exception ex) {
                Log.e(TAG, "Error saving debug image.", ex);
            }
        }
    }

    public void debugCroppedImage(Context context, Rect rcUserCrop) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "Writing crop bitmap debug sample to disk.");

            Paint paint = new Paint();
            paint.setColor(0x33ff0000);
            paint.setStyle(Paint.Style.FILL);

            Bitmap bitmap = Bitmap.createBitmap(mBitmapCropped);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawRect(rcUserCrop, paint);

            File root = context.getExternalCacheDir();
            Log.w(TAG, "Bitmap will be at: " + root.getAbsolutePath());

            File file1 = new File(root, "user-crop-rect.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                fos.close();
            } catch (Exception ex) {
                Log.e(TAG, "Error saving debug image.", ex);
            }
        }
    }

    public boolean isUploading() {
        return mIsUploading;
    }

    public OcrImageResponse getResponseOcrImage() {
        return mResponseOcrImage;
    }

    public Exception getResponseError() {
        return mResponseError;
    }

    public PhotoCropHelper getPhotoCropHelper() {
        return mPhotoCropHelper;
    }

    public CameraHelper.PreviewTransformInfo getTransformInfo() {
        return mPreviewTransformInfo;
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean clearPhoto) {
        httpManager.cancelRequest(mRequestTag);

        if (clearPhoto) {
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            mBitmap = null;

            if (mBitmapCropped != null) {
                mBitmapCropped.recycle();
            }
            mBitmapCropped = null;
        }

        mRequestTag = null;
        mResponseOcrImage = null;
        mResponseError = null;
        mIsUploading = false;
    }
}