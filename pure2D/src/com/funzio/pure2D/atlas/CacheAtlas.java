/**
 * 
 */
package com.funzio.pure2D.atlas;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.BufferTexture;
import com.funzio.pure2D.utils.RectPacker;

/**
 * @author long
 */
public class CacheAtlas extends Atlas {

    public static final String TAG = CacheAtlas.class.getSimpleName();

    private GLState mGLState;
    private BufferTexture mTexture;
    private FrameBuffer mFrameBuffer;

    private RectPacker mPacker;
    private Playable mTarget;

    public CacheAtlas(final GLState glState, final Playable target, final int maxWidth) {
        Log.v(TAG, "PlayableAtlas()");

        mGLState = glState;
        mTarget = target;

        initBuffer(maxWidth);
        generateFrames();
    }

    /**
     * Creates a buffer texture to bind to the frame buffer
     */
    private void initBuffer(final int maxWidth) {
        mPacker = new RectPacker(maxWidth);
        final int frames = mTarget.getNumFrames();
        for (int i = 0; i < frames; i++) {
            final RectF frameRect = mTarget.getFrameRect(i);
            mPacker.occupy(Math.round(frameRect.width()), Math.round(frameRect.height()));
        }

        // update the size
        mWidth = mPacker.getWidth();
        mHeight = mPacker.getHeight();

        // Log.v(TAG, String.format("initBuffer(%d, %d)", mWidth, mHeight));

        // create a new texture
        mFrameBuffer = new FrameBuffer(mGLState, (int) mWidth, (int) mHeight, false);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();
    }

    /**
     * Render and generate the frames
     */
    private void generateFrames() {
        final int frames = mTarget.getNumFrames();
        mFrameBuffer.bind();

        // draw bg
        // Rectangular rect = new Rectangular();
        // rect.setSize(mWidth, mHeight);
        // rect.setColor(new GLColor(0, 0.3f, 0, 1f));
        // rect.draw(mGLState);

        final RectF posRect = new RectF();
        for (int i = 0; i < frames; i++) {
            mTarget.stopAt(i);
            posRect.set(mPacker.getRect(i));
            final RectF frameRect = mTarget.getFrameRect(i);
            // mTarget.setPosition(posRect.left - frameRect.left, posRect.top - frameRect.top); // bottom - top
            mTarget.setPosition(posRect.left - frameRect.left, mHeight - posRect.top - frameRect.top - posRect.height()); // top - bottom
            mTarget.draw(mGLState);

            // create frame
            final AtlasFrame frame = new AtlasFrame(this, i, "", posRect);
            frame.mOffset = new PointF(frameRect.left, frameRect.top);
            addFrame(frame);
        }
        mFrameBuffer.unbind();
    }

    public BufferTexture getTexture() {
        return mTexture;
    }

    public FrameBuffer getFrameBuffer() {
        return mFrameBuffer;
    }
}
