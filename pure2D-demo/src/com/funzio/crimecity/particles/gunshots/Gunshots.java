/**
 * 
 */
package com.funzio.crimecity.particles.gunshots;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.Particle;
import com.funzio.pure2D.particles.RectangularEmitter;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public class Gunshots extends RectangularEmitter implements Reusable {
    protected static final int TOTAL_FRAMES = 150;
    protected static final int MAX_PARTICLES = 36;

    private int mNumParticles = 0;
    private int mFrame = 0;

    private Texture mSmokeTexture;

    private Runnable mAddParticleRunnable = new Runnable() {

        @Override
        public void run() {
            // use pool for better perf
            GunshotSmokeParticle particle = ParticleAdapter.GUNSHOT_SMOKE_PARTICLES.acquire();
            if (particle == null) {
                particle = new GunshotSmokeParticle(mSmokeTexture);
            } else {
                particle.setTexture(mSmokeTexture);
                particle.reset();
            }

            particle.setPosition(mPosition.x + mRandom.nextInt(101) - 50, mPosition.y + mRandom.nextInt(101) - 50);
            addParticle(particle);
        }
    };

    public Gunshots() {
        mSmokeTexture = ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture;
        setRemoveOnFinish(true);
    }

    public Gunshots(final Texture smoke) {
        mSmokeTexture = smoke;
        setRemoveOnFinish(true);
    }

    public void reset() {
        mNumParticles = 0;
        mFrame = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;
        if (mFrame % (ParticleAdapter.FRAME_THROTTLE ? 4 : 2) == 0 && mNumParticles < MAX_PARTICLES) {
            queueEvent(mAddParticleRunnable);
            mNumParticles++;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        // the 1 spot particle
        addParticle(new GunshotSpotParticle(mSmokeTexture));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#removeParticle(com.funzio.pure2D.particles.Particle)
     */
    @Override
    protected boolean removeParticle(final Particle particle) {
        if (super.removeParticle(particle)) {
            // recycle
            if (particle instanceof GunshotSmokeParticle) {
                ParticleAdapter.GUNSHOT_SMOKE_PARTICLES.release((GunshotSmokeParticle) particle);
            }

            return true;
        }

        return false;
    }
}
