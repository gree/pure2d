/**
 * 
 */
package com.funzio.pure2D.geom;

import android.graphics.PointF;

/**
 * @author long
 */
public class Line {

    /**
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return the intersection between 2 points.
     */
    public static PointF getLinesIntersection(final PointF p1, final PointF p2, final PointF p3, final PointF p4) {
        final float d = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x);
        if (d == 0) {
            return new PointF(p2.x, p2.y);
        } else {
            float x = ((p3.x - p4.x) * (p1.x * p2.y - p1.y * p2.x) - (p1.x - p2.x) * (p3.x * p4.y - p3.y * p4.x)) / d;
            float y = ((p3.y - p4.y) * (p1.x * p2.y - p1.y * p2.x) - (p1.y - p2.y) * (p3.x * p4.y - p3.y * p4.x)) / d;
            return new PointF(x, y);
        }
    }

    /**
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param result is the intersection between 2 points. This is more optimized because it doesn't allocate new PointF.
     */
    public static void getLinesIntersection(final PointF p1, final PointF p2, final PointF p3, final PointF p4, final PointF result) {
        final float d = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x);
        if (d == 0) {
            result.x = p2.x;
            result.y = p2.y;
        } else {
            result.x = ((p3.x - p4.x) * (p1.x * p2.y - p1.y * p2.x) - (p1.x - p2.x) * (p3.x * p4.y - p3.y * p4.x)) / d;
            result.y = ((p3.y - p4.y) * (p1.x * p2.y - p1.y * p2.x) - (p1.y - p2.y) * (p3.x * p4.y - p3.y * p4.x)) / d;
        }
    }

    /**
     * Find the parallel line that is shifted "delta" units from the given line
     * 
     * @param p1
     * @param p2
     * @param delta
     * @return
     */
    public static PointF shiftLine(final PointF p1, final PointF p2, final float delta) {
        final float angle = (float) Math.atan2(p2.y - p1.y, p2.x - p1.x);
        final float deltaX = -delta * (float) Math.sin(angle);
        final float deltaY = delta * (float) Math.cos(angle);

        p1.x += deltaX;
        p1.y += deltaY;
        p2.x += deltaX;
        p2.y += deltaY;

        // return the delta
        return new PointF(deltaX, deltaY);
    }

    /**
     * Find the parallel line that is shifted "delta" units from the given line and angle
     * 
     * @param p1
     * @param p2
     * @param angle
     * @param delta
     * @return
     */
    public static PointF shiftLine(final PointF p1, final PointF p2, final float delta, final float angle) {
        final float deltaX = -delta * (float) Math.sin(angle);
        final float deltaY = delta * (float) Math.cos(angle);

        p1.x += deltaX;
        p1.y += deltaY;
        p2.x += deltaX;
        p2.y += deltaY;

        // return the delta
        return new PointF(deltaX, deltaY);
    }
}
