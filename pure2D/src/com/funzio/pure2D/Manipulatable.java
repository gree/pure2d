/**
 * 
 */
package com.funzio.pure2D;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author long
 */
public interface Manipulatable {
    public void setPosition(final float x, final float y);

    public void setPosition(final PointF position);

    public PointF getPosition();

    public void moveTo(final float x, final float y);

    public void move(final float dx, final float dy);

    public PointF getSize();

    public void setSize(final PointF size);

    public void setSize(final float w, final float h);

    public void setRotation(final float degree);

    public float getRotation();

    public void rotate(final float degreeDelta);

    public void setScale(final float sx, final float sy);

    public void setScale(final float scale);

    public PointF getScale();

    public RectF getBounds();
}
