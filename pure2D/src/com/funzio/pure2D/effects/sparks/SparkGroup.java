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
                final float rotationStep = 360 / (float) mNumChildren;
                for (int i = 0; i < mNumChildren; i++) {
                    final Sparkable spark = ((Sparkable) getChildAt(i));
                    spark.removeAllManipulators();

                    if (style == STYLE_RANDOM) {
                        // initial angle
                        final float degree = RANDOM.nextInt(360);
                        spark.setRotation(degree);

                        // animators
                        final RotateAnimator rotateAnimator = new RotateAnimator(null);
                        rotateAnimator.setLoop(Playable.LOOP_REPEAT);
                        rotateAnimator.setDuration(mAnimationDuration + RANDOM.nextInt(mAnimationDuration));
                        rotateAnimator.setValues(degree, degree + (RANDOM.nextInt(2) == 0 ? -359 : 359));
                        spark.addManipulator(rotateAnimator);
                        rotateAnimator.start();
                    } else if (style == STYLE_FAN) {
                        // initial angle
                        final float degree = i * rotationStep;
                        spark.setRotation(degree);

                        // animators
                        final RotateAnimator rotateAnimator = new RotateAnimator(null);
                        rotateAnimator.setLoop(Playable.LOOP_REPEAT);
                        rotateAnimator.setDuration(mAnimationDuration);
                        rotateAnimator.setValues(degree, degree + 359);
                        spark.addManipulator(rotateAnimator);
                        rotateAnimator.start();
                    }
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
        for (int i = 0; i < mNumChildren; i++) {
            final Sparkable spark = ((Sparkable) getChildAt(i));
            final Manipulator manipulator = spark.getManipulator(0);
            if (manipulator != null && manipulator instanceof TweenAnimator) {
                ((TweenAnimator) manipulator).setDuration(duration);
            }
        }
    }
}
