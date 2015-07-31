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

import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.AssetTexture;
import com.funzio.pure2D.gl.gl10.textures.BufferTexture;
import com.funzio.pure2D.gl.gl10.textures.FileTexture;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 * @category This works but does not handle GLSurface's lifecycle automatically.
 */
@Deprecated
public class ImageSequenceBufferAtlas extends Atlas {

    public static final String TAG = ImageSequenceBufferAtlas.class.getSimpleName();

    private GLState mGLState;
    // private GL10 mGL;
    private BufferTexture mTexture;
    private FrameBuffer mFrameBuffer;

    private int mStartX = 0;
    private int mStartY = 0;
    private int mCurrentRowHeight = 0;
    private int mFrameIndex = 0;
    private Sprite mDrawer;

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

            // init the buffer based on the first texture
            if (mTexturesLoaded == 0) {
                initBuffer(texture, mTextures.length);
            }

            // all textures are loaded?
            if (++mTexturesLoaded == mTextures.length) {
                // create the frames
                createFrames();
            }
        }
    };

    /**
     * Inits and loads images from an Asset folder
     * 
     * @param glState
     */
    public ImageSequenceBufferAtlas(final GLState glState) {
        Log.v(TAG, "ImageSequenceAtlas()");

        mGLState = glState;
        // mGL = mGLState.mGL;

        if (!FrameBuffer.isSupported()) {
            Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
            return;
        }
    }

    // public ImageSequenceAtlas(final GLState glState, final int width, final int height) {
    // Log.v(TAG, "ImageSequenceAtlas() " + width + " " + height);
    //
    // mGLState = glState;
    // mGL = mGLState.mGL;
    //
    // if (!FrameBuffer.isSupported(mGL)) {
    // Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
    // return;
    // }
    //
    // // init texture and frame buffer
    // initBuffer(width, height);
    // }

    // public void reset(final GLState glState, final int width, final int height) {
    // mGLState = glState;
    // mGL = mGLState.mGL;
    //
    // // init texture and frame buffer
    // initBuffer(width, height);
    // }

    /**
     * Creates a buffer texture to bind to the frame buffer
     * 
     * @param width
     * @param height
     */
    private void initBuffer(final int width, final int height) {
        Log.v(TAG, String.format("initBuffer(%d, %d)", width, height));

        mWidth = width;
        mHeight = height;

        // create a new texture
        mFrameBuffer = new FrameBuffer(mGLState, mWidth, mHeight, true);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();
    }

    private void initBuffer(final Texture firstTexture, final int numFrames) {
        // find the atlas texture size that fits all the frames, assuming all the images have the same dimensions
        final PointF size = firstTexture.getSize();
        final Point textureSize = Pure2DUtils.getSmallestTextureSize((int) size.x, (int) size.y, numFrames, Math.min(1024, Pure2D.GL_MAX_TEXTURE_SIZE), !Pure2D.GL_NPOT_TEXTURE_SUPPORTED);

        // init texture and frame buffer
        initBuffer(textureSize.x, textureSize.y);
    }

    /**
     * This loads all of the images in a specific Assets's directory and draws them into the frame buffer, and blocks GL Thread.
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadDir(final AssetManager assetManager, final String assetDir, final TextureOptions options) {
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

            // create a temp texture for the image
            final AssetTexture texture = mGLState.getTextureManager().createAssetTexture(assetDir + "/" + filenames[i], options);

            // init the buffer based on the first texture
            if (i == 0) {
                initBuffer(texture, filenames.length);

                // start drawing images to the frame buffer
                mFrameBuffer.bind(Scene.AXIS_TOP_LEFT);
            }

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(texture, filenames[i].split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

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

            // create a temp texture for the image
            final FileTexture texture = mGLState.getTextureManager().createFileTexture(files[i].getAbsolutePath(), options);

            // init the buffer based on the first texture
            if (i == 0) {
                initBuffer(texture, files.length);

                // start drawing images to the frame buffer
                mFrameBuffer.bind(Scene.AXIS_TOP_LEFT);
            }

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(texture, files[i].getName().split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

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
            final FileTexture texture = mGLState.getTextureManager().createFileTexture(files[i].getAbsolutePath(), options, true); // async
            // listen to it
            texture.setListener(mTextureListener);

            // for ref later
            mTextures[i] = texture;
            mTextureNames[i] = files[i].getName();
        }
    }

    protected void createFrames() {
        // start drawing images to the frame buffer
        mFrameBuffer.bind(Scene.AXIS_TOP_LEFT);
        for (int i = 0; i < mTextures.length; i++) {

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            createFrame(mTextures[i], mTextureNames[i].split("\\.")[0], false);

            // unload the texture
            mTextures[i].unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

        // done
        mTextures = null;
        mTextureNames = null;

        // callback
        Log.d(TAG, "createFrames() | done: " + mImageDir);
        if (mListener != null) {
            mListener.onAtlasLoad(this);
        }
    }

    protected void createFrame(final Texture texture, final String frameName, final boolean autoBind) {
        final PointF frameSize = texture.getSize();
        // find the max frame height to define the row height
        if (frameSize.y > mCurrentRowHeight) {
            mCurrentRowHeight = (int) frameSize.y;
        }

        if (autoBind) {
            // start drawing to the frame buffer
            mFrameBuffer.bind(Scene.AXIS_TOP_LEFT);
        }

        if (mDrawer == null) {
            // init the drawer
            mDrawer = new Sprite();
        }
        // use the texture
        mDrawer.setTexture(texture);
        // move to the right position
        mDrawer.moveTo(mStartX, mHeight - mStartY - frameSize.y);
        // draw onto frame buffer
        mDrawer.draw(mGLState);

        if (autoBind) {
            // done drawing
            mFrameBuffer.unbind();
        }

        // create frame
        final AtlasFrame frame = new AtlasFrame(mTexture, mFrameIndex++, frameName, new RectF(mStartX, mStartY, mStartX + (int) frameSize.x - 1, mStartY + (int) frameSize.y - 1));
        addFrame(frame);

        // find next position
        mStartX += frameSize.x;
        if (mStartX + frameSize.x > mWidth) {
            // go to new row
            mStartX = 0;
            mStartY += mCurrentRowHeight;
            mCurrentRowHeight = 0;
        }
    }

    // protected AtlasFrame addFrame(final Bitmap bitmap, final String frameName) {
    // // create a temp texture from the given bitmap
    // Texture texture = new Texture(mGLState, bitmap) {
    // @Override
    // public void reload() {
    // // TODO Auto-generated method stub
    // }
    // };
    //
    // // draw the texture into the frame buffer
    // AtlasFrame frame = addFrame(texture, frameName, true);
    // // and unload it
    // texture.unload();
    //
    // return frame;
    // }

    public BufferTexture getTexture() {
        return mTexture;
    }

    // public FrameBuffer getFrameBuffer() {
    // return mFrameBuffer;
    // }
}
