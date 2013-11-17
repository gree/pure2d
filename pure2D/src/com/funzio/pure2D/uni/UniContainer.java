/**
 * 
 */
package com.funzio.pure2D.uni;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * @author long
 */
public interface UniContainer {
    public boolean addChild(final UniObject child);

    public boolean addChild(final UniObject child, final int index);

    public boolean removeChild(final UniObject child);

    public void removeAllChildren();

    public UniObject getChildAt(final int index);

    public int getChildIndex(final UniObject child);

    public UniObject getChildById(final String id);

    public boolean swapChildren(final UniObject child1, final UniObject child2);

    public boolean swapChildren(final int index1, final int index2);

    public int getNumChildren();

    public int getNumGrandChildren();

    public void invalidate();

    public void invalidate(int flags);

    public PointF getSize();

    public PointF localToGlobal(final PointF local);

    public void localToGlobal(final PointF local, final PointF result);

    public PointF globalToLocal(final PointF global);

    public void globalToLocal(final PointF global, final PointF result);

    public Matrix getMatrix();

    public boolean queueEvent(Runnable r);
}
