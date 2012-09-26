/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class VerticesOld {
    private int mVerticesNum = 0;
    private int mIndicesNum = 0;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private boolean mHasColor = false;
    private boolean mHasTexture = false;
    private int mPrimitive = GL10.GL_TRIANGLES;
    private int mStride = 0;

    public VerticesOld(final int primitive, final int verticesNum, final float[] vertices, final short[] indices, final boolean hasColor, final boolean hasTexture) {
        setVertices(primitive, verticesNum, vertices, indices, hasColor, hasTexture);
    }

    public VerticesOld(final int primitive, final int verticesNum, final float[] vertices) {
        setVertices(primitive, verticesNum, vertices, null, false, false);
    }

    public VerticesOld(final int primitive, final int verticesNum, final float[] vertices, final short[] indices) {
        setVertices(primitive, verticesNum, vertices, indices, false, false);
    }

    public void setVertices(final int primitive, final int verticesNum, final float[] vertices, final short[] indices, final boolean hasColor, final boolean hasTexture) {
        mPrimitive = primitive;
        mHasColor = hasColor;
        mHasTexture = hasTexture;

        // vertices
        mVerticesNum = verticesNum;
        // a float is 4 bytes, therefore we multiply the number if vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
        }
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        // indices
        if (mIndexBuffer != null) {
            // clean the old first
            mIndexBuffer.clear();
            mIndexBuffer = null;
            mIndicesNum = 0;
        }
        if (indices != null) {
            mIndicesNum = indices.length;
            // short is 2 bytes, therefore we multiply the number if vertices with 2.
            ByteBuffer ibb = ByteBuffer.allocateDirect(mIndicesNum * 2);
            ibb.order(ByteOrder.nativeOrder());

            mIndexBuffer = ibb.asShortBuffer();
            mIndexBuffer.put(indices);
            mIndexBuffer.position(0);
        }

        mStride = 0;
        if (hasColor) {
            mStride += (3 + 4) * 4; // (x, y, z, r, g, b, a)
        }
        if (hasTexture) {
            mStride += 2 * 4; // (x, y)
        }
    }

    public void draw(final GL10 gl) {
        int position = 0;
        mVertexBuffer.position(position);
        // Enabled the vertices buffer for writing and to be used during rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, mStride, mVertexBuffer);
        position += 3 * 4; // xyz * 4

        if (mHasColor) {
            mVertexBuffer.position(position);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glColorPointer(4, GL10.GL_FLOAT, mStride, mVertexBuffer);
            position += 4 * 4; // rgba * 4
        }

        if (mHasTexture) {
            mVertexBuffer.position(position);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, mStride, mVertexBuffer);
        }

        if (mIndicesNum > 0) {
            gl.glDrawElements(mPrimitive, mIndicesNum, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        } else {
            gl.glDrawArrays(mPrimitive, 0, mVerticesNum);
        }

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        if (mHasColor) {
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
        if (mHasTexture) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
    }

    public void dispose() {
        // vertices buffer
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }

        // indices buffer
        if (mIndexBuffer != null) {
            mIndexBuffer.clear();
            mIndexBuffer = null;
        }
    }
}
