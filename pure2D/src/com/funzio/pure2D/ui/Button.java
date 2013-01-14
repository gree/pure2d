/**
 * 
 */
package com.funzio.pure2D.ui;

import android.graphics.PointF;
import android.view.MotionEvent;

import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class Button extends Sprite implements UIObject {
    public static final GLColor DIMMED_COLOR = new GLColor(0.75f, 0.75f, 0.75f, 1f);

    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_DISABLED = 2;

    protected boolean mEnabled = true;
    protected int mState = STATE_UP;
    protected AtlasFrameSet mFrameSet;
    protected int mNumFrames = 0;

    protected boolean mTouchable = true; // true by default
    protected boolean mFocus = false;

    protected Listener mListener;

    public Button() {
        super();

        // for hit testing
        setAutoUpdateBounds(true);
    }

    @Override
    public void setTexture(final Texture texture) {
        setTextures(texture);
    }

    public void setTextures(final Texture... textures) {
        if (mFrameSet == null) {
            mFrameSet = new AtlasFrameSet("");
        } else {
            mFrameSet.removeAllFrames();
        }

        int i = 0;
        for (Texture texture : textures) {
            AtlasFrame frame = new AtlasFrame(texture, i++, "");
            mFrameSet.addFrame(frame);
        }
        mNumFrames = i;

        // update
        setState(mState);
    }

    protected void setState(final int state) {
        mState = state;
        // go to the state's frame
        AtlasFrame frame = mNumFrames == 0 ? null : mFrameSet.getFrame(Math.min(state, mNumFrames - 1));
        boolean forceDim = false;
        if (frame == null) {
            if (mNumFrames > 0) {
                // use first frame as default
                frame = mFrameSet.getFrame(0);
                forceDim = true;
            }
        }
        setAtlasFrame(frame);
        // dim it if there is missing frame
        setColor((state > mNumFrames - 1 || forceDim) ? DIMMED_COLOR : null);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;

        setState(enabled ? STATE_UP : STATE_DISABLED);
    }

    /**
     * @param x
     * @param y
     * @return true if point(x,y) is within the bounds
     */
    public boolean hitTest(final float x, final float y) {
        return mBounds.contains(x, y);
    }

    public void setTouchable(final boolean touchable) {
        mTouchable = touchable;
    }

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
        final PointF touchedPoint = mScene.getTouchedPoint();

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
