/**
 * 
 */
package com.funzio.pure2D.text;

import java.util.ArrayList;

import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.containers.Alignment;
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
    protected int mTextAlignment = Alignment.LEFT;
    protected RectF mTextBounds = new RectF();

    private int mSceneAxis = -1;
    private TextureCoordBuffer mTextureCoordBuffer;
    private ArrayList<Float> mLineWidths = new ArrayList<Float>();

    public BmfTextObject() {
        super();
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

    public int getTextAlignment() {
        return mTextAlignment;
    }

    public void setTextAlignment(final int alignment) {
        mTextAlignment = alignment;

        invalidate();
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

        // NOTE: there is a floating error in the native logic. So we need this for precision
        final int length = mText.length();
        float nextX = 0;
        float width = 0;
        int lineIndex = 0;
        float lineWidth = 0;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = mText.charAt(i);

            if (ch == Characters.SPACE) {
                nextX += mFontMetrics.whitespace + mFontMetrics.letterSpacing;
            } else if (ch == Characters.NEW_LINE) {
                nextX = 0;

                // calculate line width
                if (lineIndex > mLineWidths.size() - 1) {
                    mLineWidths.add(lineWidth);
                } else {
                    mLineWidths.set(lineIndex, lineWidth);
                }
                lineIndex++;
                lineWidth = 0;
            } else {
                nextX += mBitmapFont.getCharFrame(ch).getSize().x + mFontMetrics.letterSpacing;
            }

            lineWidth = nextX;
            if (nextX > width) {
                width = nextX;
            }
        }
        if (lineIndex > mLineWidths.size() - 1) {
            mLineWidths.add(lineWidth);
        } else {
            mLineWidths.set(lineIndex, lineWidth);
        }
        // fix the right
        mTextBounds.right = mTextBounds.left + width - 1;

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
        if ((mInvalidateFlags & CHILDREN) != 0) {
            updateTextBounds();
        }

        return super.update(deltaTime);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mBitmapFont == null || mTexture == null) {
            return false;
        }

        // find axis system
        if (mSceneAxis < 0) {
            mSceneAxis = getScene().getAxisSystem();
        }
        final boolean axisFlipped = mSceneAxis == Scene.AXIS_TOP_LEFT;

        // blend mode
        glState.setBlendFunc(mBlendFunc);
        // color and alpha
        glState.setColor(getBlendColor());

        // texture
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            final int length = mText.length();
            float nextX, nextY = mTextBounds.bottom;
            char ch;
            AtlasFrame frame;
            PointF frameSize;

            // alignment
            int lineIndex = 0;
            if ((mTextAlignment & Alignment.HORIZONTAL_CENTER) > 0) {
                nextX = (mSize.x - mLineWidths.get(lineIndex)) * 0.5f;
            } else if ((mTextAlignment & Alignment.RIGHT) > 0) {
                nextX = (mSize.x - mLineWidths.get(lineIndex));
            } else {
                nextX = 0;
            }

            for (int i = 0; i < length; i++) {
                ch = mText.charAt(i);

                if (ch == Characters.SPACE) {
                    nextX += mFontMetrics.whitespace + mFontMetrics.letterSpacing;
                } else if (ch == Characters.NEW_LINE) {

                    // alignment
                    lineIndex++;
                    if ((mTextAlignment & Alignment.HORIZONTAL_CENTER) > 0) {
                        nextX = (mSize.x - mLineWidths.get(lineIndex)) * 0.5f;
                    } else if ((mTextAlignment & Alignment.RIGHT) > 0) {
                        nextX = (mSize.x - mLineWidths.get(lineIndex));
                    } else {
                        nextX = 0;
                    }

                    nextY -= (mFontMetrics.bottom - mFontMetrics.top);
                } else {
                    frame = mBitmapFont.getCharFrame(ch);
                    frameSize = frame.getSize();

                    // apply the coordinates
                    if (mTextureCoordBuffer == null) {
                        mTextureCoordBuffer = new TextureCoordBuffer(frame.getTextureCoords());
                    } else {
                        mTextureCoordBuffer.setValues(frame.getTextureCoords());
                    }
                    mTextureCoordBuffer.apply(glState);

                    // set position and size
                    if (axisFlipped) {
                        mQuadBuffer.setRectFlipVertical(nextX, convertY(nextY - (frameSize.y - frame.mOffset.y), frameSize.y), frameSize.x, frameSize.y);
                    } else {
                        mQuadBuffer.setRect(nextX, nextY - (frameSize.y - frame.mOffset.y), frameSize.x, frameSize.y);
                    }
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

    protected float convertY(final float y, final float size) {
        return mSceneAxis == Scene.AXIS_BOTTOM_LEFT ? y : mSize.y - y - size;
    }

}
