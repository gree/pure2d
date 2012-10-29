/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

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
 */
public class ImageSequenceAtlas extends Atlas {

    public static final String TAG = ImageSequenceAtlas.class.getSimpleName();

    private GLState mGLState;
    private GL10 mGL;
    private TextureOptions mOptions;
    private BufferTexture mTexture;
    private FrameBuffer mFrameBuffer;

    private int mStartX = 0;
    private int mStartY = 0;
    private int mCurrentRowHeight = 0;
    private int mFrameIndex = 0;
    private Sprite mDrawer;

    // textures to load
    private Texture[] mTextures;
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
    public ImageSequenceAtlas(final GLState glState) {
        Log.v(TAG, "ImageSequenceAtlas()");

        mGLState = glState;
        mGL = mGLState.mGL;

        if (!FrameBuffer.isSupported(mGL)) {
            Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
            return;
        }
    }

    /**
     * Loads images from an Asset folder
     * 
     * @param glState
     * @param assetManager
     * @param assetDir
     * @param options
     */
    public void load(final AssetManager assetManager, final String assetDir, final TextureOptions options, final boolean async) {
        Log.v(TAG, "load() " + assetDir);

        mOptions = options;

        // start loading the images
        if (async) {
            loadImagesAsync(assetManager, assetDir);
        } else {
            loadImages(assetManager, assetDir);
        }
    }

    /**
     * Loads images from a system file folder
     * 
     * @param glState
     * @param folder
     * @param options
     */
    public void load(final String folder, final TextureOptions options, final boolean async) {
        Log.v(TAG, "load() " + folder);

        mOptions = options;

        // start loading the images
        if (async) {
            loadImagesAsync(folder);
        } else {
            loadImages(folder);
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

        // make sure the size is power of 2
        mWidth = Pure2DUtils.getNextPO2(width);
        mHeight = Pure2DUtils.getNextPO2(height);

        // create a new texture
        mFrameBuffer = new FrameBuffer(mGLState, mWidth, mHeight);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();
    }

    private void initBuffer(final Texture firstTexture, final int numFrames) {
        // find the atlas texture size that fits all the frames, assuming all the images have the same dimensions
        final PointF size = firstTexture.getSize();
        final Point textureSize = Pure2DUtils.getSmallestTextureSize((int) size.x, (int) size.y, numFrames, mGLState.getMaxTextureSize());

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
    protected void loadImages(final AssetManager assetManager, final String assetDir) {

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
            final AssetTexture texture = new AssetTexture(mGLState, assetManager, assetDir + "/" + filenames[i], mOptions, true);

            // init the buffer based on the first texture
            if (i == 0) {
                initBuffer(texture, filenames.length);

                // start drawing images to the frame buffer
                mFrameBuffer.bind();
            }

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            addFrame(texture, filenames[i].split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

        // callback
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
    protected void loadImagesAsync(final AssetManager assetManager, final String assetDir) {

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
        mTexturesLoaded = 0;

        for (int i = 0; i < filenames.length; i++) {
            // load the texture asynchronously
            final AssetTexture texture = new AssetTexture(mGLState, assetManager, assetDir + "/" + filenames[i], mOptions, true, true); // async
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
    protected void loadImages(final String folder) {
        // list the files in assetDir
        final File file = new File(folder);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, folder + " is empty!");
            return;
        }

        for (int i = 0; i < files.length; i++) {

            // create a temp texture for the image
            final FileTexture texture = new FileTexture(mGLState, files[i].getAbsolutePath(), mOptions, true);

            // init the buffer based on the first texture
            if (i == 0) {
                initBuffer(texture, files.length);

                // start drawing images to the frame buffer
                mFrameBuffer.bind();
            }

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            addFrame(texture, files[i].getName().split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

        // callback
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
    protected void loadImagesAsync(final String folder) {

        // list the files in assetDir
        final File file = new File(folder);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, folder + " is empty!");
            return;
        }

        // prepare
        mTextures = new Texture[files.length];
        mTexturesLoaded = 0;

        for (int i = 0; i < files.length; i++) {
            // load the texture asynchronously
            final FileTexture texture = new FileTexture(mGLState, files[i].getAbsolutePath(), mOptions, true, true); // async
            // listen to it
            texture.setListener(mTextureListener);

            // for ref later
            mTextures[i] = texture;
        }
    }

    protected void createFrames() {
        // start drawing images to the frame buffer
        mFrameBuffer.bind();
        for (final Texture texture : mTextures) {

            Log.v("long", "texture: " + texture);
            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            addFrame(texture, texture.toString().split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();

        // done
        mTextures = null;

        // callback
        if (mListener != null) {
            mListener.onAtlasLoad(this);
        }
    }

    /**
     * Draws the texture to the FrameBuffer and creates a new Frame
     * 
     * @param texture
     * @param frameName
     * @return
     */
    protected AtlasFrame addFrame(final Texture texture, final String frameName, final boolean autoBind) {
        PointF frameSize = texture.getSize();
        // find the max frame height to define the row height
        if (frameSize.y > mCurrentRowHeight) {
            mCurrentRowHeight = (int) frameSize.y;
        }

        if (autoBind) {
            // start drawing to the frame buffer
            mFrameBuffer.bind();
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
        AtlasFrame frame = new AtlasFrame(this, mFrameIndex++, frameName, new Rect(mStartX, mStartY, mStartX + (int) frameSize.x - 1, mStartY + (int) frameSize.y - 1));
        addFrame(frame);

        // find next position
        mStartX += frameSize.x;
        if (mStartX + frameSize.x > mWidth) {
            // go to new row
            mStartX = 0;
            mStartY += mCurrentRowHeight;
            mCurrentRowHeight = 0;
        }

        return frame;
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
