package com.funzio.pure2D.lwf;

import android.util.Log;
import android.graphics.RectF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Playable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;

public class LWFObject extends BaseDisplayObject implements Playable {
    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFObject.class.getSimpleName();

    private LWF mLWF;
    private int mAttachId;
    private boolean mPlaying;

    public LWFObject() {
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mLWF == null)
            return false;
        mLWF.update(deltaTime);
        invalidate(InvalidateFlags.VISUAL);
        return super.update(deltaTime);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if (mLWF == null)
            return false;
        mLWF.draw();
        return true;
    }

    public void play() {
        if (mLWF == null)
            return;
        mLWF.setPlaying(true);
    }

    public void playAt(final int frame) {
        if (mLWF == null)
            return;
        mLWF.setPlaying(true);
    }

    public void stop() {
        if (mLWF == null)
            return;
        mLWF.setPlaying(false);
    }

    public void stopAt(final int frame) {
        if (mLWF == null)
            return;
        mLWF.setPlaying(false);
    }

    public int getLoop() {
        return LOOP_NONE;
    }

    public void setLoop(final int type) {
    }

    public int getCurrentFrame() {
        return 1;
    }

    public int getNumFrames() {
        return 1;
    }

    public boolean isPlaying() {
        if (mLWF == null)
            return false;
        return mLWF.isPlaying();
    }

    public RectF getFrameRect(final int frame) {
        RectF r = new RectF();
        return r;
    }

    public LWF attachLWF(LWFData data) {
        String attachName = String.format("childLWF%d", mAttachId++);
        return attachLWF(data, "_root", attachName);
    }

    public LWF attachLWF(LWFData data, String target, String attachName) {
        if (mLWF == null)  {
            mLWF = mScene.getLWFManager().createLWF();
            mPlaying = true;
        }

        LWF lwf = mScene.getLWFManager().createLWF(data);
        mLWF.attachLWF(lwf, target, attachName);
        return lwf;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mLWF != null) {
            if (LOG_ENABLED) {
                Log.e(TAG, "dispose()");
            }
            mLWF.dispose();
            mLWF = null;
        }
    }
}
