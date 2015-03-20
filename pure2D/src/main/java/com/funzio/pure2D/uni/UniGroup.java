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
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.QuadMeshColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long.ngo
 */
public class UniGroup extends AbstractUniGroup implements StackableObject {

    protected QuadMeshBuffer mMeshBuffer;
    protected QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    protected QuadMeshColorBuffer mColorBuffer;

    protected boolean mStackable;

    public UniGroup() {
        super();
    }

    @Override
    protected VertexBuffer createVertexBuffer() {
        return mMeshBuffer = new QuadMeshBuffer(0);
    }

    @Override
    protected TextureCoordBuffer createTextureCoordBuffer() {
        return mTextureCoordBuffer = new QuadMeshTextureCoordBuffer(0);
    }

    @Override
    protected ColorBuffer createColorBuffer() {
        return mColorBuffer = new QuadMeshColorBuffer(0);
    }

    @Override
    protected void onTextureLoaded(final Texture texture) {
        super.onTextureLoaded(texture);

        mTextureCoordBuffer.setScale(texture.mCoordScaleX, texture.mCoordScaleY);
    }

    @Override
    protected void setNumDrawingChildren(final int num) {
        super.setNumDrawingChildren(num);

        // check and allocate
        if (num > mMeshBuffer.getNumCells()) {
            mMeshBuffer.setNumCells(num);
            mColorBuffer.setNumCells(num);

            if (mTexture != null) {
                mTextureCoordBuffer.setNumCells(num);
            }
        }
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        // stack all children first
        if (!stackChildren(glState)) {
            return false;
        }

        // color buffer
        if (mColorBuffer != null) {
            // apply color buffer
            mColorBuffer.apply(glState);
        } else {
            glState.setColorArrayEnabled(false);
        }

        // texture
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            // apply coords
            mTextureCoordBuffer.apply(glState);
        } else {
            // unbind the texture
            glState.unbindTexture();
            glState.setTextureCoordArrayEnabled(false);
        }

        // flush it out
        mMeshBuffer.setIndicesNumUsed(mNumDrawingChildren * QuadMeshBuffer.NUM_INDICES_PER_CELL);
        mMeshBuffer.draw(glState);

        return true;
    }

    // StackableObject implementation, for recursive nesting ///////////////////////////

    @Override
    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {
        // stack all sub children
        if (!stackChildren(glState)) {
            return 0;
        }

        final float[] vertices = mMeshBuffer.getVertices();
        if (mMatrixForVertices != null) {
            mMatrixForVertices.mapPoints(vertices);
        }

        ((QuadMeshBuffer) vertexBuffer).setValuesAt(index, mNumDrawingChildren, vertices);
        ((QuadMeshColorBuffer) colorBuffer).setValuesAt(index, mNumDrawingChildren, mColorBuffer.getValues());

        // optional
        if (coordBuffer != null) {
            ((QuadMeshTextureCoordBuffer) coordBuffer).setValuesAt(index, mNumDrawingChildren, mTextureCoordBuffer.getValues());
        }

        // for debugging
        final int debugFlags = Pure2D.DEBUG_FLAGS | mDebugFlags;
        // debug global bounds
        if ((debugFlags & Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS) != 0) {
            drawBounds(glState);
        }

        // validate visual only
        mInvalidateFlags &= ~VISUAL;

        return mNumDrawingChildren;
    }

    /**
     * @hide for internal use
     */
    @Override
    public void setStackable(final boolean value) {
        mStackable = value;
    }

    /**
     * @hide for internal use
     */
    @Override
    public boolean isStackable() {
        return mStackable;
    }

}
