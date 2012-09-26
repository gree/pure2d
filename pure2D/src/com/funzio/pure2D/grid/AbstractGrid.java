/**
 * 
 */
package com.funzio.pure2D.grid;

import android.graphics.Point;

/**
 * @author long
 */
public abstract class AbstractGrid<T> implements Grid<T> {

    protected T[][] mData;
    protected Point mSize = new Point();
    protected boolean mUseCellCenter = true;

    public AbstractGrid(final int width, final int height) {
        setSize(width, height);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#setSize(int, int)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setSize(final int w, final int h) {
        mSize.x = w;
        mSize.y = h;

        mData = (T[][]) new Object[h][w];
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#getSize()
     */
    @Override
    public Point getSize() {
        return mSize;
    }

    public T getDataAt(final int cellX, final int cellY) {
        // bounds check
        if (cellX < 0 || cellX >= mSize.x || cellY < 0 || cellY >= mSize.y) {
            return null;
        }

        return mData[cellY][cellX];
    }

    public void setDataAt(final int cellX, final int cellY, final T data) {
        // bounds check
        if (cellX < 0 || cellX >= mSize.x || cellY < 0 || cellY >= mSize.y) {
            return;
        }

        mData[cellY][cellX] = data;
    }

    public void dispose() {
        mSize = null;
        mData = null;
    }
}
