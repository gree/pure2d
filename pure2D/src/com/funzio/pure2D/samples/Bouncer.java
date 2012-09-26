/**
 * 
 */
package com.funzio.pure2D.samples;

import java.util.Random;

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class Bouncer extends Sprite {

    private Random mRandom = new Random();
    private PointF mBoundary;
    private Point mVeloc;

    public Bouncer() {
        mVeloc = new Point(1 + mRandom.nextInt(3), 1 + mRandom.nextInt(3));
        mVeloc.x *= Math.random() > 0.5 ? 1 : -1;
        mVeloc.y *= Math.random() > 0.5 ? 1 : -1;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        if (mPosition.x >= mBoundary.x - mSize.x) {
            mPosition.x = mBoundary.x - mSize.x;
            mVeloc.x = -Math.abs(mVeloc.x);
        } else if (mPosition.x <= 0) {
            mPosition.x = 0;
            mVeloc.x = Math.abs(mVeloc.x);
        }

        if (mPosition.y >= mBoundary.y - mSize.y) {
            mPosition.y = mBoundary.y - mSize.y;
            mVeloc.y = -Math.abs(mVeloc.y);
        } else if (mPosition.y <= 0) {
            mPosition.y = 0;
            mVeloc.y = Math.abs(mVeloc.y);
        }

        final float factor = (float) deltaTime / 10;
        moveBy(mVeloc.x * factor, mVeloc.y * factor);
        // rotateBy(1);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#onAdded(com.funzio.pure2D.IContainer)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        if (mParent != null && mBoundary == null) {
            mBoundary = mParent.getSize();
        }
    }

    public void setBoundary(final float x, final float y) {
        mBoundary = new PointF(x, y);
    }
}
