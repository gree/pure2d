/**
 * 
 */
package com.funzio.pure2D.demo.objects;

import java.util.Random;

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.uni.UniContainer;
import com.funzio.pure2D.uni.UniSprite;

/**
 * @author long
 */
public class UniBouncer extends UniSprite {

    private Random mRandom = new Random();
    private PointF mBoundary;
    private Point mVeloc;

    public UniBouncer() {
        mVeloc = new Point(1 + mRandom.nextInt(5), 1 + mRandom.nextInt(5));
        mVeloc.x *= mRandom.nextFloat() > 0.5 ? 1 : -1;
        mVeloc.y *= mRandom.nextFloat() > 0.5 ? 1 : -1;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mPosition.x >= mBoundary.x - mSize.x) {
            // mPosition.x = mBoundary.x - mSize.x;
            mVeloc.x = -Math.abs(mVeloc.x);
        } else if (mPosition.x <= 0) {
            // mPosition.x = 0;
            mVeloc.x = Math.abs(mVeloc.x);
        }

        if (mPosition.y >= mBoundary.y - mSize.y) {
            // mPosition.y = mBoundary.y - mSize.y;
            mVeloc.y = -Math.abs(mVeloc.y);
        } else if (mPosition.y <= 0) {
            // mPosition.y = 0;
            mVeloc.y = Math.abs(mVeloc.y);
        }

        final float factor = (float) deltaTime / 30;
        move(mVeloc.x * factor, mVeloc.y * factor);
        // rotate(1);

        return super.update(deltaTime);
    }

    @Override
    public void onAdded(final UniContainer parent) {
        super.onAdded(parent);

        if (mParent != null && mBoundary == null) {
            mBoundary = mParent.getSize();
        }
    }

    public void setBoundary(final float x, final float y) {
        mBoundary = new PointF(x, y);
    }
}
