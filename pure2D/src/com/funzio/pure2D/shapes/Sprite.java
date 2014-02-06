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
    private boolean mSizeToFrame = true;

    protected AtlasFrame mAtlasFrame;
    protected float mOffsetX = 0;
    protected float mOffsetY = 0;

    public Sprite() {
        super();
    }

    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // fit size to texture
        if (mSizeToTexture && texture != null) { // do not do: && texture.isLoaded(), because texture can be loaded and later expired
            setSize(texture.getSize());
        }
    }

    @Override
    protected void onTextureLoaded(final Texture texture) {
        super.onTextureLoaded(texture);

        // match size
        if (mSizeToTexture) {
            setSize(texture.getSize());
        }
    }

    public boolean isSizeToTexture() {
        return mSizeToTexture;
    }

    public void setSizeToTexture(final boolean sizeToTexture) {
        mSizeToTexture = sizeToTexture;

        // fit size to texture
        if (mSizeToTexture && mTexture != null && mTexture.isLoaded()) {
            setSize(mTexture.getSize());
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
            if (mSizeToFrame && (newSize.x != mSize.x || newSize.y != mSize.y)) {
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
