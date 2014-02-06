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

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.particles.RectangularEmitter;

/**
 * @author long
 */
public class DynamicEmitter extends RectangularEmitter {
    private static final BlendFunc BLEND_FUNC_1 = BlendFunc.getAdd();
    // private static final BlendFunc BLEND_FUNC_2 = new BlendFunc(GL10.GL_SRC_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA);

    private Texture mFireTexture;
    private Texture mSmokeTexture;
    private int mType = 0;
    private PointF mDestination = new PointF();
    private PointF mVelocity = new PointF();
    private PointF mDelta = new PointF();
    private Point mBounds = new Point();

    private boolean mLockDestination = false;

    /**
     * 
     */
    public DynamicEmitter(final Point bounds, final Texture fire, final Texture smoke) {
        setRemoveOnFinish(true);
        mFireTexture = fire;
        mSmokeTexture = smoke;
        mBounds.x = bounds.x / 2;
        mBounds.y = bounds.y / 2;
        setPosition(mRandom.nextInt(mBounds.x), mRandom.nextInt(mBounds.y));
    }

    private void addParticles() {
        final float x = mPosition.x;
        final float y = mPosition.y;

        Particle1 particle = null;
        if (mType <= 1) {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            particle.setColor(Particle1.RED);
            particle.start1(x, y);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);
        } else if (mType == 2) {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            particle.setColor(Particle1.GREEN);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);
        } else {
            particle = Particle1.create();
            particle.setTexture(mSmokeTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            particle.setSize(Particle1.SIZE_BIG, Particle1.SIZE_BIG);
            particle.setColor(Particle1.BLUE);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);

            particle = Particle1.create();
            particle.setTexture(mFireTexture);
            particle.setBlendFunc(BLEND_FUNC_1);
            particle.start1(x, y);
            addParticle(particle);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.RectangularEmitter#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // update position
        mPosition.x += mVelocity.x / 5;
        mPosition.y += mVelocity.y / 5;

        // steering
        mDelta.x = mDestination.x - mPosition.x;
        mDelta.y = mDestination.y - mPosition.y;
        mVelocity.x += (mDelta.x - mVelocity.x) / 200;
        mVelocity.y += (mDelta.y - mVelocity.y) / 200;

        invalidate();

        // hit destination? auto changes
        mDelta.x = mDestination.x - mPosition.x;
        mDelta.y = mDestination.y - mPosition.y;
        if (Math.abs(mDelta.x) < 20 || Math.abs(mDelta.y) < 20) {
            if (!mLockDestination) {
                nextDestination();
            }
        }

        // emitting
        addParticles();

        return super.update(deltaTime);
    }

    /**
     * @return the destination
     */
    public PointF getDestination() {
        return mDestination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(final float x, final float y) {
        mDestination.x = x;
        mDestination.y = y;
    }

    private void nextDestination() {
        mDestination.x = mBounds.x / 2 + mRandom.nextInt(mBounds.x);
        mDestination.y = mBounds.y / 2 + mRandom.nextInt(mBounds.y);
    }

    public void lockDestination(final boolean lock) {
        mLockDestination = lock;
    }

    /**
     * @return the type
     */
    public int getType() {
        return mType;
    }

    /**
     * @param type the type to set
     */
    public void setType(final int type) {
        mType = type;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        addParticles();
        nextDestination();
    }
}
