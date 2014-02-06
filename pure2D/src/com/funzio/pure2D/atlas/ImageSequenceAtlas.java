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
package com.funzio.pure2D.atlas;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import android.content.res.AssetManager;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.AssetTexture;
import com.funzio.pure2D.gl.gl10.textures.FileTexture;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class ImageSequenceAtlas extends Atlas {

    public static final String TAG = ImageSequenceAtlas.class.getSimpleName();

    private GLState mGLState;

    private int mFrameIndex = 0;

    private String mImageDir;
    private static Comparator<? super File> mFileComparator = new Comparator<File>() {
        public int compare(final File file1, final File file2) {
            return file1.compareTo(file2);
        }
    };

    // textures to load
    private Texture[] mTextures;
    private String[] mTextureNames;
    private int mTexturesLoaded = 0;
    private Texture.Listener mTextureListener = new Texture.Listener() {
        @Override
        public void onTextureLoad(final Texture texture) {

            // all textures are loaded?
            if (++mTexturesLoaded == mTextures.length) {
                createFrames();
            }
        }
    };

    /**
     * Inits and loads images from an Asset folder
     * 
     * @param glState
     */
    public ImageSequenceAtlas(final GLState glState) {
        Log.v(TAG, "ImageSequenceAtlas()");

        mGLState = glState;
    }

    /**
     * This loads all of the images in a specific Assets's directory and draws them into the frame buffer, and blocks GL Thread.
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadDir(final AssetManager assetManager, final String assetDir, final TextureOptions options) {
        loadDir(assetManager, assetDir, options, null);
    }

    /**
     * This loads all of the images in included from a specific Assets's directory and draws them into the frame buffer, and blocks GL Thread. If included is null all images are loaded
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadDir(final AssetManager assetManager, final String assetDir, final TextureOptions options, final Set<String> included) {
        Log.d(TAG, "loadDir() | " + assetDir);

        mImageDir = assetDir;
        String[] filenames;
        try {
            // list the files in assetDir
            filenames = assetManager.list(assetDir);
            if (filenames == null || filenames.length == 0) {
                Log.e(TAG, assetDir + " is empty!");
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage() + "\n" + Log.getStackTraceString(e));
            return;
        }

        for (int i = 0; i < filenames.length; i++) {
            if (included != null && !included.contains(filenames[i])) {
                continue;
            }

            // create a temp texture for the image
            final AssetTexture texture = mGLState.getTextureManager().createAssetTexture(assetDir + "/" + filenames[i], options);
            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(texture, filenames[i].split("\\.")[0]);
        }

        // callback
        Log.d(TAG, "loadDir() | done: " + assetDir);
        if (mListener != null) {
            mListener.onAtlasLoad(this);
        }
    }

    /**
     * This asynchronously loads all of the images in a specific Assets's directory and draws them into the frame buffer, without blocking GL Thread.
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadDirAsync(final AssetManager assetManager, final String assetDir, final TextureOptions options) {
        Log.d(TAG, "loadDirAsync() | " + assetDir);

        mImageDir = assetDir;
        String[] filenames;
        try {
            // list the files in assetDir
            filenames = assetManager.list(assetDir);
            if (filenames == null || filenames.length == 0) {
                Log.e(TAG, assetDir + " is empty!");
                return;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage() + "\n" + Log.getStackTraceString(e));
            return;
        }

        // prepare
        mTextures = new Texture[filenames.length];
        mTextureNames = filenames;
        mTexturesLoaded = 0;

        for (int i = 0; i < filenames.length; i++) {
            // load the texture asynchronously
            final AssetTexture texture = mGLState.getTextureManager().createAssetTexture(assetDir + "/" + filenames[i], options, true); // async
            // listen to it
            texture.setListener(mTextureListener);

            // for ref later
            mTextures[i] = texture;
        }
    }

    /**
     * This loads all of the images in a specific file system's directory and draws them into the frame buffer.
     * 
     * @param files
     */
    public void loadDir(final String dir, final TextureOptions options) {
        loadDir(dir, options, null);
    }

    /**
     * This loads all images in included from a specific file system's directory and draws them into the frame buffer. If included is null all images are loaded
     * 
     * @param files
     */
    public void loadDir(final String dir, final TextureOptions options, final Set<String> included) {
        Log.d(TAG, "loadDir() | " + dir);

        mImageDir = dir;
        // list the files in assetDir
        final File file = new File(dir);
        final File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, dir + " is empty!");
            return;
        }
        // sort the files by name
        Arrays.sort(files, mFileComparator);

        for (int i = 0; i < files.length; i++) {
            if (included != null && !included.contains(files[i].getName())) {
                continue;
            }

            // create a temp texture for the image
            final FileTexture texture = mGLState.getTextureManager().createFileTexture(files[i].getAbsolutePath(), options);

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(texture, files[i].getName().split("\\.")[0]);
        }

        // callback
        Log.d(TAG, "loadDir() | done: " + dir);
        if (mListener != null) {
            mListener.onAtlasLoad(this);
        }
    }

    /**
     * This asynchronously loads all of the images in a specific file system's directory and draws them into the frame buffer, without blocking GL Thread.
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadDirAsync(final String dir, final TextureOptions options) {
        Log.d(TAG, "loadDirAsync() | " + dir);

        mImageDir = dir;
        // list the files in assetDir
        final File file = new File(dir);
        final File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, dir + " is empty!");
            return;
        }
        // sort the files by name
        Arrays.sort(files, mFileComparator);

        // prepare
        mTextures = new Texture[files.length];
        mTextureNames = new String[files.length];
        mTexturesLoaded = 0;

        for (int i = 0; i < files.length; i++) {
            // load the texture asynchronously
            final FileTexture texture = mGLState.getTextureManager().createFileTexture(files[i].getAbsolutePath(), options, true);// async
            // listen to it
            texture.setListener(mTextureListener);

            // for ref later
            mTextures[i] = texture;
            mTextureNames[i] = files[i].getName();
        }
    }

    protected void createFrames() {
        for (int i = 0; i < mTextures.length; i++) {
            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(mTextures[i], mTextureNames[i].split("\\.")[0]);
        }

        // done
        mTextures = null;
        mTextureNames = null;

        // callback
        Log.d(TAG, "createFrames() | done: " + mImageDir);
        if (mListener != null) {
            mListener.onAtlasLoad(this);
        }
    }

    protected boolean createFrame(final Texture texture, final String frameName) {
        // create frame
        if (texture.isLoaded()) {
            addFrame(new AtlasFrame(texture, mFrameIndex++, frameName));
        } else {
            Log.e(TAG, "Texture not loaded! " + frameName);
            return false;
        }

        return true;
    }
}
