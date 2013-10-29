/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.util.Log;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.vo.FontVO;

/**
 * @author long.ngo
 */
public class UITextureManager extends TextureManager {

    protected HashMap<String, BitmapFont> mBitmapFonts = new HashMap<String, BitmapFont>();
    protected final HashMap<String, Texture> mGeneralTextures;

    protected UIManager mUIManager;

    /**
     * @param scene
     * @param res
     */
    public UITextureManager(final Scene scene, final Resources res) {
        super(scene, res);

        mGeneralTextures = new HashMap<String, Texture>();
    }

    public UIManager getUIManager() {
        return mUIManager;
    }

    public void setUIManager(final UIManager uIManager) {
        mUIManager = uIManager;
    }

    public void loadBitmapFonts() {
        if (mUIManager == null) {
            Log.e(TAG, "UIManager not found!", new Exception());
            return;
        }

        // make bitmap fonts
        final List<FontVO> fonts = mUIManager.getConfig().fonts;
        final int size = fonts.size();
        for (int i = 0; i < size; i++) {
            final TextOptions options = fonts.get(i).createTextOptions(mAssets);
            final BitmapFont font = new BitmapFont(options.inCharacters, options);
            font.load(mGLState);
            // map it
            mBitmapFonts.put(options.id, font);
        }
    }

    public BitmapFont getBitmapFont(final String fontId) {
        return mBitmapFonts.get(fontId);
    }

    public Texture getUriTexture(final String textureUri, final boolean async) {
        if (mUIManager == null) {
            Log.e(TAG, "UIManager not found!", new Exception());
            return null;
        }

        String actualPath = null;
        String shortPath = null;
        int drawable = 0;

        if (textureUri.startsWith(UIConfig.URI_DRAWABLE)) {
            actualPath = textureUri.substring(UIConfig.URI_DRAWABLE.length());
            drawable = mResources.getIdentifier(actualPath, UIConfig.TYPE_DRAWABLE, mUIManager.getContext().getApplicationContext().getPackageName());
            actualPath = String.valueOf(drawable);
        } else if (textureUri.startsWith(UIConfig.URI_ASSET)) {
            actualPath = textureUri.substring(UIConfig.URI_ASSET.length());
        } else if (textureUri.startsWith(UIConfig.URI_FILE)) {
            actualPath = textureUri.substring(UIConfig.URI_FILE.length());
        } else if (textureUri.startsWith(UIConfig.URI_HTTP)) {
            actualPath = textureUri; // keep
        } else if (textureUri.startsWith(UIConfig.URI_CACHE)) {
            shortPath = textureUri.substring(UIConfig.URI_CACHE.length());
            actualPath = mUIManager.getConfig().cache_dir + shortPath;
        } else {
            actualPath = textureUri;
        }

        if (mGeneralTextures.containsKey(actualPath)) {
            // use cache
            return mGeneralTextures.get(actualPath);
        } else {
            Texture texture = null;
            final TextureOptions textureOptions = mUIManager.getConfig().getTextureOptions();
            // create
            if (textureUri.startsWith(UIConfig.URI_DRAWABLE)) {
                // load from file / sdcard
                if (drawable > 0) {
                    texture = createDrawableTexture(drawable, textureOptions, async);
                }
            } else if (textureUri.startsWith(UIConfig.URI_FILE)) {
                // load from file / sdcard
                texture = createFileTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(UIConfig.URI_ASSET)) {
                // load from bundle assets
                texture = createAssetTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(UIConfig.URI_HTTP)) {
                // load from bundle assets
                texture = createURLTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(UIConfig.URI_CACHE)) {
                // load from url or cache file
                texture = createURLCacheTexture(mUIManager.getConfig().cdn_url, mUIManager.getConfig().cache_dir, shortPath, textureOptions, async);
            }

            // and cache it if created
            if (texture != null) {
                mGeneralTextures.put(actualPath, texture);
            }

            return texture;
        }
    }
}
