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
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.geom.Matrix4;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.GLFloatBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.gl.gl20.DefaultAlias;
import com.funzio.pure2D.gl.gl20.ShaderProgram;

/**
 * @author long
 */
public class VertexBuffer extends GLFloatBuffer {
    protected int mPrimitive = GLES20.GL_TRIANGLES;
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
        glState.setVertexArrayEnabled(true);

        // Specifies the location and data format of an array of vertex coordinates to use when rendering.
        glState.setVertexBuffer(this);

        glState.bindShaderProgram();
        if (!glState.bindTransformMatrix()) return;
        if (!glState.bindVertices()) return;
        glState.bindTexture();
        glState.bindColor();

        if (mIndicesNum > 0) {
            glState.drawElements(mPrimitive, mIndicesNumUsed > 0 ? Math.min(mIndicesNumUsed, mIndicesNum) : mIndicesNum, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);
        } else {
            glState.drawArrays(mPrimitive, 0, mVerticesNum);
        }

        glState.unbind();

        // Disable the vertices buffer.
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
