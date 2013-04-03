/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.RectF;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Sprite9 extends Rectangular {
    protected static final int NUM_PATCHES = 9;

    private final RectF m9Patches = new RectF(0, 0, 0, 0);
    private final QuadBuffer[] mQuadBuffers = new QuadBuffer[NUM_PATCHES];
    private final TextureCoordBuffer[] mCoordBuffers = new TextureCoordBuffer[NUM_PATCHES];;

    public Sprite9() {
        super();

        for (int i = 0; i < NUM_PATCHES; i++) {
            mQuadBuffers[i] = new QuadBuffer();
            mCoordBuffers[i] = new TextureCoordBuffer();
        }
    }

    public void set9Patches(final float left, final float right, final float top, final float bottom) {
        m9Patches.set(left, top, right, bottom);

        invalidate(InvalidateFlags.TEXTURE_COORDS);
    }

    public RectF get9Patches() {
        return m9Patches;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#setTexture(com.funzio.pure2D.gl.gl10.textures.Texture)
     */
    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // auto set size if it's not set
        if (texture != null && mSize.x <= 1 && mSize.y <= 1) {
            setSize(texture.getSize());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#validateTextureCoordBuffer()
     */
    @Override
    protected void validateTextureCoordBuffer() {
        if (mTexture == null) {
            return;
        }

        final float left = m9Patches.left;
        final float right = m9Patches.right;
        final float top = m9Patches.top;
        final float bottom = m9Patches.bottom;
        final float textureW = mTexture.getSize().x;
        final float textureH = mTexture.getSize().y;
        final float middleW = mSize.x - left - right;
        final float middleH = mSize.y - top - bottom;

        // vertices
        final float[] widths = {
                left, middleW, right
        };
        final float[] heights = {
                bottom, middleH, top
        };
        // swap for AXIS_TOP_LEFT
        if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
            heights[0] = top;
            heights[2] = bottom;
        }

        // texture coordinates
        final float[] scaleX = {
                (left / textureW) * mTexture.mCoordScaleX, ((textureW - left - right) / textureW) * mTexture.mCoordScaleX, (right / textureW) * mTexture.mCoordScaleX
        };
        final float[] scaleY = {
                (bottom / textureH) * mTexture.mCoordScaleY, ((textureH - top - bottom) / textureH) * mTexture.mCoordScaleY, (top / textureH) * mTexture.mCoordScaleY
        };
        // swap for AXIS_TOP_LEFT
        if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
            final float temp = scaleY[0];
            scaleY[0] = scaleY[2];
            scaleY[2] = temp;
        }

        float vx = 0, vy = 0; // vertex start x,y
        float tx = 0, ty = 1, tyInverted = 0; // texture coord start x,y
        int index = 0;
        for (int row = 0; row < 3; row++) {
            float vh = heights[row];
            float th = scaleY[row];
            vx = 0;
            tx = 0;

            for (int col = 0; col < 3; col++) {
                float vw = widths[col];
                float tw = scaleX[col];

                // set the quad values
                mQuadBuffers[index].setXYWH(vx, vy, vw, vh);

                // set the coordinates
                if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                    mCoordBuffers[index].setXYWH(tx, tyInverted, tw, th);
                } else {
                    mCoordBuffers[index].setXYWH(tx, ty, tw, -th);
                }

                vx += vw;
                tx += tw;
                index++;
            }

            vy += vh;
            ty -= th;
            tyInverted += th;
        }

        // clear flag: texture coords
        validate(InvalidateFlags.TEXTURE_COORDS);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Rectangular#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // also invalidate coordinates
        invalidate(InvalidateFlags.TEXTURE_COORDS);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.shapes.Shape#draw(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    public boolean draw(final GLState glState) {
        // texture check
        if (mTexture == null) {
            return super.draw(glState);
        }

        drawStart(glState);

        // blend mode
        final boolean blendChanged = glState.setBlendFunc(mBlendFunc);

        // color and alpha
        glState.setColor(getSumColor());

        // color buffer
        if (mColorBuffer == null) {
            glState.setColorArrayEnabled(false);
        } else {
            // apply color buffer
            mColorBuffer.apply(glState);
        }

        // bind the texture
        mTexture.bind();

        // check and draw the quads
        QuadBuffer quad;
        for (int i = 0; i < NUM_PATCHES; i++) {
            quad = mQuadBuffers[i];
            // only draw when the quad is set
            if (quad.isSet()) {
                // now draw, woo hoo!
                mCoordBuffers[i].apply(glState);
                quad.draw(glState);
            }
        }

        if (blendChanged) {
            // recover the blending
            glState.setBlendFunc(null);
        }

        // wrap up
        drawEnd(glState);

        return true;
    }
}
