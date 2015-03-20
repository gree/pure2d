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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.gl.GLFloatBuffer;

/**
 * @author long
 */
public class VertexBuffer extends GLFloatBuffer {
    protected int mPrimitive = GL10.GL_TRIANGLES;
    protected int mVerticesNum = 0;
    protected int mIndicesNum = 0;
    protected int mIndicesNumUsed = 0;
    protected ShortBuffer mIndexBuffer;
    protected int mVertexPointerSize = 2; // only x & y

    public VertexBuffer(final int primitive, final int verticesNum, final float... vertices) {
        setVertices(primitive, verticesNum, vertices);
    }

    public void setVertices(final int primitive, final int verticesNum, final float... vertices) {
        setPrimitive(primitive);

        // vertices buffer
        mVerticesNum = verticesNum;
        setValues(vertices);
    }

    public int getVerticesNum() {
        return mVerticesNum;
    }

    public void setIndices(final short... indices) {
        if (indices != null) {
            if (mIndexBuffer == null || mIndicesNum != indices.length) {
                // short is 2 bytes, therefore we multiply the number if vertices with 2.
                ByteBuffer bb = ByteBuffer.allocateDirect(indices.length * 2);
                bb.order(ByteOrder.nativeOrder());
                mIndexBuffer = bb.asShortBuffer();
            } else {
                // recycle for better perf
                mIndexBuffer.clear();
            }

            mIndexBuffer.put(indices);
            mIndexBuffer.flip();
            mIndicesNum = indices.length;
        } else if (mIndexBuffer != null) {
            // clean up
            mIndexBuffer.clear();
            mIndexBuffer = null;
            mIndicesNum = 0;
        }
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public int getIndicesNum() {
        return mIndicesNum;
    }

    public int getIndicesNumUsed() {
        return mIndicesNumUsed;
    }

    public void setIndicesNumUsed(final int indicesNumUsed) {
        mIndicesNumUsed = indicesNumUsed;
    }

    public int getPrimitive() {
        return mPrimitive;
    }

    public void setPrimitive(final int primitive) {
        mPrimitive = primitive;
    }

    public int getVertexPointerSize() {
        return mVertexPointerSize;
    }

    public void setVertexPointerSize(final int vertexPointerSize) {
        mVertexPointerSize = vertexPointerSize;
    }

    public void draw(final GLState glState) {
        // gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        glState.setVertexArrayEnabled(true);

        // Specifies the location and data format of an array of vertex coordinates to use when rendering.
        // glState.mGL.glVertexPointer(mVertexPointerSize, GL10.GL_FLOAT, 0, mBuffer);
        glState.setVertexBuffer(this);

        if (mIndicesNum > 0) {
            glState.mGL.glDrawElements(mPrimitive, mIndicesNumUsed > 0 ? Math.min(mIndicesNumUsed, mIndicesNum) : mIndicesNum, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        } else {
            glState.mGL.glDrawArrays(mPrimitive, 0, mVerticesNum);
        }

        // Disable the vertices buffer.
        // gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public void dispose() {
        super.dispose();

        // indices buffer
        if (mIndexBuffer != null) {
            mIndexBuffer.clear();
            mIndexBuffer = null;
            mIndicesNum = 0;
        }
    }
}
