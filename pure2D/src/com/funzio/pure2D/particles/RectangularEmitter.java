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

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class RectangularEmitter extends BaseDisplayObject implements ParticleEmitter {

    protected boolean mFinished = false;
    protected boolean mRemoveOnFinish = false;
    protected int nNumParticles = 0;
    protected final Random mRandom = Particle.RANDOM;

    protected Listener mListener;

    @Override
    protected boolean drawChildren(final GLState glState) {
        // draw nothing
        return false;
    }

    @Override
    public void dispose() {

        // simple clean up
        nNumParticles = 0;
        mFinished = false;
    }

    /**
     * This is more memory efficient
     * 
     * @param pt
     * @return
     */
    public PointF getNextPosition(final PointF pt) {
        pt.x = mPosition.x - mOrigin.x + (mSize.x > 1 ? mRandom.nextInt((int) mSize.x) : 0);
        pt.y = mPosition.y - mOrigin.y + (mSize.y > 1 ? mRandom.nextInt((int) mSize.y) : 0);

        return pt;
    }

    /**
     * This one consume more memory to create a new instance of PointF
     * 
     * @return
     */
    public PointF getNextPosition() {
        final PointF pt = new PointF();
        pt.x = mPosition.x - mOrigin.x + (mSize.x > 1 ? mRandom.nextInt((int) mSize.x) : 0);
        pt.y = mPosition.y - mOrigin.y + (mSize.y > 1 ? mRandom.nextInt((int) mSize.y) : 0);

        return pt;
    }

    protected boolean addParticle(final Particle particle) {
        if (particle.getEmitter() != this) {
            nNumParticles++;

            // register listeners
            particle.setEmitter(this);

            // add particle to the same parent
            if (mParent != null) {
                mParent.addChild(particle);
            }
            return true;
        }

        return false;
    }

    protected boolean removeParticle(final Particle particle) {
        if (particle.getEmitter() == this) {
            nNumParticles--;

            // remove listener
            particle.setEmitter(null);

            // remove particle from parent
            if (mParent != null) {
                mParent.removeChild(particle);
            }

            // done?
            if (nNumParticles == 0) {
                finish();
            }

            return true;
        }

        return false;
    }

    public int getNumParticles() {
        return nNumParticles;
    }

    public void finish() {
        // check
        if (mFinished) {
            return;
        }

        mFinished = true;
        if (mRemoveOnFinish) {
            // auto remove me
            removeFromParent();
        }

        // check listener
        if (mListener != null) {
            mListener.onEmitterFinish(this);
        }
    }

    public void queueFinish() {
        final boolean success = queueEvent(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        });

        if (!success) {
            finish();
        }
    }

    public boolean isFinished() {
        return mFinished;
    }

    /**
     * @return the removeOnFinish
     */
    public boolean isRemoveOnFinish() {
        return mRemoveOnFinish;
    }

    /**
     * @param removeOnFinish the removeOnFinish to set
     */
    public void setRemoveOnFinish(final boolean removeOnFinish) {
        mRemoveOnFinish = removeOnFinish;
    }

    @Override
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    @Override
    public Listener getListener() {
        return mListener;
    }

    @Override
    public void onParticleFinish(final Particle particle) {
        queueEvent(new Runnable() {

            @Override
            public void run() {
                // auto remove
                removeParticle(particle);
            }
        });
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        // clean up
        dispose();
    }
}
