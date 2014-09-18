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
package com.funzio.pure2D.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Pure2DURI;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.loaders.tasks.URLLoadBitmapTask;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.UIConfig;

/**
 * @author long
 */

public class Pure2DUtils {
    public static final float PI_D2 = (float) Math.PI / 2f;
    public static final float DEGREE_TO_RADIAN = (float) Math.PI / 180;
    public static final float RADIAN_TO_DEGREE = 180 / (float) Math.PI;

    /**
     * Create a texture from an asset file
     * 
     * @param assetManager
     * @param assetPath
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getAssetBitmap(final AssetManager assetManager, final String assetPath, final TextureOptions options, final int[] outDimensions) {
        try {
            return getStreamBitmap(assetManager.open(assetPath), options, outDimensions);
        } catch (IOException e) {
            Log.e(Pure2D.TAG, "BITMAP LOADING ERROR:", e);
            return null;
        }
    }

    /**
     * Create a texture from a resource
     * 
     * @param resources
     * @param resourceID
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getResourceBitmap(final Resources resources, final int resourceID, TextureOptions options, final int[] outDimensions) {
        if (options == null) {
            options = TextureOptions.getDefault();
        }

        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceID, options);
        if (bitmap != null) {
            // resize to the specified scale
            bitmap = convertBitmap(bitmap, options, outDimensions);
        }

        return bitmap;
    }

    /**
     * Create a texture from a file with a specified path
     * 
     * @param filePath
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getFileBitmap(final String filePath, TextureOptions options, final int[] outDimensions) {
        if (options == null) {
            options = TextureOptions.getDefault();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (bitmap != null) {
            // resize to the specified scale
            bitmap = convertBitmap(bitmap, options, outDimensions);
        }

        return bitmap;
    }

    /**
     * Create a texture from a stream
     * 
     * @param stream
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getStreamBitmap(final InputStream stream, TextureOptions options, final int[] outDimensions) {
        if (options == null) {
            options = TextureOptions.getDefault();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        if (bitmap != null) {
            // resize to the specified scale
            bitmap = convertBitmap(bitmap, options, outDimensions);
        }

        return bitmap;
    }

    /**
     * Create a bitmap from a URL
     * 
     * @param url
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getURLBitmap(final String url, final TextureOptions options, final int[] outDimensions) {
        final URLLoadBitmapTask task = new URLLoadBitmapTask(url, options);
        if (task.run()) {
            Bitmap bitmap = task.getContent();
            if (bitmap != null) {
                // resize to the specified scale
                bitmap = convertBitmap(bitmap, options, outDimensions);
            }

            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * Create a bitmap from a uri. For example: asset://images/image1.png
     * 
     * @param resources only required when uri is @drawable/ or asset://
     * @param packageName only required when uri is @drawable/
     * @param uri
     * @param options
     * @param outDimensions
     * @return the bitmap
     * @see Pure2DURI
     */
    public static Bitmap getUriBitmap(final Resources resources, final String packageName, final String uri, final TextureOptions options, final int[] outDimensions) {
        final String actualPath = Pure2DURI.getPathFromUri(uri);
        Bitmap bitmap = null;

        // create
        if (uri.startsWith(Pure2DURI.DRAWABLE)) {
            // load from resources
            final int resId = resources.getIdentifier(actualPath, UIConfig.TYPE_DRAWABLE, packageName);
            if (resId > 0) {
                bitmap = getResourceBitmap(resources, resId, options, outDimensions);
            }
        } else if (uri.startsWith(Pure2DURI.FILE)) {
            // load from file / sdcard
            bitmap = getFileBitmap(actualPath, options, outDimensions);
        } else if (uri.startsWith(Pure2DURI.ASSET)) {
            // load from bundle assets
            bitmap = getAssetBitmap(resources.getAssets(), actualPath, options, outDimensions);
        } else if (uri.startsWith(Pure2DURI.HTTP)) {
            // load from http
            bitmap = getURLBitmap(actualPath, options, outDimensions);
        }

        return bitmap;
    }

