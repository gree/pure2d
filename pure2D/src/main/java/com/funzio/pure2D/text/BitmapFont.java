/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.utils.RectBinPacker;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class BitmapFont {
    private static final String TAG = BitmapFont.class.getSimpleName();

    private final TextOptions mTextOptions;
    private final String mCharacters;
    private Texture mTexture;

    private HashMap<Character, AtlasFrame> mCharFrames = new HashMap<Character, AtlasFrame>();
    private RectBinPacker mRectPacker;
    private PointF[] mCharOffsets;
    private float[] mCharPositions;
    private BitmapFontMetrics mFontMetrics;

    private TextureManager.TextureRunnable mLoadRunnable = new TextureManager.TextureRunnable() {
        @Override
        public void run(final Texture texture) {
            final Bitmap bitmap = createBitmap();
            texture.load(bitmap, bitmap.getWidth(), bitmap.getHeight(), mTextOptions.inMipmaps);
            bitmap.recycle();
        }
    };

    public BitmapFont(final String characters, final TextOptions textOptions) {
        this(characters, textOptions, 0);
    }

    public BitmapFont(final String characters, final TextOptions textOptions, int textureMaxSize) {
        mTextOptions = (textOptions == null) ? TextOptions.getDefault() : textOptions;
        mCharacters = characters;

        mFontMetrics = new BitmapFontMetrics(mTextOptions);

        // auto size
        if (textureMaxSize <= 0) {
            textureMaxSize = Pure2D.GL_MAX_TEXTURE_SIZE;
        }
        mRectPacker = new RectBinPacker(Math.min(textureMaxSize, Pure2D.GL_MAX_TEXTURE_SIZE), mTextOptions.inPo2);
        mRectPacker.setRotationEnabled(false);
    }

    /**
     * This can only called on GL Thread
     *
     * @param glState
     * @return
     */
    public Texture load(final GLState glState) {
        return load(glState.getTextureManager());
    }

    /**
     * This can only called on GL Thread
     *
     * @param textureManager
     * @return
     */
    public Texture load(final TextureManager textureManager) {
        Log.i(TAG, String.format("load(): %d chars, %s", mCharacters.length(), mTextOptions.toString()));

        if (mTexture == null) {
            mTexture = textureManager.createDynamicTexture(mLoadRunnable);

            mTexture.reload();
            mTexture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR); // better output

            AtlasFrame frame;
            final int length = mCharacters.length();
            char ch;
            for (int i = 0; i < length; i++) {
                ch = mCharacters.charAt(i);

                frame = new AtlasFrame(mTexture, i, String.valueOf(ch), new RectF(mRectPacker.getRect(i)));
                frame.mOffset = mCharOffsets[i];
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

    protected void findCharOffsets() {
        final Rect bounds = new Rect();

        final long start = SystemClock.elapsedRealtime();

        // find the bounds
        final int length = mCharacters.length();
        mCharPositions = new float[length * 2];
        mCharOffsets = new PointF[length];
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
            mCharPositions[i * 2] = charRect.left / mTextOptions.inScaleX - bounds.left;
            mCharPositions[i * 2 + 1] = charRect.top / mTextOptions.inScaleY - bounds.top;
            // save offset
            mCharOffsets[i] = new PointF(bounds.left * mTextOptions.inScaleX, -bounds.top * mTextOptions.inScaleY);
        }

        Log.i(TAG, String.format("Packing Result: (%d, %d) in %d ms", mRectPacker.getWidth(), mRectPacker.getHeight(), SystemClock.elapsedRealtime() - start));
    }

    @SuppressWarnings("deprecation")
    protected Bitmap createBitmap() {
        if (mCharOffsets == null) {
            findCharOffsets();
        }

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
            canvas.drawPosText(mCharacters, mCharPositions, mTextOptions.inStrokePaint);
        }
        // draw the text
        canvas.drawPosText(mCharacters, mCharPositions, mTextOptions.inTextPaint);

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

        return bitmap;
    }

    @Override
    public String toString() {
        return mTextOptions.toString();
    }
}
