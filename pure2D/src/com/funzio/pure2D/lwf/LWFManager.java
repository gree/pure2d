package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import android.util.Log;

public class LWFManager {
    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFManager.class.getSimpleName();

    private HashSet<LWFData> mDatas;
    private HashSet<LWF> mLWFs;
    private boolean mRemoving = false;

    public LWFManager() {
        mDatas = new HashSet<LWFData>();
        mLWFs = new HashSet<LWF>();
    }

    public LWFData createLWFData(InputStream stream) throws Exception {
        LWFData data = new LWFData(this, stream);
        mDatas.add(data);
        return data;
    }

    public void addLWFData(LWFData data) {
        mDatas.add(data);
    }

    public LWF createLWF() {
        LWF lwf = new LWF(this);
        mLWFs.add(lwf);
        return lwf;
    }

    public LWF createLWF(LWFData data) {
        LWF lwf = new LWF(this, data);
        mLWFs.add(lwf);
        return lwf;
    }

    public void removeLWFData(LWFData data) {
        mDatas.remove(data);
    }

    public void removeLWF(LWF lwf) {
        if (!mRemoving)
            mLWFs.remove(lwf);
    }

    public void dispose() {
        mRemoving = true;
        for (LWF lwf : mLWFs)
            lwf.dispose();
        mRemoving = false;
        mDatas.clear();
        mLWFs.clear();
    }
}
