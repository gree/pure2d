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
    private int mVerticesNum = 0;
    private int mIndicesNum = 0;
    private ShortBuffer mIndexBuffer;
    private int mPrimitive = GL10.GL_TRIANGLES;
    private int mVertexPointerSize = 2; // only x & y

    public VertexBuffer(final int primitive, final int verticesNum, final float[] vertices, final short[] indices) {
        setVertices(primitive, verticesNum, vertices, indices);
    }

    public VertexBuffer(final int primitive, final int verticesNum, final float[] vertices) {
        setVertices(primitive, verticesNum, vertices, null);
    }

    public void setVertices(final int primitive, final int verticesNum, final float[] vertices) {
        setVertices(primitive, verticesNum, vertices, null);
    }

    public void setVertices(final int primitive, final int verticesNum, final float[] vertices, final short[] indices) {
        setPrimitive(primitive);

        // vertices buffer
        mVerticesNum = verticesNum;
        setValues(vertices);

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

    public int getVerticesNum() {
        return mVerticesNum;
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
        glState.mGL.glVertexPointer(mVertexPointerSize, GL10.GL_FLOAT, 0, mBuffer);

        if (mIndicesNum > 0) {
            glState.mGL.glDrawElements(mPrimitive, mIndicesNum, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
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
