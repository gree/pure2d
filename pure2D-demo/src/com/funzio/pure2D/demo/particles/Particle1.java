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
package com.funzio.pure2D.demo.particles;

import android.graphics.PointF;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.particles.SpriteParticle;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class Particle1 extends SpriteParticle {
    public static final ObjectPool<Particle1> POOL = new ObjectPool<Particle1>(1000);

    public static final GLColor RED = new GLColor(1, 0, 0, 1f);
    public static final GLColor GREEN = new GLColor(0, 1, 0, 1f);
    public static final GLColor BLUE = new GLColor(0, 0, 1, 1f);
    public static final int SIZE = 128;
    public static final int SIZE_BIG = (int) (SIZE * 1.5);

    private float mRotationDelta;
    private float mAlphaMultiplier;
    private PointF mScaleMultiplier = new PointF();
    private PointF mAcceleration = new PointF();
    private boolean mLeft = false;

    public static Particle1 create() {
        Particle1 particle = POOL.acquire();
        if (particle == null) {
            particle = new Particle1();
        } else {
            particle.reset();
        }

        return particle;
    }

    public static void recycle(final Particle1 particle) {
        POOL.release(particle);
    }

    public static void clearPool() {
        POOL.clear();
    }

    public Particle1() {
        super();
        reset();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
        super.reset(params);

        // initial state
        mBlendFunc = null;
        mSize.x = mSize.y = SIZE;
        mColor = null;
        mLeft = false;
        mAlpha = 1;
        mRotation = 0;
        mScale.x = mScale.y = 1;
        mVelocity.x = mVelocity.y = 0;
        mAcceleration.x = mAcceleration.y = 0;
        mVisible = true;
    }

    public void start1(final float x, final float y) {
        mPosition.x = x + mRandom.nextInt(10) - 5;
        mPosition.y = y + mRandom.nextInt(10) - 5;
        mRotation = mRandom.nextInt(360);

        mSize.x += mRandom.nextInt(64) - 32;
        mSize.y += mRandom.nextInt(64) - 32;

        // deltas
        mVelocity.x = mRandom.nextInt(6) - 3;
        mVelocity.y = mRandom.nextInt(10) - 5;
        mRotationDelta = mRandom.nextInt(10) - 5;
        mAlphaMultiplier = 0.95f;
        mScaleMultiplier.x = mScaleMultiplier.y = 0.95f;
        mAcceleration.x = 0.5f;
        mAcceleration.y = mVelocity.y > 0 ? 0.1f : 0.2f;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // apply the deltas
        mAlpha *= mAlphaMultiplier;
        mScale.x *= mScaleMultiplier.x;
        mScale.y *= mScaleMultiplier.y;

        mPosition.x += mVelocity.x;
        mPosition.y += mVelocity.y;
        mRotation += mRotationDelta;

        if (mLeft && mVelocity.x <= -2) {
            mLeft = false;
            mAcceleration.x = mScale.y;
        } else if (!mLeft && mVelocity.x >= 2) {
            mLeft = true;
            mAcceleration.x = -mScale.y;
        }
        mVelocity.x += mAcceleration.x;
        mVelocity.y += mAcceleration.y;

        invalidate();

        if (mAlpha < 0.08) {
            finish();
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.SpriteParticle#finish()
     */
    @Override
    public void finish() {
        super.finish();

        POOL.release(this);
    }
}
