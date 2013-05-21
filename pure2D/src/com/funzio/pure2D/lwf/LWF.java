package com.funzio.pure2D.animation.lwf;

import com.funzio.pure2D.animation.lwf.LWFData;

public class LWF {
    static {
        System.loadLibrary("pure2d");
    }

    private int mId;
    private long mPtr;

    private native int create(int lwfDataId);
    private native long getPointer(int lwfId);
    private native void destroy(int lwfId);
    private native void exec(long ptr);
    private native void execWithTick(long ptr, float tick);

    public LWF(LWFData data) {
        mId = create(data.getId());
        if (mId < 0)
            return;
        mPtr = getPointer(mId);
    }

    public void update() {
        exec(mPtr);
    }

    public void updateWithTick(float tick) {
        execWithTick(mPtr, tick);
    }

    public void dispose() {
        destroy(mId);
    }
}
