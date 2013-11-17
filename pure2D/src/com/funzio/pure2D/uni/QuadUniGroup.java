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
public class QuadUniGroup extends UniGroup {

    protected QuadMeshBuffer mMeshBuffer;
    protected QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    protected QuadMeshColorBuffer mColorBuffer;

    public QuadUniGroup() {
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
    protected boolean drawChildren(final GLState glState) {
        // check and allocate
        if (mNumChildren > mMeshBuffer.getNumCells()) {
            mMeshBuffer.setNumCells(mNumChildren);
            mTextureCoordBuffer.setNumCells(mNumChildren);
            mColorBuffer.setNumCells(mNumChildren);
        }

        super.drawChildren(glState);

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
        if (mNumStackedChildren > 0) {
            mMeshBuffer.setIndicesNumUsed(mNumStackedChildren * QuadMeshBuffer.NUM_INDICES_PER_CELL);
            mMeshBuffer.draw(glState);
            return true;
        }

        return false;
    }

    @Override
    protected void stackChildAt(final UniObject child, final int index) {
        mMeshBuffer.setValuesAt(index, child.getVertices());
        mTextureCoordBuffer.setRectAt(index, child.getTextureCoords());
        mColorBuffer.setColorAt(index, child.getInheritedColor());
    }
}
