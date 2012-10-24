/**
 * 
 */
package com.funzio.pure2D.effects.sparks;

import java.util.Random;

import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public class SparkGroup extends DisplayGroup {
    protected static final Random RANDOM = new Random();

    protected int mNumSparks = 0;
    protected int mRadius1;
    protected int mRadius2;
    protected int mWeight1;
    protected int mWeight2;
    private Class<? extends Sparkable> mSparkClass;

    public SparkGroup(final Class<? extends Sparkable> sparkClass, final int numSparks, final int r1, final int r2, final int weight1, final int weight2) {
        super();

        mSparkClass = sparkClass;
        mRadius1 = r1;
        mRadius2 = r2;
        mWeight1 = weight1;
        mWeight2 = weight2;
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
                    spark.setSize(mRadius1 + RANDOM.nextInt(mRadius2 - mRadius1 + 1), mWeight1 + RANDOM.nextInt(mWeight2 - mWeight1 + 1));
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
    }

    public void setColors(final GLColor... colors) {
        for (int i = 0; i < mNumChildren; i++) {
            ((Sparkable) getChildAt(i)).setColors(colors);
        }
    }
}
