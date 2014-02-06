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
package com.funzio.pure2D.particles;

import java.util.Random;

import android.graphics.PointF;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class SpriteParticle extends Sprite implements Particle {

    protected ParticleEmitter mEmitter;
    protected Listener mListener = null;
    protected PointF mVelocity = new PointF(0, 0);
    protected final Random mRandom = Particle.RANDOM;

    protected boolean mFinished = false;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset()
     */
    public void reset(final Object... params) {
        mEmitter = null;
        mListener = null;
        mFinished = false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        mPosition.x += mVelocity.x;
        mPosition.y += mVelocity.y;
        invalidate(InvalidateFlags.POSITION);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Rectangular#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // origin is always at the center
        setOriginAtCenter();
    }

    public void setEmitter(final ParticleEmitter emitter) {
        mEmitter = emitter;
    }

    public ParticleEmitter getEmitter() {
        return mEmitter;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#setListener(com.funzio.pure2D.particles.Particle.Listener)
     */
    @Override
    public void setParticleListener(final Listener listener) {
        mListener = listener;
    }

    public Listener getParticleListener() {
        return mListener;
    }

    /**
     * @return the velocity
     */
    public PointF getVelocity() {
        return mVelocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(final PointF velocity) {
        mVelocity = velocity;
    }

    public void finish() {
        mFinished = true;

        if (mEmitter != null) {
            mEmitter.onParticleFinish(this);
        } else {
            // auto remove
            removeFromParent();
        }

        // additional listener
        if (mListener != null) {
            mListener.onParticleFinish(this);
        }
    }

    public boolean isFinished() {
        return mFinished;
    }

}
