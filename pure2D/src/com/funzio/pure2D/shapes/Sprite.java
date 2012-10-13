/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.PointF;

import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Sprite extends Rectangular {
    private boolean mSizeToTexture = true;
    protected AtlasFrame mAtlasFrame;

    public Sprite() {
        super();
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
            setTextureCoordBuffer(frame.getTextureCoords());
            // size changed?
            final PointF newSize = frame.getSize();
            if (newSize.x != mSize.x || newSize.y != mSize.y) {
                setSize(newSize);
            }
        } else {
            setTextureCoordBuffer(TextureCoordBuffer.getDefault());
            // fit size to texture
            if (mTexture != null && mSizeToTexture) {
                setSize(mTexture.getSize());
            }
        }

        mAtlasFrame = frame;
    }

    public AtlasFrame getAtlasFrame() {
        return mAtlasFrame;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#draw(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    public boolean draw(final GLState glState) {

        if (mAtlasFrame != null && mAtlasFrame.mOffset != null) {
            final PointF offset = mAtlasFrame.mOffset;
            if (offset.x != 0 || offset.y != 0) {

                // shift
                glState.mGL.glTranslatef(offset.x, offset.y, 0);
                super.draw(glState);
                // unshift
                glState.mGL.glTranslatef(-offset.x, -offset.y, 0);

                return true;
            }
        }

        return super.draw(glState);
    }
}
