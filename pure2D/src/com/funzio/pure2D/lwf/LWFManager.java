package com.funzio.pure2D.lwf;

import java.util.HashMap;
import java.util.HashSet;

import android.content.res.AssetManager;
import android.util.Log;

import com.funzio.pure2D.Scene;

public class LWFManager {
    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFManager.class.getSimpleName();

    private final Scene mScene;
    private HashMap<String, LWFData> mDataCache;
    private HashSet<LWF> mLWFs;
    private boolean mRemoving = false;

    public LWFManager(final Scene scene) {
        mScene = scene;
        mDataCache = new HashMap<String, LWFData>();
        mLWFs = new HashSet<LWF>();
    }

    public LWF createLWF() {
        LWF lwf = new LWF(this);
        mLWFs.add(lwf);
        return lwf;
    }

    public LWF createLWF(AssetManager assetManager, String path) {
        LWFData data = mDataCache.get(path);
        if (data == null) {
            data = new LWFData(mScene, assetManager, path);
            mDataCache.put(path, data);
        }
        LWF lwf = new LWF(this, data);
        mLWFs.add(lwf);
        return lwf;
    }

    public void removeLWF(LWF lwf) {
        if (!mRemoving)
            mLWFs.remove(lwf);
    }

    public void removeLWFData(LWFData data) {
        mDataCache.remove(data.getPath());
    }

    public void removeAllInstances() {
        mRemoving = true;
        for (LWF lwf : mLWFs)
            lwf.dispose();
        mRemoving = false;
        mDataCache.clear();
        mLWFs.clear();
    }

    public final Scene getScene() {
        return mScene;
    }
}
