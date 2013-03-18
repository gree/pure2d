/**
 * 
 */
package com.funzio.crimecity.particles.explosions;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.Particle;

/**
 * @author long
 */
public class BlackExplosion extends Explosion {

    public BlackExplosion(final Texture fire) {
        super(fire, null);
    }

    @Override
    protected Particle createParticle(final Class<? extends ExplosionParticle> type) {

        ExplosionParticle particle = null;
        if (type == ExplosionSmokeParticle.class) {
            particle = new BlackExplosionSmokeParticle(mFireTexture);
            particle.setPosition(mPosition.x + RANDOM.nextInt(7) - 3, mPosition.y + RANDOM.nextInt(7) - 3);
        } else if (type == ExplosionFireParticle.class) {
            particle = new ExplosionFireParticle(mFireTexture);
            particle.setPosition(mPosition.x + RANDOM.nextInt(7) - 3, mPosition.y + RANDOM.nextInt(7) - 3);
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
}
