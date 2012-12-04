/**
 * 
 */
package com.funzio.pure2D;

import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public interface DisplayObject extends Manipulatable {
    public static final int FLIP_X = 1;
    public static final int FLIP_Y = 1 << 1;

    public boolean update(final int deltaTime);

    public boolean draw(final GLState glState);

    public void setX(final float x);

    public float getX();

    public void setY(final float y);

    public float getY();

    public void setZ(final float z);

    public float getZ();

    public PointF getOrigin();

    public void setOrigin(final PointF origin);

    public void setAlive(final boolean value);

    public boolean isAlive();

    public void invalidate();

    public void invalidate(int flags);

    public void setVisible(final boolean value);

    public boolean isVisible();

    public GLColor getColor();

    public void setColor(final GLColor color);

    public float getAlpha();

    public void setAlpha(final float alpha);

    public void setFps(int value);

    public int getFps();

    public boolean addManipulator(final Manipulator manipulator);

    public boolean removeManipulator(Manipulator manipulator);

    public int removeAllManipulators();

    public Scene getScene();

    public Container getParent();

    public boolean removeFromParent();

    public void dispose();

    public RectF getBounds();

    public RectF updateBounds();

    public boolean isAutoUpdateBounds();

    public void setAutoUpdateBounds(final boolean autoUpdateBounds);

    public void onAdded(Container parent);

    public void onRemoved();
}
