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

/**
 * @author long
 */
public class Bezier {
    public static PointF getCubicBezierPoint(final float t, final PointF start, final PointF c1, final PointF c2, final PointF end) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        PointF p = new PointF(start.x * uuu, start.y * uuu);
        p.x += 3 * uu * t * c1.x;
        p.y += 3 * uu * t * c1.y;
        p.x += 3 * u * tt * c2.x;
        p.y += 3 * u * tt * c2.y;
        p.x += ttt * end.x;
        p.y += ttt * end.y;

        return p;
    }

    public static void getCubicBezierPoint(final float t, final PointF start, final PointF c1, final PointF c2, final PointF end, final float[] result) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        float rx = start.x * uuu;
        float ry = start.y * uuu;
        rx += 3 * uu * t * c1.x;
        ry += 3 * uu * t * c1.y;
        rx += 3 * u * tt * c2.x;
        ry += 3 * u * tt * c2.y;
        rx += ttt * end.x;
        ry += ttt * end.y;

        result[0] = rx;
        result[1] = ry;
    }

    public static void getCubicBezierPoint(final float t, final PointF start, final PointF c1, final PointF c2, final PointF end, final PointF result) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        float rx = start.x * uuu;
        float ry = start.y * uuu;
        rx += 3 * uu * t * c1.x;
        ry += 3 * uu * t * c1.y;
        rx += 3 * u * tt * c2.x;
        ry += 3 * u * tt * c2.y;
        rx += ttt * end.x;
        ry += ttt * end.y;

        result.x = rx;
        result.y = ry;
    }
}
