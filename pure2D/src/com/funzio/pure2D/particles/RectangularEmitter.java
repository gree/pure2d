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

    // protected List<Particle> mParticles = new ArrayList<Particle>();
    protected Random mRandom = new Random();
    protected boolean mFinished = false;
    private boolean mRemoveOnFinish = false;
    private int nNumParticles = 0;

    public boolean draw(final GLState glState) {
        // draw nothing
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // do nothing
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#dispose()
     */
    @Override
    public void dispose() {

        // for (int i = 0; i < nNumParticles; i++) {
        // Particle particle = mParticles.get(i);
        // particle.setListener(null);
        //
        // // also remove it from parent, or not
        // // /* particle.removeFromParent();
        // }
        // mParticles.clear();

        // simple clean up
        nNumParticles = 0;
        mRandom = null;
        mFinished = false;
    }

    protected PointF getNextPosition() {
        PointF pt = new PointF(mPosition.x + mRandom.nextInt((int) mSize.x), mPosition.y + mRandom.nextInt((int) mSize.y));
        return pt;
    }

    protected boolean addParticle(final Particle particle) {
        if (particle.getEmitter() != this) {
            // mParticles.add(particle);
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
            // mParticles.remove(particle);
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
        mFinished = true;
        if (mRemoveOnFinish) {
            // auto remove me
            removeFromParent();
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

    public boolean removeFromParentSafely() {
        if (mParent != null) {
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    mParent.removeChild(RectangularEmitter.this);
                }
            });
        }
        return false;
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // clean up
        dispose();
    }
}
