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
package com.funzio.crimecity.particles;

import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class SimpleSmokeParticle extends SpriteParticle {

    public SimpleSmokeParticle() {
        super();
    }

    public SimpleSmokeParticle(final Texture texture) {
        super();
        setTexture(texture);
    }

    public void reset() {
        mAlpha = 1;
        mRotation = 0;
        mScale.x = mScale.y = 1;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        if (mAlpha < 0.1) {
            finish();
        } else {
            mVelocity.x *= 0.95;
            mVelocity.y *= 0.95;
            mPosition.x += mVelocity.x;
            mPosition.y += mVelocity.y;
            mScale.x *= 1.02;
            mScale.y *= 1.02;
            mAlpha *= 0.98;
            mRotation += (mVelocity.x > 0) ? -5 : 5;
            invalidate();
        }

        return true;
    }

}
