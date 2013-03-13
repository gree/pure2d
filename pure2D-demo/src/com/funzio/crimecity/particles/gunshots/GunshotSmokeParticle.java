/**
 * 
 */
package com.funzio.crimecity.particles.gunshots;

import android.graphics.PointF;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class GunshotSmokeParticle extends SpriteParticle {
    private static final int SIZE = 16;

    private float mDeltaRotation;
    private PointF mDeltaScale = new PointF();
    private float mAlphaDelta;
    private int mFrame;

    public GunshotSmokeParticle(final Texture texture) {
        super();

        setBlendFunc(ParticleAdapter.BF_ADD);
        setTexture(texture);
        reset();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset();

        // initial state
        setSize(SIZE, SIZE);
        mAlpha = 1;
        // mPosition.x = mRandom.nextInt(101) - 50;
        // mPosition.y = mRandom.nextInt(101) - 50;
        mRotation = 0;
        mScale.x = mScale.y = 1;
        mVisible = true;

        // deltas
        mVelocity.x = 1;
        mVelocity.y = 1;
        mDeltaRotation = mRandom.nextInt(5) - 2;
        mDeltaScale.x = mDeltaScale.y = .08f;
        mAlphaDelta = -.01f;

        // frame stuff
        mFrame = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // apply the deltas
        mPosition.x += mVelocity.x;
        mPosition.y += mVelocity.y;
        mRotation += mDeltaRotation;
        mScale.x += mDeltaScale.x;
        mScale.y += mDeltaScale.y;
        mAlpha += mAlphaDelta;
        invalidate();

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;

        if (mFrame >= Gunshots.TOTAL_FRAMES) {
            finish();
        }

        return true;
    }
}
