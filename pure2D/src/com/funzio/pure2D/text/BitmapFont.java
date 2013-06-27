/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.utils.RectPacker;

/**
 * @author long
 */
public class BitmapFont {

    private final TextOptions mTextOptions;
    private final String mCharacters;
    private Texture mTexture;

    public BitmapFont(final String characters, final TextOptions textOptions) {
        mTextOptions = (textOptions == null) ? TextOptions.getDefault() : textOptions;
        mCharacters = characters;
    }

    /**
     * This can only called on GL Thread
     * 
     * @param glState
     * @return
     */
    public Texture load(final GLState glState) {
        if (mTexture == null) {
            int[] dimensions = new int[2];
            final Bitmap bitmap = createBitmap(dimensions);
            mTexture = new Texture(glState);
            mTexture.load(bitmap, dimensions[0], dimensions[1], 0);
        }

        return mTexture;
    }

    public Texture getTexture() {
        return mTexture;
    }

    protected Bitmap createBitmap(final int[] outDimensions) {
        final Rect bounds = new Rect();
        final RectPacker packer = new RectPacker(512, mTextOptions.inPo2);
        packer.setQuickMode(false);
        packer.setRotationEnabled(false);
        final FontMetrics metrics = new FontMetrics();
        mTextOptions.inTextPaint.getFontMetrics(metrics);

        long start = SystemClock.elapsedRealtime();
        // find the bounds
        final int length = mCharacters.length();
        float[] positions = new float[length * 2];
        Rect rect;
        for (int i = 0; i < length; i++) {
            final String ch = mCharacters.substring(i, i + 1);
            mTextOptions.inTextPaint.getTextBounds(ch, 0, 1, bounds);
            bounds.inset(-Math.round(mTextOptions.inPaddingX * 2 * mTextOptions.inScaleX), -Math.round(mTextOptions.inPaddingY * 2 * mTextOptions.inScaleY));

            rect = packer.occupy(Math.round((bounds.width() + metrics.descent) * mTextOptions.inScaleX), Math.round((bounds.height() + metrics.descent) * mTextOptions.inScaleY));

            positions[i * 2] = rect.left + (mTextOptions.inPaddingX + mTextOptions.inOffsetX) * mTextOptions.inScaleX - bounds.left;
            positions[i * 2 + 1] = rect.top + (mTextOptions.inPaddingY + mTextOptions.inOffsetY) * mTextOptions.inScaleY - bounds.top;
            // Log.e("long", ch + " " + bounds.left + " " + bounds.top);
        }
        long time = SystemClock.elapsedRealtime() - start;
        start = SystemClock.elapsedRealtime();
        Log.e("long", "=> " + packer.getWidth() + " " + packer.getHeight() + " " + time + "ms");

        // create a new bitmap
        final Bitmap bitmap = Bitmap.createBitmap(packer.getWidth(), packer.getHeight(), mTextOptions.inPreferredConfig);

        // use a canvas to draw the text
        final Canvas canvas = new Canvas(bitmap);
        canvas.scale(mTextOptions.inScaleX, mTextOptions.inScaleY);
        if (mTextOptions.inBackground != null) {
            canvas.drawBitmap(mTextOptions.inBackground, 0, 0, mTextOptions.inTextPaint);
        }

        Rect charRect;
        // for (int i = 0; i < length; i++) {
        // charRect = packer.getRect(i);
        // final float textX = charRect.left + (mTextOptions.inPaddingX + mTextOptions.inOffsetX) * mTextOptions.inScaleX;
        // final float textY = charRect.top + (mTextOptions.inPaddingY + mTextOptions.inOffsetY) * mTextOptions.inScaleY;
        // final String ch = mCharacters.substring(i, i + 1);
        // // draw the stroke
        // if (mTextOptions.inStrokePaint != null) {
        // canvas.drawText(ch, textX, textY, mTextOptions.inStrokePaint);
        // }
        // // draw the text
        // canvas.drawText(ch, textX, textY, mTextOptions.inTextPaint);
        // }

        if (mTextOptions.inStrokePaint != null) {
            canvas.drawPosText(mCharacters, positions, mTextOptions.inStrokePaint);
        }
        // draw the text
        canvas.drawPosText(mCharacters, positions, mTextOptions.inTextPaint);

        time = SystemClock.elapsedRealtime() - start;
        Log.e("long", "draw time: " + time + "ms");

        if (outDimensions != null) {
            outDimensions[0] = bitmap.getWidth();
            outDimensions[1] = bitmap.getHeight();
        }

        // // resize to the specified size
        // if (mTextOptions.inScaleX != 1 || mTextOptions.inScaleY != 1) {
        // final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * mTextOptions.inScaleX), Math.round(bitmap.getHeight() * mTextOptions.inScaleY), true);
        // bitmap.recycle();
        // bitmap = newBitmap;
        // }
        //
        // if (mTextOptions.inPo2) {
        // bitmap = Pure2DUtils.scaleBitmapToPo2(bitmap, outDimensions);
        // } else {
        // // also output the original width and height
        // if (outDimensions != null) {
        // outDimensions[0] = bitmap.getWidth();
        // outDimensions[1] = bitmap.getHeight();
        // }
        // }

        return bitmap;
    }
}
