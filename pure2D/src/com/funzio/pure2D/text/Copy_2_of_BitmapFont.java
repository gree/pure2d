/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class Copy_2_of_BitmapFont {

    private final TextOptions mTextOptions;
    private final String mCharacters;
    private Texture mTexture;

    public Copy_2_of_BitmapFont(final String characters, final TextOptions textOptions) {
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
        final FontMetrics metrics = new FontMetrics();
        mTextOptions.inTextPaint.getFontMetrics(metrics);

        // find the bounds
        mTextOptions.inTextPaint.getTextBounds(mCharacters, 0, mCharacters.length(), bounds);
        bounds.inset(-Math.round(mTextOptions.inPaddingX * 2), -Math.round(mTextOptions.inPaddingY * 2));

        // create a new bitmap
        Bitmap bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), mTextOptions.inPreferredConfig);

        // use a canvas to draw the text
        final Canvas canvas = new Canvas(bitmap);
        if (mTextOptions.inBackground != null) {
            canvas.drawBitmap(mTextOptions.inBackground, 0, 0, mTextOptions.inTextPaint);
        }
        final float textX = mTextOptions.inPaddingX + mTextOptions.inOffsetX - bounds.left;
        final float textY = mTextOptions.inPaddingY + mTextOptions.inOffsetY - bounds.top;
        // draw the stroke
        if (mTextOptions.inStrokePaint != null) {
            canvas.drawText(mCharacters, textX, textY, mTextOptions.inStrokePaint);
        }
        // draw the text
        canvas.drawText(mCharacters, textX, textY, mTextOptions.inTextPaint);

        // debug
        mTextOptions.inStrokePaint.setColor(Color.RED);
        final int length = mCharacters.length();
        for (int i = 0; i < length; i++) {
            mTextOptions.inStrokePaint.getTextBounds(mCharacters, i, i + 1, bounds);
            bounds.offset(Math.round(textX - bounds.left), Math.round(textY - bounds.top));
            canvas.drawRect(bounds, mTextOptions.inStrokePaint);
            // Log.e("long", bounds.left + " " + bounds.top);
        }

        // resize to the specified size
        if (mTextOptions.inScaleX != 1 || mTextOptions.inScaleY != 1) {
            final Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * mTextOptions.inScaleX), Math.round(bitmap.getHeight() * mTextOptions.inScaleY), true);
            bitmap.recycle();
            bitmap = newBitmap;
        }

        if (mTextOptions.inPo2) {
            bitmap = Pure2DUtils.scaleBitmapToPo2(bitmap, outDimensions);
        } else {
            // also output the original width and height
            if (outDimensions != null) {
                outDimensions[0] = bitmap.getWidth();
                outDimensions[1] = bitmap.getHeight();
            }
        }

        return bitmap;
    }
}
