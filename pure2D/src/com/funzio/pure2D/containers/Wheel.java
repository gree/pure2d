/**
 * 
 */
package com.funzio.pure2D.containers;

/**
 * @author long
 */
public interface Wheel {
    public static final float SPIN_ACCELERATION = 0.002f;

    public void spin(float veloc);

    public void stop();

    public float getVelocity();
}
