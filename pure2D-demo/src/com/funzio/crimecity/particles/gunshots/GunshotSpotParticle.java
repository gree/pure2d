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
package com.funzio.crimecity.particles.gunshots;

import android.graphics.PointF;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class GunshotSpotParticle extends SpriteParticle {
    private static final int SIZE = 8;
    private static final int TOTAL_FRAMES = 75;

    private int mFrame = 0;

    public GunshotSpotParticle(final Texture texture) {
        super();

        setTexture(texture);
        setSize(SIZE, SIZE);
        setBlendFunc(ParticleAdapter.BF_ADD);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;
        if (mFrame < TOTAL_FRAMES) {
            if (mEmitter != null) {
                // randomly jump
                final PointF start = mEmitter.getPosition();
                setPosition(start.x + mRandom.nextInt(101) - 50, start.y + mRandom.nextInt(101) - 50);
            }
        } else {
            finish();
        }

        return true;
    }
}
