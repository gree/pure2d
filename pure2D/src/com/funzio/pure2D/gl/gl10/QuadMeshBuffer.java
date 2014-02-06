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

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class QuadMeshBuffer extends VertexBuffer {
    public static final int NUM_VERTICES_PER_CELL = 4;
    public static final int NUM_INDICES_PER_CELL = 6;

    protected float[] mVertices;
    protected short[] mIndices;
    protected int mNumCells = 0;
    protected boolean mInvalidated = false;

    public QuadMeshBuffer(final int numCells) {
        super(GL10.GL_TRIANGLES, numCells * NUM_VERTICES_PER_CELL);

        setNumCells(numCells);
    }

    public void setNumCells(final int numCells) {
        if (numCells > mNumCells) {
            // final float[] currentVertices = mVertices;
            mVertices = new float[numCells * NUM_VERTICES_PER_CELL * mVertexPointerSize];
            mIndices = new short[numCells * NUM_INDICES_PER_CELL];

            // restore values
            // if (currentVertices != null) {
            // for (int i = 0; i < currentVertices.length; i++) {
            // mVertices[i] = currentVertices[i];
            // }
            // }

            // indices is always fixed
            int start = 0;
            short vertexStart = 0;
            for (int i = 0; i < numCells; i++) {
                // first triangle
                mIndices[start] = vertexStart;
                mIndices[start + 1] = (short) (vertexStart + 1);
                mIndices[start + 2] = (short) (vertexStart + 2);
                // second triangle
                mIndices[start + 3] = (short) (vertexStart + 2);
                mIndices[start + 4] = (short) (vertexStart + 1);
                mIndices[start + 5] = (short) (vertexStart + 3);
                start += NUM_INDICES_PER_CELL;
                vertexStart += NUM_VERTICES_PER_CELL;
            }
            setIndices(mIndices);

            mInvalidated = true;
        }

        mNumCells = numCells;
        mVerticesNum = numCells * NUM_VERTICES_PER_CELL;
    }

    public int getNumCells() {
        return mNumCells;
    }

    /**
     * @return
     */
    public float[] getVertices() {
        return mVertices;
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

        final int start = index * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        mVertices[start] = x;
        mVertices[start + 1] = y + height;
        mVertices[start + 2] = x;
        mVertices[start + 3] = y;
        mVertices[start + 4] = x + width;
        mVertices[start + 5] = y + height;
        mVertices[start + 6] = x + width;
        mVertices[start + 7] = y;

        mInvalidated = true;
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
    public void setRectFlipVerticalAt(final int index, final float x, final float y, final float width, final float height) {

        final int start = index * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        mVertices[start + 2] = x;
        mVertices[start + 3] = y + height;
        mVertices[start + 0] = x;
        mVertices[start + 1] = y;
        mVertices[start + 6] = x + width;
        mVertices[start + 7] = y + height;
        mVertices[start + 4] = x + width;
        mVertices[start + 5] = y;

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final float... values) {

        final int start = index * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        final int length = values.length;
        for (int i = 0; i < length; i++) {
            mVertices[start + i] = values[i];
        }

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final int numCells, final float... values) {

        final int start = index * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        final int length = numCells * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        for (int i = 0; i < length; i++) {
            mVertices[start + i] = values[i];
        }

        mInvalidated = true;
    }

    public void setValuesAt(final int index, final int numCells, final int srcOffset, final float... values) {

        final int start = index * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        final int length = numCells * NUM_VERTICES_PER_CELL * mVertexPointerSize;
        for (int i = 0; i < length; i++) {
            mVertices[start + i] = values[srcOffset + i];
        }

        mInvalidated = true;
    }

    /**
     * Applies the values set by {@link #setRectAt(int, float...)}
     */
    protected void validate() {
        if (mInvalidated) {
            setValues(mVertices);

            // unflag
            mInvalidated = false;
        }
    }

    @Override
    public void draw(final GLState glState) {
        validate();

        super.draw(glState);
    }

}
