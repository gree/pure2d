/**
 * 
 */
package com.funzio.pure2D.utils.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author sajjadtabib
 *
 */
public class BitmapUtils {

    public static Bitmap getSubSampledBitmap(final Resources res, final int resourceId, final int reqWidth, final int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceId, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; //use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resourceId, options);
    }

    public static Bitmap getSubSampledBitmap(final String fileName, final int reqWidth, final int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; //use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(fileName, options);
    }

    private static int calculateSampleSize(final int reqWidth, final int reqHeight, final BitmapFactory.Options options) {

        int actualImageHeight = options.outHeight;
        int actualImageWidth = options.outWidth;

        int sampleSize = 1;

        if (reqWidth < actualImageWidth || reqHeight < actualImageHeight) {
            if (actualImageWidth > actualImageHeight) {
                sampleSize = Math.round((float) actualImageHeight / (float) reqHeight);
            } else {
                sampleSize = Math.round((float) actualImageWidth / (float) reqWidth);
            }
        }
        return sampleSize;
    }
}
