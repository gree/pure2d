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
