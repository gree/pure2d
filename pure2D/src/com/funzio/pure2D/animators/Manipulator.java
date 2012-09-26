/**
 * 
 */
package com.funzio.pure2D.animators;

import com.funzio.pure2D.Manipulatable;

/**
 * @author long
 */
public interface Manipulator {
    public void setTarget(Manipulatable target);

    public Manipulatable getTarget();

    public boolean update(int deltaTime);
}
