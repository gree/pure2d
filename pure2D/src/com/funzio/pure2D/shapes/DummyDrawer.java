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

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 * @category Use this class to draw a Texture quickly instead of Sprite
 */
public class DummyDrawer extends Sprite {

    @Override
    /**
     * This only draws the children without applying any transformations, colors, for drawing cache purpose
     */
    public boolean draw(final GLState glState) {
        // texture coordinates changed?
        if ((mInvalidateFlags & InvalidateFlags.TEXTURE_COORDS) != 0) {
            validateTextureCoordBuffer();
        }

        // // blend mode
        // glState.setBlendFunc(mBlendFunc);
        // // color and alpha
        // glState.setColor(getBlendColor());
        //
        // // color buffer
        // if (mColorBuffer == null) {
        // glState.setColorArrayEnabled(false);
        // } else {
        // // apply color buffer
        // mColorBuffer.apply(glState);
        // }

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
