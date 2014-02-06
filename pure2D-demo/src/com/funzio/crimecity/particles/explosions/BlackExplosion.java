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
