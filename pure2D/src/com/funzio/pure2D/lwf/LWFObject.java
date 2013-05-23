package com.funzio.pure2D.lwf;

import android.util.Log;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.gl.gl10.GLState;

public class LWFObject extends BaseDisplayObject {
    private static final String TAG = LWFObject.class.getSimpleName();

    private LWF mLWF;
    private int mAttachId;

    public LWFObject() {
        mLWF = new LWF();
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

    public void attachLWF(LWF lwf) {
        String attachName = String.format("childLWF%d", mAttachId++);
        attachLWF(lwf, "_root", attachName);
    }

    public void attachLWF(LWF lwf, String target, String attachName) {
        mLWF.attachLWF(lwf, target, attachName);
    }

    public void addEventHandler(LWF.Handler handler) {
        mLWF.addEventHandler(handler);
    }

    public void gotoAndPlay(String target, String label) {
        mLWF.gotoAndPlay(target, label);
    }

    public void gotoAndPlay(String target, int frame) {
        mLWF.gotoAndPlay(target, frame);
    }

    public void dispose() {
        mLWF.dispose();
        mLWF = null;
    }
}
