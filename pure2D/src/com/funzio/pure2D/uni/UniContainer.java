/**
 * 
 */
package com.funzio.pure2D.uni;

import android.graphics.Matrix;
import android.graphics.PointF;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public interface UniContainer {
    public Texture getTexture();

    public void setTexture(final Texture texture);

    public boolean addChild(final Uniable child);

    public boolean addChild(final Uniable child, final int index);

    public boolean removeChild(final Uniable child);

    public void removeAllChildren();

    public Uniable getChildAt(final int index);

    public int getChildIndex(final Uniable child);

    public Uniable getChildById(final String id);

    public boolean swapChildren(final Uniable child1, final Uniable child2);

    public boolean swapChildren(final int index1, final int index2);

    public int getNumChildren();

    public int getNumGrandChildren();

    public int getNumDrawingChildren();

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
