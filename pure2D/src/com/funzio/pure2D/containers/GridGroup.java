/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.containers;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.grid.Grid;

/**
 * @author long
 */
public class GridGroup<T extends DisplayObject> extends DisplayGroup {

    protected Grid<T> mGrid;
    protected PointF mTempPoint = new PointF();

    public GridGroup(final Grid<T> grid) {
        setGrid(grid);
    }

    protected void setGrid(final Grid<T> grid) {
        mGrid = grid;

        // match the bound size
        if (grid != null) {
            final RectF bounds = grid.getBounds();
            setSize(bounds.width() + 1, bounds.height() + 1);
        }
    }

    public void addChildAt(final T child, final int cellX, final int cellY) {
        addChild(child);

        setChildAt(child, cellX, cellY, true);
    }

    public void addChildAt(final T child, final int cellX, final int cellY, final boolean updatePosition) {
        addChild(child);

        setChildAt(child, cellX, cellY, updatePosition);
    }

    public T getChildAt(final int cellX, final int cellY) {
        return mGrid.getDataAt(cellX, cellY);
    }

    public void setChildAt(final T child, final int cellX, final int cellY, final boolean updatePosition) {
        // set data
        mGrid.setDataAt(cellX, cellY, child);

        // set position
        if (child != null && updatePosition) {
            mGrid.cellToPoint(cellX, cellY, mTempPoint);
            child.setPosition(mTempPoint);
        } else {
            // force
            invalidate(InvalidateFlags.CHILDREN);
        }
    }

    public void setChildAt(final T child, final Point cell, final boolean updatePosition) {
        setChildAt(child, cell.x, cell.y, updatePosition);
    }

    public void swapChildren(final Point cell1, final Point cell2, final boolean updatePosition) {
        T child1 = getChildAt(cell1.x, cell1.y);
        T child2 = getChildAt(cell2.x, cell2.y);

        // swap data
        setChildAt(child1, cell2.x, cell2.y, updatePosition);
        setChildAt(child2, cell1.x, cell1.y, updatePosition);
    }

    public void swapChildren(final Point cell1, final Point cell2) {
        swapChildren(cell1, cell2, true);
    }

    public void removeChildAt(final int cellX, final int cellY) {
        T child = getChildAt(cellX, cellY);
        if (child != null) {
            removeChild(child);

            // clear grid
            setChildAt(null, cellX, cellY, false);
        }
    }

    public void removeChildAt(final Point cell) {
        removeChildAt(cell.x, cell.y);
    }

    public Grid<T> getGrid() {
        return mGrid;
    }
}
