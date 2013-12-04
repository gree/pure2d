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

        mMeshBuffer = new QuadMeshBuffer(0);
        mTextureCoordBuffer = new QuadMeshTextureCoordBuffer(0);
        mColorBuffer = new QuadMeshColorBuffer(0);
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
    protected int stackChildAt(final GLState glState, final StackableObject child, final int index) {
        return child.stack(glState, index, mMeshBuffer, mColorBuffer, mTexture != null ? mTextureCoordBuffer : null);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (!super.drawChildren(glState)) {
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

        // draw
        mMeshBuffer.setIndicesNumUsed(mNumDrawingChildren * QuadMeshBuffer.NUM_INDICES_PER_CELL);
        mMeshBuffer.draw(glState);

        return true;
    }

    @Override
    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {
        // stack all sub children
        if (!stackChildren(glState)) {
            return 0;
        }

        final float[] vertices = mMeshBuffer.getVertices();
        // if (mUniParent != null) {
        // mMatrix.mapPoints(vertices);
        // } else if (mMatrixWithoutParents != null) {
        // mMatrixWithoutParents.mapPoints(vertices);
        // }
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
