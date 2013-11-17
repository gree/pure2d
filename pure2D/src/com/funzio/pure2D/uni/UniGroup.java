/**
 * 
 */
package com.funzio.pure2D.uni;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.QuadMeshColorBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long.ngo
 */
public class UniGroup extends AbstractUniGroup {

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
    public void updateChildren(final int deltaTime) {
        super.updateChildren(deltaTime);

        // check and allocate
        if (mNumDrawingChildren > mMeshBuffer.getNumCells()) {
            mMeshBuffer.setNumCells(mNumDrawingChildren);
            mColorBuffer.setNumCells(mNumDrawingChildren);

            if (mTexture != null) {
                mTextureCoordBuffer.setNumCells(mNumDrawingChildren);
            }
        }
    }

    @Override
    protected int stackChildAt(final Uniable child, final int index) {
        mMeshBuffer.setValuesAt(index, child.getVertices());
        mColorBuffer.setColorAt(index, ((UniObject) child).getInheritedColor());

        if (mTexture != null) {
            mTextureCoordBuffer.setRectAt(index, child.getTextureCoords());
        }

        return 1; // just me
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

}
