/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.res.Resources;
import android.util.Log;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.vo.FontVO;
import com.funzio.pure2D.ui.vo.UIConfigVO;

/**
 * @author long.ngo
 */
public class UITextureManager extends TextureManager {
    protected static final String TAG = UITextureManager.class.getSimpleName();

    protected HashMap<String, BitmapFont> mBitmapFonts = new HashMap<String, BitmapFont>();
    protected final HashMap<String, Texture> mGeneralTextures;
    protected final HashMap<String, AtlasFrameSet> mAtlasFrames;

    protected UIManager mUIManager;
    protected UIConfigVO mUIConfigVO;

    /**
     * @param scene
     * @param res
     */
    public UITextureManager(final Scene scene, final Resources res) {
        super(scene, res);

        mGeneralTextures = new HashMap<String, Texture>();
        mAtlasFrames = new HashMap<String, AtlasFrameSet>();
    }

    public UIManager getUIManager() {
        return mUIManager;
    }

    public void setUIManager(final UIManager manager) {
        mUIManager = manager;

        if (manager != null) {
            mUIConfigVO = manager.getConfig();
            // texture expiration
            setExpirationCheckInterval(manager.getConfig().texture_manager.expiration_check_interval);
        }
    }

    public void loadBitmapFonts() {
        if (mUIManager == null) {
            Log.e(TAG, "UIManager not found!", new Exception());
            return;
        }

        // make bitmap fonts
        final List<FontVO> fonts = mUIConfigVO.fonts;
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
        Log.v(TAG, "getUriTexture(): " + textureUri);

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
            actualPath = mUIConfigVO.texture_manager.cache_dir + shortPath;
        } else {
            actualPath = textureUri;
        }

        if (mGeneralTextures.containsKey(actualPath)) {
            // use cache
            return mGeneralTextures.get(actualPath);
        } else {
            Texture texture = null;
            final TextureOptions textureOptions = mUIConfigVO.getTextureOptions();
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
                texture = createURLCacheTexture(mUIConfigVO.texture_manager.cdn_url, mUIConfigVO.texture_manager.cache_dir, shortPath, textureOptions, async);
            }

            // and cache it if created
            if (texture != null) {
                // texture expiration
                texture.setExpirationTime(mUIConfigVO.texture_manager.texture_expiration_time);
                mGeneralTextures.put(actualPath, texture);
            }

            return texture;
        }
    }

    /**
     * Load a Json atlas file
     * 
     * @param assets
     * @param jsonUri
     * @return
     */
    public AtlasFrameSet getUriAtlas(final String jsonUri, final boolean async) {
        Log.v(TAG, "getUriAtlas(): " + jsonUri);

        String actualPath = null;
        if (jsonUri.startsWith(UIConfig.URI_ASSET)) {
            actualPath = jsonUri.substring(UIConfig.URI_ASSET.length());
        } else if (jsonUri.startsWith(UIConfig.URI_FILE)) {
            actualPath = jsonUri.substring(UIConfig.URI_FILE.length());
        } else {
            actualPath = jsonUri;
        }

        if (mAtlasFrames.containsKey(actualPath)) {
            // reuse it
            return mAtlasFrames.get(actualPath);
        } else if (actualPath.endsWith(UIConfig.FILE_JSON)) {
            try {
                // create new
                final JsonAtlas atlas = new JsonAtlas(mScene.getAxisSystem());
                // load from sdcard / assets
                if (jsonUri.startsWith(UIConfig.URI_ASSET)) {
                    atlas.load(mAssets, actualPath, mUIConfigVO.scale);
                } else if (jsonUri.startsWith(UIConfig.URI_FILE)) {
                    atlas.load(null, actualPath, mUIConfigVO.scale);
                }

                final AtlasFrameSet multiFrames = atlas.getMasterFrameSet();
                multiFrames.setTexture(getUriTexture(jsonUri.replace(UIConfig.FILE_JSON, UIConfig.FILE_PNG), async));

                // cache it
                mAtlasFrames.put(actualPath, multiFrames);
                return multiFrames;
            } catch (Exception e) {
                Log.e(TAG, "Atlas Loading Error! " + actualPath, e);
                return null;
            }
        }

        return null;
    }

    /**
     * Clear and reset everything for memory saving
     */
    public void reset() {
        Log.w(TAG, "reset()");

        synchronized (mAtlasFrames) {
            // also release the textures
            Set<String> keys = mAtlasFrames.keySet();
            for (String key : keys) {
                mAtlasFrames.get(key).setTexture(null);
            }

            mAtlasFrames.clear();
        }
    }

}
