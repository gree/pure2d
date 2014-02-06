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
package com.funzio.pure2D.grid;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * @author long
 */
public class RectGrid<T> extends AbstractGrid<T> {

    protected PointF mCellSize = new PointF(1, 1);
    protected PointF mCellHaftSize = new PointF(mCellSize.x / 2, mCellSize.y / 2);

    // positive orientation should be true by default
    protected boolean mFlipVertical = false;

    /**
     * @param width
     * @param height
     */
    public RectGrid(final int width, final int height) {
        super(width, height);
    }

    public void setCellSize(final float cellWidth, final float cellHeight) {
        mCellSize.x = cellWidth;
        mCellSize.y = cellHeight;

        mCellHaftSize.x = cellWidth / 2;
        mCellHaftSize.y = cellHeight / 2;

        // update the bounds
        updateBounds();
    }

    public PointF getCellSize() {
        return mCellSize;
    }

    @Override
    protected void updateBounds() {
        if (mCellSize != null) {
            mBounds.right = mSize.x * mCellSize.x;
            mBounds.bottom = mSize.y * mCellSize.y;
        }
    }

    @Override
    public void pointToCell(final float x, final float y, final Point cell) {
        cell.x = (int) (x / mCellSize.x);
        cell.y = convertVertical((int) (y / mCellSize.y));
    }

    @Override
    public void pointToCell(final PointF p, final Point cell) {
        cell.x = (int) (p.x / mCellSize.x);
        cell.y = convertVertical((int) (p.y / mCellSize.y));
    }

    @Override
    public void cellToPoint(final int cellX, final int cellY, final PointF point) {
        point.x = cellX * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0);
        point.y = convertVertical(cellY) * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#cellToPoint(android.graphics.Point)
     */
    @Override
    public void cellToPoint(final Point cell, final PointF point) {
        point.x = cell.x * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0);
        point.y = convertVertical(cell.y) * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0);
    }

    public int getCellX(final float x) {
        return (int) (x / mCellSize.x);
    }

    public int getCellY(final float y) {
        return convertVertical((int) (y / mCellSize.y));
    }

    public float getPointX(final int cellX) {
        return cellX * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0);
    }

    public float getPointY(final int cellY) {
        return convertVertical(cellY) * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0);
    }

    public boolean isFlipVertical() {
        return mFlipVertical;
    }

    /**
     * Flip vertically, can be used in AXIS_BOTTOM_LEFT mode
     * 
     * @param flipVertical
     * @see Scene.AXIS_BOTTOM_LEFT, Scene.AXIS_TOP_LEFT
     */
    public void flipVertical(final boolean flipVertical) {
        mFlipVertical = flipVertical;
    }

    protected int convertVertical(final int cellY) {
        return mFlipVertical ? mSize.y - cellY - 1 : cellY;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.AbstractGrid#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        mCellSize = null;
    }
}
