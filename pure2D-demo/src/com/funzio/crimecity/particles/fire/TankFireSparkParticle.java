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
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

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
