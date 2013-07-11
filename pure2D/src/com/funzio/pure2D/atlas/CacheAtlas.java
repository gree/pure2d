/**
 * 
 */
package com.funzio.pure2D.atlas;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
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

        initBuffer(maxWidth <= 0 ? Pure2D.GL_MAX_TEXTURE_SIZE : maxWidth);
        generateFrames();
    }

    /**
     * Creates a buffer texture to bind to the frame buffer
     */
    private void initBuffer(final int maxWidth) {
        mPacker = new RectPacker(maxWidth, !Pure2D.GL_NPOT_TEXTURE_SUPPORTED); // !Pure2D.GL_NPOT_TEXTURE_SUPPORTED
        mPacker.setQuickMode(true);
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
        mFrameBuffer = new FrameBuffer(mGLState, mWidth, mHeight, false);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();
    }

    /**
     * Render and generate the frames
     */
    private void generateFrames() {
        mFrameBuffer.bind(Scene.AXIS_TOP_LEFT); // invert

        // debug: draw bg
        // final Rectangular debugRect = new Rectangular();
        // debugRect.setSize(mWidth, mHeight);
        // debugRect.setColor(new GLColor(0, 0.3f, 0, 1f));
        // debugRect.draw(mGLState);

        final RectF posRect = new RectF();
        final int frames = mTarget.getNumFrames();
        for (int i = 0; i < frames; i++) {

            // prepare to draw the frame
            mTarget.stopAt(i);
            posRect.set(mPacker.getRect(i));
            final RectF frameRect = mTarget.getFrameRect(i);
            mTarget.setOrigin(frameRect.left, frameRect.top);

            // create frame
            final AtlasFrame frame = new AtlasFrame(this, i, "", posRect);
            frame.mOffset = new PointF(frameRect.left, frameRect.top);
            addFrame(frame);

            if (Math.round(frameRect.width()) == posRect.width()) {
                mTarget.setRotation(0);
                mTarget.setPosition(posRect.left, mHeight - (posRect.top + posRect.height())); // top to bottom
                mTarget.draw(mGLState);
            } else {
                // rotate 90 CCW
                mTarget.setRotation(90);
                mTarget.setPosition(posRect.left + frameRect.height(), mHeight - (posRect.top + posRect.height())); // top to bottom
                mTarget.draw(mGLState);

                frame.rotateCW();
            }
        }

        // debug: draw dots
        // final int pixelWidth = 2;
        // final Rectangular r = new Rectangular();
        // r.setSize(pixelWidth, pixelWidth);
        // final TreeSet<Point> hots = mPacker.getHotPoints();
        // for (Point p : hots) {
        // r.setPosition(p.x, mHeight - p.y - pixelWidth);
        // r.draw(mGLState);
        // }

        // done
        mFrameBuffer.unbind();
    }

    public BufferTexture getTexture() {
        return mTexture;
    }

    public FrameBuffer getFrameBuffer() {
        return mFrameBuffer;
    }
}
