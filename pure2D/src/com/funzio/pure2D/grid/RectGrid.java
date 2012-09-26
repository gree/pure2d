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
    }

    public PointF getCellSize() {
        return mCellSize;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#pointToCell(float, float)
     */
    @Override
    public Point pointToCell(final float x, final float y) {
        return new Point((int) (x / mCellSize.x), (int) (y / mCellSize.y));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#pointToCell(android.graphics.PointF)
     */
    @Override
    public Point pointToCell(final PointF p) {
        return new Point((int) (p.x / mCellSize.x), (int) (p.y / mCellSize.y));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#cellToPoint(int, int)
     */
    @Override
    public PointF cellToPoint(final int x, final int y) {
        return new PointF(x * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0), y * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.grid.Grid#cellToPoint(android.graphics.Point)
     */
    @Override
    public PointF cellToPoint(final Point cell) {
        return new PointF(cell.x * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0), cell.y * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0));
    }

    public int getCellX(final float x) {
        return (int) (x / mCellSize.x);
    }

    public int getCellY(final float y) {
        return (int) (y / mCellSize.y);
    }

    public float getPointX(final int cellX) {
        return cellX * mCellSize.x + (mUseCellCenter ? mCellHaftSize.x : 0);
    }

    public float getPointY(final int cellY) {
        return cellY * mCellSize.y + (mUseCellCenter ? mCellHaftSize.y : 0);
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
