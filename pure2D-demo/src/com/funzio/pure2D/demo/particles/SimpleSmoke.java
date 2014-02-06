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

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.RectangularEmitter;

/**
 * @author long
 */
public class SimpleSmoke extends RectangularEmitter {
    private int mInitParticles = 0;
    private int mRetainParticles = 0;
    private Texture mParticleTexture;
    private boolean mParticleTextureEnabled = false;

    private Runnable mInitRunnable = new Runnable() {

        @Override
        public void run() {
            for (int n = 0; n < mInitParticles; n++) {
                createParticle();
            }
        }
    };

    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            final int more = Math.min(mRetainParticles - getNumParticles(), 2);
            for (int n = 0; n < more; n++) {
                createParticle();
            }
        }
    };

    /**
     * 
     */
    public SimpleSmoke(final int initParticles, final int retainParticles) {
        mInitParticles = initParticles;
        mRetainParticles = retainParticles;
        setSize(50, 50);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        queueEvent(mUpdateRunnable);

        return true;
    }

    private void createParticle() {
        SimpleSmokeParticle particle = SimpleSmokeParticle.newInstance();
        particle.setTexture(mParticleTextureEnabled ? mParticleTexture : null);

        particle.setSize(40, 40);
        particle.setAlpha(0.3f);
        // random position
        getNextPosition(particle.getPosition());
        // random velocity
        particle.setVelocity(new PointF(mRandom.nextInt(7) - 3, 5 + mRandom.nextInt(10)));

        // add to scene
        addParticle(particle);
    }

    /**
     * @return the particleTexture
     */
    public Texture getParticleTexture() {
        return mParticleTexture;
    }

    /**
     * @param particleTexture the particleTexture to set
     */
    public void setParticleTexture(final Texture particleTexture) {
        mParticleTexture = particleTexture;
    }

    /**
     * @return the useTexture
     */
    public boolean isParticleTextureEnabled() {
        return mParticleTextureEnabled;
    }

    /**
     * @param useTexture the useTexture to set
     */
    public void setParticleTextureEnabled(final boolean useTexture) {
        mParticleTextureEnabled = useTexture;
    }

    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        queueEvent(mInitRunnable);
    };

}
