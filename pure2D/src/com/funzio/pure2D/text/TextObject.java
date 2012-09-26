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

    public TextObject(final String text) {
        mText = text;
    }

    /**
     * @return the text
     */
    public String getText() {
        return mText;
    }

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

        Scene scene = getScene();
        if (scene != null) {
            setTexture(scene.getTextureManager().createTexture(mText, mOptions));
        }
    }

    /**
     * @return the options
     */
    public TextOptions getOptions() {
        return mOptions;
    }
}
