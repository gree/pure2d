/**
 * 
 */
package com.funzio.pure2D.ui;

import android.view.MotionEvent;

import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class Button extends Sprite implements UIObject {
    public static final int STATE_UP = 0;
    public static final int STATE_DOWN = 1;
    public static final int STATE_DISABLED = 2;

    protected boolean mEnabled = true;
    protected int mState = STATE_UP;
    protected AtlasFrameSet mFrameSet;
    protected int mNumFrames = 0;

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
        setAtlasFrame(mNumFrames == 0 ? null : mFrameSet.getFrame(Math.min(state, mNumFrames - 1)));
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;

        setState(enabled ? STATE_UP : STATE_DISABLED);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

}
