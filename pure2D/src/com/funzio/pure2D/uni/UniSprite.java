/**
 * 
 */
package com.funzio.pure2D.uni;

import android.graphics.PointF;

import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class UniSprite extends UniRect {
    private boolean mSizeToTexture = true;
    private boolean mSizeToFrame = true;

    protected AtlasFrame mAtlasFrame;
    protected float mOffsetX = 0;
    protected float mOffsetY = 0;

    public UniSprite() {
        super();
    }

    public boolean isSizeToTexture() {
        return mSizeToTexture;
    }

    public void setSizeToTexture(final boolean sizeToTexture) {
        mSizeToTexture = sizeToTexture;

        // fit size to texture
        if (mSizeToTexture && mParent != null) {
            final Texture texture = mParent.getTexture();
            if (texture != null && texture.isLoaded()) {
                setSize(texture.getSize());
            }
        }
    }

    public boolean isSizeToFrame() {
        return mSizeToFrame;
    }

    public void setSizeToFrame(final boolean sizeToFrame) {
        mSizeToFrame = sizeToFrame;

        // fit size to texture
        if (mAtlasFrame != null && sizeToFrame) {
            setSize(mAtlasFrame.getSize());
        }
    }

    public void setAtlasFrame(final AtlasFrame frame) {
        if (frame != null) {
            // apply frame coords
            setTextureCoords(frame.getTextureCoords());

            // offset check
            if (frame.mOffset != null) {
                // add the offset
                setOrigin(mOrigin.x - (frame.mOffset.x - mOffsetX), mOrigin.y - (frame.mOffset.y - mOffsetY));
                mOffsetX = frame.mOffset.x;
                mOffsetY = frame.mOffset.y;
            } else if (mOffsetX != 0 || mOffsetY != 0) {
                // cancel previous offset
                setOrigin(mOrigin.x + mOffsetX, mOrigin.y + mOffsetY);
                mOffsetX = mOffsetY = 0;
            }

            // size changed?
            final PointF newSize = frame.getSize();
            if (mSizeToFrame && (newSize.x != mSize.x || newSize.y != mSize.y)) {
                setSize(newSize.x, newSize.y);
            } else {
                invalidate(FRAME);
            }
        } else {
            // default coord
            TextureCoordBuffer.getDefault(mTextureCoords);

            if (mOffsetX != 0 || mOffsetY != 0) {
                // cancel previous offset
                setOrigin(mOrigin.x + mOffsetX, mOrigin.y + mOffsetY);
                mOffsetX = mOffsetY = 0;
            }
            // fit size to texture
            invalidate(FRAME);
        }

        mAtlasFrame = frame;
    }

    public AtlasFrame getAtlasFrame() {
        return mAtlasFrame;
    }

    @Override
    public void onAdded(final UniContainer container) {
        super.onAdded(container);

        if (mSizeToTexture) {
            setSizeToTexture(mSizeToTexture);
        }
    }

}
