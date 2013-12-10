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
