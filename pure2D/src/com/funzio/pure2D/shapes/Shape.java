/**
 * 
 */
package com.funzio.pure2D.shapes;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Shape extends BaseDisplayObject {
    public final static String TAG = Shape.class.getSimpleName();

    protected int mNumVertices = 0;
    protected VertexBuffer mVertexBuffer;

    protected Texture mTexture;
    protected TextureCoordBuffer mTextureCoordBuffer;
    protected TextureCoordBuffer mTextureCoordBufferScaled;
    protected ColorBuffer mColorBuffer;

    private boolean mTextureFlipped = false;

    public Shape() {
        // TODO nothing
    }

    public Shape(final int verticesNum) {
        mNumVertices = verticesNum;
    }

    public void setVertexBuffer(final VertexBuffer buffer) {
        mVertexBuffer = buffer;
    }

    public VertexBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    /**
     * @return the texture
     */
    public Texture getTexture() {
        return mTexture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(final Texture texture) {
        mTexture = texture;

        // texture coordinates might need to change
        invalidateTextureCoordBuffer();
    }

    /**
     * Invalidate texture coords
     */
    protected void invalidateTextureCoordBuffer() {
        // match texture coordinates with the Axis system
        final Scene scene = getScene();
        if (mTextureCoordBuffer != null && scene != null && scene.getAxisSystem() == Scene.AXIS_TOP_LEFT && !mTextureFlipped) {
            // flip vertically
            mTextureCoordBuffer.flipVertical();
            mTextureFlipped = true;
        }

        // scale to match with the Texture scale, for optimization
        if (mTexture != null && mTextureCoordBuffer != null) {
            if ((mTexture.mCoordScaleX != 1 || mTexture.mCoordScaleY != 1)) {
                // scale the values
                final float[] scaledValues = mTextureCoordBuffer.getValues().clone();
                TextureCoordBuffer.scale(scaledValues, mTexture.mCoordScaleX, mTexture.mCoordScaleY);

                if (mTextureCoordBufferScaled != null && mTextureCoordBufferScaled != mTextureCoordBuffer) {
                    mTextureCoordBufferScaled.setValues(scaledValues);
                } else {
                    mTextureCoordBufferScaled = new TextureCoordBuffer(scaledValues);
                }
            } else {
                mTextureCoordBufferScaled = mTextureCoordBuffer;
            }
        } else {
            mTextureCoordBufferScaled = null;
        }

        // something has changed
        invalidate();
    }

    protected void setTextureCoordBuffer(final float[] values) {
        if (mTextureCoordBuffer != null) {
            mTextureCoordBuffer.setValues(values);
        } else {
            mTextureCoordBuffer = new TextureCoordBuffer(values);
        }

        // invalidate texture coords
        mTextureFlipped = false;
        invalidateTextureCoordBuffer();
    }

    public void setTextureCoordBuffer(final TextureCoordBuffer coords) {
        // diff check
        if (mTextureCoordBuffer == coords) {
            return;
        }

        mTextureCoordBuffer = coords;

        // invalidate texture coords
        mTextureFlipped = false;
        invalidateTextureCoordBuffer();
    }

    public TextureCoordBuffer getTextureCoordBuffer() {
        return mTextureCoordBuffer;
    }

    public void setColorBuffer(final ColorBuffer buffer) {
        mColorBuffer = buffer;
    }

    public ColorBuffer getColorBuffer() {
        return mColorBuffer;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#draw(javax.microedition.khronos.opengles.GL10, int)
     */
    @Override
    public boolean draw(final GLState glState) {
        if (mVertexBuffer == null) {
            return false;
        }

        drawStart(glState);

        // blend mode
        boolean blendChanged = glState.setBlendFunc(mBlendFunc);

        // color and alpha
        glState.setColor(getSumColor());

        // color buffer
        if (mColorBuffer == null) {
            glState.setColorArrayEnabled(false);
        } else {
            // apply color buffer
            mColorBuffer.apply(glState);
        }

        // texture and color
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            // apply the coordinates
            if (mTextureCoordBufferScaled != null) {
                mTextureCoordBufferScaled.apply(glState);
            }
        } else {
            // unbind the texture
            glState.unbindTexture();
        }

        // now draw, woo hoo!
        mVertexBuffer.draw(glState);

        if (blendChanged) {
            // recover the blending
            glState.setBlendFunc(null);
        }

        // wrap up
        drawEnd(glState);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#dispose()
     */
    @Override
    public void dispose() {
        if (mVertexBuffer != null) {
            mVertexBuffer.dispose();
            mVertexBuffer = null;
        }

        if (mTextureCoordBuffer != null) {
            mTextureCoordBuffer.dispose();
            mTextureCoordBuffer = null;
            mTextureFlipped = false;
        }

        if (mTextureCoordBufferScaled != null) {
            mTextureCoordBufferScaled.dispose();
            mTextureCoordBufferScaled = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.containers.Container)
     */
    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        invalidateTextureCoordBuffer();
    }
}
