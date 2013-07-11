/**
 * 
 */
package com.funzio.pure2D;

/**
 * @author long
 */
public interface InvalidateFlags {
    // transformation related
    public static final int ORIGIN = 1 << 0;
    public static final int POSITION = 1 << 1;
    public static final int SCALE = 1 << 2;
    public static final int ROTATION = 1 << 3;
    public static final int SKEW = 1 << 4;
    public static final int SIZE = 1 << 5;
    public static final int PIVOT = 1 << 6;

    // visual related
    public static final int VISIBILITY = 1 << 10;
    public static final int COLOR = 1 << 11;
    public static final int ALPHA = 1 << 12;
    public static final int BLEND = 1 << 13;
    public static final int TEXTURE = 1 << 14;
    public static final int TEXTURE_COORDS = 1 << 15;
    public static final int PERSPECTIVE = 1 << 16;
    public static final int DEPTH = 1 << 17;
    public static final int FRAME = 1 << 18;

    // container related
    public static final int CHILDREN = 1 << 20;
    public static final int CACHE = 1 << 21;

    // combined flags
    public static final int TRANSFORM_MATRIX = SKEW;
    public static final int BOUNDS = ORIGIN | POSITION | ROTATION | SCALE | SIZE | PIVOT | TRANSFORM_MATRIX;
    public static final int VISUAL = VISIBILITY | COLOR | ALPHA | BLEND | TEXTURE | TEXTURE_COORDS | PERSPECTIVE | DEPTH | FRAME;
    public static final int ALL = BOUNDS | VISUAL | CHILDREN;
}
