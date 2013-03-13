/**
 * 
 */
package com.funzio.crimecity.particles.fire;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class TankFireSparkParticle extends TankFireParticle {

    public TankFireSparkParticle(final Texture texture) {
        // don't use texture for now
        super(null);

        setBlendFunc(ParticleAdapter.BF_ADD);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.ExplosionParticle#reset()
     */
    @Override
    public void reset() {
        super.reset();

        // setColor(new GLColor(1f, 0.5f, 0f, 1f));
        mPosition.x = 0;
        mPosition.y = 0;
        mVelocity.x = mRandom.nextInt(11) - 5;
        mVelocity.y = mRandom.nextInt(11) - 5;
        mScale.x = mScale.y = 1f;

        setSize(2.0f + mRandom.nextInt(3), 2.0f + mRandom.nextInt(3));

        // deltas
        mDeltaRotation = 0;
        mDeltaScale.x = mDeltaScale.y = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.ExplosionParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // wind flow up-right
        if (mVelocity.x < 3) {
            mVelocity.x += .15;
        }
        if (mVelocity.y < 3) {
            mVelocity.y += .15;
        }

        return super.update(deltaTime);
    }

}
