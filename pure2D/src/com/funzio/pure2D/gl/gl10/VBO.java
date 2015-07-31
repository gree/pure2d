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

import android.opengl.GLES20;

import com.funzio.pure2D.gl.gl20.ShaderProgram;

/**
 * @author long
 * @description This is designed for large buffer only and requires GLES11.
 */
public class VBO extends VertexBuffer {

    private int mVertexID = 0;
    private int mIndexID = 0;
    private boolean mInvalidated = false;
    private final int[] mScratch = new int[1];

    public VBO(final int primitive, final int verticesNum, final float... vertices) {
        super(primitive, verticesNum, vertices);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.GLFloatBuffer#setValues(float[])
     */
    @Override
    public void setValues(final float... values) {
        super.setValues(values);

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

            if (mVertexID != 0) {
                mScratch[0] = mVertexID;
                // unload it
                GLES20.glDeleteBuffers(1, mScratch, 0);
            }
            if (mIndexID != 0) {
                mScratch[0] = mIndexID;
                // unload it
                GLES20.glDeleteBuffers(1, mScratch, 0);
            }

            // create new vertex id
            GLES20.glGenBuffers(1, mScratch, 0);
            mVertexID = mScratch[0];
            // bind it
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexID);
            // load it
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVerticesNum * mVertexPointerSize * 4, mBuffer, GLES20.GL_STATIC_DRAW);

            if (mIndicesNum > 0) {
                // create new index id
                GLES20.glGenBuffers(1, mScratch, 0);
                mIndexID = mScratch[0];
                // bind it
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexID);
                // load it
                GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndicesNum * 2, mIndexBuffer, GLES20.GL_STATIC_DRAW);
            }

            // unflag
            mInvalidated = false;
        } else {
            // bind vertex buffer
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexID);

            if (mIndexID != 0) {
                // bind index buffer
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexID);
            }
        }

        // Specifies the location and data format of an array of vertex coordinates to use when rendering.
//        GLES11.glVertexPointer(mVertexPointerSize, GL11.GL_FLOAT, 0, 0);

        if (mIndicesNum > 0) {
//            GLES11.glDrawElements(mPrimitive, mIndicesNum, GL10.GL_UNSIGNED_SHORT, 0);
        } else {
//            GLES11.glDrawArrays(mPrimitive, 0, mVerticesNum);
        }
//        public void draw(final GLState glState) {
//            glState.setVertexArrayEnabled(true);
//
//            // Specifies the location and data format of an array of vertex coordinates to use when rendering.
            glState.setVertexBuffer(this);
//
//            if (!glState.bindTransformMatrix(program)) return;
            if (!glState.bindVertices()) return;
//public boolean bindVertices(final ShaderProgram program) {
//    final int a_Position = program.getAttribLocation(DefaultAlias.ATTRIB_VEC2_POSITION);
//    if (a_Position < 0) {
//        Log.w(TAG, "Could not bind vertices; ShaderProgram is missing alias: " + DefaultAlias.ATTRIB_VEC2_POSITION);
//        return false;
//    }
//
//    final Buffer vertexBuffer = mVertexBuffer.mBuffer;
//    vertexBuffer.position(0);
//
//    GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
//    GLES20.glEnableVertexAttribArray(a_Position);
//    return true;
//}



//            glState.bindTexture(program);
//            glState.bindColor(program);
//
//            if (mIndicesNum > 0) {
//                glState.drawElements(mPrimitive, mIndicesNumUsed > 0 ? Math.min(mIndicesNumUsed, mIndicesNum) : mIndicesNum, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
//            } else {
//                glState.drawArrays(mPrimitive, 0, mVerticesNum);
//            }
//
//            glState.unbind(program);
//
//            // Disable the vertices buffer.
//        }

        // unbind
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        if (mIndexID != 0) {
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    public void unload() {
        if (mVertexID == 0) {
            mScratch[0] = mVertexID;
            GLES20.glDeleteBuffers(1, mScratch, 0);
            mVertexID = 0;

            // check index
            if (mIndexID != 0) {
                mScratch[0] = mIndexID;
                GLES20.glDeleteBuffers(1, mScratch, 0);
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
