/**
 * 
 */
package com.funzio.crimecity.particles.explosions;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.HybridEmitter;
import com.funzio.pure2D.particles.Particle;

/**
 * @author long
 */
public class Explosion extends HybridEmitter {
    private static final int NUM_PARTICLES = 30;
    private static final int NUM_SMOKES = NUM_PARTICLES / 2;
    private static final int NUM_FIRES = NUM_PARTICLES / 2;
    private static final int NUM_SPARKS = 7;

    protected Texture mFireTexture;
    protected Texture mSmokeTexture;

    public Explosion(final Texture fire, final Texture smoke) {
        setRemoveOnFinish(true);
        mFireTexture = fire;
        mSmokeTexture = smoke;
    }

    protected Particle createParticle(final Class<? extends ExplosionParticle> type) {

        ExplosionParticle particle = null;
        if (type == ExplosionSmokeParticle.class) {
            particle = new ExplosionSmokeParticle(mSmokeTexture);
            particle.setPosition(mPosition.x + mRandom.nextInt(7) - 3, mPosition.y + mRandom.nextInt(7) - 3);
        } else if (type == ExplosionFireParticle.class) {
            particle = new ExplosionFireParticle(mFireTexture);
            particle.setPosition(mPosition.x + mRandom.nextInt(7) - 3, mPosition.y + mRandom.nextInt(7) - 3);
        } else if (type == ExplosionSparkParticle.class) {
            // use pool for better perf
            ExplosionSparkParticle spark = ParticleAdapter.EXPLOSION_SPARK_PARTICLES.acquire();
            if (spark == null) {
                spark = new ExplosionSparkParticle(null);
            } else {
                spark.reset();
            }
            spark.setPosition(mPosition);
            particle = spark;
        }

        return particle;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#removeParticle(com.funzio.pure2D.particles.Particle)
     */
    @Override
    protected boolean removeParticle(final Particle particle) {
        if (super.removeParticle(particle)) {
            // recycle
            if (particle instanceof ExplosionSparkParticle) {
                ParticleAdapter.EXPLOSION_SPARK_PARTICLES.release((ExplosionSparkParticle) particle);
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        // smokes first
        for (int i = 0; i < NUM_SMOKES; i++) {
            addParticle(createParticle(ExplosionSmokeParticle.class));
        }

        // fires later
        for (int i = 0; i < NUM_FIRES; i++) {
            addParticle(createParticle(ExplosionFireParticle.class));
        }

        // sparks last
        for (int i = 0; i < NUM_SPARKS; i++) {
            addParticle(createParticle(ExplosionSparkParticle.class));
        }
    }
}
