/**
 * 
 */
package com.funzio.pure2D.geom;

/**
 * @author long.ngo
 */
public class Rectangle {

    public static boolean intersects(final float left1, final float top1, final float right1, final float bottom1, final float left2, final float top2, final float right2, final float bottom2) {
        return left1 < right2 && left2 < right1 && top1 < bottom2 && top2 < bottom1;
    }

}
