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
 * @author long.ngo
 */
public class VerticalHexGrid<T> extends HexGrid<T> {
    public static final int[][] EVEN_COLUMN_NEIGHBOR_OFFSETS = {
            // even X
            {
                    +1, +1
            }, {
                    +1, 0
            }, {
                    0, -1
            }, {
                    -1, 0
            }, {
                    -1, +1
            }, {
                    0, +1
            },
            // odd X
            {
                    +1, 0
            }, {
                    +1, -1
            }, {
                    0, -1
            }, {
                    -1, -1
            }, {
                    -1, 0
            }, {
                    0, +1
            }
    };
    public static final int[][] ODD_COLUMN_NEIGHBOR_OFFSETS = {
            // even X
            {
                    +1, 0
            }, {
                    +1, -1
            }, {
                    0, -1
            }, {
                    -1, -1
            }, {
                    -1, 0
            }, {
                    0, +1
            },
            // odd X
            {
                    +1, +1
            }, {
                    +1, 0
            }, {
                    0, -1
            }, {
                    -1, 0
            }, {
                    -1, +1
            }, {
                    0, +1
            }
    };

    // ood or even column
    protected boolean mEvenColumn = false;

    public VerticalHexGrid(final int width, final int height, final boolean evenColumn) {
        super(width, height);

        mEvenColumn = evenColumn;
    }

    @Override
    public void pointToCell(final float px, final float py, final Point cell) {
        // FIXME this is not 100% precision
        cell.x = (int) (px / (mCellRadius * 1.5f));
        cell.y = (int) (py / (mCellRadius * SQRT_3) - (mEvenColumn ? -1 : 1) * 0.5f * (cell.x & 1));
    }

    @Override
    public void pointToCell(final PointF point, final Point cell) {
        // FIXME this is not 100% precision
        cell.x = (int) (point.x / (mCellRadius * 1.5f));
        cell.y = (int) (point.y / (mCellRadius * SQRT_3) - (mEvenColumn ? -1 : 1) * 0.5f * (cell.x & 1));
    }

    @Override
    public void cellToPoint(final int cellX, final int cellY, final PointF point) {
        point.x = mCellRadius * 1.5f * cellX;
        point.y = mCellRadius * SQRT_3 * (cellY + (mEvenColumn ? -1 : 1) * 0.5f * (cellX & 1));
    }

    @Override
    public void cellToPoint(final Point cell, final PointF point) {
        point.x = mCellRadius * 1.5f * cell.x;
        point.y = mCellRadius * SQRT_3 * (cell.y + (mEvenColumn ? -1 : 1) * 0.5f * (cell.x & 1));
    }

    @Override
    protected void updateBounds() {
        mBounds.right = (mCellRadius) * (1.5f * mSize.x + 0.5f);
        mBounds.bottom = (mCellRadius * SQRT_3) * (mSize.y + (mEvenColumn ? -1 : 1) * 0.5f);
    }

    public int[][] getNeighborOffets() {
        return mEvenColumn ? EVEN_COLUMN_NEIGHBOR_OFFSETS : ODD_COLUMN_NEIGHBOR_OFFSETS;
    }

    /**
     * Get neighbors of a cell and fetch them into a pre-created array of Points
     * 
     * @param cell
     * @param neighbors
     * @return number of neighbors found
     */
    public int getNeighborsAt(final Point cell, final Point[] neighbors) {
        final int[][] indices = mEvenColumn ? EVEN_COLUMN_NEIGHBOR_OFFSETS : ODD_COLUMN_NEIGHBOR_OFFSETS;
        final int start = (cell.x % 2) * CELL_MAX_NEIGHBORS;
        int index = 0, x, y;
        for (int i = 0; i < CELL_MAX_NEIGHBORS; i++) {
            x = cell.x + indices[start + i][0];
            y = cell.y + indices[start + i][1];
            if (x >= 0 && x < mSize.x && y >= 0 && y < mSize.y) {
                neighbors[index++].set(x, y);
            }
        }

        return index;
    }

    /**
     * Get empty only (or not empty only) neighbors of a cell and fetch them into a pre-created array of Points
     * 
     * @param cell
     * @param neighbors
     * @return number of neighbors found
     */
    public int getNeighborsAt(final Point cell, final Point[] neighbors, final boolean emptyFlag) {
        final int[][] indices = mEvenColumn ? EVEN_COLUMN_NEIGHBOR_OFFSETS : ODD_COLUMN_NEIGHBOR_OFFSETS;
        final int start = (cell.x % 2) * CELL_MAX_NEIGHBORS;
        int index = 0, x, y;
        for (int i = 0; i < CELL_MAX_NEIGHBORS; i++) {
            x = cell.x + indices[start + i][0];
            y = cell.y + indices[start + i][1];
            if (x >= 0 && x < mSize.x && y >= 0 && y < mSize.y && ((emptyFlag && mData[y][x] == null) || (!emptyFlag && mData[y][x] != null))) {
                neighbors[index++].set(x, y);
            }
        }

        return index;
    }

    public int getCellsDistance(final int x1, final int y1, final int x2, final int y2) {
        int cx1, cy1, cz1, cx2, cy2, cz2;
        if (mEvenColumn) {
            cx1 = x1;
            cz1 = y1 - (x1 + x1 & 1) / 2;
            cy1 = -cx1 - cz1;

            cx2 = x2;
            cz2 = y2 - (x2 + x2 & 1) / 2;
            cy2 = -cx2 - cz2;
        } else {
            cx1 = x1;
            cz1 = y1 - (x1 - x1 & 1) / 2;
            cy1 = -cx1 - cz1;

            cx2 = x2;
            cz2 = y2 - (x2 - x2 & 1) / 2;
            cy2 = -cx2 - cz2;
        }

        return (Math.abs(cx1 - cx2) + Math.abs(cy1 - cy2) + Math.abs(cz1 - cz2)) / 2;
    }
}
