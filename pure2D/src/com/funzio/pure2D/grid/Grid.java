/**
 * 
 */
package com.funzio.pure2D.grid;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * @author long
 */
public interface Grid<T> {
    public void setSize(int w, int h);

    public Point getSize();

    public Point pointToCell(float x, float y);

    public Point pointToCell(PointF p);

    public PointF cellToPoint(int x, int y);

    public PointF cellToPoint(Point cell);

    public T getDataAt(final int cellX, final int cellY);

    public void setDataAt(final int cellX, final int cellY, final T data);

    public void dispose();
}
