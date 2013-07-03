/**
 * 
 */
package com.funzio.pure2D.text;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.funzio.pure2D.Pure2D;
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
    private BitmapFontMetrics mFontMetrics;

    public BitmapFont(final String characters, final TextOptions textOptions) {
        mTextOptions = (textOptions == null) ? TextOptions.getDefault() : textOptions;
        mCharacters = characters;

        mFontMetrics = new BitmapFontMetrics(mTextOptions);

        mRectPacker = new RectPacker(Math.min(512, Pure2D.GL_MAX_TEXTURE_SIZE), mTextOptions.inPo2);
        mRectPacker.setQuickMode(true);
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
            char ch;
            for (int i = 0; i < length; i++) {
                ch = mCharacters.charAt(i);

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

    public TextOptions getTextOptions() {
        return mTextOptions;
    }

    public BitmapFontMetrics getFontMetrics() {
        return mFontMetrics;
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

        // long start = SystemClock.elapsedRealtime();

        // find the bounds
        final int length = mCharacters.length();
        float[] positions = new float[length * 2];
        mOffsets = new PointF[length];
        // float totalOffsetX = (mTextOptions.inOffsetX) * mTextOptions.inScaleX;
        // float totalOffsetY = (mTextOptions.inOffsetY) * mTextOptions.inScaleY;
        Rect charRect;
        for (int i = 0; i < length; i++) {
            final char ch = mCharacters.charAt(i);

            // find text bounds
            mTextOptions.inTextPaint.getTextBounds(String.valueOf(ch), 0, 1, bounds);
            // inflate by padding
            bounds.inset(-Math.round(mFontMetrics.letterPaddingX), -Math.round(mFontMetrics.letterPaddingY));

            // occupy
            charRect = mRectPacker.occupy(Math.round((bounds.right - bounds.left + 1) * mTextOptions.inScaleX), Math.round((bounds.bottom - bounds.top + 1) * mTextOptions.inScaleY));

            // find positions
            positions[i * 2] = charRect.left / mTextOptions.inScaleX - bounds.left;
            positions[i * 2 + 1] = charRect.top / mTextOptions.inScaleY - bounds.top;
            // save offset
            mOffsets[i] = new PointF(bounds.left * mTextOptions.inScaleX, -bounds.top * mTextOptions.inScaleY);
        }

        // long time = SystemClock.elapsedRealtime() - start;
        // start = SystemClock.elapsedRealtime();
        // Log.e("long", "=> " + mRectPacker.getWidth() + " " + mRectPacker.getHeight() + " " + time + "ms");

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

        // time = SystemClock.elapsedRealtime() - start;
        // Log.e("long", "draw time: " + time + "ms");

        if (outDimensions != null) {
            outDimensions[0] = bitmap.getWidth();
            outDimensions[1] = bitmap.getHeight();
        }

        return bitmap;
    }
}
