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

/**
 * @author long
 */
public class ExplosionFireParticle extends ExplosionParticle {

    public ExplosionFireParticle(final Texture texture) {
        super(texture);

        setBlendFunc(ParticleAdapter.BF_ADD);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.ExplosionParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mLifeStage == 0) {
            // nothing
        } else if (mLifeStage == 1) {
            mDeltaScale.x *= .65;
            mDeltaScale.y *= .65;
        } else if (mLifeStage == 2) {
            if (mScale.x <= 0) {
                mVisible = false;
            } else {
                mDeltaScale.x -= .07;
                mDeltaScale.y -= .07;
            }
        }

        return super.update(deltaTime);
    }
}
