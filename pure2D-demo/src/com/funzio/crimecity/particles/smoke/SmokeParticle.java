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

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;

/**
 * @author long
 */
public class SmokeParticle extends SpriteParticle {
    private static final int SIZE = 16;
    private static final BlendFunc BLEND_FUNC = new BlendFunc(GL10.GL_ZERO, GL10.GL_ONE_MINUS_SRC_ALPHA);

    // deltas
    private float mDeltaRotation;
    private PointF mDeltaScale = new PointF();
    private float mDeltaAlpha;

    public SmokeParticle(final Texture texture) {
        super();
        setTexture(texture);
        reset();
        setBlendFunc(BLEND_FUNC);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        // initial state
        setSize(SIZE, SIZE);
        mAlpha = .7f;
        mPosition.x = 0;
        mPosition.y = 0;
        mRotation = mRandom.nextInt(360);
        mScale.x = mScale.y = 1 + mRandom.nextFloat();

        // deltas
        mVelocity.x = mRandom.nextInt(2) + 1;
        mVelocity.y = mRandom.nextInt(3) + 1;
        mDeltaRotation = mRandom.nextInt(5) - 2;
        mDeltaScale.x = mDeltaScale.y = .09f + mRandom.nextInt(8) / 100;
        mDeltaAlpha = -.01f;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // apply the deltas
        mRotation += mDeltaRotation;
        mScale.x += mDeltaScale.x;
        mScale.y += mDeltaScale.y;
        mPosition.x += mVelocity.x;
        mPosition.y += mVelocity.y;
        mAlpha += mDeltaAlpha;
        invalidate();

        if (mAlpha <= 0) {
            finish();
        }

        return true;
    }
}
