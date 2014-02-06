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

import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.SpriteParticle;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class SimpleSmokeParticle extends SpriteParticle {
    private static final BlendFunc BLEND_FUNC = BlendFunc.getScreen();
    private static final ObjectPool<SimpleSmokeParticle> POOL = new ObjectPool<SimpleSmokeParticle>(1000);

    public static SimpleSmokeParticle newInstance() {
        SimpleSmokeParticle particle = POOL.acquire();
        if (particle != null) {
            particle.reset();
        } else {
            particle = new SimpleSmokeParticle();
        }

        return particle;
    }

    public static void clearPool() {
        POOL.clear();
    }

    private SimpleSmokeParticle() {
        super();
        setBlendFunc(BLEND_FUNC);
    }

    public SimpleSmokeParticle(final Texture texture) {
        super();
        setTexture(texture);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.ClipParticle#reset(java.lang.Object[])
     */
    @Override
    public void reset(final Object... params) {
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
        if (mAlpha < 0.1) {
            finish();
        } else {
            mVelocity.x *= 0.95;
            mVelocity.y *= 0.95;
            mScale.x *= 1.02;
            mScale.y *= 1.02;
            mAlpha *= 0.98;
            mRotation += (mVelocity.x > 0) ? -5 : 5;
        }

        return super.update(deltaTime);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onRemoved()
     */
    @Override
    public void onRemoved() {
        super.onRemoved();

        // recycle
        POOL.release(this);
    }

}
