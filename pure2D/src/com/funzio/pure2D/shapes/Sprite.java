/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.PointF;

import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Sprite extends Rectangular {
    private boolean mSizeToTexture = true;

    protected AtlasFrame mAtlasFrame;
    protected float mOffsetX = 0;
    protected float mOffsetY = 0;

    public Sprite() {
        super();
    }

    @Override
    public boolean update(final int deltaTime) {
        // texture loaded detection and auto size
        if (!mTextureLoaded && mSizeToTexture && mTexture != null && mTexture.isLoaded()) {
            setSize(mTexture.getSize());
        }

        return super.update(deltaTime);
    }

    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // fit size to texture
        if (texture != null && mSizeToTexture) {
            setSize(texture.getSize());
        }
    }

    public boolean isSizeToTexture() {
        return mSizeToTexture;
    }

    public void setSizeToTexture(final boolean value) {
        mSizeToTexture = value;

        // fit size to texture
        if (mTexture != null && mSizeToTexture) {
            setSize(mTexture.getSize());
        }
    }

    public void setAtlasFrame(final AtlasFrame frame) {
        if (frame != null) {

            // if there is a specific texture
            final Texture frameTexture = frame.getTexture();
            if (frameTexture != null) {
                mTexture = frameTexture;
            }

            setTextureCoordBuffer(frame.getTextureCoords());

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
            if (newSize.x != mSize.x || newSize.y != mSize.y) {
                setSize(newSize.x, newSize.y);
            } else {
                invalidate(FRAME);
            }
        } else {
            setTextureCoordBuffer(TextureCoordBuffer.getDefault());
            if (mOffsetX != 0 || mOffsetY != 0) {
                // cancel previous offset
                setOrigin(mOrigin.x + mOffsetX, mOrigin.y + mOffsetY);
                mOffsetX = mOffsetY = 0;
            }
            // fit size to texture
            if (mTexture != null && mSizeToTexture) {
                setSize(mTexture.getSize());
            } else {
                invalidate(FRAME);
            }
        }

        mAtlasFrame = frame;
    }

    public AtlasFrame getAtlasFrame() {
        return mAtlasFrame;
    }

}
