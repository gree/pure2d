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
package com.funzio.pure2D.text;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Cacheable;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.containers.Alignment;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.DummyDrawer;
import com.funzio.pure2D.ui.UIConfig;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public class BmfTextObject extends BaseDisplayObject implements Cacheable {

    // xml attributes
    protected static final String ATT_LETTER_SPACING = "letterSpacing";
    protected static final String ATT_TEXT = "text";
    protected static final String ATT_TEXT_ALIGN = "textAlign";
    protected static final String ATT_FONT = "font";
    protected static final String ATT_SIZE = "size";

    protected BitmapFont mBitmapFont;
    protected TextOptions mTextOptions;
    protected BitmapFontMetrics mFontMetrics;

    protected Texture mTexture;
    protected QuadMeshBuffer mMeshBuffer;

    protected String mText = "";
    protected int mTextAlignment = Alignment.LEFT;
    protected RectF mTextBounds = new RectF();

    private int mSceneAxis = -1;
    private QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    private ArrayList<Float> mLineWidths = new ArrayList<Float>();

    // cache
    protected FrameBuffer mCacheFrameBuffer;
    protected DummyDrawer mCacheDrawer;
    protected boolean mCacheEnabled = false;

    // a copy of text for rendering without being interrupted
    private static String sScratchText;

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

    @Override
    public boolean update(final int deltaTime) {
        // find axis system
        if (mSceneAxis < 0) {
            mSceneAxis = mScene.getAxisSystem();
        }

        // update text bounds
        if ((mInvalidateFlags & CHILDREN) != 0 || mSize.x <= 1 || mSize.y <= 1) {
            updateTextBounds();
        }

        return super.update(deltaTime);
    }

    @Override
    public boolean draw(final GLState glState) {
        if (mText == null || mText.length() == 0) {
            return false;
        }

        drawStart(glState);

        // no color buffer supported
        glState.setColorArrayEnabled(false);

        // check cache enabled, only refresh when children stop changing
        if (mCacheEnabled && (mInvalidateFlags & CHILDREN) == 0 && (mSize.x > 0 && mSize.y > 0)) {
            // check invalidate flags
            if ((mInvalidateFlags & CACHE) != 0 || (mCacheFrameBuffer != null && !mCacheFrameBuffer.verifyGLState(glState))) {

                // when surface got reset, the old framebuffer and texture need to be re-created!
                if (mCacheFrameBuffer != null && !mCacheFrameBuffer.verifyGLState(glState)) {
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

        // bind the texture
        mTexture.bind();

        if ((mInvalidateFlags & InvalidateFlags.CHILDREN) > 0) {
            sScratchText = mText;
            final boolean axisFlipped = (mSceneAxis == Scene.AXIS_TOP_LEFT);
            final int length = sScratchText.length();
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

            // init mesh buffer
            if (mMeshBuffer == null) {
                mMeshBuffer = new QuadMeshBuffer(length);
            } else {
                mMeshBuffer.setNumCells(length);
            }
            // apply the coordinates
            if (mTextureCoordBuffer == null) {
                mTextureCoordBuffer = new QuadMeshTextureCoordBuffer(length);
            } else {
                mTextureCoordBuffer.setNumCells(length);
            }

            int meshIndex = 0;
            for (int i = 0; i < length; i++) {
                ch = sScratchText.charAt(i);

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
                        mTextureCoordBuffer.setRectAt(meshIndex, frame.getTextureCoords());

                        // set position and size
                        if (axisFlipped) {
                            mMeshBuffer.setRectFlipVerticalAt(meshIndex++, nextX, convertY(nextY - (frameSize.y - frame.mOffset.y), frameSize.y), frameSize.x, frameSize.y);
                        } else {
                            mMeshBuffer.setRectAt(meshIndex++, nextX, nextY - (frameSize.y - frame.mOffset.y), frameSize.x, frameSize.y);
                        }

                        // find next x
                        nextX += frameSize.x + mFontMetrics.letterSpacing;
                    }
                }
            }

            // apply
            // mTextureCoordBuffer.validate();
            // and the vertex buffer
            mMeshBuffer.setIndicesNumUsed(meshIndex * QuadMeshBuffer.NUM_INDICES_PER_CELL);
            // mMeshBuffer.validate();
        }

        // draw now
        mTextureCoordBuffer.apply(glState);
        mMeshBuffer.draw(glState);

        return true;
    }

    @Deprecated
    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    /**
     * Enable/disable cache. With the new method of QuadMeshBuffer, Cache is not necessary faster.
     * 
     * @param cacheEnabled
     */
    @Deprecated
    public void setCacheEnabled(final boolean cacheEnabled) {
        // diff check
        if (mCacheEnabled == cacheEnabled) {
            return;
        }

        mCacheEnabled = cacheEnabled;

        invalidate(CACHE);
    }

    @Deprecated
    public void clearCache() {
        if (mCacheFrameBuffer != null) {
            mCacheFrameBuffer.getTexture().unload();
            mCacheFrameBuffer.unload();
            mCacheFrameBuffer = null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (mMeshBuffer != null) {
            mMeshBuffer.dispose();
            mMeshBuffer = null;
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

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String font = xmlParser.getAttributeValue(null, ATT_FONT);
        if (font != null) {
            BitmapFont bitmapFont = manager.getTextureManager().getBitmapFont(font);
            if (bitmapFont != null) {
                setBitmapFont(bitmapFont);
                setText(manager.evalString(xmlParser.getAttributeValue(null, ATT_TEXT)));
                final String sizeAttr = xmlParser.getAttributeValue(null, ATT_SIZE);
                if (sizeAttr != null) {
                    try {
                        final int size = Integer.parseInt(sizeAttr);
                        setScale(size / bitmapFont.getTextOptions().inTextPaint.getTextSize());
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid value of 'size': " + sizeAttr);
                    }
                }

                final String letterSpacing = xmlParser.getAttributeValue(null, ATT_LETTER_SPACING);
                if (letterSpacing != null) {
                    mFontMetrics.letterSpacing = Float.valueOf(letterSpacing);
                }
            } else {
                Log.e(TAG, "Font not found: " + font, new Exception());
            }
        }

        final String textAlign = xmlParser.getAttributeValue(null, ATT_TEXT_ALIGN);
        if (textAlign != null) {
            setTextAlignment(UIConfig.getAlignment(textAlign));
        }

    }
}
