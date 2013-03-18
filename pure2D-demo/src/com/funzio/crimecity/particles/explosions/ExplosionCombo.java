/**
 * 
 */
package com.funzio.crimecity.particles.explosions;

import android.graphics.PointF;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.smoke.SmokePuff;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.RectangularEmitter;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public class ExplosionCombo extends RectangularEmitter implements Reusable {

    private int mFrame = 0;

    private Texture mFireTexture;
    private Texture mSmokeTexture;

    private Runnable mAddParticleRunnable = new Runnable() {

        @Override
        public void run() {
            PointF position = new PointF(mPosition.x + RANDOM.nextInt(25), mPosition.y + RANDOM.nextInt(25));

            // explosion
            BlackExplosion explosion = new BlackExplosion(mFireTexture);
            explosion.setPosition(position);
            addParticle(explosion);

            // with smoke puff...
            SmokePuff smoke = new SmokePuff(mSmokeTexture);
            smoke.setPosition(position);
            addParticle(smoke);
        }
    };

    public ExplosionCombo() {
        setRemoveOnFinish(true);

        mFireTexture = ParticleAdapter.TEXTURE_MANAGER.mFireTexture;
        mSmokeTexture = ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture;
    }

    public ExplosionCombo(final Texture fire, final Texture smoke) {
        setRemoveOnFinish(true);

        mFireTexture = fire;
        mSmokeTexture = smoke;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        mFrame = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {

        if (mFrame <= 20 && mFrame % 10 == 0) {
            queueEvent(mAddParticleRunnable);
        }

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;

        return true;
    }
}
