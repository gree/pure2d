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
package com.funzio.pure2D.effects.sparks;

import java.util.Random;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.animators.TweenAnimator;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public class SparkGroup extends DisplayGroup {
    public static final int STYLE_RANDOM = 0;
    public static final int STYLE_FAN = 1;
    public static final int STYLE_FAN_REVERSED = 2;

    protected static final Random RANDOM = new Random();

    protected Class<? extends Sparkable> mSparkClass;
    protected int mNumSparks = 0;
    protected float mRadius1;
    protected float mRadius2;
    protected float mWeight1;
    protected float mWeight2;

    protected int mAnimationStyle = STYLE_RANDOM;
    protected int mAnimationDuration = 2000;
    protected GLColor[] mColors;

    public SparkGroup(final Class<? extends Sparkable> sparkClass, final int numSparks, final float r1, final float r2, final float weight1, final float weight2, final GLColor... colors) {
        super();

        mSparkClass = sparkClass;

        mRadius1 = r1;
        mRadius2 = r2;
        mWeight1 = weight1;
        mWeight2 = weight2;

        mColors = colors;

        setNumSparks(numSparks);
    }

    /**
     * @param numSparks
     */
    public void setNumSparks(final int numSparks) {
        final int diff = numSparks - mNumSparks;
        if (diff == 0) {
            return;
        } else if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                try {
                    final Sparkable spark = mSparkClass.newInstance();
                    spark.setColors(mColors);
                    spark.setSize(mRadius1 + RANDOM.nextFloat() * (mRadius2 - mRadius1), mWeight1 + RANDOM.nextFloat() * (mWeight2 - mWeight1));
                    addChild(spark);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        } else {
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i > diff; i--) {
                        removeChild(0);
                    }
                }
            });
        }

        mNumSparks = numSparks;

        // refresh style
        setAnimationStyle(mAnimationStyle);
    }

    public void setColors(final GLColor... colors) {
        for (int i = 0; i < mNumChildren; i++) {
            ((Sparkable) getChildAt(i)).setColors(colors);
        }
    }

    public void setAnimationStyle(final int style) {

        mAnimationStyle = style;

        // queue for thread-safe
        queueEvent(new Runnable() {
            @Override
            public void run() {
                // clear animator
                removeAllManipulators();

                if (style == STYLE_RANDOM) {
                    for (int i = 0; i < mNumChildren; i++) {
                        final Sparkable spark = ((Sparkable) getChildAt(i));
                        // clear animator
                        spark.removeAllManipulators();

                        // initial angle
                        final float degree = RANDOM.nextInt(360);
                        spark.setRotation(degree);

                        // individual animator
                        final RotateAnimator rotateAnimator = new RotateAnimator(null);
                        rotateAnimator.setLoop(Playable.LOOP_REPEAT);
                        rotateAnimator.setDuration(mAnimationDuration + RANDOM.nextInt(mAnimationDuration));
                        rotateAnimator.setValues(degree, degree + (RANDOM.nextInt(2) == 0 ? -359 : 359));
                        spark.addManipulator(rotateAnimator);
                        rotateAnimator.start();
                    }
                } else if (style == STYLE_FAN || style == STYLE_FAN_REVERSED) {
                    final float rotationStep = 360 / (float) mNumChildren;
                    // reposition children
                    for (int i = 0; i < mNumChildren; i++) {
                        final Sparkable spark = ((Sparkable) getChildAt(i));
                        // clear animator
                        spark.removeAllManipulators();

                        // initial angle
                        spark.setRotation(i * rotationStep);
                    }

                    // global animator
                    final RotateAnimator rotateAnimator = new RotateAnimator(null);
                    rotateAnimator.setLoop(Playable.LOOP_REPEAT);
                    rotateAnimator.setDuration(mAnimationDuration);
                    if (style == STYLE_FAN) {
                        rotateAnimator.setValues(0, 359);
                    } else {
                        rotateAnimator.setValues(0, -359);
                    }
                    addManipulator(rotateAnimator);
                    rotateAnimator.start();
                }
            }
        });

    }

    public void setAnimationDuration(final int duration) {
        // diff check
        if (mAnimationDuration == duration) {
            return;
        }

        mAnimationDuration = duration;

        if (mAnimationStyle == STYLE_RANDOM) {
            for (int i = 0; i < mNumChildren; i++) {
                final Sparkable spark = ((Sparkable) getChildAt(i));
                final Manipulator manipulator = spark.getManipulator(0);
                // update individual animator
                if (manipulator != null && manipulator instanceof TweenAnimator) {
                    ((TweenAnimator) manipulator).setDuration(duration);
                }
            }
        } else if (mAnimationStyle == STYLE_FAN || mAnimationStyle == STYLE_FAN_REVERSED) {
            // update global animator
            final Manipulator manipulator = getManipulator(0);
            if (manipulator != null && manipulator instanceof TweenAnimator) {
                ((TweenAnimator) manipulator).setDuration(duration);
            }
        }
    }
}
