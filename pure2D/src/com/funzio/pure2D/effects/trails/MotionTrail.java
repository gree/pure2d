/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public interface MotionTrail extends DisplayObject, Reusable {

    public int getNumPoints();

    public void setNumPoints(final int numPoints);

    public Manipulatable getTarget();

    public void setTarget(final Manipulatable target);

    public Object getData();

    public void setData(final Object data);
}
