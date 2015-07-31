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
package com.funzio.pure2D.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.res.Resources;
import android.util.Log;

import com.funzio.pure2D.Pure2DURI;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.atlas.SingleFrameSet;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.particles.nova.NovaConfig;
import com.funzio.pure2D.particles.nova.NovaDelegator;
import com.funzio.pure2D.particles.nova.NovaEmitter;
import com.funzio.pure2D.particles.nova.NovaFactory;
import com.funzio.pure2D.particles.nova.NovaLoader;
import com.funzio.pure2D.particles.nova.NovaParticle;
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
    protected final HashMap<String, NovaFactory> mNovaFactories;

    protected UIManager mUIManager;
    protected UIConfigVO mUIConfigVO;

    private NovaDelegator mNovaDelegator;

    /**
     * @param scene
     * @param res
     */
    public UITextureManager(final Scene scene, final Resources res) {
        super(scene, res);

        mGeneralTextures = new HashMap<String, Texture>();
        mAtlasFrames = new HashMap<String, AtlasFrameSet>();
        mNovaFactories = new HashMap<String, NovaFactory>();
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

    public void preloadAssets() {
        loadBitmapFonts();
    }

    protected void loadBitmapFonts() {
        if (mUIManager == null) {
            Log.e(TAG, "UIManager not found!", new Exception());
            return;
        }

        // make bitmap fonts
        final List<FontVO> fonts = mUIConfigVO.fonts;
        final int size = fonts.size();
        for (int i = 0; i < size; i++) {
            final FontVO fontVO = fonts.get(i);
            final TextOptions options = fontVO.createTextOptions(mUIManager);
            final BitmapFont font = new BitmapFont(options.inCharacters, options, fontVO.texture_size);
            font.load(this);
            // map it
            mBitmapFonts.put(options.id, font);
        }
    }

    public BitmapFont getBitmapFont(final String fontId) {
        return mBitmapFonts.get(fontId);
    }

    /**
     * Create and cache a texture synchronously from a specified URI. If this texture is already in cache, it simply returns the cache version.
     * 
     * @param textureUri
     * @return
     */
    public Texture getUriTexture(final String textureUri) {
        return getUriTexture(textureUri, null, false);
    }

    /**
     * Create and cache a texture synchronously from a specified URI. If this texture is already in cache, it simply returns the cache version.
     * 
     * @param textureUri
     * @param options can be null. If this is null, it used the Texture Options defined in UI Config file.
     * @return
     */
    public Texture getUriTexture(final String textureUri, final TextureOptions options) {
        return getUriTexture(textureUri, options, false);
    }

    /**
     * Create and cache a texture from a specified URI. If this texture is already in cache, it simply returns the cache version.
     * 
     * @param textureUri
     * @param async
     * @param options can be null. If this is null, it used the Texture Options defined in UI Config file.
     * @return
     * @see Pure2DURI
     */
    public Texture getUriTexture(String textureUri, final TextureOptions options, final boolean async) {
        if (Texture.LOG_ENABLED) {
            Log.v(TAG, "getUriTexture(): " + textureUri);
        }

        if (mUIManager == null) {
            Log.e(TAG, "UIManager not found!", new Exception());
            return null;
        }

        // XXX HACK for bingo backward compatibility
        if (UIConfig.isUnknownUri(textureUri)) {
            textureUri = Pure2DURI.ASSET + textureUri; // make it asset://
        }
        final String actualPath = Pure2DURI.getPathFromUri(textureUri);

        if (mGeneralTextures.containsKey(actualPath)) {
            // use cache
            return mGeneralTextures.get(actualPath);
        } else {
            Texture texture = null;
            final TextureOptions textureOptions = (options == null) ? mUIManager.getTextureOptions() : options;
            // create
            if (textureUri.startsWith(Pure2DURI.DRAWABLE)) {
                // load from file / sdcard
                final int drawable = mResources.getIdentifier(actualPath, UIConfig.TYPE_DRAWABLE, mUIManager.getPackageName());
                if (drawable > 0) {
                    texture = createDrawableTexture(drawable, textureOptions, async);
                }
            } else if (textureUri.startsWith(Pure2DURI.FILE)) {
                // load from file / sdcard
                texture = createFileTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(Pure2DURI.ASSET)) {
                // load from bundle assets
                texture = createAssetTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(Pure2DURI.HTTP)) {
                // load from bundle assets
                texture = createURLTexture(actualPath, textureOptions, async);
            } else if (textureUri.startsWith(Pure2DURI.CACHE)) {
                // load from url or cache file
                texture = createURLCacheTexture(mUIConfigVO.texture_manager.cdn_url + actualPath, mUIConfigVO.texture_manager.cache_dir + actualPath, textureOptions, async);
            }

            // and cache it if created
            if (texture != null) {
                // texture expiration
                texture.setExpirationTime(mUIConfigVO.texture_manager.texture_expiration_time);
                // set filter
                final int filter = mUIConfigVO.texture_manager.texture_options.filter;
                if (filter > 0) {
                    texture.setFilters(filter, filter);
                }
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
    public AtlasFrameSet getUriAtlas(String jsonUri, final boolean async) {
        if (Texture.LOG_ENABLED) {
            Log.v(TAG, "getUriAtlas(): " + jsonUri);
        }

        // XXX HACK for bingo backward compatibility
        if (UIConfig.isUnknownUri(jsonUri)) {
            jsonUri = Pure2DURI.ASSET + jsonUri; // make it asset://
        }
        final String actualPath = Pure2DURI.getPathFromUri(jsonUri);

        if (mAtlasFrames.containsKey(actualPath)) {
            // reuse cache
            return mAtlasFrames.get(actualPath);
        } else if (actualPath.endsWith(UIConfig.FILE_JSON)) {
            try {
                // create new
                final JsonAtlas atlas = new JsonAtlas(mScene.getAxisSystem());

                // load from sdcard / assets
                if (jsonUri.startsWith(Pure2DURI.ASSET)) {
                    if (async) {
                        atlas.loadAsync(mAssets, actualPath, mUIConfigVO.screen_scale);
                    } else {
                        atlas.load(mAssets, actualPath, mUIConfigVO.screen_scale);
                    }
                } else if (jsonUri.startsWith(Pure2DURI.FILE)) {
                    if (async) {
                        atlas.loadAsync(null, actualPath, mUIConfigVO.screen_scale);
                    } else {
                        atlas.load(actualPath, mUIConfigVO.screen_scale);
                    }
                } else if (jsonUri.startsWith(Pure2DURI.HTTP)) {
                    if (async) {
                        atlas.loadURLAsync(actualPath, null, mUIConfigVO.screen_scale);
                    } else {
                        atlas.loadURL(actualPath, null, mUIConfigVO.screen_scale);
                    }
                } else if (jsonUri.startsWith(Pure2DURI.CACHE)) {
                    if (async) {
                        atlas.loadURLAsync(mUIConfigVO.texture_manager.cdn_url + actualPath, mUIConfigVO.texture_manager.cache_dir + actualPath, mUIConfigVO.screen_scale);
                    } else {
                        atlas.loadURL(mUIConfigVO.texture_manager.cdn_url + actualPath, mUIConfigVO.texture_manager.cache_dir + actualPath, mUIConfigVO.screen_scale);
                    }
                }

                // now load texture
                final AtlasFrameSet multiFrames = atlas.getMasterFrameSet();
                multiFrames.setTexture(getUriTexture(jsonUri.replace(UIConfig.FILE_JSON, UIConfig.FILE_PNG), null, async));

                // cache it
                mAtlasFrames.put(actualPath, multiFrames);
                return multiFrames;

            } catch (Exception e) {
                Log.e(TAG, "Atlas Loading Error! " + actualPath, e);
                return null;
            }
        } else {
            final SingleFrameSet singleFrame = new SingleFrameSet(actualPath, getUriTexture(jsonUri.replace(UIConfig.FILE_JSON, UIConfig.FILE_PNG), null, async));
            // cache it
            mAtlasFrames.put(actualPath, singleFrame);
            return singleFrame;
        }
    }

    public NovaFactory getUriNova(final String jsonUri, final boolean async) {
        if (Texture.LOG_ENABLED) {
            Log.v(TAG, "getUriNova(): " + jsonUri);
        }

        final String actualPath = Pure2DURI.getPathFromUri(jsonUri);

        if (mNovaFactories.containsKey(actualPath)) {
            // reuse cache
            return mNovaFactories.get(actualPath);
        } else {
            final NovaLoader novaLoader = new NovaLoader();
            final NovaFactory novaFactory = new NovaFactory(novaLoader, getNovaDelegator(), mUIConfigVO.screen_scale);
            // load from sdcard / assets
            if (jsonUri.startsWith(Pure2DURI.ASSET)) {
                if (async) {
                    novaLoader.loadAsync(mAssets, actualPath);
                } else {
                    novaLoader.load(mAssets, actualPath);
                }
            } else if (jsonUri.startsWith(Pure2DURI.FILE)) {
                if (async) {
                    novaLoader.loadAsync(null, actualPath);
                } else {
                    novaLoader.load(null, actualPath);
                }
            } else if (jsonUri.startsWith(Pure2DURI.HTTP)) {
                if (async) {
                    novaLoader.loadURLAsync(actualPath, null);
                } else {
                    novaLoader.loadURL(actualPath, null);
                }
            } else if (jsonUri.startsWith(Pure2DURI.CACHE)) {
                if (async) {
                    novaLoader.loadURLAsync(mUIConfigVO.texture_manager.cdn_url + actualPath, mUIConfigVO.texture_manager.cache_dir + actualPath);
                } else {
                    novaLoader.loadURL(mUIConfigVO.texture_manager.cdn_url + actualPath, mUIConfigVO.texture_manager.cache_dir + actualPath);
                }
            }

            // cache it
            mNovaFactories.put(actualPath, novaFactory);
            return novaFactory;
        }
    }

    public NovaDelegator getNovaDelegator() {
        if (mNovaDelegator != null) {
            return mNovaDelegator;
        }

        // this is how you assign texture to the sprite to a particle
        mNovaDelegator = new NovaDelegator() {
            @Override
            public void delegateEmitter(final NovaEmitter emitter, final Object... params) {

            }

            @Override
            public void delegateParticle(final NovaParticle particle, final Object... params) {
                final String sprite = NovaConfig.getString(particle.getParticleVO().sprite, -1);

                if (sprite == null) {
                    return;
                }

                final String formattedSprite = sprite.replace(NovaConfig.$SD, Pure2DURI.CACHE); // bingo compatibility
                // get the loaded frames
                AtlasFrameSet frames = null;

                // apply the texture
                if (formattedSprite.startsWith(NovaConfig.$TEXT)) {
                    // FIXME set floatie text texture
                    // frames.setTexture(getFloatieTextTexture((String) NovaConfig.getParamValue($TEXT, formattedSprite, params)));
                } else if (formattedSprite.startsWith(NovaConfig.$SPRITE)) {
                    final String decodedSprite = (String) NovaConfig.getParamValue(NovaConfig.$SPRITE, formattedSprite, params);
                    // load the frames
                    frames = getUriAtlas(decodedSprite, false); // FIXME make async

                    if (frames instanceof SingleFrameSet) {
                        if (!((SingleFrameSet) frames).isTextureLoaded()) {
                            frames.setTexture(frames.getTexture()); // refresh
                        }
                    }
                } else {
                    frames = getUriAtlas(formattedSprite, false); // FIXME make async

                    if (frames instanceof SingleFrameSet) {
                        if (!((SingleFrameSet) frames).isTextureLoaded()) {
                            frames.setTexture(frames.getTexture()); // refresh
                        }
                    }
                }

                // apply the frames
                particle.setAtlasFrameSet(frames);
            }
        };

        return mNovaDelegator;
    }

    /**
     * Clear and reset everything for memory saving
     */
    public void reset() {
        Log.w(TAG, "reset()");

        synchronized (mAtlasFrames) {
            // also release the textures
            final Set<String> keys = mAtlasFrames.keySet();
            for (String key : keys) {
                mAtlasFrames.get(key).setTexture(null);
            }

            mAtlasFrames.clear();
        }

        synchronized (mNovaFactories) {
            // only clear the pools but keep everything else
            final Set<String> keys = mNovaFactories.keySet();
            for (String key : keys) {
                mNovaFactories.get(key).clearPools();
            }

            mNovaFactories.clear();
        }
    }

}
