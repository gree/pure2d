/**
 * 
 */
package com.funzio.pure2D.particles;

import com.funzio.pure2D.shapes.Clip;

/**
 * @author long
 */
public class ClipParticle extends Clip implements Particle {

    protected ParticleEmitter mEmitter;
    protected Listener mListener = null;
    protected boolean mFinished = false;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset()
     */
    @Override
    public void reset() {
        mEmitter = null;
        mListener = null;
        mFinished = false;
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
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public Listener getListener() {
        return mListener;
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
