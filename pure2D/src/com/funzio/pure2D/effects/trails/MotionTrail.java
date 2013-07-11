/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public interface MotionTrail extends DisplayObject, Reusable {

    public int getNumPoints();

    public void setNumPoints(final int numPoints);

    public DisplayObject getTarget();

    public void setTarget(final DisplayObject target);

    public Object getData();

    public void setData(final Object data);
}
