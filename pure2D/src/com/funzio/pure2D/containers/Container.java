/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public interface Container {
    public boolean addChild(final DisplayObject child);

    public boolean addChild(final DisplayObject child, final int index);

    public boolean removeChild(final DisplayObject child);

    public void removeAllChildren();

    public DisplayObject getChildAt(final int index);

    public int getChildIndex(final DisplayObject child);

    public DisplayObject getChildById(final String id);

    public boolean swapChildren(final DisplayObject child1, final DisplayObject child2);

    public boolean swapChildren(final int index1, final int index2);

    public int getNumChildren();

    public int getNumGrandChildren();

    public void invalidate();

    public void invalidate(int flags);

    public PointF getSize();

    public void localToGlobal(final PointF local, final PointF result);

    public void globalToLocal(final PointF global, final PointF result);

    public Matrix getMatrix();

    public Scene getScene();

    public boolean queueEvent(Runnable r);
}
