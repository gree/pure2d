package com.funzio.pure2D.lwf;

import java.util.HashMap;

import android.util.Log;

public class LWF {
    static {
        System.loadLibrary("pure2d");
    }

    public class Handler {
        void call() {}
    }

    private static final String TAG = LWF.class.getSimpleName();

    private LWFData mData;
    private int mId;
    private long mPtr;
    private HashMap<Integer, Handler> mHandlers;
    private int mHandlerId;

    private native int create(int lwfDataId);
    private native long getPointer(int lwfId);
    private native void destroy(int lwfId);
    private native void exec(long ptr, float tick);
    private native void render(long ptr);
    private native void attachLWF(long ptr, int childId, String target, String attachName);
    private native int addEventHandler(long ptr, int handlerId);
    private native void gotoAndPlay(long ptr, String target, String label);
    private native void gotoFrameAndPlay(long ptr, String target, int frame);
    private native void moveTo(long ptr, String target, float x, float y);

    public LWF() {
        mId = create(Integer.MAX_VALUE);
        if (mId < 0)
            return;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
    }

    public LWF(LWFData data) {
        mId = create(data.getId());
        if (mId < 0)
            return;
        mData = data;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
    }

    public void attachLWF(LWF lwf, String target, String attachName) {
        if (mId < 0)
            return;
        attachLWF(mPtr, lwf.mId, target, attachName);
    }

    public void addEventHandler(Handler handler) {
        if (mId < 0)
            return;
        int handlerId = ++mHandlerId;
        mHandlers.put(Integer.valueOf(handlerId), handler);
        addEventHandler(mPtr, handlerId);
    }

    public void callHandler(int handlerId) {
        Handler handler = mHandlers.get(Integer.valueOf(handlerId));
        if (handler == null)
            return;
        handler.call();
    }

    public void gotoAndPlay(String target, String label) {
        if (mId < 0)
            return;
        gotoAndPlay(mPtr, target, label);
    }

    public void gotoAndPlay(String target, int frame) {
        if (mId < 0)
            return;
        gotoFrameAndPlay(mPtr, target, frame);
    }

    public void moveTo(String target, float x, float y) {
        if (mId < 0)
            return;
        moveTo(mPtr, target, x, y);
    }

    public void update(final int deltaTime) {
        if (mId < 0)
            return;
        exec(mPtr, (float)deltaTime / 1000.f);
    }

    protected void draw() {
        if (mId < 0)
            return;
        render(mPtr);
    }

    public void dispose() {
        destroy(mId);
        mId = -1;
        mPtr = 0;
        mData = null;
    }
}
