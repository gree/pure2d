/**
 * 
 */
package com.funzio.pure2D.physics;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 * @category Physics
 */
public interface PhysicsObject extends DisplayObject {
    public void setBody(PhysicsBody body);

    public PhysicsBody getBody();
}
