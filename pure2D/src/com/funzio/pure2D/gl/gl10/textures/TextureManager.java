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

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.text.TextOptions;

/**
 * @author long
 */
public class TextureManager {

    public static final String TAG = TextureManager.class.getSimpleName();
    public static final int DEFAULT_EXPIRATION_CHECK_INTERVAL = 60 * 1000; // ms

    protected Scene mScene;
    protected ArrayList<Texture> mTextures = new ArrayList<Texture>();
    protected GLState mGLState;
    protected GL10 mGL;

    protected Resources mResources;
    protected AssetManager mAssets;

    // texture expiration
    protected int mExpirationCheckInterval = 0; // <= 0 means disabled
    protected int mExpirationCheckElapsedTime = 0;

    public TextureManager(final Scene scene, final Resources res) {
        mScene = scene;
        if (mScene != null) {
            mGLState = mScene.getGLState();
            mGL = mGLState.mGL;
        }

        mResources = res;
        mAssets = mResources.getAssets();
    }

    public Scene getScene() {
        return mScene;
    }

    public GLState getGLState() {
        return mGLState;
    }

    /**
     * Get the Expiration check interval (in ms)
     * 
     * @return
     */
    public int getExpirationCheckInterval() {
        return mExpirationCheckInterval;
    }

    /**
     * Set how often (in ms) this Manager should check for all the Textures' expiration. By default, it doesn't check at all.
     * 
     * @param expirationCheckInterval
     * @see Texture.#setExpirationTime(int)
     */
    public void setExpirationCheckInterval(final int expirationCheckInterval) {
        mExpirationCheckInterval = expirationCheckInterval;
    }

    /**
     * Call this when GL changed
     * 
     * @param gl
     * @param res
     */
    public void reload(final GLState glState, final Resources res) {
        mGLState = glState;
        mGL = mGLState.mGL;
        mResources = res;
        mAssets = mResources.getAssets();

        // reset
        reloadAllTextures(false);
    }

    public Resources getResources() {
        return mResources;
    }

    public AssetManager getAssets() {
        return mAssets;
    }

