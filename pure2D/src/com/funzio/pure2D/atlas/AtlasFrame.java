/**
 * 
 */
package com.funzio.pure2D.atlas;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * @author long
 */
public class AtlasFrame {
    private Atlas mAtlas;
    private Rect mRect = new Rect();
    private float[] mTextureCoords = new float[8];
    private PointF mSize = new PointF();
    public PointF mOffset = null;

    private final int mIndex;
    private final String mName;

    public AtlasFrame(final Atlas atlas, final int index, final String name, final int left, final int top, final int right, final int bottom) {
        mIndex = index;
        mAtlas = atlas;
        setRect(left, top, right, bottom);
        mName = name;
    }

    public AtlasFrame(final Atlas atlas, final int index, final String name, final Rect rect) {
        this(atlas, index, name, rect.left, rect.top, rect.right, rect.bottom);
    }

    public void setRect(final int left, final int top, final int right, final int bottom) {
        mRect.left = left;
        mRect.right = right;
        mRect.top = top;
        mRect.bottom = bottom;

        // pre-cal
        mSize.x = right - left + 1;
        mSize.y = bottom - top + 1;

        // TL
        mTextureCoords[0] = (float) left / mAtlas.mWidth;
        mTextureCoords[1] = (float) top / mAtlas.mHeight;
        // BL
        mTextureCoords[2] = (float) left / mAtlas.mWidth;
        mTextureCoords[3] = (float) bottom / mAtlas.mHeight;
        // TR
        mTextureCoords[4] = (float) right / mAtlas.mWidth;
        mTextureCoords[5] = (float) top / mAtlas.mHeight;
        // BR
        mTextureCoords[6] = (float) right / mAtlas.mWidth;
        mTextureCoords[7] = (float) bottom / mAtlas.mHeight;
    }

    public void setRect(final Rect rect) {
        setRect(rect.left, rect.top, rect.right, rect.bottom);
    }

    public Rect getRect() {
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

    @Override
    public String toString() {
        return String.format("AtlasFrame( %s, %s )", mName, mRect.toShortString());
    }
}