    /**
     * Get dimensions of a Bitmap by its uri.
     * 
     * @param resources
     * @param packageName
     * @param uri
     * @param options
     * @param outDimensions
     * @return
     */
    public static boolean getUriBitmapDimensions(final Resources resources, final String packageName, final String uri, final TextureOptions options, final int[] outDimensions) {
        final String actualPath = Pure2DURI.getPathFromUri(uri);

        final BitmapFactory.Options temp = new BitmapFactory.Options();
        temp.inJustDecodeBounds = true; // for bounds only
        temp.inPurgeable = true;

        try {
            if (uri.startsWith(Pure2DURI.DRAWABLE)) {
                // load from resources
                final int resId = resources.getIdentifier(actualPath, UIConfig.TYPE_DRAWABLE, packageName);
                if (resId > 0) {
                    BitmapFactory.decodeResource(resources, resId, temp);
                }
            } else if (uri.startsWith(Pure2DURI.FILE)) {
                // load from file / sdcard
                BitmapFactory.decodeFile(actualPath, temp);
            } else if (uri.startsWith(Pure2DURI.ASSET)) {
                // load from bundle assets
                BitmapFactory.decodeStream(resources.getAssets().open(actualPath), null, temp);
            } else if (uri.startsWith(Pure2DURI.HTTP)) {
                // load from http
                final URLLoadBitmapTask task = new URLLoadBitmapTask(actualPath, temp);
                task.run();
            }

            if (outDimensions != null) {
                outDimensions[0] = options != null ? Math.round(temp.outWidth * options.inScaleX) : temp.outWidth;
                outDimensions[1] = options != null ? Math.round(temp.outHeight * options.inScaleY) : temp.outHeight;
            }

            return true;
        } catch (Exception e) {
            Log.e(Pure2D.TAG, "Failed to decode bitmap: " + uri, e);
            return false;
        }
    }

    /**
     * Create a bitmap with a specific text rendered on
     * 
     * @param text
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap getTextBitmap(final String text, final TextOptions options, final int[] outDimensions) {
        final TextOptions textOptions = (options == null) ? TextOptions.getDefault() : options;

        // find the bounds
        final Rect bounds = new Rect();
        textOptions.inTextPaint.getTextBounds(text, 0, text.length(), bounds);
        bounds.right += textOptions.inPaddingX * 2;
        bounds.bottom += textOptions.inPaddingY * 2;

        // create a new bitmap
        final FontMetrics metrics = new FontMetrics();
        textOptions.inTextPaint.getFontMetrics(metrics);
        Bitmap bitmap;
        if (textOptions.inBackground == null) {
            bitmap = Bitmap.createBitmap(bounds.width() + (int) (metrics.descent / 2), bounds.height() + (int) (metrics.descent), textOptions.inPreferredConfig);
        } else {
            bitmap = Bitmap.createBitmap(textOptions.inBackground.getWidth(), textOptions.inBackground.getHeight(), textOptions.inPreferredConfig);
        }

        // use a canvas to draw the text
        final Canvas canvas = new Canvas(bitmap);
        if (textOptions.inBackground != null) {
            canvas.drawBitmap(textOptions.inBackground, 0, 0, textOptions.inTextPaint);
        }
        final float textX = textOptions.inPaddingX + textOptions.inOffsetX + (bitmap.getWidth() - bounds.width()) / 2;
        final float textY = -textOptions.inPaddingY + textOptions.inOffsetY + (bitmap.getHeight() + bounds.height()) / 2;
        // draw the stroke
        if (textOptions.inStrokePaint != null) {
            canvas.drawText(text, textX, textY, textOptions.inStrokePaint);
        }
        // draw the text
        canvas.drawText(text, textX, textY, textOptions.inTextPaint);

        // resize to the specified scale
        bitmap = convertBitmap(bitmap, textOptions, outDimensions);

        return bitmap;
    }

    /**
     * Check and convert the bitmap to (Power of 2) if required
     * 
     * @param bitmap
     * @param options
     * @param outDimensions
     * @return
     */
    public static Bitmap convertBitmap(Bitmap bitmap, TextureOptions options, final int[] outDimensions) {
        if (options == null) {
            options = TextureOptions.getDefault();
        }

        // resize to the specified scale
        if (options.inScaleX != 1 || options.inScaleY != 1) {
            final int newWidth = Math.round(bitmap.getWidth() * options.inScaleX);
            final int newHeight = Math.round(bitmap.getHeight() * options.inScaleY);

            if (newWidth > 0 && newHeight > 0) {
                final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                if (newBitmap != bitmap) {
                    bitmap.recycle();
                }
                bitmap = newBitmap;
            }
        }

        if (options.inPo2) {
            bitmap = scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null && bitmap != null) {
                outDimensions[0] = bitmap.getWidth();
                outDimensions[1] = bitmap.getHeight();
            }
        }

