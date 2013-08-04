/**
 * 
 */
package com.funzio.pure2D.text;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Cacheable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.containers.Alignment;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.shapes.DummyDrawer;

/**
 * @author long
 */
public class BmfTextObject extends BaseDisplayObject implements Cacheable {
    protected BitmapFont mBitmapFont;
    protected TextOptions mTextOptions;
    protected BitmapFontMetrics mFontMetrics;

    protected Texture mTexture;
    protected QuadBuffer mQuadBuffer;

    protected String mText = "";
    protected int mTextAlignment = Alignment.LEFT;
    protected RectF mTextBounds = new RectF();

    private int mSceneAxis = -1;
    private TextureCoordBuffer mTextureCoordBuffer;
    private ArrayList<Float> mLineWidths = new ArrayList<Float>();

    // cache
    protected FrameBuffer mCacheFrameBuffer;
    protected DummyDrawer mCacheDrawer;
    protected boolean mCacheEnabled = false;

    // protected int mCacheProjection = Scene.AXIS_BOTTOM_LEFT;

    public BmfTextObject() {
        super();
    }

    /**
     * @param text the text to set
     */
    public void setText(final String text) {
        mText = text;
        invalidate(CHILDREN);
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

        invalidate(CACHE);
    }

    public BitmapFont getBitmapFont() {
        return mBitmapFont;
    }

    public void setBitmapFont(final BitmapFont bitmapFont) {
        mBitmapFont = bitmapFont;
        mTextOptions = bitmapFont.getTextOptions();
        mFontMetrics = mBitmapFont.getFontMetrics();

        mTexture = bitmapFont.getTexture();
        invalidate(TEXTURE | CHILDREN);
    }

