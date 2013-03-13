/**
 * 
 */
package com.funzio.pure2D.demo.objects;

import java.util.Random;

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class SquareContainer extends DisplayGroup {

    private Random mRandom = new Random();
    private PointF mBounds;
    private Point mVeloc;

    private Rectangular mSquare = new Rectangular();

    public SquareContainer() {
        mSquare.setColor(new GLColor(1, 1, 1, 0.5f));
        addChild(mSquare);

        mVeloc = new Point(1 + mRandom.nextInt(1), 1 + mRandom.nextInt(1));
        mVeloc.x *= Math.random() > 0.5 ? 1 : -1;
        mVeloc.y *= Math.random() > 0.5 ? 1 : -1;
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        mSquare.setSize(w, h);
        setOrigin(new PointF(w / 2, h / 2));
    };

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        rotateBy(1);
        // setScale(2);

        if (mPosition.x > mBounds.x) {
            mPosition.x = mBounds.x;
            mVeloc.x = -Math.abs(mVeloc.x);
        } else if (mPosition.x <= 0) {
            mPosition.x = 0;
            mVeloc.x = Math.abs(mVeloc.x);
        }

        if (mPosition.y > mBounds.y) {
            mPosition.y = mBounds.y;
            mVeloc.y = -Math.abs(mVeloc.y);
        } else if (mPosition.y <= 0) {
            mPosition.y = 0;
            mVeloc.y = Math.abs(mVeloc.y);
        }

        float factor = (float) deltaTime / 10;
        moveBy(mVeloc.x * factor, mVeloc.y * factor);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#onAdded(com.funzio.pure2D.IContainer)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        if (mParent != null) {
            mBounds = mParent.getSize();
        }
    }

}
