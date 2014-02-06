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
package com.funzio.pure2D.atlas;

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class AtlasFrame {
    protected Atlas mAtlas;
    protected RectF mRect = new RectF();
    protected float[] mTextureCoords = new float[8];
    protected PointF mSize = new PointF();

    public PointF mOffset = null;
    protected Texture mTexture;

    protected final int mIndex;
    protected final String mName;

    public AtlasFrame(final Atlas atlas, final int index, final String name, final float left, final float top, final float right, final float bottom) {
        mIndex = index;
        mAtlas = atlas;
        setRect(left, top, right, bottom);
        mName = name;
    }

    public AtlasFrame(final Atlas atlas, final int index, final String name, final RectF rect) {
        this(atlas, index, name, rect.left, rect.top, rect.right, rect.bottom);
    }

    public AtlasFrame(final Texture texture, final int index, final String name, final RectF rect) {
        mIndex = index;
        mTexture = texture;
        setRect(rect);
        mName = name;
    }

    public AtlasFrame(final Texture texture, final int index, final String name) {
        mIndex = index;
        mTexture = texture;
        mName = name;

        // auto set rect to match texture size
        if (texture != null) {
            setRect(0, 0, (int) texture.getSize().x - 1, (int) texture.getSize().y - 1);
        }
    }

    public void setRect(final float left, final float top, final float right, final float bottom) {
        mRect.left = left;
        mRect.right = right;
        mRect.top = top;
        mRect.bottom = bottom;

        // pre-cal
        mSize.x = Math.abs(right - left + 1);
        mSize.y = Math.abs(bottom - top + 1);

        if (mTexture != null) {
            final PointF size = mTexture.getSize();
            // TL
            mTextureCoords[0] = left / size.x;
            mTextureCoords[1] = top / size.y;
            // BL
            mTextureCoords[2] = left / size.x;
            mTextureCoords[3] = bottom / size.y;
            // TR
            mTextureCoords[4] = right / size.x;
            mTextureCoords[5] = top / size.y;
            // BR
            mTextureCoords[6] = right / size.x;
            mTextureCoords[7] = bottom / size.y;
        } else if (mAtlas != null) {
            // TL
            mTextureCoords[0] = left / mAtlas.mWidth;
            mTextureCoords[1] = top / mAtlas.mHeight;
            // BL
            mTextureCoords[2] = left / mAtlas.mWidth;
            mTextureCoords[3] = bottom / mAtlas.mHeight;
            // TR
            mTextureCoords[4] = right / mAtlas.mWidth;
            mTextureCoords[5] = top / mAtlas.mHeight;
            // BR
            mTextureCoords[6] = right / mAtlas.mWidth;
            mTextureCoords[7] = bottom / mAtlas.mHeight;
        }
    }

    protected void rotateCCW() {
        // start from TL
        final float x0 = mTextureCoords[0];
        final float y0 = mTextureCoords[1];

        // TL -> TR
        mTextureCoords[0] = mTextureCoords[4];
        mTextureCoords[1] = mTextureCoords[5];
        // TR -> BR
        mTextureCoords[4] = mTextureCoords[6];
        mTextureCoords[5] = mTextureCoords[7];
        // BR -> BL
        mTextureCoords[6] = mTextureCoords[2];
        mTextureCoords[7] = mTextureCoords[3];
        // BL -> TL
        mTextureCoords[2] = x0;
        mTextureCoords[3] = y0;

        // also flip the size
        final float temp = mSize.x;
        mSize.x = mSize.y;
        mSize.y = temp;
    }

    protected void rotateCW() {
        // start from TR
        final float x2 = mTextureCoords[4];
        final float y2 = mTextureCoords[5];

        // TR -> TL
        mTextureCoords[4] = mTextureCoords[0];
        mTextureCoords[5] = mTextureCoords[1];
        // TL -> BL
        mTextureCoords[0] = mTextureCoords[2];
        mTextureCoords[1] = mTextureCoords[3];
        // BL -> BR
        mTextureCoords[2] = mTextureCoords[6];
        mTextureCoords[3] = mTextureCoords[7];
        // BR -> TR
        mTextureCoords[6] = x2;
        mTextureCoords[7] = y2;

        // also flip the size
        final float temp = mSize.x;
        mSize.x = mSize.y;
        mSize.y = temp;
    }

    public void setRect(final RectF rect) {
        setRect(rect.left, rect.top, rect.right, rect.bottom);
    }

    public RectF getRect() {
        return mRect;
    }

    public PointF getSize() {
        return mSize;
    }

    // public PointF getOffset() {
    // return mOffset;
    // }
    //
    // public void setOffset(final float x, final float y) {
    // mOffset.x = x;
    // mOffset.y = y;
    // }
    //
    // public void setOffset(final PointF offset) {
    // mOffset.x = offset.x;
    // mOffset.y = offset.y;
    // }

    public float[] getTextureCoords() {
        return mTextureCoords;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    public void setTexture(final Texture texture) {
        mTexture = texture;

        if (mAtlas == null) {
            // refresh the coords
            setRect(mRect);
        }
    }

    public Texture getTexture() {
        return mTexture;
    }

    @Override
    @TargetApi(14)
    public String toString() {
        return String.format("AtlasFrame( %s, %s )", mName, mRect.toShortString());
    }
}
