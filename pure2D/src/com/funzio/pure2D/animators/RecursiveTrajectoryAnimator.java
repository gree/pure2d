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
package com.funzio.pure2D.animators;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class RecursiveTrajectoryAnimator extends TrajectoryAnimator {

    protected float mDecelerationRate = 0.75f;
    protected float mMinVelocity = 1;

    public RecursiveTrajectoryAnimator() {
        super();
    }

    /**
     * @param ground
     */
    public RecursiveTrajectoryAnimator(final float ground) {
        super(ground);
    }

    @Override
    public void end() {
        final float newVelocity = mVelocity * mDecelerationRate;
        if (Math.abs(newVelocity) >= mMinVelocity) {
            stop();

            // and restart
            if (mAxisSystem == Scene.AXIS_BOTTOM_LEFT) {
                start(newVelocity, mSin < 0.0f ? -mAngle : mAngle);
            } else {
                start(newVelocity, mSin > 0.0f ? -mAngle : mAngle);
            }
        } else {
            super.end();
        }
    }

    public float getDecelerationRate() {
        return mDecelerationRate;
    }

    public void setDecelerationRate(final float decelerationRate) {
        mDecelerationRate = decelerationRate;
    }

    public float getMinVelocity() {
        return mMinVelocity;
    }

    public void setMinVelocity(final float minVelocity) {
        mMinVelocity = minVelocity;
    }

}