    public void updateTextBounds() {
        // find the bounds, this is not 100% precised, so we need the below logic
        mFontMetrics.getTextBounds(mText, mTextBounds);

        final int length = mText.length();
        float nextX = 0;
        float width = 0;
        int lineIndex = 0;
        float lineWidth = 0;
        char ch;
        AtlasFrame frame;
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
                frame = mBitmapFont.getCharFrame(ch);
                if (frame != null) {
                    nextX += frame.getSize().x + mFontMetrics.letterSpacing;
                } else {
                    Log.e(TAG, "Missing Text Frame: " + ch, new Exception());
                }
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

        // NOTE: there is a floating error in the native logic. So we need this for precision
        mTextBounds.right = mTextBounds.left + width - 1;

        // auto update size
        setSize(mTextBounds.right - mTextBounds.left + 1, mTextBounds.bottom - mTextBounds.top + 1);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        // find axis system
        if (mSceneAxis < 0) {
            mSceneAxis = getScene().getAxisSystem();
        }

        // update text bounds
        if ((mInvalidateFlags & CHILDREN) != 0) {
            updateTextBounds();
        }

        return super.update(deltaTime);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#draw(javax.microedition.khronos.opengles.GL10, int)
     */
    @Override
    public boolean draw(final GLState glState) {
        if (mText == null || mLineWidths.size() == 0) {
            return false;
        }

        drawStart(glState);

        // no color buffer supported
        glState.setColorArrayEnabled(false);

        // check cache enabled, only refresh when children stop changing
        if (mCacheEnabled && (mInvalidateFlags & CHILDREN) == 0 && (mSize.x > 0 && mSize.y > 0)) {
            // check invalidate flags
            if ((mInvalidateFlags & CACHE) != 0 || glState.isInvalidated(SURFACE)) {

                // when surface got reset, the old framebuffer and texture need to be re-created!
                if (glState.isInvalidated(SURFACE) && mCacheFrameBuffer != null) {
                    // unload and remove the old texture
                    glState.getTextureManager().removeTexture(mCacheFrameBuffer.getTexture());
                    // flag for a new frame buffer
                    mCacheFrameBuffer = null;
                }

                // init frame buffer
                if (mCacheFrameBuffer == null || !mCacheFrameBuffer.hasSize(mSize)) {
                    if (mCacheFrameBuffer != null) {
                        mCacheFrameBuffer.unload();
                        mCacheFrameBuffer.getTexture().unload();
                    }
                    mCacheFrameBuffer = new FrameBuffer(glState, mSize.x, mSize.y, true);
                    mCacheFrameBuffer.getTexture().setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR); // better output
                    // init drawer
                    if (mCacheDrawer == null) {
                        mCacheDrawer = new DummyDrawer();
                        // framebuffer is inverted
                        if (mSceneAxis == Scene.AXIS_BOTTOM_LEFT) {
                            mCacheDrawer.flipTextureCoordBuffer(FLIP_Y);
                        }
                    }
                    mCacheDrawer.setTexture(mCacheFrameBuffer.getTexture());
                }

                // cache to FBO
                mCacheFrameBuffer.bind(mSceneAxis);
                mCacheFrameBuffer.clear();

                // this helps fix the FBO's alpha blending, not 100% though
                glState.setBlendFunc(BlendModes.PREMULTIPLIED_ALPHA_FUNC);
                // clear the color
                glState.setColor(null);
                // draw now
                drawChildren(glState);

                mCacheFrameBuffer.unbind();

                // validate cache
                validate(CACHE);
            }

            // now the real blend mode
            glState.setBlendFunc(getInheritedBlendFunc());
            // color and alpha
            glState.setColor(getInheritedColor());
            // now draw the cache
            mCacheDrawer.draw(glState);
        } else {
            // blend mode
            glState.setBlendFunc(getInheritedBlendFunc());
            // color and alpha
            glState.setColor(getInheritedColor());
            // draw the children directly
            drawChildren(glState);

            // invalidate cache
            invalidate(CACHE);
        }

        drawEnd(glState);

        // validate visual and children, NOT bounds
        mInvalidateFlags &= ~(VISUAL | CHILDREN);

        return true;
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        // sanity check
        if (mBitmapFont == null || mTexture == null) {
            return false;
        }

        // init quad buffer
        if (mQuadBuffer == null) {
            mQuadBuffer = new QuadBuffer();
        }

        // bind the texture
        mTexture.bind();

        final boolean axisFlipped = (mSceneAxis == Scene.AXIS_TOP_LEFT);
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

                // next y
                nextY -= (mFontMetrics.bottom - mFontMetrics.top);
            } else {
                // get the current atlas frame
                frame = mBitmapFont.getCharFrame(ch);
                if (frame != null) {
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

                    // find next x
                    nextX += frameSize.x + mFontMetrics.letterSpacing;
                }
            }
        }

        return true;
    }

    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    /**
     * Enable/disable cache. Use this when there are many static children to improve performance. This also clips the children inside the bounds.
     * 
     * @param cacheEnabled
     */
    public void setCacheEnabled(final boolean cacheEnabled) {
        // diff check
        if (mCacheEnabled == cacheEnabled) {
            return;
        }

        mCacheEnabled = cacheEnabled;

        invalidate(CACHE);
    }

    public void clearCache() {
        if (mCacheFrameBuffer != null) {
            mCacheFrameBuffer.getTexture().unload();
            mCacheFrameBuffer.unload();
            mCacheFrameBuffer = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.IDisplayObject#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();

        if (mQuadBuffer != null) {
            mQuadBuffer.dispose();
            mQuadBuffer = null;
        }

        if (mTextureCoordBuffer != null) {
            mTextureCoordBuffer.dispose();
            mTextureCoordBuffer = null;
        }

        if (mLineWidths != null) {
            mLineWidths.clear();
            mLineWidths = null;
        }

        // clear cache
        clearCache();

        if (mCacheDrawer != null) {
            mCacheDrawer.dispose();
            mCacheDrawer = null;
        }
    }

    protected float convertY(final float y, final float size) {
        return mSceneAxis == Scene.AXIS_BOTTOM_LEFT ? y : mSize.y - y - size;
    }

}
