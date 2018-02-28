package org.socratic.android.util;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @date 2017-02-02
 */
@SuppressWarnings("deprecation")
public class CameraUtils {

    private static final String TAG = CameraUtils.class.getSimpleName();

    private static SizeExt getOptimalSize(final int targetWidth,
                                          final int targetHeight,
                                          final int maxSideSize,
                                          List<Camera.Size> options)
    {
        MultiLog.d(TAG, "  Finding optimal size for: "
            + targetWidth + " x " + targetHeight);

        if (options == null || options.isEmpty()) {
            MultiLog.e(TAG, "    Size options empty.");
            return null;
        }

        final double targetRatio = ratio(targetWidth, targetHeight);
        final int targetPixelCount = targetWidth * targetHeight;

        MultiLog.d(TAG, "    Target ratio: " + targetRatio);

        ArrayList<Camera.Size> sorted = new ArrayList<>();
        for (Camera.Size option : options) {
            if (filterSize(option, maxSideSize)) {
                sorted.add(option);
            }
        }

        if (sorted.isEmpty()) {
            return null;
        }

        Comparator<Camera.Size> comparator
                = new Comparator<Camera.Size>() {

            public int compare(Camera.Size o1, Camera.Size o2) {
                double ratio1 = Math.abs(targetRatio - ratio(o1));
                double ratio2 = Math.abs(targetRatio - ratio(o2));

                if (ratio1 < ratio2) {
                    return -1;
                } else if (ratio2 < ratio1) {
                    return 1;
                } else {
                    int numPixels1 =
                            Math.abs(targetPixelCount - o1.width * o1.height);
                    int numPixels2 =
                            Math.abs(targetPixelCount - o2.width * o2.height);

                    if (numPixels1 < numPixels2) {
                        return -1;
                    } else if (numPixels2 < numPixels1) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        };

        Collections.sort(sorted, comparator);

        Camera.Size picked = sorted.get(0);

        MultiLog.d(TAG, "    Sorted sizes:");
        for (Camera.Size size : sorted) {
            MultiLog.d(TAG, "      " + size.width + " x " + size.height
                    + ", " + ratio(size));
        }

        MultiLog.d(TAG, "    Picked size: " + picked.width + " x " + picked.height
                + ", " + ratio(picked));

        return new SizeExt(picked);
    }

    /**
     * 1. See if we have sizes that match the closest screen ratio,
     *    and choose those if they match.
     * 2. If no matches, try to find matching aspect ratios independent
     *    of the aspect ratio of the screen.
     */
    public static SizeExt[] getOptimalSizes(final int targetWidth,
                                            final int targetHeight,
                                            final int maxSideSize,
                                            Camera.Parameters parameters)
    {
        MultiLog.d(TAG, "Finding optimal sizes for: "
                + targetWidth + " x " + targetHeight);

        if (parameters == null) {
            MultiLog.e(TAG, "  Invalid parameters.");
            return null;
        }

        List<Camera.Size> previewSizes = getFilteredSizes(parameters.getSupportedPreviewSizes(),
                maxSideSize);
        if (previewSizes == null) {
            MultiLog.e(TAG, new Throwable("Invalid preview sizes found."));
            return null;
        }

        List<Camera.Size> pictureSizes = getFilteredSizes(parameters.getSupportedPictureSizes(),
                maxSideSize);
        if (pictureSizes == null) {
            MultiLog.e(TAG, new Throwable("Invalid picture sizes found."));
            return null;
        }

        double epsilon = 1e-4;

        SizeExt previewSizeTest1 = getOptimalSize(targetWidth, targetHeight, maxSideSize,
                previewSizes);
        SizeExt pictureSizeTest1 = getOptimalSize(targetWidth, targetHeight, maxSideSize,
                pictureSizes);

        if (Util.floatEquals(previewSizeTest1.ratio, pictureSizeTest1.ratio, epsilon)) {
            MultiLog.d(TAG, "Found capture sizes that match screen ratio:");
            MultiLog.d(TAG, "  Preview: " + previewSizeTest1.width + " x " + previewSizeTest1.height
                + ", " + previewSizeTest1.ratio);
            MultiLog.d(TAG, "  Picture: " + pictureSizeTest1.width + " x " + pictureSizeTest1.height
                    + ", " + pictureSizeTest1.ratio);

            return new SizeExt[] {
                previewSizeTest1, pictureSizeTest1
            };
        }

        MultiLog.d(TAG, "Could not find sizes that matched screen ratio.");

        // No matches to screen ratio. Try to find best ratio match to each other.
        List<Camera.Size> previewSizesFiltered = new ArrayList<>();
        List<Camera.Size> pictureSizesFiltered = new ArrayList<>();
        HashSet<Integer> usedPictureIndexes = new HashSet<>();

        for (int i = 0; i < previewSizes.size(); i++) {

            Camera.Size preview = previewSizes.get(i);
            double ratioPreview = CameraUtils.ratio(preview);

            for (int j = 0; j < pictureSizes.size(); j++) {
                Camera.Size picture = pictureSizes.get(j);
                double ratioPicture = CameraUtils.ratio(picture);
                if (Util.floatEquals(ratioPicture, ratioPreview, epsilon)) {
                    if (!usedPictureIndexes.contains(j)) {
                        usedPictureIndexes.add(j);
                        pictureSizesFiltered.add(picture);
                    }
                    if (j == 0) {
                        previewSizesFiltered.add(preview);
                    }
                }
            }
        }

        // Find the closest sizes from the filtered lists.
        if (previewSizesFiltered.size() > 0 && pictureSizesFiltered.size() > 0) {
            MultiLog.d(TAG, "Finding optimal sizes from filtered lists.");

            SizeExt previewSizeTest2 = getOptimalSize(targetWidth, targetHeight,
                    maxSideSize,
                    previewSizesFiltered);
            SizeExt pictureSizeTest2 = getOptimalSize(previewSizeTest2.width, previewSizeTest2.height,
                    maxSideSize,
                    pictureSizesFiltered);

            MultiLog.d(TAG, "  Preview: " + previewSizeTest2.width + " x " + previewSizeTest2.height
                    + ", " + previewSizeTest2.ratio);
            MultiLog.d(TAG, "  Picture: " + pictureSizeTest2.width + " x " + pictureSizeTest2.height
                    + ", " + pictureSizeTest2.ratio);

            return new SizeExt[] {
                    previewSizeTest2, pictureSizeTest2
            };

        } else {

            MultiLog.d(TAG, "Could not find optimal capture sizes, using:");
            MultiLog.d(TAG, "  Preview: " + previewSizeTest1.width + " x " + previewSizeTest1.height
                    + ", " + previewSizeTest1.ratio);
            MultiLog.d(TAG, "  Picture: " + pictureSizeTest1.width + " x " + pictureSizeTest1.height
                    + ", " + pictureSizeTest1.ratio);

            return new SizeExt[] {
                    previewSizeTest1, pictureSizeTest1
            };
        }
    }

    public static double ratio(Camera.Size size) {
        return ratio(size.width, size.height);
    }

    public static double ratio(int width, int height) {
        return Math.min(width, height)
                / (double)Math.max(width, height);
    }

    private static ArrayList<Camera.Size> getFilteredSizes(List<Camera.Size> in, int maxSize) {
        ArrayList<Camera.Size> filtered = new ArrayList<>();
        for (Camera.Size size : in) {
            if (filterSize(size, maxSize)) {
                filtered.add(size);
            }
        }

        return filtered;
    }

    private static boolean filterSize(Camera.Size size, int maxSize) {
        if (maxSize < 1) {
            return true;
        }
        return size.width <= maxSize && size.height <= maxSize;
    }

    /**
     * http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation%28int%29
     */
    public static int getCameraDisplayOrientationRotation(Context context,
                                                          Camera.CameraInfo info)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    public static class SizeExt {
        int width;
        int height;
        double ratio;

        public SizeExt(Camera.Size size) {
            width = size.width;
            height = size.height;
            ratio = ratio(size);
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
