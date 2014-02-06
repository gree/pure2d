/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.utils.bitmap;

import java.io.FileDescriptor;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * @author sajjadtabib
 */
public class BitmapUtils {

    public static BitmapFactory.Options getBitmapOptionsForSubSampling(final InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeStream(is, null, options);
        return options;
    }

    public static Bitmap getSubSampledBitmap(final InputStream is, final int reqWidth, final int reqHeight, final Options options) {
        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; // use sample size
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap getSubSampledBitmap(final Resources res, final int resourceId, final int reqWidth, final int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeResource(res, resourceId, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; // use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resourceId, options);
    }

    public static Bitmap getSubSampledBitmap(final String fileName, final int reqWidth, final int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(fileName, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; // use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(fileName, options);
    }

    public static Bitmap getSubSampledBitmap(final FileDescriptor fd, final int reqWidth, final int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; // use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    public static Bitmap getSubSampledBitmap(final InputStream is, final int reqWidth, final int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeStream(is, null, options);

        int sampleSize = BitmapUtils.calculateSampleSize(reqWidth, reqHeight, options);

        options.inSampleSize = sampleSize; // use sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(is, null, options);
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

    public static Drawable getSubSampledDrawable(final Resources res, final int resourceId, final int reqWidth, final int reqHeight) {
        Bitmap bitmap = getSubSampledBitmap(res, resourceId, reqWidth, reqHeight);
        if (bitmap.getNinePatchChunk() != null) {
            return new NinePatchDrawable(res, bitmap, bitmap.getNinePatchChunk(), new Rect(), null);
        }

        return new BitmapDrawable(res, bitmap);
    }
}
