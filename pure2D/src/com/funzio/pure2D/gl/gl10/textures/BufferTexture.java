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
package com.funzio.pure2D.gl.gl10.textures;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class BufferTexture extends Texture {

    /**
     * @param gl
     */
    protected BufferTexture(final GLState glState, final int width, final int height) {
        super(glState);

        // create an blank texture
        load(null, width, height, 0);
    }

    /**
     * @param gl
     */
    protected BufferTexture(final GLState glState, final int actualWidth, final int actualHeight, final boolean checkPo2) {
        super(glState);

        final int bitmapWidth;
        final int bitmapHeight;
        // check for power of 2
        if (checkPo2 && !Pure2D.GL_NPOT_TEXTURE_SUPPORTED) {
            bitmapWidth = Pure2DUtils.getNextPO2(actualWidth);
            bitmapHeight = Pure2DUtils.getNextPO2(actualHeight);
        } else {
            bitmapWidth = actualWidth;
            bitmapHeight = actualHeight;
        }

        // BitmapSize >= ActualSize!
        load(bitmapWidth, bitmapHeight, actualWidth, actualHeight, 0);
    }

    public void load(final int bitmapWidth, final int bitmapHeight, final int actualWidth, final int actualHeight, final int mipmaps) {
        // create an blank texture
        load(null, bitmapWidth, bitmapHeight, mipmaps);

        // update the bitmap size to re-calculate the scale. BitmapSize >= ActualSize!
        setBitmapSize(bitmapWidth, bitmapHeight, actualWidth, actualHeight);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.Texture#reload()
     */
    @Override
    public void reload() {
        // create an blank texture
        load(null, (int) mSize.x, (int) mSize.y, 0);
    }

}
