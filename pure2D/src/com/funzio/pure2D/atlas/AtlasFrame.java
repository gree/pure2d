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
    private Atlas mAtlas;
    private RectF mRect = new RectF();
    private float[] mTextureCoords = new float[8];
    private PointF mSize = new PointF();

    public PointF mOffset = null;
    private Texture mTexture;

    private final int mIndex;
    private final String mName;

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
        setRect(0, 0, (int) mTexture.getSize().x - 1, (int) mTexture.getSize().y - 1);
        mName = name;
    }

    public void setRect(final float left, final float top, final float right, final float bottom) {
        mRect.left = left;
        mRect.right = right;
        mRect.top = top;
        mRect.bottom = bottom;

        // pre-cal
        mSize.x = mRect.width();
        mSize.y = mRect.height();

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
        } else {
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
        float x0 = mTextureCoords[0];
        float y0 = mTextureCoords[1];
        float x1 = mTextureCoords[2];
        float y1 = mTextureCoords[3];

        // TR
        mTextureCoords[0] = mTextureCoords[4];
        mTextureCoords[1] = mTextureCoords[5];
        // TL
        mTextureCoords[2] = x0;
        mTextureCoords[3] = y0;
        // BR
        mTextureCoords[4] = mTextureCoords[6];
        mTextureCoords[5] = mTextureCoords[7];
        // BL
        mTextureCoords[6] = x1;
        mTextureCoords[7] = y1;

        // also flip the size
        float temp = mSize.x;
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

    public Texture getTexture() {
        return mTexture;
    }

    @Override
    @TargetApi(14)
    public String toString() {
        return String.format("AtlasFrame( %s, %s )", mName, mRect.toShortString());
    }
}
