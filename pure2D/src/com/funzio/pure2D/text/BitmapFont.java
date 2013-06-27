/**
 * 
 */
package com.funzio.pure2D.text;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;

import com.funzio.pure2D.atlas.AtlasFrame;
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

    private HashMap<Character, AtlasFrame> mCharFrames = new HashMap<Character, AtlasFrame>();
    private RectPacker mRectPacker;
    private PointF[] mOffsets;

    public BitmapFont(final String characters, final TextOptions textOptions) {
        mTextOptions = (textOptions == null) ? TextOptions.getDefault() : textOptions;
        mCharacters = characters;

        mRectPacker = new RectPacker(512, mTextOptions.inPo2);
        mRectPacker.setQuickMode(false);
        mRectPacker.setRotationEnabled(false);
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

            AtlasFrame frame;
            final int length = mCharacters.length();
            for (int i = 0; i < length; i++) {
                final char ch = mCharacters.charAt(i);

                frame = new AtlasFrame(mTexture, i, String.valueOf(ch), new RectF(mRectPacker.getRect(i)));
                frame.mOffset = mOffsets[i];
                mCharFrames.put(ch, frame);
            }
        }

        return mTexture;
    }

    public String getCharacters() {
        return mCharacters;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public AtlasFrame getCharFrame(final char ch) {
        return mCharFrames.get(ch);
    }

    @SuppressWarnings("deprecation")
    protected Bitmap createBitmap(final int[] outDimensions) {
        final Rect bounds = new Rect();
        final FontMetrics metrics = new FontMetrics();
        mTextOptions.inTextPaint.getFontMetrics(metrics);

        long start = SystemClock.elapsedRealtime();
        // find the bounds
        final int length = mCharacters.length();
        float[] positions = new float[length * 2];
        mOffsets = new PointF[length];
        // float totalOffsetX = (mTextOptions.inOffsetX) * mTextOptions.inScaleX;
        // float totalOffsetY = (mTextOptions.inOffsetY) * mTextOptions.inScaleY;
        Rect charRect;
        for (int i = 0; i < length; i++) {
            final String ch = mCharacters.substring(i, i + 1);

            // find text bounds
            mTextOptions.inTextPaint.getTextBounds(ch, 0, 1, bounds);
            // inflate by padding
            bounds.inset(-Math.round(mTextOptions.inPaddingX * 2 * mTextOptions.inScaleX), -Math.round(mTextOptions.inPaddingY * 2 * mTextOptions.inScaleY));

            // occupy
            charRect = mRectPacker.occupy(Math.round((bounds.width()) * mTextOptions.inScaleX), Math.round((bounds.height()) * mTextOptions.inScaleY));

            // find positions
            positions[i * 2] = charRect.left / mTextOptions.inScaleX - bounds.left;
            positions[i * 2 + 1] = charRect.top / mTextOptions.inScaleY - bounds.top;
            mOffsets[i] = new PointF(bounds.left, bounds.top);
        }
        long time = SystemClock.elapsedRealtime() - start;
        start = SystemClock.elapsedRealtime();
        Log.e("long", "=> " + mRectPacker.getWidth() + " " + mRectPacker.getHeight() + " " + time + "ms");

        // create a new bitmap
        final Bitmap bitmap = Bitmap.createBitmap(mRectPacker.getWidth(), mRectPacker.getHeight(), mTextOptions.inPreferredConfig);

        // use a canvas to draw the text
        final Canvas canvas = new Canvas(bitmap);
        // apply scale
        if (mTextOptions.inScaleX != 1 || mTextOptions.inScaleY != 1) {
            canvas.scale(mTextOptions.inScaleX, mTextOptions.inScaleY);
        }
        if (mTextOptions.inBackground != null) {
            canvas.drawBitmap(mTextOptions.inBackground, 0, 0, mTextOptions.inTextPaint);
        }

        // draw all chars at once
        if (mTextOptions.inStrokePaint != null) {
            canvas.drawPosText(mCharacters, positions, mTextOptions.inStrokePaint);
        }
        // draw the text
        canvas.drawPosText(mCharacters, positions, mTextOptions.inTextPaint);

        // manually draw 1 by 1 char
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

        time = SystemClock.elapsedRealtime() - start;
        Log.e("long", "draw time: " + time + "ms");

        if (outDimensions != null) {
            outDimensions[0] = bitmap.getWidth();
            outDimensions[1] = bitmap.getHeight();
        }

        return bitmap;
    }
}
