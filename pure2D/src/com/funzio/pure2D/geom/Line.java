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
public class Line {

    /**
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return the intersection between 2 lines.
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
     * @param result is the intersection between 2 lines. This is more optimized because it doesn't allocate new PointF.
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
     * Detect if 2 line segments intersect. Copied from java.awt.geom.Line2D
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     * @return
     */
    public static boolean linesIntersect(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4) {
        // Return false if either of the lines have zero length
        if (x1 == x2 && y1 == y2 || x3 == x4 && y3 == y4) {
            return false;
        }

        // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
        float ax = x2 - x1;
        float ay = y2 - y1;
        float bx = x3 - x4;
        float by = y3 - y4;
        float cx = x1 - x3;
        float cy = y1 - y3;

        float alphaNumerator = by * cx - bx * cy;
        float commonDenominator = ay * bx - ax * by;
        if (commonDenominator > 0) {
            if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                return false;
            }
        }
        float betaNumerator = ax * cy - ay * cx;
        if (commonDenominator > 0) {
            if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                return false;
            }
        }

        if (commonDenominator == 0) {
            // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
            // The lines are parallel.
            // Check if they're collinear.
            float y3LessY1 = y3 - y1;
            float collinearityTestForP3 = x1 * (y2 - y3) + x2 * (y3LessY1) + x3 * (y1 - y2); // see http://mathworld.wolfram.com/Collinear.html
            // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
            if (collinearityTestForP3 == 0) {
                // The lines are collinear. Now check if they overlap.
                if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                    if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 || y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 || y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2) {
                        return true;
                    }
                }
            }
            return false;
        }

        return true;
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
