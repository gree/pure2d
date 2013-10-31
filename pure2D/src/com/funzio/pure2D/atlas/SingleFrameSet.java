/**
 * 
 */
package com.funzio.pure2D.atlas;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class SingleFrameSet extends AtlasFrameSet {

    private AtlasFrame mFrame;
    private boolean mTextureLoaded;

    public SingleFrameSet(final String name, final Texture texture) {
        super(name);

        // only has 1 single frame
        addFrame(mFrame = new AtlasFrame(null, 0, name));

        // apply texture
        setTexture(texture);
    }

    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // re-set the rect
        if (texture != null && texture.isLoaded()) {
            mTextureLoaded = true;

            mFrame.setRect(0, 0, (int) texture.getSize().x - 1, (int) texture.getSize().y - 1);

            // find the max frame size
            if (mFrame.mSize.x > mFrameMaxSize.x) {
                mFrameMaxSize.x = mFrame.mSize.x;
            }
            if (mFrame.mSize.y > mFrameMaxSize.y) {
                mFrameMaxSize.y = mFrame.mSize.y;
            }
        } else {
            mTextureLoaded = false;
        }
    }

    public boolean isTextureLoaded() {
        return mTextureLoaded;
    }

    @Override
    public String toString() {
        return String.format("SingleFrameSet( %s,\n%s )", mName, mFrame.toString());
    }
}
