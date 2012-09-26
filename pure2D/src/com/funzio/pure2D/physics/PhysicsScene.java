/**
 * 
 */
package com.funzio.pure2D.physics;

import com.funzio.pure2D.Scene;

/**
 * @author long
 * @category Physics
 */
public interface PhysicsScene extends Scene {

    public PhysicsWorld getWorld();

    public void setWorld(PhysicsWorld world);

}
