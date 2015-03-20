/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package com.funzio.pure2D.lwf;

import java.util.HashMap;
import java.util.HashSet;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

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

    private native void setColor(long ptr, String target, float red, float green, float blue, float alpha);

    private native PointF localToGlobal(long ptr, String target, float x, float y);

    private native PointF globalToLocal(long ptr, String target, float x, float y);

    private native RectF getButtonInstanceRect(long ptr, String target);

    private native void setPlaying(long ptr, boolean playing);

    private native boolean isPlaying(long ptr, String target);

    private native void fitForHeight(long ptr, float w, float h);

    private native void fitForWidth(long ptr, float w, float h);

    private native void scaleForHeight(long ptr, float w, float h);

    private native void scaleForWidth(long ptr, float w, float h);

    private native int getWidth(long ptr);

    private native int getHeight(long ptr);

    private native String[] getEvents(long ptr);

    public static native void disposeAll();

    public LWF(final LWFManager manager) {
        mId = create(Integer.MAX_VALUE);
        if (mId < 0) {
            return;
        }
        mManager = manager;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
        mLWFs = new HashSet<LWF>();
    }

    public LWF(final LWFManager manager, final LWFData data) {
        mId = create(data.getId());
        if (mId < 0) {
            return;
        }
        mManager = manager;
        mData = data;
        mPtr = getPointer(mId);
        mHandlers = new HashMap<Integer, Handler>();
        mLWFs = new HashSet<LWF>();
    }

    public void init() {
        if (mId < 0) {
            return;
        }
        init(mPtr);
    }

    public void attachLWF(final LWF lwf, final String target, final String attachName) {
        if (mId < 0) {
            return;
        }
        mLWFs.add(lwf);
        attachLWF(mPtr, lwf.mId, target, attachName);
    }

    public void addEventHandler(final String event, final Handler handler) {
        if (mId < 0) {
            return;
        }
        int handlerId = ++mHandlerId;
        mHandlers.put(Integer.valueOf(handlerId), handler);
        addEventHandler(mPtr, event, handlerId);
    }

    public void callHandler(final int handlerId) {
        if (mId < 0) {
            return;
        }
        Handler handler = mHandlers.get(Integer.valueOf(handlerId));
        if (handler == null) {
            return;
        }
        handler.call();
    }

    public void gotoAndPlay(final String target, final String label) {
        if (mId < 0) {
            return;
        }
        gotoAndPlay(mPtr, target, label);
    }

    public void gotoAndPlay(final String target, final int frame) {
        if (mId < 0) {
            return;
        }
        gotoFrameAndPlay(mPtr, target, frame);
    }

    public void playAt(final String target, final String label) {
        gotoAndPlay(target, label);
    }

    public void playAt(final String target, final int frame) {
        gotoAndPlay(target, frame);
    }

    public void stopAt(final String target, final String label) {
        gotoAndPlay(target, label);
        stop(target);
    }

    public void stopAt(final String target, final int frame) {
        gotoAndPlay(target, frame);
        stop(target);
    }

    public void play(final String target) {
        if (mId < 0) {
            return;
        }
        play(mPtr, target);
    }

    public void stop(final String target) {
        if (mId < 0) {
            return;
        }
        stop(mPtr, target);
    }

    public void move(final String target, final float x, final float y) {
        if (mId < 0) {
            return;
        }
        move(mPtr, target, x, y);
    }

    public void moveTo(final String target, final float x, final float y) {
        if (mId < 0) {
            return;
        }
        moveTo(mPtr, target, x, y);
    }

    public void scale(final String target, final float x, final float y) {
        if (mId < 0) {
            return;
        }
        scale(mPtr, target, x, y);
    }

    public void scaleTo(final String target, final float x, final float y) {
        if (mId < 0) {
            return;
        }
        scaleTo(mPtr, target, x, y);
    }

    public void setColor(final String target, final float red, final float green, final float blue, final float alpha) {
        if (mId < 0) {
            return;
        }
        setColor(mPtr, target, red, green, blue, alpha);
    }

    public PointF localToGlobal(final String target, final float x, final float y) {
        if (mId < 0) {
            return null;
        }
        return localToGlobal(mPtr, target, x, y);
    }

    public PointF globalToLocal(final String target, final float x, final float y) {
        if (mId < 0) {
            return null;
        }
        return globalToLocal(mPtr, target, x, y);
    }

    public void setPlaying(final boolean playing) {
        if (mId < 0) {
            return;
        }
        mPlaying = playing;
        setPlaying(mPtr, playing);
    }

    public boolean isPlaying(final String target) {
        if (mId < 0) {
            return false;
        }
        return isPlaying(mPtr, target);
    }

    public void fitForHeight(final float width, final float height) {
        if (mId < 0) {
            return;
        }
        fitForHeight(mPtr, width, height);
    }

    public void fitForWidth(final float width, final float height) {
        if (mId < 0) {
            return;
        }
        fitForWidth(mPtr, width, height);
    }

    public void scaleForHeight(final float width, final float height) {
        if (mId < 0) {
            return;
        }
        scaleForHeight(mPtr, width, height);
    }

    public void scaleForWidth(final float width, final float height) {
        if (mId < 0) {
            return;
        }
        scaleForWidth(mPtr, width, height);
    }

    public Point getSize() {
        if (mId < 0) {
            return new Point();
        }
        return new Point(getWidth(mPtr), getHeight(mPtr));
    }

    public RectF getButtonInstanceRect(final String target) {
        if (mId < 0) {
            return null;
        }
        return getButtonInstanceRect(mPtr, target);
    }

    public String[] getEvents() {
        if (mId < 0) {
            return null;
        }
        return getEvents(mPtr);
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void update(final int deltaTime) {
        if (mId < 0) {
            return;
        }
        exec(mPtr, deltaTime / 1000.f);
    }

    protected void draw() {
        if (LOG_ENABLED && !Thread.currentThread().getName().startsWith("GLThread")) {
            Log.e(TAG, "LWF.draw() was called from " + Thread.currentThread().getName());
        }
        if (mId < 0) {
            return;
        }
        render(mPtr);
    }

    public void dispose() {
        if (LOG_ENABLED && !Thread.currentThread().getName().startsWith("GLThread")) {
            Log.e(TAG, "LWF.dispose() was called from " + Thread.currentThread().getName());
        }
        if (mId != -1) {
            if (LOG_ENABLED) {
                Log.i(TAG, "dispose()");
            }
            mHandlers.clear();
            for (LWF lwf : mLWFs) {
                lwf.dispose();
            }
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
