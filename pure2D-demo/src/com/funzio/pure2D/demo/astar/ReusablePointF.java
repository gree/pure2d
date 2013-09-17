/**
 * 
 */
package com.funzio.pure2D.demo.astar;

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.utils.Reusable;

/**
 * @author long.ngo
 */
public class ReusablePointF extends PointF implements Reusable {

    public ReusablePointF() {
    }

    public ReusablePointF(final Point p) {
        super(p);
    }

    public ReusablePointF(final float x, final float y) {
        super(x, y);
    }

    @Override
    public void reset(final Object... params) {
        if (params.length >= 2) {
            x = (Integer) params[0];
            y = (Integer) params[1];
        } else {
            x = y = 0;
        }

    }

}
