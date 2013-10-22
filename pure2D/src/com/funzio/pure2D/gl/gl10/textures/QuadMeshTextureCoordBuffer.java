/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

/**
 * @author long.ngo
 */
public class QuadMeshTextureCoordBuffer extends TextureCoordBuffer {
    private static final int NUM_COORD_PER_CELL = 4 * 2;

    protected int mNumCells;
    protected boolean mInvalidated = false;

    public QuadMeshTextureCoordBuffer(final int numCells) {
        super(null);

        setNumCells(numCells);
    }

    public void setNumCells(final int numCells) {
        if (numCells > mNumCells) {
            mValues = new float[numCells * NUM_COORD_PER_CELL];

            mInvalidated = true;
        }

        mNumCells = numCells;
    }

    public int getNumCells() {
        return mNumCells;
    }

    /**
     * Sets a Rect at a specified index but doesn't apply untill applyValues() gets called
     * 
     * @param index
     * @param x
     * @param y
     * @param width
     * @param height
     * @see #validate()
     */
    public void setRectAt(final int index, final float x, final float y, final float width, final float height) {

        final int start = index * NUM_COORD_PER_CELL;
        mValues[start + 0] = x;
        mValues[start + 1] = y;
        mValues[start + 2] = x;
        mValues[start + 3] = y + height;
        mValues[start + 4] = x + width;
        mValues[start + 5] = y;
        mValues[start + 6] = x + width;
        mValues[start + 7] = y + height;

        mInvalidated = true;
    }

    public void setRectFlipVerticalAt(final int index, final float x, final float y, final float width, final float height) {

        final int start = index * NUM_COORD_PER_CELL;
        mValues[start + 0] = x;
        mValues[start + 1] = y + height;
        mValues[start + 2] = x;
        mValues[start + 3] = y;
        mValues[start + 4] = x + width;
        mValues[start + 5] = y + height;
        mValues[start + 6] = x + width;
        mValues[start + 7] = y;

        mInvalidated = true;
    }

    /**
     * Sets a Rect at a specified index but doesn't apply untill applyValues() gets called
     * 
     * @param index
     * @param values
     * @see #validate()
     */
    public void setRectAt(final int index, final float... values) {

        final int start = index * NUM_COORD_PER_CELL;
        mValues[start + 0] = values[0];
        mValues[start + 1] = values[1];
        mValues[start + 2] = values[2];
        mValues[start + 3] = values[3];
        mValues[start + 4] = values[4];
        mValues[start + 5] = values[5];
        mValues[start + 6] = values[6];
        mValues[start + 7] = values[7];

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setRectAt(int, float...)}
     */
    public void validate() {
        if (mInvalidated) {
            setValues(mValues);

            // unflag
            mInvalidated = false;
        }
    }

}
