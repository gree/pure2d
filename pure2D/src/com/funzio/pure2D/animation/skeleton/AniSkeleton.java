/**
 * 
 */
package com.funzio.pure2D.animation.skeleton;

import android.graphics.RectF;

import com.funzio.pure2D.animation.PlayableObject;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class AniSkeleton extends PlayableObject {
    private VertexBuffer[] mVertexBuffers;
    private TextureCoordBuffer[] mCoordBuffers;
    private AniFile mAniFile;
    private int mFlips = 0;
    private RectF[] mBounds;

    // for debug
    private boolean mDebugging = false;
    private Rectangular mDebugRect = new Rectangular();

    public AniSkeleton(final AniFile file) {
        if (file != null) {
            setAniFile(file);
        }
    }

    public void setAniFile(final AniFile file) {
        // Log.v("long", ">>" + file.mVersion + " " + mAniFile.mNumParts + " " + file.mNumFrames + " " + file.mFrameSize);

        mAniFile = file;
        if (file != null && mAniFile.mSkeletonData.length > 0) {
            mVertexBuffers = new VertexBuffer[file.mNumParts];
            mCoordBuffers = new TextureCoordBuffer[file.mNumParts];
            mBounds = new RectF[file.mNumFrames];
            if (file.mVersion == 2) {
                // version 2 has the same coordinates across the frames
                file.getFrameCoordBuffers(0, mCoordBuffers);
            }

            // set the frame
            mNumFrames = file.mNumFrames;
        } else {
            // unset the frame
            mNumFrames = 0;
        }

        mPreviousFrame = -1;
        mCurrentFrame = 0;
    }

    @Override
    protected void updateFrame(final int frame) {
        if (frame >= mNumFrames) {
            return;
        }

        // get the vertex buffers of the parts, and also the bounds
        final RectF bounds = mBounds[frame] == null ? new RectF() : mBounds[frame];
        mAniFile.getFrameVertexBuffers(frame, mFlips, mVertexBuffers, bounds);
        if (mBounds[frame] == null) {
            mBounds[frame] = bounds;
        }

        if (mAniFile.mVersion == 1) {
            // version 1 has different coordinates across the frames
            mAniFile.getFrameCoordBuffers(frame, mCoordBuffers);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animation.PlayableObject#drawChildren(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    protected void drawChildren(final GLState glState) {
        if (mDebugging) {
            mDebugRect.setRect(mBounds[mCurrentFrame]);
            mDebugRect.setColor(new GLColor(1f, (float) Math.random(), (float) Math.random(), 0.8f));
            mDebugRect.draw(glState);
        }

        final int parts = mNumFrames == 0 ? 0 : mVertexBuffers.length;
        for (int i = 0; i < parts; i++) {
            final Texture texture = mAniFile.getTexture(i);
            // check the texture so we don't draw white boxes
            if (texture != null) {
                texture.bind();
                mCoordBuffers[i].apply(glState);
                mVertexBuffers[i].draw(glState);
            }
        }
    }

    @Override
    public RectF getFrameRect(final int frame) {
        if (mAniFile == null) {
            return null;
        } else {
            if (mBounds != null && mBounds[frame] != null) {
                return mBounds[frame];
            } else {
                final RectF rect = new RectF();
                mAniFile.getFrameVertexBuffers(frame, 0, null, rect);
                return rect;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#dispose()
     */
    @Override
    public void dispose() {
        mVertexBuffers = null;
        mCoordBuffers = null;
    }

    public int getFlips() {
        return mFlips;
    }

    /**
     * Flip the vertices horizontally and/or vertically
     * 
     * @param flips
     * @see DisplayObject, FLIP_X, FLIP_Y
     */
    public void setFlips(final int flips) {
        mFlips = flips;
    }

    public boolean isDebugging() {
        return mDebugging;
    }

    public void setDebugging(final boolean debugging) {
        mDebugging = debugging;
    }
}
