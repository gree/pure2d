package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.util.HashSet;

public class LWFManager {
    public static boolean LOG_ENABLED = true;
    // private static final String TAG = LWFManager.class.getSimpleName();

    private HashSet<LWFData> mDatas;
    private HashSet<LWF> mLWFs;
    private boolean mRemoving = false;

    public LWFManager() {
        mDatas = new HashSet<LWFData>();
        mLWFs = new HashSet<LWF>();
    }

    public LWFData createLWFData(final InputStream stream) throws Exception {
        LWFData data = new LWFData(this, stream);
        synchronized (mDatas) {
            mDatas.add(data);
        }
        return data;
    }

    public void addLWFData(final LWFData data) {
        data.setLWFManager(this);
        synchronized (mDatas) {
            mDatas.add(data);
        }
    }

    public LWF createLWF() {
        LWF lwf = new LWF(this);
        synchronized (mLWFs) {
            mLWFs.add(lwf);
        }
        return lwf;
    }

    public LWF createLWF(final LWFData data) {
        LWF lwf = new LWF(this, data);
        synchronized (mLWFs) {
            mLWFs.add(lwf);
        }
        return lwf;
    }

    public void removeLWFData(final LWFData data) {
        if (!mRemoving) {
            synchronized (mDatas) {
                mDatas.remove(data);
            }
        }
    }

    public void removeLWF(final LWF lwf) {
        if (!mRemoving) {
            synchronized (mLWFs) {
                mLWFs.remove(lwf);
            }
        }
    }

    public void dispose() {
        mRemoving = true;
        synchronized (mLWFs) {
            for (LWF lwf : mLWFs) {
                lwf.dispose();
            }
        }
        synchronized (mDatas) {
            for (LWFData data : mDatas) {
                data.dispose();
            }
        }
        mRemoving = false;
        synchronized (mLWFs) {
            mLWFs.clear();
        }
        synchronized (mDatas) {
            mDatas.clear();
        }
    }
}
