package com.funzio.pure2D.lwf;

import android.util.Log;
import android.graphics.RectF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.Playable;
import com.funzio.pure2D.gl.gl10.GLState;

public class LWFObject extends BaseDisplayObject implements Playable {
    private static final String TAG = LWFObject.class.getSimpleName();

    private LWF mLWF;
    private int mAttachId;
    private boolean mPlaying;

    public LWFObject() {
        mLWF = new LWF();
        mPlaying = true;
    }

    @Override
    public boolean update(final int deltaTime) {
        mLWF.update(deltaTime);
        invalidate(InvalidateFlags.VISUAL);
        return super.update(deltaTime);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        mLWF.draw();
        return true;
    }

    public void play() {
        mLWF.setPlaying(true);
    }

    public void playAt(final int frame) {
        mLWF.setPlaying(true);
    }

    public void stop() {
        mLWF.setPlaying(false);
    }

    public void stopAt(final int frame) {
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
        return mLWF.isPlaying();
    }

    public RectF getFrameRect(final int frame) {
        RectF r = new RectF();
        return r;
    }

    public void attachLWF(LWF lwf) {
        String attachName = String.format("childLWF%d", mAttachId++);
        attachLWF(lwf, "_root", attachName);
    }

    public void attachLWF(LWF lwf, String target, String attachName) {
        mLWF.attachLWF(lwf, target, attachName);
    }

    public void dispose() {
        mLWF.dispose();
        mLWF = null;
    }
}
