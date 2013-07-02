/**
 * 
 */
package com.funzio.pure2D.text;

import android.graphics.PointF;
import android.graphics.RectF;

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
public class BmfTextObject extends BaseDisplayObject {
    protected BitmapFont mBitmapFont;
    protected TextOptions mTextOptions;
    protected BitmapFontMetrics mFontMetrics;

    protected Texture mTexture;
    protected QuadBuffer mQuadBuffer = new QuadBuffer();

    protected String mText = "";
    protected RectF mTextBounds = new RectF();

    private TextureCoordBuffer mTextureCoordBufferScaled;
    private boolean mTextureFlippedForAxis = false;
    protected float mTextureScaleX = 1, mTextureScaleY = 1;

    public BmfTextObject() {
        super();
        mAutoUpdateBounds = true; // XXX remove me
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        mText = text;
        invalidate(CHILDREN | BOUNDS);
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
        mTextOptions = bitmapFont.getTextOptions();
        mFontMetrics = mBitmapFont.getFontMetrics();

        mTexture = bitmapFont.getTexture();
        invalidate(TEXTURE | TEXTURE_COORDS);
    }

    protected void updateTextBounds() {
        mFontMetrics.getTextBounds(mText, mTextBounds);

        // auto update size
        mSize.x = mTextBounds.right - mTextBounds.left + 1;
        mSize.y = mTextBounds.bottom - mTextBounds.top + 1;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // update text bounds
        if ((mInvalidateFlags & BOUNDS) != 0) {
            updateTextBounds();
        }

        return super.update(deltaTime);
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
            float nextX = 0, nextY = mTextBounds.bottom;
            char ch;
            AtlasFrame frame;
            PointF frameSize;
            for (int i = 0; i < length; i++) {
                ch = mText.charAt(i);

                if (ch == Characters.SPACE) {
                    nextX += mFontMetrics.whitespace + mFontMetrics.letterSpacing;
                } else if (ch == Characters.NEW_LINE) {
                    nextX = 0;
                    nextY -= (mFontMetrics.bottom - mFontMetrics.top);
                } else {
                    frame = mBitmapFont.getCharFrame(ch);
                    frameSize = frame.getSize();

                    // apply the coordinates
                    mTextureCoordBufferScaled.setValues(frame.getTextureCoords());
                    mTextureCoordBufferScaled.apply(glState);

                    // set position and size
                    mQuadBuffer.setRect(nextX, nextY - (frameSize.y - frame.mOffset.y), frameSize.x, frameSize.y);
                    // draw
                    mQuadBuffer.draw(glState);

                    nextX += frameSize.x + mFontMetrics.letterSpacing;
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
        // if (mTexture != null) {
        // // diff check
        // if ((mTexture.mCoordScaleX != mTextureScaleX || mTexture.mCoordScaleY != mTextureScaleY)) {
        // // apply scale
        // mTextureCoordBufferScaled.scale(mTexture.mCoordScaleX / mTextureScaleX, mTexture.mCoordScaleY / mTextureScaleY);
        // // store for ref
        // mTextureScaleX = mTexture.mCoordScaleX;
        // mTextureScaleY = mTexture.mCoordScaleY;
        // }
        // }

        // clear flag: texture coords
        validate(TEXTURE_COORDS);
    }

}
