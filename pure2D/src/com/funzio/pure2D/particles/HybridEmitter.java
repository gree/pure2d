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
    private Listener mListener;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        mEmitter = null;
        mListener = null;
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
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.Particle#getListener()
     */
    @Override
    public Listener getListener() {
        return mListener;
    }

    @Override
    public void finish() {
        if (mEmitter != null) {
            mEmitter.onParticleFinish(this);
        } else {
            // auto remove
            super.finish();
        }

        // additional listener
        if (mListener != null) {
            mListener.onParticleFinish(this);
        }
    }
}
