/**
 * 
 */
package com.funzio.pure2D.text;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Text extends BaseDisplayObject {

    private BitmapFont mBitmapFont;
    private Texture mTexture;
    protected String mText = "";
    private TextureCoordBuffer mTextureCoordBufferScaled;
    private boolean mTextureFlippedForAxis = false;
    protected float mTextureScaleX = 1, mTextureScaleY = 1;
    protected QuadBuffer mQuadBuffer = new QuadBuffer();;

    public Text() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        mText = text;
    }

    /**
     * @return the text
     */
    public String getText() {
        return mText;
    }

    public BitmapFont getBitmapFont() {
        return mBitmapFont;
    }

    public void setBitmapFont(final BitmapFont bitmapFont) {
        mBitmapFont = bitmapFont;
        mTexture = mBitmapFont.getTexture();
        invalidate(TEXTURE | TEXTURE_COORDS);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mBitmapFont == null || mTexture == null) {
            return false;
        }

        if ((mInvalidateFlags & TEXTURE_COORDS) != 0) {
            validateTextureCoordBuffer();
        }

        // blend mode
        glState.setBlendFunc(mBlendFunc);
        // color and alpha
        glState.setColor(getBlendColor());

        // texture
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            final int length = mText.length();
            float x = 0;
            for (int i = 0; i < length; i++) {
                char ch = mText.charAt(i);
                if (ch == ' ') {
                    // do something
                } else {
                    AtlasFrame frame = mBitmapFont.getCharFrame(ch);

                    // apply the coordinates
                    mTextureCoordBufferScaled.setValues(frame.getTextureCoords());
                    mTextureCoordBufferScaled.apply(glState);

                    // set position and size
                    mQuadBuffer.setRect(x, 0, frame.getSize().x, frame.getSize().y);
                    // draw
                    mQuadBuffer.draw(glState);

                    x += frame.getSize().x;
                }
            }

        } else {
            // unbind the texture
            glState.unbindTexture();
        }

        return true;
    }

    /**
     * validate texture coords
     */
    protected void validateTextureCoordBuffer() {
        if (mTextureCoordBufferScaled == null) {
            mTextureCoordBufferScaled = TextureCoordBuffer.getDefault();
            mTextureFlippedForAxis = false;
        }

        // match texture coordinates with the Axis system
        final Scene scene = getScene();
        if (scene != null && scene.getAxisSystem() == Scene.AXIS_TOP_LEFT && !mTextureFlippedForAxis) {
            // flip vertically
            mTextureCoordBufferScaled.flipVertical();
            mTextureFlippedForAxis = true;
        }

        // scale to match with the Texture scale, for optimization
        if (mTexture != null) {
            // diff check
            if ((mTexture.mCoordScaleX != mTextureScaleX || mTexture.mCoordScaleY != mTextureScaleY)) {
                // apply scale
                mTextureCoordBufferScaled.scale(mTexture.mCoordScaleX / mTextureScaleX, mTexture.mCoordScaleY / mTextureScaleY);
                // store for ref
                mTextureScaleX = mTexture.mCoordScaleX;
                mTextureScaleY = mTexture.mCoordScaleY;
            }
        }

        // clear flag: texture coords
        validate(TEXTURE_COORDS);
    }

}
