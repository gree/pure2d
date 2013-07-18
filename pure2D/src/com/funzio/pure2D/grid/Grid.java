/**
 * 
 */
package com.funzio.pure2D.grid;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author long
 */
public interface Grid<T> {
    public void setSize(int w, int h);

    public Point getSize();

    public void pointToCell(float px, float py, final Point cell);

    public void pointToCell(PointF point, final Point cell);

    public void cellToPoint(final int cellX, final int cellY, PointF point);

    public void cellToPoint(Point cell, PointF point);

    public T getDataAt(final int cellX, final int cellY);

    public void setDataAt(final int cellX, final int cellY, final T data);

    public RectF getBounds();

    public void dispose();
}
