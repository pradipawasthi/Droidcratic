package org.socratic.android.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();

    private Camera mCamera;
    private int mCameraId;
    private Activity mActivity;
    private int mCameraRotation;
    private boolean mSupportsContinuousFocus;
    private boolean mSupportsAutoFocus;
    private CameraUtils.SizeExt mSizePreview;
    private CameraUtils.SizeExt mSizeCapture;
    private PreviewTransformInfo mPreviewTransformInfo;
    private ViewGroup mTextureViewParent;
    private TextureView mTextureView;
    private int mMaxPhotoSizeInPixels;
    private PictureListener mPictureListener;

    private boolean mIsReady;
    private boolean mIsTakingPicture;
    private boolean mIsFocusing;


    public CameraHelper() {
    }

    public void onCreate(Activity activity,
                         ViewGroup textureViewParent,
                         int maxPhotoSizeInPixels,
                         PictureListener pictureListener) {

        MultiLog.d(TAG, "onCreate().");

        mActivity = activity;
        mMaxPhotoSizeInPixels = maxPhotoSizeInPixels;

        mPictureListener = pictureListener;
        if (mPictureListener != null) {
            mPictureListener.onCameraBusy();
        }

        mTextureViewParent = textureViewParent;

        mSupportsContinuousFocus = false;
        mSupportsAutoFocus = false;

        mCameraId = findBackFacingCameraId();
    }

    private int findBackFacingCameraId() {

        MultiLog.d(TAG, "findBackFacingCameraId().");

        int cameraCount = Camera.getNumberOfCameras();
        MultiLog.d(TAG, "  Found " + cameraCount + " cameras.");

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {

            try {
                Camera.getCameraInfo(i, cameraInfo);

                MultiLog.d(TAG, "  Checking camera: " + i);

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

                    MultiLog.d(TAG, "  CameraHelper found back-facing camera with ID: " + i);

                    return i;

                } else {
                    MultiLog.d(TAG, "  Skip front-facing camera: " + i);
                }

            } catch (Exception ex) {
                MultiLog.e(TAG, "  Could not open back-facing camera with ID: " + i, ex);
            }
        }

        MultiLog.e(TAG, "  Find camera error.", new Throwable("CameraHelper could not find a back-facing camera!"));

        return -1;
    }

    public void onResume() {

        if (mCameraId > -1) {

            MultiLog.d(TAG, "Camera helper resume has camera ID: " + mCameraId);

            mTextureView = new TextureView(mTextureViewParent.getContext());
            mTextureView.setSurfaceTextureListener(mSurfaceListener);
            mTextureViewParent.addView(mTextureView);

            mSupportsContinuousFocus = false;
            mSupportsAutoFocus = false;
            try {
                mCamera = Camera.open(mCameraId);
            } catch (Exception ex) {
                MultiLog.e(TAG, "Error opening camera in resume.", ex);
            }
        } else {
            MultiLog.d(TAG, "Camera helper resume has no camera ID.");
        }
    }

    public void onPause() {
        MultiLog.d(TAG, "Camera helper pause.");

        if (mTextureView != null) {
            mTextureView.setVisibility(View.GONE);
        }

        if (mCamera != null) {
            try {
                mCamera.release();
                mCamera = null;
            } catch (Exception ex) {
                MultiLog.e(TAG, "Error releasing camera in pause.", ex);
            }
            mCamera = null;
        }

        mIsReady = false;

        mTextureViewParent.removeAllViews();

        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(null);
        }
    }

    public void takePicture() {
        if (!mIsReady) {
            return;
        }
        if (mIsTakingPicture) {
            return;
        }

        mIsTakingPicture = true;
        if (mPictureListener != null) {
            mPictureListener.onCameraBusy();
        }

        try {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    mIsTakingPicture = false;

                    if (data == null) {
                        MultiLog.e(TAG, "Error taking picture.", new Exception("Picture byte array is null."));
                        onPictureTakenCleanup();
                        return;
                    }

                    Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (original == null) {
                        MultiLog.e(TAG, "Error taking picture.", new Exception("Error decoding photo byte array."));
                        onPictureTakenCleanup();
                        return;
                    }

                    MultiLog.d(TAG, "Photo size: " + original.getWidth() + " x "
                            + original.getHeight());

                    Bitmap rotated = null;
                    if (mCameraRotation != 0) {
                        MultiLog.d(TAG, "Rotating image for camera rotation: " + mCameraRotation);

                        rotated = TransformationUtils.rotateImage(original, mCameraRotation);

                        MultiLog.d(TAG, "Rotated size: " + rotated.getWidth() + " x "
                            + rotated.getHeight());

                    } else {
                        MultiLog.d(TAG, "Camera rotation was zero, no need to rotate.");
                        rotated = original;
                    }

                    if (mPictureListener != null) {
                        mPictureListener.pictureTaken(rotated);
                    }
                    onPictureTakenCleanup();
                }
            });

        } catch (Exception ex) {
            MultiLog.e(TAG, "Picture capture failed.", ex);
        }
    }

    private void onPictureTakenCleanup() {
        mCamera.startPreview();
        if (mPictureListener != null) {
            mPictureListener.onCameraReady();
        }
    }

    public void tapForFocus(int screenX, int screenY) {
        if ((mSupportsAutoFocus || mSupportsContinuousFocus) && mIsReady && !mIsFocusing) {
            mIsFocusing = true;

            if (mSupportsContinuousFocus) {
                mCamera.cancelAutoFocus();
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(parameters);
            }

            try {
                mCamera.autoFocus(mAutoFocusCallback);
            } catch (Exception ex) {
                mIsFocusing = false;
                MultiLog.e(TAG, "Error starting autofocus.", ex);
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            mIsFocusing = false;

            if (mSupportsContinuousFocus && camera != null) {
                camera.cancelAutoFocus();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            }

            if (mPictureListener != null) {
                mPictureListener.onCameraFocused();
            }
        }
    };

    private void onSurfaceTextureReady(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewTexture(surfaceTexture);

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(mCameraId, cameraInfo);
                mCameraRotation = CameraUtils.getCameraDisplayOrientationRotation(
                        mActivity, cameraInfo);
                mCamera.setDisplayOrientation(mCameraRotation);
                Camera.Parameters params = mCamera.getParameters();

                MultiLog.d(TAG, "Camera rotation: " + mCameraRotation);
                MultiLog.d(TAG, "Surface size: " + width + " x " + height);

                CameraUtils.SizeExt[] captureSizes = CameraUtils.getOptimalSizes(
                        width, height, mMaxPhotoSizeInPixels, params);
                if (captureSizes == null) {
                    if (mPictureListener != null) {
                        mPictureListener.onCameraError();
                    }
                    return;
                }

                mSizePreview = captureSizes[0];
                params.setPreviewSize(mSizePreview.width, mSizePreview.height);

                mSizeCapture = captureSizes[1];
                params.setPictureSize(mSizeCapture.width, mSizeCapture.height);

                List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
                if (supportedFocusModes != null) {
                    if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        MultiLog.d(TAG, "  Continuous focus mode supported.");
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        mSupportsContinuousFocus = true;
                    } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        MultiLog.d(TAG, "  Auto focus mode supported.");
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        mSupportsAutoFocus = true;
                    }
                }

                mCamera.setParameters(params);

                mCamera.startPreview();

                adjustAspectRatio(getRotatedPreviewWidth(), getRotatedPreviewHeight());

                mIsReady = true;
                if (mPictureListener != null) {
                    mPictureListener.onCameraReady();
                }
            }
        } catch (Exception ex) {
            MultiLog.e(TAG, "onSurfaceTextureReady error.", ex);
        }
    }

    private TextureView.SurfaceTextureListener mSurfaceListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            MultiLog.d(TAG, "onSurfaceTextureAvailable: " + width + " x " + height);

            onSurfaceTextureReady(surfaceTexture, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            MultiLog.d(TAG, "onSurfaceTextureSizeChanged: " + ", " + width + ", " + height);

            onSurfaceTextureReady(surfaceTexture, width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            MultiLog.d(TAG, "onSurfaceTextureDestroyed");

            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                mCamera.stopPreview();
            }

            mIsReady = false;

            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private int getRotatedPreviewWidth() {
        if (mCameraRotation == 90 || mCameraRotation == 270) {
            return mSizePreview.height;
        }
        return mSizePreview.width;
    }

    private int getRotatedPreviewHeight() {
        if (mCameraRotation == 90 || mCameraRotation == 270) {
            return mSizePreview.width;
        }
        return mSizePreview.height;
    }

    /**
     * TextureView will stretch the camera output to fill. Apply an additional
     * transform to match the aspect ratio of the view.
     */
    private void adjustAspectRatio(int previewWidth, int previewHeight) {

        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();

        final boolean correct = true;
        if (correct) {

            float scaleStretchedX = viewWidth / (float) previewWidth;
            float scaleStretchedY = viewHeight / (float) previewHeight;

            float scaleStretchedMax = Math.max(scaleStretchedX, scaleStretchedY);
            int correctWidth = (int) (previewWidth * scaleStretchedMax);
            int correctHeight = (int) (previewHeight * scaleStretchedMax);

            float scaleFixedX = correctWidth / (float) viewWidth;
            float scaleFixedY = correctHeight / (float) viewHeight;

            int offsetX = (viewWidth - correctWidth) / 2;

            MultiLog.d(TAG, "Updating textureview to match aspect ratio:");
            MultiLog.d(TAG, "  View size: " + viewWidth + " x " + viewHeight);
            MultiLog.d(TAG, "  Preview size: " + previewWidth + " x " + previewHeight);
            MultiLog.d(TAG, "  Stretch scales: " + scaleStretchedX + ", " + scaleStretchedY);
            MultiLog.d(TAG, "  Stretch scale max: " + scaleStretchedMax);
            MultiLog.d(TAG, "  Correct size: " + correctWidth + " x " + correctHeight);
            MultiLog.d(TAG, "  Scales fixed: " + scaleFixedX + " x " + scaleFixedY);
            MultiLog.d(TAG, "  OffsetX: " + offsetX);

            mPreviewTransformInfo = new PreviewTransformInfo();
            mPreviewTransformInfo.width = correctWidth;
            mPreviewTransformInfo.height = correctHeight;
            mPreviewTransformInfo.offsetX = offsetX;
            mPreviewTransformInfo.scaleX = scaleFixedX;
            mPreviewTransformInfo.scaleY = scaleFixedY;

            mTextureView.setTransform(mPreviewTransformInfo.toMatrixForPreviewScreen());

        } else {

            mPreviewTransformInfo = new PreviewTransformInfo();
            mPreviewTransformInfo.width = viewWidth;
            mPreviewTransformInfo.height = viewHeight;
            mPreviewTransformInfo.offsetX = 0;
            mPreviewTransformInfo.scaleX = 1f;
            mPreviewTransformInfo.scaleY = 1f;

            mTextureView.setTransform(mPreviewTransformInfo.toMatrixForPreviewScreen());
        }
    }

    public PreviewTransformInfo getPreviewTransformInfo() {
        return new PreviewTransformInfo(mPreviewTransformInfo);
    }

    public static class PreviewTransformInfo {
        int width;
        int height;
        int offsetX;
        int offsetY;
        float scaleX;
        float scaleY;

        public PreviewTransformInfo() {
        }

        public PreviewTransformInfo(PreviewTransformInfo src) {
            width = src.width;
            height = src.height;
            offsetX = src.offsetX;
            offsetY = src.offsetY;
            scaleX = src.scaleX;
            scaleY = src.scaleY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Matrix toMatrixForPreviewScreen() {
            Matrix matrix = new Matrix();
            matrix.setScale(scaleX, scaleY);
            matrix.postTranslate(offsetX, offsetY);
            return matrix;
        }

        public Matrix toMatrixForCropScreen(int viewWidth, int viewHeight, int photoWidth,
                                            int photoHeight) {
            float scaleX = width / (float)photoWidth;
            float scaleY = height / (float)photoHeight;
            float scale = Math.min(scaleX, scaleY);
            int correctWidth = (int)(photoWidth * scale);
            int offsetX = (viewWidth - correctWidth) / 2;

            MultiLog.d(TAG, "Creating matrix for crop screen:");
            MultiLog.d(TAG, "  view size: " + viewWidth + " x " + viewHeight);
            MultiLog.d(TAG, "  photo size: " + photoWidth + " x " + photoHeight);
            MultiLog.d(TAG, "  scales: " + scaleX + ", " + scaleY + " => " + scale);
            MultiLog.d(TAG, "  correctWidth: " + correctWidth);
            MultiLog.d(TAG, "  offsetX: " + offsetX);

            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postTranslate(offsetX, offsetY);
            return matrix;
        }
    }

    public interface PictureListener {
        void pictureTaken(Bitmap bitmap);
        void onCameraReady();
        void onCameraBusy();
        void onCameraFocused();
        void onCameraError();
    }
}