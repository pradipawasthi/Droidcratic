package org.socratic.android.util;

import android.graphics.Bitmap;

/**
 * Created by pcnofelt on 3/30/17.
 */

public class BitmapUtil {

    public final static int DIRECTION_FORWARD = 1;
    public final static int DIRECTION_BACK = -1;

    public static int getRowWithPixel(Bitmap bitmap, int direction) {

        int pArray[] = new int[bitmap.getWidth()];

        int rowStart = direction == DIRECTION_FORWARD ? 0 : bitmap.getHeight() - 1;

        for (int row = rowStart; (direction == DIRECTION_FORWARD ? row < bitmap.getHeight() : row >= 0); row += direction) {
            bitmap.getPixels(pArray,
                    0, bitmap.getWidth(),
                    0/*x-start*/, row,
                    bitmap.getWidth(), 1);

            if (hasColorPixel(pArray)) {
                return row;
            }
        }

        return -1;
    }

    public static int getColumnWithPixel(Bitmap bitmap, int direction) {

        int pArray[] = new int[bitmap.getHeight()];

        int columnStart = direction == DIRECTION_FORWARD ? 0 : bitmap.getWidth() - 1;

        for (int column = columnStart; (direction == DIRECTION_FORWARD ? column < bitmap.getWidth() : column >= 0); column += direction) {
            bitmap.getPixels(pArray,
                    0, 1,
                    column/*y-start*/, 0,
                    1, bitmap.getHeight());

            if (hasColorPixel(pArray)) {
                return column;
            }
        }

        return -1;
    }

    private static boolean hasColorPixel(int[] pixels) {
        for (int pixel : pixels) {
            if (pixel != 0) {
                return true;
            }
        }

        return false;
    }
}
