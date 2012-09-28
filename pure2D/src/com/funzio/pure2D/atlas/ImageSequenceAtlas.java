/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
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

    /**
     * Inits and loads images from an Asset folder
     * 
     * @param glState
     * @param assetManager
     * @param assetDir
     * @param options
     */
    public ImageSequenceAtlas(final GLState glState, final AssetManager assetManager, final String assetDir, final TextureOptions options) {
        Log.v(TAG, "ImageSequenceAtlas() " + assetDir);

        mGLState = glState;
        mGL = mGLState.mGL;

        if (!FrameBuffer.isSupported(mGL)) {
            Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
            return;
        }

        mOptions = options;

        try {
            // list the files in assetDir
            String[] files = assetManager.list(assetDir);
            if (files == null || files.length == 0) {
                Log.e(TAG, assetDir + " is empty!");
                return;
            }

            // find the frame dimensions based on the first image
            Bitmap bitmap = Pure2DUtils.getStreamBitmap(assetManager.open(assetDir + "/" + files[0]), mOptions, false, null);
            // find the atlas texture size that fits all the frames, assuming all the images have the same dimensions
            Point textureSize = Pure2DUtils.getSmallestTextureSize(bitmap.getWidth(), bitmap.getHeight(), files.length, glState.getMaxTextureSize());
            // clean it up
            bitmap.recycle();

            // init texture and frame buffer
            initBuffer(textureSize.x, textureSize.y);

            // start loading the images
            loadImages(assetManager, assetDir);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage() + "\n" + Log.getStackTraceString(e));
        }
    }

    /**
     * Inits and loads images from a system file folder
     * 
     * @param glState
     * @param folder
     * @param options
     */
    public ImageSequenceAtlas(final GLState glState, final String folder, final TextureOptions options) {
        Log.v(TAG, "ImageSequenceAtlas() " + folder);

        mGLState = glState;
        mGL = mGLState.mGL;

        if (!FrameBuffer.isSupported(mGL)) {
            Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
            return;
        }

        mOptions = options;

        // list the files in assetDir
        final File file = new File(folder);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, folder + " is empty!");
            return;
        }

        // find the frame dimensions based on the first image
        Bitmap bitmap = Pure2DUtils.getFileBitmap(files[0].getAbsolutePath(), mOptions, false, null);
        // find the atlas texture size that fits all the frames, assuming all the images have the same dimensions
        Point textureSize = Pure2DUtils.getSmallestTextureSize(bitmap.getWidth(), bitmap.getHeight(), files.length, glState.getMaxTextureSize());
        // clean it up
        bitmap.recycle();

        // init texture and frame buffer
        initBuffer(textureSize.x, textureSize.y);

        // start loading the images
        loadImages(files);
    }

    public ImageSequenceAtlas(final GLState glState, final int width, final int height) {
        Log.v(TAG, "ImageSequenceAtlas() " + width + " " + height);

        mGLState = glState;
        mGL = mGLState.mGL;

        if (!FrameBuffer.isSupported(mGL)) {
            Log.e(TAG, "FrameBuffer is not supported!\n" + Log.getStackTraceString(new Exception()));
            return;
        }

        // init texture and frame buffer
        initBuffer(width, height);
    }

    public void reset(final GLState glState, final int width, final int height) {
        mGLState = glState;
        mGL = mGLState.mGL;

        // init texture and frame buffer
        initBuffer(width, height);
    }

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

    /**
     * This loads all of the images in a specific Assets's directory and draws them into the frame buffer.
     * 
     * @param assetManager
     * @param assetDir
     * @param gl
     */
    public void loadImages(final AssetManager assetManager, final String assetDir) {
        String[] files;
        try {
            // list the files in assetDir
            files = assetManager.list(assetDir);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage() + "\n" + Log.getStackTraceString(e));
            return;
        }

        // start drawing images to the frame buffer
        mFrameBuffer.bind();
        for (int i = 0; i < files.length; i++) {

            // create a temp texture for the image
            final AssetTexture texture = new AssetTexture(mGLState, assetManager, assetDir + "/" + files[i], mOptions, true);

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            addFrame(texture, files[i].split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
    }

    /**
     * This loads all of the images in a specific file system's directory and draws them into the frame buffer.
     * 
     * @param files
     */
    public void loadImages(final File[] files) {
        // start drawing images to the frame buffer
        mFrameBuffer.bind();
        for (int i = 0; i < files.length; i++) {

            // create a temp texture for the image
            final FileTexture texture = new FileTexture(mGLState, files[i].getAbsolutePath(), mOptions, true);

            // draw to the frame buffer and create a frame. The frame's name is the filename without the extension such as .png, .jpg
            addFrame(texture, files[i].getName().split("\\.")[0], false);

            // unload the texture
            texture.unload();
        }
        mFrameBuffer.unbind();
    }

    /**
     * Draws the texture to the FrameBuffer and creates a new Frame
     * 
     * @param texture
     * @param frameName
     * @return
     */
    public AtlasFrame addFrame(final Texture texture, final String frameName, final boolean autoBind) {
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

            // if (mGLState.getAxisSystem() == Scene.AXIS_BOTTOM_LEFT) {
            // // framebuffer starts from bottom-left (not top-left)
            // mDrawer.getTextureCoordBuffer().flipVertical();
            // }
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

    public AtlasFrame addFrame(final Bitmap bitmap, final String frameName) {
        // create a temp texture from the given bitmap
        Texture texture = new Texture(mGLState, bitmap) {
            @Override
            public void reload() {
                // TODO Auto-generated method stub
            }
        };

        // draw the texture into the frame buffer
        AtlasFrame frame = addFrame(texture, frameName, true);
        // and unload it
        texture.unload();

        return frame;
    }

    public BufferTexture getTexture() {
        return mTexture;
    }

    public FrameBuffer getFrameBuffer() {
        return mFrameBuffer;
    }
}
