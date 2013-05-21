package com.funzio.pure2D.animation.lwf;

public class LWFData {
    private int mId;

    private native int create(byte[] data);
    private native void destroy(int lwfDataId);

    public LWFData(byte[] data) {
        mId = create(data);
    }

    public int getId() {
        return mId;
    }

    public void dispose() {
        destroy(mId);
    }
}
