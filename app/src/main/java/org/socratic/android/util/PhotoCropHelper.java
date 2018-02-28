package org.socratic.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import org.socratic.android.R;

/**
 * @date 2017-02-08
 */
public class PhotoCropHelper {

    private static final String TAG = PhotoCropHelper.class.getSimpleName();
    public static final int UPLOAD_IMAGE_SIZE = 1400;

    private Point originalPhotoSize;
    private Rect maxCropRectPhoto;

    private Point screenSize;
    private Point maxCropRectCenterPt;
    private Rect maxCropRect;

    private Rect croppedBitmapRect;

    public void initScreenSize(Context context, int w, int h) {
        screenSize = new Point(w, h);
        maxCropRectPhoto = new Rect();

        Resources res = context.getResources();
        int sidePadding = (int)res.getDimension(R.dimen.side_padding_crop_widget);
        int maxWidth = w - sidePadding * 2;
        int maxHeight = (int)res.getDimension(R.dimen.max_crop_rect_height);

        maxCropRectCenterPt = new Point();
        maxCropRectCenterPt.x = w / 2;
        maxCropRectCenterPt.y = h / 2;

        maxCropRect = new Rect();
        maxCropRect.left = maxCropRectCenterPt.x - maxWidth / 2;
        maxCropRect.top = maxCropRectCenterPt.y - maxHeight / 2;
        maxCropRect.right = maxCropRect.left + maxWidth;
        maxCropRect.bottom = maxCropRect.top + maxHeight;
    }

    public Bitmap buildCropBitmap(Context context, Bitmap bitmap, int screenWidth, int screenHeight,
                                  CameraHelper.PreviewTransformInfo previewTransformInfo) {

        initScreenSize(context, screenWidth, screenHeight);

        MultiLog.d(TAG, "Constructing crop bitmap from: " + bitmap);
        MultiLog.d(TAG, "Size: " + bitmap.getWidth() + " x " + bitmap.getHeight());
        MultiLog.d(TAG, "Screen: " + screenSize.x + " x " + screenSize.y);

        double ratio1 = CameraUtils.ratio(bitmap.getWidth(), bitmap.getHeight());
        double ratio2 = CameraUtils.ratio(previewTransformInfo.width, previewTransformInfo.height);
        double ratio3 = CameraUtils.ratio(screenWidth, screenHeight);

        MultiLog.e(TAG, "Ratio photo: " + ratio1);
        MultiLog.e(TAG, "Ratio preview transformed: " + ratio2);
        MultiLog.e(TAG, "Ratio screen: " + ratio3);

        // In preview coordinates.
        int cropLeft = (int)
                (previewTransformInfo.getWidth() / 2f - maxCropRect.width() / 2f);

        Rect rcCropMax = new Rect(
                cropLeft,
                maxCropRect.top,
                cropLeft + maxCropRect.width(),
                maxCropRect.top + maxCropRect.height());

        float pl = rcCropMax.left / (float) previewTransformInfo.width;
        float pt = rcCropMax.top / (float) previewTransformInfo.height;
        float pw = rcCropMax.width() / (float) previewTransformInfo.width;
        float ph = rcCropMax.height() / (float) previewTransformInfo.height;

        int cropL = (int)(bitmap.getWidth() * pl);
        int cropT = (int)(bitmap.getHeight() * pt);
        int cropW = (int)(bitmap.getWidth() * pw);
        int cropH = (int)(bitmap.getHeight() * ph);

        maxCropRectPhoto.set(cropL, cropT, cropL + cropW, cropT + cropH);

        // Now we can extract the bitmap.
        Bitmap bitmapCropped = Bitmap.createBitmap(
                bitmap,
                maxCropRectPhoto.left,
                maxCropRectPhoto.top,
                maxCropRectPhoto.width(),
                maxCropRectPhoto.height());

        MultiLog.d(TAG, "Cropped bitmap has size: " + bitmap.getWidth()
            + " x " + bitmap.getHeight());

        // Resize if larger than allowed.
        int maxSide = Math.max(bitmapCropped.getWidth(), bitmapCropped.getHeight());
        if (maxSide > UPLOAD_IMAGE_SIZE) {
            float scaleApi = UPLOAD_IMAGE_SIZE / (float)maxSide;

            int nw = (int)(bitmapCropped.getWidth() * scaleApi);
            int nh = (int)(bitmapCropped.getHeight() * scaleApi);

            MultiLog.d(TAG, "Scaled crop bitmap down for API to: " + nw + " x " + nh);

            bitmapCropped = Bitmap.createScaledBitmap(
                    bitmapCropped, nw, nh, false);
        }

        croppedBitmapRect = new Rect(0, 0, bitmapCropped.getWidth(), bitmapCropped.getHeight());

        return bitmapCropped;
    }

    public Rect getMaxCropRect() {
        return maxCropRect;
    }

    public Rect getMaxCropRectPhoto() {
        return maxCropRectPhoto;
    }

    public Rect computeUserCropRectForApi(RectF userRect) {

        float pl = (userRect.left - maxCropRect.left) / (float)maxCropRect.width();
        float pt = (userRect.top - maxCropRect.top) / (float)maxCropRect.height();
        float pr = (userRect.right - maxCropRect.left) / (float)maxCropRect.width();
        float pb = (userRect.bottom - maxCropRect.top) / (float)maxCropRect.height();

        int l = (int)(croppedBitmapRect.width() * pl);
        int t = (int)(croppedBitmapRect.height() * pt);
        int r = (int)(croppedBitmapRect.width() * pr);
        int b = (int)(croppedBitmapRect.height() * pb);

        return new Rect(l, t, r, b);
    }
}
