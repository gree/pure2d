/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long.ngo
 */
public class QuadMeshTextureCoordBuffer extends TextureCoordBuffer {
    public static final int NUM_COORD_PER_CELL = 4 * 2;

    protected int mNumCells;
    protected boolean mInvalidated = false;

    protected float mScaleX = 1;
    protected float mScaleY = 1;

    public QuadMeshTextureCoordBuffer(final int numCells) {
        super(null);

        setNumCells(numCells);
    }

    public void setNumCells(final int numCells) {
        if (numCells > mNumCells) {
            // final float[] currentValues = mValues;
            mValues = new float[numCells * NUM_COORD_PER_CELL];

            // restore values
            // if (currentValues != null) {
            // for (int i = 0; i < currentValues.length; i++) {
            // mValues[i] = currentValues[i];
            // }
            // }

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
        mValues[start + 0] = x * mScaleX;
        mValues[start + 1] = y * mScaleY;
        mValues[start + 2] = x * mScaleX;
        mValues[start + 3] = (y + height) * mScaleY;
        mValues[start + 4] = (x + width) * mScaleX;
        mValues[start + 5] = y * mScaleY;
        mValues[start + 6] = (x + width) * mScaleX;
        mValues[start + 7] = (y + height) * mScaleY;

        mInvalidated = true;
    }

    public void setRectFlipVerticalAt(final int index, final float x, final float y, final float width, final float height) {

        final int start = index * NUM_COORD_PER_CELL;
        mValues[start + 0] = x * mScaleX;
        mValues[start + 1] = (y + height) * mScaleY;
        mValues[start + 2] = x * mScaleX;
        mValues[start + 3] = y * mScaleY;
        mValues[start + 4] = (x + width) * mScaleX;
        mValues[start + 5] = (y + height) * mScaleY;
        mValues[start + 6] = (x + width) * mScaleX;
        mValues[start + 7] = y * mScaleY;

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
        mValues[start + 0] = values[0] * mScaleX;
        mValues[start + 1] = values[1] * mScaleY;
        mValues[start + 2] = values[2] * mScaleX;
        mValues[start + 3] = values[3] * mScaleY;
        mValues[start + 4] = values[4] * mScaleX;
        mValues[start + 5] = values[5] * mScaleY;
        mValues[start + 6] = values[6] * mScaleX;
        mValues[start + 7] = values[7] * mScaleY;

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setRectAt(int, float...)}
     */
    protected void validate() {
        if (mInvalidated) {
            setValues(mValues);

            // unflag
            mInvalidated = false;
        }
    }

    /**
     * @param scaleX
     * @param scaleY
     */
    public void setScale(final float scaleX, final float scaleY) {
        // store the values
        if (mValues != null) {
            for (int i = 0; i < mValues.length; i++) {
                if (i % 2 == 0) {
                    mValues[i] *= (scaleX / mScaleX);
                } else {
                    mValues[i] *= (scaleY / mScaleY);
                }
            }
        }

        mScaleX = scaleX;
        mScaleY = scaleY;

        // unflag
        mInvalidated = false;
    }

    @Override
    public void apply(final GLState glState) {
        validate();

        super.apply(glState);
    }

}
