/**
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
 */
/**
 *
 */
package com.funzio.pure2D.ui;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite9;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author long
 */
public class Button extends DisplayGroup implements UIObject {
    protected static final String ATT_SOURCE = "source";
    protected static final String ATT_PATCHES = "patches";
    protected static final String ATT_ASYNC = "async";
    protected static final String ATT_FLIP_X = "flipX";
    protected static final String ATT_FLIP_Y = "flipY";

    public static final GLColor DIMMED_COLOR = new GLColor(0.75f, 0.75f, 0.75f, 1f);

    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_DISABLED = 2;

    protected boolean mEnabled = true;
    protected int mState = STATE_UP;
    protected Texture[] mTextures;

    protected boolean mTouchable = true; // true by default
    protected boolean mFocus = false;
    private int mTouchPointerID = -1;

    protected Sprite9 mButtonSprite;
    protected DisplayObject mContentGroup;

    protected TouchListener mTouchListener;

    private Texture mCurrentTexture;
    private boolean mTextureLoaded;

    public Button() {
        super();

        // wrap by default
        mWrapContentWidth = mWrapContentHeight = true;

        createChildren();
    }

    @Override
    public boolean update(final int deltaTime) {
        if (!mTextureLoaded && mCurrentTexture != null && mCurrentTexture.isLoaded()) {
            // flag
            mTextureLoaded = true;

            final PointF size = mCurrentTexture.getSize();
            final float w = Math.max(mSize.x, size.x);
            final float h = Math.max(mSize.y, size.y);
            if (w != mSize.x || h != mSize.y) {
                setSize(w, h);
            }
        }

        return super.update(deltaTime);
    }

    protected void createChildren() {
        mButtonSprite = new Sprite9();
        mButtonSprite.setPivotAtCenter();
        mButtonSprite.setBlendFunc(BlendModes.PREMULTIPLIED_ALPHA_FUNC);
        mButtonSprite.setAutoUpdateBounds(true);
        addChild(mButtonSprite);
    }

    public void setTextures(final Texture... textures) {
        mTextures = textures;

        // force update
        final int currentState = mState;
        mState = -1;
        setState(currentState);
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // also set the button size
        mButtonSprite.setSize(w, h);
    }

    public void setContent(final DisplayObject contentGroup) {
        final boolean success = queueEvent(new Runnable() {

            @Override
            public void run() {
                setContentForced(contentGroup);
            }
        });

        // if scene is not ready, set content directly
        if (!success) {
            setContentForced(contentGroup);
        }
    }

    public void set9Patches(final float left, final float right, final float top, final float bottom) {
        mButtonSprite.set9Patches(left, right, top, bottom);
    }

    protected void setContentForced(final DisplayObject contentGroup) {
        if (mContentGroup != null) {
            mContentGroup.removeFromParent();
        }

        if (contentGroup != null) {
            addChild(contentGroup);
        }

        mContentGroup = contentGroup;
    }

    protected void setState(final int state) {
        // diff check
        if (mState == state) {
            return;
        }

        mState = state;
        final Texture texture = getStateTexture(state);
        if (texture != mCurrentTexture) {
            mCurrentTexture = texture;
            mButtonSprite.setTexture(texture);

            if (mCurrentTexture != null) {
                final PointF size = mCurrentTexture.getSize();
                final float w = Math.max(mSize.x, size.x);
                final float h = Math.max(mSize.y, size.y);
                if (w != mSize.x || h != mSize.y) {
                    setSize(w, h);
                }

                mTextureLoaded = true;
            } else {
                mTextureLoaded = false; // flag for update later
            }
        }

        // dim it if there is missing frame
        setColor((texture == null || state >= mTextures.length) ? DIMMED_COLOR : null);
    }

    protected Texture getStateTexture(final int state) {
        return (mTextures == null || mTextures.length == 0) ? null : mTextures[Math.min(state, mTextures.length - 1)];
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;

        setState(enabled ? STATE_UP : STATE_DISABLED);
    }

    public Sprite9 getButtonSprite() {
        return mButtonSprite;
    }

