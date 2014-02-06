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

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class TextTexture extends Texture {

    private String mText = "";
    private TextOptions mOptions;

    protected TextTexture(final GLState glState, final String text, final TextOptions options) {
        super(glState);

        load(text, options);
    }

    protected void load(final String text, final TextOptions options) {
        mText = text;
        mOptions = options;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getTextBitmap(mText, mOptions, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.textures.Texture#reload()
     */
    @Override
    public void reload() {
        load(mText, mOptions);
    }

}
