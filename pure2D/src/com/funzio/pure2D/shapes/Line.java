package com.funzio.pure2D.shapes;

import android.graphics.PointF;
import android.util.FloatMath;

/**
 * @author long
 */
public class Line extends Rectangular {
    private float mThickness = 1;

    public Line() {
        super();
    }

    /**
     * @return the thickness
     */
    public float getThickness() {
        return mThickness;
    }

    /**
     * @param thickness the thickness to set
     */
    public void setThickness(final float thickness) {
        mThickness = thickness;
        setOrigin(new PointF(thickness / 2, thickness / 2));
        setSize(mSize.x + mThickness, mThickness);
    }

    public void setPoints(final PointF p1, final PointF p2) {
        PointF delta = new PointF(p2.x - p1.x, p2.y - p1.y);
        float len = FloatMath.sqrt(delta.x * delta.x + delta.y * delta.y);
        float degree = (float) (Math.atan2(delta.y, delta.x) * 180 / Math.PI);
        setPosition(p1.x, p1.y);
        setRotation(degree);
        setSize(len, mThickness);
    }
}
