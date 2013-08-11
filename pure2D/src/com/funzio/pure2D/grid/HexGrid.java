/**
 * 
 */
package com.funzio.pure2D.grid;

/**
 * @author long.ngo
 */
public abstract class HexGrid<T> extends AbstractGrid<T> {
    public static final float SQRT_3 = (float) Math.sqrt(3);
    public static final int CELL_MAX_NEIGHBORS = 6;

    protected float mCellRadius = 1;

    public HexGrid(final int width, final int height) {
        super(width, height);
    }

    public float getCellSize() {
        return mCellRadius;
    }

    public void setCellSize(final float cellSize) {
        mCellRadius = cellSize;

        // update the bounds
        updateBounds();
    }
}
