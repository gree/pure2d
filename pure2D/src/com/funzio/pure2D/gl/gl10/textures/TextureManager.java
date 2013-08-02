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

    protected Scene mScene;
    protected ArrayList<Texture> mTextures = new ArrayList<Texture>();
    protected GLState mGLState;
    protected GL10 mGL;

    protected Resources mResources;
    protected AssetManager mAssets;

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
        reloadAllTextures();
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
    public DrawableTexture createDrawableTextureAsync(final int drawable, final TextureOptions options) {
        Log.v(TAG, String.format("createDrawableTextureAsync(%d, %s)", drawable, options));

        final DrawableTexture texture = new DrawableTexture(mGLState, getResources(), drawable, options, true);

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
    public AssetTexture createAssetTextureAsync(final String filePath, final TextureOptions options) {
        Log.v(TAG, String.format("createAssetTextureAsync(%s, %s)", filePath, options));

        final AssetTexture texture = new AssetTexture(mGLState, mAssets, filePath, options, true);

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
    public FileTexture createFileTextureAsync(final String filePath, final TextureOptions options) {
        Log.v(TAG, String.format("createFileTextureAsync( %s, %s)", filePath, options));

        final FileTexture texture = new FileTexture(mGLState, filePath, options, true);

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
    public URLTexture createURLTextureAsync(final String url, final TextureOptions options) {
        Log.v(TAG, String.format("createURLTextureAsync(%s, %s)", url, options));

        final URLTexture texture = new URLTexture(mGLState, url, options, true);

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
        return new BufferTexture(mGLState, width, height, checkPo2);
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
    public void reloadAllTextures() {
        Log.v(TAG, "reloadAllTextures()");

        // reload every texture
        final int len = mTextures.size();
        for (int i = 0; i < len; i++) {
            Texture texture = mTextures.get(i);
            if (texture instanceof DrawableTexture) {
                ((DrawableTexture) texture).setResources(mResources);
            }
            texture.reload(mGLState);
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
            Texture texture = mTextures.get(i);
            texture.unload();
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

}
