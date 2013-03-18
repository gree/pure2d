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
