/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;
import java.util.List;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.TextOptions;

/**
 * @author long.ngo
 */
public class UITextureManager extends TextureManager {

    public static final String URI_DRAWABLE = "@drawable/";
    public static final String URI_ASSET = "asset://";
    public static final String URI_FILE = "file://";
    public static final String URI_HTTP = "http://";

    protected HashMap<String, BitmapFont> mBitmapFonts = new HashMap<String, BitmapFont>();
    protected final HashMap<String, Texture> mGeneralTextures;

    protected UIManager mUIManager;

    /**
     * @param scene
     * @param res
     */
    public UITextureManager(final Scene scene, final UIManager manager) {
        super(scene, manager.getContext().getResources());

        mUIManager = manager;
        mGeneralTextures = new HashMap<String, Texture>();
    }

    private void reset() {
        // TODO Auto-generated method stub

    }

    public void loadBitmapFonts() {
        reset();

        // make bitmap fonts
        final List<TextOptions> fonts = mUIManager.getConfig().getFonts();
        final int size = fonts.size();
        for (int i = 0; i < size; i++) {
            final TextOptions options = fonts.get(i);
            final BitmapFont font = new BitmapFont(options.inCharacters, options);
            font.load(mGLState);
            // map it
            mBitmapFonts.put(options.id, font);
        }
    }

    public BitmapFont getBitmapFont(final String fontId) {
        return mBitmapFonts.get(fontId);
    }

    public Texture getUITexture(final String textureUri) {
        String actualPath = null;

        if (textureUri.startsWith(URI_DRAWABLE)) {
            actualPath = textureUri.substring(URI_DRAWABLE.length());
            int drawable = mResources.getIdentifier(actualPath, "drawable", mUIManager.getContext().getApplicationContext().getPackageName());
            actualPath = String.valueOf(drawable);
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
            final TextureOptions textureOptions = mUIManager.getConfig().getTextureOptions();
            final boolean async = mUIManager.getConfig().mTextureAsync;
            // create
            if (textureUri.startsWith(URI_DRAWABLE)) {
                // load from file / sdcard
                texture = async ? createDrawableTextureAsync(Integer.valueOf(actualPath), textureOptions) : createDrawableTexture(Integer.valueOf(actualPath), textureOptions);
            } else if (textureUri.startsWith(URI_FILE)) {
                // load from file / sdcard
                texture = async ? createFileTextureAsync(actualPath, textureOptions) : createFileTexture(actualPath, textureOptions);
            } else if (textureUri.startsWith(URI_ASSET)) {
                // load from bundle assets
                texture = async ? createAssetTextureAsync(actualPath, textureOptions) : createAssetTexture(actualPath, textureOptions);
            } else if (textureUri.startsWith(URI_HTTP)) {
                // load from bundle assets
                texture = async ? createURLTextureAsync(actualPath, textureOptions) : createURLTexture(actualPath, textureOptions);
            }

            // and cache it if created
            if (texture != null) {
                mGeneralTextures.put(actualPath, texture);
            }

            return texture;
        }
    }
}
