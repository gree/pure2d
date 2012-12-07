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
}
