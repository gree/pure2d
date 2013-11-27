/**
 * 
 */
package com.funzio.pure2D;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * @author long
 */
public interface Parentable {

    public void removeAllChildren();

    public boolean swapChildren(final int index1, final int index2);

    public int getNumChildren();

    public int getNumGrandChildren();

    public void invalidate();

    public void invalidate(int flags);

    public PointF getSize();

    public boolean isClippingEnabled();

    public void setClippingEnabled(final boolean clippingEnabled);

    public void localToGlobal(final PointF local, final PointF result);

    public void globalToLocal(final PointF global, final PointF result);

    public Matrix getMatrix();

    public Scene getScene();

    public boolean queueEvent(Runnable r);
}
