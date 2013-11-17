/**
 * 
 */
package com.funzio.pure2D.uni;

import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;

/**
 * @author long.ngo
 */
public interface Uniable extends Manipulatable {
    public float[] getVertices();

    public float[] getTextureCoords();

    public boolean update(final int deltaTime);

    public void setX(final float x);

    public float getX();

    public void setY(final float y);

    public float getY();

    public void setZ(final float z);

    public float getZ();

    public float getWidth();

    public float getHeight();

    public PointF getOrigin();

    public void setOrigin(final PointF origin);

    public void setOrigin(final float x, final float y);

    public void setOriginAtCenter();

    public PointF getPivot();

    public void setPivot(final PointF pivot);

    public void setPivot(final float x, final float y);

    public void setPivotAtCenter();

    public void setSkew(final float kx, final float ky);

    public PointF getSkew();

    public void setAlive(final boolean value);

    public boolean isAlive();

    public void invalidate();

    public void invalidate(int flags);

    public void setVisible(final boolean value);

    public boolean isVisible();

    public boolean shouldDraw();

    public GLColor getColor();

    public void setColor(final GLColor color);

    public float getAlpha();

    public void setAlpha(final float alpha);

    public void setBlendFunc(final BlendFunc blendFunc);

    public BlendFunc getBlendFunc();

    public void setFps(int value);

    public int getFps();

    public boolean addManipulator(final Manipulator manipulator);

    public boolean removeManipulator(Manipulator manipulator);

    public int removeAllManipulators();

    public Manipulator getManipulator(final int index);

    public int getNumManipulators();

    public UniContainer getParent();

    public boolean removeFromParent();

    public void dispose();

    public void localToGlobal(final PointF local, final PointF result);

    public void globalToLocal(final PointF global, final PointF result);

    public RectF getBounds();

    public RectF updateBounds();

    public boolean isAutoUpdateBounds();

    public void setAutoUpdateBounds(final boolean autoUpdateBounds);

    public String getObjectTree(final String prefix);

    public String getId();

    public void setId(String id);

    /**
     * @hide For internal use
     */
    public void onAdded(UniContainer container);

    /**
     * @hide For internal use
     */
    public void onRemoved();
}