        return bitmap;
    }

    /**
     * Scale the specified bitmap to the size of the closest-power-of-2 of the current size
     * 
     * @param bitmap
     * @param outDimensions
     * @return
     */
    public static Bitmap scaleBitmapToPo2(final Bitmap bitmap, final int[] outDimensions) {
        // null check for input bitmap
        if (bitmap == null) {
            return null;
        }

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // also output the original width and height
        if (outDimensions != null) {
            outDimensions[0] = originalWidth;
            outDimensions[1] = originalHeight;
        }

        int powWidth = getNextPO2(originalWidth);
        int powHeight = getNextPO2(originalHeight);

        if (originalWidth == powWidth && originalHeight == powHeight) {
            return bitmap;
        } else {
            Bitmap po2Bitmap;
            Config config = bitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            try {
                po2Bitmap = Bitmap.createBitmap(powWidth, powHeight, config);
            } catch (OutOfMemoryError e) {
                try {
                    po2Bitmap = Bitmap.createBitmap(powWidth, powHeight, config);
                } catch (OutOfMemoryError e1) {
                    if (config == Bitmap.Config.ARGB_8888) {
                        try {
                            // try with lower quality
                            Log.w(Pure2D.TAG, "BITMAP CREATION FALLBACK: " + powWidth + " x " + powHeight, e1);
                            po2Bitmap = Bitmap.createBitmap(powWidth, powHeight, Bitmap.Config.ARGB_4444);
                        } catch (OutOfMemoryError e2) {
                            Log.e(Pure2D.TAG, "BITMAP CREATION FALLBACK ERROR: " + powWidth + " x " + powHeight, e2);
                            // crash prevention
                            return null;
                        }
                    } else {
                        Log.e(Pure2D.TAG, "BITMAP CREATION ERROR: " + powWidth + " x " + powHeight, e);
                        // crash prevention
                        return null;
                    }
                }
            }

            // wtf? po2Bitmap can be null on Huawei U9200?
            if (po2Bitmap == null) {
                Log.e(Pure2D.TAG, "BITMAP NULL ERROR: " + powWidth + " x " + powHeight, new Exception());
                return null;
            } else {
                final Canvas canvas = new Canvas(po2Bitmap);
                canvas.drawBitmap(bitmap, 0, 0, null);
                bitmap.recycle();
                return po2Bitmap;
            }
        }
    }

    /**
     * non-premultiplied alpha version of GLUtils.texImage2D(). Note: this method is Slow and should only be used when really necessary!
     * 
     * @param gl
     * @param bitmap
     * @see GLUtils.texImage2D()
     */
    public static void texImage2DNonPremultipliedAlpha(final GL10 gl, final Bitmap bitmap) {
        final int[] pixels = extractPixels(bitmap);
        final byte[] pixelComponents = new byte[pixels.length * 4];
        int byteIndex = 0, p;
        for (int i = 0; i < pixels.length; i++) {
            p = pixels[i];
            // Convert to byte representation RGBA required by gl.glTexImage2D.
            pixelComponents[byteIndex++] = (byte) ((p >> 16) & 0xFF); // red
            pixelComponents[byteIndex++] = (byte) ((p >> 8) & 0xFF); //
            pixelComponents[byteIndex++] = (byte) ((p) & 0xFF); // blue
            pixelComponents[byteIndex++] = (byte) (p >> 24); // alpha
        }
        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixelComponents));
    }

    // /**
    // * non-premultiplied alpha version of GLUtils.texImage2D(). Note: this method is Slow and should only be used when really necessary!
    // *
    // * @param gl
    // * @param bitmap
    // * @see GLUtils.texImage2D()
    // */
    // public static void texImage2DNonPremultipliedAlpha(final GL10 gl, final Bitmap bitmap) {
    // final int width = bitmap.getWidth();
    // final int height = bitmap.getHeight();
    // final int size = width * height;
    // final byte[] pixelComponents = new byte[size * 4];
    // int byteIndex = 0, p;
    // for (int i = 0; i < size; i++) {
    // p = bitmap.getPixel(i % width, i / width);
    // // Convert to byte representation RGBA required by gl.glTexImage2D.
    // pixelComponents[byteIndex++] = (byte) ((p >> 16) & 0xFF); // red
    // pixelComponents[byteIndex++] = (byte) ((p >> 8) & 0xFF); //
    // pixelComponents[byteIndex++] = (byte) ((p) & 0xFF); // blue
    // pixelComponents[byteIndex++] = (byte) (p >> 24); // alpha
    // }
    // gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixelComponents));
    // }

    public static int[] extractPixels(final Bitmap bitmap) {
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int[] colors = new int[w * h];
        bitmap.getPixels(colors, 0, w, 0, 0, w, h);
        return colors;
    }

    /**
     * Scale the specified bitmap to the size of the closest-power-of-2 of the current size
     * 
     * @param bitmap
     * @param outDimensions
     * @return
     */
    // public static Bitmap scaleBitmapToPo2Old(final Bitmap bitmap, final int[] outDimensions) {
    // int originalWidth = bitmap.getWidth();
    // int originalHeight = bitmap.getHeight();
    //
    // // also output the original width and height
    // if (outDimensions != null) {
    // outDimensions[0] = originalWidth;
    // outDimensions[1] = originalHeight;
    // }
    //
    // int powWidth = getNextPO2(originalWidth);
    // int powHeight = getNextPO2(originalHeight);
    //
    // if (originalWidth == powWidth && originalHeight == powHeight) {
    // return bitmap;
    // } else {
    // final Bitmap po2Bitmap = Bitmap.createScaledBitmap(bitmap, powWidth, powHeight, true);
    // bitmap.recycle();
    // return po2Bitmap;
    // }
    // }

    /**
     * Calculates the next highest power of two for a given integer.
     * 
     * @param n the number
     * @return a power of two equal to or higher than n
     */
    public static int getNextPO2(int n) {
        n -= 1;
        n = n | (n >> 1);
        n = n | (n >> 2);
        n = n | (n >> 4);
        n = n | (n >> 8);
        n = n | (n >> 16);
        return n + 1;
    }

    public static boolean isPO2(final int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Find the smallest area that contains multiples rect defined by width & height
     * 
     * @param width
     * @param height
     * @param num
     * @return
     */
    public static Point getSmallestTextureSize(final int width, final int height, final int num, final int maxTextureSize, final boolean forcePo2) {
        int minWidth = 0;
        int minHeight = 0;
        int minArea = Integer.MAX_VALUE;
        for (int row = 1; row <= num; row++) {
            int col = (int) Math.ceil((float) num / (float) row);
            int po2Width = forcePo2 ? getNextPO2(col * width) : col * width;
            int po2Height = forcePo2 ? getNextPO2(row * height) : row * height;
            int area = po2Width * po2Height;
            if (area < minArea && po2Width <= maxTextureSize) {
                minArea = area;
                minWidth = po2Width;
                minHeight = (po2Height < maxTextureSize) ? po2Height : maxTextureSize;
            }
        }

        return new Point(minWidth, minHeight);
    }

    public static void getMatrix3DValues(final Matrix matrix2D, final float[] matrix3D) {
        matrix2D.getValues(matrix3D);
        // Log.e("long", matrix2D.toShortString());
        final float v0 = matrix3D[0];
        final float v1 = matrix3D[1];
        final float v2 = matrix3D[2];
        final float v3 = matrix3D[3];
        final float v4 = matrix3D[4];
        final float v5 = matrix3D[5];
        final float v6 = matrix3D[6];
        final float v7 = matrix3D[7];
        final float v8 = matrix3D[8];

        matrix3D[0] = v0;
        matrix3D[4] = v1;
        matrix3D[8] = v2;

        matrix3D[1] = v3;
        matrix3D[5] = v4;
        matrix3D[9] = v5;

        matrix3D[2] = v6;
        matrix3D[6] = v7;
        matrix3D[10] = v8;

        matrix3D[3] = matrix3D[7] = matrix3D[11] = 0;

        matrix3D[12] = matrix3D[13] = matrix3D[14] = 0;
        matrix3D[15] = 1;
    }
}
