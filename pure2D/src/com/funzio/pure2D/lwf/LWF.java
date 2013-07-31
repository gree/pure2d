package com.funzio.pure2D.lwf;

import java.util.HashMap;
import java.util.HashSet;

import android.util.Log;
import android.graphics.Point;
import android.graphics.PointF;

public class LWF {
    static {
        loadLibrary();
    }

    private static boolean mLoaded = false;

    public static boolean loadLibrary() {
        if (!mLoaded) {
            try {
                System.loadLibrary("lwf-pure2d");
                mLoaded = true;
            } catch (UnsatisfiedLinkError e) {
            }
        }
        return mLoaded;
    }

    public interface Handler {
        void call();
    }

    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWF.class.getSimpleName();

    private LWFManager mManager;
    private LWFData mData;
    private int mId;
    private long mPtr;
    private HashMap<Integer, Handler> mHandlers;
    private HashSet<LWF> mLWFs;
    private int mHandlerId;
    private boolean mPlaying = true;

    private native int create(int lwfDataId);
    private native long getPointer(int lwfId);
    private native void destroy(int lwfId);
    private native void init(long ptr);
    private native void exec(long ptr, float tick);
    private native void render(long ptr);
    private native void attachLWF(long ptr, int childId, String target, String attachName);
    private native int addEventHandler(long ptr, String event, int handlerId);
    private native void gotoAndPlay(long ptr, String target, String label);
    private native void gotoFrameAndPlay(long ptr, String target, int frame);
    private native void play(long ptr, String target);
    private native void stop(long ptr, String target);
    private native void move(long ptr, String target, float x, float y);
    private native void moveTo(long ptr, String target, float x, float y);
    private native void scale(long ptr, String target, float x, float y);
    private native void scaleTo(long ptr, String target, float x, float y);
    private native PointF localToGlobal(long ptr, String target, float x, float y);
    private native PointF globalToLocal(long ptr, String target, float x, float y);
    private native void setPlaying(long ptr, boolean playing);
    private native void fitForHeight(long ptr, float w, float h);
    private native void fitForWidth(long ptr, float w, float h);
    private native void scaleForHeight(long ptr, float w, float h);
    private native void scaleForWidth(long ptr, float w, float h);
    private native int getWidth(long ptr);
    private native int getHeight(long ptr);
    public static native void disposeAll();

    public LWF(LWFManager manager) {
        mId = create(Integer.MAX_VALUE);
        if (mId < 0)
            return;
        mManager = manager;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
        mLWFs = new HashSet<LWF>();
    }

    public LWF(LWFManager manager, LWFData data) {
        mId = create(data.getId());
        if (mId < 0)
            return;
        mManager = manager;
        mData = data;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
        mLWFs = new HashSet<LWF>();
    }

    public void init() {
        if (mId < 0)
            return;
        init(mPtr);
    }

    public void attachLWF(LWF lwf, String target, String attachName) {
        if (mId < 0)
            return;
        mLWFs.add(lwf);
        attachLWF(mPtr, lwf.mId, target, attachName);
    }

    public void addEventHandler(String event, Handler handler) {
        if (mId < 0)
            return;
        int handlerId = ++mHandlerId;
        mHandlers.put(Integer.valueOf(handlerId), handler);
        addEventHandler(mPtr, event, handlerId);
    }

    public void callHandler(int handlerId) {
        if (mId < 0)
            return;
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

    public void playAt(String target, String label) {
        gotoAndPlay(target, label);
    }

    public void playAt(String target, int frame) {
        gotoAndPlay(target, frame);
    }

    public void stopAt(String target, String label) {
        gotoAndPlay(target, label);
        stop(target);
    }

    public void stopAt(String target, int frame) {
        gotoAndPlay(target, frame);
        stop(target);
    }

    public void play(String target) {
        if (mId < 0)
            return;
        play(mPtr, target);
    }

    public void stop(String target) {
        if (mId < 0)
            return;
        stop(mPtr, target);
    }

    public void move(String target, float x, float y) {
        if (mId < 0)
            return;
        move(mPtr, target, x, y);
    }

    public void moveTo(String target, float x, float y) {
        if (mId < 0)
            return;
        moveTo(mPtr, target, x, y);
    }

    public void scale(String target, float x, float y) {
        if (mId < 0)
            return;
        scale(mPtr, target, x, y);
    }

    public void scaleTo(String target, float x, float y) {
        if (mId < 0)
            return;
        scaleTo(mPtr, target, x, y);
    }

    public PointF localToGlobal(String target, float x, float y) {
        if (mId < 0)
            return null;
        return localToGlobal(mPtr, target, x, y);
    }

    public PointF globalToLocal(String target, float x, float y) {
        if (mId < 0)
            return null;
        return globalToLocal(mPtr, target, x, y);
    }

    public void setPlaying(boolean playing) {
        if (mId < 0)
            return;
        mPlaying = playing;
        setPlaying(mPtr, playing);
    }

    public void fitForHeight(float width, float height) {
        if (mId < 0)
            return;
        fitForHeight(mPtr, width, height);
    }

    public void fitForWidth(float width, float height) {
        if (mId < 0)
            return;
        fitForWidth(mPtr, width, height);
    }

    public void scaleForHeight(float width, float height) {
        if (mId < 0)
            return;
        scaleForHeight(mPtr, width, height);
    }

    public void scaleForWidth(float width, float height) {
        if (mId < 0)
            return;
        scaleForWidth(mPtr, width, height);
    }

    public Point getSize() {
        if (mId < 0)
            return new Point();
        return new Point(getWidth(mPtr), getHeight(mPtr));
    }

    public boolean isPlaying() {
        return mPlaying;
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
        if (mId != -1) {
            if (LOG_ENABLED) {
                Log.e(TAG, "dispose()");
            }
            mHandlers.clear();
            for (LWF lwf : mLWFs)
                lwf.dispose();
            destroy(mId);
            mId = -1;
            mPtr = 0;
            mLWFs.clear();
            mData = null;
            mManager.removeLWF(this);
            mManager = null;
        }
    }
}
