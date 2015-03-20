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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author long
 */
public abstract class AbstractGrid<T> implements Grid<T> {
    public static final String TAG = AbstractGrid.class.getName();

    protected T[][] mData;
    protected Point mSize = new Point();
    protected boolean mUseCellCenter = true;
    protected RectF mBounds = new RectF();

    protected ArrayList<PointF> mScratchList;

    public AbstractGrid(final int width, final int height) {
        setSize(width, height);
    }

    abstract protected void updateBounds();

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

        updateBounds();
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

    public RectF getBounds() {
        return mBounds;
    }

    public PointF[] cellToPointPath(final List<? extends Point> cellPath, final boolean compression) {

        int size = cellPath.size();
        int count = 0;
        PointF[] path;

        if (compression) {
            if (mScratchList == null) {
                mScratchList = new ArrayList<PointF>();
            } else {
                // clear
                mScratchList.clear();
            }

            // optimize the path by removing nodes on the same line
            PointF lastPoint = null;
            float lastVectorX = 0, lastVectorY = 0;
            float newVectorX, newVectorY;
            for (int i = 0; i < size; i++) {
                final PointF newPoint = new PointF();
                // from cell to point
                cellToPoint(cellPath.get(i), newPoint);

                if (lastPoint == null) {
                    // first node
                    mScratchList.add(newPoint);
                    count++;
                } else {
                    newVectorX = Math.round(newPoint.x - lastPoint.x);
                    newVectorY = Math.round(newPoint.y - lastPoint.y);
                    if ((lastVectorX == newVectorX && lastVectorY == newVectorY)) {
                        // override the node
                        mScratchList.get(count - 1).set(newPoint);
                    } else {
                        // add new node
                        mScratchList.add(newPoint);
                        count++;
                    }

                    lastVectorX = newVectorX;
                    lastVectorY = newVectorY;
                }

                lastPoint = newPoint;
            }

            // now make the array
            path = new PointF[count];
            for (int i = 0; i < count; i++) {
                path[i] = mScratchList.get(i);
            }
            // clear
            mScratchList.clear();

        } else {
            path = new PointF[size];
            for (int i = 0; i < size; i++) {
                path[i] = new PointF();
                cellToPoint(cellPath.get(i), path[i]);
            }
            count = size;
        }

        // Log.v(TAG, "cellToPointPath(): " + size + " -> " + count);

        return path;
    }

    /**
     * Optimized version
     * 
     * @param cellPath
     * @param compression
     * @param resultPath
     * @return
     */
    public int cellToPointPath(final List<? extends Point> cellPath, final boolean compression, final PointF[] resultPath) {

        int size = cellPath.size();
        int count = 0;

        if (compression) {

            // optimize the path by removing nodes on the same line
            PointF lastPoint = null;
            float lastVectorX = 0, lastVectorY = 0;
            float newVectorX, newVectorY;
            for (int i = 0; i < size; i++) {
                PointF newPoint = resultPath[i];
                if (newPoint == null) {
                    newPoint = new PointF();
                }
                // from cell to point
                cellToPoint(cellPath.get(i), newPoint);

                if (lastPoint == null) {
                    // first node
                    if (resultPath[count] != newPoint) {
                        if (resultPath[count] == null) {
                            resultPath[count] = newPoint;
                        } else {
                            resultPath[count].set(newPoint);
                        }
                    }
                    count++;
                } else {
                    newVectorX = Math.round(newPoint.x - lastPoint.x);
                    newVectorY = Math.round(newPoint.y - lastPoint.y);
                    if ((lastVectorX == newVectorX && lastVectorY == newVectorY)) {
                        // override the node
                        resultPath[count - 1].set(newPoint);
                    } else {
                        // add new node
                        if (resultPath[count] != newPoint) {
                            if (resultPath[count] == null) {
                                resultPath[count] = newPoint;
                            } else {
                                resultPath[count].set(newPoint);
                            }
                        }
                        count++;
                    }

                    lastVectorX = newVectorX;
                    lastVectorY = newVectorY;
                }

                lastPoint = newPoint;
            }

        } else {
            for (int i = 0; i < size; i++) {
                // reuse when possible
                if (resultPath[i] == null) {
                    resultPath[i] = new PointF();
                }
                cellToPoint(cellPath.get(i), resultPath[i]);
            }
            count = size;
        }

        // Log.v(TAG, "cellToPointPath(): " + size + " -> " + count);

        return count;
    }
}
