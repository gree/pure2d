/**
 * 
 */
package com.funzio.pure2D.shapes;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 * @category Use this class to draw a Texture quickly instead of Sprite
 */
public class DummyDrawer extends Sprite {

    @Override
    /**
     * This only draws the children without applying any transformation, color..., for drawing cache purpose
     */
    public boolean draw(final GLState glState) {
        // texture coordinates changed?
        if ((mInvalidateFlags & InvalidateFlags.TEXTURE_COORDS) != 0) {
            validateTextureCoordBuffer();
        }

        // texture
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            // apply the coordinates
            if (mTextureCoordBufferScaled != null) {
                mTextureCoordBufferScaled.apply(glState);
            }
        } else {
            // unbind the texture
            glState.unbindTexture();
        }

        // now draw, woo hoo!
        mVertexBuffer.draw(glState);

        return (mTexture != null);
    }
}
