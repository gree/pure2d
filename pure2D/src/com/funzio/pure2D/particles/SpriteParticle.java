/**
 * 
 */
package com.funzio.pure2D.particles;

import java.util.Random;

import android.graphics.PointF;

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
        invalidate();

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
