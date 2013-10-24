/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.xml.UIConfig;

/**
 * @author long.ngo
 */
public class BaseUITextureManager extends TextureManager implements UITextureManager {

    protected HashMap<String, BitmapFont> mBitmapFonts = new HashMap<String, BitmapFont>();

    /**
     * @param scene
     * @param res
     */
    public BaseUITextureManager(final Scene scene, final Resources res) {
        super(scene, res);
        // TODO Auto-generated constructor stub
    }

    private void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUIConfig(final UIConfig config) {
        reset();

        // make bitmap fonts
        final List<TextOptions> fonts = config.getFonts();
        final int size = fonts.size();
        for (int i = 0; i < size; i++) {
            final TextOptions options = fonts.get(i);
            final BitmapFont font = new BitmapFont(options.inCharacters, options);
            font.load(mGLState);
            // map it
            mBitmapFonts.put(options.id, font);
        }
    }

    @Override
    public BitmapFont getBitmapFont(final String fontId) {
        return mBitmapFonts.get(fontId);
    }

    @Override
    public Texture getUITexture(final String textureUri) {
        // TODO Auto-generated method stub
        return null;
    }

}
