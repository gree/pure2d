/**
 * 
 */
package com.funzio.pure2D.containers;

/**
 * @author long
 */
public interface Alignment {
    public static final int NONE = 0;

    public static final int TOP = 1 << 0;
    public static final int VERTICAL_CENTER = 1 << 1;
    public static final int BOTTOM = 1 << 2;

    public static final int LEFT = 1 << 3;
    public static final int HORIZONTAL_CENTER = 1 << 4;
    public static final int RIGHT = 1 << 5;
}