    /**
     * Create a new Texture from a Drawable
     * 
     * @param drawable
     * @param config
     * @return
     */
    public DrawableTexture createDrawableTexture(final int drawable, final TextureOptions options) {
        Log.v(TAG, String.format("createDrawableTexture(%d, %s)", drawable, options));

        final DrawableTexture texture = new DrawableTexture(mGLState, getResources(), drawable, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a Drawable asynchronously
     * 
     * @param drawable
     * @param config
     * @return
     */
    public DrawableTexture createDrawableTexture(final int drawable, final TextureOptions options, final boolean async) {
        Log.v(TAG, String.format("createDrawableTexture(%d, %s, %b)", drawable, options, async));

        final DrawableTexture texture = new DrawableTexture(mGLState, getResources(), drawable, options, async);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from an AssetManager
     * 
     * @param assetManager
     * @param filePath
     * @param config
     * @return
     */
    public AssetTexture createAssetTexture(final String filePath, final TextureOptions options) {
        Log.v(TAG, String.format("createAssetTexture(%s, %s)", filePath, options));

        final AssetTexture texture = new AssetTexture(mGLState, mAssets, filePath, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from an AssetManager asynchronously
     * 
     * @param assetManager
     * @param filePath
     * @param config
     * @return
     */
    public AssetTexture createAssetTexture(final String filePath, final TextureOptions options, final boolean async) {
        Log.v(TAG, String.format("createAssetTexture(%s, %s, %b)", filePath, options, async));

        final AssetTexture texture = new AssetTexture(mGLState, mAssets, filePath, options, async);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a file
     * 
     * @param filePath
     * @param config
     * @return
     */
    public FileTexture createFileTexture(final String filePath, final TextureOptions options) {
        Log.v(TAG, String.format("createFileTexture( %s, %s)", filePath, options));

        final FileTexture texture = new FileTexture(mGLState, filePath, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a file asynchronously
     * 
     * @param filePath
     * @param config
     * @return
     */
    public FileTexture createFileTexture(final String filePath, final TextureOptions options, final boolean async) {
        Log.v(TAG, String.format("createFileTexture( %s, %s, %b)", filePath, options, async));

        final FileTexture texture = new FileTexture(mGLState, filePath, options, async);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a URL
     * 
     * @param url
     * @param options
     * @return
     */
    public URLTexture createURLTexture(final String url, final TextureOptions options) {
        Log.v(TAG, String.format("createURLTexture(%s, %s)", url, options));

        final URLTexture texture = new URLTexture(mGLState, url, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a URL asynchronously
     * 
     * @param url
     * @param options
     * @return
     */
    public URLTexture createURLTexture(final String url, final TextureOptions options, final boolean async) {
        Log.v(TAG, String.format("createURLTexture(%s, %s, %b)", url, options, async));

        final URLTexture texture = new URLTexture(mGLState, url, options, async);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a URL and Cache synchronously
     * 
     * @param fileUrl
     * @param cachePath
     * @param options
     * @return
     */
    public URLCacheTexture createURLCacheTexture(final String fileUrl, final String cachePath, final TextureOptions options) {
        Log.v(TAG, String.format("createURLCacheTexture(%s, %s, %s)", fileUrl, cachePath, options));

        final URLCacheTexture texture = new URLCacheTexture(mGLState, fileUrl, cachePath, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Texture from a URL and Cache asynchronously
     * 
     * @param fileUrl
     * @param cachePath
     * @param options
     * @param async
     * @return
     */
    public URLCacheTexture createURLCacheTexture(final String fileUrl, final String cachePath, final TextureOptions options, final boolean async) {
        Log.v(TAG, String.format("createURLCacheTexture(%s, %s, %s, %b)", fileUrl, cachePath, options, async));

        final URLCacheTexture texture = new URLCacheTexture(mGLState, fileUrl, cachePath, options, async);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new Text Texture
     * 
     * @param text
     * @param options
     * @return
     */
    public TextTexture createTextTexture(final String text, final TextOptions options) {
        Log.v(TAG, String.format("createTextTexture( %s, %s)", text, options));

        final TextTexture texture = new TextTexture(mGLState, text, options);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * This is used for FrameBuffer
     * 
     * @param width
     * @param height
     * @return
     */
    public BufferTexture createBufferTexture(final int width, final int height, final boolean checkPo2) {
        Log.v(TAG, String.format("createBufferTexture( %d, %d)", width, height));

        final BufferTexture texture = new BufferTexture(mGLState, width, height, checkPo2);

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Create a new general Texture with your own reload logic
     * 
     * @param text
     * @param options
     * @return
     */
    public Texture createDynamicTexture(final Runnable loadRunnable, final TextureOptions options) {
        Log.v(TAG, String.format("createDynamicTexture()"));

        final Texture texture = new Texture(mGLState) {

            @Override
            public void reload() {
                loadRunnable.run();
            }
        };

        // add to list
        addTexture(texture);

        return texture;
    }

    /**
     * Add a new texture which created outside this manager
     * 
     * @param texture
     * @return
     */
    public boolean addTexture(final Texture texture) {
        return mTextures.add(texture);
    }

    /**
     * Remove and Unload a specific Texture.
     * 
     * @param texture
     */
    public void removeTexture(final Texture texture) {
        if (mTextures.remove(texture)) {
            texture.unload();
        }
    }

    /**
     * Can be used after the Surface reloaded.
     */
    public void reloadAllTextures(final boolean includeExpiredTextures) {
        Log.v(TAG, "reloadAllTextures()");

        // reload every texture
        final int len = mTextures.size();
        for (int i = 0; i < len; i++) {
            Texture texture = mTextures.get(i);
            if (texture instanceof DrawableTexture) {
                ((DrawableTexture) texture).setResources(mResources);
            }

            // check for expired texture
            if (includeExpiredTextures || !texture.isExpired()) {
                texture.reload(mGLState);
            }
        }
    }

    /**
     * Can be used after the Surface stopped.
     */
    public void unloadAllTextures() {
        Log.v(TAG, "unloadAllTextures()");

        final int len = mTextures.size();
        // unload all
        for (int i = 0; i < len; i++) {
            mTextures.get(i).unload();
        }
    }

    /**
     * Can be used after the Surface stopped.
     */
    public void removeAllTextures() {
        Log.v(TAG, "removeAllTextures()");

        // unload all first
        unloadAllTextures();

        // empty
        mTextures.clear();
    }

    /**
     * @hide For internal use only. Do NOT call!
     * @param deltaTime
     */
    public void update(final int deltaTime) {
        // negative check
        if (mExpirationCheckInterval > 0) {

            mExpirationCheckElapsedTime += deltaTime;
            if (mExpirationCheckElapsedTime >= mExpirationCheckInterval) {
                // check every texture
                final int len = mTextures.size();
                for (int i = 0; i < len; i++) {
                    mTextures.get(i).update(mExpirationCheckElapsedTime);
                }

                // reset
                mExpirationCheckElapsedTime = 0;
            }
        }
    }

    public int getNumTextures() {
        return mTextures.size();
    }
}
