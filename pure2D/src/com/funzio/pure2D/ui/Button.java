/**
 * 
 */
package com.funzio.pure2D.ui;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite9;

/**
 * @author long
 */
public class Button extends DisplayGroup implements UIObject {
    public static final GLColor DIMMED_COLOR = new GLColor(0.75f, 0.75f, 0.75f, 1f);

    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_DISABLED = 2;

    protected boolean mEnabled = true;
    protected int mState = STATE_UP;
    protected Texture[] mTextures;

    protected boolean mTouchable = true; // true by default
    protected boolean mFocus = false;

    protected Sprite9 mButtonSprite;
    protected DisplayObject mContentGroup;

    protected Listener mListener;

    public Button() {
        super();

        createChildren();
    }

    protected void createChildren() {
        mButtonSprite = new Sprite9();
        mButtonSprite.setAutoUpdateBounds(true);
        addChild(mButtonSprite);
    }

    public void setTextures(final Texture... textures) {
        mTextures = textures;

        // update
        setState(mState);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // also set the button size
        mButtonSprite.setSize(w, h);
    }

    public Sprite9 getButtonSprite() {
        return mButtonSprite;
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
        mState = state;
        // go to the state's frame
        final Texture texture = mTextures == null || mTextures.length == 0 ? null : mTextures[Math.min(state, mTextures.length - 1)];
        mButtonSprite.setTexture(texture);

        // dim it if there is missing frame
        setColor((texture == null || state >= mTextures.length) ? DIMMED_COLOR : null);

        // match the size
        setSize(mButtonSprite.getSize());
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
        return mTouchable;
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    /**
     * @hide
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!mEnabled) {
            return false;
        }

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final PointF touchedPoint = getScene().getTouchedPoint();

        if (action == MotionEvent.ACTION_DOWN) {
            if (hitTest(touchedPoint.x, touchedPoint.y)) {
                // flag focus
                mFocus = true;
                setState(STATE_DOWN);

                // event
                if (mListener != null) {
                    mListener.onTouchDown(this);
                }

                // take control
                return true;
            } else if (mFocus) {
                // if for some reason, there's another ACTION_DOWN while we're on focus?
                // unflag focus
                mFocus = false;
                setState(STATE_UP);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mFocus) {
                // unflag focus
                mFocus = false;
                setState(STATE_UP);

                if (hitTest(touchedPoint.x, touchedPoint.y)) {
                    // event
                    if (mListener != null) {
                        mListener.onTouchUp(this);
                    }

                    // take control
                    return true;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mFocus) {
                if (hitTest(touchedPoint.x, touchedPoint.y)) {
                    setState(STATE_DOWN);
                } else {
                    setState(STATE_UP);
                }
            }
        }

        return false;
    }

    public static interface Listener {
        public void onTouchDown(Button button);

        public void onTouchUp(Button button);
    }
}
