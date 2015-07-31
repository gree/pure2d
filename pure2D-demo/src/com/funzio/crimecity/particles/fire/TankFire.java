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
package com.funzio.crimecity.particles.fire;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.Particle;
import com.funzio.pure2D.particles.RectangularEmitter;

/**
 * @author long
 */
public class TankFire extends RectangularEmitter {
    private static final int NUM_PARTICLES = 30;
    private static final int NUM_SMOKES = NUM_PARTICLES / 2;
    private static final int NUM_FIRES = NUM_PARTICLES / 2;
    private static final int NUM_SPARKS = 7;

    private Texture mFireTexture;
    private Texture mSmokeTexture;

    public TankFire(final Texture fire, final Texture smoke) {
        setRemoveOnFinish(true);
        mFireTexture = fire;
        mSmokeTexture = smoke;
    }

    protected Particle createParticle(final Class<? extends TankFireParticle> type) {

        TankFireParticle particle = null;
        if (type == TankFireSmokeParticle.class) {
            particle = new TankFireSmokeParticle(mSmokeTexture);
            particle.setPosition(mPosition.x + mRandom.nextInt(7) - 3, mPosition.y + mRandom.nextInt(7) - 3);
        } else if (type == TankFireFireParticle.class) {
            particle = new TankFireFireParticle(mFireTexture);
            particle.setPosition(mPosition.x + mRandom.nextInt(7) - 3, mPosition.y + mRandom.nextInt(7) - 3);
        } else if (type == TankFireSparkParticle.class) {
            particle = new TankFireSparkParticle(mFireTexture);
            particle.setPosition(mPosition);
        }

        return particle;
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
            addParticle(createParticle(TankFireSmokeParticle.class));
        }

        // fires later
        for (int i = 0; i < NUM_FIRES; i++) {
            addParticle(createParticle(TankFireFireParticle.class));
        }

        // sparks last
        for (int i = 0; i < NUM_SPARKS; i++) {
            addParticle(createParticle(TankFireSparkParticle.class));
        }
    }
}
