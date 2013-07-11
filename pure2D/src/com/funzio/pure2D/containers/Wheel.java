/**
 * 
 */
package com.funzio.pure2D.containers;

/**
 * @author long
 */
public interface Wheel {
    public static final float DEFAULT_SPIN_ACCELERATION = 0.002f;
    public static final float DEFAULT_SNAP_ACCELERATION = 0.002f;
    public static final int DEFAULT_SNAP_DURATION = 200;

    public void spin(float veloc);

    public void stop();

    public float getVelocity();
}
