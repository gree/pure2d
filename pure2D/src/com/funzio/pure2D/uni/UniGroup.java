/**
 * 
 */
package com.funzio.pure2D.uni;

import com.funzio.pure2D.BaseDisplayObject;
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
public class UniGroup extends AbstractUniGroup implements Uniable {

    protected QuadMeshBuffer mMeshBuffer;
    protected QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    protected QuadMeshColorBuffer mColorBuffer;

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
    protected boolean stackChildren(final GLState glState) {
        // check and allocate
        if (mNumDrawingChildren > mMeshBuffer.getNumCells()) {
            mMeshBuffer.setNumCells(mNumDrawingChildren);
            mColorBuffer.setNumCells(mNumDrawingChildren);

            if (mTexture != null) {
                mTextureCoordBuffer.setNumCells(mNumDrawingChildren);
            }
        }

        return super.stackChildren(glState);
    }

    @Override
    protected int stackChildAt(final GLState glState, final Uniable child, final int index) {
        return child.updateBuffers(glState, index, mMeshBuffer, mColorBuffer, mTexture != null ? mTextureCoordBuffer : null);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
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

        // draw
        mMeshBuffer.setIndicesNumUsed(mNumDrawingChildren * QuadMeshBuffer.NUM_INDICES_PER_CELL);
        mMeshBuffer.draw(glState);

        return true;
    }

    @Override
    public int updateBuffers(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {
        // stack all sub children
        stackChildren(glState);

        final float[] vertices = mMeshBuffer.getVertices();
        if (mMatrixWithoutParents != null) {
            mMatrixWithoutParents.mapPoints(vertices);
        }

        ((QuadMeshBuffer) vertexBuffer).setValuesAt(index, mNumDrawingChildren, vertices);
        ((QuadMeshColorBuffer) colorBuffer).setValuesAt(index, mNumDrawingChildren, mColorBuffer.getValues());

        // optional
        if (coordBuffer != null) {
            ((QuadMeshTextureCoordBuffer) coordBuffer).setValuesAt(index, mNumDrawingChildren, mTextureCoordBuffer.getValues());
        }

        return mNumDrawingChildren;
    }

    @Override
    public UniContainer getUniParent() {
        return (mParent instanceof UniContainer) ? (UniContainer) mParent : null;
    }

    @Override
    public void onAdded(final UniContainer container) {
        if (container instanceof BaseDisplayObject) {
            mScene = ((BaseDisplayObject) container).getScene();
        }

        // use parent's texture
        setTexture(container.getTexture());
    }
}
