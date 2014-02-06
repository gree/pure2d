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
        mValues[start + 0] = x;
        mValues[start + 1] = y;
        mValues[start + 2] = x;
        mValues[start + 3] = (y + height);
        mValues[start + 4] = (x + width);
        mValues[start + 5] = y;
        mValues[start + 6] = (x + width);
        mValues[start + 7] = (y + height);

        mInvalidated = true;
    }

    public void setRectFlipVerticalAt(final int index, final float x, final float y, final float width, final float height) {

        final int start = index * NUM_COORD_PER_CELL;
        mValues[start + 0] = x;
        mValues[start + 1] = (y + height);
        mValues[start + 2] = x;
        mValues[start + 3] = y;
        mValues[start + 4] = (x + width);
        mValues[start + 5] = (y + height);
        mValues[start + 6] = (x + width);
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

    public void setValuesAt(final int index, final int numCells, final float... values) {

        final int start = index * NUM_COORD_PER_CELL;
        final int length = numCells * NUM_COORD_PER_CELL;
        for (int i = 0; i < length; i++) {
            mValues[start + i] = values[i];
        }

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final int numCells, final int srcOffset, final float... values) {

        final int start = index * NUM_COORD_PER_CELL;
        final int length = numCells * NUM_COORD_PER_CELL;
        for (int i = 0; i < length; i++) {
            mValues[start + i] = values[srcOffset + i];
        }

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setRectAt(int, float...)}
     */
    protected void validate() {
        if (mInvalidated) {

            // scale the values
            if (mValues != null && (mScaleX != 1 || mScaleY != 1)) {
                for (int i = 0; i < mValues.length; i++) {
                    if (i % 2 == 0) {
                        mValues[i] *= mScaleX;
                    } else {
                        mValues[i] *= mScaleY;
                    }
                }
            }

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
        mScaleX = scaleX;
        mScaleY = scaleY;

        // unflag
        mInvalidated = true;
    }

    @Override
    public void apply(final GLState glState) {
        validate();

        super.apply(glState);
    }

}
