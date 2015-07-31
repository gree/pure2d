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
package com.funzio.pure2D.text;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class TextObject extends Rectangular {
    protected TextOptions mOptions;
    protected String mText = "";

    /**
     * @param text the text to set
     */
    public void setText(final String text, final TextOptions options, final boolean unloadOldTexture) {
        mText = text;
        mOptions = options;

        // unload the old texture
        if (unloadOldTexture && mTexture != null) {
            mTexture.unload();
            mTexture = null;
        }

        if (mText.length() > 0) {
            if (mScene != null && mScene.getTextureManager() != null) {
                setTexture(mScene.getTextureManager().createTextTexture(mText, mOptions));
                // match the size with the texture
                if (mTexture != null) {
                    setSize(mTexture.getSize());
                }
            }
        }
    }

    /**
     * @return the text
     */
    public String getText() {
        return mText;
    }

    /**
     * @return the options
     */
    public TextOptions getOptions() {
        return mOptions;
    }

    @Override
    public void onAddedToScene(final Scene scene) {
        super.onAddedToScene(scene);

        // if there is no texture yet
        if (mTexture == null && mText.length() > 0) {
            if (scene != null && scene.getTextureManager() != null) {
                setTexture(scene.getTextureManager().createTextTexture(mText, mOptions));
                // match the size with the texture
                if (mTexture != null) {
                    setSize(mTexture.getSize());
                }
            }
        }
    }

}
