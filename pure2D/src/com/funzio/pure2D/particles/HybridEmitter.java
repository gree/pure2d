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

/**
 * @author long
 * @category An Emitter that also acts like a Particle
 */
public class HybridEmitter extends RectangularEmitter implements Particle {

    private ParticleEmitter mEmitter;
    private Particle.Listener mParticleListener;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        mEmitter = null;
        mListener = null;
        mParticleListener = null;
        mFinished = false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#setEmitter(com.funzio.pure2D.particles.ParticleEmitter)
     */
    @Override
    public void setEmitter(final ParticleEmitter emitter) {
        mEmitter = emitter;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#getEmitter()
     */
    @Override
    public ParticleEmitter getEmitter() {
        return mEmitter;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#setListener(com.funzio.pure2D.particles.Particle.Listener)
     */
    @Override
    public void setParticleListener(final Particle.Listener listener) {
        mParticleListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#getParticleListener()
     */
    @Override
    public Particle.Listener getParticleListener() {
        return mParticleListener;
    }

    @Override
    public void finish() {
        if (mEmitter != null) {
            mEmitter.onParticleFinish(this);
        } else {
            // auto remove
            super.finish();
        }

        // particle listener
        if (mParticleListener != null) {
            mParticleListener.onParticleFinish(this);
        }
    }
}