    /**
     * @param x
     * @param y
     * @return true if point(x,y) is within the bounds
     */
    public boolean hitTest(final float x, final float y) {
        return mBounds.contains(x, y);
    }

    @Override
    public void setTouchable(final boolean touchable) {
        mTouchable = touchable;
    }

    @Override
    public boolean isTouchable() {
        return mTouchable && mAlive;
    }

    public TouchListener getTouchListener() {
        return mTouchListener;
    }

    public void setTouchListener(final TouchListener listener) {
        mTouchListener = listener;
    }

    /**
     * Note: This is called from UI-Thread
     *
     * @hide
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final Scene scene = mScene;
        if (!mEnabled || scene == null) {
            return false;
        }

        final int action = event.getActionMasked();
        final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final PointF touchedPoint = scene.getTouchedPoint(pointerIndex);

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            if (!mFocus && hitTest(touchedPoint.x, touchedPoint.y)) {
                // keep pointer id
                mTouchPointerID = event.getPointerId(pointerIndex);
                // flag focus
                mFocus = true;
                setState(STATE_DOWN);

                // event
                if (mTouchListener != null) {
                    mTouchListener.onTouchDown(this);
                }

                // take control
                return true;
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            if (mFocus && event.getPointerId(pointerIndex) == mTouchPointerID) {
                mTouchPointerID = -1;
                // unflag focus
                mFocus = false;

                // hit test
                final boolean hit = hitTest(touchedPoint.x, touchedPoint.y);
                // local callback
                onTouchUp(hit);

                setState(STATE_UP);

                // event
                if (mTouchListener != null) {
                    mTouchListener.onTouchUp(this, hit);
                }
                if (hit) {
                    // take control
                    return true;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            final int touchPointerIndex = event.findPointerIndex(mTouchPointerID);
            if (mFocus && touchPointerIndex >= 0) {
                final PointF movePoint = scene.getTouchedPoint(touchPointerIndex);
                if (hitTest(movePoint.x, movePoint.y)) {
                    setState(STATE_DOWN);
                } else {
                    setState(STATE_UP);
                }
            }
        }

        return false;
    }

    protected void onTouchUp(final boolean hit) {
        // TODO
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String source = xmlParser.getAttributeValue(null, ATT_SOURCE);
        if (source != null) {
            final String[] sources = manager.evalString(source).split(",");
            if (sources.length > 0) {
                final Texture[] textures = new Texture[sources.length];
                int i = 0;
                final String async = xmlParser.getAttributeValue(null, ATT_ASYNC);
                for (String s : sources) {
                    textures[i++] = manager.getTextureManager().getUriTexture(s, null, async != null ? Boolean.valueOf(async) : true);
                }
                setTextures(textures);
            }
        }

        if (xmlParser.getAttributeValue(null, ATT_PATCHES) != null) {
            final String[] patches = manager.evalString(xmlParser.getAttributeValue(null, ATT_PATCHES)).split(",");
            final float configScale = manager.getConfig().screen_scale;
            final float left = patches.length >= 1 ? Float.valueOf(patches[0].trim()) * configScale : 0;
            final float right = patches.length >= 2 ? Float.valueOf(patches[1].trim()) * configScale : 0;
            final float top = patches.length >= 3 ? Float.valueOf(patches[2].trim()) * configScale : 0;
            final float bottom = patches.length >= 4 ? Float.valueOf(patches[3].trim()) * configScale : 0;
            set9Patches(left, right, top, bottom);
        }

        final String flipXSt = xmlParser.getAttributeValue(null, ATT_FLIP_X);
        final boolean flipX = (flipXSt == null) ? false : Boolean.valueOf(flipXSt);
        final String flipYSt = xmlParser.getAttributeValue(null, ATT_FLIP_Y);
        final boolean flipY = (flipYSt == null) ? false : Boolean.valueOf(flipYSt);
        if (flipX || flipY) {
            mButtonSprite.setScale(flipX ? -1 : 1, flipY ? -1 : 1);
        }
    }

}
