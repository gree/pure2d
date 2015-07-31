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
package com.funzio.pure2D.geom;

import android.graphics.PointF;
import android.util.FloatMath;

/**
 * @author long
 */
public class Point3D extends PointF {

    public float z;

    public Point3D() {
        super();
    }

    public Point3D(final Point3D p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }

    public Point3D(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(final Point3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public void negate3() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void offset(final float dx, final float dy, final float dz) {
        x += dx;
        y += dy;
        z += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y,z)
     */
    public boolean equals(final float x, final float y, final float z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean equals(final Point3D p) {
        return this.x == p.x && this.y == p.y && this.z == p.z;
    }

    /**
     * Return the euclidian distance from (0,0,0) to the point
     */
    public float length3() {
        return length(x, y, z);
    }

    /**
     * Returns the euclidian distance from (0,0,0) to (x,y,z)
     */
    public static float length(final float x, final float y, final float z) {
        return FloatMath.sqrt(x * x + y * y + z * z);
    }

}
