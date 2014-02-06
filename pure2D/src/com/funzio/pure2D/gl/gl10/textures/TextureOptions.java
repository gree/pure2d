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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.funzio.pure2D.Pure2D;

/**
 * @author long
 */
public class TextureOptions extends BitmapFactory.Options {
    public float inScaleX = 1;
    public float inScaleY = 1;
    public boolean inPo2 = !Pure2D.GL_NPOT_TEXTURE_SUPPORTED; // power of 2 dimensions
    public int inMipmaps = 0; // for GL texture mipmapping

    /**
     * Use {@link #getDefault()} to create a default instance
     */
    protected TextureOptions() {
        super();
    }

    /**
     * @return a new instance with default configuration
     */
    public static TextureOptions getDefault() {
        TextureOptions options = new TextureOptions();

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        options.inDither = false;
        options.inPurgeable = true; // for gc

        options.inScaleX = options.inScaleY = 1;
        options.inPo2 = !Pure2D.GL_NPOT_TEXTURE_SUPPORTED;
        options.inMipmaps = 0;

        return options;
    }

    public void set(final TextureOptions options) {
        inPreferredConfig = options.inPreferredConfig;
        inScaled = options.inScaled;
        inDither = options.inDither;
        inPurgeable = options.inPurgeable;

        inScaleX = options.inScaleX;
        inScaleY = options.inScaleY;
        inPo2 = options.inPo2;
        inMipmaps = options.inMipmaps;
    }
}
