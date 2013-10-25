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

    public static final String URI_DRAWABLE = "drawable://";
    public static final String URI_ASSET = "asset://";
    public static final String URI_FILE = "file://";
    public static final String URI_HTTP = "http://";

    protected HashMap<String, BitmapFont> mBitmapFonts = new HashMap<String, BitmapFont>();
    protected final HashMap<String, Texture> mGeneralTextures;

    protected UIConfig mUIConfig;

    /**
     * @param scene
     * @param res
     */
    public BaseUITextureManager(final Scene scene, final Resources res) {
        super(scene, res);

        mGeneralTextures = new HashMap<String, Texture>();
    }

    private void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUIConfig(final UIConfig config) {
        reset();

        mUIConfig = config;

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
    public Texture getTexture(final String textureUri) {
        String actualPath = null;

        if (textureUri.startsWith(URI_DRAWABLE)) {
            actualPath = textureUri.substring(URI_DRAWABLE.length());
            // int drawable = 0;
            // try {
            // Field field = R.drawable.class.getField(actualPath);
            // drawable = field.getInt(null);
            // } catch (Exception e) {
            // // TODO nothing
            // }
            // actualPath = String.valueOf(drawable);
        } else if (textureUri.startsWith(URI_ASSET)) {
            actualPath = textureUri.substring(URI_ASSET.length());
        } else if (textureUri.startsWith(URI_FILE)) {
            actualPath = textureUri.substring(URI_FILE.length());
        } else if (textureUri.startsWith(URI_HTTP)) {
            actualPath = textureUri; // keep
        } else {
            actualPath = textureUri;
        }

        if (mGeneralTextures.containsKey(actualPath)) {
            // use cache
            return mGeneralTextures.get(actualPath);
        } else {
            Texture texture = null;
            // create
            if (textureUri.startsWith(URI_FILE)) {
                // load from file / sdcard
                texture = createFileTexture(actualPath, mUIConfig.getTextureOptions());
            } else if (textureUri.startsWith(URI_ASSET)) {
                // load from bundle assets
                texture = createAssetTexture(actualPath, mUIConfig.getTextureOptions());
            } else if (textureUri.startsWith(URI_HTTP)) {
                // load from bundle assets
                texture = createURLTexture(actualPath, mUIConfig.getTextureOptions());
            }

            // and cache it if created
            if (texture != null) {
                mGeneralTextures.put(actualPath, texture);
            }

            return texture;
        }
    }

}
