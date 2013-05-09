/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLES11;

/**
 * @author long
 * @description This is designed for large buffer only and requires GLES11.
 */
public class VBO extends VertexBuffer {

    private int mVertexID = 0;
    private int mIndexID = 0;
    private boolean mInvalidated = false;

    public VBO(final int primitive, final int verticesNum, final float[] vertices, final short[] indices) {
        super(primitive, verticesNum, vertices, indices);
    }

    public VBO(final int primitive, final int verticesNum, final float... vertices) {
        super(primitive, verticesNum, vertices);
    }

    @Override
    public void setVertices(final int primitive, final int verticesNum, final float[] vertices, final short[] indices) {
        super.setVertices(primitive, verticesNum, vertices, indices);

        // flag
        mInvalidated = true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.VertexBuffer#draw(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    public void draw(final GLState glState) {
        glState.setVertexArrayEnabled(true);

        if (mVertexID == 0 || mInvalidated) {

            final int[] temp = new int[1];
            if (mVertexID != 0) {
                temp[0] = mVertexID;
                // unload it
                GLES11.glDeleteBuffers(1, temp, 0);
            }
            if (mIndexID != 0) {
                temp[0] = mIndexID;
                // unload it
                GLES11.glDeleteBuffers(1, temp, 0);
            }

            // create new vertex id
            GLES11.glGenBuffers(1, temp, 0);
            mVertexID = temp[0];
            // bind it
            GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexID);
            // load it
            GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER, mVerticesNum * mVertexPointerSize * 4, mBuffer, GLES11.GL_STATIC_DRAW);

            if (mIndicesNum > 0) {
                // create new index id
                GLES11.glGenBuffers(1, temp, 0);
                mIndexID = temp[0];
                // bind it
                GLES11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexID);
                // load it
                GLES11.glBufferData(GLES11.GL_ELEMENT_ARRAY_BUFFER, mIndicesNum * 2, mIndexBuffer, GLES11.GL_STATIC_DRAW);
            }

            // unflag
            mInvalidated = false;
        } else {
            // bind vertex buffer
            GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexID);

            if (mIndexID != 0) {
                // bind index buffer
                GLES11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexID);
            }
        }

        // Specifies the location and data format of an array of vertex coordinates to use when rendering.
        GLES11.glVertexPointer(mVertexPointerSize, GL11.GL_FLOAT, 0, 0);

        if (mIndicesNum > 0) {
            GLES11.glDrawElements(mPrimitive, mIndicesNum, GL10.GL_UNSIGNED_SHORT, 0);
        } else {
            GLES11.glDrawArrays(mPrimitive, 0, mVerticesNum);
        }

        // unbind
        GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        if (mIndexID != 0) {
            GLES11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void unload() {
        if (mVertexID == 0) {
            final int[] buffers = {
                mVertexID
            };
            GLES11.glDeleteBuffers(1, buffers, 0);
            mVertexID = 0;

            // check index
            if (mIndexID != 0) {
                buffers[0] = mIndexID;
                GLES11.glDeleteBuffers(1, buffers, 0);
                mIndexID = 0;
            }
        }
    }

    public int getVertexID() {
        return mVertexID;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.VertexBuffer#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();

        unload();
    }

}
