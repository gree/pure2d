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
package com.funzio.pure2D.gl.gl10;

import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public class QuadMeshColorBuffer extends ColorBuffer {
    public static final int NUM_CHANNEL_PER_COLOR = 4;
    public static final int NUM_COLOR_PER_CELL = 4;

    protected float[] mValues;
    protected int mNumCells = 0;

    protected boolean mInvalidated = false;

    public QuadMeshColorBuffer(final int numCells) {
        super();

        setNumCells(numCells);
    }

    public void setNumCells(final int numCells) {
        if (numCells > mNumCells) {
            mValues = new float[numCells * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL];

            int start = 0;
            for (int i = 0; i < numCells; i++) {
                for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                    mValues[start++] = 1f;
                }
            }

            mInvalidated = true;
        }

        mNumCells = numCells;
    }

    public int getNumCells() {
        return mNumCells;
    }

    public float[] getValues() {
        return mValues;
    }

    /**
     * Set color at the specified cell
     * 
     * @param index
     * @param color
     * @see #validate()
     */
    public void setColorAt(final int index, final GLColor color) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
            if (color != null) {
                mValues[start++] = color.r;
                mValues[start++] = color.g;
                mValues[start++] = color.b;
                mValues[start++] = color.a;
            } else {
                mValues[start++] = 1f;
                mValues[start++] = 1f;
                mValues[start++] = 1f;
                mValues[start++] = 1f;
            }
        }

        mInvalidated = true;
    }

    public void setColorAt(final int index, final float r, final float g, final float b, final float a) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
            mValues[start++] = r;
            mValues[start++] = g;
            mValues[start++] = b;
            mValues[start++] = a;
        }

        mInvalidated = true;
    }

    public void setAlphaAt(final int index, final float alpha) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int j = 0; j < NUM_COLOR_PER_CELL; j++) {
            // mValues[start + 0] = mValues[start + 1] = mValues[start + 2] = mValues[start + 3] = alpha;
            mValues[start + 3] = alpha;

            start += NUM_COLOR_PER_CELL;
        }

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final int numCells, final float... values) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        final int length = numCells * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int i = 0; i < length; i++) {
            mValues[start + i] = values[i];
        }

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final int numCells, final int srcOffset, final float... values) {

        int start = index * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        final int length = numCells * NUM_CHANNEL_PER_COLOR * NUM_COLOR_PER_CELL;
        for (int i = 0; i < length; i++) {
            mValues[start + i] = values[srcOffset + i];
        }

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setColorAt(int, float...)}
     */
    protected void validate() {
        if (mInvalidated) {
            setValues(mValues);

            mInvalidated = false;
        }
    }

    @Override
    public void apply(final GLState glState) {
        validate();

        super.apply(glState);
    }

}
