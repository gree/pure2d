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
package com.funzio.pure2D.uni;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.QuadMeshColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class UniRect extends UniObject {
    public static final int NUM_VERTICES = 4;

    protected float[] mTextureCoords;

    public UniRect() {
        super();

        mVertices = new float[NUM_VERTICES * 2];
        mTextureCoords = new float[NUM_VERTICES * 2];
        TextureCoordBuffer.getDefault(mTextureCoords);
    }

    protected void setTextureCoords(final float[] source) {
        mTextureCoords[0] = source[0];
        mTextureCoords[1] = source[1];
        mTextureCoords[2] = source[2];
        mTextureCoords[3] = source[3];
        mTextureCoords[4] = source[4];
        mTextureCoords[5] = source[5];
        mTextureCoords[6] = source[6];
        mTextureCoords[7] = source[7];
    }

    @Override
    protected void resetVertices() {
        final float x = 0;
        final float y = 0;
        final float width = mSize.x;
        final float height = mSize.y;

        mVertices[0] = x;
        mVertices[1] = y + height;
        mVertices[2] = x;
        mVertices[3] = y;
        mVertices[4] = x + width;
        mVertices[5] = y + height;
        mVertices[6] = x + width;
        mVertices[7] = y;
    }

    @Override
    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {
        ((QuadMeshBuffer) vertexBuffer).setValuesAt(index, mVertices);
        ((QuadMeshColorBuffer) colorBuffer).setColorAt(index, getInheritedColor());

        // optional
        if (coordBuffer != null) {
            ((QuadMeshTextureCoordBuffer) coordBuffer).setRectAt(index, mTextureCoords);
        }

        // for debugging
        final int debugFlags = Pure2D.DEBUG_FLAGS | mDebugFlags;
        // local rect
        if ((debugFlags & Pure2D.DEBUG_FLAG_WIREFRAME) != 0) {
            drawWireframe(glState);
        }
        // debug global bounds
        if ((debugFlags & Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS) != 0) {
            drawBounds(glState);
        }

        // validate visual only
        mInvalidateFlags &= ~VISUAL;

        return 1; // just me
    }
}
