/**
 * 
 */
package com.funzio.pure2D.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.FloatMath;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.text.TextOptions;

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

        // resize to the specified size
        if (options.inScaleX != 1 || options.inScaleY != 1) {
            final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * options.inScaleX), Math.round(bitmap.getHeight() * options.inScaleY), true);
            bitmap.recycle();
            bitmap = newBitmap;
        }

        if (options.inPo2) {
            bitmap = scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null) {
                outDimensions[0] = bitmap.getWidth();
                outDimensions[1] = bitmap.getHeight();
            }
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

        if (bitmap == null) {
            return null;
        }

        // resize to the specified size
        if (options.inScaleX != 1 || options.inScaleY != 1) {
            final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * options.inScaleX), Math.round(bitmap.getHeight() * options.inScaleY), true);
            bitmap.recycle();
            bitmap = newBitmap;
        }

        if (options.inPo2) {
            bitmap = scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null) {
                outDimensions[0] = bitmap.getWidth();
                outDimensions[1] = bitmap.getHeight();
            }
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

        if (bitmap == null) {
            return null;
        }

        // resize to the specified size
        if (options.inScaleX != 1 || options.inScaleY != 1) {
            final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * options.inScaleX), Math.round(bitmap.getHeight() * options.inScaleY), true);
            bitmap.recycle();
            bitmap = newBitmap;
        }

        if (options.inPo2) {
            bitmap = scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null) {
                outDimensions[0] = bitmap.getWidth();
                outDimensions[1] = bitmap.getHeight();
            }
        }

        return bitmap;
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

        // resize to the specified size
        if (textOptions.inScaleX != 1 || textOptions.inScaleY != 1) {
            final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * textOptions.inScaleX), Math.round(bitmap.getHeight() * textOptions.inScaleY), true);
            bitmap.recycle();
            bitmap = newBitmap;
        }

        if (textOptions.inPo2) {
            bitmap = scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null) {
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
            try {
                po2Bitmap = Bitmap.createBitmap(powWidth, powHeight, bitmap.getConfig());
            } catch (OutOfMemoryError e) {
                try {
                    // try again with GC, not a good way but...
                    System.gc(); // FIXME this is not practical
                    po2Bitmap = Bitmap.createBitmap(powWidth, powHeight, bitmap.getConfig());
                } catch (OutOfMemoryError e1) {
                    if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
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

            final Canvas canvas = new Canvas(po2Bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();
            return po2Bitmap;
        }
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
        n = n | (n >> 32);
        return n + 1;
    }

    /**
     * Find the smallest area that contains multiples rect defined by width & height
     * 
     * @param width
     * @param height
     * @param num
     * @return
     */
    public static Point getSmallestTextureSize(final int width, final int height, final int num, final int maxTextureSize) {
        int minWidth = 0;
        int minHeight = 0;
        int minArea = Integer.MAX_VALUE;
        for (int row = 1; row <= num; row++) {
            int col = (int) FloatMath.ceil((float) num / (float) row);
            int po2Width = getNextPO2(col * width);
            int po2Height = getNextPO2(row * height);
            int area = po2Width * po2Height;
            if (area < minArea && po2Width <= maxTextureSize) {
                minArea = area;
                minWidth = po2Width;
                minHeight = (po2Height < maxTextureSize) ? po2Height : maxTextureSize;
            }
        }

        return new Point(minWidth, minHeight);
    }
}
