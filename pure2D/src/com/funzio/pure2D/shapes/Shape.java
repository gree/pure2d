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
package com.funzio.pure2D.shapes;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.ui.UIConfig;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public class Shape extends BaseDisplayObject {
    public final static String TAG = Shape.class.getSimpleName();

    // XML attributes
    protected static final String ATT_ASYNC = "async";
    protected static final String ATT_SOURCE = "source";

    protected VertexBuffer mVertexBuffer;

    protected Texture mTexture;
    private boolean mTextureLoaded;
    protected TextureCoordBuffer mTextureCoordBuffer;
    protected TextureCoordBuffer mTextureCoordBufferScaled;
    protected ColorBuffer mColorBuffer;

    // for axis system
    private boolean mTextureFlippedForAxis = false;

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
        mTextureLoaded = mTexture != null ? mTexture.isLoaded() : false;

        invalidate(mTextureLoaded ? (TEXTURE | TEXTURE_COORDS) : TEXTURE);
    }

    @Override
    public boolean update(final int deltaTime) {
        // async support: texture loaded detection
        if (!mTextureLoaded && mTexture != null && mTexture.isLoaded()) {
            // flag
            mTextureLoaded = true;
            invalidate(TEXTURE_COORDS);

            // internal callback
            onTextureLoaded(mTexture);
        }

        return super.update(deltaTime);
    }

    @Override
    protected void drawStart(final GLState glState) {
        // texture coordinates changed?
        if ((mInvalidateFlags & TEXTURE_COORDS) != 0) {
            validateTextureCoordBuffer();
        }

        super.drawStart(glState);
    }

    /**
     * validate texture coords
     */
    protected void validateTextureCoordBuffer() {
        // match texture coordinates with the Axis system
        if (mTextureCoordBuffer != null && mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT && !mTextureFlippedForAxis) {
            // flip vertically
            mTextureCoordBuffer.flipVertical();
            mTextureFlippedForAxis = true;
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

        // clear flag: texture coords
        validate(TEXTURE_COORDS);
    }

    protected boolean setTextureCoordBuffer(final float[] values) {
        if (mTextureCoordBuffer != null) {
            // diff check
            if (Arrays.equals(mTextureCoordBuffer.getValues(), values)) {
                return false;
            }

            mTextureCoordBuffer.setValues(values);
        } else {
            mTextureCoordBuffer = new TextureCoordBuffer(values);
        }

        // invalidate texture coords
        mTextureFlippedForAxis = false;

        invalidate(TEXTURE_COORDS);

        return true;
    }

    public void setTextureCoordBuffer(final TextureCoordBuffer coords) {
        // diff check
        if (mTextureCoordBuffer == coords) {
            return;
        }

        mTextureCoordBuffer = coords;

        // invalidate texture coords
        mTextureFlippedForAxis = false;

        invalidate(TEXTURE_COORDS);
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

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mVertexBuffer == null) {
            return false;
        }

        // color buffer
        if (mColorBuffer == null) {
            glState.setColorArrayEnabled(false);
        } else {
            // apply color buffer
            mColorBuffer.apply(glState);
        }

        // texture
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

        return true;
    }

    @Override
    protected void drawWireframe(final GLState glState) {
        // null check
        if (mVertexBuffer == null) {
            return;
        }

        // pre-draw
        final int primitive = mVertexBuffer.getPrimitive();
        final GLColor currentColor = glState.getColor();
        final boolean textureEnabled = glState.isTextureEnabled();
        final float currentLineWidth = glState.getLineWidth();
        glState.setColor(Pure2D.DEBUG_WIREFRAME_COLOR);
        glState.setTextureEnabled(false);

        // draw
        mVertexBuffer.setPrimitive(GL10.GL_LINE_STRIP);
        mVertexBuffer.draw(glState);

        // post-draw
        glState.setTextureEnabled(textureEnabled);
        glState.setColor(currentColor);
        glState.setLineWidth(currentLineWidth);
        mVertexBuffer.setPrimitive(primitive);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (mVertexBuffer != null) {
            mVertexBuffer.dispose();
            mVertexBuffer = null;
        }

        if (mTextureCoordBuffer != null) {
            mTextureCoordBuffer.dispose();
            mTextureCoordBuffer = null;
            mTextureFlippedForAxis = false;
        }

        if (mTextureCoordBufferScaled != null) {
            mTextureCoordBufferScaled.dispose();
            mTextureCoordBufferScaled = null;
        }
    }

    /**
     * @param flips can be #DisplayObject.FLIP_X and/or #DisplayObject.FLIP_Y
     * @see #DisplayObject
     */
    public void flipTextureCoordBuffer(final int flips) {
        // null check
        if (mTextureCoordBuffer == null) {
            return;
        }

        boolean flipped = false;

        if ((flips & FLIP_X) > 0) {
            mTextureCoordBuffer.flipHorizontal();
            flipped = true;
        }

        if ((flips & FLIP_Y) > 0) {
            mTextureCoordBuffer.flipVertical();
            flipped = true;
        }

        if (flipped) {
            invalidate(TEXTURE_COORDS);
        }
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String source = xmlParser.getAttributeValue(null, ATT_SOURCE);
        if (source != null && !source.endsWith(UIConfig.FILE_JSON)) {
            final String async = xmlParser.getAttributeValue(null, ATT_ASYNC);
            setTexture(manager.getTextureManager().getUriTexture(manager.evalString(source), null, async != null ? Boolean.valueOf(async) : UIConfig.DEFAULT_ASYNC));
        }
    }

    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        invalidate(TEXTURE_COORDS);
    }

    protected void onTextureLoaded(final Texture texture) {
        // TODO Auto-generated method stub
    }

}
