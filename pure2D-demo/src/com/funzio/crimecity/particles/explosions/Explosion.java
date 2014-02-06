/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
