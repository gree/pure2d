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
package com.funzio.crimecity.particles.smoke;

import android.graphics.PointF;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.explosions.ExplosionSparkParticle;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.HybridEmitter;
import com.funzio.pure2D.particles.Particle;

/**
 * @author long
 */
public class SmokePuff extends HybridEmitter {
    private static final int NUM_FRAMES = 350;

    private int mFrame = 0;

    private Texture mSmokeTexture;

    private int mNumSmokes = 0;
    private Runnable mAddParticleRunnable = new Runnable() {

        @Override
        public void run() {
            // use pool for better perf
            SmokeParticle particle = ParticleAdapter.SMOKE_PARTICLES.acquire();
            if (particle == null) {
                particle = new SmokeParticle(mSmokeTexture);
            } else {
                particle.setTexture(mSmokeTexture);
                particle.reset();
            }

            particle.setPosition(mPosition);
            // particle.setPosition(mPosition.x + mRandom.nextInt(4) - 2, mPosition.y + mRandom.nextInt(4) - 2);
            addParticle(particle);

            // optional, more fun
            if (++mNumSmokes % 3 == 0) {
                // use pool for better perf
                ExplosionSparkParticle spark = ParticleAdapter.EXPLOSION_SPARK_PARTICLES.acquire();
                if (spark == null) {
                    spark = new ExplosionSparkParticle(null);
                } else {
                    spark.reset();
                }
                spark.setPosition(mPosition);
                spark.setVelocity(new PointF(RANDOM.nextInt(4) - 2, RANDOM.nextInt(5) - 1));
                addParticle(spark);
            }
        }
    };

    /**
     * 
     */
    public SmokePuff(final Texture smoke) {
        setRemoveOnFinish(true);
        mSmokeTexture = smoke;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mFrame % (ParticleAdapter.FRAME_THROTTLE ? 20 : 10) == 0 && mFrame < NUM_FRAMES) {
            queueEvent(mAddParticleRunnable);
        }

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#removeParticle(com.funzio.pure2D.particles.Particle)
     */
    @Override
    protected boolean removeParticle(final Particle particle) {
        if (super.removeParticle(particle)) {
            // recycle
            if (particle instanceof SmokeParticle) {
                ParticleAdapter.SMOKE_PARTICLES.release((SmokeParticle) particle);
            } else if (particle instanceof ExplosionSparkParticle) {
                ParticleAdapter.EXPLOSION_SPARK_PARTICLES.release((ExplosionSparkParticle) particle);
            }

            return true;
        }

        return false;
    }
}
